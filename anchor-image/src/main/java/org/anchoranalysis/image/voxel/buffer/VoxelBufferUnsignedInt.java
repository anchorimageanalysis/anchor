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

import lombok.AllArgsConstructor;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

@AllArgsConstructor
final class VoxelBufferUnsignedInt extends VoxelBuffer<UnsignedIntBuffer> {

    private final UnsignedIntBuffer delegate;

    @Override
    public UnsignedIntBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<UnsignedIntBuffer> duplicate() {
        return new VoxelBufferUnsignedInt(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedIntVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return delegate.getRaw(index);
    }

    @Override
    public void putInt(int index, int value) {
        delegate.putRaw(index, value);
    }

    @Override
    public void putByte(int index, byte value) {
        delegate.putRaw(index, PrimitiveConverter.unsignedByteToInt(value));
    }

    @Override
    public void transferFrom(
            int destinationIndex, VoxelBuffer<UnsignedIntBuffer> src, int sourceIndex) {
        delegate.putRaw(destinationIndex, src.buffer().getRaw(sourceIndex));
    }

    @Override
    public int capacity() {
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

    @Override
    public byte[] underlyingBytes() {
        int[] array = delegate.array();
        ByteBuffer buffer = ByteBuffer.allocate(array.length * 4);
        buffer.asIntBuffer().put(array);
        return buffer.array();
    }
}
