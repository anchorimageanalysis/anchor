package org.anchoranalysis.anchor.mpp.pixelpart;

/*
 * #%L
 * anchor-mpp
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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.image.voxel.VoxelIntensityList;

public class PixelPartPixelList implements PixelPart<VoxelIntensityList> {

	private VoxelIntensityList combined;
	private List<VoxelIntensityList> list;
	
	public PixelPartPixelList( int numSlices ) {
		
		combined = new VoxelIntensityList();
		
		list = new ArrayList<>();
		for(int i=0; i<numSlices; i++) {
			list.add( new VoxelIntensityList() );
		}
	}
	
	// Should only be used RO, if we want to maintain integrity with the combined list
	@Override
	public VoxelIntensityList getSlice( int sliceID ) {
		return list.get(sliceID);
	}
	
	@Override
	public void addForSlice( int sliceID, int val ) {
		double valD = (double) val;
		combined.add(valD);
		list.get(sliceID).add(valD);
	}

	@Override
	public VoxelIntensityList getCombined() {
		return combined;
	}

	@Override
	public void cleanUp(PixelPartFactory<VoxelIntensityList> factory) {
		// NOTHING TO DO
	}

	@Override
	public int numSlices() {
		return list.size();
	}
}
