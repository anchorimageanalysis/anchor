package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Like a {@link CacheableCalculation} but has been resolved against a cache to ensure its unique (singular).
 * 
 * <p>This operation should always occur before a cached-calculation is used</p>
 * 
 * @author Owen Feehan
 *
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 */
public class ResolvedCalculation<S, T extends FeatureInput> {

	private CacheableCalculation<S, T> calc;

	/**
	 * Constructor
	 * 
	 * @param calc the cacheable-calculation that is now considered resolved
	 */
	public ResolvedCalculation(CacheableCalculation<S, T> calc) {
		super();
		this.calc = calc;
	}
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param params If there is no existing cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	public S getOrCalculate( T params) throws ExecuteException {
		return calc.getOrCalculate(params);
	}
	
	// We delegate to the CachedCalculation to check equality. Needed for the search.
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResolvedCalculation) {
			return ((ResolvedCalculation<S, T>) obj).calc.equals(calc);
		} else {
			return false;
		}
	}

	// We delegate to the CachedCalculation to check hashCode. Needed for the search.
	@Override
	public int hashCode() {
		return calc.hashCode();
	}
}
