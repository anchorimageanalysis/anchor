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

package org.anchoranalysis.image.voxel.buffer;

import java.nio.IntBuffer;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class VoxelBufferInt extends VoxelBuffer<IntBuffer> {

    private final IntBuffer delegate;

    public static VoxelBufferInt allocate(int size) {
        return new VoxelBufferInt(IntBuffer.allocate(size));
    }

    public static VoxelBufferInt wrapArray(int[] arr) {
        return new VoxelBufferInt(IntBuffer.wrap(arr));
    }

    public static VoxelBufferInt wrapBuffer(IntBuffer buffer) {
        return new VoxelBufferInt(buffer);
    }

    @Override
    public IntBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<IntBuffer> duplicate() {
        return new VoxelBufferInt(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedIntVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return PrimitiveConverter.unsignedIntToInt(delegate.get(index));
    }

    @Override
    public void putInt(int index, int value) {
        delegate.put(index, value);
    }

    @Override
    public void putByte(int index, byte value) {
        delegate.put(index, PrimitiveConverter.unsignedByteToInt(value));
    }

    @Override
    public void transferFrom(int destinationIndex, VoxelBuffer<IntBuffer> src, int sourceIndex) {
        delegate.put(destinationIndex, src.buffer().get(sourceIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
    
    @Override
    public boolean hasRemaining() {
        return delegate.hasRemaining();
    }

    @Override
    public void position(int newPosition) {
        delegate.position(newPosition);
    }
    
    @Override
    public boolean isDirect() {
        return delegate.isDirect();
    }
}
