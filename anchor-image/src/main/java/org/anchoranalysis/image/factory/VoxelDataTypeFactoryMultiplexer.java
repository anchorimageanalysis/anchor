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

package org.anchoranalysis.image.factory;

import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.Float;
import org.anchoranalysis.image.voxel.datatype.UnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedInt;
import org.anchoranalysis.image.voxel.datatype.UnsignedShort;

/**
 * Multiplexes betwen four different types of factories each representing a particular primitive
 * type
 *
 * @author Owen Feehan
 * @param <T>
 */
public abstract class VoxelDataTypeFactoryMultiplexer<T> {

    private T factoryByte;
    private T factoryShort;
    private T factoryInt;
    private T factoryFloat;

    public VoxelDataTypeFactoryMultiplexer(
            T factoryByte, T factoryShort, T factoryInt, T factoryFloat) {
        super();
        this.factoryByte = factoryByte;
        this.factoryShort = factoryShort;
        this.factoryInt = factoryInt;
        this.factoryFloat = factoryFloat;
    }

    /**
     * Multiplexes one of the factories according to data-type
     *
     * @param dataType the type to find a factory for
     * @return a factory if it exists, or else an exception
     */
    public T get(VoxelDataType dataType) {

        if (dataType.equals(UnsignedByte.INSTANCE)) {
            return factoryByte;
        } else if (dataType.equals(UnsignedShort.INSTANCE)) {
            return factoryShort;
        } else if (dataType.equals(UnsignedInt.INSTANCE)) {
            return factoryInt;
        } else if (dataType.equals(Float.INSTANCE)) {
            return factoryFloat;
        } else {
            throw new IncorrectVoxelDataTypeException("Non-existent type");
        }
    }
}
