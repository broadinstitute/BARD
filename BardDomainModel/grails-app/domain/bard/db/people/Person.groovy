package bard.db.people

import org.apache.commons.lang.StringUtils

class Person {
    public static final int NAME_MAX_SIZE = 255
    public static final int MODIFIED_BY_MAX_SIZE = 40

    String userName
    String emailAddress
    String fullName
    boolean accountExpired
    boolean accountLocked
    boolean accountEnabled
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static mapping = {
        table('PERSON')
        id(column: 'PERSON_ID', generator: "sequence", params: [sequence: 'PERSON_ID_SEQ'])
        version(false)
        userName(column: 'USERNAME')
    }
    static transients = ["rolesAsList", "displayName"]

    static constraints = {
        userName(blank: false, maxSize: NAME_MAX_SIZE)
        emailAddress(nullable: true, maxSize: NAME_MAX_SIZE, validator: {
            if(it != null && it.toLowerCase() != it.toLowerCase()) {
                return 'email.not.lowercase'
            }
        })
        fullName(nullable: true, blank: false, maxSize: NAME_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    String getDisplayName(){
        if(fullName)
            return fullName.contains('@') ? StringUtils.substringBefore(fullName,'@') : fullName
        else
            return !emailAddress ? userName : StringUtils.substringBefore(emailAddress, '@')
    }

    Set<Role> getRoles() {

        PersonRole.withTransaction {  //see http://jira.grails.org/browse/GRAILS-8450
            return PersonRole.findAllByPerson(this).collect { it.role } as Set
        }
    }

    String getRolesAsList() {
        List<String> displayNames = []
        for (Role role : getRoles()) {
            displayNames.add(role.displayName)
        }
        return displayNames.join(",")
    }
}