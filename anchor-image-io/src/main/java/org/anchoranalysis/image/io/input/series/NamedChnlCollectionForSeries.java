package org.anchoranalysis.image.io.input.series;

import java.util.Optional;

/*
 * #%L
 * anchor-image-io
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


import java.util.Set;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.chnl.ChnlGetter;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public abstract class NamedChnlCollectionForSeries implements ChnlGetter {
	
	public abstract Channel getChnl(String chnlName, int t, ProgressReporter progressReporter) throws GetOperationFailedException;
	
	public abstract Optional<Channel> getChnlOrNull(String chnlName, int t, ProgressReporter progressReporter) throws GetOperationFailedException;
	
	public abstract Set<String> chnlNames();
	
	public abstract int sizeT( ProgressReporter progressReporter ) throws RasterIOException;
	
	public abstract ImageDimensions dimensions() throws RasterIOException;

	public abstract void addAsSeparateChnls( NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter ) throws OperationFailedException;
	public abstract void addAsSeparateChnls( NamedProviderStore<TimeSequence> stackCollection, final int t ) throws OperationFailedException;
	
	public abstract Operation<Stack,OperationFailedException> allChnlsAsStack( final int t );
}