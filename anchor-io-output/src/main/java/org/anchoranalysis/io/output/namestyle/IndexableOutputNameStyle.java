/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.output.namestyle;

import java.util.Optional;

/**
 * An output-name with an index somehow appended or prepended in a particular style.
 *
 * <p>The no-arguments constructor exists only for deserialization.
 *
 * @author Owen Feehan
 */
public abstract class IndexableOutputNameStyle extends OutputNameStyle {

    private static final long serialVersionUID = 01L;

    /** Empty constructor, as needed for deserialization. */
    protected IndexableOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    /**
     * Creates with an output-name.
     *
     * @param outputName the output-name.
     */
    protected IndexableOutputNameStyle(String outputName) {
        super(outputName);
    }

    /**
     * Copy constructor.
     *
     * @param source source.
     */
    protected IndexableOutputNameStyle(IndexableOutputNameStyle source) {
        super(source.getOutputName());
    }

    /**
     * Like {@link #filenameWithoutExtension()} but incorporates an <i>integer</i> index.
     *
     * @param index the index, unique within a set of filenames being outputted.
     * @return the filename (without an extension, including without the period before the
     *     extension).
     */
    public String filenameWithoutExtension(int index) {
        return filenameWithoutExtension(Integer.toString(index));
    }

    /**
     * Like {@link #filenameWithoutExtension()} but incorporates an <i>string</i> index.
     *
     * @param index the index
     * @return the filename (without an extension, including without the period before the
     *     extension).
     */
    public String filenameWithoutExtension(String index) {
        return filenameFromOutputFormatString(outputFormatString(), index);
    }

    @Override
    public abstract IndexableOutputNameStyle duplicate();

    @Override
    public Optional<String> filenameWithoutExtension() {
        throw new UnsupportedOperationException("an index is required to determine a filename");
    }

    /**
     * Constructs a file name from the output format string and an index.
     *
     * @param index the index, unique within a set of filenames being outputted.
     * @param outputFormatString a format-string as recognized by {@link String#format} that expects
     *     a single string element (what will be populated by {@code index}) to form the name.
     * @return the file-name as a string.
     */
    protected abstract String filenameFromOutputFormatString(
            String outputFormatString, String index);

    /**
     * A format-string as recognized by {@link String#format} that expects a single string element.
     *
     * @return the format-string.
     */
    protected abstract String outputFormatString();
}
