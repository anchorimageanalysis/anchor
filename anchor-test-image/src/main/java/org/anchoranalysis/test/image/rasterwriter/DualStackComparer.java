package org.anchoranalysis.test.image.rasterwriter;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedPredicate;
import org.anchoranalysis.test.image.DualComparer;

/**
 * Two different optional comparers for an image-stack.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class DualStackComparer {

    /**
     * If defined, when no comparison raster can be found, errors are not thrown, and instead the
     * generated rasters are copied to this path.
     *
     * <p>This is useful for building a set of rasters to compare against, when they don't already
     * exist.
     */
    private static final Optional<String> PATH_COPY_TO = Optional.empty();

    /** A minimum file-size for all written rasters, below which we assume an error has occurred. */
    private static final int MINIMUM_FILE_SIZE = 20;

    // START REQUIRED ARGUMENTS
    /**
     * The comparer used for comparing bytewise to the newly created files with the saved rasters.
     */
    private final Optional<DualComparer> comparerBytewise;

    /**
     * The comparer used for comparing voxelwise to the newly created files with the saved rasters.
     */
    private final Optional<DualComparerWithExtension> comparerVoxelwise;

    /**
     * The extension to use for writing and testing files.
     *
     * <p>This is case-sensitive.
     *
     * <p>This shouldn't include the full-stop.
     */
    private final String extension;
    // END REQUIRED ARGUMENTS

    /**
     * Performs comparisons for a stack written at {@code path} against available comparers.
     *
     * @param path the path the image-stack was written to
     * @param filenameWithoutExtension the filename before extensions were added.
     * @throws IOException
     */
    public void assertComparisons(Path path, String filenameWithoutExtension) throws IOException {

        assertTrue(
                filenameWithoutExtension + "_minimumFileSize",
                Files.size(path) > MINIMUM_FILE_SIZE);

        if (comparerBytewise.isPresent()) {
            performComparison(
                    filenameWithoutExtension,
                    "_binaryCompare",
                    path,
                    fileToCompare ->
                            comparerBytewise
                                    .get()
                                    .compareTwoBinaryFiles(
                                            addSelectedExtension(filenameWithoutExtension)));
        }

        if (comparerVoxelwise.isPresent()) {
            String fileToCompareWithExtension = addSelectedExtension(filenameWithoutExtension);
            performComparison(
                    filenameWithoutExtension,
                    "_voxelwiseCompare",
                    path,
                    fileToCompare ->
                            comparerVoxelwise
                                    .get()
                                    .getComparer()
                                    .compareTwoImages(
                                            fileToCompareWithExtension,
                                            addExtension(
                                                    fileToCompare,
                                                    comparerVoxelwise.get().getExtension()),
                                            true));
        }
    }

    private void performComparison(
            String filename,
            String suffix,
            Path path,
            CheckedPredicate<String, IOException> comparer)
            throws IOException {
        try {
            assertTrue(filename + suffix, comparer.test(filename));
        } catch (IOException e) {

            if (PATH_COPY_TO.isPresent()) {
                Path copyTo = Paths.get(PATH_COPY_TO.get()).resolve(addSelectedExtension(filename));
                Files.copy(path, copyTo);
            } else {
                System.err.printf(
                        "The test wrote a file to temporary-folder directory at:%n%s%n",
                        path); // NOSONAR
                throw new IOException(
                        String.format(
                                "The comparer threw an IOException, which likely means it cannot find an appropriate raster to compare against for %s.",
                                filename),
                        e);
            }
        }
    }

    private String addSelectedExtension(String filename) {
        return addExtension(filename, extension);
    }

    private static String addExtension(String filename, String extensionToAdd) {
        return filename + "." + extensionToAdd;
    }
}
