/*-
 * #%L
 * anchor-io-input
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
package org.anchoranalysis.io.input.file;

import com.google.common.base.Preconditions;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A base class for inputs that refer to a single file.
 *
 * @author Owen Feehan
 */
public abstract class SingleFileInputBase implements InputFromManager {

    private NamedFile file;

    /**
     * Creates for a particular file.
     *
     * @param file the file, with an associated name.
     */
    protected SingleFileInputBase(NamedFile file) {
        this.file = file;
        Preconditions.checkArgument(!file.getIdentifier().isEmpty());
    }

    @Override
    public String identifier() {
        return file.getIdentifier();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return Optional.of(file.getPath());
    }

    @Override
    public String toString() {
        return identifier();
    }

    /**
     * The associated single file.
     *
     * @return the file, as used internally.
     */
    public File getFile() {
        return file.getFile();
    }
}
