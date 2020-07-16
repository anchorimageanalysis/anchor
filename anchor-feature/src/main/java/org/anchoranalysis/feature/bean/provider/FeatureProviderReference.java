/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureProviderReference extends FeatureProvider<FeatureInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";

    @BeanField @Getter @Setter private String featureListRef = "";
    // END BEAN PROPERTIES

    private Feature<FeatureInput> feature;

    @Override
    public Feature<FeatureInput> create() throws CreateException {
        if (feature == null) {
            if (getInitializationParameters().getSharedFeatureSet() == null) {
                throw new CreateException("sharedFeatureSet is null");
            }

            if (featureListRef != null && !featureListRef.isEmpty()) {
                // We request this to make sure it's evaluated and added to the
                // pso.getSharedFeatureSet()
                try {
                    getInitializationParameters().getFeatureListSet().getException(featureListRef);
                } catch (NamedProviderGetException e) {
                    throw new CreateException(e.summarize());
                }
            }

            try {
                this.feature = getInitializationParameters().getSharedFeatureSet().getException(id);
            } catch (NamedProviderGetException e) {
                throw new CreateException(e.summarize());
            }
        }
        return feature;
    }
}
