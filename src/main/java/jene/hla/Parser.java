
package jene.hla;

import jam.lang.JamException;
import jam.util.StringUtil;

final class Parser {
    private final String original;

    private String substr;
    private Locus  locus;
    private int    superType;
    private int    subType;

    private Parser(String string) {
        this.original = string;
        this.substr   = string;
    }

    static Allele parse(String string) {
        return new Parser(string).parse();
    }

    private Allele parse() {
        trimWhiteSpace();
        removePrefix();
        parseLocus();
        removeDelim1();
        parseSuperType();
        removeDelim2();
        parseSubType();

        return Allele.instance(locus, superType, subType);
    }

    private void trimWhiteSpace() {
        substr = original.trim();
    }

    private void removePrefix() {
        substr = StringUtil.removePrefix(substr, Allele.PREFIX);
    }

    private void parseLocus() {
        for (Locus candidate : Locus.values()) {
            if (substr.startsWith(candidate.name())) {
                locus  = candidate;
                substr = StringUtil.removePrefix(substr, candidate.name());
                return;
            }
        }

        throw JamException.runtime("Missing locus in allele [%s].", original);
    }

    private void removeDelim1() {
        substr = StringUtil.removePrefix(substr, "*");
        substr = StringUtil.removePrefix(substr, "-");
        substr = StringUtil.removePrefix(substr, "_");
    }

    private void parseSuperType() {
        superType = Integer.parseInt(substr.substring(0, 2));
        substr    = substr.substring(2);
    }

    private void removeDelim2() {
        substr = StringUtil.removePrefix(substr, ":");
        substr = StringUtil.removePrefix(substr, "-");
        substr = StringUtil.removePrefix(substr, "_");
    }

    private void parseSubType() {
        if (substr.length() < 2 || substr.length() > 3)
            throw JamException.runtime("Invalid allele format [%s].", original);

        subType = Integer.parseInt(substr);
    }
}
