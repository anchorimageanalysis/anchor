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

package org.anchoranalysis.image.voxel.kernel;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Caches a small number of slices around which we wish to work, so the memory is efficiently
 * accessed.
 *
 * @author Owen Feehan
 */
public class LocalSlices {

    private final byte[][] array;
    private final int shift;

    public LocalSlices(int z, int kernelSize, Voxels<UnsignedByteBuffer> voxels) {
        array = new byte[kernelSize][];

        shift = ((kernelSize - 1) / 2);

        for (int i = 0; i < kernelSize; i++) {

            int rel = z + i - shift;

            if (rel >= 0 && rel < voxels.extent().z()) {
                array[i] = voxels.sliceBuffer(rel).array();
            } else {
                array[i] = null;
            }
        }
    }

    /**
     * All local access is indexed relative (e.g. -1, -2, +1, +2 etc.)
     *
     * <p>If an invalid index is requested, then {@link Optional#empty} is returned.
     *
     * @param relativeZIndex the shift in the z index relative to the current z-slice's index
     * @return the buffer corresponding to the z-index, if it exists.
     */
    public Optional<UnsignedByteBuffer> getLocal(int relativeZIndex) {
        int index = relativeZIndex + shift;
        if (index >= 0 && index<array.length) {
            byte[] slice = array[index];
            return Optional.ofNullable(slice).map(UnsignedByteBuffer::wrapRaw);    
        } else {
            return Optional.empty();
        }
    }
}
