/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.init;

import org.anchoranalysis.anchor.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgProvider;
import org.anchoranalysis.anchor.mpp.bean.proposer.CfgProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pair.PairCollection;
import org.anchoranalysis.anchor.mpp.probmap.ProbMap;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;

// A wrapper around SharedObjects which types certain MPP entities
public class MPPInitParams implements BeanInitParams {

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

    public MPPInitParams(ImageInitParams soImage, SharedObjects so) {
        super();
        this.soImage = soImage;
        this.soPoints = PointsInitParams.create(soImage, so);

        storeCfg = so.getOrCreate(Cfg.class);
        storeCfgProposer = so.getOrCreate(CfgProposer.class);
        storeMarkBounds = so.getOrCreate(MarkBounds.class);
        storeMarkProposer = so.getOrCreate(MarkProposer.class);
        storeMarkMergeProposer = so.getOrCreate(MarkMergeProposer.class);
        storeMarkSplitProposer = so.getOrCreate(MarkSplitProposer.class);
        storeProbMap = so.getOrCreate(ProbMap.class);
        storePairCollection = so.getOrCreate(PairCollection.class);
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
     * Backwards compatible for when we only had a single bounds
     *
     * <p>This is stored in all our named-definition files, from now on as "primary"
     *
     * @return
     * @throws NamedProviderGetException
     */
    public MarkBounds getMarkBounds() throws NamedProviderGetException {
        return getMarkBoundsSet().getException("primary");
    }

    public void populate(PropertyInitializer<?> pi, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<MPPInitParams> populater =
                new PopulateStoreFromDefine<>(define, pi, logger);
        populater.copyWithoutInit(MarkBounds.class, getMarkBoundsSet());
        populater.copyInit(MarkProposer.class, getMarkProposerSet());
        populater.copyInit(CfgProposer.class, getCfgProposerSet());
        populater.copyInit(MarkSplitProposer.class, getMarkSplitProposerSet());
        populater.copyInit(MarkMergeProposer.class, getMarkMergeProposerSet());
        populater.copyWithoutInit(PairCollection.class, getSimplePairCollection());

        populater.copyProvider(CfgProvider.class, getCfgCollection());

        soImage.populate(pi, define, logger);
        soPoints.populate(pi, define, logger);
    }
}
