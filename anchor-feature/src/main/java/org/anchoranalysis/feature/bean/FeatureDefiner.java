package org.anchoranalysis.feature.bean;

/*
 * #%L
 * anchor-feature
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

import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.init.FeatureInitParams;

class FeatureDefiner<T extends FeatureInput> extends PropertyDefiner<FeatureInitParams> {

	public FeatureDefiner() {
	}

	@Override
	public boolean accepts( Class<?> paramType ) {
		return FeatureInitParams.class.isAssignableFrom(paramType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doInitFor(Object propertyValue, Object params, Object parent, LogErrorReporter logger) throws InitException {
		
		if (parent!=null) {
			if (!(parent instanceof FeatureBase)) {
				throw new InitException("A feature may only have another FeatureBase as a bean-parent");
			}
		}

		if (propertyValue instanceof Feature) {
			Feature<T> propertyValueCast = (Feature<T>) propertyValue;
			propertyValueCast.init(
				(FeatureInitParams) params,
				(FeatureBase<T>) parent,
				logger
			);
		}
	}

	@Override
	public String describeAcceptedClasses() {
		return FeatureInitParams.class.getSimpleName();
	}
	
}