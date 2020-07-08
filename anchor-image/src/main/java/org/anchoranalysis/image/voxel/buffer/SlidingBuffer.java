package org.anchoranalysis.image.voxel.buffer;

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


import java.nio.Buffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Contains the ByteBuffer for the current slice, the current slice minus 1, and the current slice plus 1
 * 
 * Can then be shifted (incremented) across all z-slices
 *
 * @param <T> buffer-type
 */
public final class SlidingBuffer<T extends Buffer> {

	private final VoxelBox<T> vb;
	
	private VoxelBuffer<T> centre;
	private VoxelBuffer<T> plusOne;
	private VoxelBuffer<T> minusOne;
	
	private int sliceNum = -1;
	
	public SlidingBuffer(VoxelBox<T> vb) {
		super();
		this.vb = vb;
		seek(0);	// We start off on slice 0 always
	}
	
	/** Seeks a particular slice */
	public void seek(int sliceIndexToSeek) {
		
		if (sliceIndexToSeek==sliceNum) {
			return;
		}
		
		sliceNum = sliceIndexToSeek;
		minusOne = null;
		centre = vb.getPixelsForPlane(sliceNum);
		
		if ((sliceNum-1) >= 0) {
			minusOne = vb.getPixelsForPlane(sliceNum-1);
		}
		
		if ((sliceNum+1) < vb.extent().getZ()) {
			plusOne = vb.getPixelsForPlane(sliceNum+1);
		}
	}
	
	/** Increments the slice number by one */
	public void shift() {
		minusOne = centre;
		centre = plusOne;
		
		sliceNum++;
		
		if ((sliceNum+1)<vb.extent().getZ()) {
			plusOne = vb.getPixelsForPlane(sliceNum+1);
		} else {
			plusOne = null;
		}
	}

	public VoxelBuffer<T> bufferRel( int rel ) {
		switch( rel ) {
		case 1:
			return plusOne;
		case 0:
			return centre;
		case -1:
			return minusOne;
		default:
			return vb.getPixelsForPlane(sliceNum+rel);
		}
	}
	
	public VoxelBox<T> getVoxelBox() {
		return vb;
	}
	
	public Extent extent() {
		return vb.extent();
	}
	
	public VoxelBuffer<T> getCentre() {
		return centre;
	}
	public void setCentre(VoxelBuffer<T> centre) {
		this.centre = centre;
	}
	public VoxelBuffer<T> getPlusOne() {
		return plusOne;
	}
	public void setPlusOne(VoxelBuffer<T> plusOne) {
		this.plusOne = plusOne;
	}
	public VoxelBuffer<T> getMinusOne() {
		return minusOne;
	}
	public void setMinusOne(VoxelBuffer<T> minusOne) {
		this.minusOne = minusOne;
	}
	
}
