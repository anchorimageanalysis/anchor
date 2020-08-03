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

package org.anchoranalysis.image.io.generator.raster.obj;

import java.util.Optional;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.IterableGeneratorBridge;
import org.anchoranalysis.io.generator.combined.IterableCombinedListGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;

/**
 * Like {@link org.anchoranalysis.image.io.generator.raster.obj.ObjectsAsBinaryChnlGenerator} but
 * also outputs a serialized bounding box.
 *
 * @author Owen Feehan
 */
public class ObjectWithBoundingBoxGenerator extends IterableCombinedListGenerator<ObjectMask> {

    public ObjectWithBoundingBoxGenerator(ImageResolution res) {
        this(new ObjectsAsBinaryChnlGenerator(BinaryValuesByte.getDefault().getOnByte(), res));
    }

    private ObjectWithBoundingBoxGenerator(IterableGenerator<ObjectMask> generator) {
        super(
                generator,
                // We create an iterable bridge from object-mask to BoundingBox
                IterableGeneratorBridge.createOneToOne(
                        new ObjectOutputStreamGenerator<>(Optional.of("BoundingBox")),
                        ObjectMask::getBoundingBox));
    }
}
