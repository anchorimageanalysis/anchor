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

package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.Collection;
import java.util.Set;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculate.CacheCreator;
import org.anchoranalysis.feature.cache.calculate.FeatureSessionCache;
import org.anchoranalysis.feature.cache.calculate.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * Caches repeated-calls to the same feature, or references to a feature by an ID-value
 *
 * <p>Two separate indexes are made to prior feature-calculations: a mapping from the Feature (by
 * object reference) to the value a mapping from the customName (if it exists) to the value
 *
 * @author Owen Feehan
 */
public class HorizontalFeatureCache<T extends FeatureInput> implements FeatureSessionCache<T> {

    private FeatureSessionCache<T> delegate;

    private HorizontalFeatureCacheCalculator<T> retriever;

    private FeatureResultMap<T> map = new FeatureResultMap<>();

    HorizontalFeatureCache(
            FeatureSessionCache<T> delegate,
            FeatureList<T> namedFeatures,
            SharedFeatureSet<T> sharedFeatures,
            Collection<String> ignorePrefixes) {
        super();
        this.delegate = delegate;

        for (Feature<T> f : namedFeatures) {
            map.add(f);
        }

        for (NameValue<Feature<T>> f : sharedFeatures.getSet()) {
            map.add(f.getValue());
        }

        retriever =
                new HorizontalFeatureCacheCalculator<>(delegate.calculator(), map, ignorePrefixes);
    }

    @Override
    public void init(FeatureInitParams featureInitParams, Logger logger) {
        delegate.init(featureInitParams, logger);
    }

    @Override
    public void invalidate() {
        map.clear();
        delegate.invalidate();
    }

    @Override
    public void invalidateExcept(Set<ChildCacheName> childCacheNames) {
        map.clear();
        delegate.invalidateExcept(childCacheNames);
    }

    @Override
    public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
            ChildCacheName childName,
            Class<? extends FeatureInput> paramsType,
            CacheCreator cacheCreator) {
        return delegate.childCacheFor(childName, paramsType, cacheCreator);
    }

    @Override
    public FeatureSessionCacheCalculator<T> calculator() {
        return retriever;
    }
}
