package org.anchoranalysis.image.voxel.kernel.count;

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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

/**
 * The number of touching-faces of a voxel with a neighbour
 * 
 * i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighbouring pixel
 * 
 * @author Owen Feehan
 *
 */
public abstract class CountKernelNghbBase extends CountKernel {
	
	private boolean useZ;
	
	private BinaryValuesByte bv;
	
	private LocalSlices inSlices;
	
	private Extent extnt;
		
	private boolean outsideAtThreshold = false;
	private boolean ignoreAtThreshold = false;
	
	/**
	 * If TRUE, a voxel is allowed to have more than 1 neighbour.  If FALSE, once at least one neighbour is found, it exists with a count of 1
	 */
	private boolean multipleMatchesPerVoxel = false;
	
	// Constructor
	public CountKernelNghbBase(boolean useZ, BinaryValuesByte bv, boolean multipleMatchesPerVoxel ) {
		super(3);
		this.useZ = useZ;
		this.bv = bv;
		this.multipleMatchesPerVoxel = multipleMatchesPerVoxel;
	}
	
	@Override
	public void init(VoxelBox<ByteBuffer> in) {
		this.extnt = in.extent();
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		this.inSlices = inSlices;
	}

	protected abstract boolean isNghbVoxelAccepted( Point3i pnt, int xShift, int yShift, int zShift, Extent extnt );
		
	@Override
	public int countAtPos(int ind, Point3i pnt) {

		ByteBuffer inArr_Z = inSlices.getLocal(0);
		ByteBuffer inArr_ZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArr_ZPlus1 = inSlices.getLocal(+1);
		
		
		int xLength = extnt.getX();
		
		int x = pnt.getX();
		int y = pnt.getY();
		
		if (bv.isOff(inArr_Z.get(ind))) {
			return 0;
		}
		
		int cnt = 0;
		
		// We walk up and down in x
		x--;
		ind--;
		if (x>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				if (isNghbVoxelAccepted(pnt,-1,0,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				if (isNghbVoxelAccepted(pnt,-1,0,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		}
		
		x += 2;
		ind += 2;
		if (x<extnt.getX()) {
			if (bv.isOff(inArr_Z.get(ind))) {
				if (isNghbVoxelAccepted(pnt,+1,0,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				if (isNghbVoxelAccepted(pnt,+1,0,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		}
		x--;
		ind--;
		
		
		// We walk up and down in y
		y--;
		ind -= xLength;
		if (y>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				if (isNghbVoxelAccepted(pnt,0,-1,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				if (isNghbVoxelAccepted(pnt,0,-1,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		}
		
		y += 2;
		ind += (2*xLength);
		if (y<(extnt.getY())) {
			if (bv.isOff(inArr_Z.get(ind))) {
				if (isNghbVoxelAccepted(pnt,0,+1,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				if (isNghbVoxelAccepted(pnt,0,+1,0, extnt)) {
					if (!multipleMatchesPerVoxel) { return 1; };
					cnt++;
				}
			}
		}
		y--;
		ind -= xLength;
		
		
		if (useZ) {
			
			if (inArr_ZLess1!=null) {
				if (bv.isOff(inArr_ZLess1.get(ind))) {
					if (isNghbVoxelAccepted(pnt,0,0,-1, extnt)) {
						if (!multipleMatchesPerVoxel) { return 1; };
						cnt++;
					}
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					if (isNghbVoxelAccepted(pnt,0,0,-1, extnt)) {
						if (!multipleMatchesPerVoxel) { return 1; };
						cnt++;
					}
				}
			}
			
			if (inArr_ZPlus1!=null) {
				if (bv.isOff(inArr_ZPlus1.get(ind))) {
					if (isNghbVoxelAccepted(pnt,0,0,+1, extnt)) {
						if (!multipleMatchesPerVoxel) { return 1; };
						cnt++;
					}
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					if (isNghbVoxelAccepted(pnt,0,0,+1, extnt)) {
						if (!multipleMatchesPerVoxel) { return 1; };
						cnt++;
					}
				}
			}
		}
		
		return cnt;
	}

}
