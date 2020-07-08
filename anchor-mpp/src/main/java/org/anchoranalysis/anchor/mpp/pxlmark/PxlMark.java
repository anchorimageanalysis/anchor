package org.anchoranalysis.anchor.mpp.pxlmark;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;

// A voxelated mark, localised at certain position
public abstract class PxlMark {

	private ObjectMask objMask;
	private ObjectMask objMaskMIP;		// null until we ned it
	
	public abstract void initForMark( Mark mark, NRGStack stack, RegionMap regionMap );
	
	public BoundedVoxelBox<ByteBuffer> getVoxelBox() {
		return objMask.getVoxelBoxBounded();
	}
	
	public BoundingBox getBoundingBox() {
		return objMask.getBoundingBox();
	}
	
	public BoundingBox getBoundingBoxMIP() {
		return objMaskMIP.getBoundingBox();
	}

	public ObjectMask getObjMask() {
		return objMask;
	}
	
	public ObjectMask getObjMaskMIP() {
		return objMaskMIP;
	}

	protected void setObjMask(ObjectMask objMask) {
		this.objMask = objMask;
	}
	
	protected void setObjMaskMIP(ObjectMask objMask) {
		this.objMaskMIP = objMask;
	}

	public abstract PxlMark duplicate();
	
	public abstract VoxelStatistics statisticsForAllSlices( int chnlID, int regionID );
	
	public abstract VoxelStatistics statisticsForAllSlicesMaskSlice( int chnlID, int regionID, int maskChnlID );
	
	public abstract VoxelStatistics statisticsFor( int chnlID, int regionID, int sliceID );
	
	public abstract void cleanUp();
}
