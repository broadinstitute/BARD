package dataexport.experiment

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContextItem
import bard.db.project.Project
import bard.db.project.ProjectStep
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ExperimentExportService)
@Build([Experiment, Assay, ExternalReference, ExternalSystem])
class ExperimentExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ExperimentExportService experimentExportService
    final int maxNumberOfExperimentsPerPage = 2

    void setup() {
        this.experimentExportService = this.service
        LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)

        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(
                    experimentsMediaType: "experimentsMediaType",
                    experimentMediaType: "experimentMediaType",
                    resultsMediaType: "resultsMediaType",
                    elementMediaType: "elementMediaType",
                    projectMediaType: "projectMediaType",
                    stageMediaType: "stageMediaType",
                    externalReferenceMediaType: "externalReferenceMediaType",
                    assayMediaType: "assayMediaType"
            )
        this.experimentExportService.mediaTypeDTO = mediaTypesDTO
        this.experimentExportService.numberRecordsPerPage = maxNumberOfExperimentsPerPage
        this.experimentExportService.resultExportService = Mock(ResultExportService.class)
        this.experimentExportService.grailsLinkGenerator = grailsLinkGenerator
        this.experimentExportService.resultExportService.grailsLinkGenerator = grailsLinkGenerator
        this.experimentExportService.resultExportService.mediaTypes = mediaTypesDTO
        this.experimentExportService.resultExportService.maxResultsRecordsPerPage = maxNumberOfExperimentsPerPage
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test Generate Experiment Links #label"() {
        given: "An Experiment"
        final Experiment experiment = Experiment.build()
        ExternalReference externalReference = ExternalReference.build(experiment: experiment)

        //new Experiment(id: 1, assay: new Assay(id: 1))
        when: "We call the service method to generate links for the experiment"
        this.markupBuilder.root() {
            this.experimentExportService.generateExperimentLinks(this.markupBuilder, experiment)
        }
        then: "The generated XML is the similar to the expected XML"
        XmlTestAssertions.assertResults(XmlTestSamples.EXPERIMENTS_LINK_MINIMAL, this.writer.toString())
    }

    void "test generate Experiment #label"() {
        given: "An Experiment"
        Set<ExperimentContextItem> experimentContextItems = [] as Set<ExperimentContextItem>
        Set<ProjectStep> projectSteps = [] as Set<ProjectStep>
        final Set<ExternalReference> externalReferences = [] as Set<ExternalReference>

        if (description) {

            final ExternalReference externalReference =
                new ExternalReference(extAssayRef: "External Assay Ref", project: new Project(projectName: "projectName"),
                        externalSystem: new ExternalSystem(systemUrl: "http://broad.org", systemName: "systemName", owner: "owner"))
            externalReferences.add(externalReference)

            Element attributeElement = new Element(label: "attribute")
            Element valueElement = new Element(label: "valueControlled")

            experimentContextItems.add(new ExperimentContextItem(qualifier: "<", valueDisplay: "< 20 uM", valueNum: 1, valueMin: 5,
                    valueMax: 20, attributeElement: attributeElement, valueElement: valueElement))
            final Experiment precedingExperiment = new Experiment(id: 5)
            ProjectStep projectStep = new ProjectStep(project: new Project(id: 1),
                    precedingExperiment: precedingExperiment, description: description
            )
            projectSteps.add(projectStep)

        }
        final Experiment experiment =
            new Experiment(
                    experimentName: "Experiment1",
                    holdUntilDate: new Date(0),
                    runDateFrom: new Date(0),
                    runDateTo: new Date(0),
                    experimentStatus: 'Published',
                    description: description,
                    experimentContextItems: experimentContextItems,
                    projectSteps: projectSteps,
                    externalReferences: externalReferences)

        when: "We attempt to generate an experiment XML document"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                | experimentName | description | results
        "Minimal Experiment" | "Experiment1"  | ""          | XmlTestSamples.EXPERIMENT_UNIT_ONLY_ATTRIBUTES
        "Full Experiment"    | "Experiment2"  | "Broad"     | XmlTestSamples.EXPERIMENT_FULL

    }

    void "test generate Attributes For Experiment #label"() {
        given:
        Experiment experiment = valueUnderTest.call()
        when: "We extract key-value pairs, needed to generate the XML attributes for an Experiment document"
        def attributesForExperiment =
            this.experimentExportService.generateAttributesForExperiment(experiment)
        then: "We expect the resulting Map to be equal to the expected map"
        attributesForExperiment.experimentId.toString() == results.experimentId.toString()
        attributesForExperiment.experimentName == results.experimentName
        attributesForExperiment.status == results.status
        attributesForExperiment.readyForExtraction == results.readyForExtraction

        where:
        label                  | valueUnderTest                                    | results
        "Experiment - Minimal" | {Experiment.build(experimentName: "Experiment2")} | [experimentId: 1, status: 'Pending', readyForExtraction: 'Pending']
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
