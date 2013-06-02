package bard.db.registration

import bard.db.enums.ExperimentStatus
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import bard.db.project.ExperimentController
import bard.db.project.ExperimentControllerHelper
import bard.db.project.InlineEditableCommand
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before
import spock.lang.Specification

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
@Build([Assay, Experiment])
class ExperimentControllerUnitSpec extends Specification {
    @Before
    void setup() {
        controller.metaClass.mixin(ExperimentControllerHelper)

        ExperimentService experimentService = Mock(ExperimentService)
        controller.experimentService = experimentService
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
        assert response.status == HttpServletResponse.SC_CONFLICT
        assert response.text == "default.optimistic.locking.failure"
        assert response.contentType == "text/plain;charset=utf-8"
    }
    void 'test edit Experiment hold until date success'() {
        given:

        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateTo: new Date())
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
        assert responseJSON.get("data").asText() == ExperimentController.formatter.format(updatedExperiment.holdUntilDate)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment hold until date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.holdUntilDate)
        when:
        controller.editHoldUntilDate(inlineEditableCommand)
        then:
        controller.experimentService.updateHoldUntilDate(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the hold until date. null"
        assert response.contentType == "text/plain;charset=utf-8"
    }
    void 'test edit Experiment Run From Date success'() {
        given:

        Experiment newExperiment = Experiment.build(version: 0, experimentName: "My Name", runDateTo: new Date())
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
        assert responseJSON.get("data").asText() == ExperimentController.formatter.format(updatedExperiment.runDateFrom)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Run From Date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.runDateFrom)
        when:
        controller.editRunFromDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunFromDate(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the run from date. null"
        assert response.contentType == "text/plain;charset=utf-8"
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
        assert responseJSON.get("data").asText() == ExperimentController.formatter.format(updatedExperiment.runDateTo)
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Run To Date errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.runDateTo)
        when:
        controller.editRunToDate(inlineEditableCommand)
        then:
        controller.experimentService.updateRunToDate(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the run to date. null"
        assert response.contentType == "text/plain;charset=utf-8"
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

    void 'test edit Project Description with errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newExperiment.id,
                version: newExperiment.version, name: newExperiment.experimentName, value: newExperiment.description)
        when:
        controller.editDescription(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentDescription(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the experiment description. "
        assert response.contentType == "text/plain;charset=utf-8"
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
        controller.experimentService.updateExperimentName(_, _) >> { return updatedExperiment}
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedExperiment.experimentName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Experiment Name with errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0)
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName)
        when:
        controller.editExperimentName(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentName(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the experiment name. "
        assert response.contentType == "text/plain;charset=utf-8"
    }
    void 'test edit Experiment Status success'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0, experimentStatus: ExperimentStatus.DRAFT)  //no designer
        Experiment updatedExperiment =
            Experiment.build(experimentName: "My New Name", version: 1, lastUpdated: new Date(),
                experimentStatus: ExperimentStatus.APPROVED)
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
    }

    void 'test edit Experiment Status with errors'() {
        given:
        Experiment newExperiment = Experiment.build(version: 0, experimentStatus: ExperimentStatus.APPROVED)
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newExperiment.id, version: newExperiment.version, name: newExperiment.experimentName, value: ExperimentStatus.APPROVED.id)
        when:
        controller.editExperimentStatus(inlineEditableCommand)
        then:
        controller.experimentService.updateExperimentStatus(_, _) >> { throw new Exception("") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "Could not edit the experiment status. "
        assert response.contentType == "text/plain;charset=utf-8"
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
        Assay assay = Assay.build()
        ExperimentService experimentService = Mock(ExperimentService)
        controller.experimentService = experimentService

        when:
        params.assayId = assay.id
        params.experimentName = "name"
        params.description = "desc"
        params.experimentTree = "[]"
        params.experimentStatus = 'DRAFT'
        controller.save()

        then:
        1 * experimentService.updateMeasures(_, _)
        Experiment.getAll().size() == 1
        def experiment = Experiment.getAll().first()
        assert response.redirectedUrl == "/experiment/show/${experiment.id}"
    }

    def 'test show'() {
        setup:
        controller.measureTreeService = Mock(MeasureTreeService)
        controller.measureTreeService.createMeasureTree(_, _) >> []

        when:
        Experiment exp = Experiment.build()
        params.id = exp.id
        def m = controller.show()

        then:
        m.instance == exp
    }
}
