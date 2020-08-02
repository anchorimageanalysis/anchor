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

package org.anchoranalysis.io.manifest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CallableWithException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.ManifestDeserializer;

public class ManifestRecorderFile {

    private final File file;
    private final CallableWithException<ManifestRecorder, OperationFailedException> operation;
    
    public ManifestRecorderFile(File file, ManifestDeserializer manifestDeserializer) {
        this.file = file;
        this.operation = CachedOperation.of( () -> {
            try {
                if (!file.exists()) {
                    throw new OperationFailedException(
                            String.format("File %s cannot be found", file.getPath()));
                }
                return manifestDeserializer.deserializeManifest(file);
            } catch (DeserializationFailedException e) {
                throw new OperationFailedException(e);
            }
        });
    }
    
    public ManifestRecorder call() throws OperationFailedException {
        return operation.call();
    }
    
    public Path getRootPath() {
        // Returns the path of the root of the manifest file (or what it will become)
        return Paths.get(file.getParent());
    }
}
