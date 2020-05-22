package org.anchoranalysis.image.objmask.factory.unionfind;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.Buffer;
import java.nio.IntBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.jgrapht.alg.util.UnionFind;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type of input voxel-box (used both as an input-source and to keep track of already visited pixels)
 */
abstract class PopulateIndexFromBinary<T extends Buffer> {
	
	public int populateIndexFromBinary(
		BinaryVoxelBox<T> visited,
		VoxelBox<IntBuffer> indexBuffer,
		SlidingBuffer<IntBuffer> slidingIndex,
		UnionFind<Integer> unionIndex,
		boolean bigNghb
	) throws OperationFailedException {
		boolean do3D = indexBuffer.extnt().getZ()>1;
		
		MergeWithNghbs mergeWithNgbs = new MergeWithNghbs(slidingIndex, unionIndex, do3D, bigNghb);
	
		int cnt = 1;
		BinaryValuesByte bvb = visited.getBinaryValues().createByte();
		Extent extnt = visited.extnt();
		for (int z=0; z<extnt.getZ(); z++) {
			
			T bbBinary = visited.getVoxelBox().getPixelsForPlane(z).buffer();
			IntBuffer bbIndex = indexBuffer.getPixelsForPlane(z).buffer();
			
			cnt = processSlice(
				extnt,
				bbBinary,
				bbIndex,
				visited.getBinaryValues(),
				bvb,
				mergeWithNgbs,
				z,
				cnt,
				unionIndex
			);
			
			slidingIndex.shift();
		}
		return cnt-1;
	}
	
	protected abstract boolean isBufferOn( T buffer, int offset, BinaryValues bv, BinaryValuesByte bvb );
	
	protected abstract void putBufferCnt( T buffer, int offset, int cnt );
	
	private int processSlice(
		Extent extnt,
		T bbBinary,
		IntBuffer bbIndex,
		BinaryValues bv,
		BinaryValuesByte bvb,
		MergeWithNghbs mergeWithNgbs,
		int z,
		int cnt,
		UnionFind<Integer> unionIndex
	) {
		int offset = 0;
		
		for (int y=0; y<extnt.getY(); y++) {
			for (int x=0; x<extnt.getX(); x++) {
				
				if (isBufferOn(bbBinary,offset,bv,bvb) && bbIndex.get(offset)==0) {
					
					int nghbLab = mergeWithNgbs.calcMinNghbLabel(
						new Point3i(x, y, z),
						offset
					);
					if (nghbLab==-1) {
						putBufferCnt(bbBinary, offset, cnt);
						bbIndex.put(offset, cnt);
						unionIndex.addElement(cnt);
						cnt++;
					} else {
						bbIndex.put(offset, nghbLab);
					}
				}
				
				offset++;
			}
			
		}
		return cnt;
	}
}
