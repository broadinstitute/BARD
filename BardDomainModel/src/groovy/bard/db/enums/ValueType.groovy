package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/8/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
enum ValueType implements IEnumUserType {
    NUMERIC("numeric"),
    ELEMENT('element'),
    EXTERNAL_ONTOLOGY('external ontology'),
    FREE_TEXT('free text'),
    NONE('none'),
    RANGE('range')

    final String id;

    private ValueType(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static ValueType byId(String id) {
        ValueType expectedValueType = values().find { it.id == id }
        if (expectedValueType) {
            return expectedValueType
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}