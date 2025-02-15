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

package org.anchoranalysis.image.io.object.output.hdf5;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.io.object.HDF5PathHelper;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Writes an object-mask to a path within a HDF5 file
 *
 * <p>The object-mask is written as a 3D array of 255 and 0 bytes
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

    /** Writes the {@link ObjectMask} to the HDF5 file. */
    public void writeObject() {

        writer.uint8().writeMDArray(pathHDF5, byteArray(object.binaryVoxels()), compressionLevel());

        addCorner();
    }

    /** Adds attributes for the minimum-corner of the bounding-box of the {@link ObjectMask}. */
    private void addCorner() {
        addIntAttribute(HDF5PathHelper.EXTENT_X, ReadableTuple3i::x);
        addIntAttribute(HDF5PathHelper.EXTENT_Y, ReadableTuple3i::y);
        addIntAttribute(HDF5PathHelper.EXTENT_Z, ReadableTuple3i::z);
    }

    /** Adds an attribute of type int, as extracted from a {@link ReadableTuple3i}. */
    private void addIntAttribute(String name, ToIntFunction<ReadableTuple3i> extractValue) {
        Integer value = extractValue.applyAsInt(object.boundingBox().cornerMin());
        writer.uint32().setAttr(pathHDF5, name, value.intValue());
    }

    /**
     * Converts a {@code BinaryVoxels<UnsignedByteBuffer>} into a byte-array suitable for the HDF5
     * file.
     */
    private static MDByteArray byteArray(BinaryVoxels<UnsignedByteBuffer> voxels) {

        MDByteArray array = new MDByteArray(voxels.extent().toArray());

        IterateVoxelsAll.withBuffer(
                voxels.voxels(),
                (point, buffer, offset) ->
                        array.set(buffer.getRaw(offset), point.x(), point.y(), point.z()));

        return array;
    }
}
