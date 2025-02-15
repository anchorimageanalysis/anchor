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

package org.anchoranalysis.image.core.channel.convert.attached.channel;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;
import org.anchoranalysis.image.voxel.statistics.HistogramFactory;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Implements channel conversion after first creating a {@link Histogram} of the voxels in the
 * source channel.
 *
 * @author Owen Feehan
 * @param <T> buffer-type the voxels will be converted <b>to</b>.
 */
@RequiredArgsConstructor
class DelegateToHistogram<T> implements ChannelConverterAttached<Channel, T> {

    private final ChannelConverterAttached<Histogram, T> delegate;

    @Override
    public void attachObject(Channel object) throws OperationFailedException {
        delegate.attachObject(HistogramFactory.createFrom(object.voxels()));
    }

    @Override
    public Channel convert(Channel channel, ConversionPolicy changeExisting) {
        return delegate.convert(channel, changeExisting);
    }

    @Override
    public VoxelsConverter<T> getVoxelsConverter() {
        return delegate.getVoxelsConverter();
    }
}
