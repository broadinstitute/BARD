package dataexport.experiment

import bard.db.experiment.Project
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class ProjectExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_PROJECT_EXPORT_SCHEMA = "test/integration/dataexport/experiment/projectSchema.xsd"

    ProjectExportService projectExportService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate and validate Project #label"() {
        given: "Given a Project"
        final Project project = Project.get(1)

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, project.id)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(XmlTestSamples.PROJECT, this.writer.toString())
    }

    void "test generate and validate Project given an id #label"() {

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, new Long("1"))
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(XmlTestSamples.PROJECT, this.writer.toString())
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_PROJECT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))

    }


    void "test generate and validate Projects #label"() {
        given: "Given there is at least one project ready for extraction"
        when: "A service call is made to generate a list of projects ready to be extracted"
        this.projectExportService.generateProjects(this.markupBuilder)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.PROJECTS, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("2", "//projects/@count", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("edit", "//link/@rel", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=project", "//link/@type", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("http://localhost:8080/dataExport/api/projects/1", "//link/@href", this.writer.toString());

    }
}
