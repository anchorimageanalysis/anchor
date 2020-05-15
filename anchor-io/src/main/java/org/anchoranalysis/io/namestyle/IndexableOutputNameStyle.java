package org.anchoranalysis.io.namestyle;

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


public abstract class IndexableOutputNameStyle extends OutputNameStyle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2393013576294162543L;
	
	private String outputName;
	
	// Only for deserialization
	public IndexableOutputNameStyle() {
		super();
	}
	
	public IndexableOutputNameStyle(String outputName) {
		this.outputName = outputName;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src source
	 */
	protected IndexableOutputNameStyle(IndexableOutputNameStyle src) {
		this.outputName = src.outputName;
	}
	
	protected abstract String outputFormatString();

	public String getPhysicalName( int index ){
		return getPhysicalName(Integer.toString(index) );
	}
	
	/** The full physical name written to the file, including prefix, suffix, index etc. */
	public String getPhysicalName(String index) {
		return nameFromOutputFormatString(
			outputFormatString(),
			index
		);
	}
	@Override
	public abstract IndexableOutputNameStyle duplicate();
	
	/** Constructs a full name from the output format string and an index */
	protected abstract String nameFromOutputFormatString( String outputFormatString, String index );
	
	@Override
	public String getOutputName() {
		return outputName;
	}

	@Override
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	
	@Override
	public String getPhysicalName() {
		throw new UnsupportedOperationException("an index is required for getPhysicalName in this class");
	}
}
