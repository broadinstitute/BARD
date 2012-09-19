package bard.hibernate

import grails.plugins.springsecurity.SpringSecurityService
import org.hibernate.event.PreInsertEvent
import org.hibernate.event.PreInsertEventListener
import org.hibernate.event.PreUpdateEvent
import org.hibernate.event.PreUpdateEventListener

import org.hibernate.event.AbstractPreDatabaseOperationEvent
import groovy.transform.InheritConstructors

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
        String username = springSecurityService.getPrincipal()?.username
        if (username) {
            abstractEvent.entity.modifiedBy = username
        }
        else{
            throw new AuthenticatedUserRequired('An authenticated user was expected this point');
        }
    }
}
@InheritConstructors
class AuthenticatedUserRequired extends RuntimeException{}