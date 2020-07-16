/* (C)2020 */
package org.anchoranalysis.image.object.ops;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryChnlFromObjects {

    /** We look for space IN objects, and create channel to display it */
    public static Mask createFromObjects(
            ObjectCollection masks, ImageDimensions sd, BinaryValues outVal) {
        return createChannelObjectCollectionHelper(
                masks, sd, outVal, outVal.getOffInt(), outVal.createByte().getOnByte());
    }

    /** We look for space NOT in the objects, and create channel to display it */
    public static Mask createFromNotObjects(
            ObjectCollection objects, ImageDimensions sd, BinaryValues outVal) {
        return createChannelObjectCollectionHelper(
                objects, sd, outVal, outVal.getOnInt(), outVal.createByte().getOffByte());
    }

    // We look for the values that are NOT on the masks
    private static Mask createChannelObjectCollectionHelper(
            ObjectCollection masks,
            ImageDimensions dimensions,
            BinaryValues outVal,
            int initialState,
            byte objectState) {

        Channel chnlNew =
                ChannelFactory.instance()
                        .createEmptyInitialised(dimensions, VoxelDataTypeUnsignedByte.INSTANCE);
        VoxelBox<ByteBuffer> vbNew = chnlNew.getVoxelBox().asByte();

        if (outVal.getOnInt() != 0) {
            vbNew.setAllPixelsTo(initialState);
        }

        writeChannelObjectCollection(vbNew, masks, objectState);

        return new Mask(chnlNew, outVal);
    }

    // nullVal is assumed to be 0
    private static void writeChannelObjectCollection(
            VoxelBox<ByteBuffer> vb, ObjectCollection masks, byte outVal) {

        for (ObjectMask object : masks) {
            writeObjectToVoxelBox(object, vb, outVal);
        }
    }

    private static void writeObjectToVoxelBox(
            ObjectMask object, VoxelBox<ByteBuffer> voxelBoxOut, byte outValByte) {

        BoundingBox bbox = object.getBoundingBox();

        ReadableTuple3i maxGlobal = bbox.calcCornerMax();
        Point3i pointGlobal = new Point3i();
        Point3i pointLocal = new Point3i();

        byte maskOn = object.getBinaryValuesByte().getOnByte();

        pointLocal.setZ(0);
        for (pointGlobal.setZ(bbox.cornerMin().getZ());
                pointGlobal.getZ() <= maxGlobal.getZ();
                pointGlobal.incrementZ(), pointLocal.incrementZ()) {

            ByteBuffer maskIn = object.getVoxelBox().getPixelsForPlane(pointLocal.getZ()).buffer();

            ByteBuffer pixelsOut =
                    voxelBoxOut.getPlaneAccess().getPixelsForPlane(pointGlobal.getZ()).buffer();
            writeToBufferMasked(
                    maskIn,
                    pixelsOut,
                    voxelBoxOut.extent(),
                    bbox.cornerMin(),
                    pointGlobal,
                    maxGlobal,
                    maskOn,
                    outValByte);
        }
    }

    private static void writeToBufferMasked(
            ByteBuffer maskIn,
            ByteBuffer pixelsOut,
            Extent extentOut,
            ReadableTuple3i cornerMin,
            Point3i pointGlobal,
            ReadableTuple3i maxGlobal,
            byte maskOn,
            byte outValByte) {

        for (pointGlobal.setY(cornerMin.getY());
                pointGlobal.getY() <= maxGlobal.getY();
                pointGlobal.incrementY()) {

            for (pointGlobal.setX(cornerMin.getX());
                    pointGlobal.getX() <= maxGlobal.getX();
                    pointGlobal.incrementX()) {

                if (maskIn.get() != maskOn) {
                    continue;
                }

                int indexGlobal = extentOut.offset(pointGlobal.getX(), pointGlobal.getY());
                pixelsOut.put(indexGlobal, outValByte);
            }
        }
    }
}
