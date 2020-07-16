/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import java.nio.ShortBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ShortFromUnsignedShort extends ConvertToShort {

    private int bytesPerPixel = 2;
    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;

    public ShortFromUnsignedShort(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<ShortBuffer> convertSingleChnl(byte[] src, int channelRelative) {

        short[] crntChnlBytes = new short[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            int s = DataTools.bytesToShort(src, indIn, bytesPerPixel, littleEndian);

            // Make positive
            if (s < 0) {
                s += (VoxelDataTypeUnsignedShort.MAX_VALUE_INT + 1);
            }

            if (s > VoxelDataTypeUnsignedShort.MAX_VALUE_INT) {
                s = VoxelDataTypeUnsignedShort.MAX_VALUE_INT;
            }
            if (s < 0) {
                s = 0;
            }

            crntChnlBytes[indOut++] = (short) s;
        }

        return VoxelBufferShort.wrap(crntChnlBytes);
    }
}
