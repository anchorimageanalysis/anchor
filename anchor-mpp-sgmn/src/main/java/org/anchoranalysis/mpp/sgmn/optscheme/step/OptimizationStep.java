package org.anchoranalysis.mpp.sgmn.optscheme.step;

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

import java.util.function.Function;

import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.optscheme.DualState;
import org.anchoranalysis.plugin.mpp.sgmn.bean.optscheme.statereporter.StateReporter;
import org.anchoranalysis.plugin.mpp.sgmn.optscheme.TransformationContext;


/**
 * 
 * @author FEEHANO
 *
 * @param <S> kernel-type
 * @param <T> optimization-state type
 */
public class OptimizationStep<S,T> {
	
	// The important state needed for the current algorithm step
	private DualState<T> state;
	
	// Everything else is additional historical information we use for
	
	private boolean accptd;
	private boolean best;
	
	private T proposal;
	
	private DscrData<S> dscrData = new DscrData<>();
		
	public OptimizationStep() {
		state = new DualState<>();
		state.clearBest();
	}
	

	public void setTemperature(double temperature) {
		dscrData.setTemperature(temperature);
	}
	
	public void assignProposal(T proposalNew, KernelWithID<S> kid) {
		
		dscrData.setKernel(kid);
		
		if (this.proposal==proposalNew) {
			return;
		}
		
		this.proposal = proposalNew;
	}

	public void acceptProposal( Function<T,Double> funcScore ) {
		accptd = true;
		assgnCrntFromProposal(funcScore);
		
		setKernelNoProposalDescription( null );
		markChanged( dscrData.getKernel() );
	}
	
	public void rejectProposal() {
		markRejected();
		setKernelNoProposalDescription( null );
		markChanged( dscrData.getKernel() );
	}
	
	public void markNoProposal( ProposerFailureDescription proposerFailureDescription ) {
		proposal = null;
		
		setKernelNoProposalDescription( proposerFailureDescription );
		setChangedMarkIDs( new int[]{} );
		
		markRejected();
	}

	public void setExecutionTime(long executionTime) {
		dscrData.setExecutionTime(executionTime);
	}

	public T releaseKeepBest() {
		T ret = state.releaseKeepBest();
		releaseProposal();
		return ret;
	}

	private void releaseProposal() {
		proposal = null;
	}

	private void assgnCrntFromProposal( Function<T,Double> funcScore ) {
		state.assignCrnt( proposal );
		maybeAssignAsBest( funcScore );
	}
	
	
	private void setKernelNoProposalDescription(ProposerFailureDescription kernelRejectionDescription) {
		dscrData.setKernelNoProposalDescription(kernelRejectionDescription);
	}
	
	private void maybeAssignAsBest( Function<T,Double> funcScore ) {
		if( state.isBestUndefined()
				// Is the score from crnt, greater than the score from best?
			|| funcScore.apply( state.getCrnt() ) > funcScore.apply( state.getBest() )
		) {
			state.assignBestFromCrnt();
			best = true;
		}
	}
	
	private void markChanged( KernelWithID<S> kid ) {
		setChangedMarkIDs( kid.getKernel().changedMarkIDArray() );
	}
	
	private void setChangedMarkIDs(int[] changedMarkIDs) {
		dscrData.setChangedMarkIDs(changedMarkIDs);
	}
	
	private void markRejected() {
		accptd = false;
		best = false;
	}
		
	public KernelWithID<S> getKernel() {
		return dscrData.getKernel();
	}

	public T getCrnt() {
		return state.getCrnt();
	}
	
	public T getBest() {
		return state.getBest();
	}
	
	public boolean isBestUndefined() {
		return state.isBestUndefined();
	}
	
	public boolean hasProposal() {
		return proposal != null;
	}

	public T getProposal() {
		return proposal;
	}
	
	/**
	 * 
	 * @param iter
	 * @param func Main function for extracting a CfgNRGPixelized from type T 
	 * @param funcProposalSecondary Function for extracting a secondary proposal
	 * @return
	 * @throws OperationFailedException 
	 */
	public <U> Reporting<U> reporting(
		int iter,
		StateReporter<T,U> stateReporter,
		TransformationContext context
	) throws OperationFailedException {
		
		return new Reporting<U>(
			iter,
			state.transform( stateReporter.primaryReport(), context),
			proposal != null ? stateReporter.primaryReport().transform(proposal, context) : null,
			stateReporter.secondaryReport() != null && proposal != null ? stateReporter.secondaryReport().transform(proposal, context) : null,
			dscrData,
			accptd,
			best
		);
	}


}
