/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.stack.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.stack.time.ExtractFrameStore;
import org.anchoranalysis.image.io.stack.time.TimeSeries;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Base class for inputs which somehow eventually send up providing stacks, with or without names.
 *
 * @author Owen Feehan
 */
public interface ProvidesStackInput extends InputFromManager {

    /**
     * Exposes the input as a single {@link Stack} throw an error if more than one exists.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the single stack.
     * @throws OperationFailedException if more than one stack exists, or otherwise a fatal error
     *     occurs loading the stacks.
     */
    default Stack asStack(Logger logger) throws OperationFailedException {
        NamedStacks set = asSet(logger);
        if (set.isEmpty()) {
            throw new OperationFailedException(
                    "No stack exists in the input. Exactly one is required.");
        }
        if (set.size() > 1) {
            throw new OperationFailedException(
                    "More than one stack exists in the input, only one is expected.");
        }
        return set.getArbitraryElement();
    }

    /**
     * Exposes the input as a set of named stacks (inferring the names).
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return a set of named-stacks.
     * @throws OperationFailedException if a fatal error occurs loading the stack.
     */
    default NamedStacks asSet(Logger logger) throws OperationFailedException {
        NamedStacks set = new NamedStacks();
        addToStoreInferNames(new ExtractFrameStore(set), 0, logger);
        return set;
    }

    /**
     * Adds the current object to a named-store of stacks (using the default series).
     *
     * @param store the store.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException if the operation cannot successfully complete.
     */
    default void addToStoreInferNames(NamedProviderStore<Stack> store, Logger logger)
            throws OperationFailedException {
        addToStoreInferNames(
                new ExtractFrameStore(store),
                0, // default series-number
                logger);
    }

    /**
     * The number of time-frames in the underlying input image.
     *
     * @return the number of time-frames.
     * @throws OperationFailedException if the operation cannot successfully complete.
     */
    int numberFrames() throws OperationFailedException;

    /**
     * Adds any stacks exposed by the current element to a named-store of stacks - inferring the
     * names of the {@link Stack}s.
     *
     * @param stacks the named-store of stacks.
     * @param seriesIndex the index of the series (beginning at 0) to retrieve stacks from the
     *     {@link TimeSeries}.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException if the operation cannot successfully complete.
     */
    void addToStoreInferNames(NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException;

    /**
     * Adds any stacks exposed by the current element to a named-store of stacks - with a particular
     * name.
     *
     * @param name the name to use for the added stack.
     * @param stacks the named-store of stacks.
     * @param seriesIndex the index of the series (beginning at 0) to retrieve stacks from the
     *     {@link TimeSeries}.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException if the operation cannot successfully complete.
     */
    void addToStoreWithName(
            String name, NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException;
}
