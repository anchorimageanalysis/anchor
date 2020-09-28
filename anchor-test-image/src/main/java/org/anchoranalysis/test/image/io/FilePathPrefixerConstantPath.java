package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;

class FilePathPrefixerConstantPath extends FilePathPrefixer {

    private FilePathPrefix prefix;

    public FilePathPrefixerConstantPath(Path path) {
        prefix = new FilePathPrefix(path);
    }

    @Override
    public FilePathPrefix outFilePrefix(
            NamedPath path,
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