/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input.bean.files;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.file.FilesProviderException;

/**
 * Base class for providing a list of {@link File}s.
 *
 * @author Owen Feehan
 */
public abstract class FilesProvider extends AnchorBean<FilesProvider> {

    /**
     * Creates or provides a list of files.
     *
     * @param parameters parameters influencing how input-files are obtained.
     * @return the list of files from the provider
     * @throws FilesProviderException if list of files cannot be successfully established.
     */
    public abstract List<File> create(InputManagerParameters parameters)
            throws FilesProviderException;

    /**
     * A root directory for these files, if it exists.
     *
     * <p>Any file that is part of the collection returned by this provider, must exist inside this
     * directory (either directly or in any nested subdirectories).
     *
     * @param inputContext the input-context.
     * @return a path to this directory.
     * @throws FilesProviderException if an error occurs establishing the root directory.
     */
    public abstract Optional<Path> rootDirectory(InputContextParameters inputContext)
            throws FilesProviderException;
}
