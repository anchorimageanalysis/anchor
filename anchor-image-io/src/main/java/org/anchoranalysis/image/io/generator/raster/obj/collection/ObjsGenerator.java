package org.anchoranalysis.image.io.generator.raster.obj.collection;

import java.util.Optional;

/*-
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;

import lombok.AllArgsConstructor;


/**
 * Base class for generators that accept a set of objects as input
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public abstract class ObjsGenerator extends RasterGenerator implements IterableGenerator<ObjectCollection> {
	
	private ObjectCollection objects;
	private final ImageDimensions dim;
	
	public ObjsGenerator(ImageDimensions dim) {
		this.dim = dim;
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", "maskCollection")
		);
	}

	@Override
	public boolean isRGB() {
		return false;
	}

	@Override
	public ObjectCollection getIterableElement() {
		return objects;
	}

	@Override
	public void setIterableElement(ObjectCollection element)
			throws SetOperationFailedException {
		this.objects = element;
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	public ImageDimensions getDimensions() {
		return dim;
	}

	protected ObjectCollection getObjs() {
		return objects;
	}
}
