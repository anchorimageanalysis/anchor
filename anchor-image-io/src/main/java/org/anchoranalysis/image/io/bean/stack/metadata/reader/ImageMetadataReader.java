package org.anchoranalysis.image.io.bean.stack.metadata.reader;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;

/**
 * Reads an {@link ImageMetadata} from the file-system.
 *
 * @author Owen Feehan
 */
public abstract class ImageMetadataReader extends AnchorBean<ImageMetadataReader> {

    /**
     * Opens a file containing one or more images but does not read an image.
     *
     * @param path where the file is located.
     * @param defaultStackReader the default {@link StackReader} to use, if needed, and if not
     *     otherwise specified, for reading metadata.
     * @param errorReporter where to record errors that are not thrown as exceptions.
     * @return an interface to the opened file that should be closed when no longer in use.
     * @throws ImageIOException if the file cannot be read.
     */
    public abstract ImageMetadata openFile(
            Path path, StackReader defaultStackReader, ErrorReporter errorReporter)
            throws ImageIOException;
}
