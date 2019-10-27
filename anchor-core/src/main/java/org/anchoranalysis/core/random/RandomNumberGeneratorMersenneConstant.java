package org.anchoranalysis.core.random;

/*-
 * #%L
 * anchor-core
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

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

public class RandomNumberGeneratorMersenneConstant implements RandomNumberGenerator {

	private RandomEngine randomEngine;
	
	public RandomNumberGeneratorMersenneConstant() {
		randomEngine = new MersenneTwisterConstant();
	}
	@Override
	public int nextInt() {
		return randomEngine.nextInt();
	}

	@Override
	public double nextDouble() {
		return randomEngine.nextDouble();
	}

	@Override
	public Poisson generatePossion(double param) {
		return new Poisson(param, randomEngine);
	}

	@Override
	public Normal generateNormal(double mean, double sd) {
		return new Normal(mean,sd,randomEngine);
	}
}
