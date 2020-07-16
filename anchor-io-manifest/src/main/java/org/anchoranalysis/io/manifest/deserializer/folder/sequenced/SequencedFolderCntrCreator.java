/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.folder.sequenced;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

public abstract class SequencedFolderCntrCreator<T> implements ITypedGetFromIndex<T> {

    private SequencedFolder rootFolder;

    public SequencedFolderCntrCreator(SequencedFolder rootFolder) {
        super();
        this.rootFolder = rootFolder;
    }

    protected abstract T createFromFilePath(Path path) throws CreateException;

    @Override
    public T get(int index) throws GetOperationFailedException {

        try {
            List<FileWrite> foundList = new ArrayList<>();

            String indexStr = rootFolder.getAssociatedSequence().indexStr(index);

            rootFolder.findFileFromIndex(foundList, indexStr, true);

            if (foundList.size() != 1) {
                throw new IllegalArgumentException(String.format("Cannot find index %s", indexStr));
            }

            Path path = foundList.get(0).calcPath();

            return createFromFilePath(path);
        } catch (CreateException e) {
            throw new GetOperationFailedException(e);
        }
    }
}
