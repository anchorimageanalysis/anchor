/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import java.util.function.Consumer;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Likes a SequentialSession but automatically changes parameters before calculation
 *
 * @author Owen Feehan
 * @param <T> feature-input-type
 */
public class FeatureCalculatorMultiChangeInput<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private FeatureCalculatorMulti<T> calculator;
    private Consumer<T> funcToApplyChange;

    /**
     * Constructor
     *
     * @param calculator delegate which is called after an input is changed
     * @param funcToApplyChange a function that is applied to change the input before being passed
     *     to the delegate
     */
    public FeatureCalculatorMultiChangeInput(
            FeatureCalculatorMulti<T> calculator, Consumer<T> funcToApplyChange) {
        this.calculator = calculator;
        this.funcToApplyChange = funcToApplyChange;
    }

    public ResultsVector calc(T input) throws FeatureCalcException {
        funcToApplyChange.accept(input);
        return calculator.calc(input);
    }

    @Override
    public ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException {
        funcToApplyChange.accept(input);
        return calculator.calc(input, featuresSubset);
    }

    public ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter) {
        funcToApplyChange.accept(input);
        return calculator.calcSuppressErrors(input, errorReporter);
    }

    @Override
    public int sizeFeatures() {
        return calculator.sizeFeatures();
    }
}
