package org.anchoranalysis.mpp.io.cfg.generator;

/*
 * #%L
 * anchor-overlay
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


import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterMaskFromOverlay;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Converts a configuration to a set of object masks, using a simple ObjMaskWriter for
//   all objects
public class SimpleOverlayWriter extends OverlayWriter {

	private ObjMaskWriter objMaskWriter;
	
	public SimpleOverlayWriter(ObjMaskWriter objMaskWriter ) {
		super();
		this.objMaskWriter = objMaskWriter;
	}

	@Override
	public void writePrecalculatedOverlays(
		List<PrecalcOverlay> precalculatedMasks,
		ColoredOverlayCollection overlays,
		ImageDim dim,
		RGBStack background,
		IDGetter<Overlay> idGetter,
		IDGetter<ObjectWithProperties> idGetterColor,
		BoundingBox bboxContainer
	) throws OperationFailedException {
		
		for( int i=0; i<precalculatedMasks.size(); i++ ) {
			
			PrecalcOverlay pre = precalculatedMasks.get(i);
			
			pre.writePrecalculatedMask(
				background,
				createMaskIDGetter(overlays, idGetter),
				idGetterColor,
				i,
				overlays.getColorList(),
				bboxContainer
			);
		}
		
		
	}

	@Override
	public void writeOverlaysIfIntersects(ColoredOverlayCollection oc,
			RGBStack stack, IDGetter<Overlay> idGetter, List<BoundingBox> intersectList)
			throws OperationFailedException {

		ImageDim dim = stack.getDimensions();
		ColoredOverlayCollection intersectOverlays = oc.subsetWhereBBoxIntersects(
			dim,
			this,
			intersectList
		);
		
		writeOverlays( intersectOverlays, stack, idGetter );
	}

	@Override
	public ObjMaskWriter getObjMaskWriter() {
		return objMaskWriter;
	}
	
	private static IDGetter<ObjectWithProperties> createMaskIDGetter( ColoredOverlayCollection oc, IDGetter<Overlay> idGetter) {
		return new IDGetterMaskFromOverlay( idGetter, oc, true );
	}

}
