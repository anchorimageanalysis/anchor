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

package org.anchoranalysis.mpp.segment.bean.optimization.feedback;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequence;
import org.anchoranalysis.io.generator.sequence.OutputSequenceDirectory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceNonIncrementalChecked;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackEndParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiver;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiverException;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;

public abstract class PeriodicSubfolderReporter<T>
        extends ReporterInterval<VoxelizedMarksWithEnergy> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String outputName;
    // END BEAN PROPER

    private OutputSequenceNonIncrementalChecked<T> sequenceWriter;

    private InputOutputContext parentContext;

    /** Handles period-receiver updates */
    private class AddToWriter implements PeriodReceiver<VoxelizedMarksWithEnergy> {

        private Optional<T> runningElement;

        @Override
        public void periodStart(Reporting<VoxelizedMarksWithEnergy> reporting)
                throws PeriodReceiverException {

            try {
                Optional<T> element = generateIterableElement(reporting);

                if (element.isPresent()) {
                    runningElement = element;
                } else {
                    if (!runningElement.isPresent()) {
                        throw new PeriodReceiverException(
                                "The first iterable item must be non-empty, but it is empty");
                    }
                }

                sequenceWriter.add(runningElement.get(), String.valueOf(reporting.getIter()));

            } catch (OutputWriteFailedException | ReporterException e) {
                throw new PeriodReceiverException(e);
            }
        }

        @Override
        public void periodEnd(Reporting<VoxelizedMarksWithEnergy> reporting) {
            // NOTHING TO DO
        }
    }

    // We generate an OutputName class from the outputName string
    protected int numberDigitsInOutputName() {
        return 10;
    }

    // We setup the manifest from a Generator
    protected IncrementalSequenceType init(Generator<T> generator)
            throws OutputWriteFailedException {

        IncrementalSequenceType sequenceType = new IncrementalSequenceType(0, getAggInterval());
        
        OutputSequenceDirectory sequenceDirectory = new OutputSequenceDirectory(
            outputName,
            numberDigitsInOutputName(),
            true,
            Optional.empty()
        );
        
        this.sequenceWriter = OutputSequence.createNonIncrementalChecked(sequenceDirectory, generator, getParentContext());
        this.sequenceWriter.start(sequenceType);

        return sequenceType;
    }

    @Override
    public void reportBegin(FeedbackBeginParameters<VoxelizedMarksWithEnergy> optInit)
            throws ReporterException {

        this.parentContext = optInit.getInitContext().getInputOutputContext();

        // Let's only do this if the output is allowed
        if (parentContext.getOutputter().outputsEnabled().isOutputEnabled(outputName)) {
            optInit.getPeriodTriggerBank().obtain(getAggInterval(), new AddToWriter());
        }
    }

    @Override
    public void reportEnd(FeedbackEndParameters<VoxelizedMarksWithEnergy> params)
            throws ReporterException {

        try {
            this.sequenceWriter.end();
        } catch (OutputWriteFailedException e) {
            throw new ReporterException(e);
        }
    }
    
    protected abstract Optional<T> generateIterableElement(
            Reporting<VoxelizedMarksWithEnergy> reporting) throws ReporterException;

    protected InputOutputContext getParentContext() {
        return parentContext;
    }
}
