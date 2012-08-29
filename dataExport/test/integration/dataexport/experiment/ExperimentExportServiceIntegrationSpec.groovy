package dataexport.experiment

import bard.db.experiment.Experiment
import bard.db.registration.ExternalReference
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import bard.db.enums.ReadyForExtraction

@Unroll
class ExperimentExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_EXPERIMENT_EXPORT_SCHEMA = "test/integration/dataexport/experiment/experimentSchema.xsd"

    ExperimentExportService experimentExportService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update #label"() {
        given: "Given an Experiment with id #id and version #version"
        when: "We call the experiment service to update this experiment"
        final BardHttpResponse bardHttpResponse = this.experimentExportService.update(experimentId, version, status)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Experiment.get(experimentId).readyForExtraction == expectedStatus
        where:
        label                                                | expectedStatusCode                         | expectedETag | experimentId | version | status     | expectedStatus
        "Return OK and ETag 1"                               | HttpServletResponse.SC_OK                  | new Long(1)  | new Long(1)  | 0       | "Complete" | ReadyForExtraction.Complete
        "Return NOT_ACCEPTABLE and ETag 0"                   | HttpServletResponse.SC_NOT_ACCEPTABLE      | new Long(0)  | new Long(2)  | 0       | "Complete" | ReadyForExtraction.Ready
        "Return CONFLICT and ETag 0"                         | HttpServletResponse.SC_CONFLICT            | new Long(0)  | new Long(1)  | -1      | "Complete" | ReadyForExtraction.Ready
        "Return PRECONDITION_FAILED and ETag 0"              | HttpServletResponse.SC_PRECONDITION_FAILED | new Long(0)  | new Long(1)  | 2       | "Complete" | ReadyForExtraction.Ready
        "Return OK and ETag 0, Already completed Experiment" | HttpServletResponse.SC_OK                  | new Long(0)  | new Long(23) | 0       | "Complete" | ReadyForExtraction.Complete
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
        final Experiment experiment = Experiment.get(1)

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment.id)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))

    }

    void "test generate and validate Experiment"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.get(1)

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))

    }

    void "test generate and validate Experiments"() {
        given: "Given that 2 experiments exists in the database"


        when: "A service call is made to generate the experiments"
        this.experimentExportService.generateExperiments(this.markupBuilder, 0)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
    }

    void "test generate external references"() {
        given: "Given that there is an experiment"
        final Experiment experiment = Experiment.get(1)


        when: "A service call is made to generate the external references"
        this.markupBuilder.experiment(experimentName: 'experimentName') {
            this.experimentExportService.generateExternalReferences(this.markupBuilder, experiment.externalReferences)
        }
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
    }

    void "test generate external reference"() {
        given: "Given that there is an experiment"
        final Experiment experiment = Experiment.get(1)
        final List<ExternalReference> refs = experiment.externalReferences as List<ExternalReference>
        final ExternalReference externalReference = refs.get(0)
        when: "A service call is made to generate the external reference"
        this.experimentExportService.generateExternalReference(this.markupBuilder, externalReference)

        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("1", "count(//externalReference)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("aid=1007", "//externalAssayRef", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("PubChem", "//externalSystem/@name", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("NIH", "//externalSystem/@owner", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?", "//systemUrl", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("http://localhost:8080/dataExport/api/projects/1", "//link/@href", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=project", "//link/@type", this.writer.toString());

    }

}
