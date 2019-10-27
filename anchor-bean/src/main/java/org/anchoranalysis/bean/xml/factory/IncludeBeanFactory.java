package org.anchoranalysis.bean.xml.factory;

/*
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.bean.gui.ShowDialogFiles;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

public class IncludeBeanFactory extends AnchorBeanFactory {
	
	private boolean choice = false;
	
	public IncludeBeanFactory( boolean choice ) {
		this.choice = choice;
	}
	
	
    // Creates the bean. Checks if already an instance exists.
	@Override
	@SuppressWarnings("rawtypes")
	public synchronized Object createBean(Class beanClass, BeanDeclaration decl,
        Object param) throws Exception
    {
    	XMLBeanDeclaration declXML = (XMLBeanDeclaration) decl;
    	
    	String filePathStr = (String) declXML.getBeanProperties().get("filePath");
    	Path filePath = Paths.get( filePathStr );
    	
    	Path exstPath = Paths.get( (String) param );
    	
    	Path totalPath;
    	if (choice) {
    		// We present a dialog asking the user to choose from a list of files
    		// We use the directory of filePath as the area to choose from
    		// We default to the current selection
    		Path parentDir = filePath.getParent();
    		String defaultChoice = filePath.getFileName().toString();
    		
    		parentDir = BeanPathUtilities.combine(exstPath, parentDir);
    		totalPath = ShowDialogFiles.showDialogFilesSelectOne( parentDir, "*.xml", defaultChoice);
    	} else {
    		totalPath = BeanPathUtilities.combine(exstPath, filePath);	
    	}
    	
    	String xmlPath = (String) declXML.getBeanProperties().get("xpath");
    	
    	if (xmlPath==null) {
    		xmlPath = "bean";
    	}

    	// A check in case its the same file, so we don't want to include it, and get a stack
    	//  over flow. There's not much we can do if, X includes Y which includes X
    	if (Files.isSameFile(exstPath,totalPath)) {
    		throw new BeanXmlException(
    			String.format(
					"Including file would cause overflow: %s",
					exstPath
				)
    		);
    	}
    	
		return BeanXmlLoader.loadBeanLocalized( totalPath, xmlPath );
    }
	
	
}
