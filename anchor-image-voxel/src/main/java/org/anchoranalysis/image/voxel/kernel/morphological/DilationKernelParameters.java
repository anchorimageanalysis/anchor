/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.voxel.kernel.morphological;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.spatial.point.Point3i;

@AllArgsConstructor
public class DilationKernelParameters {

    /** How the kernel is applied to the scene. */
    @Getter private final KernelApplicationParameters kernelApplication;

    private final boolean bigNeighborhood;

    /** A precondition which must be satisfied, before any voxel can be dilated. */
    private final Optional<Predicate<Point3i>> precondition;

    public DilationKernelParameters(
            OutsideKernelPolicy outsideKernelPolicy,
            boolean useZ,
            boolean bigNeighborhood,
            Optional<Predicate<Point3i>> precondition) {
        this(
                new KernelApplicationParameters(outsideKernelPolicy, useZ),
                bigNeighborhood,
                precondition);
    }
    /**
     * Creates a kernel for performing the dilation.
     *
     * @return the kernel that performs the dilation.
     */
    public BinaryKernel createKernel() {

        BinaryKernel kernelDilation = new DilationKernel(bigNeighborhood);

        if (precondition.isPresent()) {
            return new ConditionalKernel(kernelDilation, precondition.get());
        } else {
            return kernelDilation;
        }
    }
}
