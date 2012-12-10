package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.Result
import common.tests.XmlTestAssertions
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.Resource
import spock.lang.Unroll

import static bard.db.enums.ReadyForExtraction.Complete
import static bard.db.enums.ReadyForExtraction.Ready
import static javax.servlet.http.HttpServletResponse.*
import org.springframework.core.io.FileSystemResource

@Unroll
class ExperimentExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_EXPERIMENT_EXPORT_SCHEMA = "src/java/experimentSchema.xsd"

    ExperimentExportService experimentExportService
    Writer writer
    MarkupBuilder markupBuilder
    def grailsApplication
    Resource schemaResource = new FileSystemResource(new File(BARD_EXPERIMENT_EXPORT_SCHEMA))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update #label"() {
        given: "Given an Experiment with id #id and version #version"
        Experiment experiment = Experiment.build(readyForExtraction: initialReadyForExtraction)
        numResults.times {Result.build(readyForExtraction: Ready, experiment: experiment)}

        when: "We call the experiment service to update this experiment"
        final BardHttpResponse bardHttpResponse = this.experimentExportService.update(experiment.id, version, 'Complete')

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Experiment.get(experiment.id).readyForExtraction == expectedReadyForExtraction

        where:
        label                                                | expectedStatusCode     | expectedETag | version | numResults | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                               | SC_OK                  | 1            | 0       | 0          | Ready                     | Complete
        "Return NOT_ACCEPTABLE and ETag 0"                   | SC_NOT_ACCEPTABLE      | 0            | 0       | 1          | Ready                     | ReadyForExtraction.Ready
        "Return CONFLICT and ETag 0"                         | SC_CONFLICT            | 0            | -1      | 0          | Ready                     | ReadyForExtraction.Ready
        "Return PRECONDITION_FAILED and ETag 0"              | SC_PRECONDITION_FAILED | 0            | 2       | 0          | Ready                     | ReadyForExtraction.Ready
        "Return OK and ETag 0, Already completed Experiment" | SC_OK                  | 0            | 0       | 0          | Complete                  | ReadyForExtraction.Complete
    }



    void "test update Not Found Status"() {
        given: "Given a non-existing Experiment"

        when: "We call the experiment service to update this experiment"
        this.experimentExportService.update(new Long(100000), 0, ReadyForExtraction.Complete.toString())

        then: "An exception is thrown, indicating that the experiment does not exist"
        thrown(NotFoundException)
    }

    void "test generate and validate Experiment with id"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.build(readyForExtraction: Ready)

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment.id)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        println(actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }

    void "test generate and validate Experiment"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.build(readyForExtraction: Ready)

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        println(actualXml)
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

//    void "test generate external references"() {
//        given: "Given that there is an experiment"
//        final Experiment experiment = Experiment.get(1)
//
//
//        when: "A service call is made to generate the external references"
//        this.markupBuilder.experiment(experimentName: 'experimentName') {
//            this.experimentExportService.generateExternalReferences(this.markupBuilder, experiment.externalReferences)
//        }
//        then: "An XML is generated that conforms to the expected XML"
//        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
//        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
//        final Validator validator = schema.newValidator()
//        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
//    }
//
//    void "test generate external reference"() {
//        given: "Given that there is an experiment"
//        final Experiment experiment = Experiment.get(1)
//        final List<ExternalReference> refs = experiment.externalReferences as List<ExternalReference>
//        final ExternalReference externalReference = refs.get(0)
//        when: "A service call is made to generate the external reference"
//        this.experimentExportService.generateExternalReference(this.markupBuilder, externalReference)
//
//        then: "An XML is generated that conforms to the expected XML"
//        XMLAssert.assertXpathEvaluatesTo("1", "count(//externalReference)", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("aid=1007", "//externalAssayRef", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("PubChem", "//externalSystem/@name", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("NIH", "//externalSystem/@owner", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?", "//systemUrl", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("http://localhost:8080/dataExport/api/projects/1", "//link/@href", this.writer.toString());
//        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=project", "//link/@type", this.writer.toString());
//
//    }

}
