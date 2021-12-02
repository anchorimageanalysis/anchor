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

package org.anchoranalysis.feature.session.replace;

import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.SessionInputSequential;
import org.anchoranalysis.feature.session.cache.finder.ChildCacheFinder;
import org.anchoranalysis.feature.session.cache.finder.DefaultChildCacheFinder;

/**
 * Always create a new session-input with no reuse or caching.
 *
 * @author Owen Feehan
 * @param <T> feature-input type
 */
@AllArgsConstructor
public class AlwaysNew<T extends FeatureInput> implements ReplaceStrategy<T> {

    private CacheCreator cacheCreator;
    private ChildCacheFinder findChildStrategy;

    /**
     * Constructor with default means of creating a session-input
     *
     * @param cacheCreator
     */
    public AlwaysNew(CacheCreator cacheCreator) {
        this(cacheCreator, DefaultChildCacheFinder.instance());
    }

    @Override
    public FeatureCalculationInput<T> createOrReuse(T input) {
        return new SessionInputSequential<>(input, cacheCreator, findChildStrategy);
    }
}
