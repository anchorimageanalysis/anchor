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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.extent.box.BoundingBox;

@AllArgsConstructor
public class SpatiallySeparate<T> {

    private RTree<T> tree;
    private Function<T, BoundingBox> extractBoundingBox;

    /**
     * Splits the collection of objects into spatially separate <i>clusters</i>.
     *
     * <p>Any objects whose bounding-boxes intersect belong to the same cluster, but otherwise not.
     *
     * <p>This is similar to a simplified <a href="https://en.wikipedia.org/wiki/DBSCAN">DB Scan
     * algorithm</a>.
     *
     * @return a list of object-collections, each object-collection is guaranteed to be spatially
     *     separate from the others.
     */
    public List<List<T>> spatiallySeperate(Set<T> unprocessed) {

        List<List<T>> out = new ArrayList<>();

        while (!unprocessed.isEmpty()) {

            T identifier = unprocessed.iterator().next();

            List<T> spatiallyConnected = new ArrayList<>();
            addSpatiallyConnected(spatiallyConnected, identifier, unprocessed);
            out.add(spatiallyConnected);
        }
        assert (unprocessed.isEmpty());
        return out;
    }

    private void addSpatiallyConnected(List<T> spatiallyConnected, T source, Set<T> unprocessed) {

        unprocessed.remove(source);
        spatiallyConnected.add(source);

        List<T> queue = tree.intersectsWith(extractBoundingBox.apply(source));

        while (!queue.isEmpty()) {
            T current = queue.remove(0);

            if (unprocessed.contains(current)) {

                unprocessed.remove(current);
                spatiallyConnected.add(current);

                queue.addAll(tree.intersectsWith(extractBoundingBox.apply(current)));
            }
        }
    }
}
