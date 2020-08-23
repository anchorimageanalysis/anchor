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

package org.anchoranalysis.image.voxel.iterator;

import java.nio.Buffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.Voxels;
import lombok.RequiredArgsConstructor;

/**
 * Exposes a {@link ProcessVoxel} as a {@link ProcessVoxelSliceBuffer} by retrieving a buffer
 * from voxels for each z-slice.
 *
 * <p>Note that {@link #notifyChangeSlice} <b>need not</b> be be called for all slices (perhaps only a subset),
 * but {@link #process} <b>must</b> be called for ALL voxels on a given slice.
 *
 * @author Owen Feehan
 * @param <T> buffer-type for slice
 */
@RequiredArgsConstructor
public final class RetrieveBufferForSlice<T extends Buffer> implements ProcessVoxel {

    // START REQUIRED ARGUMENTS
    private final Voxels<T> voxels;
    private final ProcessVoxelSliceBuffer<T> processor;
    // END REQUIRED ARGUMENTS

    private T bufferSlice;
    /** A 2D offset within the current slice */
    private int offsetWithinSlice;

    @Override
    public void notifyChangeSlice(int z) {
        processor.notifyChangeSlice(z);
        offsetWithinSlice = 0;
        this.bufferSlice = voxels.sliceBuffer(z);
    }

    @Override
    public void process(Point3i point) {
        processor.process(point, bufferSlice, offsetWithinSlice++);
    }
}
