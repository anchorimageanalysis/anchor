package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

/*
 * #%L
 * anchor-mpp-sgmn
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public abstract class ReporterInterval<T> extends FeedbackReceiverBean<T> {

	// START BEAN Parameters
	/** 
	 * How many iterations before printing a new report? Encoded in log10.
	 * 
	 * <p>e.g. 0 implies every iteration, 1 implies every 10, 2 implies every 100 etc.
	 * 
	 */
	@BeanField
	private double aggIntervalLog10 = 0;
	// END
	
	public ReporterInterval() {
		// Standard bean constructor
	}
	
	public ReporterInterval(double aggIntervalLog10) {
		this.aggIntervalLog10 = aggIntervalLog10;
	}
	
	@Override
	public void reportItr( Reporting<T> reporting  ) {
	}
	
	protected int getAggInterval() {
		return (int) Math.pow(10.0, aggIntervalLog10);
	}

	public double getAggIntervalLog10() {
		return aggIntervalLog10;
	}

	public void setAggIntervalLog10(double aggIntervalLog10) {
		this.aggIntervalLog10 = aggIntervalLog10;
	}
}
