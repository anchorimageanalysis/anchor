/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.bean.grouper;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.grouper.InputGrouper;

/**
 * Determines how partition inputs into groups.
 *
 * <p>This is typically based upon the input-identifiers.
 *
 * @author Owen Feehan
 */
public abstract class Grouper extends AnchorBean<Grouper> {

    /**
     * Creates an {@link InputGrouper} that can be used to derive a group-key from a particular
     * input.
     *
     * @param groupIndexRange an index-range to use for grouping, by subsetting components from each
     *     input's identifier.
     * @return the {@link InputGrouper}, if grouping is enabled. Otherwise {@link Optional#empty()}.
     */
    public abstract Optional<InputGrouper> createInputGrouper(
            Optional<IndexRangeNegative> groupIndexRange);
}
