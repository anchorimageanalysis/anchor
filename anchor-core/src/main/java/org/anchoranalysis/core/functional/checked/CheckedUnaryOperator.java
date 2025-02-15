package org.anchoranalysis.core.functional.checked;

import java.util.function.UnaryOperator;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
 * Like a {@link UnaryOperator} but allows an exception to be thrown.
 *
 * @author Owen Feehan
 * @param <T> input and output-type of operator
 * @param <E> type of exception that may be thrown if something goes wrong
 */
@FunctionalInterface
public interface CheckedUnaryOperator<T, E extends Exception> {

    /**
     * Applies the operation.
     *
     * @param in the argument passed <i>in</i> to the operation.
     * @return the result of the operation, passed <i>out</i>.
     * @throws E if the operation cannot succeed.
     */
    T apply(T in) throws E;
}
