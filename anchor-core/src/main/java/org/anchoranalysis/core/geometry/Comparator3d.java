package org.anchoranalysis.core.geometry;

/*
 * #%L
 * anchor-core
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

import java.util.Comparator;

public final class Comparator3d<T extends Tuple3d> implements Comparator<T> {

    @Override
    public int compare(T arg0, T arg1) {

        // First order by X
        int cmpX = Double.compare(arg0.x(), arg1.x());
        if (cmpX != 0) {
            return cmpX;
        }

        // Then ordered by Y
        int cmpY = Double.compare(arg0.y(), arg1.y());
        if (cmpY != 0) {
            return cmpY;
        }

        // Then ordered by X
        int cmpZ = Double.compare(arg0.z(), arg1.z());
        if (cmpZ != 0) {
            return cmpZ;
        }

        return 0;
    }
}
