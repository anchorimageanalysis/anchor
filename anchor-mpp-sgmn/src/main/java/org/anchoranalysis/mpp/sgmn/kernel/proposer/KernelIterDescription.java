package org.anchoranalysis.mpp.sgmn.kernel.proposer;

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


import java.io.Serializable;

import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.index.IIndexGetter;

public class KernelIterDescription implements Serializable, IIndexGetter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5135255409310941727L;
	
	private int id;
	private String description;
	private boolean accepted;
	private boolean proposed;
	private int[] changedMarkIDArr = null;
	private transient ProposerFailureDescription noProposalReason;
	
	// For now we store this here, but we probably should separate executionTime
	//  out into a different form of storage
	private long executionTime;
	private int iter;
	
	public KernelIterDescription( KernelWithID<?> kernelWithID, boolean accepted, boolean proposed, int[] changedMarkIDArr, long executionTime, int iter, ProposerFailureDescription noProposalReason ) {
		
		this.id = kernelWithID.getID();
		this.description = kernelWithID.getDescription();
		this.accepted = accepted;
		this.proposed = proposed;
		this.changedMarkIDArr = changedMarkIDArr;
		this.executionTime = executionTime;
		this.iter = iter;
		this.noProposalReason = noProposalReason;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public boolean isProposed() {
		return proposed;
	}

	public int[] getChangedMarkIDArr() {
		return changedMarkIDArr;
	}

	public void setChangedMarkIDArr(int[] changedMarkIDArr) {
		this.changedMarkIDArr = changedMarkIDArr;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	@Override
	public int getIndex() {
		return iter;
	}
	
	public int getIter() {
		return iter;
	}

	public ProposerFailureDescription getNoProposalReason() {
		return noProposalReason;
	}

}
