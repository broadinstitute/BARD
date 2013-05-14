package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AssayType implements IEnumUserType {
    REGULAR("Regular"),
    PANEL_ARRAY("Panel - Array"),
    PANEL_GROUP('Panel - Group'),
    TEMPLATE('Template')

    final String id;

    private AssayType(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static AssayType byId(String id) {
        AssayType assayType = values().find { it.id == id }
        if (assayType) {
            return assayType
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}