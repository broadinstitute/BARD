package bard.db.context.item

import bard.db.ContextItemService
import bard.db.audit.BardContextUtils
import bard.db.project.ProjectContextItem
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 5/24/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ContextItemControllerIntegrationSpec extends IntegrationSpec {


    ContextItemController controller

    ProjectContextItem contextItem
    SessionFactory sessionFactory
    ContextItemService contextItemService
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        controller = new ContextItemController()
    }

    void "test update number handling #desc"() {
        given:
        ProjectContextItem contextItem = ProjectContextItem.build()
        // unclear what is going on with the build method that requires a flush
        // after the save.
        // if the flush does not happen, then it occurs in the service call which causes a version mismatch
        ProjectContextItem.withSession { session ->
            session.flush()
        }
        BasicContextItemCommand contextItemCommand =
            new BasicContextItemCommand(attributeElementId: contextItem.attributeElement.id, valueNum: valueNumParam,qualifier: '= ')

        setContextItemRelatedParams(contextItem,contextItemCommand)

          when:
        controller.update(contextItemCommand)

        then:
        controller.modelAndView.model.instance.contextItem == contextItem
        controller.modelAndView.model.instance.valueNum == expectedValueNum

        where:
        desc | valueNumParam | expectedValueNum
        ''   | '3.5e-3'      | '0.0035'
        ''   | '3.5E-3'      | '0.0035'
        ''   | '0.0035'      | '0.0035'
        ''   | '0.0012'      | '0.0012'
        ''   | '0.1234567'   | '0.1234567'
        ''   | '0.3'         | '0.3'
        ''   | '0.000003'    | '0.000003'
        ''   | '0.000003'    | '0.000003'


    }

    void "test delete"() {
        given:
        ProjectContextItem contextItem = ProjectContextItem.build()
        final Long id = contextItem.id

        BasicContextItemCommand contextItemCommand =
            new BasicContextItemCommand()

        setContextItemRelatedParams(contextItem,contextItemCommand)

        when:
        controller.delete(contextItemCommand)

        then:
        ProjectContextItem.findById(id) == null

    }

    private void setContextItemRelatedParams(ProjectContextItem contextItem,BasicContextItemCommand contextItemCommand) {
        contextItemCommand.contextOwnerId = contextItem.context?.owner?.id
        contextItemCommand.contextId = contextItem.context?.id
        contextItemCommand.contextItemId = contextItem.id
        contextItemCommand.version = contextItem.version
        contextItemCommand.contextClass = 'ProjectContext'
        contextItemCommand.contextItemService = contextItemService
    }


}
