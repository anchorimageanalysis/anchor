package org.anchoranalysis.math.moment;

import java.util.ArrayList;

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


import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;

/**
 * Calculates the first moment (mean) and eigenvalues of the second moments (covariance) from a matrix of points
 * 
 * @author Owen Feehan
 *
 */
public class MomentsFromPointsCalculator {
	
	private List<EigenvalueAndVector> list;
	private double[] mean = new double[3];
	
	private MomentsFromPointsCalculator() {
		
	}
	
	/**
	 * Calculates the second-moments from the covariance of a matrix of points 
	 * 
	 * Steps:
	 * 1. Constructs first and the second-moments matrix of some input points
     * 2. Calculates an eigenvalue-decomposition of the second-moment matrix 
	 * 
	 * @param matrixPoints a matrix where each row represents a point (n x 3) and each column an axis
	 * @param suppressZ iff TRUE the z-dimension is ignored
	 * @param sortAscending if TRUE, eigenValues are sorted in ascendingOrder, if FALSE in descending order
	 */
	public MomentsFromPointsCalculator( DoubleMatrix2D matrixPoints, boolean suppressZ, boolean sortAscending ) {

		mean = calcFirstMoments(matrixPoints);
		
		DoubleMatrix2D secondMoments = calcSecondMoments( matrixPoints, suppressZ );
		
		list = EigenValueDecompose.apply(secondMoments, sortAscending);
	}
		
	public EigenvalueAndVector get(int index) {
		return list.get(index);
	}
	
	/**
	 * Index is the axis (0 for X, 1 for Y, 2 for Z)
	 * @param index
	 * @return
	 */
	public double getMean(int index) {
		return mean[index];
	}
	
	// Removes the entry that is closest to having an eigenVector in direction (0,0,1)
	public void removeClosestToUnitZ() {
		
		double zMax = Double.NEGATIVE_INFINITY;
		int index = -1;
		for( int i=0; i<3; i++ ) {
			
			// Dot product considers only the z value
			double zVal = list.get(i).getEigenvector().get(2);
			if (zVal > zMax) {
				zMax = zVal;
				index = i;
			}
		}
		
		assert(index!=-1);
		
		// Remove the closest to z
		list.remove(index);
	}
	
	
	public MomentsFromPointsCalculator duplicate() {
		MomentsFromPointsCalculator out = new MomentsFromPointsCalculator();
		out.list = new ArrayList<>();
		for( int i=0; i<3; i++ ) {
			out.list.add( list.get(i).duplicate() );
		}
		return out;
	}
	
	/** Calculates the first moment (the mean) */
	private static double[] calcFirstMoments( DoubleMatrix2D matrixPoints ) {
		double[] mean = new double[3];
		for( int i=0; i<3; i++ ) {
			mean[i] = matrixPoints.viewColumn(i).zSum() / matrixPoints.rows();
			
		}
		return mean;
	}
	
	/** Calculates the second moment (the covariance) */
	private static DoubleMatrix2D calcSecondMoments( DoubleMatrix2D matrixPoints, boolean suppressZ ) {
		
		DoubleMatrix2D secondMoments = Statistic.covariance(matrixPoints);
		
		if (suppressZ) {
			secondMoments.set(2, 0, 0);
			secondMoments.set(2, 1, 0);
			secondMoments.set(0, 2, 0);
			secondMoments.set(1, 2, 0);
		}
		
		return secondMoments;
	}
}