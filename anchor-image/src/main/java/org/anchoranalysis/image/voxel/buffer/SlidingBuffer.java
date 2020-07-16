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

import lombok.Getter;

/**
 * Contains the {@link ByteBuffer} for the current slice, the current slice minus 1, and the current slice plus 1
 * 
 * Can then be shifted (incremented) across all z-slices
 *
 * @param <T> buffer-type
 */
public final class SlidingBuffer<T extends Buffer> {

	@Getter
	private final VoxelBox<T> voxelBox;
	
	@Getter
	private VoxelBuffer<T> center;
	
	@Getter
	private VoxelBuffer<T> plusOne;
	
	@Getter
	private VoxelBuffer<T> minusOne;
	
	private int sliceNumber = -1;
	
	public SlidingBuffer(VoxelBox<T> voxelBox) {
		super();
		this.voxelBox = voxelBox;
		seek(0);	// We start off on slice 0 always
	}
	
	/** Seeks a particular slice */
	public void seek(int sliceIndexToSeek) {
		
		if (sliceIndexToSeek==sliceNumber) {
			return;
		}
		
		sliceNumber = sliceIndexToSeek;
		minusOne = null;
		center = voxelBox.getPixelsForPlane(sliceNumber);
		
		if ((sliceNumber-1) >= 0) {
			minusOne = voxelBox.getPixelsForPlane(sliceNumber-1);
		}
		
		if ((sliceNumber+1) < voxelBox.extent().getZ()) {
			plusOne = voxelBox.getPixelsForPlane(sliceNumber+1);
		}
	}
	
	/** Increments the slice number by one */
	public void shift() {
		minusOne = center;
		center = plusOne;
		
		sliceNumber++;
		
		if ((sliceNumber+1)<voxelBox.extent().getZ()) {
			plusOne = voxelBox.getPixelsForPlane(sliceNumber+1);
		} else {
			plusOne = null;
		}
	}

	public VoxelBuffer<T> bufferRel( int rel ) {
		switch( rel ) {
		case 1:
			return plusOne;
		case 0:
			return center;
		case -1:
			return minusOne;
		default:
			return voxelBox.getPixelsForPlane(sliceNumber+rel);
		}
	}
	
	public Extent extent() {
		return voxelBox.extent();
	}
}
