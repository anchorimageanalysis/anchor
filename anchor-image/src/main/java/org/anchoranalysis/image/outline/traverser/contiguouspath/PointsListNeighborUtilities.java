package org.anchoranalysis.image.outline.traverser.contiguouspath;

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

import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class PointsListNeighborUtilities {
	
	/** Are all points in a list neighboring the next point in the list? */
	public static boolean areAllPointsInBigNeighborhood( List<Point3i> list ) {
		for( int i=0; i<list.size(); i++) {
			if (i!=0) {
				
				Point3i first = list.get(i-1);
				Point3i second = list.get(i);
					
				if(!arePointsNeighbors(first, second)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/** Makes sure no successive neighbors are equal */
	public static boolean areNeighborsDistinct( List<Point3i> list ) {
		for( int i=0; i<list.size(); i++) {
			if (i!=0) {
				
				Point3i first = list.get(i-1);
				Point3i second = list.get(i);
				
				if (first.equals(second)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean arePointsNeighbors( Point3i point1, Point3i point2 ) {
		if (point1.equals(point2)) {
			return false;
		}
		if (distanceSingleDim(point1.getX(), point2.getX())) {
			return false;
		}
		if (distanceSingleDim(point1.getY(), point2.getY())) {
			return false;
		}
		return (!distanceSingleDim(point1.getZ(), point2.getZ()));
	}
	
	private static boolean distanceSingleDim( int x, int y ) {
		return Math.abs( x - y ) > 1;
	}
}
