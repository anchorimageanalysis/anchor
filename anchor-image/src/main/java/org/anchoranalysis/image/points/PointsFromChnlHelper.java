package org.anchoranalysis.image.points;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

class PointsFromChnlHelper {
	
	private int skipAfterSuccessiveEmptySlices;
	private Point3i crnrMin;
	private Point3i crnrMax;
	private VoxelBox<ByteBuffer> vb;
	private BinaryValuesByte bvb;
	private int startZ;
	private List<Point3i> listOut;
	
	// Stays as -1 until we reach a non-empty slice
	private int successiveEmptySlices = -1;
	private Extent e = vb.extent();
	
	public PointsFromChnlHelper(int skipAfterSuccessiveEmptySlices, Point3i crnrMin, Point3i crnrMax,
			VoxelBox<ByteBuffer> vb, BinaryValuesByte bvb, int startZ, List<Point3i> listOut) {
		super();
		this.skipAfterSuccessiveEmptySlices = skipAfterSuccessiveEmptySlices;
		this.crnrMin = crnrMin;
		this.crnrMax = crnrMax;
		this.vb = vb;
		this.bvb = bvb;
		this.startZ = startZ;
		this.listOut = listOut;
	}
	
	public void firstHalf() {
		for( int z=startZ; z<=crnrMax.getZ(); z++) {
			
			ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
			
			if (!addPointsFromSlice(bb, z)) {
				successiveEmptySlices = 0;
				
			// We don't increase the counter until we've been inside a non-empty slice
			} else if (successiveEmptySlices!=-1){	
				successiveEmptySlices++;
				if (successiveEmptySlices >= skipAfterSuccessiveEmptySlices) {
					break;
				}
				
			}
		}
	}
	
	public void secondHalf() {
		for( int z=(startZ-1); z>=crnrMin.getZ(); z--) {
			
			ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
			
			if (!addPointsFromSlice(bb,z)) {
				successiveEmptySlices = 0;
				
			// We don't increase the counter until we've been inside a non-empty slice
			} else if (successiveEmptySlices!=-1){	
				successiveEmptySlices++;
				if (successiveEmptySlices >= skipAfterSuccessiveEmptySlices) {
					break;
				}
				
			}
		}
	}
	
	private boolean addPointsFromSlice(ByteBuffer bb, int z) {
		boolean addedToSlice = false;
		for( int y=crnrMin.getY(); y<=crnrMax.getY(); y++) {
			for( int x=crnrMin.getX(); x<=crnrMax.getX(); x++) {
				
				int offset = e.offset(x, y);
				if (bb.get(offset)==bvb.getOnByte()) {
					addedToSlice = true;
					listOut.add( new Point3i(x,y,z) );
				}
				
			}
		}
		return addedToSlice;
	}
}
