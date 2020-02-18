package org.anchoranalysis.experiment.task.processor;

/*
 * #%L
 * anchor-experiment
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


import java.util.concurrent.Callable;

import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A job derived from a {@link Task} that can be placed on different threads
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-object bean
 */
public class CallableJob<T extends InputFromManager,S> implements Callable<JobExecutionException> {
	
	private Task<T,S> task;
	private ParametersUnbound<T,S> paramsUnbound; 
	private JobState jobState;
	private JobDescription jobDescription;
	private JobStartStopLogger logger;
	
	/**
	 * Constructor
	 * 
	 * @param task
	 * @param paramsUnbound
	 * @param taskState
	 * @param taskDescription
	 * @param monitor
	 * @param logReporterMonitor the logReporter used for the monitor (or NULL if none is applied)
	 */
	public CallableJob(
		Task<T,S> task,
		ParametersUnbound<T,S> paramsUnbound,
		JobState taskState,
		JobDescription taskDescription,
		ConcurrentJobMonitor monitor,
		LogReporter logReporterMonitor,
		int showOngoingJobsLessThan
	) {
		super();
		this.task = task;
		this.paramsUnbound = paramsUnbound;
		this.jobState = taskState;
		this.jobDescription = taskDescription;
		this.logger = new JobStartStopLogger(
			"Job",
			logReporterMonitor,
			monitor,
			false,
			showOngoingJobsLessThan
		);
	}
	
	@Override
	public JobExecutionException call() {
		
		try {
			Task<T,S> taskDup = task.duplicateBean();

			jobState.markAsExecuting();
			
			logger.logStart(jobDescription);
			
			boolean success = taskDup.executeJob( paramsUnbound );
				
			jobState.markAsCompleted(success);
			logger.logEnd(jobDescription, jobState, success);
			return null;
			
		} catch (Throwable e) {
			// If executeTask is called with supressException==TRUE then Exceptions shouldn't occur here as a rule from specific-tasks,
			//   as they should be logged internally to task-log. So if any error is actually thrown here, let's consider it suspciously
			//
			// If executeTask is called with supressException==FALSE then we arrive here fairly, easily, and record the error in the experiment-log just
			//  in case, even though it's probably already in the task log.
			
			ErrorReporterIntoLog errorReporter = new ErrorReporterIntoLog( paramsUnbound.getParametersExperiment().getLogReporterExperiment() );
			errorReporter.recordError(CallableJob.class, e);
				
			jobState.markAsCompleted(false);
			logger.logEnd(jobDescription, jobState, false);
			
			return new JobExecutionException(e);
		} 
	}
}
