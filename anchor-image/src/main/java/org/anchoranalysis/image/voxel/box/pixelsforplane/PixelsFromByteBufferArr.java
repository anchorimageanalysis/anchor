package org.anchoranalysis.image.voxel.box.pixelsforplane;

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

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class PixelsFromByteBufferArr implements IPixelsForPlane<ByteBuffer> {

	private final VoxelBuffer<ByteBuffer>[] buffer;
	private final Extent extnt;
	
	private PixelsFromByteBufferArr( Extent extnt ) {
		assert( extnt.getZ() > 0 );
		
		this.extnt = extnt;
		
		buffer = new VoxelBufferByte[extnt.getZ()];
	}
	
	private void init() {
		for (int z=0; z<extnt.getZ(); z++) {
			buffer[z] = VoxelBufferByte.allocate( extnt.getVolumeXY() );
		}		
	}
	
	// START FACTORY METHODS
	public static PixelsFromByteBufferArr createInitialised(Extent extnt) {
		PixelsFromByteBufferArr p = new PixelsFromByteBufferArr(extnt);
		p.init();
		return p;
	}
	
	public static PixelsFromByteBufferArr createEmpty(Extent extnt) {
		return new PixelsFromByteBufferArr(extnt);
	}
	// END FACTORY METHODS	
	
	@Override
	public void setPixelsForPlane(int z, VoxelBuffer<ByteBuffer> pixels) {
		buffer[z] = pixels;
		buffer[z].buffer().clear();
	}

	@Override
	public VoxelBuffer<ByteBuffer> getPixelsForPlane(int z) {
		assert(z>=0);
		VoxelBuffer<ByteBuffer> buf = buffer[z];
		buf.buffer().clear();
		return buf; 
	}

	@Override
	public Extent extnt() {
		return extnt;
	}
}