package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 2/12/14
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum TeamRole implements IEnumUserType {

    MEMBER('Member'),
    MANAGER('Manager')

    final String id;

    private TeamRole(String id) {
        this.id = id
    }

    @Override
    String getId() {
        return id
    }

    static TeamRole byId(String id) {
        TeamRole teamRole = values().find { it.id == id }
        if (teamRole) {
            return teamRole
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }
}