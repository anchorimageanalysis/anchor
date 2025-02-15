package org.anchoranalysis.core.cache;

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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;

/**
 * A cache that discards items that haven't being used recently or frequently.
 *
 * <p>The discard strategy comes from Guava's size-based eviction's defaults.
 *
 * <p>See <a href="https://github.com/google/guava/wiki/CachesExplained">Guava Caches Explained</a>.
 *
 * <p>It's thread-safe.
 *
 * @param <K> key-type used in the cache
 * @param <V> value-type used in the cache
 * @author Owen Feehan
 */
public class LRUCache<K, V> {

    private LoadingCache<K, V> cache;

    /**
     * Constructor.
     *
     * @param <E> type of an exception that may be thrown by {@code calculator}.
     * @param cacheSize maximum-size of cache.
     * @param calculator calculates the value for a given key if it's not already in the cache.
     */
    public <E extends Exception> LRUCache(int cacheSize, CheckedFunction<K, V, E> calculator) {

        if (cacheSize <= 0) {
            throw new AnchorFriendlyRuntimeException("cacheSize must be a positive integer");
        }

        CacheLoader<K, V> loader =
                new CacheLoader<K, V>() {
                    public V load(K key) throws E {
                        return calculator.apply(key);
                    }
                };
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(loader);
    }

    /**
     * Get a value, calculating if necessary, and caching the result.
     *
     * @param key the key whose value will be either calculated freshly or retrieved from the cache.
     * @return the value of applying {@code calculator} (see constructor argument) on value {@code
     *     key}.
     * @throws GetOperationFailedException if the key doesn't exist.
     */
    public V get(K key) throws GetOperationFailedException {
        try {
            return cache.get(key);
        } catch (ExecutionException | UncheckedExecutionException e) {
            throw new GetOperationFailedException(key.toString(), e.getCause());
        }
    }

    /**
     * Is a particular key present already in the cache?
     *
     * @param key they key to check.
     * @return true iff the key already exists in the cache.
     */
    public boolean has(K key) {
        return cache.getIfPresent(key) != null;
    }

    /**
     * Number of items currently in the cache.
     *
     * @return the number of items.
     */
    public long sizeCurrentLoad() {
        return cache.size();
    }

    /**
     * Gets an value, if present, but doesn't create any new entry if it's absent.
     *
     * @param key the key.
     * @return an existing element if present or {@link Optional#empty()} otherwise.
     */
    public Optional<V> getIfPresent(K key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    /**
     * Puts a key-value pair irrespective of whether its already present or not.
     *
     * @param key the key.
     * @param value the value.
     */
    public void put(K key, V value) {
        cache.put(key, value);
    }
}
