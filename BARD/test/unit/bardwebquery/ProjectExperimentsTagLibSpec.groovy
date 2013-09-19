package bardwebquery
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.experiment.ExperimentSearch
import grails.test.mixin.TestFor
import spock.lang.Specification
/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(ProjectExperimentsTagLib)
class ProjectExperimentsTagLibSpec extends Specification {

    void "test displayExperimentsGroupedByAssay"() {
        given:
        def template = '<g:displayExperimentsGroupedByAssay assays="${assays}" experiments="${experiments}" experimentTypes="${experimentTypes}"/>'
        Assay assay1 = new Assay(name: "Assay1", bardAssayId: 1)
        ExperimentSearch experiment1 = new ExperimentSearch(name: "Experiment1", bardExptId: 1, bardAssayId: 1, compounds: 5)
        ExperimentSearch experiment2 = new ExperimentSearch(name: "Experiment2", bardExptId: 2, bardAssayId: 1, compounds: 500000)
        Assay assay2 = new Assay(name: "Assay2", bardAssayId: 2)
        ExperimentSearch experiment3 = new ExperimentSearch(name: "Experiment3", bardExptId: 3, bardAssayId: 2, compounds: 50)

        def assays = [assay2, assay1]
        def experiments = [experiment3, experiment2, experiment1]
        Map<Long,String> experimentTypes = [:]
        experimentTypes[1] = "Confirmatory"
        experimentTypes[2] = "Primary"

        when:
        String actualResults = applyTemplate(template, [assays: assays, experiments: experiments, experimentTypes: experimentTypes])
        String trimmedResults = actualResults.stripMargin().replaceAll("\\n","").replaceAll(">\\s+",">").replaceAll("\\s+<","<")

        then:
        assert trimmedResults.contains("<table class=\"table\"><thead><tr><th rowspan=\"2\">EID</th><th rowspan=\"2\">Experiment Name</th><th rowspan=\"2\">Role</th><th colspan=\"2\"># Compounds</th><th rowspan=\"2\">ADID</th>")

    }
}