/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.directory;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FindFailedException;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.manifest.sequencetype.IncompleteElementRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.nullness.qual.Nullable;
import com.google.common.base.Preconditions;

// A folder contains a list of subfolders, contained files varies by implementation
public abstract class DirectoryWrite implements SequencedDirectory, WriteOperationRecorder, Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link DirectoryWrite} in the manifest for the parent directory.
     * 
     * <p>This is not {@link Optional} as it needs to be serialized.
     */
    @Nullable private DirectoryWrite parent;

    /**
     * The {@link DirectoryWrite} in the manifest for any subdirectories.
     */
    private ArrayList<DirectoryWrite> subdirectories = new ArrayList<>();

    private static Log log = LogFactory.getLog(DirectoryWrite.class);

    /**
     * A description of this directory for the manifest.
     * 
     * <p>Note that is not {@link Optional} as {@link Optional} cannot be serialized. 
     */
    @Nullable private ManifestDirectoryDescription description;

    public DirectoryWrite() {
        log.debug("New Directory Write: empty");
        parent = null;
    }

    // Parent folder
    public DirectoryWrite(DirectoryWrite parent) {
        super();
        log.debug("New Directory Write: " + parent.relativePath());
        this.parent = parent;
    }

    public abstract Path relativePath();

    public Path calculatePath() {
        if (parent != null) {
            return parent.calculatePath().resolve(relativePath());
        } else {
            return relativePath();
        }
    }

    @Override
    public void findFileFromIndex(List<FileWrite> foundList, String index, boolean recursive) throws FindFailedException {
        findFile(foundList, file -> file !=null && file.getIndex().equals(index), true);
    }

    // Finds a directory a comparator matches
    public abstract void findFile(
            List<FileWrite> foundList, Predicate<FileWrite> predicate, boolean recursive) throws FindFailedException;

    // Finds a folder a comparator matches
    public synchronized void findDirectory(List<DirectoryWrite> foundList, Predicate<DirectoryWrite> predicate) {

        for (DirectoryWrite directory : subdirectories) {

            if (predicate.test(directory)) {
                foundList.add(directory);
            }

            if (directory != null) {
                directory.findDirectory(foundList, predicate);
            }
        }
    }

    @Override
    public synchronized WriteOperationRecorder writeSubdirectory(
            Path relativeDirectoryPath,
            ManifestDirectoryDescription directoryDescription,
            Subdirectory subdirectory) {
        subdirectory.assignParentFolder(Optional.of(this));
        subdirectory.assignPath(relativeDirectoryPath);
        subdirectory.assignDescription(directoryDescription);
        subdirectories.add(subdirectory);
        return subdirectory;
    }

    protected Optional<DirectoryWrite> getParentFolder() {
        return Optional.ofNullable(parent);
    }

    public void assignParentFolder(Optional<DirectoryWrite> parentFolder) {
        this.parent = parentFolder.orElse(null);
    }

    protected List<DirectoryWrite> subdirectories() {
        return subdirectories;
    }

    public abstract List<FileWrite> fileList();

    @Override
    public IncompleteElementRange getAssociatedElementRange() {
        return description.getSequenceType().elementRange();
    }

    /**
     * Assigns a description, which may not already exist.
     * 
     * @param description
     */
    public void assignDescription(ManifestDirectoryDescription description) {
        Preconditions.checkArgument(description!=null);
        Preconditions.checkArgument(this.description==null);
        this.description = description;
    }
    
    public Optional<ManifestDirectoryDescription> description() {
        return Optional.ofNullable(description);
    }
}
