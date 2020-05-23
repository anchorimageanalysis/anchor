package org.anchoranalysis.image.objmask.factory.unionfind;

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


import java.nio.IntBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbour;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbourAbsoluteWithSlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbourFactory;
import org.anchoranalysis.image.voxel.nghb.BigNghb;
import org.anchoranalysis.image.voxel.nghb.Nghb;
import org.anchoranalysis.image.voxel.nghb.SmallNghb;
import org.jgrapht.alg.util.UnionFind;

class MergeWithNghbs {

	private boolean do3D = false;
	
	private PointTester pt;
	private ProcessVoxelNeighbour pointIterator;

	private Nghb nghb;

	// Without mask
	public MergeWithNghbs( SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex, boolean do3D, boolean bigNghb ) {
		this.do3D = do3D;
		
		nghb = bigNghb ? new BigNghb() : new SmallNghb();
		
		this.pt = new PointTester(slidingIndex,unionIndex);
		this.pointIterator = ProcessVoxelNeighbourFactory.withinExtent(slidingIndex.extnt(), pt);
	}
	
	private static class PointTester extends ProcessVoxelNeighbourAbsoluteWithSlidingBuffer {
		
		private int minLabel;
		
		private UnionFind<Integer> unionIndex;
		
		public PointTester( SlidingBuffer<IntBuffer> slidingIndex, UnionFind<Integer> unionIndex ) {
			super(slidingIndex);
			this.unionIndex = unionIndex;
		}

		@Override
		public void initSource(int sourceVal, int sourceOffsetXY) {
			super.initSource(sourceVal, sourceOffsetXY);
			minLabel = -1;
		}

		@Override
		public boolean processPoint( int xChange, int yChange, int x1, int y1) {

			int indexVal = getInt(xChange,yChange);
			
			if (indexVal==0) {
				return false;
			}
			
			if(minLabel==-1) {
				minLabel = indexVal;
			} else {
				
				if(indexVal!=minLabel) {
					
					unionIndex.union(minLabel, indexVal);
					
					if( indexVal < minLabel ) {
						minLabel = indexVal;
					}
				}
				
			}
			return true;
		}

		public int getMinLabel() {
			assert(minLabel!=0);
			return minLabel;
		}
	}
	
	// Calculates the minimum label of the neighbours, making sure to merge any different values
	//   -1 indicates that there is no indexed neighbour
	public int calcMinNghbLabel( Point3i pnt, int exstVal, int indxBuffer) {
		this.pt.initSource(exstVal, indxBuffer);
		IterateVoxels.callEachPointInNghb(pnt, nghb, do3D, pointIterator);
		return pt.getMinLabel();
	}
}
