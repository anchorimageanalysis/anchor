package org.anchoranalysis.image.feature.bean;

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

import java.util.Arrays;
import java.util.List;

import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.feature.bean.FeatureCastInitParams;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.init.FeatureInitParamsShared;


/**
 * A feature that depends on SharedObjects being passed during the parameter-initialization
 * 
 * @author Owen Feehan
 *
 * @param <S> init-params params type
 * @param <T> feature input type
 */
public abstract class FeatureShared<T extends FeatureInput> extends FeatureCastInitParams<FeatureInitParamsShared, T> {

	protected FeatureShared() {
		super(
			FeatureInitParamsShared.class,
			new PropertyInitializer<>(	FeatureInitParams.class, paramExtracters() )
		);
	}
	
	private static List<ExtractFromParam<FeatureInitParams,?>> paramExtracters() {
		return Arrays.asList(
			new ExtractFromParam<>(
				ImageInitParams.class,
				params -> extractImageInitParams(params),	// NOSONAR
				FeatureInitParamsShared.class
			)
		);
	}
		
	private static ImageInitParams extractImageInitParams(FeatureInitParams params) {
		FeatureInitParamsShared paramCast = (FeatureInitParamsShared) params;
		return paramCast.getSharedObjects();
	}
}
