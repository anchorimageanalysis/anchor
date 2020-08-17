/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster.object.rgb;

import io.vavr.control.Either;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.color.generator.HSBColorSetGenerator;

/**
 * Generates stacks of RGB images using a {@link DrawObject} to draw objects on a background.
 *
 * @param T pre-calculation type for the {@link DrawObject}
 * @author Owen Feehan
 */
public class DrawObjectsGenerator extends ObjectsOnRGBGenerator {

    // HSBColorSetGenerator

    public DrawObjectsGenerator(DrawObject drawObject, ColorIndex colorIndex) {
        this(
                drawObject,
                null, // No element yet to iterate
                null, // No background set yet
                new ObjectDrawAttributes(colorIndex));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<ImageDimensions, DisplayStack> background) {
        this(drawObject, objects, background, defaultColorsFor(objects));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<ImageDimensions, DisplayStack> background,
            ColorIndex colorIndex) {
        this(drawObject, objects, background, new ObjectDrawAttributes(colorIndex));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<ImageDimensions, DisplayStack> background,
            ObjectDrawAttributes attributes) {
        super(drawObject, attributes, background);
        this.setIterableElement(objects);
    }

    @Override
    protected RGBStack generateBackground(Either<ImageDimensions, DisplayStack> background) {
        return background.fold(
                DrawObjectsGenerator::createEmptyStackFor, ConvertDisplayStackToRGB::convert);
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks() {
        return getIterableElement();
    }

    private static ColorIndex defaultColorsFor(ObjectCollectionWithProperties objects) {
        return new HSBColorSetGenerator().generateColors(objects.size());
    }
}
