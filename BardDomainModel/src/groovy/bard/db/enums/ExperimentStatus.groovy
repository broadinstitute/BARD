package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
enum ExperimentStatus implements IEnumUserType {
    DRAFT("Draft"),
    APPROVED("Approved"),
    RETIRED("Retired")

    final String id;

    private ExperimentStatus(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static ExperimentStatus byId(String id) {
        ExperimentStatus status = values().find { it.id == id }
        if (status) {
            return status
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }
}