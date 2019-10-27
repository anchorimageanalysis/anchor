package org.anchoranalysis.image.interpolator;

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


import ij.process.ImageProcessor;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.convert.IJWrap;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class InterpolatorImageJ extends Interpolator {

	@Override
	public VoxelBuffer<ByteBuffer> interpolateByte(VoxelBuffer<ByteBuffer> src,
			VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest) {

		ImageProcessor ipSrc = IJWrap.imageProcessorByte( src, eSrc );
		ImageProcessor ipOut = ipSrc.resize( eDest.getX(), eDest.getY(), true);
		return IJWrap.voxelBufferFromImageProcessorByte( ipOut );
	}

	@Override
	public VoxelBuffer<ShortBuffer> interpolateShort(VoxelBuffer<ShortBuffer> src,
			VoxelBuffer<ShortBuffer> dest, Extent eSrc, Extent eDest) {
		
		ImageProcessor ipSrc = IJWrap.imageProcessorShort( src, eSrc );
		ImageProcessor ipOut = ipSrc.resize( eDest.getX(), eDest.getY(), true);
		return IJWrap.voxelBufferFromImageProcessorShort( ipOut );
		
	}

	@Override
	public boolean isNewValuesPossible() {
		return true;
	}

}
