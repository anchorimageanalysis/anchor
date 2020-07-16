package org.anchoranalysis.image.voxel.box;

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


import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.InterpolateUtilities;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * A box (3-dimensions) of voxels
 * 
 * <p>This class is IMMUTABLE</p>.
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type
 */
public abstract class VoxelBox<T extends Buffer> {

	private final PixelsForPlane<T> planeAccess;
	private final VoxelBoxFactoryTypeBound<T> factory;
	
	public VoxelBox(PixelsForPlane<T> pixelsForPlane, VoxelBoxFactoryTypeBound<T> factory) {
		super();
		this.planeAccess = pixelsForPlane;
		this.factory = factory;
	}

	public PixelsForPlane<T> getPlaneAccess() {
		return planeAccess;
	}
	
	public VoxelDataType dataType() {
		return factory.dataType();
	}
	
	/**
	 * A (sub-)region of the voxels.
	 * 
	 * <p>The region may some smaller portion of the voxel-box, or the voxel-box as a whole.</p>
	 * 
	 * <p>It should <b>never</b> be larger than the voxel-box.</p>
	 * 
	 * <p>Depending on policy, an the existing box will be reused if possible
	 * (if the region requested is equal to the box as a whole), useful to avoid unnecessary new memory allocation.</p>
	 * 
	 * <p>If {@link reuseIfPossible} is FALSE, it is guaranteed that a new voxel-box will always be created.</p>
	 * 
	 * @param bbox a bounding-box indicating the regions desired (not be larger than the extent)
	 * @param reuseIfPossible if TRUE the existing box will be reused if possible,, otherwise a new box is always created.
	 * @return a voxel-box corresponding to the requested region, either newly-created or reused
	 */
	public VoxelBox<T> region(BoundingBox bbox, boolean reuseIfPossible) {
		if (reuseIfPossible) {
			return regionAvoidNewIfPossible(bbox);
		} else {
			return regionAlwaysNew(bbox);
		}
	}
		
	public void replaceBy(VoxelBox<T> vb) throws IncorrectImageSizeException {
		
		if (!extent().equals(vb.extent())) {
			throw new IncorrectImageSizeException("Sizes do not match");
		}
		
		BoundingBox bbo = new BoundingBox(vb.extent());
		vb.copyPixelsTo(bbo, this, bbo);
	}

