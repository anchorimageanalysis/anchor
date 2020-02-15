package org.anchoranalysis.experiment.bean.processor;

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

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.core.text.LanguageUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.CallableJob;
import org.anchoranalysis.experiment.task.processor.ConcurrentJobMonitor;
import org.anchoranalysis.experiment.task.processor.JobDescription;
import org.anchoranalysis.experiment.task.processor.JobState;
import org.anchoranalysis.experiment.task.processor.SubmittedJob;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Executes jobs in parallel
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public class ParallelProcessor<T extends InputFromManager,S> extends JobProcessor<T,S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN
	@BeanField
	private boolean supressExceptions = true;
	
	@BeanField
	private int maxNumProcessors = 64;
	// END BEAN
		
	@Override
	protected TaskStatistics execute(
		BoundOutputManagerRouteErrors rootOutputManager,
		List<T> inputObjects,
		ParametersExperiment paramsExperiment
	) throws ExperimentExecutionException {
		
		S sharedState = getTask().beforeAnyJobIsExecuted( rootOutputManager, paramsExperiment );
			
		int nrOfProcessors = selectNumProcessors(
			paramsExperiment.getLogReporterExperiment(),
			paramsExperiment.isDetailedLogging()
		);
		ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);
		
		int cnt = 1;
		
		ConcurrentJobMonitor monitor = new ConcurrentJobMonitor( inputObjects.size() );
		
		ListIterator<T> itr = inputObjects.listIterator();
		while( itr.hasNext() ) {
			T inputObj = itr.next();
			try {
				submitJob(eservice, inputObj, cnt, sharedState, paramsExperiment, monitor ); 
				cnt++;
			} finally {
				itr.remove();
			}
		}
			
	    // This will make the executor accept no new threads
	    // and finish all existing threads in the queue
		eservice.shutdown();
		
	    // Wait until all threads are finish
	    while (!eservice.isTerminated()) {

	    }
		
		getTask().afterAllJobsAreExecuted( rootOutputManager, sharedState, paramsExperiment.getLogReporterExperiment() );
		return monitor.createStatistics();
	}
	
	private int selectNumProcessors( LogReporter logReporter, boolean detailedLogging ) {
		
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		
		int nrOfProcessors = availableProcessors - 1;
		
		if (maxNumProcessors>0) {
			nrOfProcessors = Math.min(nrOfProcessors, maxNumProcessors);
		}
		
		if (detailedLogging) {
			logReporter.logFormatted(
				"Using %s from: %d",
				LanguageUtilities.prefixPluralizeMaybe(nrOfProcessors, "processor"),
				availableProcessors
			);
		}
		
		return nrOfProcessors;
	}
	
	
	private void submitJob( ExecutorService eservice, T inputObj, int index, S sharedState, ParametersExperiment paramsExperiment, ConcurrentJobMonitor monitor ) {
		
		JobDescription td = new JobDescription(inputObj.descriptiveName(), index );
		
		ParametersUnbound<T,S> paramsUnbound = new ParametersUnbound<>(paramsExperiment);
		paramsUnbound.setInputObject(inputObj);
		paramsUnbound.setSharedState(sharedState);
		paramsUnbound.setSupressExceptions( supressExceptions );
					
		// Task always gets duplicated when it's called
		JobState taskState = new JobState();
		eservice.submit(
			new CallableJob<>(
				getTask(),
				paramsUnbound,
				taskState,
				td,
				monitor,
				logReporterForMonitor(paramsExperiment)
			)
		);
		
		SubmittedJob submittedTask = new SubmittedJob( td, taskState);
		monitor.add( submittedTask );
	}

	public boolean isSupressExceptions() {
		return supressExceptions;
	}

	public void setSupressExceptions(boolean supressExceptions) {
		this.supressExceptions = supressExceptions;
	}

	public int getMaxNumProcessors() {
		return maxNumProcessors;
	}

	public void setMaxNumProcessors(int maxNumProcessors) {
		this.maxNumProcessors = maxNumProcessors;
	}
}
