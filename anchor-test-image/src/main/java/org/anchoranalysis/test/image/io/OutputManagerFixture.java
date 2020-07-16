/* (C)2020 */
package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManagerPermissive;
import org.anchoranalysis.io.output.bean.OutputManagerWithPrefixer;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.test.LoggingFixture;

public class OutputManagerFixture {

    private OutputManagerFixture() {}

    // These operations must occur before creating TempBoundOutputManager
    private static void globalSetup() {
        TestReaderWriterUtilities.ensureRasterWriter();
    }

    public static BoundOutputManagerRouteErrors outputManagerForRouterErrors(Path pathTempFolder)
            throws BindFailedException {

        ErrorReporter errorReporter = LoggingFixture.suppressedLogErrorReporter().errorReporter();

        return new BoundOutputManagerRouteErrors(
                createBoundOutputManagerFor(pathTempFolder, errorReporter), errorReporter);
    }

    public static BoundOutputManager outputManagerFor(Path pathTempFolder)
            throws BindFailedException {
        return createBoundOutputManagerFor(
                pathTempFolder, LoggingFixture.suppressedLogErrorReporter().errorReporter());
    }

    private static BoundOutputManager createBoundOutputManagerFor(
            Path pathTempFolder, ErrorReporter errorReporter) throws BindFailedException {

        globalSetup();

        OutputWriteSettings ows = new OutputWriteSettings();

        // We populate any defaults in OutputWriteSettings from our default bean factory
        try {
            ows.checkMisconfigured(RegisterBeanFactories.getDefaultInstances());
        } catch (BeanMisconfiguredException e1) {
            errorReporter.recordError(OutputManagerFixture.class, e1);
        }

        OutputManagerWithPrefixer outputManager = new OutputManagerPermissive();
        outputManager.setSilentlyDeleteExisting(true);
        outputManager.setOutputWriteSettings(ows);
        outputManager.setFilePathPrefixer(new FilePathPrefixerConstantPath(pathTempFolder));

        try {
            return outputManager.bindRootFolder(
                    "debug",
                    new ManifestRecorder(),
                    new FilePathPrefixerParams(false, Optional.empty()));
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }

    private static class FilePathPrefixerConstantPath extends FilePathPrefixer {

        private FilePathPrefix prefix;

        public FilePathPrefixerConstantPath(Path path) {
            prefix = new FilePathPrefix(path);
        }

        @Override
        public FilePathPrefix outFilePrefix(
                PathWithDescription input,
                String experimentIdentifier,
                FilePathPrefixerParams context) {
            return prefix;
        }

        @Override
        public FilePathPrefix rootFolderPrefix(
                String experimentIdentifier, FilePathPrefixerParams context) {
            return prefix;
        }
    }
}
