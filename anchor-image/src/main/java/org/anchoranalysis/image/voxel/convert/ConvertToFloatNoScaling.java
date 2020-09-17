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

package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsRemaining;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class ConvertToFloatNoScaling extends VoxelsConverter<FloatBuffer> {

    @Override
    public void convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn, VoxelBuffer<FloatBuffer> bufferOut) {
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, (in,out) -> 
            out.put(in.get())
        );
    }

    @Override
    public void convertFromInt(VoxelBuffer<UnsignedIntBuffer> bufferIn, VoxelBuffer<FloatBuffer> bufferOut) {
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, (in,out) -> 
            out.put((float) bufferIn.buffer().getUnsigned())
        );
    }

    @Override
    public void convertFromShort(VoxelBuffer<UnsignedShortBuffer> bufferIn, VoxelBuffer<FloatBuffer> bufferOut) {
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, (in,out) -> 
            out.put((float) in.getUnsigned())
        );
    }

    @Override
    public void convertFromByte(VoxelBuffer<UnsignedByteBuffer> bufferIn, VoxelBuffer<FloatBuffer> bufferOut) {
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, (in,out) -> 
            out.put((float) in.getUnsigned())
        );
    }
}
