/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Outputs a channel but with ONLY the pixels in a mask shown, and others set to 0.
 *
 * @author Owen Feehan
 */
public class ChnlMaskedWithObjGenerator extends RasterGenerator
        implements IterableGenerator<ObjectMask> {

    private Channel srcChnl;
    private ObjectMask mask = null;

    public ChnlMaskedWithObjGenerator(Channel srcChnl) {
        super();
        this.srcChnl = srcChnl;
    }

    public ChnlMaskedWithObjGenerator(ObjectMask mask, Channel srcChnl) {
        this.mask = mask;
        this.srcChnl = srcChnl;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        Channel outChnl = createMaskedChnl(getIterableElement(), (Channel) srcChnl);
        return new Stack(outChnl);
    }

    @Override
    public ObjectMask getIterableElement() {
        return mask;
    }

    @Override
    public void setIterableElement(ObjectMask element) {
        mask = element;
    }

    @Override
    public RasterGenerator getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "maskChnl"));
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    /**
     * Creates a new channel, which copies voxels from srcChnl, but sets voxels outside the mask to
     * 0
     *
     * <p>i.e. the new channel is a masked version of srcChnl
     *
     * @param mask mask
     * @param srcChnl the channel to copy
     * @return the masked channel
     */
    private static Channel createMaskedChnl(ObjectMask mask, Channel srcChnl) {

        BoundingBox bbox = mask.getBoundingBox();

        ImageDimensions newSd =
                new ImageDimensions(bbox.extent(), srcChnl.getDimensions().getRes());

        Channel chnlNew =
                ChannelFactory.instance().createEmptyInitialised(newSd, srcChnl.getVoxelDataType());

        byte maskOn = mask.getBinaryValuesByte().getOnByte();

        ReadableTuple3i maxGlobal = bbox.calcCornerMax();
        Point3i pointGlobal = new Point3i();
        Point3i pointLocal = new Point3i();

        VoxelBox<?> vbSrc = srcChnl.getVoxelBox().any();
        VoxelBox<?> vbNew = chnlNew.getVoxelBox().any();

        pointLocal.setZ(0);
        for (pointGlobal.setZ(bbox.cornerMin().getZ());
                pointGlobal.getZ() <= maxGlobal.getZ();
                pointGlobal.incrementZ(), pointLocal.incrementZ()) {

            ByteBuffer maskIn = mask.getVoxelBox().getPixelsForPlane(pointLocal.getZ()).buffer();
            VoxelBuffer<?> pixelsIn = vbSrc.getPixelsForPlane(pointGlobal.getZ());
            VoxelBuffer<?> pixelsOut = vbNew.getPixelsForPlane(pointLocal.getZ());

            for (pointGlobal.setY(bbox.cornerMin().getY());
                    pointGlobal.getY() <= maxGlobal.getY();
                    pointGlobal.incrementY()) {

                for (pointGlobal.setX(bbox.cornerMin().getX());
                        pointGlobal.getX() <= maxGlobal.getX();
                        pointGlobal.incrementX()) {

                    if (maskIn.get() != maskOn) {
                        continue;
                    }

                    int indexGlobal =
                            srcChnl.getDimensions().offset(pointGlobal.getX(), pointGlobal.getY());
                    pixelsOut.putInt(maskIn.position() - 1, pixelsIn.getInt(indexGlobal));
                }
            }
        }

        return chnlNew;
    }
}
