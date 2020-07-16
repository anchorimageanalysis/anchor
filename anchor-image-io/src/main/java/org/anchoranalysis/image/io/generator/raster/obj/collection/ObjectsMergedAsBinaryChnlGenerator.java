/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.collection;

import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ops.BinaryChnlFromObjects;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes objects a binary-mask (with all objects merged together for the mask)
 *
 * @author Owen Feehan
 */
public class ObjectsMergedAsBinaryChnlGenerator extends ObjectsGenerator {

    public ObjectsMergedAsBinaryChnlGenerator(ImageDimensions dimensions) {
        super(dimensions);
    }

    public ObjectsMergedAsBinaryChnlGenerator(ImageDimensions dimensions, ObjectCollection masks) {
        super(dimensions, masks);
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        Mask chnl =
                BinaryChnlFromObjects.createFromObjects(
                        getObjects(), getDimensions(), BinaryValues.getDefault());
        return new ChnlGenerator(chnl.getChannel(), "maskCollection").generate();
    }
}
