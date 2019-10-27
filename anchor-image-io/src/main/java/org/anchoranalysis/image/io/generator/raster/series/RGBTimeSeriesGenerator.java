package org.anchoranalysis.image.io.generator.raster.series;

/*
 * #%L
 * anchor-image-io
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


import java.nio.ByteBuffer;
import java.nio.file.Path;

import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.io.generator.raster.RasterWriterUtilities;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

public class RGBTimeSeriesGenerator extends SingleFileTypeGenerator {

	private ImgStackSeries<ByteBuffer> stackSeries;
	
	public RGBTimeSeriesGenerator(ImgStackSeries<ByteBuffer> stackSeries) {
		super();
		this.stackSeries = stackSeries;
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
		
		try {
			RasterWriter rasterWriter = RasterWriterUtilities.getDefaultRasterWriter(outputWriteSettings);
			rasterWriter.writeTimeSeriesStackByte(stackSeries, filePath, true);
		} catch (RasterIOException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "rgbTimeSeries");
	}
}
