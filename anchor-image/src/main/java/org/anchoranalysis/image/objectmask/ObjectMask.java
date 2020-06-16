package org.anchoranalysis.image.objectmask;

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


import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objectmask.factory.CreateFromConnectedComponentsFactory;
import org.anchoranalysis.image.objectmask.intersecting.CountIntersectingPixelsBinary;
import org.anchoranalysis.image.objectmask.intersecting.DetermineWhetherIntersectingPixelsBinary;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;


/** 
 * An object expressed in voxels, bounded within overall space
 * 
 * A bounding-box defines a box within the overall space, and a raster-mask defines which voxels inside
 *  the bounding-box belong to the object.
 * 
 * Each voxel in the raster-mask must be one of two states, an ON value and an OFF value
 * 
 * These voxels are MUTABLE.
 */
public class ObjectMask {
	
	private BoundedVoxelBox<ByteBuffer> delegate;
	private BinaryValues bv = new BinaryValues( 0, 255 );
	private BinaryValuesByte bvb = new BinaryValuesByte( (byte) 0, (byte) -1 );
	
	// Initialises a voxel box to match a BoundingBox size, with all values set to 0  
	public ObjectMask(BoundingBox bbox) {
		super();
		delegate = new BoundedVoxelBox<>(bbox, VoxelBoxFactory.getByte() );
	}
	
	public ObjectMask(BoundedVoxelBox<ByteBuffer> voxelBox) {
		super();
		delegate = voxelBox;
	}
	
	public ObjectMask(BoundedVoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues) {
		super();
		delegate = voxelBox;
		this.bv = binaryValues.duplicate();
		this.bvb = binaryValues.createByte();
	}
	
	public ObjectMask(VoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(voxelBox);
	}
	
	public ObjectMask(BinaryVoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(voxelBox.getVoxelBox());
		this.bv = new BinaryValues( voxelBox.getBinaryValues() );
		this.bvb = new BinaryValuesByte( voxelBox.getBinaryValues() );
	}
	
	public ObjectMask(BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(bbox, voxelBox);
	}
	
	public ObjectMask(BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox, BinaryValuesByte binaryValues ) {
		delegate = new BoundedVoxelBox<>(bbox, voxelBox);
		this.bv = binaryValues.createInt();
		this.bvb = new BinaryValuesByte( binaryValues );
	}
	
	public ObjectMask(BoundingBox bbox, VoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues ) {
		delegate = new BoundedVoxelBox<>(bbox, voxelBox);
		this.bv = binaryValues.duplicate();
		this.bvb = binaryValues.createByte();
	}
	
	public ObjectMask(BoundingBox bbox, BinaryVoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(bbox,voxelBox.getVoxelBox());
		this.bv = new BinaryValues( voxelBox.getBinaryValues() );
		this.bvb = new BinaryValuesByte( voxelBox.getBinaryValues() );
	}
	
	// Copy constructor
	public ObjectMask(ObjectMask src) {
		super();
		delegate = new BoundedVoxelBox<>( src.delegate );
		bv = new BinaryValues( src.bv );
		bvb = new BinaryValuesByte( src.bvb );
	}

	public ObjectMask duplicate() {
		return new ObjectMask(this);
	}

	public int numPixels() {
		return delegate.getVoxelBox().countEqual( bv.getOnInt() );
	}

	public void setVoxelBox(VoxelBox<ByteBuffer> voxelBox) {
		delegate.setVoxelBox(voxelBox);
	}

	public ObjectMask flattenZ() {
		return new ObjectMask( delegate.flattenZ(), bv.duplicate() );
	}

	public ObjectMask growToZ(int sz) {
		return new ObjectMask(
			delegate.growToZ(
				sz,
				VoxelBoxFactory.getByte()
			)
		);
	}

	public ObjectMask growBuffer(Point3i neg, Point3i pos,	Optional<Extent> clipRegion) throws OperationFailedException {
		return new ObjectMask(
			delegate.growBuffer(
				neg,
				pos,
				clipRegion,
				VoxelBoxFactory.getByte()
			)
		);
	}
		
