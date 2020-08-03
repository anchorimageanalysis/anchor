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

package org.anchoranalysis.image.io.input.series;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.chnl.ChannelGetter;
import org.anchoranalysis.image.stack.NamedStacksSet;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public interface NamedChannelsForSeries extends ChannelGetter {

    /**
     * Gets a channel if it exists, returning empty if it doesn't
     *
     * @param channelName name of channel
     * @param timeIndex timepoint
     * @param progressReporter
     * @return the channel if it exists, or empty otherwise
     * @throws GetOperationFailedException if something goes wrong getting an existing channel (but
     *     never if a channel doesn't exist)
     */
    Optional<Channel> getChannelOptional(
            String channelName, int timeIndex, ProgressReporter progressReporter)
            throws GetOperationFailedException;

    Set<String> channelNames();

    int sizeT(ProgressReporter progressReporter) throws RasterIOException;

    ImageDimensions dimensions() throws RasterIOException;

    void addAsSeparateChannels(NamedStacksSet stacks, int timeIndex, ProgressReporter progressReporter)
            throws OperationFailedException;

    void addAsSeparateChannels(NamedProviderStore<TimeSequence> stacks, int timeIndex)
            throws OperationFailedException;

    StoreSupplier<Stack> allChannelsAsStack(int timeIndex);
}
