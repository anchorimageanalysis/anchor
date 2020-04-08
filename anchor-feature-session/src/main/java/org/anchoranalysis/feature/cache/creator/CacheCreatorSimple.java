package org.anchoranalysis.feature.cache.creator;

import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.CachePlus;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.HorizontalCalculationCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalFeatureCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class CacheCreatorSimple implements CacheCreator {

	private FeatureList<? extends FeatureCalcParams> namedFeatures;
	private SharedFeatureSet<? extends FeatureCalcParams> sharedFeatures;
	private FeatureInitParams featureInitParams;
	private LogErrorReporter logger;
	
	public CacheCreatorSimple(FeatureList<? extends FeatureCalcParams> namedFeatures,
			SharedFeatureSet<? extends  FeatureCalcParams> sharedFeatures, FeatureInitParams featureInitParams, LogErrorReporter logger) {
		super();
		this.namedFeatures = namedFeatures;
		this.sharedFeatures = sharedFeatures;
		this.featureInitParams = featureInitParams;
		this.logger = logger;
	}

	@Override
	public <T extends FeatureCalcParams> FeatureSessionCacheRetriever<T> create( Class<?> paramsType ) {
		
		FeatureList<T> featureList = filterFeatureList(paramsType);
		SharedFeatureSet<T> sharedFeaturesCast = maybeCastSharedFeatures(paramsType); 
		
		CachePlus<T> cache = new CachePlus<>(
			new HorizontalFeatureCacheFactory( new HorizontalCalculationCacheFactory() ),
			featureList,
			sharedFeaturesCast
		);
		try {
			cache.init(featureInitParams, logger, false);
		} catch (InitException e) {
			logger.getErrorReporter().recordError(CacheCreatorSimple.class, e);
		}
		return cache.createCache().retriever();
	}

	private <T extends FeatureCalcParams> FeatureList<T> filterFeatureList(Class<?> paramsType) {
		@SuppressWarnings("unchecked")
		List<Feature<T>> list = namedFeatures.getList().stream()
			.filter( f -> paramsType.isAssignableFrom(f.getClass()) )
			.map( f -> (Feature<T>) f )
			.collect( Collectors.toList() );
		
		return new FeatureList<>(list);
	}
	
	// Assumes either all features in the set match paramsType or none do
	@SuppressWarnings("unchecked")
	private <T extends FeatureCalcParams> SharedFeatureSet<T> maybeCastSharedFeatures(Class<?> paramsType) {
		
		if (sharedFeatures.keys().isEmpty()) {
			return new SharedFeatureSet<T>();
		}
		
		Class<?> classOfArbitraryItem = sharedFeatures.arbitraryItem().getClass(); 
		
		// Take an arbitrary item
		if(paramsType.isAssignableFrom(classOfArbitraryItem)) {
			return (SharedFeatureSet<T>) sharedFeatures;
		}
		
		return new SharedFeatureSet<T>();	
	}
}
