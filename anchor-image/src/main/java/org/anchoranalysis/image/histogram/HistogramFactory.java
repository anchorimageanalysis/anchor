/* (C)2020 */
package org.anchoranalysis.image.histogram;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramFactory {

    public static Histogram createGuessMaxBin(Collection<Histogram> histograms)
            throws CreateException {

        if (histograms.isEmpty()) {
            throw new CreateException("Cannot determine a maxBinVal as the collection is empty");
        }

        Histogram h = histograms.iterator().next();

        int maxBinVal = h.getMaxBin();
        return create(histograms, maxBinVal);
    }

    // Creates histograms from a collection of existing histograms
    // Assumes histograms all have the same max Bin
    public static Histogram create(Collection<Histogram> histograms, int maxBinVal)
            throws CreateException {

        if (histograms.isEmpty()) {
            return new HistogramArray(maxBinVal);
        }

        Histogram out = new HistogramArray(maxBinVal);
        for (Histogram h : histograms) {
            try {
                out.addHistogram(h);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }
        return out;
    }

    public static Histogram create(Channel chnl) throws CreateException {

        try {
            return create(chnl.getVoxelBox());
        } catch (IncorrectVoxelDataTypeException e) {
            throw new CreateException("Cannot create histogram from ImgChnl", e);
        }
    }

    public static Histogram create(Channel chnl, Mask mask) throws CreateException {

        if (!chnl.getDimensions().getExtent().equals(mask.getDimensions().getExtent())) {
            throw new CreateException("Size of chnl and mask do not match");
        }

        Histogram total = new HistogramArray((int) chnl.getVoxelDataType().maxValue());

        VoxelBox<?> vb = chnl.getVoxelBox().any();

        Histogram h = createWithMask(vb, new ObjectMask(mask.binaryVoxelBox()));
        try {
            total.addHistogram(h);
        } catch (OperationFailedException e) {
            assert false;
        }

        return total;
    }

    public static Histogram create(Channel chnl, ObjectMask object) {
        return create(chnl, ObjectCollectionFactory.from(object));
    }

    public static Histogram create(Channel chnl, ObjectCollection objects) {
        return createWithMasks(chnl.getVoxelBox(), objects);
    }

    public static Histogram create(VoxelBuffer<?> inputBuffer) {

        Histogram hist = new HistogramArray((int) inputBuffer.dataType().maxValue());
        addBufferToHistogram(hist, inputBuffer, inputBuffer.size());
        return hist;
    }

    public static Histogram create(VoxelBoxWrapper inputBuffer) {
        return create(inputBuffer, Optional.empty());
    }

    public static Histogram create(VoxelBoxWrapper inputBuffer, Optional<ObjectMask> object) {

        if (!isDataTypeSupported(inputBuffer.getVoxelDataType())) {
            throw new IncorrectVoxelDataTypeException(
                    String.format("Data type %s is not supported", inputBuffer.getVoxelDataType()));
        }

        if (object.isPresent()) {
            return createWithMask(inputBuffer.any(), object.get());
        } else {
            return create(inputBuffer.any());
        }
    }

    private static boolean isDataTypeSupported(VoxelDataType dataType) {
        return dataType.equals(VoxelDataTypeUnsignedByte.INSTANCE)
                || dataType.equals(VoxelDataTypeUnsignedShort.INSTANCE);
    }

    private static Histogram createWithMask(VoxelBox<?> inputBuffer, ObjectMask object) {

        Histogram hist = new HistogramArray((int) inputBuffer.dataType().maxValue());

        Extent e = inputBuffer.extent();
        Extent eMask = object.getBoundingBox().extent();

        ReadableTuple3i cornerMin = object.getBoundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.getBoundingBox().calcCornerMax();

        byte maskOnVal = object.getBinaryValuesByte().getOnByte();

        for (int z = cornerMin.getZ(); z <= cornerMax.getZ(); z++) {

            VoxelBuffer<?> bb = inputBuffer.getPixelsForPlane(z);
            ByteBuffer bbMask =
                    object.getVoxelBox().getPixelsForPlane(z - cornerMin.getZ()).buffer();

            for (int y = cornerMin.getY(); y <= cornerMax.getY(); y++) {
                for (int x = cornerMin.getX(); x <= cornerMax.getX(); x++) {

                    int offset = e.offset(x, y);
                    int offsetMask = eMask.offset(x - cornerMin.getX(), y - cornerMin.getY());

                    byte maskVal = bbMask.get(offsetMask);

                    if (maskVal == maskOnVal) {
                        int val = bb.getInt(offset);
                        hist.incrVal(val);
                    }
                }
            }
        }
        return hist;
    }

    private static Histogram createWithMasks(VoxelBoxWrapper vb, ObjectCollection objects) {

        Histogram total = new HistogramArray((int) vb.getVoxelDataType().maxValue());

        for (ObjectMask objectMask : objects) {
            Histogram histogram = createWithMask(vb.any(), objectMask);
            try {
                total.addHistogram(histogram);
            } catch (OperationFailedException e) {
                assert false;
            }
        }
        return total;
    }

    private static Histogram create(VoxelBox<?> inputBox) {

        Histogram hist = new HistogramArray((int) inputBox.dataType().maxValue());

        Extent e = inputBox.extent();
        int volumeXY = e.getVolumeXY();
        for (int z = 0; z < e.getZ(); z++) {
            addBufferToHistogram(hist, inputBox.getPixelsForPlane(z), volumeXY);
        }
        return hist;
    }

    private static void addBufferToHistogram(Histogram hist, VoxelBuffer<?> bb, int maxOffset) {
        for (int offset = 0; offset < maxOffset; offset++) {
            int val = bb.getInt(offset);
            hist.incrVal(val);
        }
    }

    public static Histogram createHistogramIgnoreZero(
            Channel chnl, ObjectMask object, boolean ignoreZero) {
        Histogram hist = create(chnl, object);
        if (ignoreZero) {
            hist.zeroVal(0);
        }
        return hist;
    }
}
