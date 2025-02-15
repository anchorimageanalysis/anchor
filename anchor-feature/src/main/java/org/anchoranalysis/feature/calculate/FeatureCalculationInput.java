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

package org.anchoranalysis.feature.calculate;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.cache.CalculateForChild;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.calculate.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPart;
import org.anchoranalysis.feature.calculate.part.CalculationPart;
import org.anchoranalysis.feature.calculate.part.CalculationPartResolver;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Gives a {@link FeatureInput} the necessary context for feature-calculation, including associating
 * it with a cache.
 *
 * <p>This is the recommended method for calculating the value of a {@link Feature} as it allows
 * sensible caching / memoization to occur of subcomponents of the feature's computation (known as
 * {@link CalculationPart}s.
 *
 * @param <T> underlying feature-input type.
 * @author Owen Feehan
 */
public interface FeatureCalculationInput<T extends FeatureInput> {

    /**
     * The underlying feature-input.
     *
     * @return the feature-input.
     */
    T get();

    /**
     * Calculates the result of a feature using this input.
     *
     * @param feature the feature to calculate with.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if a feature cannot be successfully calculated.
     */
    double calculate(Feature<T> feature) throws FeatureCalculationException;

    /**
     * Calculates the results of several features using this input.
     *
     * @param features features to calculate with.
     * @return the results of each feature's calculation respectively.
     * @throws NamedFeatureCalculateException if any feature cannot be successfully calculated.
     */
    ResultsVector calculate(FeatureList<T> features) throws NamedFeatureCalculateException;

    /**
     * Calculates a feature-calculation after resolving it against the main cache.
     *
     * @param <S> return-type of the calculation.
     * @param calculation the feature-calculation to resolve.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if a feature cannot be successfully calculated.
     */
    <S> S calculate(CalculationPart<S, T> calculation) throws FeatureCalculationException;

    /**
     * Calculates a resolved Feature-calculation.
     *
     * @param <S> return-type of the calculation.
     * @param calculation the feature-calculation to resolve.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if a feature cannot be successfully calculated.
     */
    <S> S calculate(ResolvedPart<S, T> calculation) throws FeatureCalculationException;

    /**
     * A resolver for calculations.
     *
     * @return the resolver.
     */
    CalculationPartResolver<T> resolver();

    /**
     * Performs calculations not on the main cache, but on a child cache.
     *
     * @return the calculator.
     */
    CalculateForChild<T> forChild();

    /**
     * Calculates a feature if only an symbol (ID/name) is known, which refers to another feature.
     *
     * @return the calculator.
     */
    FeatureSymbolCalculator<T> bySymbol();

    /**
     * Get the associated {@link FeatureCalculationCache}.
     *
     * @return the associated cache.
     */
    FeatureCalculationCache<T> getCache();
}
