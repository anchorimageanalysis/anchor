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

package org.anchoranalysis.io.bean.provider.file.filter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FileProviderException;

public class FilterForExistingFiles extends FilterFileProvider {

    // START BEAN PROPERTIES
    @BeanField
    private List<FilePathGenerator> listFilePathGenerator =
            new ArrayList<>(); // All files need to be present
    // END BEAN PROPERTIES

    @Override
    protected boolean isFileAccepted(File file, boolean debugMode) throws FileProviderException {

        try {
            for (FilePathGenerator fpg : listFilePathGenerator) {
                Path annotationPath = fpg.outFilePath(file.toPath(), debugMode);

                if (!annotationPath.toFile().exists()) {
                    return false;
                }
            }
            return true;

        } catch (AnchorIOException e) {
            throw new FileProviderException(e);
        }
    }

    public List<FilePathGenerator> getListFilePathGenerator() {
        return listFilePathGenerator;
    }

    public void setListFilePathGenerator(List<FilePathGenerator> listFilePathGenerator) {
        this.listFilePathGenerator = listFilePathGenerator;
    }
}
