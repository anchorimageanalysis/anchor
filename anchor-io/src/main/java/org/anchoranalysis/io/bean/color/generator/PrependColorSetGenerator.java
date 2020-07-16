package org.anchoranalysis.io.bean.color.generator;

/*
 * #%L
 * anchor-io
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.RGBColorBean;

public class PrependColorSetGenerator extends ColorSetGenerator {

	// START BEAN PROPERTIES
	@BeanField
	private ColorSetGenerator source = null;
	
	@BeanField
	private RGBColorBean prependColor = null;
	// END BEAN PROPERTIES
	
	public PrependColorSetGenerator() {
		
	}
	
	public PrependColorSetGenerator(ColorSetGenerator source, RGBColor prependColor ) {
		super();
		this.source = source;
		this.prependColor = new RGBColorBean(prependColor);
	}

	@Override
	public ColorList generateColors(int numberColors) throws OperationFailedException {
		ColorList lst = source.generateColors(numberColors);
		lst.shuffle();
		
		lst.add(0, prependColor.rgbColor() );
		
		return lst;
	}

	public ColorSetGenerator getSource() {
		return source;
	}

	public void setSource(ColorSetGenerator source) {
		this.source = source;
	}

	public RGBColorBean getPrependColor() {
		return prependColor;
	}

	public void setPrependColor(RGBColorBean prependColor) {
		this.prependColor = prependColor;
	}
}
