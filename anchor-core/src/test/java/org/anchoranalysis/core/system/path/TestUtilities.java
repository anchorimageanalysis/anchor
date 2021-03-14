package org.anchoranalysis.core.system.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Utilties for asserting rest results.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TestUtilities {

    /**
     * Applies a unary function on each element in {@code elements} and asserts an expected result.
     *
     * @param elements a list of elements, on which a function is applied
     * @param function the function to apply
     * @param expected the result expected from each application of {@code function}
     * @param <S> element-type
     * @param <T> type of expected-result
     */
    public static <S, T> void assertUnary(
            List<S> elements, Function<S, T> function, List<T> expected) {
        assert (elements.size() == expected.size());
        Streams.forEachPair(
                elements.stream(),
                expected.stream(),
                (path, expectedElement) -> assertEquals(expectedElement, function.apply(path)));
    }

    /**
     * Applies a binary function on each element in {@code elements}, also accepting an argument for
     * each path, and asserts an expected result.
     *
     * @param elements a list of elements, on which a function is applied
     * @param function the function to apply
     * @param arguments each item is used as a second argument for {@code function} alongside the
     *     correspond item from {@code elements}
     * @param expected the result expected from each application of {@code function}
     */
    public static <S, T, U> void assertBinary(
            List<S> elements, BiFunction<S, U, T> function, List<U> arguments, List<T> expected) {
        assert (elements.size() == arguments.size());
        List<Pair<S, U>> pairs = FunctionalList.zip(elements, arguments);
        assertUnary(pairs, pair -> function.apply(pair.getLeft(), pair.getRight()), expected);
    }
}
