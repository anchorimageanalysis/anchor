package org.anchoranalysis.core.functional;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.functional.checked.CheckedIntFunction;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.functional.checked.CheckedToIntFunction;

/** Map operations for streams that can throw checked-exceptions. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckedStream {

    /**
     * An exception that wraps another exception, but exposes itself as a {@link RuntimeException}.
     */
    public static class ConvertedToRuntimeException extends AnchorFriendlyRuntimeException {

        /** */
        private static final long serialVersionUID = 1L;

        @Getter private final Throwable exception;

        /**
         * Creates for any exception.
         *
         * @param exception the underlying exception to be wrapped as a run-time exception.
         */
        public ConvertedToRuntimeException(Throwable exception) {
            super(exception);
            this.exception = exception;
        }
    }

    /**
     * Performs a {@link Stream#forEach} but accepts a consumer that can throw a checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <T> type to consume.
     * @param  <E> exception that can be thrown by {@code mapFunction}.
     * @param stream the stream to apply the map on.
     * @param throwableClass the class of {@code E}.
     * @param consumer the function to call for each object in the stream.
     * @throws E if the exception is thrown during mapping.
     */
    public static <T, E extends Exception> void forEach(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            CheckedConsumer<T, E> consumer)
            throws E { // NOSONAR
        try {
            stream.forEach(item -> suppressCheckedException(item, consumer));

        } catch (ConvertedToRuntimeException e) {
            throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@link Stream#filter} but accepts a predicate that can throw a checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <T> type to consume
     * @param  <E> exception that can be thrown by {@code mapFunction}
     * @param stream the stream to apply the map on.
     * @param throwableClass the class of {@code E}.
     * @param predicate the predicate to call for each object in the stream.
     * @return elements from {@code stream} that match the predicate.
     * @throws E if the exception is thrown during filtering.
     */
    public static <T, E extends Exception> Stream<T> filter(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            CheckedPredicate<T, E> predicate)
            throws E { // NOSONAR
        try {
            return stream.filter(item -> suppressCheckedException(item, predicate));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@link Stream#map} but accepts a function that can throw a checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <S> input-type to map
     * @param  <T> output-type of map
     * @param  <E> exception that can be thrown by {@code mapFunction}
     * @param stream the stream to apply the map on.
     * @param throwableClass the class of {@code E}.
     * @param mapFunction the function to use for mapping.
     * @return the output of the flatMap.
     * @throws E if the exception is thrown during mapping.
     */
    public static <S, T, E extends Exception> Stream<T> map(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, T, E> mapFunction)
            throws E { // NOSONAR
        try {
            return stream.map(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@link Stream#mapToInt} but accepts a function that can throw a checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <S> input-type to map
     * @param  <E> exception that can be thrown by {@code mapFunction}
     * @param stream the stream to apply the map on.
     * @param throwableClass the class of {@code E}.
     * @param mapFunction the function to use for mapping.
     * @return the output of the flatMap.
     * @throws E if the exception is thrown during mapping.
     */
    public static <S, E extends Exception> IntStream mapToInt(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedToIntFunction<S, E> mapFunction)
            throws E { // NOSONAR
        try {
            return stream.mapToInt(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Creates a new feature-list by mapping integers (from a range) each to an optional feature
     * accepting a checked-exception
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * @param <T> end-type for mapping
     * @param <E> an exception that be thrown during mapping
     * @param stream stream of {@code int}s.
     * @param throwableClass the class of {@code E}.
     * @param mapFunc function for mapping.
     * @return the stream after the mapping.
     * @throws E if {@code mapFunc} throws it.
     */
    public static <T, E extends Exception> Stream<T> mapIntStream(
            IntStream stream,
            Class<? extends Exception> throwableClass,
            CheckedIntFunction<T, E> mapFunc)
            throws E { // NOSONAR
        try {
            return stream.mapToObj(index -> suppressCheckedException(index, mapFunc));
        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@link IntStream#mapToObj} but accepts a function that can throw a
     * checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <T> object-type to map-to.
     * @param  <E> exception that can be thrown by {@code mapFunction}.
     * @param stream the stream to apply the map on.
     * @param throwableClass the class of {@code E}.
     * @param mapFunction the function to use for mapping.
     * @return the output of the flatMap.
     * @throws E if the exception is thrown during mapping.
     */
    public static <T, E extends Exception> Stream<T> mapToObj(
            IntStream stream,
            Class<? extends Exception> throwableClass,
            CheckedIntFunction<T, E> mapFunction)
            throws E { // NOSONAR
        try {
            return stream.mapToObj(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@link Stream#flatMap} but accepts a function that can throw a checked-exception
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param <S> input-type to flatMap
     * @param <T> output-type of flatMap
     * @param <E> exception that can be thrown by {@code flatMapFunction}.
     * @param stream the stream to apply the flatMap on.
     * @param throwableClass the class of {@code E}.
     * @param flatMapFunction the function to use for flatMapping.
     * @return the output of the flatMap.
     * @throws E if the exception.
     */
    public static <S, T, E extends Exception> Stream<T> flatMap(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, Stream<? extends T>, E> flatMapFunction)
            throws E { // NOSONAR
        try {
            return stream.flatMap(item -> suppressCheckedException(item, flatMapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * Rethrows either the cause of a run-time exception (as a checked exception) or the run-time
     * exception itself, depending if the cause matches the expected type.
     *
     * @param <T> return-type (nothing ever returned, this is just to keep types compatible in a
     *     nice way)
     * @param <E> the exception type that may be the "cause" of the {@link
     *     ConvertedToRuntimeException}, in which case, it would be rethrown
     * @param e the exception, which will be either rethrown as-is, or its cause will be rethrown.
     * @param throwableClass the class of {@code E}.
     * @return nothing, as an exception will always be thrown.
     * @throws E always, rethrowing either the run-time exception or its cause.
     */
    private static <T, E extends Exception> T throwException(
            ConvertedToRuntimeException e, Class<? extends Exception> throwableClass) throws E {
        if (throwableClass.isAssignableFrom(e.getException().getClass())) {
            throw (E) e.getException();
        } else {
            throw e;
        }
    }

    /**
     * Catches any exceptions that occur around a {@link CheckedFunction} as it is executed and
     * wraps them into a run-time exception.
     *
     * @param <S> parameter-type for function
     * @param <T> return-type for function
     * @param <E> checked-exception that can be thrown by function.
     * @param param the parameter to apply to the function.
     * @param function the function.
     * @return the return-value of the function.
     * @throws ConvertedToRuntimeException a run-time exception if an exception is thrown by {@code
     *     function}.
     */
    private static <S, T, E extends Exception> T suppressCheckedException(
            S param, CheckedFunction<S, T, E> function) {
        try {
            return function.apply(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link CheckedToIntFunction}
     * functions.
     */
    private static <S, E extends Exception> int suppressCheckedException(
            S param, CheckedToIntFunction<S, E> function) {
        try {
            return function.applyAsInt(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link CheckedIntFunction}
     * functions.
     */
    private static <T, E extends Exception> T suppressCheckedException(
            int param, CheckedIntFunction<T, E> function) {
        try {
            return function.apply(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link CheckedConsumer} functions.
     */
    private static <T, E extends Exception> void suppressCheckedException(
            T param, CheckedConsumer<T, E> consumer) {
        try {
            consumer.accept(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link CheckedPredicate} functions.
     */
    private static <T, E extends Exception> boolean suppressCheckedException(
            T param, CheckedPredicate<T, E> predicate) {
        try {
            return predicate.test(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }
}
