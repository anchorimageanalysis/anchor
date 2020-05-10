package org.anchoranalysis.image.voxel.kernel.outline;

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
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

// Keeps any on pixel that touches an off pixel where the off pixel has a corresponding HIGH value in vbRequireHigh
public class OutlineKernel3NghbMatchValue extends OutlineKernel3Base {
	
	private BinaryVoxelBox<ByteBuffer> vbRequireHigh;
	private LocalSlices localSlicesRequireHigh;
	private BinaryValuesByte bvRequireHigh;
	private ObjMask om;
	
	public OutlineKernel3NghbMatchValue(
		boolean outsideAtThreshold,
		boolean useZ,
		ObjMask om,
		BinaryVoxelBox<ByteBuffer> vbRequireHigh,
		boolean ignoreAtThreshold
	) {
		this(
			om.getBinaryValuesByte(),
			outsideAtThreshold,
			useZ,
			om,
			vbRequireHigh,
			ignoreAtThreshold
		);
	}
	
	// Constructor
	private OutlineKernel3NghbMatchValue(
		BinaryValuesByte bv,
		boolean outsideAtThreshold,
		boolean useZ,
		ObjMask om,
		BinaryVoxelBox<ByteBuffer> vbRequireHigh,
		boolean ignoreAtThreshold
	) {
		super(bv, outsideAtThreshold, useZ, ignoreAtThreshold);
		this.vbRequireHigh = vbRequireHigh;
		this.om = om;
		this.bvRequireHigh = vbRequireHigh.getBinaryValues().createByte();
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		super.notifyZChange(inSlices, z);
		localSlicesRequireHigh = new LocalSlices(z + om.getBoundingBox().getCrnrMin().getZ(),3, vbRequireHigh.getVoxelBox());
	}
	
	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		ByteBuffer inArr_Z = inSlices.getLocal(0);
		ByteBuffer inArr_ZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArr_ZPlus1 = inSlices.getLocal(+1);
		
		ByteBuffer inArr_R = localSlicesRequireHigh.getLocal(0);
		ByteBuffer inArr_RLess1 = localSlicesRequireHigh.getLocal(-1);
		ByteBuffer inArr_RPlus1 = localSlicesRequireHigh.getLocal(+1);
		
		
		
		
		int xLength = extnt.getX();
		
		int x = pnt.getX();
		int y = pnt.getY();
		
		if (bv.isOff(inArr_Z.get(ind))) {
			return false;
		}
		
		// We walk up and down in x
		x--;
		ind--;
		if (x>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,-1,0);
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,-1,0);
			}
		}
		
		x += 2;
		ind += 2;
		if (x<extnt.getX()) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,+1,0);
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,+1,0);
			}
		}
		x--;
		ind--;
		
		
		// We walk up and down in y
		y--;
		ind -= xLength;
		if (y>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,0,-1);
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,0,-1);
			}
		}
		
		y += 2;
		ind += (2*xLength);
		if (y<(extnt.getY())) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,0,+1);
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return checkIfRequireHighIsTrue(inArr_R,pnt,0,+1);
			}
		}
		y--;
		ind -= xLength;
		
		
		if (useZ) {
			
			if (inArr_ZLess1!=null) {
				if (bv.isOff(inArr_ZLess1.get(ind))) {
					return checkIfRequireHighIsTrue(inArr_RLess1,pnt,0,0);
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					return checkIfRequireHighIsTrue(inArr_RLess1,pnt,0,0);
				}
			}
			
			if (inArr_ZPlus1!=null) {
				if (bv.isOff(inArr_ZPlus1.get(ind))) {
					return checkIfRequireHighIsTrue(inArr_RPlus1,pnt,0,0);
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					return checkIfRequireHighIsTrue(inArr_RPlus1,pnt,0,0);
				}
			}
		}
		
		return false;
	}
	
	private boolean checkIfRequireHighIsTrue( ByteBuffer inArr, Point3i pnt, int xShift, int yShift ) {
		
		if (inArr==null) {
			return outsideAtThreshold;
		}
		
		int x1 = pnt.getX() + om.getBoundingBox().getCrnrMin().getX() + xShift;
		
		if (!vbRequireHigh.extnt().containsX(x1)) {
			return outsideAtThreshold;
		}
		
		int y1 = pnt.getY() + om.getBoundingBox().getCrnrMin().getY() + yShift; 

		if (!vbRequireHigh.extnt().containsY(y1)) {
			return outsideAtThreshold;
		}
		
		int intGlobal = vbRequireHigh.extnt().offset(
			x1,
			y1
		);
		return bvRequireHigh.isOn(inArr.get(intGlobal));
	}
}
