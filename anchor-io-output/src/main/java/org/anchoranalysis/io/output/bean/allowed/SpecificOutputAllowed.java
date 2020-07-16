/* (C)2020 */
package org.anchoranalysis.io.output.bean.allowed;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;

public class SpecificOutputAllowed extends OutputAllowed {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StringSet outputsAllowed;
    // END BEAN PROPERTIES

    @Override
    public boolean isOutputAllowed(String outputName) {
        return outputsAllowed.contains(outputName);
    }
}
