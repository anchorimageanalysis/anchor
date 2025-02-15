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

package org.anchoranalysis.experiment.task.processor;

import java.util.Optional;
import org.anchoranalysis.core.log.Divider;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * Logs events when jobs start and stop, with or without errors.
 *
 * <p>The class is thread-safe as there might be multiple calls in parallel.
 *
 * @author Owen Feehan
 */
public class JobStartStopLogger {

    // Assume terminal has at least 80 characters in width
    private static final Divider DIVIDER = new Divider('#');

    /** a noun describing the job that appears in the log e.g. "Job". */
    private final String jobDescriptionText;

    /**
     * Indicates if lines of hashes should be placed before and after each log message (adds
     * emphasis).
     */
    private final boolean showHashSeperators;

    /**
     * When the number of ongoing jobs is less than this threshold, they are shown in event logs.
     *
     * <p>A value of 0 disables.
     */
    private final int showOngoingJobsLessThan;

    /** Monitors the progress of jobs. */
    private final ConcurrentJobMonitor monitor;

    /** Write messages to logger, if defined. */
    private final Optional<MessageLogger> logger;

    /**
     * Whether to log messages for each job's events. If there is only a single job, these are not
     * shown.
     */
    private final boolean disableLogMessages;

    /**
     * Creates a job-logger.
     *
     * @param jobDescriptionText a noun describing the job that appears in the log e.g. "Job"
     * @param monitor monitors the progress of jobs.
     * @param showHashSeperators indicates if lines of hashes should be placed before and after each
     *     log message (adds emphasis).
     * @param showOngoingJobsLessThan When the number of ongoing jobs is less than this threshold,
     *     they are shown in event logs. 0 disables.
     * @param logger write messages to logger, if defined.
     */
    public JobStartStopLogger(
            String jobDescriptionText,
            ConcurrentJobMonitor monitor,
            boolean showHashSeperators,
            int showOngoingJobsLessThan,
            Optional<MessageLogger> logger) {
        this.jobDescriptionText = jobDescriptionText;
        this.monitor = monitor;
        this.showHashSeperators = showHashSeperators;
        this.showOngoingJobsLessThan = showOngoingJobsLessThan;
        this.logger = logger;
        this.disableLogMessages = monitor.getTotalNumberJobs() <= 1 || !logger.isPresent();
    }

    /**
     * Performs logging for when a job starts.
     *
     * @param job the job that was started.
     */
    public synchronized void logStart(JobDescription job) {

        if (disableLogMessages) {
            return;
        }
        // We put some spaces instead of the time, to keep the tabulation similar
        logEvent("start", job, "     ");
    }

    /**
     * Performs logging for when a job ends.
     *
     * @param job the job that ended.
     * @param monitor tracks the execution-state of a job.
     */
    public synchronized void logEnd(JobDescription job, JobStateMonitor monitor) {

        if (disableLogMessages) {
            return;
        }

        logEvent(monitor.isCompletedSuccessfully() ? "end  " : "ERROR", job, timeText(monitor));
    }

    private void logEvent(String eventWord, JobDescription job, String timeStr) {

        if (!logger.isPresent()) {
            return;
        }

        logWithDecoration(
                () ->
                        logger.get()
                                .logFormatted(
                                        "%s %4d:\t%s\t[%s]\t%s\t%s  %s",
                                        jobDescriptionText,
                                        job.getNumber(),
                                        eventWord,
                                        monitor.currentStateDescription(),
                                        timeStr,
                                        job.getShortName(),
                                        ongoingJobText()));
    }

    private String ongoingJobText() {
        return showOngoingJobsLessThan > 0
                ? monitor.describeUncompletedJobs(showOngoingJobsLessThan).orElse("")
                : "";
    }

    private void logWithDecoration(Runnable logFunction) {
        logMaybeDivider();
        logFunction.run();
        logMaybeDivider();
    }

    private void logMaybeDivider() {
        if (showHashSeperators) {
            logger.get().log(DIVIDER.withoutLabel()); // NOSONAR
        }
    }

    private static String timeText(JobStateMonitor jobState) {
        return String.format("(%ds)", jobState.getExecutionDuration() / 1000);
    }
}
