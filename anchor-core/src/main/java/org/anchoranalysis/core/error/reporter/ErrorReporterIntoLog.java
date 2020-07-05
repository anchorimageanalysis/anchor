package org.anchoranalysis.core.error.reporter;

import org.anchoranalysis.core.error.friendly.IFriendlyException;
import org.anchoranalysis.core.log.LogReporter;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Records errors, by writing them into a logReporter
 * 
 * Does some formatting (and sometimes adds a stacktrace) depending on context and exception-type
 * 
 * @author Owen Feehan
 *
 */
public class ErrorReporterIntoLog implements ErrorReporter {
	
	private LogReporter logReporter;

	private static final String START_BANNER = "------------ BEGIN ERROR ------------" + System.lineSeparator();
	private static final String END_BANNER =  System.lineSeparator() + "------------ END ERROR ------------";
	
	public ErrorReporterIntoLog(LogReporter logReporter) {
		this.logReporter = logReporter;
	}

	@Override
	public void recordError( Class<?> classOriginating, Throwable exc ) {
		
		// Special behaviour if it's a friendly exception
		if (exc instanceof IFriendlyException) {
			IFriendlyException eCast = (IFriendlyException) exc;
			logWithBanner( eCast.friendlyMessageHierarchy() );
		} else {
			try {
				logWithBanner( exc.toString() + System.lineSeparator() +  ExceptionUtils.getFullStackTrace(exc), classOriginating );
			} catch (Exception e) {
				logReporter.log("An error occurred while writing an error: " + e.toString());
			}
		}
	}

	@Override
	public void recordError(Class<?> classOriginating, String errorMsg) {
		
		try {
			logWithBanner( errorMsg, classOriginating );
		} catch (Exception e) {
			logReporter.log("An error occurred while writing an error: " + e.toString() );
		}
	}
	
	private void logWithBanner( String logMessage ) {
		logReporter.log( START_BANNER + logMessage + END_BANNER );
	}
	
	private void logWithBanner( String logMessage, Class<?> classOriginating ) {
		logReporter.log( START_BANNER + logMessage + classMessage(classOriginating) + END_BANNER );
	}
	
	private static String classMessage( Class<?> c ) {
		return String.format( "%nThe error occurred when executing a method in class %s", c.getName() );
	}
}
