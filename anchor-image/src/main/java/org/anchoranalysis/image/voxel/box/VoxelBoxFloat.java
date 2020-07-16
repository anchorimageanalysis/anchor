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

package org.anchoranalysis.image.voxel.box;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferFloat;

public final class VoxelBoxFloat extends VoxelBox<FloatBuffer> {

    public VoxelBoxFloat(PixelsForPlane<FloatBuffer> pixelsForPlane) {
        super(pixelsForPlane, VoxelBoxFactory.getFloat());
    }

    @Override
    public int ceilOfMaxPixel() {

        float max = 0;
        boolean first = true;

        for (int z = 0; z < getPlaneAccess().extent().getZ(); z++) {

            FloatBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (pixels.hasRemaining()) {

                float val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return (int) Math.ceil(max);
    }

    @Override
    public void copyItem(
            FloatBuffer srcBuffer, int srcIndex, FloatBuffer destBuffer, int destIndex) {
        destBuffer.put(destIndex, srcBuffer.get(srcIndex));
    }

    @Override
    public boolean isGreaterThan(FloatBuffer buffer, int operand) {
        return buffer.get() > operand;
    }

    @Override
    public boolean isEqualTo(FloatBuffer buffer, int operand) {
        return buffer.get() == operand;
    }

    @Override
    public void setAllPixelsTo(int val) {

        float valFloat = (float) val;

        for (int z = 0; z < extent().getZ(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                buffer.put(valFloat);
            }
        }
    }

    @Override
    public void setPixelsTo(BoundingBox bbox, int val) {

        float valFloat = (float) val;

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        Extent e = extent();

        for (int z = cornerMin.getZ(); z <= cornerMax.getZ(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            for (int y = cornerMin.getY(); y <= cornerMax.getY(); y++) {
                for (int x = cornerMin.getX(); x <= cornerMax.getX(); x++) {
                    int offset = e.offset(x, y);
                    buffer.put(offset, valFloat);
                }
            }
        }
    }

    @Override
    public void multiplyBy(double val) {

        if (val == 1) {
            return;
        }

        for (int z = 0; z < extent().getZ(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                float mult = (float) (buffer.get() * val);
                buffer.put(buffer.position() - 1, mult);
            }
        }
    }

    @Override
    public VoxelBox<FloatBuffer> maxIntensityProj() {
        MaxIntensityBufferFloat mi = new MaxIntensityBufferFloat(extent());

        for (int z = 0; z < extent().getZ(); z++) {
            mi.projectSlice(getPlaneAccess().getPixelsForPlane(z).buffer());
        }

        return mi.getProjection();
    }

    @Override
    public void setVoxel(int x, int y, int z, int val) {
        FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put(getPlaneAccess().extent().offset(x, y), (float) val);
    }

    /** Casts a float to an int */
    @Override
    public int getVoxel(int x, int y, int z) {
        FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return (int) buffer.get(getPlaneAccess().extent().offset(x, y));
    }

    @Override
    public void addPixelsCheckMask(ObjectMask mask, int value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public void scalePixelsCheckMask(ObjectMask mask, double value) {
        throw new IllegalArgumentException("Currently unsupported method");
    }

    @Override
    public boolean isEqualTo(FloatBuffer buffer1, FloatBuffer buffer2) {
        return buffer1.get() == buffer2.get();
    }

    @Override
    public void subtractFrom(int val) {

        for (int z = 0; z < extent().getZ(); z++) {

            FloatBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();

            while (buffer.hasRemaining()) {
                float newVal = val - buffer.get();
                buffer.put(buffer.position() - 1, newVal);
            }
        }
    }

    @Override
    public void max(VoxelBox<FloatBuffer> other) throws OperationFailedException {
        throw new OperationFailedException("unsupported operation");
    }

    @Override
    public VoxelBox<FloatBuffer> meanIntensityProj() {
        throw new UnsupportedOperationException();
    }
}
