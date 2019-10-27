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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.apache.commons.lang.builder.HashCodeBuilder;

// A z-stack of images that all have the same XY dimension, and has a variable number of channels
public class Stack implements Iterable<Chnl> {

	private StackNotUniformSized delegate;
	
	public Stack( ImageDim sd, ChnlFactorySingleType factory, int numChnls ) {
		delegate = new StackNotUniformSized();
		for( int i=0; i<numChnls; i++) {
			delegate.addChnl( factory.createEmptyInitialised(sd) );
		}
	}
	
	public Stack( Chnl chnl ) {
		delegate = new StackNotUniformSized( chnl );
	}
	
	public Stack( Chnl chnl0, Chnl chnl1,  Chnl chnl2 ) throws IncorrectImageSizeException {
		super();
		delegate = new StackNotUniformSized();
		addChnl(chnl0);
		addChnl( chnl1 );
		addChnl( chnl2 );
	}
	
	public Stack( StackNotUniformSized stack ) throws IncorrectImageSizeException, CreateException {
		
		if (stack.getNumChnl()==0) {
			throw new CreateException("At least one channel is required");
		}
		delegate = new StackNotUniformSized( stack.getChnl(0) );
		
		for( int i=1; i<stack.getNumChnl(); i++) {
			addChnl( stack.getChnl(i) );
		}
	}
	
	private Stack(Stack src) {
		delegate = src.delegate.duplicate();
	}
	
	public Stack() {
		delegate = new StackNotUniformSized();
	}

	
	public Stack extractSlice(int z) {
		// We know the sizes will be correct
		Stack out = new Stack();
		out.delegate = delegate.extractSlice(z) ;
		return out;
	}

	public Stack maxIntensityProj() {
		// We know the sizes will be correct
		Stack out = new Stack();
		out.delegate = delegate.maxIntensityProj();
		return out;
	}

	public void addBlankChnl()
			throws OperationFailedException {
			
		if (getNumChnl()==0) {
			throw new OperationFailedException("At least one channel must exist from which to guess dimensions");
		}
		
		if (!delegate.isUniformSized()) {
			throw new OperationFailedException("Other channels do not have the same dimensions. Cannot make a good guess of dimensions.");
		}
		
		if (!delegate.isUniformTyped()) {
			throw new OperationFailedException("Other channels do not have the same type.");
		}
		
		Chnl first = getChnl(0);
		delegate.addChnl(
			ChnlFactory.instance().createEmptyInitialised(
				first.getDimensions(),
				first.getVoxelDataType()
			)
		);
		
	}

	public final void addChnl(Chnl chnl) throws IncorrectImageSizeException {
		
		// We ensure that this channel has the same size as the first
		if (delegate.getNumChnl()>=1 && !chnl.getDimensions().equals(delegate.getChnl(0).getDimensions())) {
			throw new IncorrectImageSizeException("Dimensions of channel do not match existing channel");
		}
		
		delegate.addChnl(chnl);
	}

	public final Chnl getChnl(int index) {
		return delegate.getChnl(index);
	}

	public final int getNumChnl() {
		return delegate.getNumChnl();
	}

	public ImageDim getDimensions() {
		if (getNumChnl()==0) {
			return null;
		}
		return delegate.getChnl(0).getDimensions();
	}
	
	public Stack duplicate() {
		Stack out = new Stack(this);
		return out;
	}
	
	public Stack extractUpToThreeChnls() {
		Stack out = new Stack();
		int maxNum = Math.min(3, delegate.getNumChnl());
		for (int i=0; i<maxNum; i++) {
			try {
				out.addChnl( delegate.getChnl(i) );
			} catch (IncorrectImageSizeException e) {
				assert false;
			}
		}
		return out;
	}

	@Override
	public Iterator<Chnl> iterator() {
		return delegate.iterator();
	}
	
	public List<Chnl> asListChnls() {
		ArrayList<Chnl> list = new ArrayList<>();
		for(int i=0; i<delegate.getNumChnl(); i++) {
			list.add( delegate.getChnl(i) );
		}
		return list;
	}
	
	// Returns true if the data type of all channels is equal to
	public boolean allChnlsHaveType( VoxelDataType chnlDataType ) {
		
		for (Chnl chnl : this) {
			if (!chnl.getVoxelDataType().equals(chnlDataType)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) { return false; }
		if (obj == this) { return true; }
		
		if (!(obj instanceof Stack)) {
			return false;
		}
		
		Stack objCast = (Stack) obj;
					
		if (getNumChnl()!=objCast.getNumChnl()) {
			return false;
		}
		
		for( int i=0; i<getNumChnl(); i++) {
			if (!getChnl(i).equalsDeep(objCast.getChnl(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder()
				.append(getNumChnl());
		
		for( int i=0; i<getNumChnl(); i++) {
			builder.append( getChnl(i) );
		}
		
		return builder.toHashCode();
	}
	
}
