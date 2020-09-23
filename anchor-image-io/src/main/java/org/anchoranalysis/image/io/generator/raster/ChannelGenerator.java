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

package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;

/**
 * Writes a single channel to a file.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class ChannelGenerator extends RasterGeneratorWithElement<Channel> {

    /** Function that is associated in the manifest with this output. */
    private final String manifestFunction;
    
    /** If true, any channel passed to the generator is guaranteed to be 2D. */
    private final boolean always2D;

    /**
     * Creates a generator with a particular channel assigned and with {@code always2D} calculated from this channel.
     * 
     * @param manifestFunction the manifest-function
     * @param channel the channel to assign
     */
    public ChannelGenerator(String manifestFunction, Channel channel) {
        this(manifestFunction, channel.extent().z()==1 );
        assignElement(channel);
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {

        if (getElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        return new Stack(getElement());
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    @Override
    public RasterWriteOptions rasterWriteOptions() {
        return RasterWriteOptions.singleChannelMaybe3D(always2D);
    }
}
