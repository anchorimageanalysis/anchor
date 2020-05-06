package org.anchoranalysis.anchor.mpp.mark.set;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public interface UpdatableMarkSet {

	void initUpdatableMarkSet( MemoForIndex marks, NRGStackWithParams nrgStack, LogErrorReporter logger, SharedFeatureSet<FeatureInput> sharedFeatures ) throws InitException;
	
	void add( MemoForIndex marksExisting, PxlMarkMemo newMark ) throws UpdateMarkSetException;
	
	void exchange( MemoForIndex pxlMarkMemoList, PxlMarkMemo oldMark, int indexOldMark, PxlMarkMemo newMark ) throws UpdateMarkSetException;
	
	void rmv( MemoForIndex marksExisting, PxlMarkMemo mark ) throws UpdateMarkSetException;
}
