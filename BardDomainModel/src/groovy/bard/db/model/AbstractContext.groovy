package bard.db.model

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContext {
        private static final int CONTEXT_NAME_MAX_SIZE = 128
        private static final int CONTEXT_GROUP_MAX_SIZE = 256
        private static final int MODIFIED_BY_MAX_SIZE = 40

        String contextName
        String contextGroup

        Date dateCreated = new Date()
        Date lastUpdated
        String modifiedBy

        static constraints = {
            contextName(nullable: true, blank: false, maxSize: CONTEXT_NAME_MAX_SIZE)
            contextGroup(nullable: true, blank: false, maxSize: CONTEXT_GROUP_MAX_SIZE)

            dateCreated(nullable: false)
            lastUpdated(nullable: true)
            modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
        }
}
