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

package org.anchoranalysis.image.channel.converter.voxels;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against the maximum value in each
// buffer.
// So there is no clipping of values, but some might become very small.
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned
// sizes
public final class ConvertToByteScaleByType extends VoxelsConverter<UnsignedByteBuffer> {

    // This doesn't really make sense for a float, as the maximum value is so much higher, so we
    // take
    //  it as being the same as Integer.MAX_VALUE
    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        double div = (double) UnsignedIntVoxelType.MAX_VALUE / UnsignedByteVoxelType.MAX_VALUE_INT;

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.putDouble(bufferIn.buffer().get() / div);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromInt(VoxelBuffer<UnsignedIntBuffer> bufferIn) {

        double div = (double) UnsignedIntVoxelType.MAX_VALUE / UnsignedByteVoxelType.MAX_VALUE;

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.putDouble(bufferIn.buffer().getUnsigned() / div);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromShort(
            VoxelBuffer<UnsignedShortBuffer> bufferIn) {

        double div = (double) UnsignedShortVoxelType.MAX_VALUE / UnsignedByteVoxelType.MAX_VALUE;

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.putDouble(bufferIn.buffer().getUnsigned() / div);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromByte(VoxelBuffer<UnsignedByteBuffer> in) {
        return in.duplicate();
    }
}