	public boolean equalsDeep( ObjectMask other) {
		if(!delegate.equalsDeep(other.delegate)) {
			return false;
		}
		if(!bv.equals(other.bv)) {
			return false;
		}
		if(!bvb.equals(other.bvb)) {
			return false;
		}
		// DOES NOT CHECK factory
		return true;
	}

	public int countIntersectingPixels(ObjectMask other) {
		return new CountIntersectingPixelsBinary(
			getBinaryValuesByte(),
			other.getBinaryValuesByte()
		).countIntersectingPixels(
			delegate,
			other.delegate
		);
	}
	
	public boolean hasIntersectingPixels(ObjectMask other) {
		return new DetermineWhetherIntersectingPixelsBinary(
			getBinaryValuesByte(),
			other.getBinaryValuesByte()		
		).hasIntersectingPixels(delegate, other.delegate);
	}

	// Scales an objMask making sure to create a duplicate first
	public ObjectMask scaleNew(ScaleFactor factor, Interpolator interpolator) throws OperationFailedException {
		
		if ((bv.getOnInt()==255 && bv.getOffInt()==0) || (bv.getOnInt()==0 && bv.getOffInt()==255)) {
			
			BoundedVoxelBox<ByteBuffer> boxNew = delegate.scale(factor, interpolator);
			
			// We should do a thresholding afterwards to make sure our values correspond to the two binary values
			if (interpolator!=null && interpolator.isNewValuesPossible()) {
				
				// We threshold to make sure it's still binary
				int thresholdVal = (bv.getOnInt() + bv.getOffInt()) /2;
				
				try {
					VoxelBoxThresholder.thresholdForLevel(boxNew.getVoxelBox(), thresholdVal, bv.createByte());
				} catch (CreateException e) {
					throw new OperationFailedException("Cannot convert binary values into bytes");
				}
			}
			
			return new ObjectMask( boxNew, bv.duplicate() );
			
		} else {
			throw new OperationFailedException("Operation not supported for these binary values");
		}
	}
	
	// Scales an objMask replacing the existing mask
	public void scale(ScaleFactor sf, Interpolator interpolator ) throws OperationFailedException {
		
		if ((bv.getOnInt()==255 && bv.getOffInt()==0) || (bv.getOnInt()==0 && bv.getOffInt()==255)) {
			
			delegate = delegate.scale(sf, interpolator );
			
			// We should do a thresholding afterwards to make sure our values correspond to the two binry values
			if (interpolator.isNewValuesPossible()) {
				
				// We threshold to make sure it's still binary
				int thresholdVal = (bv.getOnInt() + bv.getOffInt()) /2;
				
				try {
					VoxelBoxThresholder.thresholdForLevel(delegate.getVoxelBox(), thresholdVal, bv.createByte());
				} catch (CreateException e) {
					throw new OperationFailedException("Cannot convert binary values into bytes");
				}

				
			}
			
		} else {
			throw new OperationFailedException("Operation not supported for these binary values");
		}

	}
	
	/** Calculates center-of-gravity across all axes */
	public Point3d centerOfGravity() {
		return CenterOfGravityCalculator.calcCenterOfGravity(this);
	}

	/**
	 * Calculates center-of-gravity for one specific axis only
	 * 
	 * @param axis the axis
	 * @return a point on the specific axis that is the center-of-gravity.
	 */
	public double centerOfGravity(AxisType axis) {
		return CenterOfGravityCalculator.calcCenterOfGravityForAxis(this, axis);
	}

	public boolean sizesMatch() {
		return delegate.sizesMatch();
	}

