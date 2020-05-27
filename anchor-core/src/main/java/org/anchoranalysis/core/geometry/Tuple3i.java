package org.anchoranalysis.core.geometry;

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


import java.io.Serializable;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public abstract class Tuple3i implements ReadableTuple3i, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int x = 0;
	protected int y = 0;
	protected int z = 0;
	
	public final void add( ReadableTuple3i pnt ) {
		this.x = this.x + pnt.getX();
		this.y = this.y + pnt.getY();
		this.z = this.z + pnt.getZ();
	}
	
	public final void sub( int val ) {
		this.x = this.x - val;
		this.y = this.y - val;
		this.z = this.z - val;
	}
	
	public final void sub( ReadableTuple3i pnt ) {
		this.x = this.x - pnt.getX();
		this.y = this.y - pnt.getY();
		this.z = this.z - pnt.getZ();
	}
	
	public final void scale( int factor ) {
		this.x = this.x * factor;
		this.y = this.y * factor;
		this.z = this.z * factor;
	}
	
	public final void div( int factor ) {
		this.x = this.x / factor;
		this.y = this.y / factor;
		this.z = this.z / factor;
	}
	
	public final void scaleXY( double factor ) {
		this.x = (int) (factor * this.x);
		this.y = (int) (factor * this.y);
	}

	@Override
	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	@Override
	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	@Override
	public final int getZ() {
		return z;
	}

	public final void setZ(int z) {
		this.z = z;
	}
		
	public final int getValueByDimension(int dimIndex) {
		if (dimIndex==0) {
			return x;
		} else if (dimIndex==1) {
			return y;
		} else if (dimIndex==2) {
			return z;
		} else {
			throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
		}
	}
	
	@Override
	public final int getValueByDimension( AxisType axisType ) {
		switch( axisType ) {
		case X:
			return x;
		case Y:
			return y;
		case Z:
			return z;
		default:
			throw new AnchorFriendlyRuntimeException(AxisTypeConverter.UNKNOWN_AXIS_TYPE);
		}
	}
	
	public final void incrX() {
		this.x++;
	}
	
	public final void incrY() {
		this.y++;
	}
	
	public final void incrZ() {
		this.z++;
	}
	
	public final void incrX(int val) {
		this.x += val;
	}
	
	public final void incrY(int val) {
		this.y += val;
	}
	
	public final void incrZ(int val) {
		this.z += val;
	}
	
	public final void decrX() {
		this.x--;
	}
	
	public final void decrY() {
		this.y--;
	}
	
	public final void decrZ() {
		this.z--;
	}
	
	public final void decrX(int val) {
		this.x -= val;
	}
	
	public final void decrY(int val) {
		this.y -= val;
	}
	
	public final void decrZ(int val) {
		this.z -= val;
	}
	
	@Override
	public String toString() {
		return String.format("[%d,%d,%d]",x,y,z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple3i other = (Tuple3i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
