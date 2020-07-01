package org.anchoranalysis.mpp.sgmn.transformer;

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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

public class TransformationContext {

	private ImageDimensions dim;
	private KernelCalcContext kernelCalcContext;
	private LogErrorReporter logger;
	
	public TransformationContext(ImageDimensions dim, KernelCalcContext kernelCalcContext, LogErrorReporter logger) {
		super();
		this.dim = dim;
		this.kernelCalcContext = kernelCalcContext;
		this.logger = logger;
	}

	public ImageDimensions getDimensions() {
		return dim;
	}

	public KernelCalcContext getKernelCalcContext() {
		return kernelCalcContext;
	}

	public LogErrorReporter getLogger() {
		return logger;
	}

	public TransformationContext replaceError(ErrorNode errorNode) {
		return new TransformationContext(
			dim,
			kernelCalcContext.replaceError(errorNode),
			logger
		);
	}
	
	
}
