package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class MarkPolygonCurve extends MarkAbstractPointList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2043844259526872933L;

	private static byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
	private static byte FLAG_SUBMARK_INSIDE = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_INSIDE );
	
	// Applied the same in all 3 dimensions, maybe we need to change this
	private double distThresh = 0.7;
	
	private transient DistCalcToLine dctl = new DistCalcToLine();
	
	@Override
	public byte evalPntInside(Point3d pnt) {
	
		if (distToPolygonLocal(pnt)<distThresh) {
			return FLAG_SUBMARK_INSIDE;
		}
		return FLAG_SUBMARK_NONE;
	}

	private double distToPolygonSegmentLocal( Point3d pnt, Point3d pntFirst, Point3d pntSecond ) {
		
		if (pnt.getX()<(pntFirst.getX()-distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (pnt.getY()<(pntFirst.getY()-distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (pnt.getZ()<(pntFirst.getZ()-distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (pnt.getX()>(pntSecond.getX()+distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (pnt.getY()>(pntSecond.getY()+distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (pnt.getZ()>(pntSecond.getZ()+distThresh)) {
			return Double.POSITIVE_INFINITY;
		}
		
		dctl.setPoints(pntFirst, pntSecond);
		
		return dctl.distToLine(pnt);
	}
	
	// Distance to polygon - only local (i.e. assumes that we only care about returning small values in circumstances
	//  very close to the line segment in question, otherwise we don't care
	private double distToPolygonLocal( Point3d pnt ) {
		
		// If a point is inside the bounding box of two points +- the distThresh , we calculate the distance to
		// it.  And we take the minimum overall
		
		double min = Double.POSITIVE_INFINITY;
		
		int numPtsMinus1 = getPoints().size() - 1;
		for (int i=0; i<numPtsMinus1; i++) {
			Point3d pntFirst = getPoints().get(i);
			Point3d pntSecond = getPoints().get(i+1);
			
			double dist = distToPolygonSegmentLocal(pnt, pntFirst,pntSecond);
			min = Math.min( min, dist );
		}
		
		return min;
	}
	
	
	@Override
	public Mark duplicate() {
		MarkPolygonCurve out = new MarkPolygonCurve();
		doDuplicate(out);
		return out;
	}

	@Override
	public double volume( int regionID ) {
		// What does mean really?
		return getPoints().size();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void scale(double multFactor) throws OptionalOperationUnsupportedException {
		throw new OptionalOperationUnsupportedException("Not supported");
	}

	@Override
	public int numDims() {
		return 2;
	}

	@Override
	public Point3d centerPoint() {
		// We take the mean of the BBOX as it's not really well defined. We probably should take the COG.
		return bbox().midpoint();
	}

	@Override
	public String getName() {
		return "markPolygonCurve";
	}

	@Override
	public int numRegions() {
		return 1;
	}
	
	@Override
	public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
		return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_INSIDE);
	}
}
