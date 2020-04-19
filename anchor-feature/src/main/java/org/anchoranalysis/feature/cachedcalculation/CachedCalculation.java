package org.anchoranalysis.feature.cachedcalculation;

import org.anchoranalysis.core.cache.CachedOperation;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Caches the result of a calculation, until reset() is called
 * 
 * Implements an equivalence-relation via IParamsEquals.paramsEquals that checks
 * that the 'parameters' of a CachedCalculation are identical
 * 
 * This allows the user to search through a collection of CachedCalculation to
 *   find one with identical parameters and re-use it
 * 
 * The implementation of paramsEquals does a deep-search through fields
 *  of the class and does a equals() call.  All fields are considered apart
 *  from those marked 'transient'.
 * 
 * IMPORTANT NOTE: It is therefore important to make sure to put 'transient' on
 * all fields, which should not be equality-checked, when designing the classes.
 * 
 * @author Owen Feehan
 *
 * @param <S> result-type of the calculation
 * @param <T> params-type
 */
public abstract class CachedCalculation<S, T extends FeatureCalcParams> implements IResettableCachedCalculation {
	
	private transient T params;
	
	// We delegate the actualy execution of the cache
	private transient CachedOperation<S> delegate = new CachedOperation<S>() {

		@Override
		protected S execute() throws ExecuteException {
			return CachedCalculation.this.execute( params );
		}
	};
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param If there is no cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	synchronized S getOrCalculate( T params ) throws ExecuteException {
		
		// DEBUG
		// Checks we have the same params, if we call the cached calculation a second-time. This maybe catches errors.
		if (hasCachedCalculation()) {
			if (params!=null && !params.equals(this.params)) {
				throw new ExecuteException(
					new FeatureCalcException("This feature already has been used, its cache set is already set to different params")
				);
			}
		}
		
		initParams(params);
		return delegate.doOperation();
	}
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();	
	
	/*@Override
	public abstract CachedCalculation<S, T> duplicate();*/
	
	public void assignResult( Object savedResult) {
		@SuppressWarnings("unchecked")
		CachedCalculation<S,T> savedResultCached = (CachedCalculation<S,T>) savedResult;
		delegate.assignFrom( savedResultCached.delegate );
	}
	
	public boolean hasCachedCalculation() {
		return delegate.isDone();
	}

	@Override
	public synchronized void reset() {
		delegate.reset();
		this.params = null;	// Just to be clean, release memory, before the next getOrCalculate
	}
		
	protected abstract S execute( T params ) throws ExecuteException;
	
	@SuppressWarnings("unchecked")
	private synchronized void initParams(FeatureCalcParams params) {
		this.params = (T) params;
	}	
}
