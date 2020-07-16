/*-
 * #%L
 * anchor-io-output
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
/* (C)2020 */
package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.namestyle.SimpleOutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows users to write various things to the file system based upon // the properties of the
 * current bound output manager
 *
 * <p>We use Operations so that the generator is only calculated, if the operation is actually
 * written
 *
 * @author Owen Feehan
 */
public interface Writer {

    Optional<BoundOutputManager> bindAsSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> folder)
            throws OutputWriteFailedException;

    void writeSubfolder(
            String outputName,
            Operation<? extends WritableItem, OutputWriteFailedException> collectionGenerator)
            throws OutputWriteFailedException;

    int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator,
            String index)
            throws OutputWriteFailedException;

    void write(
            OutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator)
            throws OutputWriteFailedException;

    default void write(
            String outputName,
            Operation<? extends WritableItem, OutputWriteFailedException> generator)
            throws OutputWriteFailedException {
        write(new SimpleOutputNameStyle(outputName), generator);
    }

    // Write a file with an index represented by an int, returns the number of files created
    default int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator,
            int index)
            throws OutputWriteFailedException {
        return write(outputNameStyle, generator, Integer.toString(index));
    }

    /**
     * The path to write a particular output to
     *
     * @param outputName
     * @param extension
     * @param manifestDescription
     * @param outputNamePrefix
     * @param outputNameSuffix
     * @param index
     * @return the path to write to or null if the output is not allowed
     */
    Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription,
            String outputNamePrefix,
            String outputNameSuffix,
            String index);

    OutputWriteSettings getOutputWriteSettings();
}
