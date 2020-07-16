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

package org.anchoranalysis.image.interpolator;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public interface Interpolator {

    /**
     * Interpolates from src to dest. Both boxes must be 2-dimensional. Returns the destination
     * buffer (either as passed, or a new one that was created)
     *
     * @param src
     * @param dest
     */
    VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> src, VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest);

    VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> src, VoxelBuffer<ShortBuffer> dest, Extent eSrc, Extent eDest);

    /**
     * Returns TRUE if it's possible for values to be created after interpolation that aren't found
     * in the input-image. Returns the destination buffer (either as passed, or a new one that was
     * created)
     *
     * @return
     */
    boolean isNewValuesPossible();
}
