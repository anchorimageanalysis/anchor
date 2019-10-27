package org.anchoranalysis.io.manifest;

/*
 * #%L
 * anchor-io
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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.ManifestDeserializer;

public class ManifestRecorderFile extends CachedOperation<ManifestRecorder> {

	private File file;
	private ManifestDeserializer manifestDeserializer;

	//private static Log log = LogFactory.getLog(ManifestRecorderFile.class);
	
	public ManifestRecorderFile(File file, ManifestDeserializer manifestDeserializer) {
		super();
		this.file = file;
		this.manifestDeserializer = manifestDeserializer;
	}

	@Override
	protected ManifestRecorder execute() throws ExecuteException {

		try {
			//StopWatch sw = new StopWatch();
			//sw.start();
			
			if (!file.exists()) {
				throw new OperationFailedException( String.format("File %s cannot be found",file.getPath()) );
			}
			
			//log.info( String.format("File %s deserialization started", file.getPath() ) );
			ManifestRecorder manifestRecorder = manifestDeserializer.deserializeManifest(file);
			//log.info( String.format("File %s deserialization ended (%dms)", file.getPath(), ((int) (sw.getTime())) ) );
			return manifestRecorder;
		} catch (DeserializationFailedException | OperationFailedException e) {
			throw new ExecuteException(e);
		}
	}
	
	public Path getRootPath() {
		// Returns the path of the root of the manifest file (or what it will become)
		return Paths.get(file.getParent());
	}
}
