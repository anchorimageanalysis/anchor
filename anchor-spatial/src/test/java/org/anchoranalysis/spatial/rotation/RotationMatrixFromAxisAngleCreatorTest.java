/*-
 * #%L
 * anchor-math
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.spatial.rotation;

import static org.junit.jupiter.api.Assertions.*;

import org.anchoranalysis.spatial.point.Vector3d;
import org.anchoranalysis.spatial.rotation.factory.RotateAxisAngle;
import org.junit.jupiter.api.Test;
import cern.colt.matrix.DoubleMatrix2D;
import lombok.AllArgsConstructor;

class RotationMatrixFromAxisAngleCreatorTest {

    @Test
    void test() {

        RotateAxisAngle angle = new RotateAxisAngle(new Vector3d(-0.866, -0.5, 2.31e-014), 3);
        MatrixAsserter asserter = new MatrixAsserter(angle.create().getMatrix());

        // First Row
        asserter.value(0.502414, 0, 0);
        asserter.value(0.861667, 0, 1);
        asserter.value(-0.07056, 0, 2);

        // Second Row
        asserter.value(0.861667, 1, 0);
        asserter.value(-0.492494, 1, 1);
        asserter.value(0.122201, 1, 2);

        // Third Row
        asserter.value(0.07056, 2, 0);
        asserter.value(-0.12221, 2, 1);
        asserter.value(-0.98999, 2, 2);
    }
    
    /**
     * Shortcut means of asserting a particular value in the matrix.
     * 
     * @author Owen Feehan
     *
     */
    @AllArgsConstructor
    private static class MatrixAsserter {
        
        private static final double DELTA = 1e-3;
        
        private final DoubleMatrix2D matrix;
        
        public void value(double expectedValue, int indexRow, int indexColumn) {
            assertEquals(expectedValue, matrix.get(indexRow, indexColumn), DELTA);
        }
    }
}
