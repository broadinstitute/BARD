package bard.dm.minimumassayannotation

import org.apache.commons.lang.StringUtils
import bard.db.registration.AttributeType

/**
 * Clean up all the key/value pairs names to:
 * 1. remove the '| | |' prefix
 * 2. trim
 * 3. convert to standard names based on the attributeNameMapping map
 * 4. Convert text field to numerical values where appropriate (e.g, '680 nm' --> 680, and the 'nm' part is discarded)
 */
class AttributesContentsCleaner {
    /**
     * Clean up all the key/value pairs names to:
     * 1. remove the '| | |' prefix
     * 2. trim
     * 3. convert to standard names based on the attributeNameMapping map
     * 4. Convert text field to numerical values where appropriate (e.g, '680 nm' --> 680, and the 'nm' part is discarded)
     */
    List<ContextDTO> clean(List<ContextDTO> assayContextList, Map attributeNameMapping) {
        List<ContextDTO> assayContextListCleaned = []
        assayContextList.each {ContextDTO assayContextDTO ->
            ContextDTO assayCtxDTO = new ContextDTO()
            assayCtxDTO.name = assayContextDTO.name
            assayCtxDTO.aid = assayContextDTO.aid
            assayContextDTO.attributes.each {Attribute attribute ->
                String ky = StringUtils.split(attribute.key, '|').toList().last().trim()
                String matchedKey = attributeNameMapping.keySet().find { String key -> return StringUtils.equalsIgnoreCase(key, ky)}
                ky = matchedKey ? attributeNameMapping.get(matchedKey) : ky

                def val = attribute.value
                if (attribute.value instanceof String) {
                    String valStr = StringUtils.split(attribute.value as String, '|').toList().last().trim()
                    String matchedValue = attributeNameMapping.keySet().find { String key -> return StringUtils.equalsIgnoreCase(key, valStr)}
                    valStr = matchedValue ? attributeNameMapping.get(matchedValue) : valStr
                    //if val could be number value, replace it ('650 nM' --> 650)
                    val = (valStr && valStr.split()[0].isNumber()) ? new BigDecimal(valStr.split()[0]) : valStr
                }

                if ((attribute.attributeType != AttributeType.Free) && !val) return //Unless the attribute-type is Free, skip attributes with empty value
                Attribute attr = new Attribute(attribute)
                attr.key = ky
                attr.value = val
                assayCtxDTO.attributes << attr
            }
            assayContextListCleaned << assayCtxDTO
        }
        assayContextListCleaned
    }
}
