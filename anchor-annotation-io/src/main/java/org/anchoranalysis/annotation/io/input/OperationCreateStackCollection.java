package org.anchoranalysis.annotation.io.input;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;

class OperationCreateStackCollection extends CachedOperationWithProgressReporter<INamedProvider<Stack>,CreateException> {

	private ProvidesStackInput inputObject;
	private int seriesNum;
	private int t;
	
	public OperationCreateStackCollection( ProvidesStackInput inputObject ) {
		this(inputObject,0,0);
	}
	
	public OperationCreateStackCollection( ProvidesStackInput inputObject, int seriesNum, int t ) {
		super();
		this.inputObject = inputObject;
		this.seriesNum = seriesNum;
		this.t = t;
	}

	@Override
	protected NamedImgStackCollection execute( ProgressReporter progressReporter ) throws CreateException {
		try {
			return doOperationWithException( progressReporter );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}
	
	private NamedImgStackCollection doOperationWithException( ProgressReporter progressReporter ) throws OperationFailedException {
		NamedImgStackCollection stackCollection = new NamedImgStackCollection();
		inputObject.addToStore(
			new WrapStackAsTimeSequenceStore(stackCollection, t),
			seriesNum,
			progressReporter
		);
		return stackCollection;
	}	
}