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
