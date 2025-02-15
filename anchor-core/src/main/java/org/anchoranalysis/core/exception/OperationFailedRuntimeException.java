package org.anchoranalysis.core.exception;

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

/**
 * Generic <i>runtime</i> exception that can be thrown when a particular operation fails.
 *
 * <p>{@link OperationFailedRuntimeException} is a similar checked exception.
 *
 * @author Owen Feehan
 */
public class OperationFailedRuntimeException extends AnchorRuntimeException {

    /** */
    private static final long serialVersionUID = -5014516097016484634L;

    /**
     * Creates with only a message.
     *
     * @param message the message.
     */
    public OperationFailedRuntimeException(String message) {
        super(message);
    }

    /**
     * Creates with only a cause.
     *
     * @param cause the cause.
     */
    public OperationFailedRuntimeException(Exception cause) {
        super(cause);
    }
}
