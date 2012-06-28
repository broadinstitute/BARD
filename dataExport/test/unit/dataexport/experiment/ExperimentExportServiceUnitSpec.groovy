package dataexport.experiment

import bard.db.dictionary.Element
import bard.db.dictionary.Stage
import bard.db.experiment.Experiment
import bard.db.experiment.Project
import bard.db.experiment.ProjectExperiment
import bard.db.experiment.ResultContextItem
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import grails.test.mixin.Mock
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([Experiment])
class ExperimentExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ExperimentExportService experimentExportService
    final int maxNumberOfExperimentsPerPage = 2

    void setup() {
        LinkGenerator grailsLinkGenerator = Mock()
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(experimentsMediaType: "experimentsMediaType",
                    experimentMediaType: "experimentMediaType",
                    resultsMediaType: "resultsMediaType", elementMediaType: "elementMediaType")
        this.experimentExportService =
            new ExperimentExportService(mediaTypesDTO, this.maxNumberOfExperimentsPerPage)
        this.experimentExportService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate Experiments #label starting from #start"() {
        given: "A list of experiments and that the maximum list of experiments per page is ${this.maxNumberOfExperimentsPerPage} and we start from ${start}"
        when: "We attempt to generate an experiments XML document"
        Experiment.metaClass.static.findAllByReadyForExtraction = {status -> expectedExperiments }
        this.experimentExportService.generateExperiments(this.markupBuilder, start)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                            | start | expectedExperiments                                                                                                             | results
        "System has 2 experiments ready for extraction"  | 0     | [new Experiment(id: 1, readyForExtraction: 'ready'), new Experiment(id: 2, readyForExtraction: 'ready')]                        | XmlTestSamples.EXPERIMENTS_2_RECORDS_UNIT
        "System has 3 experiments ready for extraction"  | 0     | [new Experiment(id: 1), new Experiment(id: 2, readyForExtraction: 'ready'), new Experiment(id: 3, readyForExtraction: 'ready')] | XmlTestSamples.EXPERIMENTS_2_RECORDS_WITH_NEXT_UNIT
        "System has 3 experiments ready for extraction " | 2     | [new Experiment(id: 1), new Experiment(id: 2, readyForExtraction: 'ready'), new Experiment(id: 3, readyForExtraction: 'ready')] | XmlTestSamples.EXPERIMENT_SINGLE_RECORD_UNIT

    }

    void "test generate External Reference"() {
        given: "An External Reference object"
        final ExternalReference externalReference =
            new ExternalReference(extAssayRef: "External Assay Ref", project: new Project(projectName: "projectName"),
                    externalSystem: new ExternalSystem(systemUrl: "http://broad.org", systemName: "systemName", owner: "owner"))
        when: "We convert it to XML"
        this.experimentExportService.generateExternalReference(this.markupBuilder, externalReference)

        then: "We get the expected XML document"
        XmlTestAssertions.assertResults(XmlTestSamples.EXTERNAL_REFERENCE_UNT, this.writer.toString())

    }

    void "test generate External References"() {
        given: "An External References object"
        final ExternalReference externalReference =
            new ExternalReference(extAssayRef: "External Assay Ref", project: new Project(projectName: "projectName"),
                    externalSystem: new ExternalSystem(systemUrl: "http://broad.org", systemName: "systemName", owner: "owner"))
        final Set<ExternalReference> externalReferences = [] as Set<ExternalReference>
        externalReferences.add(externalReference)

        when: "We convert it to XML"
        this.experimentExportService.generateExternalReferences(this.markupBuilder, externalReferences)

        then: "We get the expected XML document"
        XmlTestAssertions.assertResults(XmlTestSamples.EXTERNAL_REFERENCES_UNT, this.writer.toString())
    }

    void "test generate Experiment #label"() {
        given: "An Experiment"
        Set<ResultContextItem> resultContextItems = [] as Set<ResultContextItem>
        Set<ProjectExperiment> projectExperiments = [] as Set<ProjectExperiment>
        final Set<ExternalReference> externalReferences = [] as Set<ExternalReference>

        if (description) {

            final ExternalReference externalReference =
                new ExternalReference(extAssayRef: "External Assay Ref", project: new Project(projectName: "projectName"),
                        externalSystem: new ExternalSystem(systemUrl: "http://broad.org", systemName: "systemName", owner: "owner"))
            externalReferences.add(externalReference)

            Element attribute = new Element(label: "attribute")
            Element valueControlled = new Element(label: "valueControlled")

            resultContextItems.add(new ResultContextItem(qualifier: "<", valueDisplay: "< 20 uM", valueNum: 1, valueMin: 5,
                    valueMax: 20, attribute: attribute, valueControlled: valueControlled))
            final Experiment precedingExperiment = new Experiment(id: 5)
            ProjectExperiment projectExperiment = new ProjectExperiment(project: new Project(id: 1),
                    precedingExperiment: precedingExperiment, description: description, stage: new Stage(id: 1, element: new Element(label: "stageLabel")))
            projectExperiments.add(projectExperiment)

        }
        final Experiment experiment =
            new Experiment(
                    experimentName: "Experiment1",
                    holdUntilDate: new Date(0),
                    runDateFrom: new Date(0),
                    runDateTo: new Date(0),
                    experimentStatus: 'Published',
                    description: description,
                    resultContextItems: resultContextItems,
                    projectExperiments: projectExperiments,
                    externalReferences: externalReferences)

        when: "We attempt to generate an experiment XML document"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)
        then: "A valid xml document is generated and is similar to the expected document"
        println this.writer.toString()
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                                                       | experimentName | description | results
        "No description, resultContextItems, externalReferences,projectExperiments" | "Experiment1"  | ""          | XmlTestSamples.EXPERIMENT_UNIT_ONLY_ATTRIBUTES
        "Full Experiment"                                                           | "Experiment2"  | "Broad"     | XmlTestSamples.EXPERIMENT_UNIT_ATTRIBUTES_AND_ELEMENTS

    }

    void "test generate Attributes For Experiment #label"() {
        given: "A experiment, "
        when: "We extract key-value pairs, needed to generate the XML attributes for an Experiment document"
        final Map<String, String> attributesForExperiment = this.experimentExportService.generateAttributesForExperiment(experiment)
        then: "We expect the resulting Map to be equal to the expected map"

        attributesForExperiment == results
        where:
        label                      | experiment                                                                                                                                                        | results
        "Experiment with dates"    | new Experiment(id: 1, experimentName: "Experiment1", holdUntilDate: new Date(0), runDateFrom: new Date(0), runDateTo: new Date(0), experimentStatus: 'Published') | [experimentId: null, experimentName: 'Experiment1', status: 'Published', holdUntilDate: '1969-12-31T19:00:00.000-05:00', runDateFrom: '1969-12-31T19:00:00.000-05:00', runDateTo: '1969-12-31T19:00:00.000-05:00'] as Map<String, String>
        "Experiment with no dates" | new Experiment(id: 2, experimentName: "Experiment2", experimentStatus: 'Published')                                                                               | [experimentId: null, experimentName: 'Experiment2', status: 'Published'] as Map<String, String>
    }

    void "test Generate Experiment Not Found Exception"() {
        given:
        Experiment.metaClass.static.get = {id -> null }
        when: "We attempt to generate an Experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

}
