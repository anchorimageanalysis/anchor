/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.AnchorBean;

/**
 * A bean defining a list of {@link Integer}s.
 *
 * <p>An example:
 *
 * <pre>{@code
 *    <datasets config-class="org.anchoranalysis.bean.primitive.IntegerList" config-factory="integerList">
 * 	    <item>1</item>
 * 	    <item>-4</item>
 * 	    <item>8</item>
 *   </datasets>
 * }</pre>
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public class IntegerList extends AnchorBean<IntegerList>
        implements PrimitiveBeanCollection<Integer> {

    /** The internal list storing the elements. */
    private List<Integer> list = new ArrayList<>();

    /**
     * Constructs with one or more values added to the list.
     *
     * @param values the values
     */
    public IntegerList(Integer... values) {
        Arrays.stream(values).forEach(list::add);
    }

    @Override
    public void add(Integer value) {
        list.add(value);
    }

    @Override
    public boolean contains(Integer value) {
        return list.contains(value);
    }

    @Override
    public Iterator<Integer> iterator() {
        return list.iterator();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Duplicate the bean.
     *
     * <p>NOTE: We need to specifically-implement it as the {@link AnchorBean} functionality won't
     * work with this implementation, as it uses non-default initialization (using a
     * config-factory).
     */
    @Override
    public IntegerList duplicateBean() {
        IntegerList out = new IntegerList();
        out.list.addAll(list);
        return out;
    }
}
