/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.mpp.init;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.initialization.FeatureRelatedInitialization;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.bean.nonbean.init.PopulateStoreFromDefine;
import org.anchoranalysis.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.mpp.bean.proposer.MarkCollectionProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.mpp.bean.provider.MarkCollectionProvider;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.pair.MarkPair;
import org.anchoranalysis.mpp.pair.RandomCollection;

@Accessors(fluent = true)
public class MarksInitialization implements BeanInitialization {

    // START: Initialization
    @Getter private final ImageInitialization image;
    @Getter private final PointsInitialization points;
    // END: Initialization

    // START: Stores
    @Getter private final NamedProviderStore<MarkCollection> marks;
    @Getter private final NamedProviderStore<MarkCollectionProposer> markCollectionProposers;
    @Getter private final NamedProviderStore<MarkBounds> markBounds;
    @Getter private final NamedProviderStore<MarkProposer> markProposers;
    @Getter private final NamedProviderStore<MarkMergeProposer> markMergeProposers;
    @Getter private final NamedProviderStore<MarkSplitProposer> markSplitProposers;
    @Getter private final NamedProviderStore<RandomCollection<MarkPair<Mark>>> markPairs;
    // END: Stores

    public MarksInitialization(ImageInitialization image) {
        SharedObjects sharedObjects = image.sharedObjects();
        this.image = image;
        this.points = PointsInitialization.create(image, sharedObjects);

        marks = sharedObjects.getOrCreate(MarkCollection.class);
        markCollectionProposers = sharedObjects.getOrCreate(MarkCollectionProposer.class);
        markBounds = sharedObjects.getOrCreate(MarkBounds.class);
        markProposers = sharedObjects.getOrCreate(MarkProposer.class);
        markMergeProposers = sharedObjects.getOrCreate(MarkMergeProposer.class);
        markSplitProposers = sharedObjects.getOrCreate(MarkSplitProposer.class);
        markPairs = sharedObjects.getOrCreate(RandomCollection.class);
    }

    public FeatureRelatedInitialization feature() {
        return image.featuresInitialization();
    }

    public DictionaryInitialization dictionary() {
        return image.dictionaryInitialization();
    }

    public void populate(BeanInitializer<?> initializer, Define define, Logger logger)
            throws OperationFailedException {

        PopulateStoreFromDefine<MarksInitialization> populater =
                new PopulateStoreFromDefine<>(define, initializer, logger);
        populater.copyWithoutInitialize(MarkBounds.class, markBounds);
        populater.copyInitialize(MarkProposer.class, markProposers);
        populater.copyInitialize(MarkCollectionProposer.class, markCollectionProposers);
        populater.copyInitialize(MarkSplitProposer.class, markSplitProposers);
        populater.copyInitialize(MarkMergeProposer.class, markMergeProposers);
        populater.copyWithoutInitialize(RandomCollection.class, markPairs);

        populater.copyProviderInitialize(MarkCollectionProvider.class, marks());

        image.populate(initializer, define, logger);
        points.populate(initializer, define, logger);
    }
}
