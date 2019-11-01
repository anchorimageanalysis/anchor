package ch.ethz.biol.cell.mpp.instantstate;

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


import overlay.OverlayCollectionMarkFactory;

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;

import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

// Bridges CfgNRGInstantState to OverlayedInstantState
public class CfgNRGInstantStateBridge implements IObjectBridge<CfgNRGInstantState, OverlayedInstantState> {

	private RegionMembershipWithFlags regionMembership;
		
	public CfgNRGInstantStateBridge(RegionMembershipWithFlags regionMembership) {
		super();
		this.regionMembership = regionMembership;
	}

	@Override
	public OverlayedInstantState bridgeElement(CfgNRGInstantState sourceObject) throws GetOperationFailedException {
		
		if (sourceObject==null) {
			throw new GetOperationFailedException("invalid index");
		}
		
		if (sourceObject.getCfgNRG()==null) {
			return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection() );
		}
		
		Cfg cfg = sourceObject.getCfgNRG().getCfg();
		
		if (cfg==null) {
			cfg = new Cfg();
		}
		
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			cfg,
			regionMembership
		);
		
		return new OverlayedInstantState(sourceObject.getIndex(), oc);
	}
	
	

}
