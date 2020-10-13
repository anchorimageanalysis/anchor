/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster.object.collection;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes an object-mask as a mask (i.e. as a raster image)
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ObjectAsMaskGenerator extends RasterGenerator<ObjectMask> {

    // START REQUIRED ARGUMENTS
    private final Optional<Resolution> resolution;
    // END REQUIRED ARGUMENTS

    /** Creates using a default image-resolution. */
    public ObjectAsMaskGenerator() {
        this(Optional.empty());
    }

    @Override
    public Stack transform(ObjectMask element) throws OutputWriteFailedException {
        return new Stack(createChannelFromMask(element, resolution));
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "mask"));
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    @Override
    public StackWriteOptions writeOptions() {
        return StackWriteOptions.binaryChannelMaybe3D();
    }

    /**
     * Creates a channel for an object-mask
     *
     * <p>An unsigned 8-bit buffer is created where values inside the mask are 255 are values
     * outside are 0
     *
     * @param objectMask the object-mask
     * @param resolution resolution to use for the channel
     * @return the newly created channel
     */
    private static Channel createChannelFromMask(ObjectMask objectMask, Optional<Resolution> resolution) {

        int outOnValue = BinaryValuesByte.getDefault().getOnByte();

        BoundingBox box = objectMask.boundingBox();

        Dimensions dimensions = new Dimensions(box.extent(), resolution);

        Channel channelNew =
                ChannelFactory.instance().create(dimensions, UnsignedByteVoxelType.INSTANCE);

        Voxels<UnsignedByteBuffer> voxelsNew = channelNew.voxels().asByte();

        byte matchValue = objectMask.binaryValuesByte().getOnByte();
        byte outOnValueByte = (byte) outOnValue;

        Point3i pointLocal = new Point3i();

        for (pointLocal.setZ(0); pointLocal.z() < dimensions.z(); pointLocal.incrementZ()) {

            UnsignedByteBuffer pixelsIn = objectMask.sliceBufferLocal(pointLocal.z());
            UnsignedByteBuffer pixelsOut = voxelsNew.sliceBuffer(pointLocal.z());

            while (pixelsIn.hasRemaining()) {

                if (pixelsIn.getRaw() == matchValue) {
                    pixelsOut.putRaw(pixelsIn.position() - 1, outOnValueByte);
                }
            }
        }

        return channelNew;
    }
}
