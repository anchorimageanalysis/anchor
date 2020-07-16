/*-
 * #%L
 * anchor-image-feature
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
/* (C)2020 */
package org.anchoranalysis.image.feature.bean.evaluator;

import java.util.Optional;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleChangeInput;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;

/**
 * @author Owen Feehan
 * @param <T> feature-calculation parameters
 */
public class FeatureEvaluatorNrgStack<T extends FeatureInput> extends FeatureEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean private StackProvider stackProviderNRG;

    @BeanField @OptionalBean private KeyValueParamsProvider keyValueParamsProvider;
    // END BEAN PROPERTIES

    @Override
    public FeatureCalculatorSingle<T> createAndStartSession() throws OperationFailedException {

        FeatureCalculatorSingle<T> session = super.createAndStartSession();

        final Optional<NRGStackWithParams> nrgStack = nrgStack();

        return new FeatureCalculatorSingleChangeInput<>(
                session,
                params -> {
                    // Use reflection, to only set the nrgStack on params that supports them
                    if (params instanceof FeatureInputNRG && nrgStack.isPresent()) {
                        ((FeatureInputNRG) params).setNrgStack(nrgStack.get());
                    }
                });
    }

    public Optional<NRGStackWithParams> nrgStack() throws OperationFailedException {
        try {
            if (stackProviderNRG != null) {

                NRGStackWithParams nrgStack = new NRGStackWithParams(stackProviderNRG.create());
                nrgStack.setParams(createKeyValueParams());
                return Optional.of(nrgStack);
            } else {
                return Optional.empty();
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    private KeyValueParams createKeyValueParams() throws OperationFailedException {
        if (keyValueParamsProvider != null) {
            try {
                return keyValueParamsProvider.create();
            } catch (CreateException e) {
                throw new OperationFailedException(e);
            }
        } else {
            return new KeyValueParams();
        }
    }

    public StackProvider getStackProviderNRG() {
        return stackProviderNRG;
    }

    public void setStackProviderNRG(StackProvider stackProviderNRG) {
        this.stackProviderNRG = stackProviderNRG;
    }

    public KeyValueParamsProvider getKeyValueParamsProvider() {
        return keyValueParamsProvider;
    }

    public void setKeyValueParamsProvider(KeyValueParamsProvider keyValueParamsProvider) {
        this.keyValueParamsProvider = keyValueParamsProvider;
    }
}
