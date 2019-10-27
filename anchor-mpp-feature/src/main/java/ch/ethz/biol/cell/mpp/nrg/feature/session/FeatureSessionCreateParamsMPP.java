package ch.ethz.biol.cell.mpp.nrg.feature.session;

/*
 * #%L
 * anchor-mpp
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


import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.feature.session.FeatureSessionCreateParams;
import org.anchoranalysis.image.feature.session.FeatureSessionCreateParamsSubsession;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.nrg.NRGElemAllCalcParams;
import ch.ethz.biol.cell.mpp.nrg.NRGElemIndCalcParams;
import ch.ethz.biol.cell.mpp.nrg.NRGElemPairCalcParams;
import ch.ethz.biol.cell.mpp.nrg.MemoMarks;
import ch.ethz.biol.cell.mpp.nrg.feature.cfg.FeatureCfgParams;
import ch.ethz.biol.cell.mpp.nrg.feature.mark.FeatureMarkParams;

public class FeatureSessionCreateParamsMPP {

	private FeatureSessionCreateParams delegate;
	private ParamsFactory factory;
	
	public static class ParamsFactory {
			
		private FeatureSessionCreateParams delegate;
				
		public ParamsFactory(FeatureSessionCreateParams delegate, NRGStack nrgStack, KeyValueParams keyValueParams ) {
			super();
			this.delegate = delegate;
			delegate.setNrgStack(
					new NRGStackWithParams( nrgStack, keyValueParams )
			);
			assert(delegate!=null);
		}

		public FeatureCalcParams createParams() {
			return null;
		}
			
		public FeatureCalcParams createParams( PxlMarkMemo mark ) {
			NRGElemIndCalcParams params = new NRGElemIndCalcParams(
				mark,
				new NRGStackWithParams( delegate.getNrgStack(), delegate.getKeyValueParams() )
			);
			return params;
		}
				
		public FeatureCalcParams createParams( PxlMarkMemo mark1, PxlMarkMemo mark2, NRGStack nrgStack ) throws CreateException {
			return new NRGElemPairCalcParams(
				mark1,
				mark2,
				new NRGStackWithParams(nrgStack)
			);
		}
		
		public FeatureCalcParams createParams( PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim dim ) {
			return new NRGElemPairCalcParams(
				mark1,
				mark2,
				new NRGStackWithParams(dim)
			);
		}
		
		public FeatureCalcParams createParams( Mark mark ) {
			return new FeatureMarkParams(mark, delegate.determineRes(), delegate.determineNrgParams() );
		}
		
		
		public FeatureCalcParams createParams( Cfg cfg) {
			return new FeatureCfgParams(cfg, delegate.getNrgStack().getDimensions() );
		}
		
		public FeatureCalcParams createParams( MemoMarks pxlMarkMemoList ) {
			return new NRGElemAllCalcParams(pxlMarkMemoList, new NRGStackWithParams( delegate.getNrgStack(), delegate.getKeyValueParams()) );
		}
	}

	public FeatureSessionCreateParamsMPP( FeatureList listFeatures, NRGStack nrgStack, KeyValueParams keyValueParams ) {
		assert(listFeatures!=null);
		delegate = new FeatureSessionCreateParams( listFeatures );
		factory = new ParamsFactory(delegate, nrgStack, keyValueParams);
	}
	
	public ResultsVector calc( Cfg cfg ) throws FeatureCalcException {
		return delegate.calc( factory.createParams(cfg) );
	}
	
	public ResultsVector calc( MemoMarks pxlMarkMemoList ) throws FeatureCalcException {
		return delegate.calc( factory.createParams(pxlMarkMemoList) );
	}
	
	public ResultsVector calc( Mark mark ) throws FeatureCalcException {
		return delegate.calc( factory.createParams(mark ));
	}

	public ResultsVector calc( PxlMarkMemo mark ) throws FeatureCalcException {
		return delegate.calc( factory.createParams(mark) );
	}
	
	public ResultsVector calc( PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim sd ) throws FeatureCalcException {
		return delegate.calc( factory.createParams(mark1, mark2, sd) );
	}

	public ResultsVector calc() throws FeatureCalcException {
		return delegate.calc();
	}

	public List<Feature> getFeatureList() {
		return delegate.getFeatureList();
	}

	public ImageRes getRes() {
		return delegate.getRes();
	}

	public void setRes(ImageRes res) {
		delegate.setRes(res);
	}

	public ResultsVector calc(FeatureCalcParams params) throws FeatureCalcException {
		return delegate.calc(params);
	}

	public ParamsFactory getParamsFactory() {
		return factory;
	}
	
	public void start(FeatureInitParams featureInitParams, SharedFeatureSet sharedFeatureList, LogErrorReporter logger)
			throws InitException {
		delegate.start(featureInitParams, sharedFeatureList, logger);
	}

	public int numFeatures() {
		return delegate.numFeatures();
	}

	public String featureNameForIndex(int index) {
		return delegate.featureNameForIndex(index);
	}

	public void start(LogErrorReporter logger) throws InitException {
		delegate.start(logger);
	}

	public FeatureSessionCreateParamsSubsession createSubsession(FeatureCalcParams params) throws CreateException {
		return delegate.createSubsession(params);
	}
	
	public NRGStack getNrgStack() {
		return delegate.getNrgStack();
	}
}
