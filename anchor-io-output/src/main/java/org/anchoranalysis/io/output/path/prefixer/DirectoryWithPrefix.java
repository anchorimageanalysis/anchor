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

package org.anchoranalysis.io.output.path.prefixer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A directory into which files can be written, and a prefix for the name of each written file.
 *
 * <p>A <i>delimeter</i> may additionally be specified that is inserted between the prefix and a
 * filename when a filename is specified (which may not always be the case).
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class DirectoryWithPrefix implements PathCreator {

    /** The directory in which files can be written to. */
    @Getter private Path directory;

    /** Always prepended to outputted filenames. */
    @Setter private String prefix = "";

    /** Added between {@code prefix} and the filename, if the filename is defined. */
    @Setter private String delimiter = "";

    /**
     * Creates for a directory, with no prefix.
     *
     * @param directory the directory.
     */
    public DirectoryWithPrefix(Path directory) {
        setDirectory(directory.normalize());
    }

    /**
     * Assigns a new directory, replacing the existing directory.
     *
     * @param directory the directory.
     */
    public void setDirectory(Path directory) {
        this.directory = directory.normalize();
    }

    /**
     * A path that combines the {@code directory} and {@code fileNamePrefix} and {@code delimiter}.
     *
     * @param includeDelimeter if true, the delimeter is included in the combined prefix.
     * @return the combined path
     */
    public Path asPath(boolean includeDelimeter) {
        if (includeDelimeter) {
            return getDirectory().resolve(prefixWithDelimeter());
        } else {
            return getDirectory().resolve(prefix);
        }
    }

    /**
     * The prefix concatenated with the delimeter.
     *
     * @return the result of the concatenation above.
     */
    public String prefixWithDelimeter() {
        return prefix + delimiter;
    }

    @Override
    public Path makePathAbsolute(
            Optional<String> suffix, Optional<String> extension, String fallbackSuffix) {
        return directory.resolve(buildFilename(suffix, extension, fallbackSuffix));
    }

    @Override
    public Path makePathRelative(Path fullPath) {
        return directory.relativize(fullPath);
    }

    /**
     * Builds a filename to be used together with the directory.
     *
     * @param suffix a suffix to insert after the {@code prefix} and before the {@code extension}
     * @param extension an {@code extension} to include as the final part of the filename
     * @param fallbackSuffix if neither a {@code prefix} is defined nor a {@code suffix}, then this
     *     provides a suffix to use so a file isn't only an extension.
     * @return a string describing the filename, ending with a period and {@code extension} if an
     *     extension is defined.
     */
    private String buildFilename(
            Optional<String> suffix, Optional<String> extension, String fallbackSuffix) {
        if (!prefix.isEmpty()) {
            return prefix + concatenate(suffix, extension).orElse("");
        } else {
            // If the prefix is empty, we don't ever want to try only an extension, so we use
            //  a fallbackSuffix
            Optional<String> nonEmptySuffix = Optional.of(suffix.orElse(fallbackSuffix));
            return concatenate(nonEmptySuffix, extension).get();
        }
    }

    private Optional<String> concatenate(Optional<String> suffix, Optional<String> extension) {
        if (suffix.isPresent() || extension.isPresent()) {
            String contributionFromSuffix = contributionFrom(suffix, delimiter);
            String contributionFromExtension = contributionFrom(extension, ".");
            return Optional.of(contributionFromSuffix + contributionFromExtension);
        } else {
            return Optional.empty();
        }
    }

    private static String contributionFrom(Optional<String> string, String prefix) {
        return string.map(value -> prefix + value).orElse("");
    }
}
