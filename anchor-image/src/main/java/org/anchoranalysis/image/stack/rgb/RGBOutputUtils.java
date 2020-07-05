package org.anchoranalysis.image.stack.rgb;

/*
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageDimensions;

public class RGBOutputUtils {
	
	private RGBOutputUtils() {}
	
	public static void writeRGBColorToByteArr( RGBColor c, Point3i pnt, ImageDimensions sd, ByteBuffer red, ByteBuffer blue, ByteBuffer green ) {
		int index = sd.offset(pnt.getX(), pnt.getY(), 0);
		red.put(index, (byte) c.getRed());
		green.put(index, (byte) c.getGreen());
		blue.put(index, (byte) c.getBlue());
		
	}
}
