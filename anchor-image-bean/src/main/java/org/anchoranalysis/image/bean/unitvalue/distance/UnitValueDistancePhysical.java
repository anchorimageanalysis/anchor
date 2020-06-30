package org.anchoranalysis.image.bean.unitvalue.distance;

import java.util.Optional;

/*
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

// Measures either area or volume (depending if the do3D flag is employed)
public class UnitValueDistancePhysical extends UnitValueDistance {

	// START BEAN PROPERTIES
	@BeanField
	private double value;
	
	@BeanField @AllowEmpty
	private String unitType = "";
	// END BEAN PROPERTIES

	@Override
	public double rslv(Optional<ImageResolution> res, DirectionVector dirVector) throws OperationFailedException {
		
		if (!res.isPresent()) {
			throw new OperationFailedException("An image-resolution is missing, so cannot calculate physical distances");
		}
				
		UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(unitType);
		
		double valueAsBase = SpatialConversionUtilities.convertFromUnits(value, unitPrefix);
		
		return ImageUnitConverter.convertFromPhysicalDistance(
			valueAsBase,
			res.get(),
			dirVector
		);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

}
