package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

/*-
 * #%L
 * anchor-mpp-feature
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

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CalculateDeriveSingleMemoFromPair extends FeatureCalculation<FeatureInputSingleMemo,FeatureInputPairMemo> {

	private boolean first;
	
	/**
	 * Constructor
	 * 
	 * @param first Iff true, first object is used, otherwise the second
	 */
	public CalculateDeriveSingleMemoFromPair(boolean first) {
		super();
		this.first = first;
	}
	
	@Override
	protected FeatureInputSingleMemo execute(FeatureInputPairMemo input) {
		return new FeatureInputSingleMemo(
			first ? input.getObj1() : input.getObj2(),
			input.getNrgStackOptional()
		);
	}

	@Override
	public boolean equals(Object obj) {
		 if(obj instanceof CalculateDeriveSingleMemoFromPair){
			 final CalculateDeriveSingleMemoFromPair other = (CalculateDeriveSingleMemoFromPair) obj;
		        return new EqualsBuilder()
		            .append(first, other.first)
		            .isEquals();
	    } else{
	        return false;
	    }
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(first)
			.toHashCode();
	}
}
