package org.anchoranalysis.mpp.io.cfg.generator;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;

/*
 * #%L
 * anchor-mpp-io
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


import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.mpp.io.cfg.ColoredCfgWithDisplayStack;

public class CfgGenerator extends CfgGeneratorBase {
	
	public CfgGenerator(DrawObject maskWriter, IDGetter<Overlay> idGetter ) {
		this(maskWriter, null, idGetter);
	}
	
	public CfgGenerator(DrawObject maskWriter, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter ) {
		this( maskWriter, cws, idGetter, RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
	}
	
	public CfgGenerator(DrawObject maskWriter, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter, RegionMembershipWithFlags regionMembership ) {
		super(
			new SimpleOverlayWriter(maskWriter),
			cws,
			idGetter,
			regionMembership
		);
	}
		
	@Override
	protected DisplayStack background(DisplayStack stack) {
		return stack;
	}
}
