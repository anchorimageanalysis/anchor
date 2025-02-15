/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.system.path.ResolvePathAbsolute;

/**
 * Calculates file-system paths involving an {@link AnchorBean}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanPathCalculator {

    /**
     * Calculates a path from the current bean.
     *
     * @param bean a bean that has been loaded from the file-system (and retains a link to the path
     *     it was loaded from).
     * @param relativePath a relative-path
     * @return the relative-path joined into the location of the bean on the file-system, if it can
     *     be calculated.
     */
    public static Optional<Path> pathFromBean(AnchorBean<?> bean, String relativePath) {
        Path relative = Paths.get(relativePath);
        Optional<Path> localPath = bean.getLocalPath();
        if (localPath.isPresent()) {
            return Optional.of(ResolvePathAbsolute.resolve(localPath.get(), relative));
        } else {
            return Optional.empty();
        }
    }
}
