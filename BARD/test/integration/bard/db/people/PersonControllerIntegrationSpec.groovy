package bard.db.people

import bard.PersonController
import bard.db.audit.BardContextUtils
import bard.db.context.item.ContextItemController
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.IgnoreRest
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/15/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class PersonControllerIntegrationSpec extends IntegrationSpec {

    SessionFactory sessionFactory
    PersonController controller

    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        controller = new PersonController()
    }

    void "test list verify model contents and view"() {

        when:
        controller.list()

        then: 'verify model contents'
        controller.modelAndView != null

        def model = controller.modelAndView.model
        model != null
        model.people != null
        model.roles == Role.all
        model.peopleTotal >= 0

        model.personCommand != null
        model.personCommand.username == null
        model.personCommand.email == null
        model.personCommand.displayName == null
        model.personCommand.primaryGroup == null

        and: 'verify the view'
        controller.modelAndView.viewName.endsWith('list')
    }

    void "test edit verify model contents and view"() {
        given:
        Person person = Person.build()
        controller.params.id = person.id.toString()

        when:
        controller.edit()

        then: 'verify model contents'
        controller.modelAndView != null
        controller.modelAndView.model != null

        def model = controller.modelAndView.model
        model.person == person
        model.roles == Role.all

        model.personCommand != null
        model.personCommand.username == person.userName
        model.personCommand.email == person.emailAddress
        model.personCommand.displayName == person.fullName
        model.personCommand.primaryGroup == person.newObjectRole

        and: 'verify the view'
        controller.modelAndView.viewName.endsWith('edit')
    }

    void "test edit person desc: #desc"() {
        given:
        controller.params.id = id

        when:
        controller.edit()

        then: 'verify model contents'
        controller.response.redirectUrl.endsWith('list')
        controller.flash.message == "Person not found. Try again"

        where:
        desc              | id
        'null id'         | null
        'some unknown id' | -1000L
    }
}


