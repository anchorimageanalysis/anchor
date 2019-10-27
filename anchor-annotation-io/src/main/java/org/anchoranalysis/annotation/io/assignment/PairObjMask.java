package org.anchoranalysis.annotation.io.assignment;

/*-
 * #%L
 * anchor-annotation-io
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

import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;

class PairObjMask {
	private ObjMask left;
	private ObjMask right;
	private double overlapRatio;

	public PairObjMask(ObjMask left, ObjMask right, double overlapRatio) {
		super();
		this.left = left;
		this.right = right;
		this.overlapRatio = overlapRatio;
	}

	public boolean atBorderXY( ImageDim sd ) {
		return left.getBoundingBox().atBorderXY(sd) || right.getBoundingBox().atBorderXY(sd); 
	}
	
	public ObjMask getMultiplex( boolean left ) {
		if (left) {
			return getLeft();
		} else {
			return getRight();
		}
	}
	
	public ObjMask getLeft() {
		return left;
	}

	public ObjMask getRight() {
		return right;
	}
	
	public double getOverlapRatio() {
		return overlapRatio;
	}

}
