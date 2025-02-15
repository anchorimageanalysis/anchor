/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.image.io.histogram.output;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.math.histogram.Histogram;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HistogramCSVWriter {

    private static final List<String> HEADERS = Arrays.asList("intensity", "count");

    public static void writeHistogramToFile(Histogram histogram, Path filePath, boolean ignoreZeros)
            throws OutputWriteFailedException {

        try (CSVWriter writer = CSVWriter.create(filePath)) {
            writer.writeHeaders(HEADERS);

            histogram.iterateValues(
                    (bin, count) -> {

                        // Skip any zeros if we are ignoring zeros
                        if (!ignoreZeros || count != 0) {
                            writer.writeRow(createTypedValues(bin, count));
                        }
                    });
        }
    }

    private static List<TypedValue> createTypedValues(int bin, int count) {
        return Arrays.asList(new TypedValue(bin, 0), new TypedValue(count, 0));
    }
}
