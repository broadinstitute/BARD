package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.Result
import bard.db.registration.Assay
import common.tests.XmlTestAssertions
import dataexport.registration.BardHttpResponse
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.hibernate.SessionFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static bard.db.enums.ReadyForExtraction.*
import static common.tests.XmlTestSamples.EXPERIMENTS_NONE_READY
import static common.tests.XmlTestSamples.EXPERIMENTS_ONE_READY
import static javax.servlet.http.HttpServletResponse.*



@Unroll
class ExperimentExportServiceIntegrationSpec extends IntegrationSpec {
    ExperimentExportService experimentExportService
    Writer writer
    MarkupBuilder markupBuilder
    SessionFactory sessionFactory
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def grailsApplication
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/experimentSchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

        TestDataConfigurationHolder.reset()
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
        this.resetSequenceUtil.resetSequence('EXPERIMENT_ID_SEQ')
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Generate Experiments #label"() {
        given:
        Assay assay = Assay.build(capPermissionService: null)
        for (ReadyForExtraction rfe in readyForExtractionList) {
            Experiment.build(readyForExtraction: rfe, capPermissionService: null, assay: assay)
        }

        when:
        this.experimentExportService.generateExperiments(this.markupBuilder, 0)

        then:
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(expectedXml, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label                       | readyForExtractionList                | expectedXml
        "no experiments"            | []                                    | EXPERIMENTS_NONE_READY
        "one experiment ready"      | [READY]                               | EXPERIMENTS_ONE_READY
        "only one experiment Ready" | [READY, NOT_READY, STARTED, COMPLETE] | EXPERIMENTS_ONE_READY
    }

    void "test update #label"() {
        given: "Given an Experiment with id #id and version #version"
        Assay assay = Assay.build(capPermissionService: null)
        Experiment experiment = Experiment.build(readyForExtraction: initialReadyForExtraction, assay: assay,capPermissionService: null)
        numResults.times { Result.build(readyForExtraction: READY, experiment: experiment) }

        when: "We call the experiment service to update this experiment"
        final BardHttpResponse bardHttpResponse = this.experimentExportService.update(experiment.id, version, ReadyForExtraction.COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Experiment.get(experiment.id).readyForExtraction == expectedReadyForExtraction

        where:
        label                                                | expectedStatusCode     | expectedETag | version | numResults | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                               | SC_OK                  | 1            | 0       | 0          | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                         | SC_CONFLICT            | 0            | -1      | 0          | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"              | SC_PRECONDITION_FAILED | 0            | 2       | 0          | READY                     | READY
        "Return OK and ETag 0, Already completed Experiment" | SC_OK                  | 0            | 0       | 0          | COMPLETE                  | COMPLETE
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Experiment"

        when: "We call the experiment service to update this experiment"
        this.experimentExportService.update(new Long(100000), 0, ReadyForExtraction.COMPLETE)

        then: "An exception is thrown, indicating that the experiment does not exist"
        thrown(NotFoundException)
    }

    void "test generate and validate Experiment with id"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.build(readyForExtraction: READY,assay:Assay.build(capPermissionService:null))

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment.id)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.validate(schemaResource, actualXml)
    }

    void "test generate and validate Experiment"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.build(readyForExtraction: READY,assay :Assay.build(capPermissionService:null))

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.validate(schemaResource, actualXml)
    }

    void "test generate and validate Experiments"() {
        given: "Given that 2 experiments exists in the database"


        when: "A service call is made to generate the experiments"
        this.experimentExportService.generateExperiments(this.markupBuilder, 0)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.validate(schemaResource, actualXml)
    }


}
