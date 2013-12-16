package bard.db.people

import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.security.core.GrantedAuthority

class Role implements GrantedAuthority, Comparable<Role> {
    static final int DISPLAY_NAME_SIZE = 255
    static final int AUTHORITY_NAME_SIZE = 40

    String authority
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    String displayName

    boolean equals(Object other) {
        if (other instanceof Role) {
            Role that = (Role) other
            return that?.authority == this?.authority
        }
        return false
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        return builder.append(this.authority).toHashCode()
    }

    int compareTo(Role that) {
        return this.authority.compareTo(that.authority)
    }

    static List<Role> getTeamRoles() {
        List<Role> selectedRoles = []
        for (Role role : Role.list()) {
            if (role.authority.startsWith("ROLE_TEAM_")) {
                selectedRoles.add(role)
            }
        }
        return selectedRoles
    }

    static transients = ['teamRoles']
    static constraints = {
        authority(nullable: false, blank: false, unique: true, maxSize: AUTHORITY_NAME_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)
        displayName(nullable: true, blank: false, maxSize: DISPLAY_NAME_SIZE)

    }
    static mapping = {
        table('ROLE')
        id(column: 'ROLE_ID', generator: "sequence", params: [sequence: 'ROLE_ID_SEQ'])
        displayName(column: 'DISPLAY_NAME')
        cache true
    }


}
