package org.anchoranalysis.io.bean.file.matcher;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.file.findmatching.FindFilesException;
import org.anchoranalysis.io.file.findmatching.FindMatchingFiles;
import org.anchoranalysis.io.file.findmatching.FindMatchingFilesWithProgressReporter;
import org.anchoranalysis.io.file.findmatching.FindMatchingFilesWithoutProgressReporter;
import org.anchoranalysis.io.file.findmatching.PathMatchConstraints;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Matches filepaths against patterns
 * 
 * @author Owen
 *
 */
public abstract class FileMatcher extends AnchorBean<FileMatcher> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Finds a collection of files that match particular conditions
	 * 
	 * @param dir root directory to search
	 * @param recursive whether to recursively search
	 * @param ignoreHidden whether to ignore hidden files/directories or not 
	 * @param maxDirDepth a maximum depth in directories to search
	 * @param params parameters providing input-context
	 * @param boolean acceptDirectoryErrors if true, continues when a directory-access-error occurs (logging it), otherwise throws an exception
	 * @return a collection of files matching the conditions
	 * @throws IOException if an error occurrs reading/writing or interacting with the filesystem
	 */
	public Collection<File> matchingFiles(Path dir, boolean recursive, boolean ignoreHidden, boolean acceptDirectoryErrors, int maxDirDepth, InputManagerParams params) throws AnchorIOException {
		
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {
			throw new AnchorIOException( String.format("Directory '%s' does not exist", dir) );
		}
		
		// Many checks are possible on a file, including whether it is hidden or not
		Predicate<Path> filePred = maybeAddIgnoreHidden( ignoreHidden, createMatcherFile(dir, params.getInputContext()) );
		
		// The only check on a directory is (maybe) whether it is hidden or not
		Predicate<Path> dirPred = maybeAddIgnoreHidden( ignoreHidden, p->true ); 
		
		try {
			return createMatchingFiles(params.getProgressReporter(), recursive).apply(
				dir,
				new PathMatchConstraints(
					filePred,
					dirPred,
					maxDirDepth
				),
				acceptDirectoryErrors,
				params.getLogger()
			);
		} catch (FindFilesException e) {
			throw new AnchorIOException("Cannot find matching files", e);
		}
	}
	
	private Predicate<Path> maybeAddIgnoreHidden( boolean ignoreHidden, Predicate<Path> pred ) {
		if (ignoreHidden) {
			return p -> pred.test(p) && HiddenPathChecker.includePath(p);
		} else {
			return pred;
		}
	}
	
	protected abstract Predicate<Path> createMatcherFile( Path dir, InputContextParams inputContext );
	
	private FindMatchingFiles createMatchingFiles( ProgressReporter progressReporter, boolean recursive ) {
		if (progressReporter==null) {
			return new FindMatchingFilesWithoutProgressReporter();
		} else {
			return new FindMatchingFilesWithProgressReporter(recursive, progressReporter);
		}
	}
}
