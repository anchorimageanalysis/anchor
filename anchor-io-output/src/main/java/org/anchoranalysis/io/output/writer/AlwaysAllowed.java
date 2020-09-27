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

package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.namestyle.SimpleOutputNameStyle;
import org.anchoranalysis.io.output.bound.OutputterChecked;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows every output, irrespective of whether the {@link OutputterChecked} allows the output-name.
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class AlwaysAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** Bound output manager */
    private final OutputterChecked outputter;

    /** Execute before every operation */
    private final WriterExecuteBeforeEveryOperation preop;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<OutputterChecked> createSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder)
            throws OutputWriteFailedException {

        preop.execute();
        return Optional.of(outputter.deriveSubdirectory(outputName, manifestDescription, manifestFolder));
    }

    @Override
    public boolean writeSubdirectoryWithGenerator(String outputName, GenerateWritableItem<?> collectionGenerator)
            throws OutputWriteFailedException {

        preop.execute();

        collectionGenerator.get().write(new IntegerSuffixOutputNameStyle(outputName, 3), outputter);
        
        return true;
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            GenerateWritableItem<?> generator,
            String index)
            throws OutputWriteFailedException {

        preop.execute();
        return generator.get().write(outputNameStyle, index, outputter);
    }

    // Write a file without checking if the outputName is allowed
    @Override
    public boolean write(String outputName, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException {
        preop.execute();
        generator.get().write( new SimpleOutputNameStyle(outputName), outputter);
        return true;
    }

    // A non-generator way of creating outputs, that are still included in the manifest
    // Returns null if output is not allowed
    @Override
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription) {

        preop.execute();

        Path outPath =
                outputter.outFilePath(outputName + "." + extension);

        manifestDescription.ifPresent(
                md -> outputter.writeFileToOperationRecorder(outputName, outPath, md, ""));
        return Optional.of(outPath);
    }
}
