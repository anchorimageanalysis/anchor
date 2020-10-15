/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.unitvalue.extent.area;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.error.UnitValueException;
import org.anchoranalysis.image.dimensions.UnitConverter;

/**
 * Area expressed in physical coordinates
 *
 * @author Owen Feehan
 */
public class AreaPhysical extends UnitValueArea {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String unitType = "";
    // END BEAN PROPERTIES

    @Override
    public double resolveToVoxels(Optional<UnitConverter> unitConverter) throws UnitValueException {

        if (!unitConverter.isPresent()) {
            throw new UnitValueException(
                    "An image resolution is required to calculate physical-area but it is missing");
        }
        return unitConverter.get().fromPhysicalArea(getValue(), unitType);
    }

    @Override
    public String toString() {
        if (unitType != null && !unitType.isEmpty()) {
            return String.format("%.2f%s", getValue(), unitType);
        } else {
            return String.format("%.2f", getValue());
        }
    }
}
