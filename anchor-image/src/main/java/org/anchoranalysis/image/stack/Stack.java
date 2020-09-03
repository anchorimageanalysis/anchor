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

package org.anchoranalysis.image.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.RepeatUtilities;
import org.anchoranalysis.core.functional.function.CheckedBiFunction;
import org.anchoranalysis.core.functional.function.CheckedUnaryOperator;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * One ore more single-channel images that all have the same dimensions.
 *
 * <p>This is one of the fundamental image data structures in Anchor.
 *
 * @author Owen Feehan
 */
public class Stack implements Iterable<Channel> {

    private final StackNotUniformSized delegate;

    public Stack() {
        delegate = new StackNotUniformSized();
    }

    public Stack(Channel channel) {
        delegate = new StackNotUniformSized(channel);
    }

    public Stack(Dimensions dimensions, ChannelFactorySingleType factory, int numberChannels) {
        this();
        RepeatUtilities.repeat(numberChannels, () -> delegate.addChannel(factory.createEmptyInitialised(dimensions)));
    }

    public Stack(Channel... channels) throws IncorrectImageSizeException {
        this();
        for (Channel channel : channels) {
            addChannel(channel);
        }
    }

    public Stack(Stream<Channel> channelStream) throws IncorrectImageSizeException {
        delegate = new StackNotUniformSized(channelStream);
        if (!delegate.isUniformlySized()) {
            throw new IncorrectImageSizeException("Channels in streams are not uniformly sized");
        }
    }

    private Stack(StackNotUniformSized stack) {
        delegate = stack;
    }

    /** Copy constructor */
    private Stack(Stack src) {
        delegate = src.delegate.duplicate();
    }

    /**
     * Produces a new stack with a particular mapping applied to each channel.
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely)
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order
     * @throws OperationFailedException if the channels produced have non-uniform sizes
     */
    public Stack mapChannel(CheckedUnaryOperator<Channel, OperationFailedException> mapping)
            throws OperationFailedException {
        Stack out = new Stack();
        for (Channel channel : this) {
            try {
                out.addChannel(mapping.apply(channel));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    /**
     * Produces a new stack with a particular mapping applied to each channel (with an index of the
     * channel also available)
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely)
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order
     * @throws OperationFailedException if the channels produced have non-uniform sizes
     */
    public Stack mapChannelWithIndex(
            CheckedBiFunction<Channel, Integer, Channel, OperationFailedException> mapping)
            throws OperationFailedException {
        Stack out = new Stack();
        for (int index = 0; index < getNumberChannels(); index++) {
            Channel channel = getChannel(index);
            try {
                out.addChannel(mapping.apply(channel, index));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    public Stack extractSlice(int z) {
        // We know the sizes will be correct
        return new Stack(delegate.extractSlice(z));
    }

    public Stack maximumIntensityProjection() {
        // We know the sizes will be correct
        return new Stack(delegate.maximumIntensityProjection());
    }

    public void addBlankChannel() throws OperationFailedException {

        if (getNumberChannels() == 0) {
            throw new OperationFailedException(
                    "At least one channel must exist from which to guess dimensions");
        }

        if (!delegate.isUniformlySized()) {
            throw new OperationFailedException(
                    "Other channels do not have the same dimensions. Cannot make a good guess of dimensions.");
        }

        if (!delegate.isUniformTyped()) {
            throw new OperationFailedException("Other channels do not have the same type.");
        }

        Channel first = getChannel(0);
        delegate.addChannel(
                ChannelFactory.instance().create(first.dimensions(), first.getVoxelDataType()));
    }

    public final void addChannel(Channel channel) throws IncorrectImageSizeException {

        // We ensure that this channel has the same size as the first
        if (delegate.getNumberChannels() >= 1
                && !channel.dimensions().equals(delegate.getChannel(0).dimensions())) {
            throw new IncorrectImageSizeException(
                    "Dimensions of channel do not match existing channel");
        }

        delegate.addChannel(channel);
    }

    public final void addChannelsFrom(Stack stack) throws IncorrectImageSizeException {
        for (int index = 0; index < stack.getNumberChannels(); index++) {
            addChannel(stack.getChannel(index));
        }
    }

    public final Channel getChannel(int index) {
        return delegate.getChannel(index);
    }

    public final int getNumberChannels() {
        return delegate.getNumberChannels();
    }

    public Dimensions dimensions() {
        return delegate.getChannel(0).dimensions();
    }
    
    public Resolution resolution() {
        return dimensions().resolution();
    }

    public Extent extent() {
        return dimensions().extent();
    }

    public Stack duplicate() {
        return new Stack(this);
    }

    public Stack extractUpToThreeChannels() {
        Stack out = new Stack();
        int maxNum = Math.min(3, delegate.getNumberChannels());
        for (int i = 0; i < maxNum; i++) {
            try {
                out.addChannel(delegate.getChannel(i));
            } catch (IncorrectImageSizeException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
        return out;
    }

    @Override
    public Iterator<Channel> iterator() {
        return delegate.iterator();
    }

    public List<Channel> asListChannels() {
        ArrayList<Channel> out = new ArrayList<>();
        for (Channel channel : delegate) {
            out.add(channel);
        }
        return out;
    }

    // Returns true if the data type of all channels is equal to
    public boolean allChannelsHaveType(VoxelDataType channelDataType) {

        for (Channel channel : this) {
            if (!channel.getVoxelDataType().equals(channelDataType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Stack)) {
            return false;
        }
        
        return equalsDeep( (Stack) obj, true);
    }

    /**
     * Are the two stack equal using a deep voxel by voxel comparison of each channel?
     * 
     * @param other the stack to compare with
     * @param compareResolution if true, the image-resolution is also compared for each channel.
     * @return true if they are deemed equals, false otherwise.
     */
    public boolean equalsDeep(Stack other, boolean compareResolution) {

        if (getNumberChannels() != other.getNumberChannels()) {
            return false;
        }

        for (int i = 0; i < getNumberChannels(); i++) {
            if (!getChannel(i).equalsDeep(other.getChannel(i), compareResolution)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder().append(getNumberChannels());

        for (Channel channel : this) {
            builder.append(channel);
        }

        return builder.toHashCode();
    }

    public void updateResolution(Resolution resolution) {
        for (Channel channel : this) {
            channel.updateResolution(resolution);
        }
    }
}
