/* (C)2020 */
package org.anchoranalysis.image.stack.bufferedimage;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BufferedImageFactory {

    private BufferedImageFactory() {}

    public static BufferedImage createGrayscale(VoxelBox<ByteBuffer> vb) throws CreateException {

        Extent e = vb.extent();
        checkExtentZ(e);

        return createBufferedImageFromGrayscaleBuffer(vb.getPixelsForPlane(0).buffer(), e);
    }

    public static BufferedImage createRGB(
            VoxelBox<ByteBuffer> red,
            VoxelBox<ByteBuffer> green,
            VoxelBox<ByteBuffer> blue,
            Extent e)
            throws CreateException {
        checkExtentZ(e);

        BufferedImage bi = new BufferedImage(e.getX(), e.getY(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] arrComb =
                createCombinedByteArray(
                        e,
                        firstBuffer(red, e, "red"),
                        firstBuffer(green, e, "green"),
                        firstBuffer(blue, e, "blue"));
        bi.getWritableTile(0, 0).setDataElements(0, 0, e.getX(), e.getY(), arrComb);

        return bi;
    }

    private static ByteBuffer firstBuffer(VoxelBox<ByteBuffer> vb, Extent e, String dscr)
            throws CreateException {

        if (!vb.extent().equals(e)) {
            throw new CreateException(dscr + " channel extent does not match");
        }

        return vb.getPixelsForPlane(0).buffer();
    }

    private static BufferedImage createBufferedImageFromGrayscaleBuffer(
            ByteBuffer bbGray, Extent e) {

        BufferedImage bi = new BufferedImage(e.getX(), e.getY(), BufferedImage.TYPE_BYTE_GRAY);

        byte[] arr = bbGray.array();
        bi.getWritableTile(0, 0).setDataElements(0, 0, e.getX(), e.getY(), arr);

        return bi;
    }

    private static byte[] createCombinedByteArray(
            Extent e, ByteBuffer bbRed, ByteBuffer bbGreen, ByteBuffer bbBlue) {

        int size = e.getVolumeAsInt();
        byte[] arrComb = new byte[size * 3];
        int cnt = 0;
        for (int i = 0; i < size; i++) {

            arrComb[cnt++] = bbRed.get(i);
            arrComb[cnt++] = bbGreen.get(i);
            arrComb[cnt++] = bbBlue.get(i);
        }
        return arrComb;
    }

    private static void checkExtentZ(Extent e) throws CreateException {
        if (e.getZ() != 1) {
            throw new CreateException("z dimension must be 1");
        }
    }
}
