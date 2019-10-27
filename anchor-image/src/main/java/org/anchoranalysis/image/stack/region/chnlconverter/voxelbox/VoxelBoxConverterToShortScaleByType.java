package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeShort;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against the maximum value in each buffer.
// So there is no clipping of values, but some might become very small.
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned sizes
public class VoxelBoxConverterToShortScaleByType extends VoxelBoxConverter<ShortBuffer> {

	// This doesn't really make sense for a float, as the maximum value is so much higher, so we take
	//  it as being the same as Integer.MAX_VALUE
	@Override
	public VoxelBuffer<ShortBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

		double div = (double) VoxelDataTypeInt.MAX_VALUE / VoxelDataTypeShort.MAX_VALUE_INT;
		
		ShortBuffer bufferOut = ShortBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			double f = bufferIn.buffer().get();
			
			f = f/div;
						
			bufferOut.put( (short) f );
		}
		
		return VoxelBufferShort.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<ShortBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

		double div = (double) VoxelDataTypeInt.MAX_VALUE / VoxelDataTypeShort.MAX_VALUE_INT;
		
		ShortBuffer bufferOut = ShortBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			bufferOut.put( (short) (bufferIn.buffer().get() / div) );
		}
		
		return VoxelBufferShort.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<ShortBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {
		return bufferIn.duplicate();
	}

	@Override
	public VoxelBuffer<ShortBuffer> convertFromByte(VoxelBuffer<ByteBuffer> bufferIn) {
		double mult = (double) VoxelDataTypeShort.MAX_VALUE_INT / VoxelDataTypeByte.MAX_VALUE_INT;
		
		ShortBuffer bufferOut = ShortBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			bufferOut.put( (short) (bufferIn.buffer().get() * mult) );
		}
		
		return VoxelBufferShort.wrap(bufferOut);		
	}
}
