package org.anchoranalysis.mpp.io.bean.task;

/*-
 * #%L
 * anchor-mpp-io
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.io.IReplaceTask;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.image.io.input.NamedChnlsInput;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.input.MultiInput;

/**
 * Converts NamedChnlsInput to a variety of others to match a delegate task
 * 
 * <p>Note that the presence of IReplaceTask gives special behavior to this task in the JobProcessor</p>
 * 
 * @author owen
 *
 * @param <T> the named-chnls-input we expect to receive
 * @param <S> shared-state of the task
 * @param <U> the named-chnls-input the delegate task contains
 */
public class ConvertNamedChnlsInputTask<T extends NamedChnlsInput,S,U extends NamedChnlsInput>
	extends Task<T,S>
	implements IReplaceTask<U,S> {		

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private Task<U,S> task;
	// END BEAN PROPERTIES

	@Override
	public S beforeAnyJobIsExecuted(BoundOutputManagerRouteErrors outputManager, ParametersExperiment params)
			throws ExperimentExecutionException {
		return task.beforeAnyJobIsExecuted(outputManager, params);
	}
	
	@Override
	public void doJobOnInputObject(InputBound<T, S> params) throws JobExecutionException {

		Class<? extends InputFromManager> inputObjClass = params.getInputObject().getClass();
		
		InputTypesExpected expectedFromDelegate = task.inputTypesExpected();
		if (expectedFromDelegate.doesClassInheritFromAny(inputObjClass)) {
			// All good, the delegate happily accepts our type without change
			doJobWithNamedChnlInput(params);
		} else if (expectedFromDelegate.doesClassInheritFromAny(MultiInput.class)) {
			doJobWithMultiInput(params);
		} else {
			throw new JobExecutionException(
				String.format(
					"Cannot pass or convert the input-type (%s) to match the delegate's expected input-type:%n%s",
					inputObjClass,
					expectedFromDelegate
				)
			);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doJobWithNamedChnlInput(InputBound<T, S> params) throws JobExecutionException {
		task.doJobOnInputObject( (InputBound<U, S>) params);
	}
	
	@SuppressWarnings("unchecked")
	private void doJobWithMultiInput(InputBound<T, S> params) throws JobExecutionException {
		InputBound<? extends InputFromManager, S> paramsChanged = params.changeInputObject(
			new MultiInput(params.getInputObject())
		);
		task.doJobOnInputObject( (InputBound<U, S>) paramsChanged );
	}
	
	@Override
	public InputTypesExpected inputTypesExpected() {
		InputTypesExpected expected = new InputTypesExpected(NamedChnlsInput.class);
		// Add the other types we'll consider converting
		expected.add(MultiInput.class);
		return expected;
	}
	
	@Override
	public void afterAllJobsAreExecuted(S sharedState, BoundIOContext context) throws ExperimentExecutionException {
		task.afterAllJobsAreExecuted(sharedState, context);
	}
	
	@Override
	public boolean hasVeryQuickPerInputExecution() {
		return task.hasVeryQuickPerInputExecution();
	}

	public Task<U, S> getTask() {
		return task;
	}

	@Override
	public void replaceTask(Task<U, S> taskToReplace) throws OperationFailedException {
		this.task = taskToReplace;
	}
	
	public void setTask(Task<U, S> task) {
		this.task = task;
	}
}
