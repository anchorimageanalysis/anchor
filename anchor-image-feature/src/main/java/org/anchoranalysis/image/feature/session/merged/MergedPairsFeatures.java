package org.anchoranalysis.image.feature.session.merged;

import org.anchoranalysis.core.error.InitException;


/*-
 * #%L
 * anchor-plugin-mpp-experiment
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

import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * The list of features that can be used in a {@link FeatureCalculatorMergedPairs}
 * 
 * @author Owen Feehan
 *
 */
public class MergedPairsFeatures {

	private FeatureList<FeatureInputStack> listImage;

	private FeatureList<FeatureInputSingleObject> listSingle;
	private FeatureList<FeatureInputPairObjects> listPair;
	
	/**
	 * Constructor - only calculate pair features
	 * 
	 * @param listPair features for a pair of objects
	 */
	public MergedPairsFeatures(FeatureList<FeatureInputPairObjects> listPair) {
		this(
			FeatureListFactory.empty(),
			FeatureListFactory.empty(),
			listPair
		);
	}	
	
	/**
	 * Constructor
	 * 
	 * @param listImage features for image as a whole
	 * @param listSingle features for single-objects
	 * @param listPair features for a pair of objects
	 */
	public MergedPairsFeatures(FeatureList<FeatureInputStack> listImage, FeatureList<FeatureInputSingleObject> listSingle,
			FeatureList<FeatureInputPairObjects> listPair) {
		super();
		this.listImage = listImage;
		this.listSingle = listSingle;
		this.listPair = listPair;
	}	
	
	public FeatureList<FeatureInputStack> getImage() {
		return listImage;
	}
	public FeatureList<FeatureInputSingleObject> getSingle() {
		return listSingle;
	}
	public FeatureList<FeatureInputPairObjects> getPair() {
		return listPair;
	}
	
	/** Immutably creates entirely new duplicated features */
	public MergedPairsFeatures duplicate() {
		return new MergedPairsFeatures(
			listImage.duplicateBean(),
			listSingle.duplicateBean(),
			listPair.duplicateBean()
		);
	}
	
	public int numImageFeatures() {
		return listImage.size();
	}
	
	public int numSingleFeatures() {
		return listSingle.size();
	}
	
	public int numPairFeatures() {
		return listPair.size();
	}
	
	
	public FeatureCalculatorMulti<FeatureInputStack> createImageSession(
		CreateCalculatorHelper cc,
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputStack,? extends ReplaceStrategy<FeatureInputStack>> cachingStrategy,
		boolean suppressErrors
	) throws InitException {
		return cc.createCached(
			getImage(),
			soImage,
			cachingStrategy,
			suppressErrors
		);
	}
	
	public FeatureCalculatorMulti<FeatureInputSingleObject> createSingle(
		CreateCalculatorHelper cc,
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputSingleObject,CacheAndReuseStrategy<FeatureInputSingleObject>> cachingStrategy,
		boolean suppressErrors
	) throws InitException {
		return cc.createCached(
			getSingle(),
			soImage,
			cachingStrategy,
			suppressErrors
		);
	}
	
	public FeatureCalculatorMulti<FeatureInputPairObjects> createPair(
		CreateCalculatorHelper cc,
		ImageInitParams soImage,
		CacheTransferSourceCollection cacheTransferSource
	) throws InitException {
		// TODO fix no shared features anymore, prev sharedFeatures.duplicate()
		return cc.createPair(
			getPair(),
			soImage,
			cacheTransferSource
		);

	}
}
