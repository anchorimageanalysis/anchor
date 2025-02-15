/*-
 * #%L
 * anchor-test
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

package org.anchoranalysis.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import lombok.Getter;
import org.anchoranalysis.core.serialize.XMLParser;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Loads test data, which is found at some location on the filesystem.
 *
 * @author Owen Feehan
 */
public class TestLoader {

    /** Path to the resources directory, relative to the root of the project. */
    public static final String PATH_RESOURCES = "src/test/resources";

    /** Path to where the test-data is stored. */
    @Getter private Path root;

    /** Creates a new test-data loader. */
    private TestLoader(String root) {
        this(Paths.get(root));
    }

    /**
     * Creates a new test-data loader.
     *
     * @param root path to where the test-data is stored
     */
    private TestLoader(Path root) {
        super();

        if (!root.toFile().exists()) {
            throw new TestDataInitializeException(
                    String.format("Cannot find path '%s' path", root));
        }

        if (!root.toFile().isDirectory()) {
            throw new TestDataInitializeException(String.format("Path '%s' is not a folder", root));
        }

        this.root = root.toAbsolutePath();
    }

    /**
     * Creates a new test-data loader finding {@code PATH_RESOURCES} using the Maven working
     * directory.
     *
     * @return a loader associated with {@code MAVEN_WORKING_DIR/PATH_RESOURCES}
     */
    public static TestLoader createFromMavenWorkingDirectory() {
        return new TestLoader(PATH_RESOURCES);
    }

    /**
     * Creates a new test-data loader finding {@code PATH_RESOURCES/PLUS_SOMETHING} using the Maven
     * working directory.
     *
     * @param toAppendToDirectory appended to Maven working directory to determine final directory.
     * @return a loader associated with the {@code MAVEN_WORKING_DIR/PATH_RESOURCES/PLUS_SOMETHING}
     */
    public static TestLoader createFromMavenWorkingDirectory(String toAppendToDirectory) {
        Path path = pathMavenWorkingDirectory(toAppendToDirectory);
        return new TestLoader(path.toString());
    }

    /**
     * Creates a new test-data loader using an explicit file path as root.
     *
     * @param rootDirectory the path where the root folder is.
     * @return a loader associated with the explicit root.
     */
    public static TestLoader createFromExplicitDirectory(String rootDirectory) {
        return createFromExplicitDirectory(Paths.get(rootDirectory));
    }

    /**
     * Creates a new test-data loader using an explicit file path as root.
     *
     * @param rootDirectory the path where the root folder is.
     * @return a loader associated with the explicit root.
     */
    public static TestLoader createFromExplicitDirectory(Path rootDirectory) {
        return new TestLoader(rootDirectory);
    }

    /**
     * Creates a new test-loader for a subdirectory of the current test.
     *
     * @param subdirectory the subdirectory to use (relative path to the current root).
     * @return the new test-loader
     */
    public TestLoader createForSubdirectory(String subdirectory) {
        return new TestLoader(root.resolve(subdirectory));
    }

    /**
     * Determines the path to a directory inside the Maven Working Directory.
     *
     * <p>It uses the pattern {@code PATH_RESOURCES/PLUS_SOMETHING} inside the Maven working
     * directory.
     *
     * @param toAppendToDirectory appended to Maven working directory to determine final directory.
     * @return the path to this directory on the file-system.
     */
    public static Path pathMavenWorkingDirectory(String toAppendToDirectory) {
        return Paths.get(PATH_RESOURCES).resolve(toAppendToDirectory);
    }

    /**
     * Resolves a path to test-data (relative path to the test-data root) to an absolute path on the
     * file system.
     *
     * @param testPath relative-path of a test-data item. It is relative to the test-data root.
     * @return the resolved-path
     */
    public Path resolveTestPath(String testPath) {
        if (Paths.get(testPath).isAbsolute()) {
            throw new IllegalArgumentException(
                    String.format("testPath should be relative, not absolute: %s", testPath));
        }
        return root.resolve(testPath);
    }

    /**
     * Does a resource exist with a particular {@code folderPath + fileName}.
     *
     * @param testFilePath path to a file in the test-data.
     * @return true if a file is found at the location, false otherwise.
     */
    public boolean doesPathExist(String testFilePath) {

        Path fileNameReslvd = resolveTestPath(testFilePath);

        return fileNameReslvd.toFile().exists();
    }

    /**
     * Does a resource exist with a particular {@code folderPath + fileName}.
     *
     * @param testDirectoryPath path to a folder in the test-data (can be empty).
     * @param fileName a filename in the {@code testDirectoryPath}.
     * @return true if a file is found at the location, false otherwise.
     */
    public boolean doesPathExist(String testDirectoryPath, String fileName) {
        Path folder = resolveTestPath(testDirectoryPath);
        return folder.resolve(fileName).toFile().exists();
    }

    /**
     * Prints the names of all files (recursively) in a test-folder to {@code stdout}.
     *
     * @param testDirectoryPath path to a folder in the test-data (can be empty).
     */
    public void printAllFilesFromTestDirectoryPath(String testDirectoryPath) {
        Path folderPathResolved = resolveTestPath(testDirectoryPath);
        PrintFilesInDirectory.printRecursively(folderPathResolved.toString());
    }

    /**
     * Opens a XML document - with a path relative to the test root.
     *
     * @param testPath the path to the XML file (relative to the test root).
     * @return the XML document.
     */
    public Document openXmlFromTestPath(String testPath) {
        return openXmlAbsoluteFilePath(resolveTestPath(testPath));
    }

    /**
     * Opens a XML document - with an absolute path on the filesystem.
     *
     * @param filePath the path to the XML file (absolute path).
     * @return the XML document.
     */
    public static Document openXmlAbsoluteFilePath(String filePath) {
        return openXmlAbsoluteFilePath(Paths.get(filePath));
    }

    /**
     * Opens a XML document - with an absolute path on the filesystem.
     *
     * @param filePath the path to the XML file (absolute path).
     * @return the XML document.
     */
    public static Document openXmlAbsoluteFilePath(Path filePath) {
        try {
            return XMLParser.parse(filePath.toFile());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new TestDataLoadException(e);
        }
    }

    /**
     * Copies all the data in the test-data folder (recursively), preserving file modification
     * times.
     *
     * @param destination destination-folder.
     * @throws IOException if a copy error occurs.
     */
    public void copyToDirectory(File destination) throws IOException {
        FileUtils.copyDirectory(root.toFile(), destination, true);
    }

    /**
     * Copies specific subdirectories from the test-data folder (recursively), preserving file
     * modification times.
     *
     * @param subdirectoriesSource which subdirectories to copy from (their full-path is preserved).
     * @param directoryDestination destination-folder.
     * @throws IOException if a copy error occurs.
     */
    public void copyToDirectory(String[] subdirectoriesSource, File directoryDestination)
            throws IOException {

        for (String subdirectory : subdirectoriesSource) {
            Path pathSubdir = root.resolve(subdirectory);

            // Create the target folder
            File destSubdirectory = new File(directoryDestination, subdirectory);
            destSubdirectory.mkdirs();

            FileUtils.copyDirectory(pathSubdir.toFile(), destSubdirectory, true);
        }
    }
}
