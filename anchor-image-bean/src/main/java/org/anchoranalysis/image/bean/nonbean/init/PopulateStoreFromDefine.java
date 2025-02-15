/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.init;

import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;

/**
 * Helps populates a {@link NamedProviderStore} from the contents of a {@link Define}.
 *
 * <p>Objects can be added directly (no initialization) or with initialization.
 *
 * @author Owen Feehan
 * @param <V> initialization-parameters for provider
 */
@AllArgsConstructor
public class PopulateStoreFromDefine<V extends BeanInitialization> {

    /** Define source for objects. */
    private Define define;

    /** Initializes the properties of objects, where initialization is required. */
    private BeanInitializer<?> propertyInitializer;

    /** Passed to objects added with initialization. */
    private Logger logger;

    /**
     * Copies objects of a particular class from the define <b>without</b> performing any
     * initialization.
     *
     * @param <S> type of objects
     * @param defineClass class to identify objects in {@code define}.
     * @param destination where to copy to.
     * @throws OperationFailedException if the identifier already exists, or otherwise the add
     *     operation fails.
     */
    public <S extends AnchorBean<S>> void copyWithoutInitialize(
            Class<?> defineClass, NamedProviderStore<S> destination)
            throws OperationFailedException {
        StoreAdderHelper.addPreserveName(define, defineClass, destination, identity());
    }

    /**
     * Copies objects of a particular class from the define <i>and</i> initializes.
     *
     * @param <S> type of objects
     * @param defineClass class to identify objects in {@code define}.
     * @param destination where to copy to.
     * @throws OperationFailedException if a copied identifier already exists, or otherwise the add
     *     operation fails.
     */
    public <S extends InitializableBean<S, V>> void copyInitialize(
            Class<?> defineClass, NamedProviderStore<S> destination)
            throws OperationFailedException {

        // Initializes and returns the input
        CheckedFunction<S, S, OperationFailedException> bridge =
                new InitializingBridge<>(propertyInitializer, logger, identity());

        StoreAdderHelper.addPreserveName(define, defineClass, destination, bridge);
    }

    /**
     * Copies objects of a particular class (which must be a {@link Provider}) from {@code define}
     * <i>and</i> initializes each.
     *
     * <p>Specifically, each object will be lazily initialized once when first retrieved from the
     * store.
     *
     * @param <S> type of provider-objects
     * @param <T> type of objects created by the provider
     * @param defineClass class to identify objects in {@code define}.
     * @param destination where to copy to.
     * @throws OperationFailedException if a copied identifier already exists, or otherwise the add
     *     operation fails.
     */
    public <S extends InitializableBean<?, V> & Provider<T>, T> void copyProviderInitialize(
            Class<?> defineClass, NamedProviderStore<T> destination)
            throws OperationFailedException {

        InitializingBridge<S, T, V> bridge =
                new InitializingBridge<>(
                        propertyInitializer,
                        logger,
                        source -> source.get() // NOSONAR Initializes and then gets what's provided
                        );

        StoreAdderHelper.addPreserveName(define, defineClass, destination, bridge);
    }

    /** Maps a string to itself, but exposed as a {@link CheckedFunction}. */
    private static <S, E extends Exception> CheckedFunction<S, S, E> identity() {
        return str -> str;
    }
}
