/*-
 * #%L
 * anchor-feature-session
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.feature.session.cache;

import java.util.Collection;
import java.util.Set;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculator;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesSubset;

/**
 * Caches repeated-calls to the same feature, or references to a feature by an ID-value
 *
 * <p>Two separate indexes are made to prior feature-calculations: a mapping from the Feature (by
 * object reference) to the value a mapping from the customName (if it exists) to the value
 *
 * @author Owen Feehan
 */
class FeatureCache<T extends FeatureInput> implements FeatureCalculationCache<T> {

    private FeatureCalculationCache<T> cache;

    private HorizontalFeatureCalculator<T> calculator;

    private FeatureResultMap<T> map = new FeatureResultMap<>();

    public FeatureCache(
            FeatureCalculationCache<T> cache,
            FeatureList<T> namedFeatures,
            SharedFeaturesSubset<T> sharedFeatures,
            Collection<String> ignorePrefixes) {
        this.cache = cache;

        namedFeatures.forEach(map::add);
        sharedFeatures.forEach(map::add);

        calculator = new HorizontalFeatureCalculator<>(cache.calculator(), map, ignorePrefixes);
    }

    @Override
    public void initialize(FeatureInitialization initialization, Logger logger) {
        cache.initialize(initialization, logger);
    }

    @Override
    public void invalidate() {
        map.clear();
        cache.invalidate();
    }

    @Override
    public void invalidateExcept(Set<ChildCacheName> childCacheNames) {
        map.clear();
        cache.invalidateExcept(childCacheNames);
    }

    @Override
    public <V extends FeatureInput> FeatureCalculationCache<V> childCacheFor(
            ChildCacheName childName,
            Class<? extends FeatureInput> inputType,
            CacheCreator cacheCreator) {
        return cache.childCacheFor(childName, inputType, cacheCreator);
    }

    @Override
    public FeatureCalculator<T> calculator() {
        return calculator;
    }
}
