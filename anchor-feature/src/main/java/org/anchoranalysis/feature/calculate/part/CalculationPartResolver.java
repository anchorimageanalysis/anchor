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

package org.anchoranalysis.feature.calculate.part;

import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPart;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPartMap;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Searches a cache to reuses a {@link CalculationPart} if it already exists.
 *
 * <p>The purpose is to avoid repeating calculations that may be shared by more than one feature, or
 * more than one parameterization of a feature
 *
 * @author Owen Feehan
 * @param <T> feature input-type that also provides an input to the calculation
 */
public interface CalculationPartResolver<T extends FeatureInput> {

    /**
     * Searches for an equivalent calculation to {@code calculation}.
     *
     * @param calculation the feature-calculation to find an equivalent for.
     * @return the corresponding resolved-calculation.
     */
    <S> ResolvedPart<S, T> search(CalculationPart<S, T> calculation);

    /**
     * Searches for an equivalent calculation to {@code calculation}.
     *
     * @param calculation the {@link CalculationPartMap} to find an equivalent for.
     * @return the corresponding resolved-calculation.
     */
    <S, U> ResolvedPartMap<S, T, U> search(
            CalculationPartMap<S, T, U, FeatureCalculationException> calculation);
}
