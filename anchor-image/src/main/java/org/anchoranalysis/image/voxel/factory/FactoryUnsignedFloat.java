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

package org.anchoranalysis.image.voxel.factory;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsAsFloat;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.sliceindex.FromFloat;
import org.anchoranalysis.image.voxel.sliceindex.SliceBufferIndex;

final class FactoryUnsignedFloat implements VoxelsFactoryTypeBound<FloatBuffer> {

    private static final VoxelDataType DATA_TYPE = FloatVoxelType.INSTANCE;

    @Override
    public Voxels<FloatBuffer> create(SliceBufferIndex<FloatBuffer> pixelsForPlane) {
        return new VoxelsAsFloat(pixelsForPlane);
    }

    @Override
    public Voxels<FloatBuffer> createInitialized(Extent extent) {
        return new VoxelsAsFloat(FromFloat.createInitialized(extent));
    }

    @Override
    public Voxels<FloatBuffer> createUninitialized(Extent extent) {
        return new VoxelsAsFloat(FromFloat.createUninitialized(extent));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
