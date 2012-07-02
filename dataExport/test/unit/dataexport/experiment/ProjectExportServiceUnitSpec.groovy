package dataexport.experiment

import bard.db.experiment.GroupType
import bard.db.experiment.Project
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
@Mock([Project])
class ProjectExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ProjectExportService projectExportService


    void setup() {
        LinkGenerator grailsLinkGenerator = Mock()
        MediaTypesDTO mediaTypesDTO = new MediaTypesDTO(projectMediaType: "projectMediaType", projectsMediaType: "projectsMediaType")

        this.projectExportService = new ProjectExportService(mediaTypesDTO)
        projectExportService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate Project #label"() {
        given: "A Project"
        final Project project = new Project(projectName: projectName, groupType: groupType, description: description, readyForExtraction: 'Ready')
        when: "We attempt to generate a Project XML document"
        this.projectExportService.generateProject(this.markupBuilder, project)
        then: "A valid xml document is generated and is similar to the expected document"
         XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | projectName     | groupType         | description | results
        "Project with no description" | "Project Name1" | GroupType.Project | ""          | XmlTestSamples.PROJECT_NO_DESCRIPTION
        "Full Project"                | "Project Name2" | GroupType.Panel   | "Broad"     | XmlTestSamples.PROJECT_WITH_DESCRIPTION

    }

    void "test Generate Project Not Found Exception"() {
        given:
        Project.metaClass.static.get = {id -> null }
        when: "We attempt to generate a Project"
        this.projectExportService.generateProject(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }
}
