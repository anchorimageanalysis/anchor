/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.experiment.io.InitParamsContext;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.mpp.bean.MarksBean;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MPPInitParamsFactory {

    private static final String KEY_VALUE_PARAMS_IDENTIFIER = "input_params";

    public static MarksInitialization create(
            InitParamsContext context,
            Optional<Define> define,
            Optional<? extends InputForMPPBean> input)
            throws CreateException {

        SharedObjects sharedObjects = new SharedObjects(context.common());
        ImageInitialization imageInit =
                new ImageInitialization(sharedObjects, context.getSuggestedResize());
        MarksInitialization mppInit = new MarksInitialization(imageInit, sharedObjects);

        if (input.isPresent()) {
            try {
                input.get().addToSharedObjects(mppInit, imageInit);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        if (define.isPresent()) {
            try {
                // Tries to initialize any properties (of type MPPInitParams) found in the
                // NamedDefinitions
                PropertyInitializer<MarksInitialization> initializer = MarksBean.initializerForMarksBeans();
                initializer.setParam(mppInit);
                mppInit.populate(initializer, define.get(), context.getLogger());

            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        return mppInit;
    }

    public static MarksInitialization createFromExistingCollections(
            InitParamsContext context,
            Optional<Define> define,
            Optional<NamedProvider<Stack>> stacks,
            Optional<NamedProvider<ObjectCollection>> objects,
            Optional<Dictionary> keyValueParams)
            throws CreateException {

        try {
            MarksInitialization soMPP = create(context, define, Optional.empty());

            ImageInitialization soImage = soMPP.getImage();

            if (stacks.isPresent()) {
                soImage.copyStacksFrom(stacks.get());
            }

            if (objects.isPresent()) {
                soMPP.getImage().copyObjectsFrom(objects.get());
            }

            if (keyValueParams.isPresent()) {
                soImage.addDictionary(
                        KEY_VALUE_PARAMS_IDENTIFIER, keyValueParams.get());
            }

            return soMPP;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
