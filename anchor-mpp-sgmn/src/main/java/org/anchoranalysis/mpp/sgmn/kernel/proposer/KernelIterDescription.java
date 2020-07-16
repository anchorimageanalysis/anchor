/*-
 * #%L
 * anchor-mpp-sgmn
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
/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import java.io.Serializable;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.index.IIndexGetter;

public class KernelIterDescription implements Serializable, IIndexGetter {

    /** */
    private static final long serialVersionUID = -5135255409310941727L;

    @Getter private final int id;

    @Getter private final String description;

    @Getter private final boolean accepted;

    @Getter private final boolean proposed;

    @Getter private final int[] changedMarkIDArr;

    @Getter private final transient ProposerFailureDescription noProposalReason;

    @Getter private final long executionTime;

    private int iter;

    public KernelIterDescription(
            KernelWithID<?> kernelWithID,
            boolean accepted,
            boolean proposed,
            int[] changedMarkIDArr,
            long executionTime,
            int iter,
            ProposerFailureDescription noProposalReason) {

        this.id = kernelWithID.getID();
        this.description = kernelWithID.getDescription();
        this.accepted = accepted;
        this.proposed = proposed;
        this.changedMarkIDArr = changedMarkIDArr;
        this.executionTime = executionTime;
        this.iter = iter;
        this.noProposalReason = noProposalReason;
    }

    @Override
    public int getIndex() {
        return iter;
    }
}
