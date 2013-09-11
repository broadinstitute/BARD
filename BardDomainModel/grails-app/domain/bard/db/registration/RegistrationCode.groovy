package bard.db.registration

import bard.db.experiment.Experiment

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class RegistrationCode {

    String userName
    String token = UUID.randomUUID().toString().replaceAll('-', '')
    Date dateCreated = new Date()
    Date lastUpdated = new Date()

    static mapping = {
        version false
        id(column: "REGISTRATION_CODE_ID", generator: "sequence", params: [sequence: 'REGISTRATION_CODE_ID_SEQ'])
        userName(column: 'USER_NAME')
    }
    static constraints = {
        userName(nullable: false,blank: false)
        token(nullable:false, blank: false, maxSize: Experiment.DESCRIPTION_MAX_SIZE)
    }
}
