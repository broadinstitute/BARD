package bard.hibernate

import grails.plugins.springsecurity.SpringSecurityService
import groovy.transform.InheritConstructors
import org.hibernate.event.*

/**
 * Listener to set modifiedBy for all entities modified in CAP.
 * User: ddurkin
 * Date: 9/13/12
 * Time: 1:09 PM
 */
class ModifiedByListener implements PreInsertEventListener, PreUpdateEventListener {
    SpringSecurityService springSecurityService

    boolean onPreInsert(PreInsertEvent preInsertEvent) {
        updateModifiedBy(preInsertEvent)
        return false
    }

    boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        updateModifiedBy(preUpdateEvent)
        return false
    }

    private void updateModifiedBy(AbstractPreDatabaseOperationEvent abstractEvent) {
        if (abstractEvent.hasProperty("modifiedBy")) {
            String username = springSecurityService.getPrincipal()?.username
            if (username) {
                abstractEvent.entity.modifiedBy = username
            } else {
                throw new AuthenticatedUserRequired('An authenticated user was expected this point');
            }
        }
    }
}
@InheritConstructors
class AuthenticatedUserRequired extends RuntimeException {}