	/**
	 * Tests if an object-mask is connected
	 * 
	 * TODO: this is not particular efficient. We can avoid making the ObjMaskCollection
	 * 
	 * @return
	 * @throws OperationFailedException 
	 */
	public boolean checkIfConnected() throws OperationFailedException {
		
		CreateFromConnectedComponentsFactory creator = new CreateFromConnectedComponentsFactory(true);
		ObjectCollection objs;
		try {
			objs = creator.createConnectedComponents(this.binaryVoxelBox().duplicate());
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
		return objs.size()<=1;
	}
	
	public boolean numPixelsLessThan( int num ) {
		
		Extent e = delegate.getVoxelBox().extent();
		
		int cnt = 0;
		
		for (int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();
			
			while( bb.hasRemaining() ) {
				byte b = bb.get();
				
				if (b==bvb.getOnByte()) {
					cnt++;
					
					if (cnt>=num) {
						return false;
					}
				}
			}
			
		}
		return true;
	}
	
	public boolean hasPixelsGreaterThan( int num ) {
		
		Extent e = delegate.getVoxelBox().extent();
		
		int cnt = 0;
		
		for (int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();
			
			while( bb.hasRemaining() ) {
				byte b = bb.get();
				
				if (b==bvb.getOnByte()) {
					cnt++;
					
					if (cnt>num) {
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
	/**
	 * Intersects this object-mask with another
	 * 
	 * @param other the other object-mask to intersect with
	 * @param dim dimensions to constrain any intersection
	 * @return a new mask of the intersection iff it exists
	 */
	public Optional<ObjectMask> intersect( ObjectMask other, ImageDim dim ) {
		
		// we combine the two masks
		Optional<BoundingBox> bboxIntersect = getBoundingBox().intersection().withInside( other.getBoundingBox(), dim.getExtnt() );
		
		if (!bboxIntersect.isPresent()) {
			return Optional.empty();
		}
		
		// We calculate a bounding box, which we write into in the omDest
		Point3i pntIntersectRelToSrc = bboxIntersect.get().relPosTo( getBoundingBox() );
		Point3i pntIntersectRelToOthr = bboxIntersect.get().relPosTo( other.getBoundingBox() );
				
		BinaryValues bvOut = BinaryValues.getDefault();

		// We initially set all pixels to ON
		VoxelBox<ByteBuffer> vbMaskOut = VoxelBoxFactory.getByte().create(
			bboxIntersect.get().extent()
		);
		vbMaskOut.setAllPixelsTo( bvOut.getOnInt() );
		
		// Then we set any pixels NOT on either mask to OFF..... leaving only the intersecting pixels as ON in the output buffer
		setPixelsTwoMasks(
			vbMaskOut,
			getVoxelBox(),
			other.getVoxelBox(),
			new BoundingBox(pntIntersectRelToSrc, bboxIntersect.get().extent() ),
			new BoundingBox(pntIntersectRelToOthr, bboxIntersect.get().extent() ),
			bvOut.getOffInt(),
			this.getBinaryValuesByte().getOffByte(),
			other.getBinaryValuesByte().getOffByte()
		);
		
		ObjectMask om = new ObjectMask(
			bboxIntersect.get(),
			new BinaryVoxelBoxByte(vbMaskOut, bvOut)
		);
		
		
		// If there no pixels left that haven't been set, then the intersection mask is zero
		if (om.hasPixelsGreaterThan(0)) {
			return Optional.of(om);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Sets pixels in a voxel box that match either of two masks
	 * 
	 * @param vbMaskOut voxel-box to write to
	 * @param voxelBox1 voxel-box for first mask
	 * @param voxelBox2 voxel-box for second mask
	 * @param bboxSrcMask bounding-box for first mask
	 * @param bboxOthrMask bounding-box for second mask
	 * @param value value to write
	 * @param maskMatchValue mask-value to match against for first mask
	 * @param maskMatchValue mask-value to match against for second mask
	 * @return the total number of pixels written
	 */
	private static int setPixelsTwoMasks(
		VoxelBox<ByteBuffer> vbMaskOut,
		VoxelBox<ByteBuffer> voxelBox1,
		VoxelBox<ByteBuffer> voxelBox2,
		BoundingBox bboxSrcMask,
		BoundingBox bboxOthrMask,
		int value,
		byte maskMatchValue1,
		byte maskMatchValue2
	) {
		BoundingBox allOut = new BoundingBox( vbMaskOut.extent() );
		int cntSetFirst = vbMaskOut.setPixelsCheckMask(allOut, voxelBox1, bboxSrcMask, value, maskMatchValue1 );
		int cntSetSecond = vbMaskOut.setPixelsCheckMask(allOut, voxelBox2, bboxOthrMask, value, maskMatchValue2 );
		return cntSetFirst+cntSetSecond;
	}
	
	public boolean contains( Point3i pnt ) {
		
		if (!delegate.getBoundingBox().contains().point(pnt)) {
			return false;
		}
		
		int xRel = pnt.getX() - delegate.getBoundingBox().getCrnrMin().getX();
		int yRel = pnt.getY() - delegate.getBoundingBox().getCrnrMin().getY();
		int zRel = pnt.getZ() - delegate.getBoundingBox().getCrnrMin().getZ();
		
		return delegate.getVoxelBox().getVoxel(xRel, yRel, zRel)==bv.getOnInt();
	}
	
	
	public boolean containsIgnoreZ( Point3i pnt ) {
		
		if (!delegate.getBoundingBox().contains().pointIgnoreZ(pnt)) {
			return false;
		}
		
		int xRel = pnt.getX() - delegate.getBoundingBox().getCrnrMin().getX();
		int yRel = pnt.getY() - delegate.getBoundingBox().getCrnrMin().getY();
		
		Extent e = delegate.getBoundingBox().extent();
		for( int z=0; z<e.getZ(); z++) {
			if (delegate.getVoxelBox().getVoxel(xRel, yRel, z)==bv.getOnInt()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A maximum-intensity projection (flattens in z dimension)
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @return a new object-mask flattened in Z dimension.
	 */
	public ObjectMask maxIntensityProjection() {
		return new ObjectMask(
			delegate.maxIntensityProjection()
		);
	}

	public BoundingBox getBoundingBox() {
		return delegate.getBoundingBox();
	}

	public void setBoundingBox(BoundingBox bbox) {
		delegate.setBoundingBox(bbox);
	}

	public BinaryVoxelBox<ByteBuffer> binaryVoxelBox() {
		return new BinaryVoxelBoxByte( delegate.getVoxelBox(), bv );
	}
	
	public VoxelBox<ByteBuffer> getVoxelBox() {
		return delegate.getVoxelBox();
	}

	public BinaryValues getBinaryValues() {
		return bv;
	}
	
	public BinaryValuesByte getBinaryValuesByte() {
		return bvb;
	}

	public void setBinaryValues(BinaryValues binaryValues) throws CreateException {
		this.bv = binaryValues;
		this.bvb = binaryValues.createByte();
	}
	
	public void setBinaryValues(BinaryValuesByte binaryValues) {
		this.bv = binaryValues.createInt();
		this.bvb = binaryValues;
	}

	public BoundedVoxelBox<ByteBuffer> getVoxelBoxBounded() {
		return delegate;
	}
	
	// omContained MUST be contained within the overall vox
	public void invertContainedMask( ObjectMask omContained ) throws OperationFailedException {
		
		Point3i pntRel = omContained.getBoundingBox().relPosTo(getBoundingBox());
		BoundingBox bboxRel = new BoundingBox(
			pntRel,
			omContained.getBoundingBox().extent()
		);
		
		ObjectMask omContainedRel = new ObjectMask(
			bboxRel,
			omContained.getVoxelBox(),
			omContained.getBinaryValues()
		);
			
		getVoxelBox().setPixelsCheckMask(omContainedRel, getBinaryValuesByte().getOffByte());
	}

	// If keepZ is true the slice keeps its z coordinate, otherwise its set to 0
	public ObjectMask extractSlice(int z, boolean keepZ) throws OperationFailedException {
		return new ObjectMask(
			delegate.extractSlice(z, keepZ),
			this.bv.duplicate()
		);
	}

	public ObjectMask clipToContainer(BoundingBox bboxContainer) throws OperationFailedException {
		assert( bboxContainer.intersection().existsWith(this.getBoundingBox()));
		if (bboxContainer.contains().box(getBoundingBox())) {
			// Nothing to do
			return this;
		} else {
			
			try {
				BoundingBox bboxIntersection = getBoundingBox().intersection().with(bboxContainer).orElseThrow( ()->
					new OperationFailedException("Bounding boxes do not intersect")
				);
				
				// First we try to chop of the Zs, and see if it fits.
				// This is much less work than always processing all pixels, just for the sake of Z-slices
				ObjectMask omSubslices = createVirtualSubrange(bboxIntersection.getCrnrMin().getZ(), bboxIntersection.calcCrnrMax().getZ() );
				
				if (bboxContainer.contains().box(omSubslices.getBoundingBox())) {
					return omSubslices;
				}
				
				ObjectMask om = createSubmaskAvoidNew(bboxIntersection);
				assert( bboxContainer.contains().box(om.getBoundingBox()) );
				return om;
			} catch (CreateException e) {
				throw new OperationFailedException(e);
			}
		}
	}
	
	// Creates an ObjMask with a subrange of the slices. zMin inclusive, zMax inclusive
	public ObjectMask createVirtualSubrange(int zMin, int zMax) throws CreateException {
		return new ObjectMask(
			delegate.createVirtualSubrange(
				zMin,
				zMax,
				VoxelBoxFactory.getByte()
			),
			this.bv
		);
	}
	
	
	public ObjectMask createSubmaskAvoidNew(BoundingBox bbox)
			throws CreateException {
		return new ObjectMask(
			delegate.createBufferAvoidNew(bbox),
			this.bv.duplicate()
		);
	}

	public ObjectMask createSubmaskAlwaysNew(BoundingBox bbox) throws CreateException {
		return new ObjectMask(
			delegate.createBufferAlwaysNew(bbox),
			this.bv.duplicate()
		);
	}
	
	/**
	 * More relaxed-condition than a submask. It will create a mask
	 *   within a new bounding-box, so long as part of the bounding-box
	 *   intersects with the current mask
	 *   
	 * @param bbox
	 * @return
	 * @throws CreateException
	 */
	public ObjectMask createIntersectingMaskAlwaysNew(BoundingBox bbox)
			throws CreateException {
		return new ObjectMask(
			delegate.createIntersectingBufferAlwaysNew(bbox),
			this.bv.duplicate()
		);
	}
	
	public Optional<Point3i> findAnyPntOnMask() {
		
		// First we try the mid-point
		Point3i pnt = getBoundingBox().centerOfGravity();
		if (contains(pnt)) {
			return Optional.of(pnt);
		}
		return IterateVoxels.findFirstPointOnMask(this);
	}
	
	public void shiftBy(ReadableTuple3i shiftBy) {
		delegate.shiftBy(shiftBy);
	}
	

	public void shiftBackBy(ReadableTuple3i shiftBackwardsBy) {
		delegate.shiftBackBy(shiftBackwardsBy);
	}
	
	public void shiftTo(Point3i crnrMinNew) {
		delegate.shiftTo(crnrMinNew);
	}

	public void shiftToZ(int crnrZNew) {
		delegate.shiftToZ(crnrZNew);
	}
	
	public void reflectThroughOrigin() {
		delegate.reflectThroughOrigin();
	}
		
	// Creates a new objMask that is relative to another bbox
	public ObjectMask relMaskTo( BoundingBox bbox ) {
		Point3i pnt = delegate.getBoundingBox().relPosTo(bbox);
		
		return new ObjectMask(
			new BoundingBox(pnt, delegate.extent()),
			delegate.getVoxelBox()
		);
	}
	
	@Override
	public String toString() {
		return String.format("Obj%s(cog=%s,numPixels=%d)", super.hashCode(), centerOfGravity().toString(), numPixels() );
	}
}