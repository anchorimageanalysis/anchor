/*-
 * #%L
 * anchor-beans-shared
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

package org.anchoranalysis.bean.shared.regex;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.OptionalUtilities;

@NoArgsConstructor
public class RegExSimple extends RegEx {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String matchString;
    // END BEAN PROPERTIES

    /** Lazy creation of a pattern from the regular-expression string. */
    private Pattern pattern = null;

    public RegExSimple(String matchString) {
        this.matchString = matchString;
    }

    @Override
    public Optional<String[]> match(String str) {
        createPatternIfNeeded();

        Matcher matcher = pattern.matcher(str);
        return OptionalUtilities.createFromFlag(matcher.matches(), () -> arrayFromMatcher(matcher));
    }

    @Override
    public String toString() {
        return String.format("regEx(%s)", matchString);
    }

    private void createPatternIfNeeded() {
        if (pattern == null) {
            pattern = Pattern.compile(matchString);
        }
    }

    private String[] arrayFromMatcher(Matcher matcher) {
        String[] out = new String[matcher.groupCount()];
        for (int i = 0; i < out.length; i++) {
            out[i] = matcher.group(i + 1);
        }
        return out;
    }
}
