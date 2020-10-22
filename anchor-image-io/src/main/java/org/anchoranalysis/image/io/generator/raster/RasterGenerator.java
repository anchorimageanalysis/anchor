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

package org.anchoranalysis.image.io.generator.raster;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.io.generator.TransformingGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/** 
 * Transfroms an entity to a {@link Stack} and writes it to the file-system.
 * 
 * @author Owen Feehan
 */
public abstract class RasterGenerator<T> implements TransformingGenerator<T,Stack> {

    @Override
    public void write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(),
                outputNameStyle.getOutputName(),
                "",
                outputter);
    }

    /** As only a single-file is involved, this methods delegates to a simpler virtual method. */
    @Override
    public int writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        writeInternal(
                element,
                outputNameStyle.getFilenameWithoutExtension(index),
                outputNameStyle.getOutputName(),
                index,
                outputter);

        return 1;
    }
   
    /**
     * Is the image being created RGB?
     * 
     * @return
     */
    public abstract boolean isRGB();
        
    public abstract StackWriteOptions writeOptions();
    
    public abstract Optional<ManifestDescription> createManifestDescription();
    
    protected abstract void writeToFile(
            T element, OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    protected abstract String selectFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException;
    
    private void writeInternal(
            T element,
            String filenameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            Path pathToWriteTo =
                    outputter.makeOutputPath(
                            filenameWithoutExtension, selectFileExtension(outputter.getSettings()));

            // First write to the file system, and then write to the operation-recorder. Thi
            writeToFile(element, outputter.getSettings(), pathToWriteTo);

            createManifestDescription()
                    .ifPresent(
                            manifestDescription ->
                                    outputter.writeFileToOperationRecorder(
                                            outputName, pathToWriteTo, manifestDescription, index));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
