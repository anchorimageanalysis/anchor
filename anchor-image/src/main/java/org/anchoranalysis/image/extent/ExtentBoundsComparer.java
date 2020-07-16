/* (C)2020 */
package org.anchoranalysis.image.extent;

import java.util.Optional;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/** Helper classes for calculating the union/intersection along each axis */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ExtentBoundsComparer {

    private final int min;
    private final int extent;

    public static ExtentBoundsComparer createMax(
            ReadableTuple3i min1,
            ReadableTuple3i min2,
            ReadableTuple3i max1,
            ReadableTuple3i max2,
            ToIntFunction<ReadableTuple3i> extract) {
        return calc(
                        extract.applyAsInt(min1),
                        extract.applyAsInt(min2),
                        extract.applyAsInt(max1),
                        extract.applyAsInt(max2),
                        Math::min,
                        Math::max)
                .orElseThrow(AnchorImpossibleSituationException::new);
    }

    public static Optional<ExtentBoundsComparer> createMin(
            ReadableTuple3i min1,
            ReadableTuple3i min2,
            ReadableTuple3i max1,
            ReadableTuple3i max2,
            ToIntFunction<ReadableTuple3i> extract) {
        return calc(
                extract.applyAsInt(min1),
                extract.applyAsInt(min2),
                extract.applyAsInt(max1),
                extract.applyAsInt(max2),
                Math::max,
                Math::min);
    }

    private static Optional<ExtentBoundsComparer> calc(
            int min1,
            int min2,
            int max1,
            int max2,
            IntBinaryOperator minOp,
            IntBinaryOperator maxOp) {
        int minNew = minOp.applyAsInt(min1, min2);
        int maxNew = maxOp.applyAsInt(max1, max2);
        if (minNew <= maxNew) {
            return Optional.of(new ExtentBoundsComparer(minNew, maxNew - minNew + 1));
        } else {
            return Optional.empty();
        }
    }
}
