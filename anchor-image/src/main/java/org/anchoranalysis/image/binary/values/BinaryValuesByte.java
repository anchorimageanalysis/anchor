package org.anchoranalysis.image.binary.values;

import java.io.Serializable;

import org.anchoranalysis.image.convert.ByteConverter;

/*
 * #%L
 * anchor-image
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


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Two values representing {@link byte} binary states in an unsigned-byte buffer e.g. 0 for OFF and -1 for ON
 * 
 * <p>This class is IMMUTABLE</p>
 * 
 * <p>See {@link BinaryValueBytes} for an equivalent class that stores these states as {@link int}</p>
 * 
 * @author Owen Feehan
 *
 */
public final class BinaryValuesByte implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final BinaryValuesByte defaultBB = new BinaryValuesByte( (byte) 0, (byte) -1 );
	
	private final byte offByte;
	private final byte onByte;
		
	public BinaryValuesByte(byte off, byte on) {
		this.offByte = off;
		this.onByte = on;
	}
	
	public BinaryValuesByte(int off, int on) {
		this( 
			(byte) off,
			(byte) on
		);
	}
	
	public static BinaryValuesByte getDefault() {
		return defaultBB;
	}
	
	public byte getOffByte() {
		return offByte;
	}
	
	public byte getOnByte() {
		return onByte;
	}
	
	public boolean isOn( byte val ) {
		return val==onByte;
	}
	
	public boolean isOff( byte val ) {
		return !isOn(val);
	}
	
	public BinaryValuesByte invert() {
		return new BinaryValuesByte(this.offByte, this.onByte);
	}
	
	public BinaryValues createInt() {
		return new BinaryValues(
			ByteConverter.unsignedByteToInt(offByte),
			ByteConverter.unsignedByteToInt(onByte)
		);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   BinaryValuesByte rhs = (BinaryValuesByte) obj;
		   return new EqualsBuilder()
             .append(offByte, rhs.offByte)
             .append(onByte, rhs.onByte)
             .isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(offByte)
			.append(onByte)
			.toHashCode();
	}	
}
