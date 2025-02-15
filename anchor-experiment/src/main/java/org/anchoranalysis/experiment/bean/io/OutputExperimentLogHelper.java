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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Divider;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;

/**
 * Helps creating and outputting messages to the log for {@link
 * org.anchoranalysis.experiment.bean.io.OutputExperiment}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class OutputExperimentLogHelper {

    private static final Divider DIVIDER = new Divider();

    /**
     * Log a start-experiment message, but only when in detailed mode.
     *
     * @param parameters the parameters that define an experiment.
     */
    public static void maybeLogStart(ParametersExperiment parameters) {
        if (parameters.isDetailedLogging()) {
            parameters
                    .getLoggerExperiment()
                    .logFormatted(
                            "Experiment %s started writing to %s",
                            parameters.getExperimentIdentifier(),
                            parameters.getOutputter().getOutputDirectory());
        }
    }

    /**
     * Log a start-experiment message, but only when in detailed mode.
     *
     * @param recordedOutputs tracks which outputs have been written to the file-system or not.
     * @param parameters the parameters that define an experiment.
     */
    public static void maybeRecordedOutputs(
            MultiLevelRecordedOutputs recordedOutputs, ParametersExperiment parameters) {
        if (parameters.isDetailedLogging()) {

            parameters
                    .getLoggerExperiment()
                    .logFormatted(
                            "%s%n%s%n%s",
                            DIVIDER.withLabel("Outputs"),
                            new SummarizeRecordedOutputs(recordedOutputs).summarize(),
                            DIVIDER.withoutLabel());
        }
    }

    /**
     * Log a completed-experiment message, but only when in detailed mode.
     *
     * @param parameters the parameters that define an experiment.
     * @param executionTimeSeconds how many seconds duration it took to execute the task.
     */
    public static void maybeLogCompleted(
            ParametersExperiment parameters, long executionTimeSeconds) {
        if (parameters.isDetailedLogging()) {

            parameters
                    .getLoggerExperiment()
                    .logFormatted(
                            "Experiment %s completed (%ds) writing to %s",
                            parameters.getExperimentIdentifier(),
                            executionTimeSeconds,
                            parameters.getOutputter().getOutputDirectory());
        }
    }
}
