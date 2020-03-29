package org.anchoranalysis.anchor.mpp.feature.instantstate;

/*-
 * #%L
 * anchor-mpp-feature
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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.bridge.IObjectBridge;

public class CfgWithNrgTotalInstantStateBridge implements IObjectBridge<CfgWithNrgTotalInstantState, OverlayedInstantState> {

	
	
	@Override
	public OverlayedInstantState bridgeElement(CfgWithNrgTotalInstantState sourceObject) throws BridgeElementException {
		
		if (sourceObject==null) {
			throw new BridgeElementException("The sourceObject is null. Invalid index");
		}
		
		if (sourceObject.getCfg()==null) {
			return new OverlayedInstantState(sourceObject.getIndex(), new OverlayCollection() );
		}
		
		RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			sourceObject.getCfg().getCfg(),
			regionMembership
		);
			
		return new OverlayedInstantState(sourceObject.getIndex(), oc );
	}
	
	

}
