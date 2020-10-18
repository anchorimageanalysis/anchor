/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;

/**
 * Allows two {@link WriteOperationRecorder} function together as if they are one
 *
 * <p>Every operation is applied to both.
 *
 * @author Owen Feehan
 */
public class DualWriterOperationRecorder implements WriteOperationRecorder {

    private WriteOperationRecorder recorder1;
    private WriteOperationRecorder recorder2;

    public DualWriterOperationRecorder(
            WriteOperationRecorder recorder1, WriteOperationRecorder recorder2) {
        super();
        this.recorder1 = recorder1;
        this.recorder2 = recorder2;
    }

    @Override
    public void recordWrittenFile(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        recorder1.recordWrittenFile(outputName, manifestDescription, outFilePath, index);
        recorder2.recordWrittenFile(outputName, manifestDescription, outFilePath, index);
    }

    @Override
    public WriteOperationRecorder recordSubdirectoryCreated(
            Path relativeFolderPath,
            ManifestDirectoryDescription manifestDescription,
            SubdirectoryBase folderWrite) {
        WriteOperationRecorder folder1 =
                recorder1.recordSubdirectoryCreated(
                        relativeFolderPath, manifestDescription, folderWrite);
        WriteOperationRecorder folder2 =
                recorder2.recordSubdirectoryCreated(
                        relativeFolderPath, manifestDescription, folderWrite);
        return new DualWriterOperationRecorder(folder1, folder2);
    }
}
