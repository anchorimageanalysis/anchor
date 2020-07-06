package org.anchoranalysis.io.output.bean;

import java.nio.file.Path;
import java.util.Optional;

/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.LazyDirectoryFactory;

import lombok.Getter;
import lombok.Setter;

public abstract class OutputManagerWithPrefixer extends OutputManager {

	// BEAN PROPERTIES
	@BeanField @Getter @Setter
	private FilePathPrefixer filePathPrefixer;
	
	/**  
	 * Whether to silently first delete any existing output at the intended path, or rather throw an error.
	 * 
	 * If true, if an existing output folder (at intended path) is deleted
	 * If false, an error is thrown if the folder already exists
	 */
	@BeanField @Getter @Setter
	private boolean silentlyDeleteExisting = false;

	@BeanField @Getter @Setter
	private OutputWriteSettings outputWriteSettings = new OutputWriteSettings();
	// END BEAN PROPERTIES
		
	// Binds the output to be connected to a particular file and experiment
	@Override
	public FilePathPrefix prefixForFile(
		PathWithDescription input,
		String expIdentifier,
		Optional<ManifestRecorder> manifestRecorder,
		Optional<ManifestRecorder> experimentalManifestRecorder,
		FilePathPrefixerParams context
	) throws FilePathPrefixerException {
		
		// Calculate a prefix from the incoming file, and create a file path generator
		FilePathPrefix fpp = filePathPrefixer.outFilePrefix( input, expIdentifier, context );
		
		FilePathDifferenceFromFolderPath fpd = new FilePathDifferenceFromFolderPath();
		
		try {
			fpd.init(
				this.filePathPrefixer.rootFolderPrefix(expIdentifier, context).getCombinedPrefix(),
				fpp.getCombinedPrefix()
			);
		} catch (AnchorIOException e) {
			throw new FilePathPrefixerException(e);
		}
			
		experimentalManifestRecorder.ifPresent(
			mr -> writeManifestExperimentFolder(mr, fpd.getRemainderCombined())
		);

		manifestRecorder.ifPresent(
			mr -> mr.init( fpp.getFolderPath() )	
		);
		
		return fpp;
	}
	
	private static void writeManifestExperimentFolder( ManifestRecorder manifestRecorderExperiment, Path rootPath ) {
		manifestRecorderExperiment.getRootFolder().writeFolder(
			rootPath,
			new ManifestFolderDescription(), 
			new ExperimentFileFolder()
		);	
	}
	
	@Override
	public BoundOutputManager bindRootFolder( String expIdentifier, ManifestRecorder writeOperationRecorder, FilePathPrefixerParams context ) throws BindFailedException {

		try {
			FilePathPrefix prefix = filePathPrefixer.rootFolderPrefix( expIdentifier, context );
			
			writeOperationRecorder.init(prefix.getFolderPath());
			return new BoundOutputManager(
				this,
				prefix,
				getOutputWriteSettings(),
				writeOperationRecorder.getRootFolder(),
				lazyDirectoryFactory(),
				Optional.empty()
			);

		} catch (FilePathPrefixerException e) {
			throw new BindFailedException(e);
		}
	}
	
	private LazyDirectoryFactory lazyDirectoryFactory() {
		return new LazyDirectoryFactory(silentlyDeleteExisting);
	}
}
