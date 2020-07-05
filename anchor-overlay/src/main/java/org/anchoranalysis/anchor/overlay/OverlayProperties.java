package org.anchoranalysis.anchor.overlay;

/*-
 * #%L
 * anchor-overlay
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;

public class OverlayProperties implements Iterable<NameValue<String>> {

	private NameValueSet<String> nameValueSet;

	public OverlayProperties() {
		super();
		this.nameValueSet = new NameValueSet<>();
	}
	
	public void add( String name, String value ) {
		nameValueSet.add(
			new SimpleNameValue<>(name, value)
		);
	}
	
	public void add( String name, int value ) {
		add(name, Integer.toString(value)); 
	}
	
	public void addWithUnits(
		String name,
		double value,
		DoubleUnaryOperator convertToUnits,
		UnitSuffix unitSuffix
	) {
		double valueUnits = SpatialConversionUtilities.convertToUnits(
			convertToUnits.applyAsDouble(value),
			unitSuffix
		);
		
		add(
			name,
			String.format(
				"%2.2f (%.2f%s)",
				value,
				valueUnits,
				SpatialConversionUtilities.unitMeterStringDisplay(
					unitSuffix
				)
			)
		);
	}
	
	public void addDoubleAsString( String name, double val ) {
		addDoubleAsString(name, val, "%1.2f");
	}

	public NameValueSet<String> getNameValueSet() {
		return nameValueSet;
	}
	
	@Override
	public Iterator<NameValue<String>> iterator() {
		return nameValueSet.iterator();
	}

	private void addDoubleAsString( String name, double val, String precision ) {
		add(name, String.format(precision, val)	);
	}
}
