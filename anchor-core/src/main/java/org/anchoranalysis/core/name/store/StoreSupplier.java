/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.name.store;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Supplier of an object for a store
 * 
 * @author Owen Feehan
 *
 * @param <T> type supplied to the store
 */
@FunctionalInterface
public interface StoreSupplier<T> {

    /**
     * Gets the object being supplied to the store
     * 
     * @return the supplied object
     * @throws OperationFailedException if anything goes wrong
     */
    T get() throws OperationFailedException;
    
    /**
     * Memoizes (caches) the supplied object, and returning it with an identical interface
     * 
     * @param <T> type to supply
     * @param supplier supplier to cache
     * @return a {@link StoreSupplier} interface that memoizes the supplied object
     */
    public static <T> StoreSupplier<T> cache( StoreSupplier<T> supplier ) {
        return cacheResettable(supplier)::get;
    }
    
    /**
     * Memoizes (caches) the supplied object, and returning it with a {@link CachedSupplier} interface
     * <p>
     * This interface can be used to reset and do other operations o the cache.
     * 
     * @param <T> type to supply
     * @param supplier supplier to cache
     * @return a {@link StoreSupplier} interface that memoizes the supplied object
     */
    public static <T> CachedSupplier<T,OperationFailedException> cacheResettable( StoreSupplier<T> supplier ) {
        return CachedSupplier.cache(supplier::get);
    }
}
