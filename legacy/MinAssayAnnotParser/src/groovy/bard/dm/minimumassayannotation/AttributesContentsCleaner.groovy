/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
            cleanContextItemDto.concentrationUnits = trimAndMapKey(contextItemDto.concentrationUnits)
            cleanContextItemDto.qualifier = trimAndMapKey(contextItemDto.qualifier)

            cleanContextDTO.contextItemDtoList.add(cleanContextItemDto)
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
