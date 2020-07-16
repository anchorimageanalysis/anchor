/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean;

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.NullInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class NullParamsBean<T> extends InitializableBean<T, NullInitParams> {

    protected NullParamsBean() {
        super(
                new PropertyInitializer<NullInitParams>(NullInitParams.class),
                new SimplePropertyDefiner<NullInitParams>(NullInitParams.class));
    }

    @Override
    public final void onInit(NullInitParams so) throws InitException {
        onInit();
    }

    /** As there's no parameters we expose a different method */
    public void onInit() throws InitException {
        // NOTHING TO DO. This method exists so it can be overrided as needed in sub-classes.
    }

    public void initRecursive(Logger logger) throws InitException {
        super.initRecursive(NullInitParams.instance(), logger);
    }
}
