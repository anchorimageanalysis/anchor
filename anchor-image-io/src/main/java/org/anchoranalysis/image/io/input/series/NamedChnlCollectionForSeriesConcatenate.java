package org.anchoranalysis.image.io.input.series;

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


import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class NamedChnlCollectionForSeriesConcatenate<BufferType extends Buffer> extends NamedChnlCollectionForSeries {

	private List<NamedChnlCollectionForSeries> list = new ArrayList<>();

	public NamedChnlCollectionForSeriesConcatenate() {
		super();
	}
	
	@Override
	public Chnl getChnl(String chnlName, int t, ProgressReporter progressReporter) throws GetOperationFailedException {
		
		for( NamedChnlCollectionForSeries item : list ) {
			
			Chnl c = item.getChnlOrNull(chnlName, t,progressReporter);
			if (c!=null) {
				return c;
			}
		}

		throw new GetOperationFailedException( String.format("chnlName '%s' is not found", chnlName) );
	}

	@Override
	public Chnl getChnlOrNull(String chnlName, int t, ProgressReporter progressReporter)
			throws GetOperationFailedException {

		for( NamedChnlCollectionForSeries item : list ) {
			
			Chnl c = item.getChnlOrNull(chnlName, t, progressReporter);
			if (c!=null) {
				return c;
			}
		}

		return null;
	}
	

	public void addAsSeparateChnls(NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter )
			throws OperationFailedException {
		
		try (ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, list.size())) {
			
			for( NamedChnlCollectionForSeries item : list ) {
				item.addAsSeparateChnls(stackCollection, t, new ProgressReporterOneOfMany(prm) );
				prm.incrWorker();
			}
		}
	}
	
	public void addAsSeparateChnls(NamedProviderStore<TimeSequence> stackCollection, int t)
			throws OperationFailedException {
		for( NamedChnlCollectionForSeries item : list ) {
			item.addAsSeparateChnls(stackCollection, t);
		}
	}

	public boolean add(NamedChnlCollectionForSeries e) {
		return list.add(e);
	}
	
	public Set<String> chnlNames() {
		HashSet<String> set = new HashSet<>();
		for( NamedChnlCollectionForSeries item : list ) {
			set.addAll( item.chnlNames() );
		}
		return set;
	}
	
	public int sizeT( ProgressReporter progressReporter )
			throws RasterIOException {
		
		int series = 0;
		boolean first = true;
		
		for( NamedChnlCollectionForSeries item : list ) {
			if (first) {
				series = item.sizeT(progressReporter);
				first = false;
			} else {
				series = Math.min(series, item.sizeT(progressReporter)); 
			}
		}
		return series;
	}

	@Override
	public boolean hasChnl(String chnlName) {
		for( NamedChnlCollectionForSeries item : list ) {
			if( item.chnlNames().contains(chnlName) ) {
				return true;
			}
		}
		return false;
	}
	
	public ImageDim dimensions() throws RasterIOException {
		// Assumes dimensions are the same for every item in the list
		return list.get(0).dimensions();
	}

	public Iterator<NamedChnlCollectionForSeries> iteratorFromRaster() {
		return list.iterator();
	}

	@Override
	public Operation<Stack> allChnlsAsStack(int t) throws OperationFailedException {
		return new CachedOperation<Stack>() {

			@Override
			protected Stack execute() throws ExecuteException {
				
				Stack out = new Stack();
				for( NamedChnlCollectionForSeries ncc : list ) {
					try {
						addAllChnlsFrom(
							ncc.allChnlsAsStack(t).doOperation(),
							out
						);
					} catch (OperationFailedException | IncorrectImageSizeException e) {
						throw new ExecuteException(e);
					}
				}
				return out;
			}
		};
	}
	
	private static void addAllChnlsFrom( Stack src, Stack dest ) throws IncorrectImageSizeException {
		for( Chnl c : src ) {
			dest.addChnl(c);;
		}
	}
}
