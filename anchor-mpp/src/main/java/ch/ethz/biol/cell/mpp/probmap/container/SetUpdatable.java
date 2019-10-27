package ch.ethz.biol.cell.mpp.probmap.container;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;

import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.overlap.OverlapUtilities;
import ch.ethz.biol.cell.mpp.mark.pxlmark.PxlMark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembership;
import ch.ethz.biol.cell.mpp.nrg.IGetMemoForIndex;
import ch.ethz.biol.cell.mpp.pair.UpdateMarkSetException;

public class SetUpdatable extends UpdatablePoint3dContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5569567716288411080L;

	// START BEAN PROPERTIES
	@BeanField
	private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
	// END BEAN PROPERTIES
	
	private RandomSet<Point3d> setPnts;
	
	private ImageDim dim;
	private BinaryChnl binaryImage;
	private Chnl binaryImageChnl;
	
	public SetUpdatable() {
		super();
	}
	
	@Override
	public void init(BinaryChnl binaryImage) throws InitException {
		this.binaryImage = binaryImage;
		this.binaryImageChnl = binaryImage.getChnl();
		
		dim = binaryImage.getDimensions();
		
		setPnts = new RandomSet<>();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		addEntireScene(bvb);
	}
	
	// Randomise location
    @Override
	public Point3d sample( RandomNumberGenerator re ) {
	
    	if (setPnts.size()==0) {
    		return null;
    	}
    	
    	int randomIndex = (int) (re.nextDouble() * setPnts.size());
    	Point3d pnt = setPnts.get(randomIndex);
    	
		assert( pnt.getX() >= 0 );
		assert( pnt.getY() >= 0 );
		assert( pnt.getZ() >= 0 );
		
		assert( pnt.getX() < getDimensions().getX() );
		assert( pnt.getY() < getDimensions().getY() );
		assert( pnt.getZ() < getDimensions().getZ() );
		
		// To hide our internal data from manipulation, even though this (presumably) adds
		//  a bit of overhead
    	return new Point3d(pnt);
    }
	
	@Override
	public void initUpdatableMarkSet(IGetMemoForIndex marks, NRGStackWithParams nrgStack, LogErrorReporter logger, SharedFeatureSet sharedFeatures) throws InitException {
		
		

	}
	
	
	private void addEntireScene( BinaryValuesByte bvb ) {
		
		Extent e = binaryImageChnl.getDimensions().getExtnt();
		
		VoxelBox<ByteBuffer> vbBinary = binaryImageChnl.getVoxelBox().asByte();
		
		// Where we actually do the work
		Point3i pos = new Point3i();
		
		ImageDim sd = getDimensions();
    	for( pos.setZ(0); pos.getZ()<sd.getZ(); pos.incrZ()) {
    		
    		ByteBuffer bbBinaryImage = vbBinary.getPixelsForPlane(pos.getZ()).buffer();
    		
	    	for( pos.setY(0); pos.getY()<sd.getY(); pos.incrY()) {
		    	for( pos.setX(0); pos.getX()<sd.getX(); pos.incrX()) {
		    		
		    		if( bbBinaryImage.get(e.offsetSlice(pos))==bvb.getOnByte()) {
		    			
		    			assert( pos.getX() >= 0 );
		    			assert( pos.getY() >= 0 );
		    			assert( pos.getZ() >= 0 );
		    			
		    			assert( pos.getX() < getDimensions().getX() );
		    			assert( pos.getY() < getDimensions().getY() );
		    			assert( pos.getZ() < getDimensions().getZ() );
		    			setPnts.add(
		    				new Point3d(pos)
		    			);
		    		}
		    	}
	    	}
    	}
	}
	
	public Set<Point3d> getSetPnts() {
		return setPnts;
	}
	
	@Override
	public ImageDim getDimensions() {
		return dim;
	}
	
	
	@Override
	public void add(IGetMemoForIndex marksExisting, PxlMarkMemo newMark) throws UpdateMarkSetException {
		
		try {
			rmvPntsInMark( newMark );
		} catch (ExecuteException e) {
			throw new UpdateMarkSetException(e);
		}						
	}
	
	private void rmvPnt( Point3i crntExtntPnt,  Point3i crnrPnt ) {
		int xGlobal = crnrPnt.getX() + crntExtntPnt.getX();
		int yGlobal = crnrPnt.getY() + crntExtntPnt.getY();
		int zGlobal = crnrPnt.getZ() + crntExtntPnt.getZ();
		
		Point3d pntGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
		
		setPnts.remove( pntGlobal );
	}
	

	public void rmvPntsInMark(PxlMarkMemo newMark) throws ExecuteException {
		
		// We add any points in our new mark to the set
		PxlMark pxlMark = newMark.doOperation();
		
		Point3i crnrPnt = pxlMark.getBoundingBox( regionID ).getCrnrMin();
		
		RegionMembership rm = newMark.getRegionMap().membershipForIndex(regionID);
		byte flags = rm.flags();
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extnt();
		
		Point3i crntExtntPnt = new Point3i();
		for (crntExtntPnt.setZ(0); crntExtntPnt.getZ()<e.getZ(); crntExtntPnt.incrZ()) {
			
			ByteBuffer fb = voxelBox.getPixelsForPlane(crntExtntPnt.getZ());
			
			for (crntExtntPnt.setY(0); crntExtntPnt.getY()<e.getY(); crntExtntPnt.incrY()) {
				for (crntExtntPnt.setX(0); crntExtntPnt.getX()<e.getX(); crntExtntPnt.incrX()) {

					byte membership = fb.get( e.offset(crntExtntPnt.getX(), crntExtntPnt.getY()));
					
					if ( !rm.isMemberFlag(membership, flags) ) {
						rmvPnt( crntExtntPnt, crnrPnt );
					}
				}
			}
		}
	}
	
	
	@Override
	public void exchange(IGetMemoForIndex pxlMarkMemoList, PxlMarkMemo oldMark,
			int indexOldMark, PxlMarkMemo newMark) throws UpdateMarkSetException {
		
		try {
			addPntsInMark( pxlMarkMemoList, oldMark );
			rmvPntsInMark( newMark );
		} catch (ExecuteException e) {
			throw new UpdateMarkSetException(e);
		}
	}
	
	
	public void addPntsInMark(IGetMemoForIndex marksExisting, PxlMarkMemo markToAdd) throws ExecuteException {
		// We add any points in our new mark to the set, but only if there's not already a neighbour covering them
		
		// So our first step is to identify any overlapping marks
		List<PxlMarkMemo> neighbours = findNeighbours(marksExisting, markToAdd);
		
		PxlMark pxlMark = markToAdd.doOperation();
		
		Point3i crnrPnt = pxlMark.getBoundingBox(regionID).getCrnrMin();
		
		RegionMembership rm = markToAdd.getRegionMap().membershipForIndex(regionID);
		byte flags = rm.flags();
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extnt();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		
		VoxelBox<ByteBuffer> vbBinary = binaryImageChnl.getVoxelBox().asByte();
		
		Point3i crntExtntPnt = new Point3i();
		for (crntExtntPnt.setZ(0); crntExtntPnt.getZ()<e.getZ(); crntExtntPnt.incrZ()) {
			
			int zGlobal = crnrPnt.getZ() + crntExtntPnt.getZ();
			
			ByteBuffer buffer = voxelBox.getPixelsForPlane(crntExtntPnt.getZ());
			ByteBuffer bbBinaryImage = vbBinary.getPixelsForPlane(zGlobal).buffer();
			
			for (crntExtntPnt.setY(0); crntExtntPnt.getY()<e.getY(); crntExtntPnt.incrY()) {
				int yGlobal = crnrPnt.getY() + crntExtntPnt.getY();
				
				for (crntExtntPnt.setX(0); crntExtntPnt.getX()<e.getX(); crntExtntPnt.incrX()) {
					
					int xGlobal = crnrPnt.getX() + crntExtntPnt.getX();
			
					
					int globOffset = e.offset(xGlobal, yGlobal);
					byte posCheck = buffer.get( e.offset(crntExtntPnt.getX(), crntExtntPnt.getY()));
					if ( rm.isMemberFlag(posCheck, flags) && bbBinaryImage.get(globOffset)==bvb.getOnByte()) {
						
						Point3d pntGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
						
						// Now we check to make sure the point isn't contained in any of its neighbours
						if (!isPointInList(neighbours, pntGlobal)) {
							setPnts.add(pntGlobal);	
						}
					}
				}
			}
		}
	}
	
	

	private List<PxlMarkMemo> findNeighbours( IGetMemoForIndex all, PxlMarkMemo source) throws ExecuteException {
		
		ArrayList<PxlMarkMemo> list = new ArrayList<>();
			
		for (int i=0; i<all.size(); i++) {
			
			PxlMarkMemo pmm = all.getMemoForIndex(i);
			if (pmm!=source) {
				// We check if there's any overlap
				if (OverlapUtilities.overlapWith(source,pmm,regionID)>0) {
					list.add(pmm);
				}
				
			}
		}
		return list;
	}
	
	private static boolean isPointInList( List<PxlMarkMemo> all, Point3d point) {
		
		for( PxlMarkMemo memo : all ) {
			
			RegionMembership rm = memo.getRegionMap().membershipForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
			byte flags = rm.flags();
			
			byte membership = memo.getMark().evalPntInside(point);
			if (rm.isMemberFlag(membership, flags)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public void rmv(IGetMemoForIndex marksExisting, PxlMarkMemo mark) throws UpdateMarkSetException {
		try {
			addPntsInMark(marksExisting, mark);
		} catch (ExecuteException e) {
			throw new UpdateMarkSetException(e);
		}						
	}

	@Override
	public int size() {
		return setPnts.size();
	}

	public int getRegionID() {
		return regionID;
	}

	public void setRegionID(int regionID) {
		this.regionID = regionID;
	}

}
