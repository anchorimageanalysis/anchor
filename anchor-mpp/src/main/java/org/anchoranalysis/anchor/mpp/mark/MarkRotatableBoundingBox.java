package org.anchoranalysis.anchor.mpp.mark;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import static org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities.flagForNoRegion;
import static org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities.flagForRegion;
import static org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers.SUBMARK_INSIDE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;
import org.anchoranalysis.math.rotation.RotationMatrix;

/**
 * A two-dimensional bounding-box rotated at arbitrary angle in XY plane around a point.
 * 
 * <p>Axis-aligned bounding boxes are also supported by fixing orientation.</p>
 * 
 * @author Owen Feehan
 *
 */
public class MarkRotatableBoundingBox extends MarkAbstractPosition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static byte FLAG_SUBMARK_NONE = flagForNoRegion();
	private static byte FLAG_SUBMARK_REGION0 = flagForRegion( SUBMARK_INSIDE );
	
	// START mark state

	// Note that internally three-dimensional points are used instead of two-dimensional as it
	//  fits more nicely with the rotation matrices (at the cost of some extra computation).
	
	/** Add to orientation to get the left-point and bottom-point (without rotation) */ 
	private Point3d distToLeftBottom;
	
	/** Add to orientation to get the right-point and top-point (without rotation) */
	private Point3d distToRightTop;
	
	private Orientation2D orientation;
	// END mark state
	
	// START internal objects
	private RotationMatrix rotMatrix;
	private RotationMatrix rotMatrixInv;	// Inversion of rotMatrix
	// END internal objects
	
	public MarkRotatableBoundingBox() {
		this.update( new Point2d(0,0), new Point2d(0,0), new Orientation2D() );
	}
	
	@Override
	public byte evalPntInside(Point3d pt) {

		// See if after rotating a point back, it lies with on our box
		Point3d pnt = new Point3d(pt);
		pnt.subtract( getPos() );
		
		Point3d pntRot = rotMatrixInv.calcRotatedPoint(pnt);
		
		if (pntRot.getX() < distToLeftBottom.getX() || pntRot.getX() >= distToRightTop.getX()) {
			return FLAG_SUBMARK_NONE;
		}
		
		if (pntRot.getY() < distToLeftBottom.getY() || pntRot.getY() >= distToRightTop.getY()) {
			return FLAG_SUBMARK_NONE;
		}

		return FLAG_SUBMARK_REGION0;
	}
	
	public void update( Point2d distToLeftBottom, Point2d distToRightTop, Orientation2D orientation ) {

		update(
			convert3d(distToLeftBottom),
			convert3d(distToRightTop),
			orientation
		);
	}
		
	/** Internal version with Point3d */
	private void update( Point3d distToLeftBottom, Point3d distToRightTop, Orientation2D orientation ) {
		this.distToLeftBottom = distToLeftBottom;
		this.distToRightTop = distToRightTop;
		this.orientation = orientation;
		
		this.rotMatrix = orientation.createRotationMatrix();
		this.rotMatrixInv = rotMatrix.transpose();
	}

	@Override
	public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
		
		Point3d[] points = new Point3d[] {
			cornerPoint(false, false),
			cornerPoint(true, false),
			cornerPoint(false, true),
			cornerPoint(true, true)
		};
		
		try {
			BoundingBox box = BoundingBoxFromPoints.forList(
				rotateAddPos(points)
			);
			return box.clipTo(bndScene.getExtnt());
		} catch (OperationFailedException e) {
			throw new AnchorImpossibleSituationException();
		}
	}
	
	@Override
	public BoundingBox bbox(ImageDimensions bndScene, int regionID) {
		return bboxAllRegions(bndScene);
	}
	
	@Override
	public double volume(int regionID) {
		// The volume is invariant to rotation
		double width = distToRightTop.getX() - distToLeftBottom.getX();
		double height = distToRightTop.getY() - distToLeftBottom.getY();
		return width * height;
	}

	@Override
	public Mark duplicate() {
		MarkRotatableBoundingBox out = new MarkRotatableBoundingBox();
		out.update(
			distToLeftBottom,
			distToRightTop,
			orientation.duplicate()
		);
		return out;
	}

	@Override
	public int numRegions() {
		return 1;
	}

	@Override
	public String getName() {
		return "rotatableBoundingBox";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int numDims() {
		return 2;
	}
	
	private static Point3d convert3d( Point2d pnt ) {
		return new Point3d(pnt.getX(), pnt.getY(), 0);
	}

	private Point3d cornerPoint( boolean x, boolean y ) {
		return new Point3d(
			x ? distToLeftBottom.getX() : distToRightTop.getX(),
			y ? distToLeftBottom.getY() : distToRightTop.getY(),
			0
		);
	}
	
	private List<Point3i> rotateAddPos( Point3d[] points ) {
		return Arrays.stream(points).map(
			pnt -> PointConverter.intFromDouble( rotateAddPos(pnt) )	
		).collect( Collectors.toList() );
	}
		
	/** Rotates a position and adds the current position afterwards */
	private Point3d rotateAddPos( Point3d pnt ) {
		Point3d out = rotMatrix.calcRotatedPoint(pnt);
		out.add( getPos() );
		return out;
	}
}
