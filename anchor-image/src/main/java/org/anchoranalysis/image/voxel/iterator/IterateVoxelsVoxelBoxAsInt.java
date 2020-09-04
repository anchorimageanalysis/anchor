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

import com.google.common.base.Preconditions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.ExtentMatchHelper;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * Iterates over {@link Voxels} using the (slower) {@code getInt} and {@code putInt} methods of
 * {@link VoxelBuffer} rather than directly accessing the buffers
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsVoxelBoxAsInt {

    /**
     * Changes each voxel reading and writing the buffer as an {@code int}.
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param voxels the voxels, each of which is transformed by {@code operator}
     * @param operator determines a corresponding <i>output</i> value for each <i>input</i> voxel
     */
    public static void changeEachPoint(Voxels<?> voxels, IntUnaryOperator operator) {
        voxels.slices()
                .iterateOverSlicesAndOffsets(
                        (buffer, offset) -> {
                            int value = buffer.getInt(offset);
                            buffer.putInt(offset, operator.applyAsInt(value));
                        });
    }

    /**
     * Assigns a value to any voxel matches a predicate, reading and writing the buffer as an {@code
     * int}.
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param voxels the voxels, each of which is tested by {@code predicate} and maybe assigned a
     *     new value
     * @param predicate determines if a voxel-value should be assigned or not
     * @param valueToAssign the value to assign
     */
    public static void assignEachMatchingPoint(
            Voxels<?> voxels, IntPredicate predicate, int valueToAssign) {

        voxels.slices()
                .iterateOverSlicesAndOffsets(
                        (buffer, offset) -> {
                            if (predicate.test(buffer.getInt(offset))) {
                                buffer.putInt(offset, valueToAssign);
                            }
                        });
    }

    /**
     * Finds the maximum-value (as an int) among voxels
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param voxels the voxels
     * @return whatever the maximum value is
     */
    public static int findMaxValue(Voxels<?> voxels) {
        int max = 0;
        boolean first = true;

        int sizeXY = voxels.extent().volumeXY();
        for (int z = 0; z < voxels.extent().z(); z++) {

            VoxelBuffer<?> pixels = voxels.slice(z);

            for (int offset = 0; offset < sizeXY; offset++) {

                int val = pixels.getInt(offset);
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    /**
     * Do all points on an object-mask match a predicate?
     *
     * <p>The voxel-value of the current buffer is passed to the predicate.
     *
     * <p>As soon as one voxel fails the predicate (i.e. the predicate returns false), the function
     * immediately returns false.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to the predicate
     * @param object the object-mask (global coordinates) to restrict which voxels are tested with
     *     the predicate
     * @return true if the predicate returns true for all points on the object-mask, false otherwise
     */
    public static <T extends Buffer> boolean allPointsMatchPredicate(
            Voxels<T> voxels, ObjectMask object, IntPredicate predicate) {

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMaxExclusive();

        byte maskMatchValue = object.binaryValuesByte().getOnByte();

        Extent extentVoxels = voxels.extent();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            VoxelBuffer<T> buffer = voxels.slice(point.z());
            ByteBuffer sliceMask = object.sliceBufferGlobal(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {
                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    if (sliceMask.get() == maskMatchValue) {

                        int offset = extentVoxels.offsetSlice(point);

                        int voxelIntensity = buffer.getInt(offset);

                        if (!predicate.test(voxelIntensity)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Calls each point on a bounding-box (optionally subregion thereof) of an object-mask
     *
     * <p>{@code boxVoxels} and {@code boxRelativeToObject} must both have the same extent.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param object the object-mask (global coordinates)
     * @param process processes each point that fulfills the conditions
     */
    public static <T> void callEachPoint(
            Voxels<T> voxels, ObjectMask object, ProcessVoxelSlice<T> process) {
        callEachPoint(voxels, object, Optional.empty(), process);
    }

    /**
     * Calls each point on a bounding-box (optionally subregion thereof) of an object-mask
     *
     * <p>{@code boxVoxels} and {@code boxRelativeToObject} must both have the same extent.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param object the object-mask (global coordinates)
     * @param restrictTo optional sub-region of object-mask (global coordinates)
     * @param process processes each point that fulfills the conditions
     */
    public static <T> void callEachPoint(
            Voxels<T> voxels,
            ObjectMask object,
            Optional<BoundingBox> restrictTo,
            ProcessVoxelSlice<T> process) {
        BoundingBox boxVoxels = restrictTo.orElseGet(object::boundingBox);

        Optional<BoundingBox> restrictToIntersection =
                OptionalUtilities.flatMap(
                        restrictTo, box -> box.intersection().with(object.boundingBox()));

        if (restrictTo.isPresent() && !restrictToIntersection.isPresent()) {
            // There's no intersection between the object-mask and restrictTo, so there's nothing to
            // be done. Exit early
            return;
        }

        // What part of the object-mask is iterated over. If not restricted, all of the mask is
        // iterated over */
        BoundingBox iterateBox =
                restrictToIntersection
                        .map(box -> box.relativePositionToBox(object.boundingBox()))
                        .orElseGet(boxVoxels::shiftToOrigin);

        callEachPoint(voxels, boxVoxels, object, iterateBox, process);
    }

    /**
     * Calls each point on a bounding-box subregion of an object-mask
     *
     * <p>{@code boxVoxels} and {@code boxRelativeToObject} must both have the same extent.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param boxVoxels bounding-box in {@code voxels} to iterate over
     * @param object the object-mask (global coordinates)
     * @param boxRelativeToObject bounding-box expressed <i>relative</i> to the object-mask's
     *     bounding-box
     * @param process processes each point that fulfills the conditions
     */
    private static <T> void callEachPoint(
            Voxels<T> voxels,
            BoundingBox boxVoxels,
            ObjectMask object,
            BoundingBox boxRelativeToObject,
            ProcessVoxelSlice<T> process) {

        ExtentMatchHelper.checkExtentMatch(boxVoxels, boxRelativeToObject);

        ReadableTuple3i cornerMin = boxVoxels.cornerMin();
        ReadableTuple3i cornerMax = boxVoxels.calculateCornerMaxExclusive();

        // Adding this to the voxels (global) coordinate gives a local coordinate for the
        // object-mask
        ReadableTuple3i maskShift =
                Point3i.immutableSubtract(boxRelativeToObject.cornerMin(), cornerMin);

        byte maskMatchValue = object.binaryValuesByte().getOnByte();

        Extent extentVoxels = voxels.extent();
        Extent extentObject = object.extent();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            VoxelBuffer<T> buffer = voxels.slice(point.z());
            ByteBuffer sliceMask = object.sliceBufferLocal(point.z() + maskShift.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {
                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    int indexMask =
                            extentObject.offset(
                                    point.x() + maskShift.x(), point.y() + maskShift.y());

                    if (sliceMask.get(indexMask) == maskMatchValue) {

                        int offset = extentVoxels.offsetSlice(point);
                        process.process(buffer, offset);
                    }
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated buffers for each slice
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     *
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second</b> buffer
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <S, T> void callEachPointTwo(
            Voxels<S> voxels1, Voxels<T> voxels2, ProcessVoxelSliceTwo<S, T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));

        int volumeXY = voxels1.extent().volumeXY();

        voxels1.extent()
                .iterateOverZ(
                        z -> {
                            VoxelBuffer<S> buffer1 = voxels1.slice(z);
                            VoxelBuffer<T> buffer2 = voxels2.slice(z);

                            for (int offset = 0; offset < volumeXY; offset++) {
                                process.process(buffer1, buffer2, offset);
                            }
                        });
    }
}
