package anchor.test;

import static org.junit.Assert.assertTrue;

/*
 * #%L
 * anchor-test
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


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.anchoranalysis.core.file.PathUtilities;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Loads test data, which is found at some location on the file-system
 * 
 * @author Owen Feehan
 *
 */
public class TestLoader {
	
	/**
	 * Path to where the test-data is stored
	 */
	private Path pathTestDataRoot;
	
	private static final String DEFAULT_PROPERTY_NAME_TEST_DATA_ROOT = "test.data.root";
	
	/**
	 * Makes a new test-data loader
	 * 
	 * @param pathTestDataRoot path to where the test-data is stored
	 */
	private TestLoader( String root ) {
		this( Paths.get(root) );
	}
	
	/**
	 * Makes a new test-data loader
	 * 
	 * @param pathTestDataRoot path to where the test-data is stored
	 */
	private TestLoader( Path root ) {
		super();
		
		if (!Files.exists(root)) {
			throw new TestDataInitException(
			  String.format("Cannot find path '%s' path", root)
			);
		}
		
		if (!Files.isDirectory(root)) {
			throw new TestDataInitException(
			  String.format("Path '%s' is not a folder", root)
			);			
		}
		
		this.pathTestDataRoot = root.toAbsolutePath();
	}
	

	/**
	 * Creates a new test-data loader using the default system property
	 *  (DEFAULT_PROPERTY_NAME_TEST_DATA_ROOT)
	 * 
	 * @return a testLoader associated with the root found in the default system proeprty
	 */
	public static TestLoader createFromMavenWorkingDir() {
		return new TestLoader("src/test/resources");
	}
	
	
	/**
	 * Creates a new test-data loader using the default system property
	 *  (DEFAULT_PROPERTY_NAME_TEST_DATA_ROOT)
	 * 
	 * @return a testLoader associated with the root found in the default system proeprty
	 */
	public static TestLoader createFromDefaultSystemProperty() {
		return createFromSystemProperty(DEFAULT_PROPERTY_NAME_TEST_DATA_ROOT);
	}
	
	/**
	 * Creates a new test-data loader using a system property for the root
	 * 
	 * @param propertyNameTestDataRoot path to where the test-data is stored
	 * @return a testLoader associated with the root found in the system proeprty
	 */
	public static TestLoader createFromSystemProperty( String propertyNameTestDataRoot ) {
		String root = System.getProperty(propertyNameTestDataRoot);
		
		if (root==null || root.isEmpty()) {
			throw new TestDataInitException(
			  String.format("Property '%s' must be set, specifying the location of the test-data", propertyNameTestDataRoot)
			);
		}
		return new TestLoader(root);
	}
	
	
	/**
	 * Creates a new test-data loader using an explicit File path as root
	 * 
	 * @param rootDirectory the path where the root folder is
	 * @return a testLoader associated with the explicit root
	 */
	public static TestLoader createFromExplicitDirectory( Path rootDirectory ) {
		return new TestLoader( rootDirectory );
	}
	
	
	/**
	 * Creates a new test-data loader using an explicit File path as root
	 * 
	 * @param launchClass the class the application was launched from
	 * @return a testLoader associated with the explicit root
	 */
	public static TestLoader createFromExecutingJARDirectory( Class<?> launchClass ) {
		Path launchDir = PathUtilities.pathCurrentJAR( launchClass );
		return createFromExplicitDirectory( launchDir );
	}
	

	/**
	 * Creates a new test-loader for a subdirectory of the current test
	 * @param subdirectory the subdirectory to use (relative path to the current root)
	 * @return the new test-loader
	 */
	public TestLoader createForSubdirectory( String subdirectory ) {
		return new TestLoader( pathTestDataRoot.resolve(subdirectory) );
	}
	
