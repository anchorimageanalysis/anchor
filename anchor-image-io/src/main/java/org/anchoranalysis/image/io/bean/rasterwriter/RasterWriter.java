/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.bean.rasterwriter;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.series.StackSeries;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;

/**
 * Writes a stack (i.e. raster) to the filesystem.
 *
 * @author Owen Feehan
 */
public abstract class RasterWriter extends AnchorBean<RasterWriter> {

    /**
     * File-extensions for files written by this writer.
     *
     * @param writeOptions options which may influence how a raster is written.
     */
    public abstract String fileExtension(RasterWriteOptions writeOptions);

    /**
     * Writes a stack to the filesystem at a particular path.
     *
     * @param stack the stack to write
     * @param filePath the path to write the file to
     * @param makeRGB if TRUE, the image should be written as a RGB image, rather than as separate
     *     channels.
     * @param writeOptions options which may influence how a raster is written.
     * @throws RasterIOException if anything goes wrong whle writing.
     */
    public abstract void writeStack(
            Stack stack, Path filePath, boolean makeRGB, RasterWriteOptions writeOptions)
            throws RasterIOException;

    /**
     * Writes a series of stacks to the filesystem at a particular path.
     *
     * @param stackSeries the series of stacks
     * @param filePath the path to write the file to
     * @param makeRGB if TRUE, the image should be written as a RGB image, rather than as separate
     *     channels.
     * @param writeOptions options which may influence how a raster is written.
     * @throws RasterIOException
     */
    public abstract void writeStackSeries(
            StackSeries stackSeries,
            Path filePath,
            boolean makeRGB,
            RasterWriteOptions writeOptions)
            throws RasterIOException;
}
