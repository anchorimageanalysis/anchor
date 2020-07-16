/*-
 * #%L
 * anchor-io-generator
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
package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceIncrementalWriter<T> implements GeneratorSequenceIncremental<T> {

    private GeneratorSequenceNonIncrementalWriter<T> delegate;

    private int iter = 0;
    private int startIndex = 0;

    // Automatically create a ManifestDescription for the folder from the Generator
    public GeneratorSequenceIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            int startIndex,
            boolean checkIfAllowed) {
        delegate =
                new GeneratorSequenceNonIncrementalWriter<>(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        iterableGenerator,
                        checkIfAllowed);
        this.iter = startIndex;
        this.startIndex = startIndex;
    }

    // User-specified ManifestDescription for the folder
    public GeneratorSequenceIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            ManifestDescription folderManifestDescription,
            int startIndex,
            boolean checkIfAllowed) {
        delegate =
                new GeneratorSequenceNonIncrementalWriter<>(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        iterableGenerator,
                        checkIfAllowed,
                        folderManifestDescription);
        this.startIndex = startIndex;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start(new IncrementalSequenceType(startIndex), -1);
    }

    @Override
    public void add(T element) throws OutputWriteFailedException {
        delegate.add(element, String.valueOf(iter));
        iter++;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    public boolean isOn() {
        return delegate.isOn();
    }

    public Optional<BoundOutputManager> getSubFolderOutputManager() {
        return delegate.getSubFolderOutputManager();
    }
}
