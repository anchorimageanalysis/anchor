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

package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Applies a kernel to a Voxel Box
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ApplyKernel {

    private static final VoxelsFactoryTypeBound<ByteBuffer> FACTORY = VoxelsFactory.getByte();

    public static Voxels<ByteBuffer> apply(BinaryKernel kernel, Voxels<ByteBuffer> in) {
        return apply(kernel, in, BinaryValuesByte.getDefault());
    }

    // 3 pixel diameter kernel
    public static Voxels<ByteBuffer> apply(
            BinaryKernel kernel, Voxels<ByteBuffer> in, BinaryValuesByte outBinary) {

        Voxels<ByteBuffer> out = FACTORY.createInitialized(in.extent());

        int localSlicesSize = 3;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            ByteBuffer outArr = out.slice(point.z()).buffer();

            int ind = 0;

            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    if (kernel.accptPos(ind, point)) {
                        outArr.put(ind, outBinary.getOnByte());
                    } else {
                        outArr.put(ind, outBinary.getOffByte());
                    }

                    ind++;
                }
            }
        }

        return out;
    }

    /**
     * Applies the kernel to voxels and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(CountKernel kernel, Voxels<ByteBuffer> voxels)
            throws OperationFailedException {
        return applyForCount(kernel, voxels, new BoundingBox(voxels.extent()));
    }

    /**
     * Applies the kernel to voxels and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param bbox a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be containted within voxels.
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(CountKernel kernel, Voxels<ByteBuffer> voxels, BoundingBox bbox)
            throws OperationFailedException {

        if (!voxels.extent().contains(bbox)) {
            throw new OperationFailedException(
                    String.format(
                            "BBox (%s) must be contained within extent (%s)", bbox, voxels.extent()));
        }

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = voxels.extent();

        kernel.init(voxels);

        ReadableTuple3i pointMax = bbox.calcCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(bbox.cornerMin().z());
                point.z() <= pointMax.z();
                point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, voxels);
            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(bbox.cornerMin().y());
                    point.y() <= pointMax.y();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().x());
                        point.x() <= pointMax.x();
                        point.incrementX()) {

                    int ind = extent.offset(point.x(), point.y());
                    cnt += kernel.countAtPos(ind, point);
                }
            }
        }

        return cnt;
    }

    /**
     * Applies the kernel to voxels until a positive value is returned, then exits with TRUE
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param bbox a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @return TRUE if a positive-value is encountered, 0 if it never is encountered
     * @throws OperationFailedException
     */
    public static boolean applyUntilPositive(
            CountKernel kernel, Voxels<ByteBuffer> voxels, BoundingBox bbox)
            throws OperationFailedException {

        if (!voxels.extent().contains(bbox)) {
            throw new OperationFailedException(
                    String.format(
                            "Bounding-box (%s) must be contained within extent (%s)", bbox, voxels.extent()));
        }

        int localSlicesSize = 3;

        Extent extent = voxels.extent();

        kernel.init(voxels);

        ReadableTuple3i pointMax = bbox.calcCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(bbox.cornerMin().z());
                point.z() <= pointMax.z();
                point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, voxels);
            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(bbox.cornerMin().y());
                    point.y() <= pointMax.y();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().x());
                        point.x() <= pointMax.x();
                        point.incrementX()) {

                    int ind = extent.offsetSlice(point);
                    if (kernel.countAtPos(ind, point) > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int applyForCount(BinaryKernel kernel, Voxels<ByteBuffer> in) {

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.z());

            int ind = 0;

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    if (kernel.accptPos(ind, point)) {
                        cnt++;
                    }

                    ind++;
                }
            }
        }

        return cnt;
    }

    public static int applyForCountOnMask(
            BinaryKernel kernel, Voxels<ByteBuffer> in, ObjectMask object) {

        int localSlicesSize = 3;

        int cnt = 0;

        BoundingBox bbox = object.boundingBox();
        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();

        Extent extent = in.extent();

        kernel.init(in);

        BinaryValuesByte bvb = object.binaryValues().createByte();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.z());

            int ind = 0;

            ByteBuffer bufMask =
                    object.voxels()
                            .slice(point.z() - cornerMin.z())
                            .buffer();

            for (point.setY(cornerMin.y());
                    point.y() <= cornerMax.y();
                    point.incrementY()) {
                for (point.setX(cornerMin.x());
                        point.x() <= cornerMax.x();
                        point.incrementX()) {

                    int indKernel = extent.offset(point.x(), point.y());

                    if (bufMask.get(ind) == bvb.getOnByte() && kernel.accptPos(indKernel, point)) {
                        cnt++;
                    }

                    ind++;
                }
            }
        }

        return cnt;
    }
}
