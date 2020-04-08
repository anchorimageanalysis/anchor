package org.anchoranalysis.feature.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.ICachedCalculationSearch;

/**
 * Wraps a params with a structure for adding cachable objects
 * 
 * @param T feature-calc-params
 * 
 * @author owen
 *
 */
public class CacheableParams<T extends FeatureCalcParams> implements ICachedCalculationSearch {

	private FeatureSessionCacheRetriever<T> cache;
	private Map<String, FeatureSessionCacheRetriever<FeatureCalcParams>> children = new HashMap<>();
	
	private T params;
	private CacheCreator factory;
	
	public CacheableParams(T params, CacheCreator factory) {
		this.params = params;
		this.factory = factory;
		this.cache = factory.create( params.getClass() );
	}
	
	private CacheableParams(T params, FeatureSessionCacheRetriever<T> cache, CacheCreator factory) {
		this.params = params;
		this.cache = cache;
		this.factory = factory;
	}
	
	@SuppressWarnings("unchecked")
	public <S extends FeatureCalcParams> FeatureSessionCacheRetriever<S> cacheFor(String childName, Class<?> paramsType) {
		return (FeatureSessionCacheRetriever<S>) children.computeIfAbsent(
			childName,
			s -> factory.create(paramsType)
		);
	}

	public T getParams() {
		return params;
	}

	public <S> CachedCalculation<S> search(CachedCalculation<S> cc) {
		return cache.search(cc);
	}
	
	@Override
	public <S, U> CachedCalculationMap<S, U> search(CachedCalculationMap<S, U> cc) {
		return cache.search(cc);
	}
	
	public double calc(Feature<T> feature)
			throws FeatureCalcException {
		return cache.calc(feature, this);
	}

	public ResultsVector calc(List<Feature<T>> features)
			throws FeatureCalcException {
		return cache.calc(features, this );
	}
	
	public <S> S calc(CachedCalculation<S> cc) throws ExecuteException {
		return search(cc).getOrCalculate(params);
	}
	
	public <S> S calc(CachedCalculation<S> cc, String childName) throws ExecuteException {
		return cacheFor(childName, params.getClass()).search(cc).getOrCalculate(params);
	}

	/** 
	 * Downcasts the type of the parameters in the cache, while continuing to use the same cache
	 * 
	 *  @param classToDowncastTo the class to downcast to
	 *  @return a new CacheableParams with identical but down-casted params, and using the same cache/factory as currently. 
	 * */
	@SuppressWarnings("unchecked")
	public <S extends FeatureCalcParams> CacheableParams<S> downcastParams( Class<S> classToDowncastTo ) throws FeatureCalcException {
		if (params.getClass().isAssignableFrom(classToDowncastTo)) {
			
			return new CacheableParams<S>(
				(S) params,
				(FeatureSessionCacheRetriever<S>) cache,
				factory
			);

		} else {
			throw new FeatureCalcException( "Requires " + classToDowncastTo.getSimpleName() );
		}
	}
	
	/**
	 * Maps the parameters to a new type, which also leads to being assigned a new child-cache.
	 *  
	 * @param <S> the type of the new parameters
	 * @param deriveParamsFunc derives new parameters from existing
	 * @param childName unique name to use in the cache (other features can also reference the same childName)
	 * @return a new CacheableParams with derived-parameters and a cache that is a child of the existing cache
	 */
	public <S extends FeatureCalcParams> CacheableParams<S> mapParams( Function<T,S> deriveParamsFunc, String childName ) {
		S paramsDerived = deriveParamsFunc.apply(params);
		return new CacheableParams<S>(
			paramsDerived,
			cacheFor(childName, paramsDerived.getClass()),
			factory
		);
	}
	
	public <S extends FeatureCalcParams> double calcChangeParams(Feature<S> feature, Function<T,S> deriveParamsFunc, String childName) throws FeatureCalcException {
		S paramsDerived = deriveParamsFunc.apply(params);
		FeatureSessionCacheRetriever<S> child = cacheFor(childName, paramsDerived.getClass()); 
		return child.calc(
			feature,
			new CacheableParams<S>(
					paramsDerived,
				child,
				factory
			)
		);
	}

	public String resolveFeatureID(String id) {
		return cache.resolveFeatureID(id);
	}

	public double calcFeatureByID(String resolvedID, CacheableParams<T> params)
			throws FeatureCalcException {
		return cache.calcFeatureByID(resolvedID, params);
	}
}
