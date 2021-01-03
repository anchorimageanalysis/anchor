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

package org.anchoranalysis.image.voxel.kernel.count;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.Kernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

public abstract class CountKernel extends Kernel {

    private LocalSlices inSlices;

    private Extent extent;

    private boolean outsideAtThreshold = false;
    private boolean ignoreAtThreshold = false;

    // Constructor
    protected CountKernel() {
        super(3);
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        this.extent = in.extent();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }

    protected abstract boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent);

    public int countAtPosition(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = inSlices.getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = inSlices.getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = inSlices.getLocal(+1);

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOff(buffer.getRaw(index))) {
            return 0;
        }

        int count = 0;

        // We walk up and down in x
        x--;
        index--;
        if (x >= 0) {
            if (binaryValues.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, -1, 0, 0, extent)) {
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold) {
                count++;
            }
        }

        x += 2;
        index += 2;
        if (x < extent.x()) {
            if (binaryValues.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, +1, 0, 0, extent)) {
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold) {
                count++;
            }
        }
        index--;

        // We walk up and down in y
        y--;
        index -= xLength;
        if (y >= 0) {
            if (binaryValues.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, 0, -1, 0, extent)) {
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold) {
                count++;
            }
        }

        y += 2;
        index += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOff(buffer.getRaw(index))
                    && isNeighborVoxelAccepted(point, 0, +1, 0, extent)) {
                count++;
            }
        } else {
            if (!ignoreAtThreshold
                    && !outsideAtThreshold) {
                count++;
            }
        }
        index -= xLength;

        if (params.isUseZ()) {
            if (bufferZLess1.isPresent()) {
                if (binaryValues.isOff(bufferZLess1.get().getRaw(index))
                        && isNeighborVoxelAccepted(point, 0, 0, -1, extent)) {
                    count++;
                }
            } else {
                if (!ignoreAtThreshold
                        && !outsideAtThreshold) {
                    count++;
                }
            }

            if (bufferZPlus1.isPresent()) {
                if (binaryValues.isOff(bufferZPlus1.get().getRaw(index))
                        && isNeighborVoxelAccepted(point, 0, 0, +1, extent)) {
                    count++;
                }
            } else {
                if (!ignoreAtThreshold
                        && !outsideAtThreshold) {
                    count++;
                }
            }
        }
        return count;
    }
}
