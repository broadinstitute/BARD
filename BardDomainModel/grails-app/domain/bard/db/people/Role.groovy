package bard.db.people

import org.springframework.security.core.GrantedAuthority

class Role implements GrantedAuthority{

    String authority
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    String displayName


    static List<Role> getTeamRoles() {
        List<Role> selectedRoles = []
        for(Role role:Role.list()){
            if(role.authority.startsWith("ROLE_TEAM_")){
                selectedRoles.add(role)
            }
        }
        return selectedRoles
    }

    static transients = ['teamRoles']
    static constraints = {
        authority(nullable: false, blank: false, unique: true, maxSize: Person.NAME_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)
        displayName(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)

    }
    static mapping = {
        table('ROLE')
        id(column: 'ROLE_ID', generator: "sequence", params: [sequence: 'ROLE_ID_SEQ'])
        displayName(column: 'DISPLAY_NAME')
        cache true
    }

}
