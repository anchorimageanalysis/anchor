/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import lombok.NoArgsConstructor;

/**
 * A circle
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class MarkCircle extends MarkSingleRadius {

    /** */
    private static final long serialVersionUID = 8551900716243748046L;

    // Constructor
    public MarkCircle(Bound boundRadius) {
        super(boundRadius);
    }

    // Copy Constructor - we do not copy scene
    public MarkCircle(MarkCircle src) {
        super(src);
    }

    @Override
    public String getName() {
        return "circle";
    }

    @Override
    public double volume(int regionID) {
        return (2 * Math.PI * radiusForRegionSquared(regionID));
    }

    @Override
    public String toString() {
        return String.format("%s %s pos=%s %s", "circle", strId(), strPos(), strMarks());
    }

    // The duplicate operation for the marks (we avoid clone() in case its confusing, we might not
    // always shallow copy)
    @Override
    public Mark duplicate() {
        return new MarkCircle(this);
    }

    @Override
    public int numDims() {
        return 2;
    }
}
