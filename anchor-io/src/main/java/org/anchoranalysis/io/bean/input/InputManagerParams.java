package org.anchoranalysis.io.bean.input;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.params.DebugModeParams;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Parameters passed to an InputManager to generate input-objects
 * 
 * @author owen
 *
 */
public class InputManagerParams {

	private InputContextParams inputContext;
	
	private ProgressReporter progressReporter;
	
	private LogErrorReporter logger;

	public InputManagerParams(InputContextParams inputContext, ProgressReporter progressReporter,
			LogErrorReporter logger) {
		super();
		this.inputContext = inputContext;
		this.progressReporter = progressReporter;
		this.logger = logger;
	}
	
	public InputManagerParams withProgressReporter( ProgressReporter progressReporterToAssign ) {
		return new InputManagerParams(inputContext, progressReporterToAssign, logger);
	}

	public InputContextParams getInputContext() {
		return inputContext;
	}

	public ProgressReporter getProgressReporter() {
		return progressReporter;
	}

	public LogErrorReporter getLogger() {
		return logger;
	}

	public boolean isDebugModeActivated() {
		return inputContext.isDebugModeActivated();
	}

	public DebugModeParams getDebugModeParams() {
		return inputContext.getDebugModeParams();
	}
}
