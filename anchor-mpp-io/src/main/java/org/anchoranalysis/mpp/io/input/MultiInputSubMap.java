/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.Logger;

/**
 * A sub-item of a {@link MultiInput}.
 *
 * <p>It:
 *
 * <ol>
 *   <li>involves a map of objects of type {@code T}
 *   <li>can have its contents copied into a {@link NamedProviderStore}.
 * </ol>
 *
 * @param <T> object-type
 */
public interface MultiInputSubMap<T> {

    /** Adds an entry to the map */
    void add(String name, StoreSupplier<T> supplier);

    /**
     * Copies all the existing entries into a {@link NamedProviderStore}.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     */
    void addToStore(NamedProviderStore<T> namedStore, Logger logger)
            throws OperationFailedException;

    /** Returns null if non-existent */
    StoreSupplier<T> get(String name) throws OperationFailedException;
}
