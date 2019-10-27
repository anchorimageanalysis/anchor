package org.anchoranalysis.core.color;

/*
 * #%L
 * anchor-core
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


import java.util.ArrayList;
import java.util.Collections;

public class ColorList extends ArrayList<RGBColor> implements ColorIndex {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8370813904227905624L;

	public ColorList() {
		super();
	}
	
	public ColorList( RGBColor color ) {
		addWithIndex(color);
	}
	
	@Override
	public int numUniqueColors() {
		return size();
	}
	
	public void shuffle() {
		Collections.shuffle(this);
	}
	
	public int addWithIndex( RGBColor color ) {
		int id = size();
		add(color);
		return id;
	}
	
	public void addMultiple( RGBColor color, int multiple ) {
		for( int i=0; i<multiple; i++ ) {
			add(color);
		}
	}
	
	
	public void addAllScaled( ColorList list, double scale  ) {
		for( RGBColor c : list ) {
			RGBColor scaled = new RGBColor();
			scaled.setRed( (int) (c.getRed()*scale) );
			scaled.setGreen( (int) (c.getGreen()*scale) );
			scaled.setBlue( (int) (c.getBlue()*scale) );
			this.add( scaled );
		}
	}
	
	public ColorList shallowCopy() {
		
		ColorList listOut = new ColorList();
		
		// We copy all the marks
		for( RGBColor color : this ) {
			listOut.add( color );
		}
		
		return listOut;
	}
	
	public ColorList deepCopy() {
		
		ColorList listOut = new ColorList();
		
		// We copy all the marks
		for( RGBColor color : this ) {
			listOut.add( color.duplicate() );
		}
		
		return listOut;
	}

	@Override
	public boolean has(int i) {
		return i < size();
	}
}
