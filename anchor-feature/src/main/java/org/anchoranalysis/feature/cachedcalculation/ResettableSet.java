package org.anchoranalysis.feature.cachedcalculation;

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


import java.util.HashMap;
import java.util.Map;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;

public class ResettableSet<T extends IResettableCachedCalculation> {

	/**
	 * A map for finding identical objects
	 */
	private Map<T,T> map = new HashMap<>();

	/**
	 * Do we log cache events or not?
	 */
	private boolean doLogging = false;
	
	public ResettableSet(boolean doLogging) {
		super();
		this.doLogging = doLogging;
	}

	/**
	 * Finds an existing object if its parameters match, otherwise adds target
	 * to the list.
	 * 
	 * @param target
	 * @param logger if non-NULL logging messages are written out indicating if the object was added or not
	 * @return an existing CachedCalculation if found, otherwise target if added
	 */
	public T findOrAdd( T target, LogErrorReporter logger ) {
		assert(target!=null);
		
		T existing = map.get(target);
		
		if (existing==null) {
			
			if (doLogging && logger!=null) {
				logger.getLogReporter().logFormatted("Cache-addding: %s (%d)", target, target.hashCode() );
			}
			
			map.put(target, target);
			
			return target;
			
		} else {
			
			// Reusing an existing item
			if (doLogging && logger!=null) {
				logger.getLogReporter().logFormatted("Cache-reusing: %s (%d)", existing, existing.hashCode() );
			}
			
			return existing;
		}
		
	}
	
	public void reset() {
		for (T cachedCalculation : map.values()) {
			cachedCalculation.reset();
		}
	}
	
	public void assignResult( ResettableSet<T> list ) throws OperationFailedException {
		
		//System.out.printf("Assigning %d and %d\n", size(), list.size() );
		//assert( delegate.size() == list.delegate.size() );
		
		for( T key : list.map.keySet() ) {
			
			// We find our existing key, and we transfer. If we don't have an existing key, we ignore
			T existing = map.get(key);
			if (existing!=null) {
				existing.assignResult(key);  // As the key is guaranteed to be the same as the ite
			} else {
				// We just ignore if it's not there already
				//assert false;
				//delegate.put(key, key);
			}
		}
		
//		for( int i=0; i<delegate.size(); i++ ) {
//			delegate.get(i).assignResult( list.delegate.get(i) );
//		}
	}
	
/*	@SuppressWarnings("unchecked")
	public ResettableSet<T> duplicate() {
		ResettableSet<T> out = new ResettableSet<>(doLogging);
		
		for( T key : map.keySet() ) {
			// As we know the keys are same
			T item = (T) key.duplicate();
			out.map.put(key, item);
		}

		return out;
	}*/

	public int size() {
		return map.size();
	}
	
	public String describe() {
		StringBuilder sb = new StringBuilder();
		for( T key  : map.keySet() ) {
			T item = map.get(key);
			sb.append( System.identityHashCode(item) + ": " + item.toString() );
			sb.append("\n");
		}
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return describe();
	}
	
	
	
}