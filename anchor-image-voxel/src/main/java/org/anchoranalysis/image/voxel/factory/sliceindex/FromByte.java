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

package org.anchoranalysis.image.voxel.factory.sliceindex;

import com.google.common.base.Preconditions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.Extent;

public class FromByte implements SliceBufferIndex<UnsignedByteBuffer> {

    private final VoxelBuffer<UnsignedByteBuffer>[] buffer;
    private final Extent extent;

    // START FACTORY METHODS
    public static SliceBufferIndex<UnsignedByteBuffer> createInitialized(Extent extent) {
        FromByte p = new FromByte(extent);
        p.init();
        return p;
    }

    public static SliceBufferIndex<UnsignedByteBuffer> createUninitialized(Extent extent) {
        return new FromByte(extent);
    }
    // END FACTORY METHODS

    private FromByte(Extent extent) {
        this.extent = extent;
        buffer = VoxelBufferFactory.allocateUnsignedByteArray(extent.z());
    }

    @Override
    public void replaceSlice(int z, VoxelBuffer<UnsignedByteBuffer> pixels) {
        buffer[z] = pixels;
        buffer[z].buffer().clear();
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> slice(int z) {
        Preconditions.checkArgument(z >= 0);
        VoxelBuffer<UnsignedByteBuffer> bufferSlice = buffer[z];
        bufferSlice.buffer().clear();
        return bufferSlice;
    }

    @Override
    public Extent extent() {
        return extent;
    }

    private void init() {
        int volumeXY = extent.volumeXY();
        extent.iterateOverZ(z -> buffer[z] = VoxelBufferFactory.allocateUnsignedByte(volumeXY));
    }
}
