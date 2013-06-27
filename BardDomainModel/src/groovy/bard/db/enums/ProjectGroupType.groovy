package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/18/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
enum ProjectGroupType implements IEnumUserType {

    PROJECT("Project"),
    PROBE_REPORT("Probe Report"),
    CAMPAIGN("Campaign"),
    PANEL("Panel"),
    STUDY("Study"),
    TEMPLATE("Template")

    final String id;

    private ProjectGroupType(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static ProjectGroupType byId(String id) {
        ProjectGroupType projectGroupType = values().find { it.id == id }
        if (projectGroupType) {
            return projectGroupType
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }
}
