/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.bean;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorIndexModulo;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.scheme.ColorScheme;
import org.anchoranalysis.io.bean.color.scheme.HSB;
import org.anchoranalysis.io.bean.color.scheme.Shuffle;

/**
 * Settings for how to write output, including default writers.
 *
 * <p>It is very important that {@link #init} is run before using the bean. This normally occurs
 * from checkMisconfigured() that is called automatically from the bean-loading framework
 *
 * <p>However, if the bean is not loaded through this mechanism, please call {@link #init}
 * explicitly before usage
 */
public class OutputWriteSettings extends AnchorBean<OutputWriteSettings> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter
    private ColorScheme defaultColorSetGenerator = new Shuffle(new HSB());

    /**
     * Specifies a writer bean instance for a particular type of writer (identified by the writer
     * bean class)
     */
    @BeanField @OptionalBean @Getter @Setter private List<NamedBean<?>> writers;
    // END BEAN PROPERTIES

    private static final String HTML_EXTENSION = "html";
    private static final String XML_EXTENSION = "xml";
    private static final String TEXT_EXTENSION = "txt";
    private static final String SERIALIZED_EXTENSION = "ser";

    // Contains instances for each writer
    private BeanInstanceMap writerInstances;

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        init(defaultInstances);
    }

    public void init(BeanInstanceMap defaultInstances) throws BeanMisconfiguredException {

        // A convenient place to set up our writerInstances, as it is executed once, before
        // getDefaultWriter()
        //  is called, and the defaults are available, and an error message can potentially be
        // thrown.

        writerInstances = new BeanInstanceMap();

        // First load in the defaults
        writerInstances.addFrom(defaultInstances);

        // Then load in the explicitly-specified writers (overriding any existing entries)
        if (writers != null) {
            writerInstances.addFrom(writers);
        }
    }

    public boolean hasBeenInit() {
        return (writerInstances != null);
    }

    /**
     * Gets a writer-instance for a particular {@code writerParentClass}.
     *
     * <p>1. First, it looks for a match among the bean-field 'writers' 2. If no match is found,
     * then it looks among the general default-instances 3. If no match is found, then it returns
     * null.
     *
     * <p>When a writer is returned, it will always inherits from type c.
     *
     * @param writerParentClass the class identifying which type of writer is sought
     * @return a matching writer, or null.
     */
    public Object getWriterInstance(Class<?> writerParentClass) {
        assert (writerInstances != null);

        // We look for the default instance, corresponding to the particular class
        return writerInstances.get(writerParentClass);
    }

    public ColorIndex defaultColorIndexFor(int numberColors) throws OperationFailedException {
        return new ColorIndexModulo(getDefaultColorSetGenerator().createList(numberColors));
    }

    public String getExtensionHTML() {
        return HTML_EXTENSION;
    }

    public String getExtensionSerialized() {
        return SERIALIZED_EXTENSION;
    }

    public String getExtensionXML() {
        return XML_EXTENSION;
    }

    public String getExtensionText() {
        return TEXT_EXTENSION;
    }
}
