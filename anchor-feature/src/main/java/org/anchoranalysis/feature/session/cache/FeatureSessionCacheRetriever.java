package org.anchoranalysis.feature.session.cache;

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


import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;


/**
 * Retrieves items from a FeatureSessionCache
 * 
 * @author Owen Feehan
 * @params feature calc-params that the cache supports
 */
public abstract class FeatureSessionCacheRetriever<T extends FeatureCalcParams> implements ICachedCalculationSearch {
	
	/**
	 * Has the cache been inited()
	 * 
	 * @return
	 */
	public abstract boolean hasBeenInit();
	
	/**
	 * Calculate a feature with particular values
	 * 
	 * @param feature feature
	 * @param params params
	 * @return the feature-value
	 * @throws FeatureCalcException
	 */
	public abstract double calc( Feature<T> feature, CacheableParams<T> params ) throws FeatureCalcException;
	
	/**
	 * Calculates a feature-list throwing an exception if there is an error
	 * 
	 * @param features list of features
	 * @param params params
	 * @return the results of each feature, with Double.NaN (and the stored exception) if an error occurs
	 * @throws FeatureCalcException 
	 */
	public ResultsVector calc( List<Feature<T>> features, CacheableParams<T> params ) throws FeatureCalcException {
		ResultsVector out = new ResultsVector(features.size());
		for( int i=0; i<features.size(); i++ ) {
			
			Feature<T> f = features.get(i);
			
			try {
				double val = calc( f, params );
				out.set(i, val);
			} catch (FeatureCalcException e) {
			
				throw new FeatureCalcException(
					String.format("Feature '%s' has thrown an error\n", f.getFriendlyName()),
					e
				);
			}
			
		}
		return out;
	}
	
	/**
	 * Calculates a feature-list suppressing errors
	 * 
	 * @param features list of features
	 * @param params params
	 * @return the results of each feature, with Double.NaN (and the stored exception) if an error occurs
	 */
	public ResultsVector calcSuppressErrors( List<Feature<T>> features, CacheableParams<T> params ) {
		ResultsVector out = new ResultsVector(features.size());
		for( int i=0; i<features.size(); i++ ) {
			
			Feature<T> f = features.get(i);
			
			try {
				double val = calc( f, params );
				out.set(i, val);
			} catch (FeatureCalcException e) {
				out.setError(i, e);
			}
			
		}
		return out;
	}
	


	/**
	 * Duplicates the SharedFeatureList associated with this retriever
	 * @return
	 */
	public abstract SharedFeatureSet<T> getSharedFeatureList();
	
	
	/**
	 * Due to scoping (different prefixes that can exist), an ID needs to be resolved
	 *  to a unique-string before it can be passed to calcFeatureByID
	 * 
	 * @param id
	 * @return
	 */
	public abstract String resolveFeatureID( String id );
	
	/**
	 * Searches for a feature that matches a particular ID
	 * 
	 * @param resolvedID
	 * @param params TODO
	 * @throws GetOperationFailedException 
	 */
	public abstract double calcFeatureByID( String resolvedID, CacheableParams<T> params ) throws FeatureCalcException;
	
	/**
	 * Debug method that describes the current caches
	 * @return
	 */
	public abstract String describeCaches();
	
	
	/**
	 * Creates a new cache, that can be used to store items
	 * 
	 * @return
	 * @throws CreateException
	 */
	public abstract FeatureSessionCache createNewCache();
	
	
}
