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

package org.anchoranalysis.image.histogram;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramFactory {

    public static Histogram createGuessMaxBin(Collection<Histogram> histograms)
            throws CreateException {

        if (histograms.isEmpty()) {
            throw new CreateException("Cannot determine a maxBinVal as the collection is empty");
        }

        Histogram histogram = histograms.iterator().next();

        return create(histograms, histogram.getMaxBin());
    }

    // Creates histograms from a collection of existing histograms
    // Assumes histograms all have the same max Bin
    public static Histogram create(Collection<Histogram> histograms, int maxBinVal)
            throws CreateException {

        if (histograms.isEmpty()) {
            return new Histogram(maxBinVal);
        }

        Histogram out = new Histogram(maxBinVal);
        for (Histogram histogram : histograms) {
            try {
                out.addHistogram(histogram);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }
        return out;
    }

    public static Histogram create(VoxelBuffer<?> inputBuffer) {

        Histogram histogram = new Histogram((int) inputBuffer.dataType().maxValue());
        addBufferToHistogram(histogram, inputBuffer, inputBuffer.capacity());
        return histogram;
    }

    public static Histogram create(VoxelsWrapper inputBuffer) {
        return HistogramFactory.create(inputBuffer.any());
    }

    private static Histogram create(Voxels<?> inputBox) {

        Histogram histogram = new Histogram((int) inputBox.dataType().maxValue());

        int volumeXY = inputBox.extent().volumeXY();

        inputBox.extent()
                .iterateOverZ(z -> addBufferToHistogram(histogram, inputBox.slice(z), volumeXY));

        return histogram;
    }

    private static void addBufferToHistogram(
            Histogram histogram, VoxelBuffer<?> buffer, int maxOffset) {
        for (int offset = 0; offset < maxOffset; offset++) {
            int val = buffer.getInt(offset);
            histogram.incrementValue(val);
        }
    }
}
