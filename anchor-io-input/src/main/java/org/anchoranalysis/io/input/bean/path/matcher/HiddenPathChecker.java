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

package org.anchoranalysis.io.input.bean.path.matcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.SystemUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HiddenPathChecker {

    public static boolean includePath(Path path) {
        try {
            // There is a bug in Java (apparently fixed in version 13) where Files.isHidden
            //  does not recognize directories as hidden.
            return !path.toFile().exists() || !isHidden(path);
        } catch (Exception e) {
            // If we can't perform these operations, we consider the file not to be hidden
            // rather than throwing an exception
            return true;
        }
    }

    /*
     * A workaround for a bug in Java (apparently fixed in version 13) where {@link Files.isHidden}
     *  does not recognise directories as being hidden.
     *
     * <p>See <a href="https://stackoverflow.com/questions/53791740/why-does-files-ishiddenpath-return-false-for-directories-on-windows">Stack Overflow</a></p>
     **/
    private static boolean isHidden(Path path) throws IOException {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                DosFileAttributes dosFileAttributes =
                        Files.readAttributes(
                                path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                return dosFileAttributes.isHidden();
            } else {
                // Note a {@link ArrayIndexOutOfBoundsException} is being thrown here when running
                // on
                // the Linux subsystem of Windows. It's caught in {@link includePath}.
                return fallbackIsHidden(path);
            }
        } catch (UnsupportedOperationException e) {
            return fallbackIsHidden(path);
        }
    }

    /** The least preferred method for determining if a file is hidden. */
    private static boolean fallbackIsHidden(Path path) throws IOException {
        String pathAsString = path.toString();
        if (pathAsString.equals(".") || pathAsString.equals("..")) {
            // We consider any relative paths as not being hidden
            // On MacOS a . is considered hidden, which creates problems. So it's
            // important to explictly indicate that this is not-hidden.
            return false;
        } else {
            return Files.isHidden(path);
        }
    }
}
