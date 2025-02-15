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

package org.anchoranalysis.image.core.channel.factory;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/** Creates a {@link Channel} for a specific data-type. */
@AllArgsConstructor
@Accessors(fluent = true)
public abstract class ChannelFactorySingleType {

    @Getter private final VoxelDataType dataType;
    private final VoxelsFactoryTypeBound<?> factory;

    /**
     * Create a {@link Channel} initialized with zero-valued voxel-buffers.
     *
     * @param dimensions the size of the channel.
     * @return the created channel.
     */
    public Channel createEmptyInitialised(Dimensions dimensions) {
        return create(factory.createInitialized(dimensions.extent()), dimensions.resolution());
    }

    /**
     * Create a {@link Channel} without initialization with voxel-buffers.
     *
     * @param dimensions the size of the channel.
     * @return the created channel.
     */
    public Channel createEmptyUninitialised(Dimensions dimensions) {
        return create(factory.createUninitialized(dimensions.extent()), dimensions.resolution());
    }

    /**
     * Create a {@link Channel} from voxels and a resolution.
     *
     * @param voxels the voxels, that will be used internally in the created {@link Channel}
     *     (without duplication).
     * @param resolution the resolution, if it exists.
     * @return the created channel.
     */
    public Channel create(Voxels<?> voxels, Optional<Resolution> resolution) {
        return new Channel(voxels, resolution);
    }
}
