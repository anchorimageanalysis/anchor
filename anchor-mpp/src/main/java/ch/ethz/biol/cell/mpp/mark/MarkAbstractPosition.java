package ch.ethz.biol.cell.mpp.mark;

/*
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;


public abstract class MarkAbstractPosition extends Mark implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6976277985708631268L;
	
	// START mark state
	private Point3d pos;
	// END mark state
	
	// Constructor
	public MarkAbstractPosition() {
		super();
		this.pos = new Point3d();
	}

	// Copy constructor
	public MarkAbstractPosition(MarkAbstractPosition src) {
		super(src);
    	this.pos = new Point3d( src.pos );
	}

    
    public String strPos() {
    	return String.format("[%6.1f,%6.1f,%6.1f]", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }
    
   
    
    @Override
	public void scale( double mult_factor ) {
    	scaleXYPnt(this.pos,mult_factor);
    }
    
    public static void scaleXYPnt( Point3d pnt, double mult_factor ) {
    	pnt.setX( pnt.getX() * mult_factor ); 
    	pnt.setY( pnt.getY() * mult_factor );
    }
    
	@Override
	public Point3d centerPoint() {
		return getPos();
	}
	
	public Point3d getPos() {
		return pos;
	}

	public void setPos(Point3d pos) {
		this.pos = pos;
		clearCacheID();
	}
	
	// Checks if two marks are equal by comparing all attributes
	@Override
	public boolean equalsDeep(Mark m) {
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if (!(m instanceof MarkAbstractPosition)) {
			return false;
		}
		
		MarkAbstractPosition trgt = (MarkAbstractPosition) m;
		
		if (!pos.equals(trgt.pos)) {
			return false;
		}

		return true;
	}
	
	@Override
	public ObjMaskWithProperties calcMask( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut ) {
		
		ObjMaskWithProperties mask = super.calcMask(bndScene, rm, bvOut);
		mask.setProperty("midpointInt", mask.getBoundingBox().calcRelToLowerEdgeInt( pos ) );
		return mask;
	}
	
	@Override
	public NameValueSet<String> generateProperties(ImageRes sr) {
		NameValueSet<String> nvc = super.generateProperties(sr);
		
		int numDims = numDims();
		
		if (numDims>=1) {
			nvc.add( new NameValue<>("Pos X",  String.format("%1.2f", pos.getX() )) );
		}
		if (numDims>=2) {
			nvc.add( new NameValue<>("Pos Y",  String.format("%1.2f", pos.getY() )) );
		}
		if (numDims>=3) {
			nvc.add( new NameValue<>("Pos Z",  String.format("%1.2f", pos.getZ() )) );
		}
		return nvc;
	}
	
	@Override
	public void assignFrom( Mark srcMark ) throws OptionalOperationUnsupportedException {
		
		if (!(srcMark instanceof MarkAbstractPosition)) {
			throw new OptionalOperationUnsupportedException("srcMark must be of type MarkAbstractPosition");
		}
		
		MarkAbstractPosition srcMarkAbstractPos = (MarkAbstractPosition) srcMark;
		this.pos = srcMarkAbstractPos.getPos();
		
		// As the cacheID might be cleared by previous sets
		super.assignFrom( srcMark );
	}
}
