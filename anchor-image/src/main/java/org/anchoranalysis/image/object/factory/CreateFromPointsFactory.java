package org.anchoranalysis.image.object.factory;

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

import java.nio.ByteBuffer;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CreateFromPointsFactory {
	
	public static ObjectMask create( List<Point3i> pnts ) throws CreateException {
		
		BoundingBox bbox;
		try {
			bbox = BoundingBoxFromPoints.forList(pnts);
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
		
		ObjectMask mask = new ObjectMask(bbox);
		
		byte maskOn = mask.getBinaryValuesByte().getOnByte();
		
		for (Point3i p : pnts) {
			
			int x = p.getX() - bbox.cornerMin().getX();
			int y = p.getY() - bbox.cornerMin().getY();
			int z = p.getZ() - bbox.cornerMin().getZ();
			
			ByteBuffer buffer = mask.getVoxelBox().getPixelsForPlane(z).buffer();
			buffer.put( mask.getBoundingBox().extent().offset(x,y), maskOn );
		}
		
		return mask;
	}
}
