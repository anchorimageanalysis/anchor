/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.experiment.bean.io;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.bean.processor.JobProcessor;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.io.ReplaceInputManager;
import org.anchoranalysis.experiment.io.ReplaceOutputManager;
import org.anchoranalysis.experiment.io.ReplaceTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.bean.OutputManager;

/**
 * An experiment that uses both an {@link InputManager} to specify inputs and a {@link
 * OutputManager} to specify outputting.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state for job
 */
public class InputOutputExperiment<T extends InputFromManager, S> extends OutputExperiment
        implements ReplaceInputManager, ReplaceOutputManager, ReplaceTask<T, S> {

    // START BEAN PROPERTIES
    /** The input-manager to specify where/which/how necessary inputs for the experiment occur. */
    @BeanField @Getter @Setter private InputManager<T> input;

    /**
     * What task is associated with the experiment, and how it is processed.
     *
     * <p>e.g. how the task processes the inputs in the form of jobs (sequentially, parallel, how
     * many processors? etc.)
     */
    @BeanField @Getter @Setter private JobProcessor<T, S> taskProcessor;

    /**
     * Where log messages that <b>do<b> pertain to a specific job (input) appear.
     *
     * <p>This is in contrast to {@code logExperiment} where non-job specific log messages appear.
     */
    @BeanField @Getter @Setter private LoggingDestination logTask = new ToConsole();
    // END BEAN PROPERTIES

    @Override
    public boolean useDetailedLogging() {
        // Disable detailed-logging if the task has a very quick execution
        // (unless we are in 'force' mode).
        if (isForceDetailedLogging() || !taskProcessor.hasVeryQuickPerInputExecution()) {
            return true;
        }

        return super.useDetailedLogging();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void replaceInputManager(InputManager<?> inputManager) throws OperationFailedException {
        this.input = (InputManager<T>) inputManager;
    }

    @Override
    public void replaceOutputManager(OutputManager outputter) throws OperationFailedException {
        this.setOutput(outputter);
    }

    @Override
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException {
        this.taskProcessor.replaceTask(taskToReplace);
    }

    @Override
    protected void executeExperimentWithParams(ParametersExperiment params)
            throws ExperimentExecutionException {
        try {
            List<T> inputObjects =
                    getInput()
                            .inputs(
                                    new InputManagerParams(
                                            params.getExperimentArguments().createInputContext(),
                                            ProgressReporterNull.get(),
                                            new Logger(params.getLoggerExperiment())));
            checkCompabilityInputObjects(inputObjects);

            params.setLoggerTaskCreator(logTask);

            taskProcessor.executeLogStats(params.getOutputter(), inputObjects, params);

        } catch (AnchorIOException | IOException e) {
            throw new ExperimentExecutionException(
                    "An error occured while searching for inputs", e);
        }
    }

    @Override
    protected Optional<MultiLevelOutputEnabled> defaultOutputs() {
        return taskProcessor.getTask().defaultOutputs();
    }

    private void checkCompabilityInputObjects(List<T> inputObjects)
            throws ExperimentExecutionException {
        for (T object : inputObjects) {
            if (!taskProcessor.isInputObjectCompatibleWith(object.getClass())) {
                throw new ExperimentExecutionException(
                        String.format(
                                "Input has an incompatible class for the associated task: %s",
                                object.getClass().toString()));
            }
        }
    }
}
