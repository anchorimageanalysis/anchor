package org.anchoranalysis.anchor.mpp.bean.proposer.radii;

import org.anchoranalysis.anchor.mpp.mark.bounds.MarkBounds;

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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.orientation.Orientation;

import ch.ethz.biol.cell.mpp.MPPBean;
import ch.ethz.biol.cell.mpp.proposer.ICompatibleWith;

// This IParamsEquals interface is redundant, can be removed
public abstract class RadiiProposer extends MPPBean<RadiiProposer> implements ICompatibleWith {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5448717124153679176L;

	// When we have no bounds, we should create bounds from the boundCalculator
	public abstract Point3d propose(Point3d pos, MarkBounds markBounds, RandomNumberGenerator re, ImageDim bndScene, Orientation orientation, ErrorNode proposerFailureDescription);
}
