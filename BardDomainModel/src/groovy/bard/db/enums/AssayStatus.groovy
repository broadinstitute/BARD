package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AssayStatus implements IEnumUserType {
    DRAFT("Draft"),
    WITNESSED("Witnessed"),
    FINISHED("Finished"),
    MEASURES_DONE("Measures Done"),
    ANNOTATIONS_DONE("Annotations Done"),
    RETIRED("Retired")

    final String id;

    private AssayStatus(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static AssayStatus byId(String id) {
        AssayStatus assayStatus = values().find { it.id == id }
        if (assayStatus) {
            return assayStatus
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}