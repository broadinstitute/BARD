package maas

import bard.db.registration.AttributeType

/**
 * Holds a single attribute (a key/value pair) value.
 * This should correspond to an Element in the data model
 */
class ContextItemDto {
    String key;
    def value;
    AttributeType attributeType
    Boolean typeIn = false //defines whether or not the user can type-in a value for the Value field or does it have to come from the dictionary
    String qualifier //Used to describe the qualifier in result-type (e.g., '<')
    String concentrationUnits //Used to describe the concentration units when the attribute is a numeric concentration value.

    String assignedName   // we may assign a different name other than we get from the spreadsheet for this item

    @Override
    String toString() {
        return "$key $value $attributeType $typeIn $qualifier $concentrationUnits $assignedName"
    }
}
