/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.nrg.saved;

import java.util.Set;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteria;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.RandomCollectionWithAddCriteria;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdatableMarkSet;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.graph.EdgeTypeWithVertices;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

public class NRGSavedPairs implements UpdatableMarkSet {

    // Pairwise total
    private double nrgTotal;

    private RandomCollectionWithAddCriteria<NRGPair> pairCollection;

    // START CONSTRUCTORS
    public NRGSavedPairs(AddCriteria<NRGPair> addCriteria) {
        this.pairCollection = new RandomCollectionWithAddCriteria<>(NRGPair.class);
        this.pairCollection.setAddCriteria(addCriteria);
    }

    public NRGSavedPairs shallowCopy() {
        NRGSavedPairs out = new NRGSavedPairs(this.pairCollection.getAddCriteria());
        out.pairCollection = this.pairCollection.shallowCopy();
        out.nrgTotal = this.nrgTotal;
        return out;
    }

    public NRGSavedPairs deepCopy() {
        NRGSavedPairs out = new NRGSavedPairs(this.pairCollection.getAddCriteria());
        out.pairCollection = this.pairCollection.deepCopy();
        out.nrgTotal = this.nrgTotal;
        return out;
    }
    // END CONSTRUCTORS

    // START GETTERS AND SETTERS
    public double getNRGTotal() {
        return nrgTotal;
    }
    // END GETTERS AND SETTERS

    @Override
    public void initUpdatableMarkSet(
            MemoForIndex pxlMarkMemoList,
            NRGStackWithParams stack,
            Logger logger,
            SharedFeatureMulti sharedFeatures)
            throws InitException {

        this.pairCollection.initUpdatableMarkSet(pxlMarkMemoList, stack, logger, sharedFeatures);
        calcTotalFresh();
    }

    // Calculates energy for all pairwise interactions freshly
    private void calcTotalFresh() {
        nrgTotal = 0;
        for (NRGPair nrgPair : pairCollection.createPairsUnique()) {
            nrgTotal += nrgPair.getNRG().getTotal();
        }
        assert !Double.isNaN(nrgTotal);
    }

    private double totalNRGForMark(Mark mark) {

        double total = 0;
        for (EdgeTypeWithVertices<Mark, NRGPair> pair : this.pairCollection.getPairsFor(mark)) {
            total += pair.getEdge().getNRG().getTotal();
        }
        assert !Double.isNaN(total);

        return total;
    }

    @Override
    public void add(MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {

        this.pairCollection.add(pxlMarkMemoList, newMark);
        this.nrgTotal += totalNRGForMark(newMark.getMark());
        assert !Double.isNaN(nrgTotal);
    }

    @Override
    public void rmv(MemoForIndex marksExisting, VoxelizedMarkMemo mark)
            throws UpdateMarkSetException {

        // We calculate it's individual contribution
        this.nrgTotal -= totalNRGForMark(mark.getMark());
        this.pairCollection.rmv(marksExisting, mark);
        assert !Double.isNaN(nrgTotal);
    }

    // exchanges one mark with another
    @Override
    public void exchange(
            MemoForIndex pxlMarkMemoList,
            VoxelizedMarkMemo oldMark,
            int indexOldMark,
            VoxelizedMarkMemo newMark)
            throws UpdateMarkSetException {

        // We get a total for how the old mark interacts with the other marks
        double oldPairTotal = totalNRGForMark(oldMark.getMark());

        this.pairCollection.exchange(pxlMarkMemoList, oldMark, indexOldMark, newMark);

        double newPairTotal = totalNRGForMark(newMark.getMark());
        this.nrgTotal = this.nrgTotal - oldPairTotal + newPairTotal;
    }

    // Does the pairs hash only contains items contained in a particular configuration
    public boolean isCfgSpan(Cfg cfg) {
        return pairCollection.isCfgSpan(cfg);
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(newLine);

        // We list all the non-null energy components
        for (NRGPair di : createPairsUnique()) {
            s.append(
                    String.format(
                            "%2d--%2d\tnrg=%e%n",
                            di.getPair().getSource().getId(),
                            di.getPair().getDestination().getId(),
                            di.getNRG().getTotal()));
        }

        s.append("}" + newLine);

        return s.toString();
    }

    public void assertValid() {
        assert !Double.isNaN(nrgTotal);
    }

    public Set<NRGPair> createPairsUnique() {
        return pairCollection.createPairsUnique();
    }
}
