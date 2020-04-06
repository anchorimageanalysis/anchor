package org.anchoranalysis.image.feature.bean.objmask;

/*-
 * #%L
 * anchor-image-feature
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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParams;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParamsDescriptor;

public abstract class FeatureObjMask extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public double calc( CacheableParams<? extends FeatureCalcParams> params ) throws FeatureCalcException {
		
		if (params.getParams() instanceof FeatureObjMaskParams) {
			
			FeatureObjMaskParams paramsCast = (FeatureObjMaskParams) params.getParams();
			return calcCast( params.changeParams(paramsCast) );
		} else {
			throw new FeatureCalcException("Requires " + FeatureObjMaskParams.class.getSimpleName() );
		}
	}
	
	// Calculates an NRG element for a set of pixels
	public abstract double calcCast( CacheableParams<FeatureObjMaskParams> params ) throws FeatureCalcException;

	@Override
	public CacheableParams<? extends FeatureCalcParams> transformParams(CacheableParams<? extends FeatureCalcParams> params, Feature dependentFeature) throws FeatureCalcException {
		
		if (params.getParams() instanceof FeatureObjMaskParams) {
			return transformParamsCast(
				params.changeParams( (FeatureObjMaskParams) params.getParams() ),
				dependentFeature
			);
		} else {
			throw new FeatureCalcException("Requires " + FeatureObjMaskParams.class.getSimpleName() );
		}
	}
	
	public CacheableParams<? extends FeatureCalcParams> transformParamsCast(CacheableParams<FeatureObjMaskParams> params, Feature dependentFeature) throws FeatureCalcException {
		return params;
	}
	
	@Override
	public FeatureParamsDescriptor paramType()
			throws FeatureCalcException {
		return FeatureObjMaskParamsDescriptor.instance;
	}
}
