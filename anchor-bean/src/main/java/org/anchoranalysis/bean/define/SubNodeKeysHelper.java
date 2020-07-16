/* (C)2020 */
package org.anchoranalysis.bean.define;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration.SubnodeConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SubNodeKeysHelper {

    /**
     * A list of sub-nodes of the configuration
     *
     * <p>We rely on an API call that returns multiple entries per bean including for each
     * config-class and config-factory
     *
     * <p>It only finds direct sub-nodes (i.e. it is not recursive)
     *
     * <p>e.g. for an example with two children (namedChnlProviderList and namedStackProviderList)
     * the following is returned: Key: namedChnlProviderList[@config-class] Key:
     * namedChnlProviderList[@config-factory] Key: namedChnlProviderList[@filePath] Key:
     * namedStackProviderList[@config-class] Key: namedStackProviderList[@config-factory] Key:
     * namedStackProviderList[@filePath]
     *
     * <p>We remove the [] component, and combine into a set for uniqueness to retrieve the desired
     * two items.
     *
     * @param subConfig
     * @return
     */
    public static Set<String> extractKeys(SubnodeConfiguration subConfig) {

        // TreeSet is good for outputting a sorted list (just to be neat!)
        TreeSet<String> out = new TreeSet<>();

        // We iterate through all beans defined, and assume each is a list of NamedBeans
        Iterator<String> itr = subConfig.getKeys();
        while (itr.hasNext()) {
            String keyShort = stripLongName(itr.next());

            // We skip empty short keys
            // We skip any key with a . as this means we've iterated onto the next level (i.e. it is
            // no longer a direct child)
            if (!keyShort.isEmpty() && !keyShort.contains(".")) {
                out.add(keyShort);
            }
        }
        return out;
    }

    /**
     * Strips a long key name of the bit in square-brackets at the end
     *
     * @param keyLong a key name with square-brackets at the end e.g. someName[bitInSquareBrackets]
     * @return the string without the square-brackets part e.g. someName
     */
    private static String stripLongName(String keyLong) {

        int indexBracket = keyLong.indexOf('[');

        // We assume the left-bracket always exists
        assert (indexBracket != -1);

        return keyLong.substring(0, indexBracket);
    }
}
