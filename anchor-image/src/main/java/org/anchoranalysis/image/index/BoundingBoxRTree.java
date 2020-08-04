/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.index;

import com.newbrightidea.util.RTree;
import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID,
 * associated with the item in the R-Tree.
 *
 * @author Owen Feehan
 */
public class BoundingBoxRTree {

    private RTree<Integer> rTree;

    // We re-use this singlePoint to avoid memory allocation for a single point
    private float[] singlePoint = new float[] {0, 0, 0};

    private float[] singlePointExtent = new float[] {1, 1, 1};

    public BoundingBoxRTree(int maxEntriesSuggested) {
        // We insist that maxEntries is at least twice the minimum num items
        int minEntries = 1;
        int maxEntries = Math.max(maxEntriesSuggested, minEntries * 2);

        rTree = new RTree<>(maxEntries, minEntries, 3);
    }

    public BoundingBoxRTree(List<BoundingBox> bboxList, int maxEntriesSuggested) {
        this(maxEntriesSuggested);

        for (int i = 0; i < bboxList.size(); i++) {
            add(i, bboxList.get(i));
        }
    }

    public List<Integer> contains(Point3i point) {
        singlePoint[0] = (float) point.x();
        singlePoint[1] = (float) point.y();
        singlePoint[2] = (float) point.z();

        return rTree.search(singlePoint, singlePointExtent);
    }

    public List<Integer> intersectsWith(BoundingBox bbox) {

        float[] coords = minPoint(bbox);
        float[] dimensions = extent(bbox);

        return rTree.search(coords, dimensions);
    }

    public void add(int i, BoundingBox bbox) {
        float[] coords = minPoint(bbox);
        float[] dimensions = extent(bbox);

        rTree.insert(coords, dimensions, i);
    }

    private static float[] minPoint(BoundingBox bbox) {
        return new float[] {
            bbox.cornerMin().x(), bbox.cornerMin().y(), bbox.cornerMin().z()
        };
    }

    private static float[] extent(BoundingBox bbox) {
        return new float[] {bbox.extent().x(), bbox.extent().y(), bbox.extent().z()};
    }
}
