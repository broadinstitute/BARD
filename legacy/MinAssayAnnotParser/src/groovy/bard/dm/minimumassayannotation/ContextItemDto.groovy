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

import bard.db.registration.AttributeType

/**
 * Holds a single attribute (a key/value pair) value.
 * This should correspond to an Element in the data model
 */
class ContextItemDto {
    String key;
    def value;
    AttributeType attributeType
    Boolean typeIn //defines whether or not the user can type-in a value for the Value field or does it have to come from the dictionary
    String qualifier //Used to describe the qualifier in result-type (e.g., '<')
    String concentrationUnits //Used to describe the concentration units when the attribute is a numeric concentration value.

    public ContextItemDto(String key, def value, AttributeType attributeType, Boolean typeIn = false,
                          String qualifier = null, String concentrationUnits = null) {
        this.key = key
        this.value = value
        this.attributeType = attributeType
        this.typeIn = typeIn
        this.qualifier = qualifier
        this.concentrationUnits = concentrationUnits
    }

    public ContextItemDto(ContextItemDto contextItemDto) {
        this.key = contextItemDto.key
        this.value = contextItemDto.value
        this.attributeType = contextItemDto.attributeType
        this.typeIn = contextItemDto.typeIn
        this.qualifier = contextItemDto.qualifier
        this.concentrationUnits = contextItemDto.concentrationUnits
    }

    @Override
    String toString() {
        return "$key $value $attributeType $typeIn $qualifier $concentrationUnits"
    }
}
