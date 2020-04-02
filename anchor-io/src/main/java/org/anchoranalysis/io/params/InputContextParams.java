package org.anchoranalysis.io.params;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Additional paramaters that provide context for many beans that provide input-functions
 * 
 * @author Owen Feehan
 *
 */
public class InputContextParams {

	/**
	 * A list of paths referring to specific inputs;
	 */
	private List<Path> inputPaths;
	
	/** Iff non-NULL, a directory which can be used by beans to find input */
	private Path inputDir = null;
	
	/** A glob that can be used by beans to filter input */
	private String inputFilterGlob = "*";
	
	/** A list of extensions that can be used filter inputs */
	private Set<String> inputFilterExtensions = fallBackFilterExtensions();
	
	/** If non-null, then debug-mode is activated */
	private DebugModeParams debugModeParams;
	
	public boolean hasInputDir() {
		return inputDir!=null;
	}

	public Path getInputDir() {
		return inputDir;
	}

	// This should always be ab absolute path, never a relative one
	public void setInputDir(Path inputDir) throws IOException {
		
		if (inputDir!=null) {
			checkAbsolutePath(inputDir);
		}
		
		this.inputDir = inputDir;
	}

	public void setInputFilterGlob(String inputFilterGlob) {
		this.inputFilterGlob = inputFilterGlob;
	}
	
	private static void checkAbsolutePath(Path inputDir) throws IOException {
		if (!inputDir.isAbsolute()) {
			throw new IOException(
				String.format("An non-absolute path was passed to setInputDir() of %s", inputDir)
			);
		}
	}
	
	// If no filter extensions are provided from anywhere else, this is a convenient set of defaults
	private Set<String> fallBackFilterExtensions() {
		return new HashSet<>(Arrays.asList("jpg", "png", "tif", "tiff", "gif", "bmp"));
	}

	public Set<String> getInputFilterExtensions() {
		return inputFilterExtensions;
	}

	public void setInputFilterExtensions(Set<String> inputFilterExtensions) {
		this.inputFilterExtensions = inputFilterExtensions;
	}
	

	public boolean hasInputPaths() {
		return inputPaths!=null;
	}
	
	public boolean isDebugModeActivated() {
		return debugModeParams!=null;
	}

	public void setDebugModeParams(DebugModeParams debugModeParams) {
		this.debugModeParams = debugModeParams;
	}
	
	public DebugModeParams getDebugModeParams() {
		return debugModeParams;
	}
	
	public String getInputFilterGlob() {
		return inputFilterGlob;
	}

	public List<Path> getInputPaths() {
		return inputPaths;
	}

	public void setInputPaths(List<Path> inputPaths) {
		this.inputPaths = inputPaths;
	}


}
