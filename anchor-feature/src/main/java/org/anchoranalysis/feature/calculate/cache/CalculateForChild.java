/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calculate.cache;

import java.util.function.Function;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPart;
import org.anchoranalysis.feature.calculate.part.CalculationPart;
import org.anchoranalysis.feature.calculate.part.CalculationPartResolver;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Performs calculations using child-caches instead of the main cache
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface CalculateForChild<T extends FeatureInput> {

    /**
     * Calculates a feature in a child-cache.
     *
     * @param <S> input-type for feature to calculate.
     * @param feature feature to calculate with.
     * @param input input for feature.
     * @param childCacheName a unique-name for a child-cache to use for the feature-calculation.
     * @return the result of the feature calculation.
     * @throws FeatureCalculationException if the feature cannot be successfully calculated.
     */
    <S extends FeatureInput> double calculate(
            Feature<S> feature, S input, ChildCacheName childCacheName)
            throws FeatureCalculationException;

    /**
     * Calculates a feature in a child-cache using a new input created from a {@link
     * CalculationPart}.
     *
     * @param <S> input-type for feature to calculate
     * @param feature feature to calculate with
     * @param calculation feature-calculation to generate input for the feature.
     * @param childCacheName a unique-name for a child-cache to use for the feature-calculation.
     * @return the result of the feature calculation.
     * @throws FeatureCalculationException if the feature cannot be successfully calculated.
     */
    <S extends FeatureInput> double calculate(
            Feature<S> feature, CalculationPart<S, T> calculation, ChildCacheName childCacheName)
            throws FeatureCalculationException;

    /**
     * Calculates a {@link CalculationPart} in a child-cache.
     *
     * @param <S> input-type for feature to calculate
     * @param <U> return-type of Feature-Calculation
     * @param childCacheName name of child cache.
     * @param input input to be used for calculation.
     * @param resolver returns the resolved-part, given a resolver.
     * @return the result of the calculation.
     * @throws FeatureCalculationException
     */
    <S extends FeatureInput, U> U calculate(
            ChildCacheName childCacheName,
            S input,
            Function<CalculationPartResolver<S>, ResolvedPart<U, S>> resolver)
            throws FeatureCalculationException;
}
