package org.anchoranalysis.image.bean.spatial.arrange.align;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Grow}.
 *
 * @author Owen Feehan
 */
class TestGrow {

    @Test
    void testWithoutPreserve() throws OperationFailedException {
        BoxAlignerTester.doTest(
                new Grow(false),
                BoxAlignerTester.LARGER.cornerMin(),
                BoxAlignerTester.LARGER.extent());
    }

    @Test
    void testWithPreserve() throws OperationFailedException {
        BoxAlignerTester.doTest(
                new Grow(true), new Point3i(15, 25, 15), BoxAlignerTester.LARGER.extent());
    }
}
