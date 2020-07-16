/* (C)2020 */
package org.anchoranalysis.feature.shared;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

public class SharedFeatureSet<T extends FeatureInput> {

    private NameValueSet<Feature<T>> set;

    public SharedFeatureSet(NameValueSet<Feature<T>> set) {
        super();
        this.set = set;
    }

    public void initRecursive(FeatureInitParams featureInitParams, Logger logger)
            throws InitException {
        for (NameValue<Feature<T>> nv : set) {
            nv.getValue().initRecursive(featureInitParams, logger);
        }
    }

    public NameValueSet<Feature<T>> getSet() {
        return set;
    }

    public Feature<T> getException(String name) throws NamedProviderGetException {
        return set.getException(name);
    }
}
