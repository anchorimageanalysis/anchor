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

package org.anchoranalysis.image.object;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.factory.CreateFromConnectedComponentsFactory;
import org.anchoranalysis.image.object.intersecting.CountIntersectingVoxelsBinary;
import org.anchoranalysis.image.object.intersecting.DetermineWhetherIntersectingVoxelsBinary;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;

/**
 * An object expressed in voxels, bounded within overall space
 *
 * <p>A bounding-box defines a box within the overall space, and a raster-mask defines which voxels
 * inside the bounding-box belong to the object.
 *
 * <p>Each voxel in the raster-mask must be one of two states, an ON value and an OFF value
 *
 * <p>These voxels are MUTABLE.
 */
public class ObjectMask {

    private static final CreateFromConnectedComponentsFactory CONNECTED_COMPONENT_CREATOR =
            new CreateFromConnectedComponentsFactory(true);

    private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY = VoxelBoxFactory.getByte();

    private final BoundedVoxelBox<ByteBuffer> delegate;
    private final BinaryValues bv;
    private final BinaryValuesByte bvb;

    /**
     * Constructor - creates an object-mask assuming coordinates at the origin (i.e. corner is
     * 0,0,0)
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param voxelBox voxel-box
     */
    public ObjectMask(VoxelBox<ByteBuffer> voxelBox) {
        this(new BoundedVoxelBox<>(voxelBox));
    }

    /**
     * Constructor - creates an object-mask to matching a bounding-box but all pixels are OFF (0)
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param bbox bounding-box
     */
    public ObjectMask(BoundingBox bbox) {
        this(new BoundedVoxelBox<>(bbox, FACTORY));
    }

    /**
     * Constructor - creates an object-mask to matching a bounded-voxel box
     *
     * <p>The voxel-box is reused without duplication.
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param voxelBox
     */
    public ObjectMask(BoundedVoxelBox<ByteBuffer> voxelBox) {
        this(voxelBox, BinaryValues.getDefault());
    }

    public ObjectMask(BinaryVoxelBox<ByteBuffer> voxelBox) {
        this(new BoundedVoxelBox<>(voxelBox.getVoxelBox()), voxelBox.getBinaryValues());
    }

    public ObjectMask(BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox) {
        this(new BoundedVoxelBox<>(bbox, voxelBox));
    }

    public ObjectMask(BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues) {
        this(new BoundedVoxelBox<>(bbox, voxelBox), binaryValues);
    }

    public ObjectMask(BoundingBox bbox, BinaryVoxelBox<ByteBuffer> voxelBox) {
        this(new BoundedVoxelBox<>(bbox, voxelBox.getVoxelBox()), voxelBox.getBinaryValues());
    }

    public ObjectMask(BoundedVoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues) {
        delegate = voxelBox;
        bv = binaryValues;
        bvb = binaryValues.createByte();
    }

    public ObjectMask(
            BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox, BinaryValuesByte binaryValues) {
        delegate = new BoundedVoxelBox<>(bbox, voxelBox);
        bv = binaryValues.createInt();
        bvb = binaryValues;
    }

    /**
     * Copy constructor
     *
     * @param src to copy from
     */
    private ObjectMask(ObjectMask src) {
        this(new BoundedVoxelBox<>(src.delegate), src.bv, src.bvb);
    }

    private ObjectMask(
            BoundedVoxelBox<ByteBuffer> voxelBox,
            BinaryValues binaryValues,
            BinaryValuesByte binaryValuesByte) {
        delegate = voxelBox;
        bv = binaryValues;
        bvb = binaryValuesByte;
    }

    public ObjectMask duplicate() {
        return new ObjectMask(this);
    }

    /** The number of "ON" voxels on the mask */
    public int numberVoxelsOn() {
        return delegate.getVoxelBox().countEqual(bv.getOnInt());
    }

    /**
     * Replaces the voxels in the mask.
     *
     * <p>This is an IMMUTABLE operation, and a new mask is created.
     *
     * @param voxelBoxToAssign voxels to be assigned.
     * @return a new mask the replacement voxels but identical other properties.
     */
    public ObjectMask replaceVoxels(VoxelBox<ByteBuffer> voxelBoxToAssign) {
        return new ObjectMask(delegate.replaceVoxels(voxelBoxToAssign), bv);
    }

