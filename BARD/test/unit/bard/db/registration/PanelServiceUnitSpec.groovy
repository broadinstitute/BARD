package bard.db.registration

import acl.CapPermissionService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Panel])
@Mock([Panel])
@TestMixin(ServiceUnitTestMixin)
@TestFor(PanelService)
public class PanelServiceUnitSpec extends Specification {

    void "test update panel name"() {
        given:
        final Panel panel = Panel.build(name: 'panelName20', capPermissionService: Mock(CapPermissionService))
        final String newPanelName = "New Panel Name"
        when:
        final Panel updatedPanel = service.updatePanelName(newPanelName,panel.id)
        then:
        assert newPanelName == updatedPanel.name
    }
}
