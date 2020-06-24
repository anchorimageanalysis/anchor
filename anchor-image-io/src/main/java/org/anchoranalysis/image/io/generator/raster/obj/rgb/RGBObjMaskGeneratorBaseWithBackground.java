package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;

/*-
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;

public abstract class RGBObjMaskGeneratorBaseWithBackground extends RGBObjMaskGeneratorBase {

	private DisplayStack background;
		
	protected RGBObjMaskGeneratorBaseWithBackground(ObjMaskWriter objMaskWriter, ColorIndex colorIndex,
			IDGetter<ObjectWithProperties> idGetter, IDGetter<ObjectWithProperties> colorIDGetter, DisplayStack background) {
		super(objMaskWriter, colorIndex, idGetter, colorIDGetter);
		this.background = background;
	}
	
	protected DisplayStack getBackground() {
		return background;
	}

	public void setBackground(DisplayStack background) {
		this.background = background;
	}
}
