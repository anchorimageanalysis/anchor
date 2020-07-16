/* (C)2020 */
package org.anchoranalysis.experiment.bean.identifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Automatically populates a experiment-name and version number
 *
 * @author Owen Feehan
 */
public class ExperimentIdentifierAuto extends ExperimentIdentifier {

    // START BEAN FIELDS
    /** If there's no task-name, then this constant is used as a fallback name */
    @BeanField @Getter @Setter private String fallbackName = "experiment";

    /** If true, the current day (yyyy.MM.dd) is included in the output */
    @BeanField @Getter @Setter private boolean day = true;

    /** If true, the current time (HH.mm.ss) is included in the output */
    @BeanField @Getter @Setter private boolean time = true;

    /** Only relevant if time==true. Whether the second is included in the time */
    @BeanField @Getter @Setter private boolean second = true;
    // END BEAN FIELDS

    private static final String FORMAT_DAY = "yyyy.MM.dd";
    private static final String FORMAT_TIME_BASE = "HH.mm";
    private static final String FORMAT_TIME_SECOND = "ss";

    @Override
    public String identifier(Optional<String> taskName) {
        return IdentifierUtilities.identifierFromNameVersion(
                determineExperimentName(taskName), determineVersion());
    }

    /**
     * Calculates the experiment-name part of the identifier
     *
     * @param taskName if non-null a string describing the current task, or null if none exists
     * @return the experiment-name
     */
    private String determineExperimentName(Optional<String> taskName) {
        return taskName.map(name -> removeSpecialChars(finalPartOfTaskName(name)))
                .orElse(fallbackName);
    }

    /**
     * Calculates the version part of the identifier
     *
     * @return a string describing the version or null if version should be omitted
     */
    private Optional<String> determineVersion() {
        return createFormatterPatternStr()
                .map(DateTimeFormatter::ofPattern)
                .map(formatter -> formatter.format(LocalDateTime.now()));
    }

    private Optional<String> createFormatterPatternStr() {
        if (day) {
            if (time) {
                return Optional.of(FORMAT_DAY + "." + createPatternStrTime());
            } else {
                return Optional.of(FORMAT_DAY);
            }
        } else if (time) {
            return Optional.of(createPatternStrTime());
        } else {
            return Optional.empty();
        }
    }

    private String createPatternStrTime() {
        if (second) {
            return FORMAT_TIME_BASE + "." + FORMAT_TIME_SECOND;
        } else {
            return FORMAT_TIME_BASE;
        }
    }

    private static String finalPartOfTaskName(String taskName) {
        Path path = Paths.get(taskName);
        return path.getName(path.getNameCount() - 1).toString();
    }

    private static String removeSpecialChars(String str) {
        return str.replaceAll("[^a-zA-Z]+", "");
    }
}