	/**
	 * Resolves a path to test-data (relative path to the test-data root) to an absolute path on the file system
	 * 
	 * @param testPath relative-path of a test-data item. It is relative to the test-data root.
	 * @return the resolved-path
	 */
	public Path resolveTestPath( String testPath ) {
		if (Paths.get(testPath).isAbsolute()) {
			throw new IllegalArgumentException(
				String.format("testPath should be relative, not absolute: %s", testPath )
			);
		}
		return pathTestDataRoot.resolve(testPath);
	}
	
	
	/**
	 * Does a resource exist with a particular folderPath + fileName
	 * 
	 * @param testFilePath path to a file in the test-data
	 * @return true if a file is found at the location, false otherwise
	 */
	public boolean doesPathExist( String testFilePath ) {
		
		Path fileNameReslvd = resolveTestPath( testFilePath );

		return Files.exists(fileNameReslvd);
	}
	
	
	/**
	 * Does a resource exist with a particular folderPath + fileName
	 * 
	 * @param testFolderPath path to a folder in the test-data (can be empty)
	 * @param fileName a filename in the testFolderPath
	 * @return true if a file is found at the location, false otherwise
	 */
	public boolean doesPathExist( String testFolderPath, String fileName ) {
		
		Path folderPathRslvd = resolveTestPath( testFolderPath );
		
		Path path = folderPathRslvd.resolve(fileName);
		return Files.exists(path);
	}
	
	
	private void listDirectory(String dirPath, int level) {
	    File dir = new File(dirPath);
	    File[] firstLevelFiles = dir.listFiles();
	    if (firstLevelFiles != null && firstLevelFiles.length > 0) {
	        for (File aFile : firstLevelFiles) {
	            for (int i = 0; i < level; i++) {
	                System.out.print("\t");
	            }
	            if (aFile.isDirectory()) {
	                System.out.println("[" + aFile.getName() + "]");
	                listDirectory(aFile.getAbsolutePath(), level + 1);
	            } else {
	                System.out.println(aFile.getName());
	            }
	        }
	    }
	}
	
	/**
	 * Prints the names of all files (recursively) in a test-folder to stdout
	 * 
	 * @param testFolderPath path to a folder in the test-data (can be empty)
	 */
	public void printAllFilesFromTestFolderPath( String testFolderPath) {
		Path folderPathRslvd = resolveTestPath( testFolderPath );
		listDirectory( folderPathRslvd.toString(), 0 );
	}
	
	/**
	 * Opens a XML document - with a path relative to the test root
	 * 
	 * @param testPath the path to the xml file (relative to the test root)
	 * @return the XML document
	 */
	public Document openXmlFromTestPath( String testPath ) {
		Path filePath = resolveTestPath( testPath);
		return openXmlAbsoluteFilePath(filePath);
	}
	
	/**
	 * Opens a XML document - with an absolute path on the filesystem
	 * 
	 * @param filePath the path to the xml file (absolute path)
	 * @return the XML document
	 */
	public static Document openXmlAbsoluteFilePath( String filePath ) {
		return openXmlAbsoluteFilePath( Paths.get(filePath) );
	}
	
	/**
	 * Opens a XML document - with an absolute path on the filesystem
	 * 
	 * @param filePath the path to the xml file (absolute path)
	 * @return the XML document
	 */
	public static Document openXmlAbsoluteFilePath( Path filePath ) {
		try {
			DocumentBuilderFactory dbf = createDocumentBuilderFactory();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			return db.parse( filePath.toFile() );
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new TestDataLoadException(e);
		}			
	}

	/**
	 * Does a check if the XML documents are equal
	 * 
	 * Note that both objects are normalized during the check, and their state changes permanently.
	 * 
	 * @param doc1 first document
	 * @param doc2 second document
	 * @return TRUE if their contents match, FALSE otherwise
	 */
	public static boolean areXmlEqual( Document doc1, Document doc2 ) {
		doc1.normalizeDocument();
		doc2.normalizeDocument();
		return doc1.isEqualNode(doc2);
	}

	
	/**
	 * Copies all the data in the test-data folder (recursively), preserving file-dates
	 * 
	 * @param dirDest destination-folder
	 * @throws IOException if a copy error occurs
	 */
	public void copyToDirectory( File dirDest ) throws IOException {
	    FileUtils.copyDirectory( pathTestDataRoot.toFile(), dirDest, true );
	}
	

	/**
	 * Copies specific subdirectories from the test-data folder (recursively), preserving file-dates
	 * 
	 * @param subdirectoriesSrc which subdirectories to copy from (their full-path is preserved)
	 * @param dirDest destination-folder 
	 * @throws IOException if a copy error occurs
	 */
	public void copyToDirectory( String[] subdirectoriesSrc, File dirDest ) throws IOException {
		
		for( String subdir : subdirectoriesSrc ) {
			Path pathSubdir = pathTestDataRoot.resolve(subdir);
			
			// Create the target folder
			File destSubdir = new File( dirDest, subdir );
			destSubdir.mkdirs();
			
			FileUtils.copyDirectory( pathSubdir.toFile(), destSubdir, true );
		}
	}
	
	
	public void testManifestExperiment( String outputDir ) {
		assertTrue( doesPathExist(outputDir,"manifestExperiment.ser") );
		assertTrue( doesPathExist(outputDir,"manifestExperiment.ser.xml") );
		assertTrue( !doesPathExist(outputDir,"manifestExperiment2.ser.xml") );
	}
	
	public Path getRoot() {
		return pathTestDataRoot;
	}
		
	private static DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		return dbf;
	}
	
}
