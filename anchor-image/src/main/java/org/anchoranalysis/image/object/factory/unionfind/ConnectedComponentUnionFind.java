package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.Buffer;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;
import org.jgrapht.alg.util.UnionFind;

public class ConnectedComponentUnionFind {

	private final int minNumberVoxels;
	
	private final boolean bigNghb;
	
	/**
	 * Constructor
	 * 
	 * @param minNumberVoxels a minimum number of voxels necessary in the connected-component, otherwise it omitted from the output.
	 * @param bigNghb whether to use a smaller or bigger neighbour (in 3D, 6-conn neighbours are used as small)
	 */
	public ConnectedComponentUnionFind(int minNumberVoxels, boolean bigNghb) {
		super();
		this.minNumberVoxels = minNumberVoxels;
		this.bigNghb = bigNghb;
	}	
		
	/**
	 * Converts a binary-voxel-box (byte) into connected components.
	 * 
	 * @param voxels a binary voxel-box to be searched for connected components. It is consumed (modified) during processing.
	 * @return the connected-components derived from the voxel-box
	 * @throws OperationFailedException
	 */
	public ObjectCollection deriveConnectedByte(BinaryVoxelBox<ByteBuffer> voxels) throws OperationFailedException {
		ObjectCollection objects = new ObjectCollection();
		visitRegion(
			voxels,
			objects,
			minNumberVoxels,
			new ReadWriteByte()
		);
		return objects;
	}
	
	/**
	 * Converts a binary-voxel-box (int) into connected components.
	 * 
	 * @param voxels a binary voxel-box to be searched for connected components. It is consumed (modified) during processing.
	 * @return the connected-components derived from the voxel-box
	 * @throws OperationFailedException
	 */
	public ObjectCollection deriveConnectedInt(BinaryVoxelBox<IntBuffer> voxels) throws OperationFailedException {
		ObjectCollection objects = ObjectCollectionFactory.empty();
		visitRegion(
			voxels,
			objects,
			minNumberVoxels,
			new ReadWriteInt()
		);
		return objects;
	}
	
	private <T extends Buffer> void visitRegion(
		BinaryVoxelBox<T> visited,
		ObjectCollection objects,
		int minNumberVoxels,
		BufferReadWrite<T> bufferReaderWriter
	) throws OperationFailedException {
		
		UnionFind<Integer> unionIndex = new UnionFind<>( new HashSet<Integer>() );
		VoxelBox<IntBuffer> indexBuffer = VoxelBoxFactory.getInt().create( visited.extent() );
		
		int maxBigIDAdded = populateIndexFromBinary(
			visited,
			new PopulateIndexProcessor<>(
				visited,
				indexBuffer,
				createMergeWithNghbs(indexBuffer, unionIndex),
				bufferReaderWriter
			)
		);
		
		processIndexBuffer(
			maxBigIDAdded,
			unionIndex,
			indexBuffer,
			objects,
			minNumberVoxels
		);
	}
	
	private MergeWithNghbs createMergeWithNghbs(VoxelBox<IntBuffer> indexBuffer, UnionFind<Integer> unionIndex) {
		return new MergeWithNghbs(
			indexBuffer,
			unionIndex,
			indexBuffer.extent().getZ() > 1,
			bigNghb
		);
	}
	
	private static <T extends Buffer> int populateIndexFromBinary(
		BinaryVoxelBox<T> visited,
		PopulateIndexProcessor<T> process
	) {
		IterateVoxels.callEachPoint(
			visited.getVoxelBox(),
			process
		);
		return process.getCount() - 1;
	}
	
	// Assumes unionFind begins at 1
	private static Set<Integer> setFromUnionFind( int maxValue, UnionFind<Integer> unionIndex ) {
		TreeSet<Integer> set = new TreeSet<>();
		for( int i=1; i<=maxValue; i++) {
			set.add( unionIndex.find(i) );
		}
		return set;
	}
	
	// Maps the set of integers to a sequence of integers starting at 1
	private static Map<Integer,Integer> mapValuesToContiguousSet( Set<Integer> setIDs ) {
		// We create a map between big ID and small ID
		Map<Integer,Integer> mapIDOrdered = new TreeMap<>();
		int cnt = 1;
		for( Integer id : setIDs ) {
			mapIDOrdered.put(id,cnt);
			cnt++;
		}
		return mapIDOrdered;
	}
		
	private static PointRangeWithCount[] createBBoxArray( int size ) {
		PointRangeWithCount[] bboxArr = new PointRangeWithCount[size];
		for( int i=0; i<bboxArr.length; i++) {
			bboxArr[i] = new PointRangeWithCount();
		}
		return bboxArr;
	}
	
	private static void addPointsAndAssignNewIDs(
		VoxelBox<IntBuffer> indexBuffer,
		UnionFind<Integer> unionIndex,
		Map<Integer,Integer> mapIDOrdered,
		PointRangeWithCount[] bboxArr
	) {
		
		Point3i point = new Point3i();
		Extent extent = indexBuffer.extent();
		for (point.setZ(0); point.getZ()<extent.getZ(); point.incrementZ()) {
			
			IntBuffer bbIndex = indexBuffer.getPixelsForPlane(point.getZ()).buffer();
			
			int offset = 0;
			
			for (point.setY(0); point.getY()<extent.getY(); point.incrementY()) {
				for (point.setX(0); point.getX()<extent.getX(); point.incrementX()) {
					
					int idBig = bbIndex.get(offset); 
					if (idBig!=0) {
						
						Integer idSmall = mapIDOrdered.get( unionIndex.find(idBig) );
						
						PointRangeWithCount bbox = bboxArr[ idSmall-1 ];
						bbox.add(point);
						
						bbIndex.put(offset, idSmall);
					}
					offset++;
				}
				
			}
		}
	}
	
	private static ObjectCollection extractMasksInto(
		PointRangeWithCount[] bboxArr,
		Map<Integer,Integer> mapIDOrdered,
		VoxelBox<IntBuffer> indexBuffer,
		int minNumberVoxels,
		ObjectCollection objects
	) throws OperationFailedException {
		
		for( int smallID : mapIDOrdered.values()) {
			
			PointRangeWithCount bboxWithCnt = bboxArr[smallID-1];
			
			if (bboxWithCnt.getCount()>=minNumberVoxels) {
				objects.add(
					indexBuffer.equalMask(
						bboxWithCnt.deriveBoundingBox(),
						smallID
					)
				);
			}
		}
		return objects;
	}
	
	private static void processIndexBuffer(
		int maxBigIDAdded,
		UnionFind<Integer> unionIndex,
		VoxelBox<IntBuffer> indexBuffer,
		ObjectCollection objects,
		int minNumberVoxels
	) throws OperationFailedException {
		Set<Integer> primaryIDs = setFromUnionFind( maxBigIDAdded, unionIndex );
		
		Map<Integer,Integer> mapIDOrdered = mapValuesToContiguousSet( primaryIDs );
		
		PointRangeWithCount[] bboxArr = createBBoxArray( mapIDOrdered.size() );
		
		addPointsAndAssignNewIDs(
			indexBuffer,
			unionIndex,
			mapIDOrdered,
			bboxArr
		);
		
		extractMasksInto(
			bboxArr,
			mapIDOrdered,
			indexBuffer,
			minNumberVoxels,
			objects
		);
	}
	
}
