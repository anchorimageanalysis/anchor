/* (C)2020 */
package org.anchoranalysis.test.image;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

public class ChnlFixture {

    // Creates an intensity value for a given location
    @FunctionalInterface
    public interface IntensityFunction {
        int valueFor(int x, int y, int z);
    }

    // START: IntensityFunction examples
    public static int sumMod(int x, int y, int z) {
        return mod(x + y + z);
    }

    public static int diffMod(int x, int y, int z) {
        return mod(x - y - z);
    }

    public static int multMod(int x, int y, int z) {
        int xy = mod(x * y);
        return mod(xy * z);
    }
    // END: IntensityFunction examples

    // START: image size examples
    public static final Extent SMALL_3D = new Extent(8, 11, 4);
    public static final Extent SMALL_2D = SMALL_3D.flattenZ();
    public static final Extent MEDIUM_3D = new Extent(69, 61, 7);
    public static final Extent MEDIUM_2D = MEDIUM_3D.flattenZ();
    public static final Extent LARGE_3D = new Extent(1031, 2701, 19);
    public static final Extent LARGE_2D = LARGE_3D.flattenZ();
    // END: image size examples

    public static Channel createChnl(Extent e, IntensityFunction createIntensity) {

        ImageDimensions sd = new ImageDimensions(e, ImageResFixture.INSTANCE);

        Channel chnl = new ChannelFactoryByte().createEmptyInitialised(sd);

        // Populate the channel with values
        for (int z = 0; z < e.getZ(); z++) {

            VoxelBuffer<? extends Buffer> slice = chnl.getVoxelBox().any().getPixelsForPlane(z);

            for (int x = 0; x < e.getX(); x++) {
                for (int y = 0; y < e.getY(); y++) {
                    int intens = createIntensity.valueFor(x, y, z);
                    slice.putInt(e.offset(x, y), intens);
                }
            }
        }

        return chnl;
    }

    // Finds modulus of a number with the maximum byte value (+1)
    private static int mod(int num) {
        return Math.floorMod(num, VoxelDataTypeUnsignedByte.MAX_VALUE_INT + 1);
    }
}
