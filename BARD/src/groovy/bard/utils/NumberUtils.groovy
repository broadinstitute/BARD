package bard.utils

import org.apache.commons.lang.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

class NumberUtils {

    private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("^[-+]?[1-9][0-9]*\\.?[0-9]*([Ee][+-]?[0-9]+)")

    private NumberUtils() {}

    static BigDecimal convertScientificNotationValue(String value) {
        String cleanValue = StringUtils.trimToNull(value)
        BigDecimal toReturn = null
        toReturn = new BigDecimal(value)
//        try {
////            toReturn = org.apache.commons.lang3.math.NumberUtils.createBigDecimal(cleanValue)
//        }
//        catch (NumberFormatException nfe) {
//            // do nothing for now
//        }
        toReturn
    }

    static boolean isScientificNotationValue(String value) {
        boolean isScientificNotationvalue = false
        if (value) {
            Matcher matcher = SCIENTIFIC_NOTATION_PATTERN.matcher(value);
            isScientificNotationvalue = matcher.matches()
        }
        return isScientificNotationvalue
    }
}
