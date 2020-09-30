package org.anchoranalysis.io.output.writer;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Like {@link RecordedOutputs} but accepts two levels, first and second, like in a {@link MultiLevelRecordedOutputs}.
 * 
 * @author Owen Feehan
 *
 */
@Accessors(fluent=true)
public class MultiLevelRecordedOutputs {
    
    /** For recording first-level outputs. */
    @Getter private RecordedOutputs first = new RecordedOutputs();

    /** A hash-map recording second-level outputs. */
    private HashMap<String,RecordedOutputs> second = new HashMap<>();
    
    /**
     * A {@link RecordedOutputs} for recording second-level outputs for a given {@code outputName} from the first-level.
     * 
     * @param outputName the outputName from the first-level
     * @return
     */
    public RecordedOutputs second(String outputName) {
        return second.computeIfAbsent(outputName, name -> new RecordedOutputs() );
    }

    /**
     * All second-level recorded output entries.
     * 
     * @return the set of entries from the internal hash-map that records outputs.
     */
    public Set<Entry<String, RecordedOutputs>> secondEntries() {
        return second.entrySet();
    }
}
