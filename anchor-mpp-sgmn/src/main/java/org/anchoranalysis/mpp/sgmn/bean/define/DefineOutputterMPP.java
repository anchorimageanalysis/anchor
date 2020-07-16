/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.io.input.MultiInput;

/**
 * Helper for tasks that uses a {@link Define} in association with an input to execute some tasks,
 * and then outputs results *
 *
 * @param <T> input-object type
 */
public class DefineOutputterMPP extends DefineOutputter {

    @FunctionalInterface
    public interface OperationWithInitParams<T> {
        void process(T initParams) throws OperationFailedException;
    }

    public void processInput(NamedChnlCollectionForSeries ncc, BoundIOContext context)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(context);
            ncc.addAsSeparateChnls(
                    new WrapStackAsTimeSequenceStore(initParams.getImage().getStackCollection()),
                    0);

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputImage(
            MultiInput input,
            BoundIOContext context,
            OperationWithInitParams<ImageInitParams> operation)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(input, context);

            operation.process(initParams.getImage());

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public void processInputMPP(
            MultiInput input,
            BoundIOContext context,
            OperationWithInitParams<MPPInitParams> operation)
            throws OperationFailedException {
        try {
            MPPInitParams initParams = super.createInitParams(input, context);

            operation.process(initParams);

            super.outputSharedObjects(initParams, context);

        } catch (CreateException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
