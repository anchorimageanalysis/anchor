package org.anchoranalysis.image.voxel.kernel.count;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

/**
 * For every voxel on the outline, count ALL neighbours that are adjacent, but ignoring any outside the scene.
 * 
 * Neighbouring voxels can be counted more than once.
 * 
 * @author Owen Feehan
 *
 */
public class CountKernelNghbIgnoreOutsideScene extends CountKernelNghbBase {

	private Extent extentScene;
	private ReadableTuple3i addPoint;
	
	public CountKernelNghbIgnoreOutsideScene(
		boolean useZ,
		BinaryValuesByte bv,
		boolean multipleMatchesPerVoxel,
		Extent extentScene,		// The entire extent of the scene
		ReadableTuple3i addPoint			// Added to a point before determining if it is inside or outside the scene.
	) {
		super(useZ, bv, multipleMatchesPerVoxel);
		this.extentScene = extentScene;
		this.addPoint = addPoint;
	}

	@Override
	protected boolean isNghbVoxelAccepted(Point3i point, int xShift, int yShift,
			int zShift, Extent extent) {
		
		int xNew = point.getX()+xShift+addPoint.getX();
		int yNew = point.getY()+yShift+addPoint.getY();
		int zNew = point.getZ()+zShift+addPoint.getZ();
		
		return extentScene.contains(xNew,yNew,zNew);
	}

}
