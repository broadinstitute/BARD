package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.project.Project
import bard.db.registration.ExternalReference
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import grails.buildtestdata.mixin.Build
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project, ExternalReference])
@Unroll
class ProjectExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ProjectExportService projectExportService


    void setup() {
        LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)
        MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(projectMediaType: "projectMediaType",
                    projectsMediaType: "projectsMediaType",
                    externalReferenceMediaType: "externalReferenceMediaType")

        this.projectExportService = new ProjectExportService(mediaTypesDTO)
        projectExportService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate Project #label"() {
        given: "A Project"
        final Project project = Project.build(projectName: projectName, groupType: groupType, description: description, readyForExtraction: ReadyForExtraction.Ready)
        when: "We attempt to generate a Project XML document"
        this.projectExportService.generateProject(this.markupBuilder, project)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | projectName     | groupType | description | results
        "Project with no description" | "Project Name1" | 'Project' | ""          | XmlTestSamples.PROJECT_NO_DESCRIPTION
        "Full Project"                | "Project Name2" | 'Panel'   | "Broad"     | XmlTestSamples.PROJECT_WITH_DESCRIPTION

    }

    void "test Generate Project Not Found Exception"() {
        given:
        Project.metaClass.static.get = {id -> null }
        when: "We attempt to generate a Project"
        this.projectExportService.generateProject(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test Generate Project Links #label"() {
        given:
        final Project project = (Project)valueUnderTest.call()
        when:
        this.markupBuilder.root() {
            this.projectExportService.generateProjectLinks(this.markupBuilder, project)
        }
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                       | valueUnderTest                                                           | results
        "Project Links with External References"    | {def ex = ExternalReference.build(project: Project.build()); ex.project} | XmlTestSamples.PROJECT_LINKS_WITH_EXTERNAL_REFERENCE
        "Project Links with No External References" | {Project.build()}                                                        | XmlTestSamples.PROJECT_LINKS_WITH_NO_EXTERNAL_REFERENCE
    }
}
