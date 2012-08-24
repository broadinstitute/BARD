package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Project
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert

import javax.servlet.http.HttpServletResponse
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

    void "test update Not Found Status"() {
        given: "Given a non-existing Project"
        when: "We call the project service to update this project"
        this.projectExportService.update(new Long(100000), 0, ReadyForExtraction.Complete.toString())

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given a Project with id #id and version #version"
        when: "We call the project service to update this assay"
        final BardHttpResponse bardHttpResponse = this.projectExportService.update(projectId, version, status)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Project.get(projectId).readyForExtraction == expectStatus
        where:
        label                                             | expectedStatusCode                         | expectedETag | projectId   | version | status     | expectStatus
        "Return OK and ETag 1"                            | HttpServletResponse.SC_OK                  | new Long(1)  | new Long(1) | 0       | "Complete" | ReadyForExtraction.Complete
        "Return CONFLICT and ETag 0"                      | HttpServletResponse.SC_CONFLICT            | new Long(0)  | new Long(1) | -1      | "Complete" | ReadyForExtraction.Ready
        "Return PRECONDITION_FAILED and ETag 0"           | HttpServletResponse.SC_PRECONDITION_FAILED | new Long(0)  | new Long(1) | 2       | "Complete" | ReadyForExtraction.Ready
        "Return OK and ETag 0, Already completed Project" | HttpServletResponse.SC_OK                  | new Long(0)  | new Long(3) | 0       | "Complete" | ReadyForExtraction.Complete
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
        XMLAssert.assertXpathEvaluatesTo("2", "//projects/@count", this.writer.toString());

    }
}
