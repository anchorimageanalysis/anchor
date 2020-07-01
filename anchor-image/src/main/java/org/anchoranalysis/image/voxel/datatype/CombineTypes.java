package org.anchoranalysis.image.voxel.datatype;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.error.CreateException;

public class CombineTypes {

	private CombineTypes() {}
	
	public static VoxelDataType combineTypes( VoxelDataType type1, VoxelDataType type2 ) throws CreateException {
		if (type1.equals(type2)) {
			return type1;
		} else if (type1.equals(VoxelDataTypeUnsignedByte.instance) && type2.equals(VoxelDataTypeUnsignedShort.instance) ) {
			return VoxelDataTypeUnsignedShort.instance;
		}  else if (type2.equals(VoxelDataTypeUnsignedByte.instance) && type1.equals(VoxelDataTypeUnsignedShort.instance) ) {
			return VoxelDataTypeUnsignedShort.instance;
		} else {
			throw new CreateException("Only combinations of byte and short are supported");
		}
	}
	
}
