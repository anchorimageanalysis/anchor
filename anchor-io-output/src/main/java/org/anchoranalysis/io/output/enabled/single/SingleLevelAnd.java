package org.anchoranalysis.io.output.enabled.single;

/**
 * Outputs are enabled if they are contained in <b>both</b> of two {@link SingleLevelOutputEnabled}.
 *
 * @author Owen Feehan
 */
public class SingleLevelAnd extends SingleLevelBinary {

    /**
     * Creates using two {@link SingleLevelOutputEnabled}s.
     *
     * @param enabled1 the first source of output-names that are enabled
     * @param enabled2 the second source of output-names that are enabled
     */
    public SingleLevelAnd(SingleLevelOutputEnabled enabled1, SingleLevelOutputEnabled enabled2) {
        super(enabled1, enabled2);
    }

    @Override
    public boolean combine(boolean first, boolean second) {
        return first && second;
    }
}
