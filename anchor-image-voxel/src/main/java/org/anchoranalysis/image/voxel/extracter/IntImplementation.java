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
package org.anchoranalysis.image.voxel.extracter;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferInt;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.spatial.extent.Extent;

class IntImplementation extends Base<UnsignedIntBuffer> {

    public IntImplementation(Voxels<UnsignedIntBuffer> voxels) {
        super(voxels);
    }

    @Override
    public Voxels<UnsignedIntBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferInt mi = new MaxIntensityBufferInt(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<UnsignedIntBuffer> projectMean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long voxelWithMaxIntensity() {

        long max = 0;
        boolean first = true;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            UnsignedIntBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                long val = pixels.getUnsigned();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    @Override
    public void copyBufferIndexTo(
            UnsignedIntBuffer sourceBuffer,
            int sourceIndex,
            UnsignedIntBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.putRaw(destinationIndex, sourceBuffer.getRaw(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(UnsignedIntBuffer buffer, int index) {
        return (int) buffer.getUnsigned(index);
    }

    @Override
    protected boolean bufferValueGreaterThan(UnsignedIntBuffer buffer, int threshold) {
        return buffer.getRaw() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(UnsignedIntBuffer buffer, int value) {
        return buffer.getRaw() == value;
    }
}
