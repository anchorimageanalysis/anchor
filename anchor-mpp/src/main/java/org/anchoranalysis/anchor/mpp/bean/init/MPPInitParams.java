package org.anchoranalysis.anchor.mpp.bean.init;

import java.nio.file.Path;

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

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgProvider;
import org.anchoranalysis.anchor.mpp.bean.proposer.CfgProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.anchor.mpp.bean.provider.ProbMapProvider;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pair.PairCollection;
import org.anchoranalysis.anchor.mpp.probmap.ProbMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.params.IdentityBridgeInit;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.store.BeanStoreAdder;
import org.anchoranalysis.core.bridge.IdentityBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.provider.ProviderBridge;

// A wrapper around SharedObjects which types certain MPP entities
public class MPPInitParams extends BeanInitParams {

	// START: InitParams
	private ImageInitParams soImage;
	private PointsInitParams soPoints;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<Cfg> storeCfg;
	private NamedProviderStore<CfgProposer> storeCfgProposer;
	private NamedProviderStore<MarkBounds> storeMarkBounds;
	private NamedProviderStore<MarkProposer> storeMarkProposer;
	private NamedProviderStore<MarkMergeProposer> storeMarkMergeProposer;
	private NamedProviderStore<MarkSplitProposer> storeMarkSplitProposer;
	private NamedProviderStore<ProbMap> storeProbMap;
	private NamedProviderStore<PairCollection<Pair<Mark>>> storePairCollection;
	// END: Stores

	private MPPInitParams(ImageInitParams soImage, SharedObjects so) {
		super();
		this.soImage = soImage;
		this.soPoints = PointsInitParams.create( soImage, so );
		
		storeCfg = so.getOrCreate(Cfg.class);
		storeCfgProposer = so.getOrCreate(CfgProposer.class);
		storeMarkBounds = so.getOrCreate(MarkBounds.class);
		storeMarkProposer = so.getOrCreate(MarkProposer.class);
		storeMarkMergeProposer = so.getOrCreate(MarkMergeProposer.class);
		storeMarkSplitProposer = so.getOrCreate(MarkSplitProposer.class);
		storeProbMap = so.getOrCreate(ProbMap.class);
		storePairCollection = so.getOrCreate(PairCollection.class);
	}
	
	public static MPPInitParams create( ImageInitParams soImage, SharedObjects so ) {
		return new MPPInitParams( soImage, so);
	}
	
	public static MPPInitParams create( SharedObjects so, Path modelDir ) {
		return create( ImageInitParams.create(so, modelDir), so );
	}
	
	public static MPPInitParams create(
		SharedObjects so,
		Define namedDefinitions,
		GeneralInitParams paramsGeneral
	) throws CreateException {
		ImageInitParams soImage = ImageInitParams.create( so, paramsGeneral.getRe(), paramsGeneral.getModelDir() );
		MPPInitParams soMPP = create( soImage, so);
		if (namedDefinitions!=null) {
			try {
				// Tries to initialize any properties (of type MPPInitParams) found in the NamedDefinitions
				PropertyInitializer<MPPInitParams> pi = MPPBean.getInitializer();
				pi.setParam(soMPP);
				soMPP.populate( pi, namedDefinitions, paramsGeneral.getLogErrorReporter() );
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
		
		return soMPP;
	}

	public ImageInitParams getImage() {
		return soImage;
	}
	
	public SharedFeaturesInitParams getFeature() {
		return soImage.getFeature();
	}
	
	public PointsInitParams getPoints() {
		return soPoints;
	}
	
	public KeyValueParamsInitParams getParams() {
		return soImage.getParams();
	}
	
	public NamedProviderStore<Cfg> getCfgCollection() {
		return storeCfg;
	}
	
	public NamedProviderStore<CfgProposer> getCfgProposerSet() {
		return storeCfgProposer;
	}
	
	public NamedProviderStore<MarkBounds> getMarkBoundsSet() {
		return storeMarkBounds;
	}
	
	public NamedProviderStore<MarkProposer> getMarkProposerSet() {
		return storeMarkProposer;
	}
	
	public NamedProviderStore<MarkMergeProposer> getMarkMergeProposerSet() {
		return storeMarkMergeProposer;
	}
	
	public NamedProviderStore<MarkSplitProposer> getMarkSplitProposerSet() {
		return storeMarkSplitProposer;
	}
	
	public NamedProviderStore<ProbMap> getProbMapSet() {
		return storeProbMap;
	}
	
	public NamedProviderStore<PairCollection<Pair<Mark>>> getSimplePairCollection() {
		return storePairCollection;
	}
	
	/**
	 * 
	 * Backwards compatible for when we only had a single bounds
	 * 
	 * This is stored in all our named-definition files, from now on as "primary"
	 * 
	 * @return
	 * @throws GetOperationFailedException
	 */
	public MarkBounds getMarkBounds() throws NamedProviderGetException {
		return getMarkBoundsSet().getException("primary");
	}

	@SuppressWarnings("unchecked")
	public void populate( PropertyInitializer<?> pi, Define namedDefinitions, LogErrorReporter logger ) throws OperationFailedException {
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(MarkBounds.class), getMarkBoundsSet(), new IdentityBridge<MarkBounds>() );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(CfgProvider.class), getCfgCollection(), new ProviderBridge<CfgProvider,Cfg,MPPInitParams>(pi, logger) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(MarkProposer.class), getMarkProposerSet(), new IdentityBridgeInit<MarkProposer,MPPInitParams>(pi,logger) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(CfgProposer.class), getCfgProposerSet(), new IdentityBridgeInit<CfgProposer,MPPInitParams>(pi,logger) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(ProbMapProvider.class), getProbMapSet(), new ProviderBridge<ProbMapProvider,ProbMap,MPPInitParams>(pi, logger) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(MarkSplitProposer.class), getMarkSplitProposerSet(), new IdentityBridgeInit<MarkSplitProposer,MPPInitParams>(pi,logger) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList(MarkMergeProposer.class), getMarkMergeProposerSet(), new IdentityBridgeInit<MarkMergeProposer,MPPInitParams>(pi,logger) );
		
		// We upcast to Object first so that we can convert the list
		List<? extends Object> pairCollectionList = namedDefinitions.getList(PairCollection.class);
		BeanStoreAdder.addPreserveName( (List<NamedBean<PairCollection<Pair<Mark>>>>) pairCollectionList, getSimplePairCollection(), new IdentityBridge<PairCollection<Pair<Mark>>>() );
		
		
		soImage.populate(pi, namedDefinitions, logger);
		soPoints.populate(pi, namedDefinitions, logger );
	}
	

	
}
