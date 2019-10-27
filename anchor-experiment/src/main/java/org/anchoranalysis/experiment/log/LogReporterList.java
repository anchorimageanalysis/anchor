package org.anchoranalysis.experiment.log;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;

public class LogReporterList implements StatefulLogReporter {

	private List<StatefulLogReporter> list;
	
	public LogReporterList() {
		list = new ArrayList<>();
	}
	
	public LogReporterList( StatefulLogReporter first, StatefulLogReporter second ) {
		assert first!=null;
		assert second!=null;
		list = new ArrayList<>();
		list.add(first);
		list.add(second);
	}
	
	@Override
	public void start() {
		for (StatefulLogReporter logReporter : list) {
			assert logReporter!=null;
			logReporter.start();
		}
		
	}

	@Override
	public void log(String message) {
		for (StatefulLogReporter logReporter : list) {
			assert logReporter!=null;
			logReporter.log(message);
		}
	}

	@Override
	public void close(boolean successful) {
		for (StatefulLogReporter logReporter : list) {
			logReporter.close(successful);
		}
	}

	@Override
	public void logFormatted(String formatString, Object... args) {
		for (LogReporter logReporter : list) {
			logReporter.logFormatted(formatString, args);
		}
		
	}

	public boolean add(StatefulLogReporter arg0) {
		return list.add(arg0);
	}

	public List<StatefulLogReporter> getList() {
		return list;
	}
	
}
