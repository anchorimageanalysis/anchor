/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.binary.connected;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.jgrapht.alg.util.UnionFind;

@AllArgsConstructor
class ConnectedComponentUnionFind {

    /**
     * A minimum number of voxels necessary in the connected-component, otherwise it omitted from
     * the output.
     */
    private final int minNumberVoxels;

    /** whether to use a smaller or bigger neighbor (in 3D, 6-conn neighbors are used as small) */
    private final boolean bigNeighborhood;

    /**
     * Converts binary-voxels (byte) into connected components.
     *
     * @param voxels binary-voxels to be searched for connected components. It is consumed
     *     (modified) during processing.
     * @return the connected-components derived from the voxels.
     */
    public ObjectCollection deriveConnectedByte(BinaryVoxels<UnsignedByteBuffer> voxels) {
        return deriveConnected(voxels, new ReadWriteByte());
    }

    /**
     * Converts binary-voxels (int) into connected components.
     *
     * @param voxels binary voxels to be searched for connected components. It is consumed
     *     (modified) during processing.
     * @return the connected-components derived from the voxels
     */
    public ObjectCollection deriveConnectedInt(BinaryVoxels<UnsignedIntBuffer> voxels) {
        return deriveConnected(voxels, new ReadWriteInt());
    }

    /**
     * Converts binary-voxels into connected components.
     *
     * @param voxels binary voxels to be searched for connected components. It is consumed
     *     (modified) during processing.
     * @param bufferReaderWriter reads and writes to the voxel-buffer
     * @return the connected-components derived from the voxels
     */
    private <T> ObjectCollection deriveConnected(
            BinaryVoxels<T> voxels, BufferReadWrite<T> bufferReaderWriter) {
        LinkedList<ObjectMask> objects = new LinkedList<>();
        visitRegion(voxels, objects::add, minNumberVoxels, bufferReaderWriter);
        return new ObjectCollection(new ArrayList<>(objects));
    }

    private <T> void visitRegion(
            BinaryVoxels<T> visited,
            Consumer<ObjectMask> consumer,
            int minimumNumberVoxels,
            BufferReadWrite<T> bufferReaderWriter) {

        UnionFind<Integer> unionIndex = new UnionFind<>(new HashSet<>());
        Voxels<UnsignedIntBuffer> indexBuffer =
                VoxelsFactory.getUnsignedInt().createInitialized(visited.extent());

        int maxBigIDAdded =
                populateIndexFromBinary(
                        visited,
                        new PopulateIndexProcessor<>(
                                visited,
                                indexBuffer,
                                createMergeWithNeighbors(indexBuffer, unionIndex),
                                bufferReaderWriter));

        processIndexBuffer(maxBigIDAdded, unionIndex, indexBuffer, consumer, minimumNumberVoxels);
    }

    private MergeWithNeighbors createMergeWithNeighbors(
            Voxels<UnsignedIntBuffer> indexBuffer, UnionFind<Integer> unionIndex) {
        return new MergeWithNeighbors(
                indexBuffer, unionIndex, indexBuffer.extent().z() > 1, bigNeighborhood);
    }

    private static <T> int populateIndexFromBinary(
            BinaryVoxels<T> visited, PopulateIndexProcessor<T> process) {
        IterateVoxelsAll.withBuffer(visited.voxels(), process);
        return process.getCount() - 1;
    }

    // Assumes unionFind begins at 1
    private static Set<Integer> setFromUnionFind(int maxValue, UnionFind<Integer> unionIndex) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 1; i <= maxValue; i++) {
            set.add(unionIndex.find(i));
        }
        return set;
    }

    // Maps the set of integers to a sequence of integers starting at 1
    private static Map<Integer, Integer> mapValuesToContiguousSet(Set<Integer> setIDs) {
        // We create a map between big ID and small ID
        Map<Integer, Integer> mapIDOrdered = new TreeMap<>();
        int count = 1;
        for (Integer id : setIDs) {
            mapIDOrdered.put(id, count);
            count++;
        }
        return mapIDOrdered;
    }

    private static PointRangeWithCount[] createBBoxArray(int size) {
        PointRangeWithCount[] boxArray = new PointRangeWithCount[size];
        for (int i = 0; i < boxArray.length; i++) {
            boxArray[i] = new PointRangeWithCount();
        }
        return boxArray;
    }

    private static void addPointsAndAssignNewIdentifiers(
            Voxels<UnsignedIntBuffer> indexBuffer,
            UnionFind<Integer> unionIndex,
            Map<Integer, Integer> mapIDOrdered,
            PointRangeWithCount[] boxArr) {

        Point3i point = new Point3i();
        Extent extent = indexBuffer.extent();
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            UnsignedIntBuffer bufferIndex = indexBuffer.sliceBuffer(point.z());

            int offset = 0;

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    int idBig = bufferIndex.getRaw(offset);
                    if (idBig != 0) {

                        Integer idSmall = mapIDOrdered.get(unionIndex.find(idBig));

                        PointRangeWithCount box = boxArr[idSmall - 1];
                        box.add(point);

                        bufferIndex.putRaw(offset, idSmall);
                    }
                    offset++;
                }
            }
        }
    }

    private static void extractMasksInto(
            PointRangeWithCount[] boxArr,
            Map<Integer, Integer> mapIDOrdered,
            Voxels<UnsignedIntBuffer> indexBuffer,
            int minNumberVoxels,
            Consumer<ObjectMask> consume) {

        VoxelsExtracter<UnsignedIntBuffer> extracter = indexBuffer.extract();

        for (int smallID : mapIDOrdered.values()) {

            PointRangeWithCount boxWithCnt = boxArr[smallID - 1];

            if (boxWithCnt.getCount() >= minNumberVoxels) {
                try {
                    consume.accept(
                            extracter
                                    .voxelsEqualTo(smallID)
                                    .deriveObject(boxWithCnt.deriveBoundingBox()));
                } catch (OperationFailedException e) {
                    throw new AnchorImpossibleSituationException();
                }
            }
        }
    }

    private static void processIndexBuffer(
            int maxBigIDAdded,
            UnionFind<Integer> unionIndex,
            Voxels<UnsignedIntBuffer> indexBuffer,
            Consumer<ObjectMask> consumer,
            int minNumberVoxels) {
        Set<Integer> primaryIDs = setFromUnionFind(maxBigIDAdded, unionIndex);

        Map<Integer, Integer> mapIDOrdered = mapValuesToContiguousSet(primaryIDs);

        PointRangeWithCount[] boxArr = createBBoxArray(mapIDOrdered.size());

        addPointsAndAssignNewIdentifiers(indexBuffer, unionIndex, mapIDOrdered, boxArr);

        extractMasksInto(boxArr, mapIDOrdered, indexBuffer, minNumberVoxels, consumer);
    }
}
