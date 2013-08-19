package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.validation.ValidationException
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 */


@TestFor(SplitAssayDefinitionController)
@Build([Assay, Experiment])
@Mock([Assay, Experiment])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class SplitAssayDefinitionControllerUnitSpec extends Specification {
    @Shared
    Assay assay
    @Shared
    Experiment experiment1

    @Shared
    Experiment experiment2

    @Shared
    Experiment experiment3

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.experimentService = Mock(ExperimentService)
        experiment1 = Experiment.build()
        experiment2 = Experiment.build()
        experiment3 = Experiment.build()
        Assay.build(assayName: 'Test2', experiments: [experiment3])
        assay = Assay.build(assayName: 'Test1', experiments: [experiment1, experiment2])
    }

    void 'test index'() {
        when:
        controller.index()
        then:
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectedUrl == "/splitAssayDefinition/show"
    }

    void 'test show'() {
        when:
        controller.show()
        then:
        assert response.status == HttpServletResponse.SC_OK
    }

    void 'test selectExperimentsToMove success - #desc'() {
        given:
        SplitAssayCommand splitAssayCommand = new SplitAssayCommand(assayId: assay.id)
        when:
        controller.selectExperimentsToMove(splitAssayCommand)
        then:
        assert response.status == expectedHttpResponse
        assert response.text.contains("Select one or more Experiments to move to new Assay")
        where:
        desc                | expectedHttpResponse
        "Existing Assay Id" | HttpServletResponse.SC_OK
    }

    void 'test selectExperimentsToMove exceptions - #desc'() {
        given:
        SplitAssayCommand splitAssayCommand = new SplitAssayCommand(assayId: assayId)
        when:
        controller.selectExperimentsToMove(splitAssayCommand)
        then:
        assert response.status == expectedHttpResponse
        assert response.text.contains(errorMessage)
        where:
        desc                 | expectedHttpResponse               | assayId | errorMessage
        "No Assay Id"        | HttpServletResponse.SC_BAD_REQUEST | null    | "Assay Id is required"
        "Assay Id not found" | HttpServletResponse.SC_BAD_REQUEST | 233     | "Assay Id 233 cannot be found"
    }

    void 'test splitExperiments exceptions - #desc'() {
        given:
        SplitExperimentCommand splitExperimentCommand = new SplitExperimentCommand(assay: assay, experimentIds: experimentIds)
        when:
        controller.splitExperiments(splitExperimentCommand)
        then:
        assert response.status == expectedHttpResponse
        assert response.text.contains(errorMessage)
        where:
        desc                                   | expectedHttpResponse               | experimentIds    | errorMessage
        "No ExperimentIds"                     | HttpServletResponse.SC_BAD_REQUEST | null             | "Select at least one experiment to move to new assay definition"
        "No Experiments"                       | HttpServletResponse.SC_BAD_REQUEST | [233]            | " Could not find experiments with ids [233]"
        "Experiment not associated with assay" | HttpServletResponse.SC_BAD_REQUEST | [experiment3.id] | "Could not split assay ${assay.id}"
    }

    void 'test splitExperiments success - #desc'() {
        given:
        SplitExperimentCommand splitExperimentCommand = new SplitExperimentCommand(assay: assay, experimentIds: [experiment1.id, experiment2.id])
        when:
        controller.splitExperiments(splitExperimentCommand)
        then:
        controller.experimentService.splitExperimentsFromAssay(_, _) >> { return new Assay(id: 8) }
        assert response.status == HttpServletResponse.SC_OK
        assert response.text.contains("Successfully moved the following experiments from ${assay.id} - ${assay.assayName}")
    }

}