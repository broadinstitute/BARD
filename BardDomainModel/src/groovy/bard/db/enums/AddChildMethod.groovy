package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/8/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
enum AddChildMethod implements IEnumUserType {
    RDM_REQUEST('RDM request', 'Children can only be added through a request to the RDM Team',"rdm_request"),
    DIRECT('direct', 'Any user is allowed to add a child element and use the element immediately',"direct"),
    NO('no', 'No children can be added to this element',"no_children")

    final String id;
    final String description;
    final String label;

    private AddChildMethod(String id, String description, String label) {
        this.id = id
        this.description = description
        this.label = label
    }
    String getLabel(){
        return this.label
    }
    String getDescription() {
        return this.description
    }

    String getId() {
        return id
    }

    static AddChildMethod byId(String id) {
        AddChildMethod addChildMethod = values().find { it.id == id }
        if (addChildMethod) {
            return addChildMethod
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}