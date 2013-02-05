package bard.hibernate

import bard.db.registration.Assay
import grails.plugins.springsecurity.SpringSecurityService
import org.hibernate.event.PreInsertEvent
import org.hibernate.event.PreUpdateEvent
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/13/12
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
class ModifiedByListenerUnitSpec extends Specification {
    ModifiedByListener modifiedByListener = new ModifiedByListener()

    SpringSecurityService springSecurityService = Mock()

    def setup() {
        modifiedByListener.springSecurityService = springSecurityService
    }

    def 'test onPreInsert'() {
        given: 'a username and an object with a modifiedBy property'
        String username = 'foo'
        def entityAsMap = new Assay()
        PreInsertEvent event = Mock()

        when: 'we fire the event'
        modifiedByListener.onPreInsert(event)

        then: 'the modifiedBy propertied is set to the username'
        1 * event.entity >> entityAsMap
        0 * event._
        1 * springSecurityService.getPrincipal() >> [username: username]
        0 * springSecurityService._

        entityAsMap.modifiedBy == username
    }

    def 'test onPreInsert no username'() {
        given: 'a username and an object with a modifiedBy property'
        String username = null
        def entityAsMap = new Assay()
        PreInsertEvent event = Mock()

        when: 'we fire the event'
        modifiedByListener.onPreInsert(event)

        then: 'the modifiedBy propertied is set to the username'
        1 * event.entity >> entityAsMap
        0 * event._
        1 * springSecurityService.getPrincipal() >> [username: username]
        0 * springSecurityService._

        thrown(AuthenticatedUserRequired)
    }

    def 'test onPreUpdate '() {
        given: 'a username and an object with a modifiedBy property'
        String username = 'foo'
        def entityAsMap = new Assay()
        PreUpdateEvent event = Mock()

        when: 'we fire the event'
        modifiedByListener.updateModifiedBy(event)

        then: 'the modifiedBy propertied is set to the username'
        1 * springSecurityService.getPrincipal() >> [username: username]
        0 * springSecurityService._
        1 * event.entity >> entityAsMap
        0 * event._

        entityAsMap.modifiedBy == username

    }

    def 'test onPreUpdate no username'() {
        given: 'a username and an object with a modifiedBy property'
        String username = null
        def entityAsMap = new Assay()
        PreUpdateEvent event = Mock()

        when: 'we fire the event'
        modifiedByListener.updateModifiedBy(event)

        then: 'the modifiedBy propertied is set to the username'
        1 * event.entity >> entityAsMap
        0 * event._
        1 * springSecurityService.getPrincipal() >> [username: username]
        0 * springSecurityService._

        thrown(AuthenticatedUserRequired)
    }
}