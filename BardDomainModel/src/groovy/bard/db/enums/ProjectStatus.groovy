package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/18/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
enum ProjectStatus implements IEnumUserType{
    DRAFT("Draft"),
    APPROVED("Approved"),
    RETIRED("Retired")

    final String id;

    private ProjectStatus(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static ProjectStatus byId(String id) {
        ProjectStatus status = values().find { it.id == id }
        if (status) {
            return status
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}
