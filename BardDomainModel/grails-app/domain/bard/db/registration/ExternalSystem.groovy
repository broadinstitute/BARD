package bard.db.registration

class ExternalSystem {

    private static final int OWNER_MAX_SIZE = 128
    private static final int SYSTEM_URL_MAX_SIZE = 1000
    private static final int SYSTEM_NAME_MAX_SIZE = 128
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String systemName
    String owner
    String systemUrl
    Date dateCreated
    Date lastUpdated
    String modifiedBy

    Set<ExternalReference> externalReferences = [] as Set

    static hasMany = [externalReferences: ExternalReference]

    static mapping = {
        id(column: 'EXTERNAL_SYSTEM_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_SYSTEM_ID_SEQ'])
    }

    static constraints = {
        systemName(blank: false, maxSize: SYSTEM_NAME_MAX_SIZE)
        owner(nullable: true, blank: false, maxSize: OWNER_MAX_SIZE)
        systemUrl(nullable: true, blank: false, maxSize: SYSTEM_URL_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
