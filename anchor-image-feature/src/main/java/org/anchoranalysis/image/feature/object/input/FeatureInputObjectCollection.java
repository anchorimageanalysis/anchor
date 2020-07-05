package org.anchoranalysis.image.feature.object.input;

/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectCollection;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class FeatureInputObjectCollection extends FeatureInputNRG {

	private ObjectCollection objMaskCollection;
	
	public FeatureInputObjectCollection(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
	
	public FeatureInputObjectCollection(ObjectCollection objMaskCollection, Optional<NRGStackWithParams> nrgStack) {
		super(nrgStack);
		this.objMaskCollection = objMaskCollection;
	}

	public ObjectCollection getObjMaskCollection() {
		return objMaskCollection;
	}

	public void setObjMaskCollection(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
}
