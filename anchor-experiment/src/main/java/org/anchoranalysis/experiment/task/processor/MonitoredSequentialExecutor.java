package org.anchoranalysis.experiment.task.processor;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.TaskStatistics;

/**
 * Runs executes sequence of jobs/tasks/whatever monitoring various pieces of information
 * 	- how many have run?
 *  - how many successful/failed?
 *  - how long did execution take?
 *  - etc.
 *  
 * @author Owen Feehan
 * @param T inputType
 */
public class MonitoredSequentialExecutor<T> {
	
	private Predicate<T> execFunc;
	private Function<T,String> dscrFunc;	
	private Optional<LogReporter> logger;
	private boolean showHashSeperators;
		
	/**
	 * Constructor
	 * 
	 * @param execFunc executes a particular input (String)
	 * @param dscrFunc extracts a string-description from an input
	 * @param logger reports before and after an input on the current status (disabled if empty())
	 * @param showHashSeperators indicates if lines of hashes should be placed before and after each log message (adds emphasis)
	 */
	public MonitoredSequentialExecutor(Predicate<T> execFunc, Function<T,String> dscrFunc, Optional<LogReporter> logger, boolean showHashSeperators ) {
		super();
		this.execFunc = execFunc;
		this.dscrFunc = dscrFunc;
		this.logger = logger;
		this.showHashSeperators = showHashSeperators;
	}

	/**
	 * Executes code for each element of inputs in serial, providing log-reports as to how many are finished, remain etc. 
	 * @param logPrefix prefixed to the output of each log-message
	 * @param inputs a collection of strings that uniquely determine each input
	 * @throws ExperimentExecutionException
	 */
	public TaskStatistics executeEachWithMonitor( String logPrefix, List<T> inputs ) throws ExperimentExecutionException {
		
		ConcurrentJobMonitor monitor = new ConcurrentJobMonitor(inputs.size());
		
		// So we can report on where a job in its total context
		JobStartStopLogger jobLogger = new JobStartStopLogger(
			logPrefix,
			logger,
			monitor,
			showHashSeperators,
			0
		);
		
		List<InputAfterSubmission> submittedInputs = mapToSubmitted(inputs, monitor);

		// We loop through all inputs and execute them, deleting them immediately afterwards
		//   to prevent accumulation in memory
		ListIterator<InputAfterSubmission> itr = submittedInputs.listIterator();
		while( itr.hasNext() ) {
			InputAfterSubmission input = itr.next();
			try {
				input.execInput( jobLogger );
			} finally {
				itr.remove();
			}
		}
		
		return monitor.createStatistics();
	}
	
	
	private List<InputAfterSubmission> mapToSubmitted( Collection<T> allInputs, ConcurrentJobMonitor monitor ) {
		
		List<InputAfterSubmission> out = new ArrayList<>();
		
		int cnt = 1;
		
		for( T input : allInputs ) {
			JobDescription desc = new JobDescription(
				dscrFunc.apply(input),
				cnt++
			);
			JobState state = new JobState();
			
			SubmittedJob submittedJob = new SubmittedJob(desc, state);
			out.add( new InputAfterSubmission(input, submittedJob) );
			monitor.add(submittedJob);
		}
		
		return out;
	}
	
	// Contains both a reference to the original input and a SubmittedJob
	private class InputAfterSubmission {
		
		private T input;
		private SubmittedJob sj;
		
		public InputAfterSubmission(T input, SubmittedJob submittedJob) {
			super();
			this.input = input;
			this.sj = submittedJob;
		}

		public void execInput( JobStartStopLogger jobLogger ) {
			
			sj.getJobState().markAsExecuting();

			jobLogger.logStart( sj.getJobDescription() );
			
			boolean success = execFunc.test( input );
			
			sj.getJobState().markAsCompleted(success);
			
			jobLogger.logEnd(sj.getJobDescription(), sj.getJobState(), success);
		}
	}
}
