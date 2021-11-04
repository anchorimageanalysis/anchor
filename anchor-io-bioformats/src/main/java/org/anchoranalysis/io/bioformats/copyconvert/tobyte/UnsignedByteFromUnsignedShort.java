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

package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import loci.common.DataTools;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts data of type <i>unsigned short</i> to <i>unsigned byte</i>.
 *
 * <p>If more than 8-bits are being used in the input values, scaling is applied to map the range of
 * effective-bits (how many bits are used) to an 8-bit range.
 *
 * @author Owen Feehan
 */
public class UnsignedByteFromUnsignedShort extends ToUnsignedByteWithScaling {

    /**
     * Create with a number of effective-bits.
     *
     * @param effectiveBits the number of bits that are used in the input-type e.g. 8 or 12 or 16.
     */
    public UnsignedByteFromUnsignedShort(int effectiveBits) {
        super(effectiveBits);
    }

    @Override
    protected UnsignedByteBuffer convert(ByteBuffer source, int channelIndexRelative) {

        UnsignedByteBuffer destination = allocateBuffer();

        byte[] sourceArray = source.array();

        for (int indexIn = 0; indexIn < sizeBytes; indexIn += bytesPerPixel) {

            int indexInPlus = indexIn + (channelIndexRelative * 2);

            int value =
                    DataTools.bytesToShort(
                            sourceArray, indexInPlus, 2, source.order() == ByteOrder.LITTLE_ENDIAN);

            // Make unsigned
            if (value < 0) {
                value += 65536;
            }

            value = scaleValue(value);

            if (value > 255) {
                value = 255;
            }
            if (value < 0) {
                value = 0;
            }

            destination.putUnsigned(value);
        }
        return destination;
    }

    @Override
    protected int calculateBytesPerPixel(int numberChannelsPerArray) {
        return 2 * numberChannelsPerArray;
    }
}
