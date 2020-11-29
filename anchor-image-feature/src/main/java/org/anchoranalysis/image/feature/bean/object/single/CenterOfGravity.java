/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.bean.object.single;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.SessionInput;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.spatial.axis.AxisType;
import org.anchoranalysis.spatial.axis.AxisTypeConverter;
import org.anchoranalysis.spatial.axis.AxisTypeException;

@NoArgsConstructor
public class CenterOfGravity extends FeatureSingleObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String axis = "x";

    @BeanField @Getter @Setter private double emptyValue = 0;
    // END BEAN PROPERTIES

    /**
     * Creates for a specific axis.
     *
     * @param axis axis
     */
    public CenterOfGravity(AxisType axis) {
        this.axis = axis.toString().toLowerCase();
    }

    @Override
    public double calculate(SessionInput<FeatureInputSingleObject> input)
            throws FeatureCalculationException {

        FeatureInputSingleObject params = input.get();

        double val = params.getObject().centerOfGravity(axisType());

        if (Double.isNaN(val)) {
            return emptyValue;
        }

        return val;
    }

    private AxisType axisType() throws FeatureCalculationException {
        try {
            return AxisTypeConverter.createFromString(axis);
        } catch (AxisTypeException e) {
            throw new FeatureCalculationException(e.friendlyMessageHierarchy());
        }
    }
}
