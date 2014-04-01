package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/18/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
enum Status implements IEnumUserType, ICommonStatusIds{
    DRAFT(DRAFT_ID),
    APPROVED(APPROVED_ID),
    RETIRED(RETIRED_ID),
    PROVISIONAL(PROVISIONAL_ID)

    final String id;

    private Status(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static Status byId(String id) {
        Status status = values().find { it.id == id }
        if (status) {
            return status
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}
