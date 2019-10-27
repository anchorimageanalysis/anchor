package org.anchoranalysis.anchor.mpp.bean.proposer;

/*
 * #%L
 * anchor-mpp
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


import java.util.List;

import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.geometry.Point3f;
import anchor.provider.bean.ProposalAbnormalFailureException;
import anchor.provider.bean.ProposerBean;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.proposer.ICompatibleWith;

@GroupingRoot
public abstract class MarkMergeProposer extends ProposerBean<MarkMergeProposer> implements ICompatibleWith {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8280455757195616437L;

	// Returns a merged mark or NULL
	public abstract Mark propose( PxlMarkMemo mark1, PxlMarkMemo mark2, ProposerContext context ) throws ProposalAbnormalFailureException;
	
	// A debug method for optionally associating points with the last proposal made
	public List<Point3f> getLastPnts1() {
		return null;
	}
	
	// A debug method for optionally associating points with the last proposal made
	public List<Point3f> getLastPnts2() {
		return null;
	}
}
