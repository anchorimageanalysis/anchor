/* (C)2020 */
package org.anchoranalysis.experiment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.params.DebugModeParams;
import org.anchoranalysis.io.params.InputContextParams;

@NoArgsConstructor
public class ExperimentExecutionArguments {

    /** If defined, parameters for debug-mode. */
    private Optional<DebugModeParams> debugModeParams = Optional.empty();

    /** A list of paths referring to specific inputs; */
    private Optional<List<Path>> inputPaths = Optional.empty();

    /** A directory indicating where inputs can be located */
    private Optional<Path> inputDirectory = Optional.empty();

    /** A directory indicating where inputs can be located */
    @Getter @Setter private Optional<Path> outputDirectory = Optional.empty();

    /** A directory indicating where models can be located */
    private Optional<Path> modelDirectory = Optional.empty();

    /** If non-null, a glob that is applied on inputDirectory */
    @Setter private Optional<String> inputFilterGlob = Optional.empty();

    /**
     * If defined, a set of extension filters that can be applied on inputDirectory
     *
     * <p>A defined but empty set implies no check is applied
     *
     * <p>An Optional.empty() implies no extension filters exist.
     */
    @Getter @Setter private Optional<Set<String>> inputFilterExtensions = Optional.empty();

    /** A name to describe the ongoing task */
    @Getter @Setter private Optional<String> taskName = Optional.empty();

    public ExperimentExecutionArguments(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    /**
     * Creates an input-context, reusing parameters from the experiment-execution
     *
     * @throws IOException
     */
    public InputContextParams createInputContext() throws IOException {
        InputContextParams out = new InputContextParams();
        out.setDebugModeParams(debugModeParams);
        out.setInputDir(inputDirectory);
        out.setInputPaths(inputPaths);
        inputFilterGlob.ifPresent(out::setInputFilterGlob);
        inputFilterExtensions.ifPresent(out::setInputFilterExtensions);
        return out;
    }

    public FilePathPrefixerParams createParamsContext() throws FilePathPrefixerException {
        return new FilePathPrefixerParams(isDebugModeEnabled(), outputDirectory);
    }

    public Optional<Path> getInputDirectory() {
        return inputDirectory;
    }

    // The path will be converted to an absolute path, if it hasn't been already, based upon the
    // current working directory
    public void setInputDirectory(Optional<Path> inputDirectory) {
        this.inputDirectory =
                inputDirectory.map(
                        dir -> {
                            if (!dir.isAbsolute()) {
                                return dir.toAbsolutePath().normalize();
                            } else {
                                return dir.normalize();
                            }
                        });
    }

    /**
     * Activates debug-mode
     *
     * @param debugContains either NULL (no debugContains specified) or a string used for filtering
     *     items during debug
     */
    public void activateDebugMode(String debugContains) {
        debugModeParams = Optional.of(new DebugModeParams(debugContains));
    }

    public boolean isDebugModeEnabled() {
        return debugModeParams.isPresent();
    }

    public boolean hasInputFilterExtensions() {
        return inputFilterExtensions.isPresent();
    }

    public Path getModelDirectory() {
        return modelDirectory.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Model-directory is required but absent"));
    }

    public void setModelDirectory(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    public List<Path> getInputPaths() {
        return inputPaths.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Input-paths are required but absent"));
    }

    public void setInputPaths(List<Path> inputPaths) {
        this.inputPaths = Optional.of(inputPaths);
    }
}
