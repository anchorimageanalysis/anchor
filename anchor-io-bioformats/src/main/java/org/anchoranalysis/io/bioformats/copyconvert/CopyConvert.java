/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.io.bioformats.DestinationChannelForIndex;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

/**
 * Copies the bytes from a {@link IFormatReader} to a list of channels, converting if necessary.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyConvert {

    /**
     * Copies all frames, channels, z-slices (in a byte-array) into a destination set of Channels
     * converting them if necessary along the way
     *
     * @param reader the source of the copy
     * @param dest the destination of the copy
     * @param progressReporter
     * @throws FormatException
     * @throws IOException
     */
    public static void copyAllFrames(
            IFormatReader reader,
            List<Channel> dest,
            ProgressReporter progressReporter,
            ImageFileShape targetShape,
            ConvertTo<?> convertTo,
            ReadOptions readOptions)
            throws FormatException, IOException {
        int numberChannelsPerByteArray = readOptions.channelsPerByteArray(reader);

        int numberByteArraysPerIteration =
                calculateByteArraysPerIteration(
                        targetShape.getNumberChannels(), numberChannelsPerByteArray);

        try (ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter)) {

            pri.setMax(targetShape.totalNumberSlices());
            pri.open();

            IterateOverSlices.iterateDimensionsOrder(
                    reader.getDimensionOrder(),
                    targetShape,
                    numberByteArraysPerIteration,
                    (t, z, c, readerIndex) -> {

                        /** Selects a destination channel for a particular relative channel */
                        DestinationChannelForIndex destinationChannel =
                                channelIndexRelative ->
                                        dest.get(
                                                destIndex(
                                                        c + channelIndexRelative,
                                                        t,
                                                        targetShape.getNumberChannels()));

                        byte[] bufferArray = reader.openBytes(readerIndex);

                        convertTo.copyAllChannels(
                                targetShape.getImageDimensions(),
                                ByteBuffer.wrap(bufferArray),
                                destinationChannel,
                                z,
                                numberChannelsPerByteArray);

                        pri.update();
                    });
        }
    }

    private static int calculateByteArraysPerIteration(int numChannel, int numChannelsPerByteArray)
            throws FormatException {

        if ((numChannel % numChannelsPerByteArray) != 0) {
            throw new FormatException(
                    String.format(
                            "numChannels(%d) mod numChannelsPerByteArray(%d) != 0",
                            numChannel, numChannelsPerByteArray));
        }

        return numChannel / numChannelsPerByteArray;
    }

    private static int destIndex(int c, int t, int numChannelsPerFrame) {
        return (t * numChannelsPerFrame) + c;
    }
}
