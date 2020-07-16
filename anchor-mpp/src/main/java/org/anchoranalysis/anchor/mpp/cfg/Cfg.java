package org.anchoranalysis.anchor.mpp.cfg;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

/// A particular configuration of marks
public final class Cfg implements Iterable<Mark>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2398855316191681489L;
	
	private final List<Mark> marks;

	public Cfg() {
		this( new ArrayList<>() );
	}
	
	public Cfg( Stream<Mark> stream ) {
		this(
			stream.collect( Collectors.toList() )
		);
	}
		
	public Cfg( List<Mark> marks ) {
		this.marks = marks;
	}
	
	public Cfg( Mark m ) {
		this();
		add( m );
	}
	
	public Cfg shallowCopy() {
		return new Cfg( marks.stream() );
	}

	public Cfg deepCopy() {
		return new Cfg(
			marks.stream().map(Mark::duplicate)
		);
	}

	public boolean add(Mark arg0) {
		return marks.add(arg0);
	}
	
	public void addAll(Cfg cfg) {
		for( Mark m : cfg ) {
			add(m);
		}
	}

	public boolean contains(Object arg0) {
		return marks.contains(arg0);
	}

	public final boolean isEmpty() {
		return marks.isEmpty();
	}

	public final int size() {
		return marks.size();
	}
	
	@Override
	public final Iterator<Mark> iterator() {
		return marks.iterator();
	}

	@Override
	public String toString() {
		
		String newLine = System.getProperty("line.separator");
		
		StringBuilder s = new StringBuilder("{");
		
		s.append( String.format("size=%d%n", marks.size() ) );
		
		for (Iterator<Mark> i = marks.iterator(); i.hasNext(); ) { 
			s.append( i.next().toString() );
			s.append( newLine );
		}
		
		s.append("}" );
		s.append( newLine );
		
		return s.toString();
	}
	
	public Mark remove(int index) {
		return marks.remove(index);
	}
	
	public void removeTwo(int index1, int index2) {
		
		int maxIndex = Math.max( index1, index2 );
		int minIndex = Math.min( index1, index2 );
		
		// Remove the second index first
		marks.remove( maxIndex );
		marks.remove( minIndex );
	}

	public final int randomIndex( RandomNumberGenerator randomNumberGenerator ) {
		return randomNumberGenerator.sampleIntFromRange(size());
	}
	
	public Mark get(int index) {
		return marks.get(index);
	}

	public final Mark randomMark( RandomNumberGenerator randomNumberGenerator ) {
		return marks.get( randomIndex(randomNumberGenerator) );
	}
	
	public final void exchange( int index, Mark newMark ) {
		marks.set(index, newMark);
	}
	
	// Inefficient, be careful with usage
	public int indexOf( Mark m ) {
		return marks.indexOf(m);
	}
	
	public ObjectCollectionWithProperties calcMask(
		ImageDimensions bndScene,
		RegionMembershipWithFlags rm,
		BinaryValuesByte bvOut,
		IDGetter<Mark> colorIDGetter
	) {
		
		ObjectCollectionWithProperties maskCollection = new ObjectCollectionWithProperties();
		
		for (int i=0; i<marks.size(); i++) {
			Mark mark = marks.get(i);
			
			if (rm.getRegionID() >= mark.numRegions()) {
				continue;
			}
			
			ObjectWithProperties mask = Cfg.calcMaskWithColor(
				mark,
				bndScene,
				rm,
				bvOut,
				colorIDGetter,
				i
			);
			maskCollection.add( mask );
		}
		
		return maskCollection;
	}
	
	public void scaleXY( double scaleFactor ) throws OptionalOperationUnsupportedException {
		
		for (Mark mark : marks) {
			mark.scale(scaleFactor);
		}
	}
	
	public Cfg marksAt(Point3d point, RegionMap regionMap, int regionID) {
		
		Cfg cfgOut = new Cfg();
		
		RegionMembership rm = regionMap.membershipForIndex(regionID);
		byte flags = rm.flags();
		
		// We cycle through each item in the configuration
		for (Mark m : this) {
			
			byte membership = m.evalPointInside(point);
			if (rm.isMemberFlag(membership,flags)) {
				cfgOut.add( m );
			}
		}
		return cfgOut;
	}
	
	public boolean equalsDeep( Cfg other ) {

		// Size
		if (size()!=other.size()) {
			return false;
		}
		
		int i = 0;
		for (Mark m : this) {
	
			if (!m.equalsDeep(  other.get(i++) )) {
				return false;
			}
		}
		return true;
	}

	// A hashmap of all the marks, using the Id as an index
	public Map<Integer,Mark> createIdHashMap() {
		
		HashMap<Integer,Mark> hashMap = new HashMap<>();
	
		for (Mark mark : this) {
			hashMap.put( mark.getId(), mark);
		}
		
		return hashMap;
	}
	
	public int[] createIdArr() {
		
		int[] idArr = new int[size()];
		
		int i = 0;
		for (Mark mark : this) {
			idArr[i++] = mark.getId();
		}
		
		return idArr;
	}
	
	// A hashmap of all the marks, using the Id as an index
	public Set<Mark> createSet() {
		
		HashSet<Mark> hashMap = new HashSet<>();
	
		for (Mark mark : this) {
			hashMap.add( mark );
		}
		
		return hashMap;
	}
	
	// A hashmap of all the marks, using the Id as an index, mapping to the id
	public Map<Mark,Integer> createHashMapToId() {
		
		HashMap<Mark,Integer> hashMap = new HashMap<>();
	
		for (int i=0; i<size(); i++) {
			hashMap.put( get(i), i );
		}
		
		return hashMap;
	}
	
	public Cfg createMerged( Cfg toMerge ) {
		
		Cfg mergedNew = shallowCopy();
		
		Set<Mark> set = mergedNew.createSet();
		
		for (Mark m : toMerge) {
			if (!set.contains(m)) {
				mergedNew.add( m );
			}
		}
		
		return mergedNew;
	}

	public List<BoundingBox> bboxList( ImageDimensions bndScene, int regionID ) {
		
		ArrayList<BoundingBox> list = new ArrayList<>(); 
		for (Mark m : this) {
			list.add( m.bbox(bndScene, regionID));
		}
		return list;
	}

	public Mark set(int index, Mark element) {
		return marks.set(index, element);
	}

	public List<Mark> getMarks() {
		return marks;
	}
	
	private static ObjectWithProperties calcMaskWithColor(
		Mark mark,
		ImageDimensions bndScene,
		RegionMembershipWithFlags rm,
		BinaryValuesByte bvOut,
		IDGetter<Mark> colorIDGetter,
		int iter
	) {
		ObjectWithProperties mask = mark.calcMask(bndScene, rm, bvOut );
		if (colorIDGetter!=null) {
			mask.setProperty("colorID", colorIDGetter.getID(mark, iter));
		}
		return mask;
	}
}
