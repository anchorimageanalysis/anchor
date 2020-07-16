/* (C)2020 */
package org.anchoranalysis.experiment;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception that occurs when an experiment is executed. This should always contain a friendly
 * error message to the user
 *
 * @author Owen Feehan
 */
public class ExperimentExecutionException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -7030930961354624998L;

    public ExperimentExecutionException(String string) {
        super(string);
    }

    public ExperimentExecutionException(Throwable exc) {
        super(exc);
    }

    /**
     * A string message displayed to the user as well as a stack-trace of the cause
     *
     * @param string the error message displayed to the user
     * @param cause cause of the error, and a stack trace is displayed to theu ser
     */
    public ExperimentExecutionException(String string, Throwable cause) {
        super(string, cause);
    }
}
