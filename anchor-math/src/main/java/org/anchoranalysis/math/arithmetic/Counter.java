/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.math.arithmetic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A simple counter that increments.
 *
 * <p>This is useful when an object on the heap is required to track counting.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
public class Counter {

    /** The count, as per current state. */
    @Getter private int count = 0;

    /** Increases the counter by one. */
    public void increment() {
        count++;
    }

    /**
     * Increases the counter by one, returning the previous value.
     *
     * @return the value before the increment.
     */
    public int incrementReturn() {
        int previous = count;
        count++;
        return previous;
    }

    /**
     * Increases the counter by a specific value.
     *
     * @param value the value to increase the counter by.
     */
    public void incrementBy(int value) {
        count += value;
    }

    /** Decreases the counter by one. */
    public void decrement() {
        count--;
    }
}
