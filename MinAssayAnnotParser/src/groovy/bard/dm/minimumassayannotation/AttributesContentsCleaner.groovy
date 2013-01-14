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

    private final Map attributeNameMapping

    public AttributesContentsCleaner(Map attributeNameMapping) {
        this.attributeNameMapping = attributeNameMapping
    }

    /**
     * Clean up all the key/value pairs names to:
     * 1. remove the '| | |' prefix
     * 2. trim
     * 3. convert to standard names based on the attributeNameMapping map
     * 4. Convert text field to numerical values where appropriate (e.g, '680 nm' --> 680, and the 'nm' part is discarded)
     */
    void clean(List<AssayDto> assayDtoList) {

        for (AssayDto assayDto : assayDtoList) {
            //clean assay contexts
            List<ContextDTO> contextListCleaned = new LinkedList<ContextDTO>()

            for (ContextDTO assayContextDTO : assayDto.assayContextDTOList) {
                final ContextDTO cleanContextDTO = cleanContext(assayContextDTO)
                if (cleanContextDTO != null) {
                    contextListCleaned.add(cleanContextDTO)
                }
            }
            assayDto.assayContextDTOList = contextListCleaned

            //clean measure contexts
            contextListCleaned = new LinkedList<ContextDTO>()
            for (ContextDTO measureContextDTO : assayDto.measureContextDTOList) {
                final ContextDTO cleanContextDTO = cleanContext(measureContextDTO)
                if (cleanContextDTO != null) {
                    contextListCleaned.add(cleanContextDTO)
                }
            }
            assayDto.measureContextDTOList = contextListCleaned
        }
    }

    /**
     * returns a cleaned ContextDTO, unless the attribute type is not free and a value is not provided, in which case
     * returns null
     * @param contextDTO
     * @return
     */
    private ContextDTO cleanContext(ContextDTO contextDTO) {
        final ContextDTO cleanContextDTO = new ContextDTO()

        cleanContextDTO.name = contextDTO.name
        cleanContextDTO.aid = contextDTO.aid

        for (ContextItemDto attribute : contextDTO.attributes) {

            String ky = trimAndMapKey(attribute.key)

            def val = attribute.value
            if (attribute.value instanceof String) {
                //remove '| | |' prefix
                String valStr = StringUtils.split(attribute.value as String, '|').toList().last().trim()

                String matchedValue = attributeNameMapping.keySet().find { String key ->
                    return StringUtils.equalsIgnoreCase(key, valStr)}

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

            if ((attribute.attributeType != AttributeType.Free) && !val) {
                return null//Unless the attribute-type is Free, skip attributes with empty value
            }

            String concentrationUnits = trimAndMapKey(attribute.concentrationUnits)

            ContextItemDto attr = new ContextItemDto(attribute)
            attr.key = ky
            attr.value = val
            attr.concentrationUnits = concentrationUnits

            cleanContextDTO.attributes << attr
        }

        return cleanContextDTO
    }

    private String trimAndMapKey(String key) {
        String trimmedKey = StringUtils?.split(key, '|')?.toList()?.last()?.trim()

        String matchedKey = attributeNameMapping.keySet().find { String keyInMap -> return StringUtils.equalsIgnoreCase(keyInMap, trimmedKey)}
        trimmedKey = matchedKey ? attributeNameMapping.get(matchedKey) : trimmedKey
        return trimmedKey
    }
}
