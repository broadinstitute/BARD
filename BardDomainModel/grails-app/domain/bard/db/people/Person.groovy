package bard.db.people

class Person {
    private static final int NAME_MAX_SIZE = 255
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String userName
    String emailAddress
    String fullName
    Boolean accountExpired
    Boolean accountLocked
    Boolean accountEnabled
    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static mapping = {
        table('PERSON')
        id(column: 'PERSON_ID', generator: 'assigned')
        version(false)
        userName(column: 'USERNAME')
    }

    static constraints = {
        userName(blank: false, maxSize: NAME_MAX_SIZE)
        emailAddress(nullable: true, maxSize: NAME_MAX_SIZE)
        fullName(nullable: true, maxSize: NAME_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}