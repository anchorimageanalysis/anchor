package org.anchoranalysis.feature.cache;

/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Resolves and calculates a feature by a symbol (an ID/variable-name referring to another feature)
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type
 */
public interface FeatureSymbolCalculator<T extends FeatureInput> {

	/**
	 * Due to scoping (different prefixes that can exist), an ID needs to be resolved
	 *  to a unique-string before it can be passed to calcFeatureByID
	 * 
	 * @param id
	 * @return
	 */
	String resolveFeatureID( String id );
	
	/**
	 * Searches for a feature that matches a particular ID
	 * 
	 * @param resolvedID
	 * @param input
	 * @throws FeatureCalcException 
	 */
	double calcFeatureByID( String resolvedID, SessionInput<T> input ) throws FeatureCalcException;
}