    public ObjectMask flattenZ() {
        return new ObjectMask(delegate.flattenZ(), bv);
    }

    public ObjectMask growToZ(int sz) {
        return new ObjectMask(delegate.growToZ(sz, FACTORY));
    }

    public ObjectMask growBuffer(Point3i neg, Point3i pos, Optional<Extent> clipRegion)
            throws OperationFailedException {
        return new ObjectMask(delegate.growBuffer(neg, pos, clipRegion, FACTORY));
    }

    public boolean equalsDeep(ObjectMask other) {
        if (!delegate.equalsDeep(other.delegate)) {
            return false;
        }
        if (!bv.equals(other.bv)) {
            return false;
        }
        return bvb.equals(other.bvb);
    }

    public int countIntersectingVoxels(ObjectMask other) {
        return new CountIntersectingVoxelsBinary(getBinaryValuesByte(), other.getBinaryValuesByte())
                .countIntersectingVoxels(delegate, other.delegate);
    }

    public boolean hasIntersectingVoxels(ObjectMask other) {
        return new DetermineWhetherIntersectingVoxelsBinary(
                        getBinaryValuesByte(), other.getBinaryValuesByte())
                .hasIntersectingVoxels(delegate, other.delegate);
    }

    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param factor scale-factor
     * @param interpolator interpolator
     * @return a scaled object-mask
     */
    public ObjectMask scale(ScaleFactor factor, Interpolator interpolator) {

        if ((bv.getOnInt() == 255 && bv.getOffInt() == 0)
                || (bv.getOnInt() == 0 && bv.getOffInt() == 255)) {

            BoundedVoxelBox<ByteBuffer> scaled = delegate.scale(factor, interpolator);

            // We should do a thresholding afterwards to make sure our values correspond to the two
            // binry values
            if (interpolator.isNewValuesPossible()) {

                // We threshold to make sure it's still binary
                int thresholdVal = (bv.getOnInt() + bv.getOffInt()) / 2;

                VoxelBoxThresholder.thresholdForLevel(
                        scaled.getVoxelBox(), thresholdVal, bv.createByte());
            }
            return new ObjectMask(scaled, bv);

        } else {
            throw new AnchorFriendlyRuntimeException(
                    "Operation not supported for these binary values");
        }
    }

    /** Calculates center-of-gravity across all axes */
    public Point3d centerOfGravity() {
        return CenterOfGravityCalculator.calcCenterOfGravity(this);
    }

    /**
     * Calculates center-of-gravity for one specific axis only
     *
     * @param axis the axis
     * @return a point on the specific axis that is the center-of-gravity.
     */
    public double centerOfGravity(AxisType axis) {
        return CenterOfGravityCalculator.calcCenterOfGravityForAxis(this, axis);
    }

