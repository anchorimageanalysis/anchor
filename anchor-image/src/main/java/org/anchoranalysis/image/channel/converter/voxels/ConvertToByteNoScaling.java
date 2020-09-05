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

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class ConvertToByteNoScaling extends VoxelsConverter<UnsignedByteBuffer> {

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float value = bufferIn.buffer().get();
            if (value > UnsignedByteVoxelType.MAX_VALUE_INT) {
                value = UnsignedByteVoxelType.MAX_VALUE_INT;
            }
            if (value < 0) {
                value = 0;
            }
            bufferOut.putFloat(value);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.putUnsignedInt(bufferIn.buffer().get());
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromShort(VoxelBuffer<UnsignedShortBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.putUnsigned(bufferIn.buffer().getUnsigned());
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromByte(VoxelBuffer<UnsignedByteBuffer> in) {
        return in.duplicate();
    }
}
