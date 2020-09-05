/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats;

import java.util.function.DoubleConsumer;
import java.util.function.Function;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Length;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

public class DimensionsCreator {

    private IMetadata lociMetadata;

    public DimensionsCreator(IMetadata lociMetadata) {
        super();
        this.lociMetadata = lociMetadata;
    }

    public Dimensions apply(IFormatReader reader, ReadOptions readOptions, int seriesIndex) {

        assert (lociMetadata != null);

        Point3d res = new Point3d();

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeX(seriesIndex), res::setX);

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeY(seriesIndex), res::setY);

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeZ(seriesIndex), res::setZ);

        return new Dimensions(
                new Extent(reader.getSizeX(), reader.getSizeY(), readOptions.sizeZ(reader)),
                new Resolution(res));
    }

    private void metadataDim(Function<IMetadata, Length> funcDimRes, DoubleConsumer setter) {
        Length len = funcDimRes.apply(lociMetadata);
        if (len != null) {
            Number converted = len.value(UNITS.METER);

            // A null implies that len can not be converted to meters as units, so we abandon
            if (converted != null) {
                Double dbl = converted.doubleValue();
                setter.accept(dbl);
            }
        }
    }
}
