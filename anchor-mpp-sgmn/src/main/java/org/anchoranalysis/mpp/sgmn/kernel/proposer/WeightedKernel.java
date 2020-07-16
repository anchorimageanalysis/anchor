/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import org.anchoranalysis.mpp.sgmn.bean.kernel.Kernel;

/**
 * A kernel with an associated weight.
 *
 * @author Owen Feehan
 * @param <T> type being modified by the kernel.
 */
public class WeightedKernel<T> {

    private Kernel<T> kernel = null;

    private double weight = 0;

    private String name;

    /**
     * Constructor for single kernel factory
     *
     * @param kernel the kernel
     * @param weight an associated weight (a positive floating-point number)
     */
    public WeightedKernel(Kernel<T> kernel, double weight) {
        this.setKernel(kernel);
        this.weight = weight;
        this.name = kernel.getBeanName();
    }

    public double getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return String.format("weight=%f, factory=%s", this.weight, this.kernel.toString());
    }

    public Kernel<T> getKernel() {
        return kernel;
    }

    public void setKernel(Kernel<T> kernel) {
        this.kernel = kernel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
