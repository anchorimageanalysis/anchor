package org.anchoranalysis.feature.cache.calculation;

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


import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.input.FeatureInput;

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
 * @param <T> feature input-type
 * @param <E> exception thrown if something goes wrong in the calculation
 */
public abstract class CacheableCalculation<S, T extends FeatureInput, E extends Exception> implements ResettableCalculation {
	
	private T input;
	
	// We delegate the actualy execution of the cache
	private CachedOperation<S,E> delegate = new CachedOperation<S,E>() {

		@Override
		protected S execute() throws E {
			return CacheableCalculation.this.execute( input );
		}
	};
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * @param input If there is no cached-value, and the calculation occurs, this input is used. Otherwise ignored.
	 * 
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	synchronized S getOrCalculate( T input ) throws E {
		
		// Checks we have the same params, if we call the cached calculation a second-time. This maybe catches errors.
		// We only do this when asserts are enabled, as its expensive.
		assert( checkParamsMatchesInput(input) );
		
		initParams(input);
		return delegate.doOperation();
	}
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();	
	
	public boolean hasCachedCalculation() {
		return delegate.isDone();
	}

	@Override
	public synchronized void invalidate() {
		delegate.reset();
		this.input = null;	// Just to be clean, release memory, before the next getOrCalculate
	}
	
	/** This performs the actual calculation when needed. It should only be called once, until invalidate() is called. */
	protected abstract S execute( T input ) throws E;
	
	private synchronized void initParams(T input) {
		this.input = input;
	}
	
	/** A check that if params are already set, any new inputs must be identical */
	private boolean checkParamsMatchesInput( T input ) {
		if (hasCachedCalculation()) {
			if (input!=null && !input.equals(this.input)) {
				throw new AnchorFriendlyRuntimeException("This feature already has been used, its cache is already set to different params");
			}
		}
		return true;
	}

}
