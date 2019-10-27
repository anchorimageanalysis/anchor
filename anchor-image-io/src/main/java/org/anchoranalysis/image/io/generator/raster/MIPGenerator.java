package org.anchoranalysis.image.io.generator.raster;

/*-
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

public class MIPGenerator extends RasterGenerator implements IterableObjectGenerator<Stack,Stack> {

	private StackGenerator delegate;
	
	public MIPGenerator( boolean padIfNec, String manifestFunction) {
		delegate = new StackGenerator( padIfNec, manifestFunction);
	}
	
	public MIPGenerator( Stack element, boolean padIfNec, String manifestFunction) {
		delegate = new StackGenerator( element, padIfNec, manifestFunction );
	}
	
	@Override
	public boolean isRGB() {
		return delegate.isRGB();
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		Stack stack = delegate.getIterableElement();
		
		Stack mip = stack.maxIntensityProj();
		return mip;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return delegate.createManifestDescription();
	}

	@Override
	public Stack getIterableElement() {
		return delegate.getIterableElement();
	}

	@Override
	public void setIterableElement(Stack element) {
		delegate.setIterableElement(element);
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}

	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

}
