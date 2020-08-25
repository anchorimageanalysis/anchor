/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.ExtractFromParam;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.initializable.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.bean.init.PointsInitParams;

public abstract class MPPBean<T> extends InitializableBean<T, MPPInitParams> {

    protected MPPBean() {
        super(initializerForMPPBeans(), new SimplePropertyDefiner<>(MPPInitParams.class));
    }

    /**
     * Creates a property-initializes for MPP-Beans
     *
     * <p>Beware concuirrency. Initializers are stateful with the {#link {@link
     * PropertyInitializer#setParam(Object)} method so this should be created newly for each thread,
     * rather reused statically
     *
     * @return
     */
    public static PropertyInitializer<MPPInitParams> initializerForMPPBeans() {
        return new PropertyInitializer<>(MPPInitParams.class, paramExtracters());
    }

    private static List<ExtractFromParam<MPPInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(PointsInitParams.class, MPPInitParams::getPoints),
                new ExtractFromParam<>(SharedFeaturesInitParams.class, MPPInitParams::getFeature),
                new ExtractFromParam<>(KeyValueParamsInitParams.class, MPPInitParams::getParams),
                new ExtractFromParam<>(ImageInitParams.class, MPPInitParams::getImage));
    }
}
