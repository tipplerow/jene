
package jene.ensembl;

import java.util.regex.Pattern;

import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.util.RegexUtil;

/**
 * Provides a skeletal implementation for Ensemble identifiers.
 */
public abstract class EnsemblID extends KeyedObject<String> {
    /**
     * Delimiter that separates fields in the Ensembl header lines.
     */
    public static final Pattern HEADER_FIELD_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Delimiter that separates the Ensembl version number from the
     * immutable primary key.
     */
    public static final Pattern ENSEMBL_VERSION_DELIM = RegexUtil.DOT;

    /**
     * Creates a new Ensembl identifier.
     *
     * @param key the key string.
     *
     * @param strip whether to remove the version number from the key.
     */
    protected EnsemblID(String key, boolean strip) {
        super(strip ? stripVersion(key) : key);
    }

    /**
     * Identifies key labels contained in header lines.
     *
     * @param headerLine the header line of an Ensembl record.
     *
     * @param labelCode the code used in the Ensemble header text to
     * indicate the identifier type.
     *
     * @return {@code true} iff the header line contains a key with
     * the given label.
     */
    public static boolean headerContains(String headerLine, String labelCode) {
        String[] fields = RegexUtil.split(HEADER_FIELD_DELIM, headerLine);

        for (String field : fields)
            if (field.startsWith(labelCode))
                return true;

        return false;
    }

    /**
     * Extracts the key string from the header line of an Ensembl
     * record.
     *
     * @param headerLine the header line of an Ensembl record.
     *
     * @param labelCode the code used in the Ensemble header text to
     * indicate the identifier type.
     *
     * @return the key contained in the given header line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted key.
     */
    public static String parseHeader(String headerLine, String labelCode) {
        String[] fields = RegexUtil.split(HEADER_FIELD_DELIM, headerLine);

        for (String field : fields)
            if (field.startsWith(labelCode))
                return stripVersion(stripLabel(field, labelCode));

        throw JamException.runtime("Missing <%s> identifier: [%s]", labelCode, headerLine);
    }

    /**
     * Strips the leading label code from a key field.
     *
     * @param keyField a key field extracted from a header line.
     *
     * @param labelCode the code used in the Ensemble header text to
     * indicate the identifier type.
     *
     * @return the key field with the leading label code removed.
     */
    public static String stripLabel(String keyField, String labelCode) {
        if (keyField.startsWith(labelCode))
            return keyField.substring(labelCode.length());
        else
            return keyField;
    }

    /**
     * Strips the trailing version code from a key string.
     *
     * @param keyString a key string.
     *
     * @return the key string with the trailing version code removed.
     */
    public static String stripVersion(String keyString) {
        return RegexUtil.split(ENSEMBL_VERSION_DELIM, keyString)[0];
    }
}
