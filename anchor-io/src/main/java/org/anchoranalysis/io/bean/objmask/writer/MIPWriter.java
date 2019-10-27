package org.anchoranalysis.io.bean.objmask.writer;

/*
 * #%L
 * anchor-io
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



import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import ch.ethz.biol.cell.imageprocessing.io.objmask.ObjMaskWriter;

// Note doesn't cache the underlying maskWriter
public class MIPWriter extends ObjMaskWriter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 689470597161517904L;
	
	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskWriter maskWriter;
	// END BEAN PROPERTIES
	
	public MIPWriter() {
		
	}
	
	public MIPWriter(ObjMaskWriter maskWriter) {
		super();
		this.maskWriter = maskWriter;
	}

	private ObjMaskWithProperties createMIPMask( ObjMaskWithProperties mask ) {

		// Now we manipulate the mask
		ObjMaskWithProperties copyMask = mask.duplicate();
		copyMask.convertToMaxIntensityProjection();
		return copyMask;
	}
	
	@Override
	public ObjMaskWithProperties precalculate(ObjMaskWithProperties mask, ImageDim dim)
			throws CreateException {
		return createMIPMask(mask);
	}

	@Override
	public void writePrecalculatedMask(
			ObjMaskWithProperties maskOrig,
			Object precalculatedObj,
			RGBStack stack, IDGetter<ObjMaskWithProperties> idGetter,
			IDGetter<ObjMaskWithProperties> colorIDGetter,
			int iter, ColorIndex colorIndex,
			BoundingBox bboxContainer)
			throws OperationFailedException {
		maskWriter.writeSingle( (ObjMaskWithProperties) precalculatedObj, stack, idGetter, colorIDGetter, iter, colorIndex, bboxContainer);
	}
	
	public ObjMaskWriter getMaskWriter() {
		return maskWriter;
	}

	public void setMaskWriter(ObjMaskWriter maskWriter) {
		this.maskWriter = maskWriter;
	}
}
