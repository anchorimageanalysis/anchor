/*-
 * #%L
 * anchor-image-feature
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
/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

public final class MergedPairsInclude {

    private boolean includeFirst;
    private boolean includeSecond;
    private boolean includeMerged;

    public MergedPairsInclude() {
        this(true, true, true);
    }

    public MergedPairsInclude(boolean includeFirst, boolean includeSecond, boolean includeMerged) {
        super();
        this.includeFirst = includeFirst;
        this.includeSecond = includeSecond;
        this.includeMerged = includeMerged;
    }

    public boolean includeFirstOrSecond() {
        return includeFirst || includeSecond;
    }

    public boolean includeFirst() {
        return includeFirst;
    }

    public boolean includeSecond() {
        return includeSecond;
    }

    public boolean includeMerged() {
        return includeMerged;
    }
}
