package org.anchoranalysis.math.equation;

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


import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Solves a Quadratic Equation by finding non-complex roots
 *
 */
public class QuadraticEquationSolver {

	private QuadraticEquationSolver() {}

	/** Roots (solution) of a quadratic equation */
	public static class QuadraticRoots {
		private double root1;
		private double root2;
				
		public QuadraticRoots(double root1, double root2) {
			super();
			this.root1 = root1;
			this.root2 = root2;
		}
		
		public double getRoot1() {
			return root1;
		}
		public void setRoot1(double root1) {
			this.root1 = root1;
		}
		public double getRoot2() {
			return root2;
		}
		public void setRoot2(double root2) {
			this.root2 = root2;
		}
	}
	
	
	/**
	 * Solves a quadratic equation in form x^2 + b^x + c = 0
	 * 
	 * Assumes no complex roots
	 * 
	 * @param a coefficient for x^2
	 * @param b coefficient for x
	 * @param c coefficient for constant term
	 * @return simple roots
	 * @throws OperationFailedException if the solution requires complex roots
	 */
	public static QuadraticRoots solveQuadraticEquation( double a, double b, double c) throws OperationFailedException {
		
		double common =  b*b - 4*a*c;

		// As sometimes we get minor presumably "round-off" error we adjust
		if (common > -1e-3 && common <0) {
			common = 0;
		}
			
		if (common<0) {
			throw new OperationFailedException( String.format("Complex roots returned: common=%f",common) );
		}
		
		double commonSqrt = Math.sqrt( common );
		double div = 2 * a;
		
		double root1 = (-b + commonSqrt)/div;
		double root2 = (-b - commonSqrt)/div;
		
		return new QuadraticRoots(root1, root2);
	}
}
