/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.output;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceUtilities;
import org.anchoranalysis.io.generator.serialized.KeyValueParamsGenerator;
import org.anchoranalysis.io.output.bound.BoundIOContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NRGStackWriter {

    public static void writeNRGStack(NRGStackWithParams nrgStack, BoundIOContext context) {
        // We write the nrg stack seperately as individual channels
        GeneratorSequenceUtilities.generateListAsSubfolder(
                "nrgStack",
                2,
                nrgStack.getNrgStack().asStack().asListChannels(),
                new ChnlGenerator("nrgStackChnl"),
                context);

        if (nrgStack.getParams() != null) {
            context.getOutputManager()
                    .getWriterCheckIfAllowed()
                    .write(
                            "nrgStackParams",
                            () ->
                                    new KeyValueParamsGenerator(
                                            nrgStack.getParams(), "nrgStackParams"));
        }
    }
}
