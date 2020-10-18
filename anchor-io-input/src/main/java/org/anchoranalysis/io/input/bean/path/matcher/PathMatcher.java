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

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.path.matcher.DualPathPredicates;
import org.anchoranalysis.io.input.path.matcher.FindFilesException;
import org.anchoranalysis.io.input.path.matcher.FindMatchingFiles;
import org.anchoranalysis.io.input.path.matcher.PathMatchConstraints;

/**
 * Matches file-paths against some kind of pattern.
 *
 * @author Owen
 */
public abstract class PathMatcher extends AnchorBean<PathMatcher> {

    /**
     * Finds a collection of files that match particular conditions on their paths.
     *
     * @param directory root directory to search
     * @param recursive whether to recursively search
     * @param ignoreHidden whether to ignore hidden files/directories or not
     * @param maxDirectoryDepth a maximum depth in directories to search
     * @param params parameters providing input-context
     * @param acceptDirectoryErrors if true, continues when a directory-access-error occurs (logging
     *     it), otherwise throws an exception
     * @return a collection of files matching the conditions
     * @throws InputReadFailedException if an error occurrs reading/writing or interacting with the
     *     filesystem
     */
    public Collection<File> matchingFiles(
            Path directory,
            boolean recursive,
            boolean ignoreHidden,
            boolean acceptDirectoryErrors,
            int maxDirectoryDepth,
            InputManagerParams params)
            throws InputReadFailedException {

        if (directory.toString().isEmpty()) {
            throw new InputReadFailedException(
                    "The directory is unspecified (an empty string) which is not allowed. Consider using '.' for the current working directory");
        }

        if (!directory.toFile().exists() || !directory.toFile().isDirectory()) {
            throw new InputReadFailedException(
                    String.format(
                            "Directory '%s' does not exist",
                            directory.toAbsolutePath().normalize()));
        }

        DualPathPredicates predicates =
                createPredicates(directory, ignoreHidden, params.getInputContext());
        try {
            return createMatchingFiles(params.getProgressReporter(), recursive)
                    .findMatchingFiles(
                            directory,
                            new PathMatchConstraints(predicates, maxDirectoryDepth),
                            acceptDirectoryErrors,
                            params.getLogger());
        } catch (FindFilesException e) {
            throw new InputReadFailedException("Cannot find matching files", e);
        }
    }

    protected abstract Predicate<Path> createMatcherFile(
            Path directory, InputContextParams inputContext) throws InputReadFailedException;

    private DualPathPredicates createPredicates(
            Path directory, boolean ignoreHidden, InputContextParams params)
            throws InputReadFailedException {

        // Many checks are possible on a file, including whether it is hidden or not
        Predicate<Path> fileMatcher =
                maybeAddIgnoreHidden(ignoreHidden, createMatcherFile(directory, params));

        // The only check on a directory is (maybe) whether it is hidden or not
        Predicate<Path> directoryMatcher = maybeAddIgnoreHidden(ignoreHidden, p -> true);

        return new DualPathPredicates(fileMatcher, directoryMatcher);
    }

    private Predicate<Path> maybeAddIgnoreHidden(boolean ignoreHidden, Predicate<Path> pred) {
        if (ignoreHidden) {
            return p -> pred.test(p) && HiddenPathChecker.includePath(p);
        } else {
            return pred;
        }
    }

    private FindMatchingFiles createMatchingFiles(
            ProgressReporter progressReporter, boolean recursive) {
        return new FindMatchingFiles(recursive, progressReporter);
    }
}
