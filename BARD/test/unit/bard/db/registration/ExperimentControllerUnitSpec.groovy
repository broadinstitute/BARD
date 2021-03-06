/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import acl.CapPermissionService
import bard.db.ContextItemService
import bard.db.ContextService
import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.enums.ExpectedValueType
import bard.db.enums.HierarchyType
import bard.db.enums.Status
import bard.db.experiment.*
import bard.db.people.Role
import bard.db.project.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/10/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ExperimentController)
@TestMixin(DomainClassUnitTestMixin)
@Build([Assay, Experiment, ExperimentFile, Role, Element, ExperimentMeasure])
@Mock([Assay, Experiment, ExperimentFile, Element, ExperimentMeasure, AssayDescriptor, AssayContextItem, AssayContext, AssayContextExperimentMeasure])
@Unroll
class ExperimentControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(EditingHelper)

        ExperimentService experimentService = Mock(ExperimentService)
        AssayDefinitionService assayDefinitionService = Mock(AssayDefinitionService)
        controller.capPermissionService = Mock(CapPermissionService)
        controller.experimentService = experimentService
        controller.assayDefinitionService = assayDefinitionService
    }

    void 'test edit optimistic lock failure'() {
        given:
        Experiment newExperiment = Experiment.build()
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName)
        when:
        controller.editExperimentName(inlineEditableCommand)
        then:
        inlineEditableCommand.validateVersions(_, _) >> { "Some error message" }
        assertOptimisticLockFailure()
    }

    void 'test edit Experiment hold until date success'() {
        given:

        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", holdUntilDate: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1,
                lastUpdated: new Date(), holdUntilDate: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(
                pk: newExperiment.id,
                version: newExperiment.version,
                name: newExperiment.experimentName,
                value: ExperimentController.inlineDateFormater.format(updatedExperiment.holdUntilDate))
        when:
        controller.editHoldUntilDate(inlineEditableCommand)
        then:
        controller.experimentService.updateHoldUntilDate(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == EditingHelper.formatter.format(updatedExperiment.holdUntilDate)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment hold until date - access denied'() {
        given:
        accessDeniedRoleMock()
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", holdUntilDate: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1,
                lastUpdated: new Date(), holdUntilDate: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(
                pk: newExperiment.id,
                version: newExperiment.version,
                name: newExperiment.experimentName,
                value: ExperimentController.inlineDateFormater.format(updatedExperiment.holdUntilDate))
        when:
        controller.editHoldUntilDate(inlineEditableCommand)
        then:
        controller.experimentService.updateHoldUntilDate(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Experiment hold until date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.holdUntilDate)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editHoldUntilDate(inlineEditableCommand)
        then:
        controller.experimentService.updateHoldUntilDate(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Experiment Run From Date success'() {
        given:

        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateFrom: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1,
                lastUpdated: new Date(), runDateFrom: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName,
                value: ExperimentController.inlineDateFormater.format(updatedExperiment.runDateFrom))
        when:
        controller.editRunFromDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunFromDate(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == EditingHelper.formatter.format(updatedExperiment.runDateFrom)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Run From Date - access denied'() {
        given:
        accessDeniedRoleMock()
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateFrom: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1,
                lastUpdated: new Date(), runDateFrom: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName,
                value: ExperimentController.inlineDateFormater.format(updatedExperiment.runDateFrom))
        when:
        controller.editRunFromDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunFromDate(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Experiment Run From Date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.runDateFrom)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editRunFromDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunFromDate(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Experiment Run To Date success'() {
        given:

        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateTo: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date(), runDateTo: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: ExperimentController.inlineDateFormater.format(updatedExperiment.runDateTo))
        when:
        controller.editRunToDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunToDate(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == EditingHelper.formatter.format(updatedExperiment.runDateTo)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Run To Date - access denied'() {
        given:
        accessDeniedRoleMock()
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateTo: new Date())
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date(), runDateTo: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: ExperimentController.inlineDateFormater.format(updatedExperiment.runDateTo))
        when:
        controller.editRunToDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunToDate(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Experiment Run To Date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.runDateTo)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editRunToDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunToDate(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Experiment Description success'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", description: "My Description")  //no designer
        Experiment updatedExperiment = Experiment.build(description: "My New Description", experimentName: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: updatedExperiment.description)
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentDescription(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedExperiment.description
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Description -access denied'() {
        given:
        accessDeniedRoleMock()
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", description: "My Description")  //no designer
        Experiment updatedExperiment = Experiment.build(description: "My New Description", experimentName: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: updatedExperiment.description)
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentDescription(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Project Description with errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.description)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentDescription(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Experiment Name success'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name")  //no designer
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: updatedExperiment.experimentName)
        when:
        controller.editExperimentName(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentName(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedExperiment.experimentName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Name - access denied'() {
        given:
        accessDeniedRoleMock()
        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name")  //no designer
        Experiment updatedExperiment = Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date())
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: updatedExperiment.experimentName)
        when:
        controller.editExperimentName(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Experiment Name with errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editExperimentName(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Experiment Status success #desc'() {

        given:
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: true)
        Assay assay = Assay.build(assayStatus: status)
        Experiment newExperiment = Experiment.build(assay: assay, version: 0,
                experimentFiles: [ExperimentFile.build()],
                experimentStatus: Status.DRAFT, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        Experiment updatedExperiment =
                Experiment.build(assay: assay, experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                        experimentStatus: status,
                        experimentFiles: [ExperimentFile.build()],
                        experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: updatedExperiment.experimentStatus.id)
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentStatus(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedExperiment.experimentStatus.id
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"

        where:
        desc                                    | status
        "With Approved Assay and Experiment"    | Status.APPROVED
        "With Provisional Assay and Experiment" | Status.PROVISIONAL
    }

    void 'test edit Experiment Status - experiment has no results #desc'() {

        given:
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: true)
        Assay assay = Assay.build(assayStatus:status)
        Experiment newExperiment = Experiment.build(assay: assay, version: 0,
                experimentStatus: Status.DRAFT, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        Experiment updatedExperiment =
                Experiment.build(assay: assay, experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                        experimentStatus: status,
                        experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: updatedExperiment.experimentStatus.id)
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "You must upload results for this experiment before it can be approved."
        where:
        desc                                    | status
        "With Approved Assay and Experiment"    | Status.APPROVED
        "With Provisional Assay and Experiment" | Status.PROVISIONAL
    }

    void 'test edit Experiment Status - Assay Definition not approved'() {

        given:
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: true)
        Assay assay = Assay.build(assayStatus: Status.DRAFT)
        Experiment newExperiment = Experiment.build(assay: assay, version: 0, experimentStatus: Status.DRAFT, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        Experiment updatedExperiment =
                Experiment.build(assay: assay, experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                        experimentStatus: Status.APPROVED, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: updatedExperiment.experimentStatus.id)
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentStatus(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "The assay definition (ADID:${assay.id} for this experiment must be marked Approved before this experiment can be marked Approved."
    }

    void 'test edit Experiment Status - access denied'() {
        given:
        accessDeniedRoleMock()
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: true)
        Assay assay = Assay.build(assayStatus: Status.APPROVED)


        Experiment newExperiment = Experiment.build(assay: assay, version: 0, experimentStatus: Status.DRAFT,
                experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>, experimentFiles: [ExperimentFile.build()])  //no designer
        Experiment updatedExperiment =
                Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                        experimentFiles: [ExperimentFile.build()],
                        experimentStatus: Status.APPROVED, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: updatedExperiment.experimentStatus.id)
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentStatus(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test experimentStatus only has Approved and Retired '() {
        when:
        controller.experimentStatus()

        then:
        final String responseAsString = response.contentAsString
        responseAsString == '["Approved","Provisional","Retired"]'
    }

    void 'test edit Experiment Status with errors'() {
        given:
        Assay assay = Assay.build(assayStatus: Status.APPROVED)

        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: true)
        Experiment newExperiment = Experiment.build(assay: assay, experimentFiles: [ExperimentFile.build()], version: 0, experimentStatus: Status.APPROVED, experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName, value: Status.APPROVED.id)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentStatus(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit owner Role success'() {
        given:
        final Role oldRole = Role.build(authority: "ROLE_TEAM_A", displayName: "ROLE TEAM A")
        final Role newRole = Role.build(authority: "ROLE_TEAM_B", displayName: "ROLE TEAM B")

        Experiment newExperiment = Experiment.build(version: 0, ownerRole: oldRole)  //no designer
        Experiment updatedExperiment =
                Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                        ownerRole: newRole)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: newRole.displayName)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [newRole]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.experimentService.updateOwnerRole(_, _) >> { return updatedExperiment }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedExperiment.ownerRole.displayName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Owner Role - access denied'() {
        given:
        accessDeniedRoleMock()
        final Role oldRole = Role.build(authority: "ROLE_TEAM_A", displayName: "ROLE TEAM A")
        final Role newRole = Role.build(authority: "ROLE_TEAM_B", displayName: "ROLE TEAM B")
        Experiment newExperiment = Experiment.build(version: 0, ownerRole: oldRole)  //no designer

        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id,
                        version: newExperiment.version, name: newExperiment.experimentName,
                        value: newRole.displayName)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [newRole]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.experimentService.updateOwnerRole(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }


    void 'test edit Experiment owner role with errors'() {

        given:
        final Role newRole = Role.build(authority: "ROLE_TEAM_A", displayName: "ROLE TEAM A")
        Experiment newExperiment = Experiment.build(version: 0, ownerRole: newRole)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName, value: newRole.displayName)
        controller.metaClass.message = { Map p -> return "foo" }
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [newRole]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.experimentService.updateOwnerRole(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    def 'test create'() {
        setup:
        controller.measureTreeService = Mock(MeasureTreeService)
        controller.measureTreeService.createMeasureTree(_, _) >> []

        when:
        Assay assay = Assay.build()
        params.assayId = assay.id
        controller.create()

        then:
        response.status == 200
    }


    def 'test save'() {
        setup:
        Role role = Role.build(authority: "ROLE_TEAM_A", displayName: "Display")
        Assay assay = Assay.build()
        Element element = Element.build()

        ExperimentService experimentService = Mock(ExperimentService)
        controller.experimentService = experimentService
        params.experimentTree = "[]"
        ExperimentCommand experimentCommand =
                new ExperimentCommand(assayId: assay.id, experimentName: "name", description: "desc", ownerRole: role.authority,substanceElementValue:element)

        experimentCommand.springSecurityService = Mock(SpringSecurityService)
        experimentCommand.contextItemService = Mock(ContextItemService)
        experimentCommand.contextService = Mock(ContextService)
        Element.metaClass.'static'.findByIdOrLabel ={ Long id, String label->
            return element

        }
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:

        controller.save(experimentCommand)

        then:
        Experiment.getAll().size() == 1
        def experiment = Experiment.getAll().first()
        assert response.redirectedUrl == "/experiment/show/${experiment.id}"
    }


    def 'test show'() {
        setup:
        CapPermissionService capPermissionService = Mock(CapPermissionService)
        controller.capPermissionService = capPermissionService
        controller.measureTreeService = Mock(MeasureTreeService)
        controller.measureTreeService.createMeasureTree(_, _) >> []

        when:
        Experiment exp = Experiment.build()
        params.id = exp.id
        def m = controller.show()

        then:
        capPermissionService.getOwner(_) >> { 'owner' }
        m.instance == exp
    }


    def 'test show - fail #label'() {
        given:
        params.id = id
        when:
        controller.show()
        then:
        assert flash.message == message
        where:
        label                        | id    | message
        "No Experiment Id"           | null  | "Experiment ID is required!"
        "Non Existing Experiment ID" | 10000 | "default.not.found.message"
    }

    void 'test create result type'() {
        when:
        Experiment experiment = Experiment.build()
        params.experimentId = experiment.id
        controller.addResultTypes()
        then:
        assert view == "/experiment/createResultTypes"
        assert model.resultTypeCommand
        assert !model.currentExperimentMeasures
    }

    void 'test create dose result type'() {
        when:
        Experiment experiment = Experiment.build()
        params.experimentId = experiment.id
        controller.addDoseResultTypes()
        then:
        assert view == "/experiment/createDoseResultTypes"
        assert model.resultTypeCommand
        assert !model.currentExperimentMeasures
    }

    void 'test save result type'() {
        given:
        final Element responseResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM
        ExperimentService experimentService = controller.experimentService
        Long experimentId = experiment.id
        Map m = [
                experimentService: experimentService,
                experimentId: experiment.id,
                resultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        ResultTypeCommand resultTypeCommand = new ResultTypeCommand(m)
        when:

        controller.saveResultType(resultTypeCommand)
        then:

        1 * experimentService.createNewExperimentMeasure(_, _, _, _, _, _, _) >> {
            new ExperimentMeasure(experiment: experiment,
                    priorityElement: false, resultType: responseResultType,
                    parent: parentExperimentMeasure, parentChildRelationship: hierarchyType,
                    statsModifier: statsModifier)
        }
        assert response.redirectedUrl == "/experiment/show/${experimentId}#result-type-header"
    }

    void 'test save dose result type'() {
        given:
        final Element concentrationResultType = Element.build()
        final Element responseResultType = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        final Experiment experiment = Experiment.build()
        final Element statsModifier = Element.build()
        final ExperimentMeasure parentExperimentMeasure = ExperimentMeasure.build()
        final HierarchyType hierarchyType = HierarchyType.CALCULATED_FROM
        ExperimentService experimentService = controller.experimentService
        Long experimentId = experiment.id
        Map m = [ontologyDataAccessService: Mock(OntologyDataAccessService),
                experimentService: experimentService,
                experimentId: experiment.id,
                concentrationResultTypeId: concentrationResultType.id,
                responseResultTypeId: responseResultType.id,
                statsModifierId: statsModifier.id,
                parentExperimentMeasureId: parentExperimentMeasure.id,
                parentChildRelationship: hierarchyType.id
        ]
        DoseResultTypeCommand doseResultTypeCommand = new DoseResultTypeCommand(m)
        when:

        controller.saveDoseResultType(doseResultTypeCommand)
        then:
        5 * experimentService.createNewExperimentMeasure(_, _, _, _, _, _, _) >> {
            new ExperimentMeasure(experiment: experiment,
                    priorityElement: false, resultType: responseResultType,
                    parent: parentExperimentMeasure, parentChildRelationship: HierarchyType.SUPPORTED_BY,
                    statsModifier: statsModifier)
        }
        assert response.redirectedUrl == "/experiment/show/${experimentId}#result-type-header"
    }

    void "test edit Measure"() {
        given:
        Experiment experiment = Experiment.build()
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment)
        params.measureId = experimentMeasure.id
        params.experimentId = experiment.id
        when:
        controller.editMeasure()

        then:
        assert view == "/experiment/createResultTypes"
        assert model.resultTypeCommand
        assert model.currentExperimentMeasures
    }

    void "test delete Measure"() {
        given:
        ExperimentService experimentService = controller.experimentService
        Experiment experiment = Experiment.build()
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment)
        when:
        controller.deleteMeasure(experimentMeasure.id, experiment.id)
        then:
        experimentService.deleteExperimentMeasure(_, _) >> {}
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);
        assert responseJSON.link.toString() == experiment.id.toString()

    }

}
