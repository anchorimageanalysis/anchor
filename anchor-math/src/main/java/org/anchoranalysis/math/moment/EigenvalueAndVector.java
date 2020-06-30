package org.anchoranalysis.math.moment;

/*
 * #%L
 * anchor-math
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


import cern.colt.matrix.DoubleMatrix1D;


// 2 returns the highest, 1 returns the middle and 0
public class EigenvalueAndVector implements Comparable<EigenvalueAndVector> {
	private double eigenvalue;
	private DoubleMatrix1D eigenvector;
	
	public EigenvalueAndVector(double eigenvalue, DoubleMatrix1D eigenvector) {
		super();
		this.eigenvalue = eigenvalue;
		this.eigenvector = eigenvector;
	}

	public double getEigenvalue() {
		return eigenvalue;
	}

	public void setEigenvalue(double eigenValue) {
		this.eigenvalue = eigenValue;
	}

	public DoubleMatrix1D getEigenvector() {
		return eigenvector;
	}

	public void setEigenvector(DoubleMatrix1D eigenvector) {
		this.eigenvector = eigenvector;
	}

	@Override
	public int compareTo(EigenvalueAndVector o) {
		return Double.compare(this.eigenvalue,o.eigenvalue);
	}
	
	/**
	 * A normalization of an eigen-value to represent axis-length.
	 * 
	 * <p>This normalization procedure is designed to return the same result as Matlab's "MajorAxisLength"
	 * feature, as per <a href="http://stackoverflow.com/questions/1711784/computing-object-statistics-from-the-second-central-moments">Stackoverflow post</a>
	 *
	 * @return
	 */
	public double eigenvalueNormalizedAsAxisLength() {
		return (4 * Math.sqrt(eigenvalue));
	}
	
	public EigenvalueAndVector duplicate() {
		return new EigenvalueAndVector(eigenvalue, eigenvector.copy());
	}
}