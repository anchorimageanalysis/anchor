package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-image-io
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


import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.objectmask.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBObjMaskNoBackgroundGenerator extends RGBObjMaskGeneratorBase {
	
	private ChannelFactorySingleType factory = new ChannelFactoryByte();
	private ImageDim backgroundDim;
	
	public RGBObjMaskNoBackgroundGenerator(ObjMaskWriter objMaskWriter,	ImageDim background, ColorIndex colorIndex) {
		this(objMaskWriter, background, colorIndex, new IDGetterIter<ObjectWithProperties>(), new IDGetterIter<ObjectWithProperties>() );
	}
	
	public RGBObjMaskNoBackgroundGenerator(ObjMaskWriter objMaskWriter,	ImageDim background, ColorIndex colorIndex, IDGetter<ObjectWithProperties> idGetter, IDGetter<ObjectWithProperties> colorIDGetter) {
		super(objMaskWriter, colorIndex, idGetter, colorIDGetter);
	}

	public RGBObjMaskNoBackgroundGenerator(ObjMaskWriter objMaskWriter,	ObjectCollectionWithProperties masks, ImageDim background, ColorIndex colorIndex) {
		this(objMaskWriter, masks, background, colorIndex, new IDGetterIter<ObjectWithProperties>(), new IDGetterIter<ObjectWithProperties>() );
	}
	
	public RGBObjMaskNoBackgroundGenerator(ObjMaskWriter objMaskWriter,
			ObjectCollectionWithProperties masks, ImageDim background, ColorIndex colorIndex, IDGetter<ObjectWithProperties> idGetter, IDGetter<ObjectWithProperties> colorIDGetter) {
		super(objMaskWriter, colorIndex, idGetter, colorIDGetter);
		this.setIterableElement(masks);
		this.backgroundDim = background;
	}

	@Override
	protected RGBStack generateBackground() throws CreateException {
		DisplayStack background = DisplayStack.create(
			factory.createEmptyInitialised(backgroundDim)
		);
		return ConvertDisplayStackToRGB.convert(background);
	}

	@Override
	protected ObjectCollectionWithProperties generateMasks() throws CreateException {
		return getIterableElement();
	}
}
