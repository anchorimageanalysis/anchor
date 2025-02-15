/*-
 * #%L
 * anchor-image-inference
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.inference.segment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.bean.displayer.StackDisplayer;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * The background to a segmentation.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SegmentedBackground {

    /** The background image to use for segmentation, when visualizing segmentations. */
    @Getter private final DualScale<Stack> background;

    /** How to convert the background in an image suitable to be displayed. */
    @Getter private final StackDisplayer displayer;

    /**
     * The background scaled to match the size of the input-image.
     *
     * @return the element, at the requested scale.
     */
    public Stack atInputScale() {
        return background.atInputScale();
    }

    /**
     * The background scaled to match the size of the input for model inference.
     *
     * @return the element, at the requested scale.
     */
    public Stack atModelScale() {
        return background.atModelScale();
    }
}