	/**
	 * Copies pixels from this object to another object
	 * 
	 * @param sourceBox relative to the current object
	 * @param destVoxelBox 
	 * @param destBox relative to destVoxelBox
	 */
	public void copyPixelsTo(BoundingBox sourceBox, VoxelBox<T> destVoxelBox,
			BoundingBox destBox) {
		
		checkExtentMatch(sourceBox, destBox);
		
		ReadableTuple3i srcStart = sourceBox.cornerMin();
		ReadableTuple3i srcEnd = sourceBox.calcCornerMax();
		
		Point3i relPos = destBox.relPosTo(sourceBox);
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			assert( z< extent().getZ() );
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			T destArr = destVoxelBox.getPlaneAccess().getPixelsForPlane(z + relPos.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {
				
					int srcIndex = getPlaneAccess().extent().offset(x, y);
					int destIndex = destVoxelBox.extent().offset(x + relPos.getX(), y+relPos.getY());
					
					copyItem( srcArr, srcIndex, destArr, destIndex );
				}
			}
		}
	}
	
	
	// Only copies pixels if part of an object, otherwise we set a null pixel
	public void copyPixelsToCheckMask(BoundingBox sourceBox, VoxelBox<T> destVoxelBox,
			BoundingBox destBox, VoxelBox<ByteBuffer> objectMaskBuffer, BinaryValuesByte maskBV ) {
		
		checkExtentMatch(sourceBox, destBox);
		
		ReadableTuple3i srcStart = sourceBox.cornerMin();
		ReadableTuple3i srcEnd = sourceBox.calcCornerMax();
		
		Point3i relPos = destBox.relPosTo(sourceBox);
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			T destArr = destVoxelBox.getPlaneAccess().getPixelsForPlane(z + relPos.getZ()).buffer();

			ByteBuffer maskBuffer = objectMaskBuffer.getPixelsForPlane(z-srcStart.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {
				
					int srcIndex = getPlaneAccess().extent().offset(x, y);
					int destIndex = destVoxelBox.extent().offset(x + relPos.getX(), y+relPos.getY());
					
					if(maskBuffer.get()==maskBV.getOnByte()) {
						copyItem( srcArr, srcIndex, destArr, destIndex );
					}
				}
			}
		}
	}
	
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask
	 * 
	 * See {@link #setPixelsCheckMask} for details
	 * 
	 * @param object the object-mask to restrict which values in the buffer are written to
	 * @param value value to be set in matched pixels
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask( ObjectMask object, int value ) {
		return setPixelsCheckMask(
			object.getBoundingBox(),
			object.getVoxelBox(),
			new BoundingBox(object.getBoundingBox().extent()),
			value,
			object.getBinaryValuesByte().getOnByte()
		);
	}
	
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask
	 * 
	 * See {@ #setPixelsCheckMask(BoundingBox, VoxelBox, BoundingBox, int, byte) for details
	 * 
	 * @param object the object-mask to restrict which values in the buffer are written to
	 * @param value value to be set in matched pixels
	 * @param maskMatchValue what's an "On" value for the mask to match against?
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask( ObjectMask object, int value, byte maskMatchValue ) {
		return setPixelsCheckMask(
			object.getBoundingBox(),
			object.getVoxelBox(),
			new BoundingBox(object.getBoundingBox().extent()),
			value,
			maskMatchValue
		);
	}
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask... with more customization
	 *
	 * <p>Pixels are unchanged if they do not match the mask</p>
	 * <p>Bounding boxes can be used to restrict regions in both the source and destination, but must be equal in volume.</p>
	 * 
	 * @param bboxToBeAssigned which part of the buffer to write to
	 * @param objectMaskBuffer the byte-buffer for the mask
	 * @param bboxMask which part of the mask to consider as input
	 * @param value value to be set in matched pixels
	 * @param maskMatchValue what's an "On" value for the mask to match against?
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask(
		BoundingBox bboxToBeAssigned,
		VoxelBox<ByteBuffer> objectMaskBuffer,
		BoundingBox bboxMask,
		int value,
		byte maskMatchValue
	) {
		checkExtentMatch(bboxMask, bboxToBeAssigned);
		
		Extent eIntersectingBox = bboxMask.extent();
		
		Extent eAssignBuffer = this.extent();
		Extent eMaskBuffer = objectMaskBuffer.extent();
		
		int cnt = 0;
		
		for (int z=0; z<eIntersectingBox.getZ(); z++) {
			
			VoxelBuffer<?> pixels = getPlaneAccess().getPixelsForPlane(z + bboxToBeAssigned.cornerMin().getZ());
			ByteBuffer pixelsMask = objectMaskBuffer.getPixelsForPlane(z + bboxMask.cornerMin().getZ()).buffer();
			
			for (int y=0; y<eIntersectingBox.getY(); y++) {
				for (int x=0; x<eIntersectingBox.getX(); x++) {

					int indexMask = eMaskBuffer.offset(x + bboxMask.cornerMin().getX(), y + bboxMask.cornerMin().getY());
					
					if (pixelsMask.get(indexMask)==maskMatchValue) {
						int indexAssgn = eAssignBuffer.offset(
							x + bboxToBeAssigned.cornerMin().getX(),
							y + bboxToBeAssigned.cornerMin().getY()
						);
						pixels.putInt(indexAssgn, value );
						cnt++;
					}
				}
			}
			
		}
		
		return cnt;
	}
	
	public abstract void addPixelsCheckMask( ObjectMask mask, int value );
	
	public abstract void scalePixelsCheckMask( ObjectMask mask, double value );
	
	public abstract void subtractFrom( int val );
	
	
	public ObjectMask equalMask( BoundingBox bbox, int equalVal ) {
		
		ObjectMask object = new ObjectMask(bbox);
		
		ReadableTuple3i pointMax = bbox.calcCornerMax();
		
		byte maskOut = object.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.cornerMin().getZ(); z<=pointMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = object.getVoxelBox().getPixelsForPlane(z - bbox.cornerMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.cornerMin().getY(); y<=pointMax.getY(); y++) {
				for (int x=bbox.cornerMin().getX(); x<=pointMax.getX(); x++) {
					
					int index = getPlaneAccess().extent().offset(x, y);
					
					pixelIn.position(index);
					
					if (isEqualTo(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOut );
					}
					
					ind++;
				}
			}
		}
		
		return object;
	}
	
	
	public ObjectMask greaterThanMask( BoundingBox bbox, int equalVal ) {
		
		ObjectMask object = new ObjectMask(bbox);
		
		ReadableTuple3i pointMax = bbox.calcCornerMax();
		
		byte maskOut = object.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.cornerMin().getZ(); z<=pointMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = object.getVoxelBox().getPixelsForPlane(z - bbox.cornerMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.cornerMin().getY(); y<=pointMax.getY(); y++) {
				for (int x=bbox.cornerMin().getX(); x<=pointMax.getX(); x++) {
					
					int index = getPlaneAccess().extent().offset(x, y);
					
					pixelIn.position(index);
					
					if (isGreaterThan(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOut );
					}
					
					ind++;
				}
			}
		}
		
		return object;
	}
	
	
	public ObjectMask greaterThanMask( ObjectMask maskIn, int equalVal ) {
		
		ObjectMask maskOut = new ObjectMask(
			maskIn.getBoundingBox()
		);
		
		BoundingBox bbox = maskIn.getBoundingBox();
		ReadableTuple3i pointMax = bbox.calcCornerMax();
		
		byte maskInVal = maskIn.getBinaryValuesByte().getOnByte();
		byte maskOutVal = maskOut.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.cornerMin().getZ(); z<=pointMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelMaskIn = maskIn.getVoxelBox().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = maskOut.getVoxelBox().getPixelsForPlane(z - bbox.cornerMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.cornerMin().getY(); y<=pointMax.getY(); y++) {
				for (int x=bbox.cornerMin().getX(); x<=pointMax.getX(); x++) {
					
					int index = getPlaneAccess().extent().offset(x, y);
					
					pixelIn.position(index);
					
					byte maskVal = pixelMaskIn.get(ind);
					
					if (maskVal==maskInVal && isGreaterThan(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOutVal);
					}
										
					ind++;
				}
			}
		}
		
		return maskOut;
	}
	

	public int countGreaterThan( int operand ) {
		
		int cnt = 0;
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isGreaterThan(buffer,operand) ) {
					cnt++;
				}
			}
			
		}
		return cnt;
	}
	
	public boolean hasGreaterThan( int operand ) {
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isGreaterThan(buffer,operand) ) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	
	public boolean hasEqualTo( int operand ) {
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isEqualTo(buffer,operand) ) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	
	
	public int countEqual( int equalVal ) {
		
		int count = 0;
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while (pixels.hasRemaining()) {
				if (isEqualTo(pixels, equalVal )) {
					count++;
				}
			}
		}
		return count;
	}
	
	
	public int countEqualMask( int equalVal, ObjectMask object ) {
		
		ReadableTuple3i srcStart = object.getBoundingBox().cornerMin();
		ReadableTuple3i srcEnd = object.getBoundingBox().calcCornerMax();

		int count = 0;
		
		byte maskOnVal = object.getBinaryValuesByte().getOnByte();
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer maskBuffer = object.getVoxelBox().getPixelsForPlane(z-srcStart.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {

					int maskIndex = object.getVoxelBox().extent().offset(x-srcStart.getX(), y-srcStart.getY());
					
					if (maskBuffer.get(maskIndex)==maskOnVal) {
					
						int srcIndex = getPlaneAccess().extent().offset(x, y);
						srcArr.position(srcIndex);
						
						if (isEqualTo(srcArr, equalVal )) {
							count++;
						}
					}
				}
			}
		}
		return count;
	}
	
	public abstract int ceilOfMaxPixel();
	
	public abstract void copyItem( T srcBuffer, int srcIndex, T destBuffer, int destIndex );
	
	public abstract boolean isGreaterThan( T buffer, int operand );
	
	public abstract boolean isEqualTo( T buffer, int operand );
	
	public abstract boolean isEqualTo( T buffer1, T buffer2 );
	
	public abstract void setAllPixelsTo( int val );
	
	public abstract void setPixelsTo( BoundingBox bbox, int val );
	
	public abstract void multiplyBy( double val );

	public abstract VoxelBox<T> maxIntensityProj();
	
	public abstract VoxelBox<T> meanIntensityProj();
	
	public void transferPixelsForPlane(int z, VoxelBox<T> src, int zSrc, boolean duplicate ) {
		if (duplicate) {
			setPixelsForPlane(z, src.getPixelsForPlane(zSrc));
		} else {
			setPixelsForPlane(z, src.getPixelsForPlane(zSrc).duplicate() );
		}
	}
	
	public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
		planeAccess.setPixelsForPlane(z, pixels);
	}

	public VoxelBuffer<T> getPixelsForPlane(int z) {
		return planeAccess.getPixelsForPlane(z);
	}

	public Extent extent() {
		return planeAccess.extent();
	}

	public int minSliceNonZero() {
		
		for (int z=0; z<extent().getZ(); z++) {
			if(isSliceNonZero(z)) {
				return z;
			}
		}
		
		return -1;
	}
	
	public boolean isSliceNonZero( int z ) {
		T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
		
		while (pixels.hasRemaining()) {
			if (!isEqualTo(pixels, 0 )) {
				return true;
			}
		}
		return false;
	}
	
	public abstract void setVoxel(int x, int y, int z, int val);

	// Very slow access, use sparingly.  Instead process slice by slice.
	public abstract int getVoxel(int x, int y, int z);
	
	public int getVoxel(ReadableTuple3i point) {
		return getVoxel(point.getX(), point.getY(), point.getZ());
	}
	
	// Creates a new channel contain a duplication only of a particular slice
	public VoxelBox<T> extractSlice( int z ) {
		
		VoxelBox<T> bufferAccess = factory.create(
			extent().duplicateChangeZ(1)
		);
		bufferAccess.getPlaneAccess().setPixelsForPlane(0, getPlaneAccess().getPixelsForPlane(z) );
		return bufferAccess;
	}
	
	/**
	 * Creates a new voxel-box that is a resized version of the current voxel-box, interpolating as needed.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param sizeX new size in X dimension
	 * @param sizeY new size in Y dimension
	 * @param interpolator means  to interpolate pixels as they are resized.
	 * @return a newly created voxel-box of specified size containing interpolated pixels from the current voxel-box.
	 */
	public VoxelBox<T> resizeXY( int sizeX, int sizeY, Interpolator interpolator ) {
		
		Extent extentResized = new Extent(
			sizeX,
			sizeY,
			extent().getZ()
		);
		
		VoxelBox<T> bufferTarget = factory.create(extentResized);
		
		assert(bufferTarget.getPixelsForPlane(0).buffer().capacity()==extentResized.getVolumeXY());
		
		InterpolateUtilities.transferSlicesResizeXY(
			new VoxelBoxWrapper(this),
			new VoxelBoxWrapper(bufferTarget),
			interpolator
		);
			
		assert(bufferTarget.getPixelsForPlane(0).buffer().capacity()==extentResized.getVolumeXY());
		return bufferTarget;
	}
	
	public VoxelBox<T> duplicate() {
		
		assert( getPlaneAccess().extent().getZ() > 0 );
		
		VoxelBox<T> bufferAccess = factory.create( getPlaneAccess().extent() );
		
		for( int z=0; z<extent().getZ(); z++ ) {
			VoxelBuffer<T> buffer = getPixelsForPlane(z);
			bufferAccess.setPixelsForPlane( z, buffer.duplicate() );
		}
		
		return bufferAccess;
	}
	
	/**
	 * Is the buffer identical to another beautiful (deep equals)
	 * 
	 * @param other
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean equalsDeep( VoxelBox<?> other) {
		
		if (!factory.dataType().equals(other.getFactory().dataType())) {
			return false;
		}
		
		if (!extent().equals(other.extent())) {
			return false;
		}
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			
			VoxelBuffer<T> buffer1 = getPlaneAccess().getPixelsForPlane(z);
			VoxelBuffer<T> buffer2 = (VoxelBuffer<T>) other.getPlaneAccess().getPixelsForPlane(z);
			
			while( buffer1.buffer().hasRemaining() ) {
				
				if ( !isEqualTo(buffer1.buffer(), buffer2.buffer()) ) {
					return false;
				}
			}
			
			assert( !buffer2.buffer().hasRemaining() );
			
		}
		
		return true;
	}
	
	public abstract void max( VoxelBox<T> other ) throws OperationFailedException;

	public VoxelBoxFactoryTypeBound<T> getFactory() {
		return factory;
	}
		
	private VoxelBox<T> regionAvoidNewIfPossible(BoundingBox bbox) {
		
		if (   bbox.equals(new BoundingBox(extent()))
			&& bbox.cornerMin().getX()==0
			&& bbox.cornerMin().getY()==0
			&& bbox.cornerMin().getZ()==0
		) {
			return this;
		}
		return regionAlwaysNew(bbox);
	}
	
	private VoxelBox<T> regionAlwaysNew(BoundingBox bbox) {
		
		// Otherwise we create a new buffer
		VoxelBox<T> vbOut = factory.create(bbox.extent());
		copyPixelsTo(bbox, vbOut, new BoundingBox(bbox.extent()));
		return vbOut;
	}
	
	private static void checkExtentMatch(BoundingBox bbox1, BoundingBox bbox2) {
		Extent extent1 = bbox1.extent();
		Extent extent2 = bbox2.extent();
		if (!extent1.equals(extent2)) {
			throw new IllegalArgumentException(
				String.format(
					"The extents of the two bounding-boxes are not identical: %s vs %s",
					extent1,
					extent2
				)
			);
		}
	}
	
	
}
