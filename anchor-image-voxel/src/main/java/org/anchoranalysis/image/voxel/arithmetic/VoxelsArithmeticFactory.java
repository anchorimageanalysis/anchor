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
package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.FloatBuffer;
import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates {@link VoxelsArithmetic} for buffers of different types.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsArithmeticFactory {

    /**
     * Create a {@link VoxelsArithmetic} for {@link UnsignedByteBuffer}.
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed.
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer).
     * @return a newly-created instance.
     */
    public static VoxelsArithmetic createUnsignedByte(
            Extent extent, IntFunction<UnsignedByteBuffer> bufferForSlice) {
        return new UnsignedByteImplementation(extent, bufferForSlice);
    }

    /**
     * Create a {@link VoxelsArithmetic} for {@link UnsignedShortBuffer}.
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed.
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer).
     * @return a newly-created instance.
     */
    public static VoxelsArithmetic createUnsignedShort(
            Extent extent, IntFunction<UnsignedShortBuffer> bufferForSlice) {
        return new UnsignedShortImplementation(extent, bufferForSlice);
    }

    /**
     * Create a {@link VoxelsArithmetic} for {@link UnsignedIntBuffer}.
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed.
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer).
     * @return a newly-created instance.
     */
    public static VoxelsArithmetic createUnsignedInt(
            Extent extent, IntFunction<UnsignedIntBuffer> bufferForSlice) {
        return new UnsignedIntImplementation(extent, bufferForSlice);
    }

    /**
     * Create a {@link VoxelsArithmetic} for {@link FloatBuffer}.
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed.
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer).
     * @return a newly-created instance.
     */
    public static VoxelsArithmetic createFloat(
            Extent extent, IntFunction<FloatBuffer> bufferForSlice) {
        return new FloatImplementation(extent, bufferForSlice);
    }
}
