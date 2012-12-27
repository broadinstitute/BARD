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

                String ky = trimAndMapKey(attribute.key, attributeNameMapping)


                def val = attribute.value
                if (attribute.value instanceof String) {
                    //remove '| | |' prefix
                    String valStr = StringUtils.split(attribute.value as String, '|').toList().last().trim()
                    String matchedValue = attributeNameMapping.keySet().find { String key -> return StringUtils.equalsIgnoreCase(key, valStr)}
                    valStr = matchedValue ? attributeNameMapping.get(matchedValue) : valStr
                    //if val could be number value, replace it ('650 nM' --> 650)
                    def matcher = valStr =~ /^\s*(\d+)\s*[muµn]\w\s*$/
                    Boolean matches = matcher.matches()
                    if (matches) {
                        val = new BigDecimal(matcher[0][1])//matcher[0] is the match; matcher[0][1] is the first matched group (Groovy Matcher spec).
                    }
                    else {
                        val = valStr
                    }
                }

                if ((attribute.attributeType != AttributeType.Free) && !val) return //Unless the attribute-type is Free, skip attributes with empty value

                String concentrationUnits = trimAndMapKey(attribute.concentrationUnits, attributeNameMapping)

                Attribute attr = new Attribute(attribute)
                attr.key = ky
                attr.value = val
                attr.concentrationUnits = concentrationUnits

                assayCtxDTO.attributes << attr
            }
            assayContextListCleaned << assayCtxDTO
        }

        return assayContextListCleaned
    }


    private String trimAndMapKey(String key, Map attributeNameMapping) {
        String trimmedKey = StringUtils?.split(key, '|')?.toList()?.last()?.trim()

        String matchedKey = attributeNameMapping.keySet().find { String keyInMap -> return StringUtils.equalsIgnoreCase(keyInMap, trimmedKey)}
        trimmedKey = matchedKey ? attributeNameMapping.get(matchedKey) : trimmedKey
        return trimmedKey
    }
}
