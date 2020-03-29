package org.anchoranalysis.mpp.sgmn.kernel;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class KernelCalcContext {

	private NRGSchemeWithSharedFeatures nrgScheme;

	private ProposerContext proposerContext;
	private CfgGenContext cfgGenContext;
	
	public KernelCalcContext(
		CfgGenContext cfgGenContext,
		NRGStackWithParams nrgStack,
		NRGSchemeWithSharedFeatures nrgScheme,
		RandomNumberGenerator re,
		ErrorNode errorNode
	) {
		super();
		this.nrgScheme = nrgScheme;
		this.cfgGenContext = cfgGenContext;
		
		this.proposerContext = new ProposerContext(re, nrgStack, nrgScheme.getRegionMap(), errorNode); 
	}
	
	public ProposerContext proposer() {
		return proposerContext;
	}

	public NRGSchemeWithSharedFeatures getNrgScheme() {
		return nrgScheme;
	}

	public KernelCalcContext replaceError(ErrorNode errorNode) {
		return new KernelCalcContext(
			cfgGenContext,
			proposerContext.getNrgStack(),
			nrgScheme,
			proposerContext.getRe(),
			errorNode
		);
	}

	public CfgGenContext cfgGen() {
		return cfgGenContext;
	}
}
