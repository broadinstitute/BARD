package bard.db.context.item

import bard.db.audit.BardContextUtils
import bard.db.project.ProjectContextItem
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 5/24/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
class ContextItemControllerIntegrationSpec extends IntegrationSpec {


    ContextItemController controller

    ProjectContextItem contextItem
    SessionFactory sessionFactory

    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        BardContextUtils.setBardContextUsername( sessionFactory.currentSession,'integrationTestUser')
        controller = new ContextItemController()
    }

    void "test update"(){
        given:
        ProjectContextItem contextItem = ProjectContextItem.build()

        controller.params.contextOwnerId = contextItem.context?.owner?.id
        controller.params.contextId = contextItem.context?.id
        controller.params.contextItemId = contextItem.id

        controller.params.version = contextItem.version
        controller.params.contextClass = 'ProjectContext'
        controller.params.attributeElementId = '3'
        controller.params.valueNum = '3.0e-3'
        controller.params.qualifier = '= '

        when:
        controller.update()

        then:
        controller.modelAndView.model.instance.contextId == contextItem.id


    }
}
