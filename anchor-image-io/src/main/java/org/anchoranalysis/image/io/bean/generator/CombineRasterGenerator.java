package org.anchoranalysis.image.io.bean.generator;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.arrangeraster.RasterArranger;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryByte;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

// Combines a number of generators of Raster images by tiling their outputs together
// The order of generators is left to right, then top to bottom
public class CombineRasterGenerator<IterationType> extends AnchorBean<CombineRasterGenerator<IterationType>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4553520179688231697L;

	// START BEAN PROPERTIES
	@BeanField
	private ArrangeRasterBean arrangeRaster;
	
	// A list of all generators to be tiled (left to right, then top to bottom)
	@BeanField
	private List<IterableObjectGenerator<IterationType,Stack>> generatorList =	new ArrayList<>();
	// END BEAN PROPERTIES
	
	
	private class Generator extends RasterGenerator implements IterableObjectGenerator<IterationType, Stack> {

		@Override
		public Stack generate() throws OutputWriteFailedException {
			
			List<RGBStack> generated = generateAll();
			
			RasterArranger rasterArranger = new RasterArranger();
			
			try {
				rasterArranger.init(arrangeRaster,generated);
			} catch (InitException e) {
				throw new OutputWriteFailedException(e);
			}
					
			// We get an ImgStack<ImgChnl> for each generator
			//
			// Then we tile them
			//
			// We assume iterable generators always produce images of the same size
			//   and base our measurements on the first call to generate
			return rasterArranger.createStack(generated,new ChnlFactoryByte()).asStack();
		}

		@Override
		public String getFileExtension(OutputWriteSettings outputWriteSettings) {
			// TODO rethink
			return generatorList.get(0).getGenerator().getFileExtension(outputWriteSettings);
		}

		@Override
		public ManifestDescription createManifestDescription() {
			return new ManifestDescription("raster", "combinedNRG");
		}

		@Override
		public void start() throws OutputWriteFailedException {
			
			for (IterableObjectGenerator<IterationType,Stack> generator : generatorList) {
				generator.start();
			}
		}

		@Override
		public void end() throws OutputWriteFailedException {
			
			for (IterableObjectGenerator<IterationType,Stack> generator : generatorList) {
				generator.end();
			}
		}

		@Override
		public IterationType getIterableElement() {
			
			return generatorList.get(0).getIterableElement();
		}

		@Override
		public void setIterableElement(IterationType element) throws SetOperationFailedException {

			for (IterableObjectGenerator<IterationType,Stack> generator : generatorList) {
				generator.setIterableElement(element);
			}
			
		}

		@Override
		public ObjectGenerator<Stack> getGenerator() {
			return this;
		}


		@Override
		public boolean isRGB() {
			return true;
		}
		
	}
	
	
		
	
	


	public CombineRasterGenerator() {
		super();
	}

	

	public void add( IterableObjectGenerator<IterationType,Stack> generator ) {
		generatorList.add( generator );
	}
	
	
	


	private List<RGBStack>  generateAll() throws OutputWriteFailedException {
		
		try {
			List<RGBStack>  listOut = new ArrayList<>();
			for (IterableObjectGenerator<IterationType,Stack> generator : generatorList) {
				Stack stackOut = generator.getGenerator().generate();
				listOut.add( new RGBStack(stackOut) );
			}
			return listOut;
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
		
	}

	public IterableObjectGenerator<IterationType, Stack> createGenerator() {
		return new Generator();
	}

	@Override
	public String getBeanDscr() {
		return getBeanName();
	}
	
	public ArrangeRasterBean getArrangeRaster() {
		return arrangeRaster;
	}

	public void setArrangeRaster(ArrangeRasterBean arrangeRaster) {
		this.arrangeRaster = arrangeRaster;
	}
}
