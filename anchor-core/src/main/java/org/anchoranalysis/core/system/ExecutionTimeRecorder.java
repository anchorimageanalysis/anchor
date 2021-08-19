package org.anchoranalysis.core.system;

import java.util.Optional;
import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * Records the execution-time of particular operations.
 *
 * @author Owen Feehan
 */
public interface ExecutionTimeRecorder {

    /**
     * Records the execution-time of a particular operation.
     *
     * @param operationIdentifier a string uniquely identifying this operation.
     * @param millis how long the operation took in milliseconds.
     */
    void recordExecutionTime(String operationIdentifier, long millis);

    /**
     * Records the execution-time of a particular operation.
     *
     * @param operationIdentifierFirst a string uniquely identifying this operation, to be used if
     *     it doesn't already exist.
     * @param operationIdentiferSubsequent a string uniquely identifying this operation, to be used
     *     if <code>operationIdentifierFirst</code> already exists.
     * @param millis how long the operation took in milliseconds.
     */
    void recordExecutionTime(
            String operationIdentifierFirst, String operationIdentiferSubsequent, long millis);

    /**
     * Executes an {@code operation} while recording the execution-time - <b>with a return type</b>.
     *
     * <p>Operation times are always executed, even they throw an Exception or otherwise end in
     * failure.
     *
     * @param writeOperationIdentifier an identifier for this type of write-operation, under which
     *     execution-times are aggregated.
     * @param operation the operation to execute
     * @param <E> type of an exception that {@code operation} may throw.
     * @throws E if {@code operation} throws this exception;
     */
    default <E extends Exception> void recordExecutionTime(
            String writeOperationIdentifier, CheckedRunnable<E> operation) throws E {
        long startTimestamp = System.currentTimeMillis();
        try {
            operation.run();
        } finally {
            recordTimeDifferenceFrom(writeOperationIdentifier, startTimestamp);
        }
    }

    /**
     * Executes an {@code operation} while recording the execution-time - <b>with a return type</b>.
     *
     * <p>Operation times are always executed, even they throw an Exception or otherwise end in
     * failure.
     *
     * @param operationIdentifier an identifier for this type of write-operation, under which
     *     execution-times are aggregated.
     * @param operation the operation to execute
     * @param <E> type of an exception that {@code operation} may throw.
     * @throws E if {@code operation} throws this exception;
     */
    default <T, E extends Exception> T recordExecutionTime(
            String operationIdentifier, CheckedSupplier<T, E> operation) throws E {
        long startTimestamp = System.currentTimeMillis();
        try {
            return operation.get();
        } finally {
            recordTimeDifferenceFrom(operationIdentifier, startTimestamp);
        }
    }

    /**
     * Records the execution-time of a particular operation by subtracting the start-time from the
     * current clock.
     *
     * @param operationIdentifier a string uniquely identifying this operation
     * @param startTimestamp a system clock timestamp (in milliseconds since the epoch) for when the
     *     operation began.
     * @return the current timestamp (millis from the epoch) used to measure the end of the
     *     operation
     */
    default long recordTimeDifferenceFrom(String operationIdentifier, long startTimestamp) {
        long currentTimestamp = System.currentTimeMillis();
        long executionTime = currentTimestamp - startTimestamp;
        recordExecutionTime(operationIdentifier, executionTime);
        return currentTimestamp;
    }

    /**
     * Like {@link #recordTimeDifferenceFrom(String, long)} but uses an alternative identifier if
     * the entry does not already exist.
     *
     * <p>The operation is presumed to end when this function is called.
     *
     * @param operationIdentifier the unique name of the operation to record the time against
     * @param alternativeIdentifierIfFirst an alternative unique to use if this is the first time
     *     the execution-time is recorded.
     * @param startTimestamp the timestamp describing millis from the epoch at the start of the
     *     operation
     * @return the current timestamp (millis from the epoch) used to measure the end of the
     *     operation
     */
    default long recordTimeDifferenceFrom(
            String operationIdentifier,
            Optional<String> alternativeIdentifierIfFirst,
            long startTimestamp) {
        long currentTimestamp = System.currentTimeMillis();
        if (alternativeIdentifierIfFirst.isPresent()) {
            long executionTime = currentTimestamp - startTimestamp;
            recordExecutionTime(
                    alternativeIdentifierIfFirst.get(), operationIdentifier, executionTime);
        } else {
            recordTimeDifferenceFrom(operationIdentifier, startTimestamp);
        }
        return currentTimestamp;
    }
}
