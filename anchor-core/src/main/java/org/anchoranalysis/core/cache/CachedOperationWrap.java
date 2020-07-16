package org.anchoranalysis.core.cache;

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
 * A helper class to defined a {@link CachedOperation} using a functional.
 *
 * <p>As {@link CachedOperation} is an abstract-base-class, it can otherwise be cumbersome to
 * quickly override.
 *
 * @author Owen Feehan
 * @param <R>
 * @param <E>
 */
public class CachedOperationWrap<R, E extends Exception> extends CachedOperation<R, E> {

    /** A functional to be wrapped */
    @FunctionalInterface
    public interface WrapFunctional<T, E extends Exception> {
        T apply() throws E;
    }

    private WrapFunctional<R, E> delegate;

    public <S> CachedOperationWrap(WrapFunctional<R, E> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    protected R execute() throws E {
        return delegate.apply();
    }
}
