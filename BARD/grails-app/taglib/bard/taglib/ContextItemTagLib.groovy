package bard.taglib

import bard.db.dictionary.Element
import bard.db.model.AbstractContextItem
import org.apache.commons.lang3.StringUtils

class ContextItemTagLib {


    public static final String PERSON_URL = 'http://www.bard.nih.gov/person#'
    def renderContextItemValueDisplay = { attrs, body ->
        final AbstractContextItem contextItem = attrs.contextItem
        final String externalUrl = contextItem?.attributeElement?.externalURL
        if(StringUtils.isNotBlank(externalUrl) && externalUrl!= PERSON_URL)   {
            out << "<a href='${externalUrl}${contextItem.extValueId}' target='_blank' >${contextItem.valueDisplay}</a>"
        }
        else{
            out << contextItem.valueDisplay
        }
    }
}
