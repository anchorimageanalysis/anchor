/* (C)2020 */
package org.anchoranalysis.image.io.objects;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import java.nio.ByteBuffer;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Writes an ObjectMask to a path within a HDF5 file
 *
 * <p>The mask is written as a 3D array of 255 and 0 bytes
 *
 * <p>The corner-position of the bounding box is added as attributes: x, y, z
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ObjectMaskHDF5Writer {

    /** The object-mask to write */
    private final ObjectMask object;

    /** The path in the HDF5 file to write it to */
    private final String pathHDF5;

    /** An opened-writer for the HDF5 file */
    private final IHDF5Writer writer;

    /** Whether to use compression or not */
    private final boolean compression;

    private HDF5IntStorageFeatures compressionLevel() {
        if (compression) {
            return HDF5IntStorageFeatures.INT_DEFLATE_UNSIGNED;
        } else {
            return HDF5IntStorageFeatures.INT_NO_COMPRESSION_UNSIGNED;
        }
    }

    public void apply() {

        writer.uint8()
                .writeMDArray(pathHDF5, byteArray(object.binaryVoxelBox()), compressionLevel());

        addCorner();
    }

    private void addCorner() {
        addAttribute(HDF5PathHelper.EXTENT_X, ReadableTuple3i::getX);
        addAttribute(HDF5PathHelper.EXTENT_Y, ReadableTuple3i::getY);
        addAttribute(HDF5PathHelper.EXTENT_Z, ReadableTuple3i::getZ);
    }

    private void addAttribute(String attrName, ToIntFunction<ReadableTuple3i> extrVal) {

        Integer crnrVal = extrVal.applyAsInt(object.getBoundingBox().cornerMin());
        writer.uint32().setAttr(pathHDF5, attrName, crnrVal.intValue());
    }

    private static MDByteArray byteArray(BinaryVoxelBox<ByteBuffer> bvb) {

        Extent extent = bvb.extent();

        MDByteArray md = new MDByteArray(dimensionsFromExtent(extent));

        for (int z = 0; z < extent.getZ(); z++) {

            ByteBuffer bb = bvb.getPixelsForPlane(z).buffer();

            for (int y = 0; y < extent.getY(); y++) {
                for (int x = 0; x < extent.getX(); x++) {
                    md.set(bb.get(extent.offset(x, y)), x, y, z);
                }
            }
        }
        return md;
    }

    private static int[] dimensionsFromExtent(Extent extent) {
        return new int[] {extent.getX(), extent.getY(), extent.getZ()};
    }
}
