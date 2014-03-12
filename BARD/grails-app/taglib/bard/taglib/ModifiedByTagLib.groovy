package bard.taglib

import org.apache.commons.lang3.StringUtils

class ModifiedByTagLib {
    def renderModifiedByEnsureNoEmail = { attrs, body ->
        String modifiedBy = attrs.entity?.modifiedBy
        out << modifiedByEnsureNoEmail(modifiedBy)
    }

    public static String modifiedByEnsureNoEmail(String modifiedBy) {
        StringUtils.substringBefore(modifiedBy, '@')
    }
}
