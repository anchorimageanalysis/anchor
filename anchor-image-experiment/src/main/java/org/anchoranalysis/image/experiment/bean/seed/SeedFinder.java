package org.anchoranalysis.image.experiment.bean.seed;

/*
 * #%L
 * anchor-image-experiment
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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.image.experiment.seed.SeedFinderException;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * 
 * @author Owen Feehan
 *
 * @param <S> shared-object state
 */
public abstract class SeedFinder<S> extends AnchorBean<SeedFinder<S>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract SeedCollection findSeeds( NamedImgStackCollection stackCollection, INamedProvider<ObjMaskCollection> objMaskProvider, ExperimentExecutionArguments expArgs, KeyValueParams keyValueParams, LogErrorReporter logger, BoundOutputManagerRouteErrors outputManager, S sharedState ) throws SeedFinderException;
	
	public abstract S beforeAnySeedFinding( BoundOutputManagerRouteErrors outputManager ) throws ExperimentExecutionException;
	
	public abstract void afterAllSeedFinding( BoundOutputManagerRouteErrors outputManager, S sharedState ) throws ExperimentExecutionException;
}
