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

package org.anchoranalysis.image.voxel.datatype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The type of data that a single voxel represents in an image or related buffer.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@Accessors(fluent = true)
public abstract class VoxelDataType {

    /**
     * A placeholder value for {@code maxValue} or {@code minValue} if the true maximum or minimum
     * value cannot be expressed as a long.
     */
    public static final long VALUE_NOT_COMPATIBLE = -1;

    /** The number of bits required to represent a voxel. */
    @Getter private int bitDepth;

    /** A string to uniquely and compactly describe this type. */
    private String typeIdentifier;

    /**
     * The maximum value this type can represent, or {@link #VALUE_NOT_COMPATIBLE} if it cannot be
     * represented in a long.
     */
    @Getter private long maxValue;

    /**
     * The minimum value this type can represent, or {@link #VALUE_NOT_COMPATIBLE} if it cannot be
     * represented in a long.
     */
    @Getter private long minValue;

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof VoxelDataType)) {
            return false;
        }

        VoxelDataType otherCasted = (VoxelDataType) obj;

        if (isInteger() != otherCasted.isInteger()) {
            return false;
        }

        if (isUnsigned() != otherCasted.isUnsigned()) {
            return false;
        }

        return bitDepth() == otherCasted.bitDepth();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(isInteger())
                .append(isUnsigned())
                .append(bitDepth())
                .toHashCode();
    }

    /**
     * Whether the data-type represents an integer or floating-point?
     *
     * @return true if the data-type represents integers only, false if it is float-point.
     */
    public abstract boolean isInteger();

    /**
     * Whether the data-type is unsigned?
     *
     * @return true if the data-type is unsigned, false if it is signed.
     */
    public abstract boolean isUnsigned();

    @Override
    public final String toString() {
        return typeIdentifier;
    }

    /**
     * The number of bytes needed to represent this data-type.
     *
     * @return the number of bytes.
     */
    public int numberBytes() {
        return bitDepth / 8;
    }
}
