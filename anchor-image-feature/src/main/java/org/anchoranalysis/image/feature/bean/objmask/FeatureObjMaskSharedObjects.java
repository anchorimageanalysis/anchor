package org.anchoranalysis.image.feature.bean.objmask;

/*
 * #%L
 * anchor-image-feature
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

import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.FeatureCastInitParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;
import org.anchoranalysis.image.feature.init.FeatureInitParamsSharedObjs;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParams;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParamsDescriptor;
import org.anchoranalysis.image.init.ImageInitParams;

/**
 * A feature that requires shared-objects during intialization
 * 
 * @author owen
 *
 */
public abstract class FeatureObjMaskSharedObjects extends FeatureCastInitParams<FeatureInitParamsSharedObjs, FeatureObjMaskParams> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private static class Initializer extends PropertyInitializer<FeatureInitParams> {

		public Initializer() {
			super( FeatureInitParams.class );
		}

		@Override
		public boolean execIfInheritsFrom(Object propertyValue, Object parent, LogErrorReporter logger)
				throws InitException {

			boolean succ = super.execIfInheritsFrom(propertyValue,parent, logger);
			
			if (succ) {
				return succ;
			}
			
			if (getParam() instanceof FeatureInitParamsSharedObjs) {
				if (initMatchingPropertiesWith(
					propertyValue,
					parent,
					logger,
					ImageInitParams.class,
					extractImageInitParams()
				)) {
					return true;
				}
			}
			
			return false;
		}
		
		private ImageInitParams extractImageInitParams() {
			FeatureInitParamsSharedObjs paramCast = (FeatureInitParamsSharedObjs) getParam();
			return paramCast.getSharedObjects();
		}
	}
	
	protected FeatureObjMaskSharedObjects() {
		super(FeatureInitParamsSharedObjs.class, new Initializer() );
	}

	@Override
	public FeatureParamsDescriptor paramType()
			throws FeatureCalcException {
		return FeatureObjMaskParamsDescriptor.instance;
	}


}
