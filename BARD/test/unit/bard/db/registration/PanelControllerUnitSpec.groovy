package bard.db.registration

import acl.CapPermissionService
import bard.db.project.InlineEditableCommand
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 */


@TestFor(PanelController)
@Build([Assay, Panel, PanelAssay])
@Mock([Assay, Panel, PanelAssay])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class PanelControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {

    Panel panel



    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(EditingHelper)

        CapPermissionService capPermissionService = Mock(CapPermissionService)
        PanelService panelService = Mock(PanelService)
        controller.panelService = panelService
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.capPermissionService = capPermissionService
        panel = Panel.build(name: 'Test')
        assert panel.validate()
    }

    void 'test save success'() {
        given:
        PanelCommand panelCommand = new PanelCommand(name: "Some Name", springSecurityService: controller.springSecurityService)
        when:
        controller.save(panelCommand)
        then:
        assert controller.response.redirectedUrl.startsWith("/panel/show/")
    }

    void 'test save failure'() {
        given:
        PanelCommand panelCommand = new PanelCommand(springSecurityService: controller.springSecurityService)
        when:
        controller.save(panelCommand)
        then:
        assert response.status == 200
    }

    void 'test create panel success'() {
        when:
        controller.create()
        then:
        assert response.status == 200
    }

    void 'test addAssayToPanel'() {
        given:
        params.assayIds = "22"
        when:
        controller.addAssayToPanel()
        then:
        assert response.status == 200
    }


    void 'test edit Panel Name success'() {
        given:
        Panel newPanel = Panel.build(version: 0, name: "My Name")
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, modifiedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.name)
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { return updatedPanel }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedPanel.name
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Panel Name - access denied'() {
        given:
        accessDeniedRoleMock()
        Panel newPanel = Panel.build(version: 0, name: "My Name")
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, modifiedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.name)
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Panel Name with errors'() {
        given:
        Panel newPanel = Panel.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id, version: newPanel.version, name: newPanel.name, value: "Designer")
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }


    void 'test edit optimistic lock failure'() {
        given:
        Panel newPanel = Panel.build()
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newPanel.id, version: newPanel.version, name: newPanel.name, value: "Designer")
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        inlineEditableCommand.validateVersions(_, _) >> { "Some error message" }
        assertOptimisticLockFailure()
    }

    void 'test show'() {
        given:
        CapPermissionService capPermissionService = controller.capPermissionService
        when:
        params.id = panel.id
        def model = controller.show()

        then:
        capPermissionService.getOwner(_) >> { 'owner' }
        model.panelInstance == panel
    }

    void 'testFindById()'() {

        when:
        params.id = "${panel.id}"
        controller.findById()

        then:
        "/panel/show/${panel.id}" == controller.response.redirectedUrl
    }

    void 'testFindByName'() {
        when:
        params.name = panel.name
        controller.findByName()

        then:
        "/panel/show/${panel.id}" == controller.response.redirectedUrl
    }

    void 'test add assays'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        params.id = panel.id
        params.assayIds = assay.id

        when:
        def panelService = mockFor(PanelService)
        panelService.demand.associateAssays(1) { Long id, List<Assay> assays ->
            assert [assay] == assays
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.addAssays()

        then:
        assert "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test add assays - access denied'() {
        given:
        accessDeniedRoleMock()
        request.method = "POST"
        Panel panel = Panel.build(name: "name")
        Assay assay = Assay.build(assayName: 'Test')

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.addAssays()

        then:
        controller.panelService.associateAssays(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id== panel.id
        assert associatePanelCommand.assayIds==assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }

    void 'test add assay'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        params.id = panel.id
        params.assayIds = assay.id

        when:
        def panelService = mockFor(PanelService)
        panelService.demand.associateAssay(1) { Long id, Assay assay1 ->
            assert assay == assay1
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.addAssay()

        then:
        assert "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test add assay - access denied'() {
        given:
        accessDeniedRoleMock()
        request.method = "POST"
        Panel panel = Panel.build(name: "name")
        Assay assay = Assay.build(assayName: 'Test')

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.addAssay()

        then:
        controller.panelService.associateAssay(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id== panel.id
        assert associatePanelCommand.assayIds==assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }

    void 'test delete panel'() {
        when:
        panel = Panel.build(name: 'Test')
        params.id = panel.id

        def panelService = mockFor(PanelService)
        panelService.demand.deletePanel(1) { Long id ->
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.deletePanel()

        then:
        assert "/panel/list" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test delete panel - access denied'() {
        given:
        accessDeniedRoleMock()
        Panel panel = Panel.build(name: "name")

        params.id = panel.id

        when:
        controller.deletePanel()

        then:
        controller.panelService.deletePanel(_) >> { throw new AccessDeniedException("msg") }

        assertAccesDeniedErrorMessage()
    }

    void 'test remove assay'() {
        given:
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:


        def panelService = mockFor(PanelService)
        panelService.demand.disassociateAssay(1) { Assay assay1, Long id ->
            assert assay == assay1
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.removeAssay()

        then:
        response.redirectedUrl == '/panel/show/' + panel.id

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test remove assay - access denied'() {

        given:
        accessDeniedRoleMock()
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.removeAssay()

        then:
        controller.panelService.disassociateAssay(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id== panel.id
        assert associatePanelCommand.assayIds==assay.id.toString()
        assert associatePanelCommand.hasErrors()

    }

    void 'test remove assays'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:


        def panelService = mockFor(PanelService)
        panelService.demand.disassociateAssays(1) { Long id, List<Assay> assays ->
            assert [assay] == assays
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.removeAssays()

        then:
        "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test remove assays - access denied'() {

        given:
        request.method = "POST"
        accessDeniedRoleMock()
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.removeAssays()

        then:
        controller.panelService.disassociateAssays(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id== panel.id
        assert associatePanelCommand.assayIds==assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }

    void "test list"() {
        when:
        controller.list()
        then:
        view == "/panel/findByName"
    }

    void "test index"() {
        when:
        controller.index()
        then:
        "/panel/list" == response.redirectedUrl
    }
}