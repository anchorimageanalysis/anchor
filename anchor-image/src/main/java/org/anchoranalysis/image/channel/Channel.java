package org.anchoranalysis.image.channel;

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
import java.util.function.UnaryOperator;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2Lanczos;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

import lombok.Getter;

/**
 * A channel from an image
 * 
 * <p>This is one of the key image-processing classes in Anchor. An image may have one channel (grayscale) or several.
 * Channels of identical size can be bundled together to form a {@link Stack}</p>
 * 
 * <p>The channel has an underlying 
 * 
 * @author Owen Feehan
 *
 */
public class Channel {

	private static final Interpolator DEFAULT_INTERPOLATOR = new InterpolatorImgLib2Lanczos();
	
	private static final ChannelFactory FACTORY = ChannelFactory.instance();
	
	@Getter
	private ImageDimensions dimensions;
	
	private VoxelBox<? extends Buffer> delegate;
		
	/**
	 * Constructor
	 * 
	 * @param voxelBox
	 * @param res
	 */
	public Channel( VoxelBox<? extends Buffer> voxelBox, ImageResolution res ) {
		this.dimensions = new ImageDimensions(
			voxelBox.extent(),
			res
		);
		delegate = voxelBox;
	}
	
	public ObjectMask equalMask( BoundingBox bbox, int equalVal ) {
		return delegate.equalMask(bbox, equalVal );
	}
	
	public VoxelBoxWrapper getVoxelBox() {
		return new VoxelBoxWrapper(delegate);
	}
	
	public void replaceVoxelBox( VoxelBoxWrapper vbNew ) {
		this.delegate = vbNew.any();
	}
	
	// Creates a new channel contain a duplication only of a particular slice
	public Channel extractSlice(int z) {
		return ChannelFactory.instance().create( delegate.extractSlice(z), getDimensions().getRes() );
	}
	
	public Channel scaleXY( ScaleFactor scaleFactor) {
		return scaleXY(scaleFactor, DEFAULT_INTERPOLATOR);
	}
		
	public Channel scaleXY( ScaleFactor scaleFactor, Interpolator interpolator ) {
		// We round as sometimes we get values which, for example, are 7.999999, intended to be 8, due to how we use our ScaleFactors
		int newSizeX = (int) Math.round(scaleFactor.getX() * getDimensions().getX());
		int newSizeY = (int) Math.round(scaleFactor.getY() * getDimensions().getY());
		return resizeXY( newSizeX, newSizeY, interpolator ); 
	}
	
	public Channel resizeXY( int x, int y) {
		return resizeXY(x, y, DEFAULT_INTERPOLATOR);
	}
	
	public Channel resizeXY( int x, int y, Interpolator interpolator ) {
		
		assert( FACTORY!=null );
		
		ImageDimensions sdNew = getDimensions().scaleXYTo(x,y);
		
		VoxelBox<? extends Buffer> ba = delegate.resizeXY(x, y, interpolator);
		assert(ba.extent().getVolumeXY()==ba.getPixelsForPlane(0).buffer().capacity());
		return FACTORY.create( ba, sdNew.getRes() );
	}
	
	public Channel maxIntensityProjection() {
		return flattenZProjection(
			VoxelBox::maxIntensityProj
		);
	}
	
	public Channel meanIntensityProjection() {
		return flattenZProjection(
			VoxelBox::meanIntensityProj
		);
	}

	// Duplicates the current channel
	public Channel duplicate() {
		Channel dup = FACTORY.create( delegate.duplicate(), getDimensions().getRes() );
		assert( dup.delegate.extent().equals( delegate.extent() ));
		return dup;
	}

	// The number of non-zero pixels.
	// Mainly intended for debugging, as it's useful in the debugger view
	public int numNonZeroPixels() {
		return delegate.countGreaterThan(0);
	}
	
	// TRUE if there is at least a single voxel with a particular value
	public boolean hasEqualTo( int value ) {
		return delegate.hasEqualTo(value);
	}
	
	// Counts the number of voxels with a value equal to a particular value
	public int countEqualTo( int value ) {
		return delegate.countEqual(value);
	}

	public void updateResolution(ImageResolution res) {
		dimensions = dimensions.duplicateChangeRes(res);
	}

	public VoxelDataType getVoxelDataType() {
		return delegate.dataType();
	}

	/** Display a histogram of voxel-intensities as the string-representation */
	@Override
	public String toString() {
		try {
			return HistogramFactory.create(this).toString();
		} catch (CreateException e) {
			return String.format("Error: %s",e);
		}
	}
	
	public boolean equalsDeep( Channel other) {
		return dimensions.equals(other.dimensions) && delegate.equalsDeep( other.delegate );
	}
	
	/** 
	 * Flattens the voxel-box in the z direction
	 * 
	 * @param voxelBox voxel-box to be flattened (i.e. 3D)
	 * @param flattenFunc function to perform the flattening
	 * @return flattened box (i.e. 2D)
	 */
	private Channel flattenZProjection( UnaryOperator<VoxelBox<? extends Buffer>> flattenFunc ) {
		int prevZSize = delegate.extent().getZ();
		return FACTORY.create(
			flattenFunc.apply(delegate),
			getDimensions().getRes().duplicateFlattenZ(prevZSize)
		);
	}
}
