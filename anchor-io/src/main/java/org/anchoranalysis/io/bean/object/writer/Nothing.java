/* (C)2020 */
package org.anchoranalysis.io.bean.object.writer;

import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Writes nothing for a particular object.
 *
 * @author Owen Feehan
 */
public class Nothing extends DrawObject {

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {
        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {
                // Nothing to DO
            }
        };
    }
}
