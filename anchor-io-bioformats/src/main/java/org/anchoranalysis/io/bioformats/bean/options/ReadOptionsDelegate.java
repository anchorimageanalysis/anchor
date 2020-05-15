package org.anchoranalysis.io.bioformats.bean.options;

/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;

import loci.formats.IFormatReader;

/**
 * Forces a particular settings, but otherwise uses settings from a delegate
 * 
 * <p>Sub-classes of this deliberately break the Liskov substitution principle by replacing existing behaviour</p>
 * 
 * @author Owen Feehan
 *
 */
public abstract class ReadOptionsDelegate extends ReadOptions {

	// START BEAN PROPERTIES
	@BeanField
	private ReadOptions options = new Default();
	// END BEAN PROPERTIES
	
	@Override
	public List<String> determineChannelNames(IFormatReader reader) {
		return options.determineChannelNames(reader);
	}
		
	@Override
	public int sizeT(IFormatReader reader) {
		return options.sizeT(reader);
	}

	@Override
	public int sizeZ(IFormatReader reader) {
		return options.sizeZ(reader);
	}

	@Override
	public int sizeC(IFormatReader reader) {
		return options.sizeC(reader);
	}
	
	@Override
	public boolean isRGB(IFormatReader reader) {
		return options.isRGB(reader);
	}
	
	@Override
	public int effectiveBitsPerPixel(IFormatReader reader) {
		return options.effectiveBitsPerPixel(reader);
	}
	
	@Override
	public int chnlsPerByteArray(IFormatReader reader) {
		return options.chnlsPerByteArray(reader);
	}
		
	protected ReadOptions delegate() {
		return options;
	}
	
	public ReadOptions getOptions() {
		return options;
	}

	public void setOptions(ReadOptions options) {
		this.options = options;
	}
}
