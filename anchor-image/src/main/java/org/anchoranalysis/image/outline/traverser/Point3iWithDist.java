package org.anchoranalysis.image.outline.traverser;

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

class Point3iWithDist {

	private Point3i point;
	private int dist;
	
	// If non-null, this is a point that is a neighbour but
	//   is disallowed from being on the same contiguous path
	private Point3i connPnt = null;
	
	public Point3iWithDist(Point3i point, int dist) {
		super();
		this.point = point;
		this.dist = dist;
	}

	public Point3i getPoint() {
		return point;
	}

	public int getDist() {
		return dist;
	}
	
	@Override
	public String toString() {
		return String.format("%s--%d",point.toString(),dist);
	}

	public boolean isForceNewPath() {
		return connPnt!=null;
	}

	public void markAsNewPath(Point3i connPnt ) {
		this.connPnt = connPnt;
	}

	public Point3i getConnPnt() {
		return connPnt;
	}
	
	
}
