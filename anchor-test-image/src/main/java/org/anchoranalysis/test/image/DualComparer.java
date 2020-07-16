/* (C)2020 */
package org.anchoranalysis.test.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import lombok.Getter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.csv.comparer.CSVComparer;
import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.io.TestLoaderImageIO;

/**
 * Allows for comparison of objects that exist on different test loaders
 *
 * @author Owen Feehan
 */
public class DualComparer {

    @Getter private final TestLoader loader1;

    @Getter private final TestLoader loader2;

    private final TestLoaderImageIO loaderImg1;
    private final TestLoaderImageIO loaderImg2;

    public DualComparer(TestLoader loader1, TestLoader loader2) {
        super();
        this.loader1 = loader1;
        this.loader2 = loader2;
        this.loaderImg1 = new TestLoaderImageIO(loader1);
        this.loaderImg2 = new TestLoaderImageIO(loader2);
    }

    /**
     * Compare two images that have an identical path, but in two different test loaders
     *
     * @param path path to compare
     * @return TRUE if the images are equal (every pixel is identical, and data-types are the same)
     * @throws FileNotFoundException if one or both of the files cannot be found
     */
    public boolean compareTwoImages(String path) throws FileNotFoundException {
        return TestLoaderImageIO.compareTwoImages(loaderImg1, path, loaderImg2, path);
    }

    /**
     * Compare two XML documents. They are compared by their DOM trees, but they need to be
     * identical for equality.
     *
     * @param path path to compare
     * @return TRUE if the xml-documents are equal, fALSE otherwise
     */
    public boolean compareTwoXmlDocuments(String path) {
        return TestLoader.areXmlEqual(
                loader1.openXmlFromTestPath(path), loader2.openXmlFromTestPath(path));
    }

    /**
     * Compare two CSV files, ignoring the first numFirstColumnsToIgnore. They need to be exactly
     * identical, apart from these ignored columns.
     *
     * @param path path to compare
     * @param regExSeperator seperator (reg ex for split function())
     * @param firstLineHeaders does the first line have headers?
     * @param numFirstColumnsToIgnore the number of columns (leftmost) that are ignored when
     *     comparing
     * @param sortLines if true, all lines in the CSV file are sorted before comparison. if false,
     *     the order remains unchanged.
     * @param rejectZeroRows throws an exception if either of the CSV files have zero rows
     * @param messageStream if non-equal, additional explanation messages are printed here
     * @return TRUE if the csv-files are identical apart from the ignored columns, fALSE otherwise
     * @throws CSVReaderException if something goes wrong with csv I/O or a csv file is reject
     */
    public boolean compareTwoCsvFiles(String path, CSVComparer comparer, PrintStream messageStream)
            throws CSVReaderException {
        return comparer.areCsvFilesEqual(
                loaderImg1.getTestLoader().resolveTestPath(path),
                loaderImg2.getTestLoader().resolveTestPath(path),
                messageStream);
    }

    /**
     * Compare two obj-mask-collections
     *
     * @param path path to compare
     * @throws IOException if something goes wrong with I/O
     */
    public boolean compareTwoObjectCollections(String path) throws IOException {
        ObjectCollection objects1 = loaderImg1.openObjectsFromTestPath(path);
        ObjectCollection objects2 = loaderImg2.openObjectsFromTestPath(path);
        return objects1.equalsDeep(objects2);
    }
}
