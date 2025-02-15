/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.resizer;

import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;

/**
 * Resizes with an interpolator that uses <a
 * href="https://en.wikipedia.org/wiki/Linear_interpolation">linear interpolation</a> as implemented
 * in Imglib2.
 *
 * @see ClampingNLinearInterpolatorFactory
 * @author Owen Feehan
 */
public class Linear extends VoxelsResizerImgLib2 {

    /** Default constructor. */
    public Linear() {
        // Using a clamping interpolator as otherwise weird values can occur at 255
        // This idea comes from the following post:
        // https://github.com/imglib/imglib2/issues/166
        super(
                new ClampingNLinearInterpolatorFactory<>(),
                new ClampingNLinearInterpolatorFactory<>(),
                new ClampingNLinearInterpolatorFactory<>());
    }

    @Override
    public boolean canValueRangeChange() {
        return true;
    }
}
