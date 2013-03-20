package bard.db.people

class Person {
    private static final int NAME_MAX_SIZE = 255
    private static final int MODIFIED_BY_MAX_SIZE = 40

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

    static constraints = {
        userName(blank: false, maxSize: NAME_MAX_SIZE)
        emailAddress(nullable: true, maxSize: NAME_MAX_SIZE)
        fullName(nullable: true, maxSize: NAME_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}