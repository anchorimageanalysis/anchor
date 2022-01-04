/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.collection;

import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

@AllArgsConstructor
public class ColoredOverlayCollection implements Iterable<Overlay> {

    @Getter private OverlayCollection overlays;
    @Getter private ColorList colors;

    public ColoredOverlayCollection() {
        overlays = new OverlayCollection();
        colors = new ColorList();
    }

    public boolean add(Overlay overlay, RGBColor color) {
        colors.add(color);
        return overlays.add(overlay);
    }

    @Override
    public Iterator<Overlay> iterator() {
        return overlays.iterator();
    }

    public int size() {
        return overlays.size();
    }

    public Overlay get(int index) {
        return overlays.get(index);
    }

    public RGBColor getColor(int index) {
        return colors.get(index);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for (int i = 0; i < overlays.size(); i++) {
            RGBColor color = colors.get(i);
            Overlay overlay = overlays.get(i);
            builder.append(String.format("col=%s\tol=%s%n", color, overlay));
        }
        builder.append("}\n");
        return builder.toString();
    }

    // TODO - make more efficient using RTrees
    public ColoredOverlayCollection subsetWhereBBoxIntersects(
            Dimensions scene, DrawOverlay drawOverlay, List<BoundingBox> toIntersectWith) {

        ColoredOverlayCollection out = new ColoredOverlayCollection();

        for (int i = 0; i < size(); i++) {

            Overlay overlay = get(i);

            if (overlay.box(drawOverlay, scene).intersection().existsWithAny(toIntersectWith)) {
                out.add(overlay, getColor(i));
            }
        }
        return out;
    }
}
