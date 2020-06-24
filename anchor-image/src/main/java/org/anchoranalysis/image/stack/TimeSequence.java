package org.anchoranalysis.image.stack;

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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class TimeSequence implements Iterable<Stack> {

	private final List<Stack> list = new ArrayList<>();

	public TimeSequence() {
	}
	
	public TimeSequence(Stack s) {
		add(s);
	}

	public boolean add(Stack arg0) {
		return list.add(arg0);
	}

	public Stack get(int arg0) {
		return list.get(arg0);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<Stack> iterator() {
		return list.iterator();
	}

	public int size() {
		return list.size();
	}
	
	// Returns true if the data type of all channels is equal to
	public boolean allChnlsHaveType( VoxelDataType chnlDataType ) {
		
		for (Stack stack : this) {
			if (!stack.allChnlsHaveType(chnlDataType)) {
				return false;
			}
		}
		return true;
	}
	
	// Assumes all dimensions are the same, but doesn't check
	public ImageDim getDimensions() {
		return list.get(0).getDimensions();
	}
}
