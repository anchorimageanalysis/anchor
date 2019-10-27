package ch.ethz.biol.cell.gui.overlay;

/*
 * #%L
 * anchor-overlay
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


import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;

public class ColoredOverlayCollection implements Iterable<Overlay> {

	private OverlayCollection delegate;
	private ColorList colors;

	public ColoredOverlayCollection() {
		delegate = new OverlayCollection();
		colors = new ColorList();
	}
	
	public ColoredOverlayCollection(OverlayCollection overlayCollection, ColorList colors) {
		super();
		this.delegate = overlayCollection;
		this.colors = colors;
	}
	
	public boolean add(Overlay e, RGBColor color) {
		colors.add(color);
		return delegate.add(e);
	}

//	public boolean addAll(Collection<? extends Overlay> c) {
//		return delegate.addAll(c);
//	}

	@Override
	public Iterator<Overlay> iterator() {
		return delegate.iterator();
	}

	public int size() {
		return delegate.size();
	}

	public Overlay remove(int index) {
		colors.remove(index);
		return delegate.remove(index);
	}

	public Overlay get(int index) {
		return delegate.get(index);
	}
	
	public RGBColor getColor(int index) {
		return colors.get(index);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for( int i=0; i<delegate.size(); i++ ) {
			RGBColor col = colors.get(i);
			Overlay ol = delegate.get(i);
			sb.append( String.format("col=%s\tol=%s\n", col, ol) );
		}
		sb.append("}\n");
		return sb.toString();
	}

	


	public ColorList getColorList() {
		return colors;
	}
	
	

	public ColoredOverlayCollection createSubsetFromIDs( IndicesSelection indices ) {

		ColoredOverlayCollection out = new ColoredOverlayCollection();
		
		// This our current
		for( int i=0; i<size(); i++) {
			Overlay overlay = get(i);
			
			if (indices.contains(overlay.getId())) {
				out.add( overlay, getColorList().get(i) );
			}
		}
		
		return out;
	}
	
	
	
	// TODO - make more efficient using RTrees
	// Calculates mask
	public ColoredOverlayCollection subsetWhereBBoxIntersects( ImageDim bndScene, OverlayWriter maskWriter, List<BoundingBox> intersectList ) {
		
		ColoredOverlayCollection out = new ColoredOverlayCollection();
		
		for (int i=0; i<size(); i++) {
			
			Overlay overlay = get(i);
			
			if (overlay.bbox(maskWriter, bndScene).intersect(intersectList)) {
				out.add(overlay, getColor(i));
			}
		}
		
		//log.info( String.format("intersect size size=%d", intersectCfg.size() ));
		
		return out;
	}
	
	
	
	
	
	// Everything from the two Cfgs which isn't in the intersection
	public static OverlayCollection createIntersectionComplement( ColoredOverlayCollection cfg1, ColoredOverlayCollection cfg2 ) {
		
		OverlayCollection out = new OverlayCollection();
		
		if (cfg2==null) {
			out.addAll(cfg1.withoutColor());
			return out;
		}
		
		Set<Overlay> set1 = cfg1.createSet();
		Set<Overlay> set2 = cfg2.createSet();
				
		for (Overlay m : cfg1) {
			if (!set2.contains(m)) {
				out.add( m );
			}
		}
		
		for (Overlay m : cfg2 ) {
			if (!set1.contains(m)) {
				out.add( m );
			}
		}
		
		return out;
	}

	public OverlayCollection withoutColor() {
		return delegate;
	}

	public List<BoundingBox> bboxList(OverlayWriter maskWriter,
			ImageDim dim) {
		return delegate.bboxList(maskWriter, dim);
	}

	public Set<Overlay> createSet() {
		return delegate.createSet();
	}

	public OverlayCollection getOverlayCollection() {
		return delegate;
	}
	
	
}
