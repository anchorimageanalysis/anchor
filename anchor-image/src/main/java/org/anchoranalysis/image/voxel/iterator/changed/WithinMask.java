/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Processes only neighboring voxels that lie on a mask.
 *
 * @author Owen Feehan
 * @param <T> result-type that can be collected after processing
 */
final class WithinMask<T> implements ProcessVoxelNeighbor<T> {

    private final ProcessChangedPointAbsoluteMasked<T> delegate;
    private final ObjectMask object;
    private final Extent extent;
    private final ReadableTuple3i cornerMin;

    private Point3i point;
    private Point3i relativeToCorner;

    // Current ByteBuffer for the object mask
    private ByteBuffer bbOM;
    private byte maskOffVal;

    private int maskOffsetXYAtPoint;

    public WithinMask(ProcessChangedPointAbsoluteMasked<T> process, ObjectMask object) {
        this.delegate = process;
        this.object = object;
        this.maskOffVal = object.getBinaryValuesByte().getOffByte();
        this.extent = object.getVoxelBox().extent();
        this.cornerMin = object.getBoundingBox().cornerMin();
    }

    @Override
    public void initSource(Point3i point, int sourceVal, int sourceOffsetXY) {
        this.point = point;

        updateRel(point);
        maskOffsetXYAtPoint = extent.offsetSlice(relativeToCorner);

        delegate.initSource(sourceVal, sourceOffsetXY);
    }

    @Override
    public boolean notifyChangeZ(int zChange) {
        int z1 = point.getZ() + zChange;

        int relZ1 = relativeToCorner.getZ() + zChange;

        if (relZ1 < 0 || relZ1 >= extent.getZ()) {
            this.bbOM = null;
            return false;
        }

        int zRel = z1 - cornerMin.getZ();
        this.bbOM = object.getVoxelBox().getPixelsForPlane(zRel).buffer();

        delegate.notifyChangeZ(zChange, z1, bbOM);
        return true;
    }

    @Override
    public void processPoint(int xChange, int yChange) {

        int x1 = point.getX() + xChange;
        int y1 = point.getY() + yChange;

        int relX1 = relativeToCorner.getX() + xChange;
        int relY1 = relativeToCorner.getY() + yChange;

        if (relX1 < 0) {
            return;
        }

        if (relX1 >= extent.getX()) {
            return;
        }

        if (relY1 < 0) {
            return;
        }

        if (relY1 >= extent.getY()) {
            return;
        }

        int offset = maskOffsetXYAtPoint + xChange + (yChange * extent.getX());

        if (bbOM.get(offset) == maskOffVal) {
            return;
        }

        delegate.processPoint(xChange, yChange, x1, y1, offset);
    }

    @Override
    public T collectResult() {
        return delegate.collectResult();
    }

    private void updateRel(Point3i point) {
        relativeToCorner = Point3i.immutableSubtract(point, cornerMin);
    }
}
