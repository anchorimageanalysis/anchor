
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


/**
 * 
 * @author Owen Feehan
 *
 * @param <T> part-type
 */
public class IndexByRegion<T> {
	
	private List<PixelPart<T>> list;
	
	public IndexByRegion( PixelPartFactory<T> factory, int numRegions, int numSlices ) {
		list = new ArrayList<>();
		for(int i=0; i<numRegions; i++) {
			list.add( factory.create(numSlices) );
		}
	}
	
	// Should only be used RO, if we want to maintain integrity with the combined list
	public T getForAllSlices( int regionID ) {
		return list.get(regionID).getCombined();
	}
	
	// Should only be used RO, if we want to maintain integrity with the combined list
	public T getForSlice( int regionID, int sliceID ) {
		return list.get(regionID).getSlice(sliceID);
	}
	
	public void addToPxlList( int regionID, int sliceID, int val ) {
		list.get(regionID).addForSlice(sliceID,val);
	}
	
	public void cleanUp( PixelPartFactory<T> factory ) {
		for (int i=0; i<list.size(); i++) {
			list.get(i).cleanUp(factory);
		}
	}
	
	public int numSlices() {
		return list.get(0).numSlices();
	}
}
