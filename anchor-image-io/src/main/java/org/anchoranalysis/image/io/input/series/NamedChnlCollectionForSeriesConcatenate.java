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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class NamedChnlCollectionForSeriesConcatenate implements NamedChannelsForSeries {

    private List<NamedChannelsForSeries> list = new ArrayList<>();

    @Override
    public Channel getChannel(String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        for (NamedChannelsForSeries item : list) {

            Optional<Channel> c = item.getChannelOptional(chnlName, t, progressReporter);
            if (c.isPresent()) {
                return c.get();
            }
        }

        throw new GetOperationFailedException(
                chnlName,
                String.format("chnlName '%s' is not found", chnlName));
    }

    @Override
    public Optional<Channel> getChannelOptional(
            String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        for (NamedChannelsForSeries item : list) {

            Optional<Channel> c = item.getChannelOptional(chnlName, t, progressReporter);
            if (c.isPresent()) {
                return c;
            }
        }

        return Optional.empty();
    }

    public void addAsSeparateChannels(
            NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter)
            throws OperationFailedException {

        try (ProgressReporterMultiple prm =
                new ProgressReporterMultiple(progressReporter, list.size())) {

            for (NamedChannelsForSeries item : list) {
                item.addAsSeparateChannels(stackCollection, t, new ProgressReporterOneOfMany(prm));
                prm.incrWorker();
            }
        }
    }

    public void addAsSeparateChannels(NamedProviderStore<TimeSequence> stackCollection, int t)
            throws OperationFailedException {
        for (NamedChannelsForSeries item : list) {
            item.addAsSeparateChannels(stackCollection, t);
        }
    }

    public boolean add(NamedChannelsForSeries e) {
        return list.add(e);
    }

    public Set<String> channelNames() {
        HashSet<String> set = new HashSet<>();
        for (NamedChannelsForSeries item : list) {
            set.addAll(item.channelNames());
        }
        return set;
    }

    public int sizeT(ProgressReporter progressReporter) throws RasterIOException {

        int series = 0;
        boolean first = true;

        for (NamedChannelsForSeries item : list) {
            if (first) {
                series = item.sizeT(progressReporter);
                first = false;
            } else {
                series = Math.min(series, item.sizeT(progressReporter));
            }
        }
        return series;
    }

    @Override
    public boolean hasChannel(String chnlName) {
        for (NamedChannelsForSeries item : list) {
            if (item.channelNames().contains(chnlName)) {
                return true;
            }
        }
        return false;
    }

    public ImageDimensions dimensions() throws RasterIOException {
        // Assumes dimensions are the same for every item in the list
        return list.get(0).dimensions();
    }

    public Iterator<NamedChannelsForSeries> iteratorFromRaster() {
        return list.iterator();
    }

    @Override
    public Operation<Stack, OperationFailedException> allChannelsAsStack(int t) {
        return new WrapOperationAsCached<>(() -> stackAllChnls(t));
    }

    private Stack stackAllChnls(int t) throws OperationFailedException {
        Stack out = new Stack();
        for (NamedChannelsForSeries ncc : list) {
            try {
                addAllChnlsFrom(ncc.allChannelsAsStack(t).doOperation(), out);
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    private static void addAllChnlsFrom(Stack src, Stack dest) throws IncorrectImageSizeException {
        for (Channel c : src) {
            dest.addChannel(c);
        }
    }
}