    /**
     * Determines if an object-mask is connected.
     *
     * <p>TODO this is not particular efficient. We can avoid making the object-collection.
     *
     * @return
     * @throws OperationFailedException
     */
    public boolean checkIfConnected() throws OperationFailedException {

        try {
            ObjectCollection objects =
                    CONNECTED_COMPONENT_CREATOR.createConnectedComponents(
                            this.binaryVoxelBox().duplicate());
            return objects.size() <= 1;
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public boolean numPixelsLessThan(int num) {

        Extent e = delegate.getVoxelBox().extent();

        int cnt = 0;

        for (int z = 0; z < e.getZ(); z++) {
            ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();

            while (bb.hasRemaining()) {
                byte b = bb.get();

                if (b == bvb.getOnByte()) {
                    cnt++;

                    if (cnt >= num) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean hasPixelsGreaterThan(int num) {

        Extent e = delegate.getVoxelBox().extent();

        int cnt = 0;

        for (int z = 0; z < e.getZ(); z++) {
            ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();

            while (bb.hasRemaining()) {
                byte b = bb.get();

                if (b == bvb.getOnByte()) {
                    cnt++;

                    if (cnt > num) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Intersects this object-mask with another
     *
     * @param other the other object-mask to intersect with
     * @param dimensions dimensions to constrain any intersection
     * @return a new mask of the intersection iff it exists
     */
    public Optional<ObjectMask> intersect(ObjectMask other, ImageDimensions dimensions) {

        // we combine the two masks
        Optional<BoundingBox> bboxIntersect =
                getBoundingBox()
                        .intersection()
                        .withInside(other.getBoundingBox(), dimensions.getExtent());

        if (!bboxIntersect.isPresent()) {
            return Optional.empty();
        }

        // We calculate a bounding box, which we write into in the omDest

        BinaryValues bvOut = BinaryValues.getDefault();

        // We initially set all pixels to ON
        VoxelBox<ByteBuffer> vbMaskOut = FACTORY.create(bboxIntersect.get().extent());
        vbMaskOut.setAllPixelsTo(bvOut.getOnInt());

        // Then we set any pixels NOT on either mask to OFF..... leaving only the intersecting
        // pixels as ON in the output buffer
        setVoxelsTwoMasks(
                vbMaskOut,
                getVoxelBox(),
                other.getVoxelBox(),
                bboxIntersect.get().relPosToBox(getBoundingBox()),
                bboxIntersect.get().relPosToBox(other.getBoundingBox()),
                bvOut.getOffInt(),
                this.getBinaryValuesByte().getOffByte(),
                other.getBinaryValuesByte().getOffByte());

        ObjectMask object =
                new ObjectMask(bboxIntersect.get(), new BinaryVoxelBoxByte(vbMaskOut, bvOut));

        // If there no pixels left that haven't been set, then the intersection mask is zero
        return OptionalUtilities.createFromFlag(object.hasPixelsGreaterThan(0), object);
    }

    public boolean contains(Point3i point) {

        if (!delegate.getBoundingBox().contains().point(point)) {
            return false;
        }

        int xRel = point.getX() - delegate.getBoundingBox().cornerMin().getX();
        int yRel = point.getY() - delegate.getBoundingBox().cornerMin().getY();
        int zRel = point.getZ() - delegate.getBoundingBox().cornerMin().getZ();

        return delegate.getVoxelBox().getVoxel(xRel, yRel, zRel) == bv.getOnInt();
    }

    public boolean containsIgnoreZ(Point3i point) {

        if (!delegate.getBoundingBox().contains().pointIgnoreZ(point)) {
            return false;
        }

        int xRel = point.getX() - delegate.getBoundingBox().cornerMin().getX();
        int yRel = point.getY() - delegate.getBoundingBox().cornerMin().getY();

        Extent e = delegate.getBoundingBox().extent();
        for (int z = 0; z < e.getZ(); z++) {
            if (delegate.getVoxelBox().getVoxel(xRel, yRel, z) == bv.getOnInt()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A maximum-intensity projection (flattens in z dimension)
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @return a new object-mask flattened in Z dimension.
     */
    public ObjectMask maxIntensityProjection() {
        return new ObjectMask(delegate.maxIntensityProjection());
    }

    public BoundingBox getBoundingBox() {
        return delegate.getBoundingBox();
    }

    public BinaryVoxelBox<ByteBuffer> binaryVoxelBox() {
        return new BinaryVoxelBoxByte(delegate.getVoxelBox(), bv);
    }

    public VoxelBox<ByteBuffer> getVoxelBox() {
        return delegate.getVoxelBox();
    }

    public BinaryValues getBinaryValues() {
        return bv;
    }

    public BinaryValuesByte getBinaryValuesByte() {
        return bvb;
    }

    public BoundedVoxelBox<ByteBuffer> getVoxelBoxBounded() {
        return delegate;
    }

    // If keepZ is true the slice keeps its z coordinate, otherwise its set to 0
    public ObjectMask extractSlice(int z, boolean keepZ) {
        return new ObjectMask(delegate.extractSlice(z, keepZ), this.bv);
    }

    /**
     * Creates an object-mask with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers.</p.
     *
     * @param zMin minimum z-slice index, inclusive.
     * @param zMax maximum z-slice index, inclusive.
     * @return a newly created object-mask for the slice-range requested.
     * @throws CreateException
     */
    public ObjectMask regionZ(int zMin, int zMax) throws CreateException {
        return new ObjectMask(delegate.regionZ(zMin, zMax, FACTORY), this.bv);
    }

    /**
     * A (sub-)region of the mask.
     *
     * <p>The region may some smaller portion of the voxel-box, or the voxel-box as a whole.</p>
     *
     * <p>It should <b>never</b> be larger than the voxel-box.</p>
     *
     * <p>See {@link org.anchoranalysis.image.voxel.box.VoxelBox::region) for more details.</p>
     *
     * @param bbox bounding-box in absolute coordinates.
     * @param reuseIfPossible if TRUE the existing mask will be reused if possible, otherwise a new mask is always created.
     * @return a mask corresponding to the requested region, either newly-created or reused
     * @throws CreateException
     */
    public ObjectMask region(BoundingBox bbox, boolean reuseIfPossible) throws CreateException {
        return new ObjectMask(delegate.region(bbox, reuseIfPossible), this.bv);
    }

    /**
     * Creates a mask covering the a bounding-box (that is required to intersect at least partially)
     *
     * <p>The region outputted will have the same size and coordinates as the bounding-box NOT the
     * existing mask.
     *
     * <p>It will contain the correct mask-values for the intersecting region, and OFF values for
     * the rest.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link region}.</p.
     *
     * @param bbox bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current mask bounds.
     * @return a newly created mask containing partially some parts of the existing mask as well as
     *     OFF voxels for any other region.
     * @throws CreateException if the boxes do not intersect
     */
    public ObjectMask regionIntersecting(BoundingBox bbox) throws CreateException {
        return new ObjectMask(delegate.regionIntersecting(bbox, bv.getOffInt()), this.bv);
    }

    /**
     * Finds any arbitrary "ON" voxel on the mask.
     *
     * <p>First it tries the center-of-gravity voxel, and if that's not on, it iterates through the
     * box until it finds an "ON" voxel.
     *
     * <p>This is a DETERMINISTIC operation, so one can rely on the same voxel being found for a
     * given mask.
     *
     * @return the location (in absolute coordinates) of an arbitrary "ON" voxel on the mask, if it
     *     exists.
     */
    public Optional<Point3i> findArbitraryOnVoxel() {

        // First we try the center-of-gravity
        Point3i point = getBoundingBox().centerOfGravity();

        if (contains(point)) {
            return Optional.of(point);
        }

        // Second, if needed, we iterate until we find any "ON" value
        return IterateVoxels.findFirstPointOnMask(this);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an almost <i>immutable</i> operation: the existing voxel-buffers are reused in the new
     * object.
     *
     * @param mapFunc map function to perform mapping of bounding-box
     * @return a new object-mask with the updated bounding box (but unchanged voxel-box)
     */
    public ObjectMask mapBoundingBoxPreserveExtent(UnaryOperator<BoundingBox> mapFunc) {
        return mapBoundingBoxPreserveExtent(mapFunc.apply(delegate.getBoundingBox()));
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an almost IMMUTABLE operation: the existing voxel-buffers are reused in the new
     * object.
     *
     * @param boundingBoxToAssign bounding-box to assign
     * @return a new object-mask with the updated bounding box (but unchanged voxel-box)
     */
    public ObjectMask mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {
        return new ObjectMask(delegate.mapBoundingBoxPreserveExtent(boundingBoxToAssign), bv, bvb);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent is expected to change
     * in value)
     *
     * <p>This is a almost <i>immutable</i> operation, and NEW voxel-buffers are usually created for the
     * new object, but not if the bounding-box or its extent need no change.
     *
     * <p>Precondition: the new bounding-box's extent must be greater than or equal to the existing
     * extent in all dimensions.
     *
     * @param boxToAssign bounding-box to assign
     * @param function to perform mapping of bounding-box
     * @return a new object-mask with the updated bounding box (and changed voxel-box)
     */
    public ObjectMask mapBoundingBoxChangeExtent(BoundingBox boxToAssign) {

        Preconditions.checkArgument(
                !delegate.extent().anyDimensionIsLargerThan(boxToAssign.extent()));

        if (delegate.getBoundingBox().equals(boxToAssign)) {
            // Nothing to do, bounding-boxes are equal, early exit
            return this;
        }

        if (delegate.getBoundingBox().equals(boxToAssign)) {
            // Nothing to do, extents are equal, take the easier path of mapping only the bounding
            // box
            return mapBoundingBoxPreserveExtent(boxToAssign);
        }

        VoxelBox<ByteBuffer> voxelBoxLarge = VoxelBoxFactory.getByte().create(boxToAssign.extent());

        BoundingBox bbLocal = delegate.getBoundingBox().relPosToBox(boxToAssign);

        voxelBoxLarge.setPixelsCheckMask(
                new ObjectMask(bbLocal, binaryVoxelBox()), bvb.getOnByte());

        return new ObjectMask(boxToAssign, voxelBoxLarge, bvb);
    }

    /**
     * Creates a new obj-mask with coordinates changed to be relative to another box.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param bbox box used as a reference point, against which new relative coordinates are
     *     calculated.
     * @return a newly created object-mask with updated coordinates.
     */
    public ObjectMask relMaskTo(BoundingBox bbox) {
        Point3i point = delegate.getBoundingBox().relPosTo(bbox);

        return new ObjectMask(new BoundingBox(point, delegate.extent()), delegate.getVoxelBox());
    }

    /** Sets a point (expressed in global coordinates) to be ON */
    public void setOn(Point3i pointGlobal) {
        setVoxel(pointGlobal, bv.getOnInt());
    }

    /** Sets a point (expressed in global coordinates) to be OFF */
    public void setOff(Point3i pointGlobal) {
        setVoxel(pointGlobal, bv.getOffInt());
    }

    /**
     * A string representation of the object-mask showing:
     *
     * <ol>
     *   <li>the center-of-gravity
     *   <li>the number of "ON" voxels on the mask
     * </ol>
     */
    @Override
    public String toString() {
        return String.format(
                "Obj%s(cog=%s,numPixels=%d)",
                super.hashCode(), centerOfGravity().toString(), numberVoxelsOn());
    }
    
    private void setVoxel(Point3i pointGlobal, int val) {
        Point3i cornerMin = delegate.getBoundingBox().cornerMin();
        delegate.getVoxelBox()
                .setVoxel(
                        pointGlobal.getX() - cornerMin.getX(),
                        pointGlobal.getX() - cornerMin.getY(),
                        pointGlobal.getX() - cornerMin.getZ(),
                        val);
    }
    
    /**
     * Sets voxels in a voxel box that match either of two masks
     *
     * @param vbMaskOut voxel-box to write to
     * @param voxelBox1 voxel-box for first mask
     * @param voxelBox2 voxel-box for second mask
     * @param bboxSrcMask bounding-box for first mask
     * @param bboxOthrMask bounding-box for second mask
     * @param value value to write
     * @param maskMatchValue mask-value to match against for first mask
     * @param maskMatchValue mask-value to match against for second mask
     * @return the total number of pixels written
     */
    private static int setVoxelsTwoMasks( // NOSONAR
            VoxelBox<ByteBuffer> vbMaskOut,
            VoxelBox<ByteBuffer> voxelBox1,
            VoxelBox<ByteBuffer> voxelBox2,
            BoundingBox bboxSrcMask,
            BoundingBox bboxOthrMask,
            int value,
            byte maskMatchValue1,
            byte maskMatchValue2) {
        BoundingBox allOut = new BoundingBox(vbMaskOut.extent());
        int cntSetFirst =
                vbMaskOut.setPixelsCheckMask(
                        allOut, voxelBox1, bboxSrcMask, value, maskMatchValue1);
        int cntSetSecond =
                vbMaskOut.setPixelsCheckMask(
                        allOut, voxelBox2, bboxOthrMask, value, maskMatchValue2);
        return cntSetFirst + cntSetSecond;
    }
}
