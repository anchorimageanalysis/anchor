package org.anchoranalysis.image.stack;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Helps a retrieve channel and an associated converter and apply operation on them jointly
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ChannelMapper {

    private IntFunction<Channel> channelGetter;
    private IntFunction<Optional<ChannelConverterAttached<Channel, ByteBuffer>>> converterGetter;

    public <T> T mapChannelIfSupported(
            int channelIndex,
            BiFunction<Channel, ChannelConverterAttached<Channel, ByteBuffer>, T> mapper,
            Function<Channel, T> fallback) {

        Channel channel = channelGetter.apply(channelIndex);
        Optional<ChannelConverterAttached<Channel, ByteBuffer>> optional =
                converterGetter.apply(channelIndex);

        if (optional.isPresent()) {
            return mapper.apply(channel, optional.get());
        } else {
            if (!channel.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
                // Datatype is not supported
                throw new AnchorFriendlyRuntimeException("Unsupported data-type");
            }
            return fallback.apply(channel);
        }
    }

    public void callChannelIfSupported(
            int channelIndex,
            BiConsumer<Channel, ChannelConverterAttached<Channel, ByteBuffer>> consumer,
            Consumer<Channel> fallback) {
        // We perform the call via a mapping that returns null
        mapChannelIfSupported(channelIndex, convertConsumer(consumer), convertConsumer(fallback));
    }

    private static <S, T> BiFunction<S, T, Void> convertConsumer(BiConsumer<S, T> consumer) {
        return (input1, input2) -> {
            consumer.accept(input1, input2);
            return null;
        };
    }

    private static <T> Function<T, Void> convertConsumer(Consumer<T> consumer) {
        return input -> {
            consumer.accept(input);
            return null;
        };
    }
}
