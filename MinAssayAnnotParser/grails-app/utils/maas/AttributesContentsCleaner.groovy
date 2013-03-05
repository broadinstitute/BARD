package maas

import bard.db.registration.AttributeType
import bard.dm.minimumassayannotation.AssayDto
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ContextItemDto
import org.apache.commons.lang.StringUtils

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

   static void cleanDtos(List<Dto> dtos) {
       Map attributeNameMapping = AttributeNameMappingBuilder.build()
        dtos.each{Dto dto->
            List<ContextDTO> cleanedContextDTOs = []
            dto.contextDTOs.each{ContextDTO contextDTO ->
                maas.ContextDTO cleanedContextDTO = cleanContext(contextDTO, attributeNameMapping)
                if (cleanedContextDTO) {
                    cleanedContextDTOs.add(cleanedContextDTO)
                }
            }
            dto.contextDTOs = cleanedContextDTOs
        }
    }


    /**
     * returns a cleaned ContextDTO, unless the attribute type is not free and a value is not provided, in which case
     * returns null
     * @param contextDTO
     * @return
     */
   static private ContextDTO cleanContext(ContextDTO contextDTO, Map attributeNameMapping) {
        final ContextDTO cleanContextDTO = new ContextDTO()

        cleanContextDTO.name = contextDTO.name
        cleanContextDTO.aid = contextDTO.aid

        for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {

            String ky = trimAndMapKey(contextItemDto.key)

            def val = contextItemDto.value
            if (contextItemDto.value instanceof String) {
                //remove '| | |' prefix
                String valStr = StringUtils.split(contextItemDto.value as String, '|').toList().last().trim()

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

            if ((contextItemDto.attributeType != AttributeType.Free) && !val) {
                return null//Unless the attribute-type is Free, skip attributes with empty value
            }

            ContextItemDto cleanContextItemDto = new ContextItemDto(contextItemDto)
            cleanContextItemDto.key = ky
            cleanContextItemDto.value = val
            cleanContextItemDto.concentrationUnits = trimAndMapKey(contextItemDto.concentrationUnits, attributeNameMapping)
            cleanContextItemDto.qualifier = trimAndMapKey(contextItemDto.qualifier, attributeNameMapping)

            cleanContextDTO.contextItemDtoList.add(cleanContextItemDto)
        }

        return cleanContextDTO
    }

    static private String trimAndMapKey(String key, Map attributeNameMapping) {
        String trimmedKey = StringUtils?.split(key, '|')?.toList()?.last()?.trim()

        String matchedKey = attributeNameMapping.keySet().find { String keyInMap -> return StringUtils.equalsIgnoreCase(keyInMap, trimmedKey)}
        trimmedKey = matchedKey ? attributeNameMapping.get(matchedKey) : trimmedKey
        return trimmedKey
    }
}
