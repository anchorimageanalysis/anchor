/* (C)2020 */
package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.ObjectMaskMerger;

/**
 * A pair of objects (first and second) and maybe a merged version of both
 *
 * <p>Note that left and right simply identify two parts of the pair (tuple). It has no physical
 * meaning related to where the objects are located in the scene..
 *
 * <p>If a merged version doesn't exist, it is created and cached on demand.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputPairObjects extends FeatureInputNRG {

    private ObjectMask first;
    private ObjectMask second;

    private Optional<ObjectMask> merged = Optional.empty();

    public FeatureInputPairObjects(ObjectMask first, ObjectMask second) {
        this(first, second, Optional.empty());
    }

    public FeatureInputPairObjects(
            ObjectMask first, ObjectMask second, NRGStackWithParams nrgStack) {
        this(first, second, Optional.of(nrgStack));
    }

    public FeatureInputPairObjects(
            ObjectMask first, ObjectMask second, Optional<NRGStackWithParams> nrgStack) {
        this(first, second, nrgStack, Optional.empty());
    }

    public FeatureInputPairObjects(
            ObjectMask first,
            ObjectMask second,
            Optional<NRGStackWithParams> nrgStack,
            Optional<ObjectMask> merged) {
        super(nrgStack);
        this.first = first;
        this.second = second;
        this.merged = merged;
    }

    protected FeatureInputPairObjects(FeatureInputPairObjects src) {
        super(src.getNrgStackOptional());
        this.first = src.first;
        this.second = src.second;
        this.merged = src.merged;
    }

    public ObjectMask getFirst() {
        return first;
    }

    public ObjectMask getSecond() {
        return second;
    }

    /**
     * Returns a merged version of the two-objects available (or NULL if not available)
     *
     * @return the merged object-mask
     */
    public ObjectMask getMerged() {
        if (!merged.isPresent()) {
            merged = Optional.of(ObjectMaskMerger.merge(first, second));
        }
        return merged.get();
    }

    public Optional<ObjectMask> getMergedOptional() {
        return merged;
    }

    @Override
    public String toString() {
        return String.format("%s vs %s", first.centerOfGravity(), second.centerOfGravity());
    }
}
