package org.anchoranalysis.io.generator;

import org.anchoranalysis.core.bridge.BridgeElementException;

/*-
 * #%L
 * anchor-io-generator
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

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.filepath.prefixer.FilePathCreator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// Allows us to call an IterableGenerator<Cfg> as if it was an IterableGenerator<CfgNRG> 
public class IterableGeneratorBridge<SourceType,DestinationType> extends Generator implements IterableGenerator<SourceType> {

	private SourceType element;
	
	private IterableGenerator<DestinationType> delegate;
	
	private IObjectBridge<SourceType, DestinationType> bridge;
	
	public IterableGeneratorBridge(IterableGenerator<DestinationType> delegate, IObjectBridge<SourceType, DestinationType> bridge) {
		super();
		this.delegate = delegate;
		this.bridge = bridge;
	}

	@Override
	public SourceType getIterableElement() {
		return this.element;
	}

	@Override
	public void setIterableElement(SourceType element) throws SetOperationFailedException {
		this.element = element;
		try {
			delegate.setIterableElement( bridge.bridgeElement(element) );
		} catch (BridgeElementException e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public Generator getGenerator() {
		return delegate.getGenerator();
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}


	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public void write(OutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		delegate.getGenerator().write(outputNameStyle, filePathGnrtr, writeOperationRecorder, outputManager);
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		return delegate.getGenerator().write(outputNameStyle, filePathGnrtr, writeOperationRecorder, index, outputManager );
	}

	@Override
	public FileType[] getFileTypes(OutputWriteSettings outputWriteSettings) {
		return delegate.getGenerator().getFileTypes(outputWriteSettings);
	}
}
