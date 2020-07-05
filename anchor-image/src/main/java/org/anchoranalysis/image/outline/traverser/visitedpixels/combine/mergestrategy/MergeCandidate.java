package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

import java.util.Optional;

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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.LoopablePoints;

// Maintains two paths, and their closest point
public class MergeCandidate {

	private PathWithClosest keep;
	private PathWithClosest merge;
	
	public MergeCandidate(ContiguousPixelPath toKeep, ContiguousPixelPath toMerge, Point3i toKeepPnt, Point3i toMergePnt) {
		this(
			new PathWithClosest(toKeep,toKeepPnt),
			new PathWithClosest(toMerge,toMergePnt)
		);
	}
	
	private MergeCandidate(PathWithClosest keep, PathWithClosest merge) {
		this.keep = keep;
		this.merge = merge;
	}
	
	
	/**
	 * Merges
	 * 
	 * @return the number of pixels removed
	 */
	public MergeStrategy determineCost() {
		return FindMergeStrategy.apply(keep,merge);
	}
	
	
	/**
	 * Merges
	 * 
	 * @return the number of pixels removed
	 */
	public int merge() {
		MergeStrategy strategy = FindMergeStrategy.apply(keep,merge);
		strategy.applyStrategy(this);
		return strategy.getCost();
	}
	
	
	
	public MergeCandidate reverse() {
		return new MergeCandidate(merge, keep);
	}
	
	
	/**
	 * Chops of one-side of keep, and one-side of merge
	 * 
	 * @param keepLeft if TRUE, chops off the left-side of keep, if FALSE rather the right-side
	 * @param mergeLeft if TRUE, chops off the left-side of merge, if FALSE rather the right-side
	 * @param replace if TRUE, replaces the points with a loop. if FALSE, simply removes them
	 */
	public void removeOrReplace( boolean keepLeft, boolean mergeLeft, boolean replace ) {
		removeOrReplaceOnPath(keep, keepLeft, replace);
		removeOrReplaceOnPath(merge, mergeLeft, replace);
	}
	
	private static void removeOrReplaceOnPath(PathWithClosest path, boolean left, boolean replace) {
		if (replace) {
			replaceWithLoop(path,left);
		} else {
			remove(path,left);
		}
	}
	
	private static void replaceWithLoop(PathWithClosest path, boolean left) {
		if (left) {
			Optional<LoopablePoints> lp = path.removeLeft();
			lp.ifPresent( points->
				path.insertBefore( points.loopPointsLeft() )
			);
		} else {
			Optional<LoopablePoints> lp = path.removeRight();
			lp.ifPresent( points->
				path.getPath().insertAfter( points.loopPointsRight() )
			);
		}
	}
	
	private static Optional<LoopablePoints> remove(PathWithClosest path, boolean left) {
		if (left) {
			return path.removeLeft();
		} else {
			return path.removeRight();
		}
	}
	
	public PathWithClosest getKeep() {
		return keep;
	}

	public PathWithClosest getMerge() {
		return merge;
	}
}
