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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.statistics.HistogramFactory;

/**
 * A buffer of voxel-values, usually corresponding to a single z-slice in {@link Voxels}.
 *
 * <p>The operations are modelled on the NIO {@link Buffer} classes that can provide the underlying
 * buffers, but parameter {@code T} need not strictly be a sub-class of {@link Buffer}. This is
 * useful for automatically wrapping signed to unsigned values with custom buffers.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
public abstract class VoxelBuffer<T> {

    /**
     * Data-type of each voxel in the buffer.
     *
     * @return the data-type.
     */
    public abstract VoxelDataType dataType();

    /**
     * The associated buffer for storing the voxels.
     *
     * <p>This buffer is either a NIO or other classes that wraps the underlying array storing voxel
     * intensities.
     *
     * @return the buffer.
     */
    public abstract T buffer();

    /**
     * Creates a deep copy of the current object, including deep-copying the associated buffer.
     *
     * @return a newly created deep copy.
     */
    public abstract VoxelBuffer<T> duplicate();

    /**
     * Gets an element from the buffer at a particular position, converting, if necessary, to an
     * {@code int}.
     *
     * <p>Note this can provide slower access than reading directly in the native buffer type.
     *
     * <p>The <i>advantage</i> is that all buffer-types implement {@code getInt} and {@code putInt}
     * so no type-specific code needs to be written.
     *
     * <p>The <i>disadvantage</i> is that this can be less efficient, unless conversion to {@code
     * int} needs to occur anyway.
     *
     * @param index the index in the buffer.
     * @return the intensity value corresponding to position {@code index} in the buffer.
     */
    public abstract int getInt(int index);

    /**
     * Puts a int in the buffer at a particular position, converting, if necessary, to the buffer
     * type.
     *
     * <p>Note this can provide slower access than reading directly in the native buffer type. See
     * the note in {@link #getInt(int)}.
     *
     * @param index the index in the buffer.
     * @param value value to put in the buffer.
     */
    public abstract void putInt(int index, int value);

    /**
     * Puts a byte in the buffer at a particular position, converting, if necessary, to the buffer
     * type.
     *
     * @param index the index in the buffer.
     * @param value value to put in the buffer.
     */
    public abstract void putByte(int index, byte value);

    /**
     * The capacity (i.e. size) of the buffer.
     *
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     *
     * @return the size.
     */
    public abstract int capacity();

    /**
     * Are there voxels remaining in a buffer?
     *
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     *
     * @return true if there are voxels remaining in the buffer, false otherwise.
     */
    public abstract boolean hasRemaining();

    /**
     * Assigns a new position to the buffer.
     *
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     *
     * @param newPosition the offset to assign as position.
     */
    public abstract void position(int newPosition);

    /**
     * Is this buffer direct or non-direct?
     *
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     *
     * @return true iff the buffer is direct.
     */
    public abstract boolean isDirect();

    /**
     * THe underlying bytes that describe the voxels.
     *
     * @return the associated {@link ByteBuffer} with the voxels.
     */
    public abstract ByteBuffer underlyingBytes();

    @Override
    public String toString() {
        return HistogramFactory.createFrom(this).toString();
    }

    /**
     * Copies one particular intensity-value from another {@link VoxelBuffer} into this buffer.
     *
     * @param destinationIndex the index in the current buffer to write to.
     * @param source the buffer to copy the value from.
     * @param sourceIndex the index of the voxel in {@code source} to copy from.
     */
    public abstract void copyVoxelFrom(
            int destinationIndex, VoxelBuffer<T> source, int sourceIndex);
}
