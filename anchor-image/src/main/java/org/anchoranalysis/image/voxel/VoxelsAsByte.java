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

package org.anchoranalysis.image.voxel;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmeticFactory;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssignerFactory;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracterFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.pixelsforslice.PixelsForSlice;

public final class VoxelsAsByte extends Voxels<UnsignedByteBuffer> {

    public VoxelsAsByte(PixelsForSlice<UnsignedByteBuffer> slices) {
        super(slices, VoxelsFactory.getByte(), createArithmetic(slices));
    }

    @Override
    protected boolean areBufferValuesEqual(UnsignedByteBuffer buffer1, UnsignedByteBuffer buffer2) {
        return buffer1.getByte() == buffer2.getByte();
    }

    @Override
    public VoxelsAssigner assignValue(int valueToAssign) {
        return VoxelsAssignerFactory.createByte(this, valueToAssign);
    }

    @Override
    public VoxelsExtracter<UnsignedByteBuffer> extract() {
        return VoxelsExtracterFactory.createByte(this);
    }

    private static VoxelsArithmetic createArithmetic(PixelsForSlice<UnsignedByteBuffer> slices) {
        return VoxelsArithmeticFactory.createByte(slices.extent(), slices::sliceBuffer);
    }

    @Override
    public boolean hasRemaining(UnsignedByteBuffer buffer) {
        return buffer.hasRemaining();
    }

    @Override
    public void setBufferPosition(UnsignedByteBuffer buffer, int offset) {
        buffer.position(offset);
    }
}
