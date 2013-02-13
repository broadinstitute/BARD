package dataexport.experiment

import bard.db.project.Project
import common.tests.XmlTestAssertions
import dataexport.registration.BardHttpResponse
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static bard.db.enums.ReadyForExtraction.COMPLETE
import static bard.db.enums.ReadyForExtraction.READY
import static javax.servlet.http.HttpServletResponse.*
import org.springframework.core.io.FileSystemResource

@Unroll
class ProjectExportServiceIntegrationSpec extends IntegrationSpec {
    ProjectExportService projectExportService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def grailsApplication
    Resource schemaResource =new FileSystemResource(new File("web-app/schemas/projectSchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

        TestDataConfigurationHolder.reset()
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
        ['PROJECT_ID_SEQ'
        ].each {
            this.resetSequenceUtil.resetSequence(it)
        }

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Project"
        when: "We call the project service to update this project"
        this.projectExportService.update(new Long(100000), 0, 'Complete')

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given a Project with id #id and version #version"
        Project.build(readyForExtraction: initialReadyForExtraction)

        when: "We call the project service to update this assay"
        final BardHttpResponse bardHttpResponse = this.projectExportService.update(projectId, version, "Complete")

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Project.get(projectId).readyForExtraction == expectedReadyForExtraction

        where:
        label                                             | expectedStatusCode     | expectedETag | projectId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                            | SC_OK                  | 1            | 1         | 0       | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                      | SC_CONFLICT            | 0            | 1         | -1      | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"           | SC_PRECONDITION_FAILED | 0            | 1         | 2       | READY                     | READY
        "Return OK and ETag 0, Already completed Project" | SC_OK                  | 0            | 1         | 0       | COMPLETE                  | COMPLETE
    }

    void "test generate and validate Project "() {
        given: "Given a Project"
        final Project project = Project.build()

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, project.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Project given an id #label"() {
        given: "Given a Project"
        final Project project = Project.build()

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, project.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Projects"() {
        given: "Given there is at least one project ready for extraction"
        Project project = Project.build(readyForExtraction: READY)
        project.save(flush: true)

        when: "A service call is made to generate a list of projects ready to be extracted"
        this.projectExportService.generateProjects(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("1", "//projects/@count", this.writer.toString());

    }
}
