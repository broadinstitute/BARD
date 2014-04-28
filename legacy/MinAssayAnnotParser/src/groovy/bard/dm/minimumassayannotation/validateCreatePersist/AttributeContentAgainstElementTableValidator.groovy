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

package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import bard.dm.Log
import bard.dm.minimumassayannotation.ContextItemDto
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ContextLoadResultsWriter

/**
 * Make sure that all the attribute's key and value match against valid values in the Element table
 */
class AttributeContentAgainstElementTableValidator {

    private final ContextLoadResultsWriter loadResultsWriter

    public AttributeContentAgainstElementTableValidator(ContextLoadResultsWriter loadResultsWriter) {
        this.loadResultsWriter = loadResultsWriter
    }

    /**
     * Make sure that all the attribute's key and value match against valid values in the Element table
     *
     * @param contextList
     */
    void removeInvalid(List<ContextDTO> contextDTOList, Map attributeNameMapping) {

        Iterator<ContextDTO> contextDTOIterator = contextDTOList.iterator()
        while (contextDTOIterator.hasNext()) {
            ContextDTO contextDTO = contextDTOIterator.next()

            boolean isValid = true;

            for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {

                //have deliberately placed result as the second operand so that the checkForElement method
                //is run so that all elements are checked
                isValid  = checkForElement(contextItemDto.key, contextDTO) && isValid

                //Check that the value is defined in the database,
                // except for the ones that are numeric values or a type-in field or a Free-type field
                if (contextItemDto.value &&
                        (contextItemDto.value instanceof String) &&
                        !contextItemDto.typeIn &&
                        (contextItemDto.attributeType != AttributeType.Free)) {

                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    isValid  = checkForElement(contextItemDto.value, contextDTO) && isValid
                }

                //If concentration units are present check that they are defined in the database
                if (contextItemDto.concentrationUnits != null) {
                    //have deliberately placed result as the second operand so that the checkForElement method
                    //is run so that all elements are checked
                    isValid  = checkForElement(contextItemDto.concentrationUnits, contextDTO) && isValid
                }
            }

            if (!isValid) {
                contextDTOIterator.remove()
            }
        }
    }

    private boolean checkForElement(String elementLabel, ContextDTO contextDTO) {
        Element element = Element.findByLabelIlike(elementLabel)

        if (element) {
            return true;
        } else {
            final String message = "Attributes in context not found in database:  $elementLabel"
            Log.logger.error(message)
            loadResultsWriter.write(contextDTO, null, ContextLoadResultsWriter.LoadResultType.fail, null, 0, message)

            return false
        }
    }
}


