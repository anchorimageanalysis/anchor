package org.anchoranalysis.image.feature.init;

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

import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.image.init.ImageInitParams;

/**
 * Extension of typical feature initialization, that also sets shared objects
 * 
 * @author owen
 *
 */
public class FeatureInitParamsSharedObjs extends FeatureInitParams {

	private ImageInitParams sharedObjects;
	
	public FeatureInitParamsSharedObjs( ImageInitParams so ) {
		super();
		this.sharedObjects = so;
	}
	
	private FeatureInitParamsSharedObjs( FeatureInitParams parent, ImageInitParams so ) {
		super(parent);
		this.sharedObjects = so;
	}

	public ImageInitParams getSharedObjects() {
		return sharedObjects;
	}

	@Override
	public FeatureInitParams duplicate() {
		return new FeatureInitParamsSharedObjs(super.duplicate(), sharedObjects);
	}
	
	
}
