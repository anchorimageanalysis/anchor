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

package org.anchoranalysis.mpp.mark.conic;

import java.util.Arrays;
import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.orientation.Orientation;
import org.anchoranalysis.mpp.mark.MarkWithPosition;
import org.anchoranalysis.spatial.point.Point3d;

public abstract class ConicBase extends MarkWithPosition {

    /** */
    private static final long serialVersionUID = 1680124471263339009L;

    public ConicBase() {
        super();
    }

    public ConicBase(MarkWithPosition src) {
        super(src);
    }

    public abstract double[] createRadiiArrayResolved(Optional<Resolution> resolution);

    public abstract double[] createRadiiArray();

    public abstract void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii);

    public abstract void setMarksExplicit(Point3d pos, Orientation orientation);

    public abstract void setMarksExplicit(Point3d pos);

    public double[] radiiOrderedResolved(Optional<Resolution> resolution) {
        double[] radii = createRadiiArrayResolved(resolution);
        Arrays.sort(radii);
        return radii;
    }

    public double[] radiiOrdered() {
        double[] radii = createRadiiArray();
        Arrays.sort(radii);
        return radii;
    }
}
