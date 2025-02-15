/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * An entity that will eventually be drawn on top of an image.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Overlay {

    /**
     * A bounding-box around the overlay.
     *
     * @param overlayWriter writes the overlay on the image.
     * @param dimensions the dimensions of the containing-image or scene.
     * @return the bounding-box.
     */
    public abstract BoundingBox box(DrawOverlay overlayWriter, Dimensions dimensions);

    /**
     * Derives an object-mask representation of the overlay to be drawn with {@code overlayWriter}.
     *
     * @param dimensionsEntireImage how large the image is onto which the overlay will be drawn.
     * @param binaryValuesOut what constitutes on and off pixels in the produced object-mask.
     * @return the created object-mask with associated properties.
     * @throws CreateException if the object cannot be created successfully.
     */
    public abstract ObjectWithProperties createObject(
            Dimensions dimensionsEntireImage, BinaryValuesByte binaryValuesOut)
            throws CreateException;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
