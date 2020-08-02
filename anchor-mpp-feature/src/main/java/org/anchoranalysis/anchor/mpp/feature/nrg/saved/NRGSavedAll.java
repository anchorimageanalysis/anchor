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

import java.io.Serializable;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.feature.calc.NamedFeatureCalculationException;
import org.anchoranalysis.feature.nrg.NRGStack;

// Saves particular features for all items
public class NRGSavedAll implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private double nrgTotal;

    public void calc(
            MemoCollection pxlMarkMemoList,
            NRGSchemeWithSharedFeatures nrgScheme,
            NRGStack nrgRaster)
            throws NamedFeatureCalculationException {
        nrgTotal = nrgScheme.calcElemAllTotal(pxlMarkMemoList, nrgRaster).getTotal();
    }

    public double getNRGTotal() {
        return nrgTotal;
    }

    public NRGSavedAll shallowCopy() {
        NRGSavedAll out = new NRGSavedAll();
        out.nrgTotal = nrgTotal;
        return out;
    }

    public NRGSavedAll deepCopy() {
        return shallowCopy();
    }
}
