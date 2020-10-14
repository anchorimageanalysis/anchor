/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.histogram;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.io.input.csv.CSVReaderByLine;
import org.anchoranalysis.io.input.csv.CSVReaderException;
import org.anchoranalysis.io.input.csv.ReadByLine;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramCSVReader {

    public static Histogram readHistogramFromFile(Path filePath) throws CSVReaderException {

        Map<Integer, Integer> map = new HashMap<>();

        try (ReadByLine reader = CSVReaderByLine.open(filePath)) {
            reader.read((line, firstLine) -> addLineToMap(map, line));
        }

        return histogramFromMap(map);
    }

    private static void addLineToMap(Map<Integer, Integer> map, String[] line)
            throws OperationFailedException {

        float binAsFloat = Float.parseFloat(line[0]);
        int bin = (int) binAsFloat;

        if (binAsFloat != bin) {
            throw new OperationFailedException(
                    String.format("Bin-value of %f is not integer.", binAsFloat));
        }

        float countAsFloat = Float.parseFloat(line[1]);
        int count = (int) countAsFloat;

        if (countAsFloat != count) {
            throw new OperationFailedException(
                    String.format("Count-value of %f is not integer.", countAsFloat));
        }

        if (map.containsKey(bin)) {
            throw new OperationFailedException(
                    String.format("There are multiple bins of value %d", bin));
        }

        map.put(bin, count);
    }

    // Maximum-value
    private static int maxValue(Set<Integer> set) {

        Integer max = null;
        for (Integer i : set) {
            if (max == null || i > max) {
                max = i;
            }
        }
        return max;
    }

    private static Histogram histogramFromMap(Map<Integer, Integer> map) throws CSVReaderException {

        // We get the highest-intensity value from the map
        int maxCsvValue = maxValue(map.keySet());

        // We guess the upper limit of the histogram to match an unsigned 8-bit or 16-bit image
        int maxHistogramValue = guessMaxHistogramBin(maxCsvValue);

        Histogram histogram = new Histogram(maxHistogramValue);

        for (Entry<Integer, Integer> entry : map.entrySet()) {
            histogram.incrementValueBy(entry.getKey(), entry.getValue());
        }
        return histogram;
    }

    private static int guessMaxHistogramBin(int maxCsvValue) throws CSVReaderException {
        if (maxCsvValue <= UnsignedByteVoxelType.MAX_VALUE) {
            return UnsignedByteVoxelType.MAX_VALUE_INT;
        } else if (maxCsvValue <= UnsignedShortVoxelType.MAX_VALUE) {
            return UnsignedShortVoxelType.MAX_VALUE_INT;
        } else {
            throw new CSVReaderException(
                    "Histograms can only supported for a maximum-value of "
                            + UnsignedShortVoxelType.MAX_VALUE_INT);
        }
    }
}
