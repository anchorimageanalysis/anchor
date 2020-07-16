/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

class ResultsVectorBuilder {

    private Optional<ErrorReporter> errorReporter;
    private ResultsVector out;
    private int cnt;

    /**
     * Constructor
     *
     * @param size
     * @param errorReporter if-defined feature errors are logged here and not thrown as exceptions.
     *     if not-defined, exceptions are thrown
     */
    public ResultsVectorBuilder(int size, Optional<ErrorReporter> errorReporter) {
        super();
        this.errorReporter = errorReporter;
        this.out = new ResultsVector(size);
        this.cnt = 0;
    }

    /** Calculates and inserts a derived obj-mask params from a merged. */
    public void calcAndInsert(
            FeatureInputPairObjects inputPair,
            Function<FeatureInputPairObjects, ObjectMask> extractObj,
            FeatureCalculatorMulti<FeatureInputSingleObject> calc)
            throws FeatureCalcException {
        FeatureInputSingleObject inputSingle =
                new FeatureInputSingleObject(extractObj.apply(inputPair));
        calcAndInsert(inputSingle, calc);
    }

    /**
     * Calculates the parameters belong to a particular session and inserts into a ResultsVector
     *
     * @param input
     * @param calc
     * @throws FeatureCalcException
     */
    public <T extends FeatureInput> void calcAndInsert(T input, FeatureCalculatorMulti<T> calc)
            throws FeatureCalcException {
        ResultsVector rvImage =
                errorReporter.isPresent()
                        ? calc.calcSuppressErrors(input, errorReporter.get())
                        : calc.calc(input);
        out.set(cnt, rvImage);
        cnt += rvImage.length();
    }

    public ResultsVector getResultsVector() {
        return out;
    }
}
