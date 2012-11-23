package dataexport.experiment

import bard.db.enums.ReadyForExtraction
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
import bard.db.project.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project, ExternalReference, ProjectContext, ProjectContextItem, StepContext, StepContextItem])
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
                    externalReferenceMediaType: "externalReferenceMediaType",
                    elementMediaType: "elementMediaType",
                    experimentMediaType: "experimentMediaType")

        this.projectExportService = new ProjectExportService()
        projectExportService.grailsLinkGenerator = grailsLinkGenerator
        projectExportService.mediaTypesDTO = mediaTypesDTO
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "generateStepContext #label"() {
        given:
        StepContext stepContext = StepContext.build(contextName: 'contextName', contextGroup: 'contextGroup')
        when:
        this.projectExportService.generateStepContext(this.markupBuilder, stepContext)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.STEP_CONTEXT_MINIMAL

    }

    //println(this.writer.toString())

    void "generateProjectContext #label"() {
        given:
        ProjectContext projectContext = ProjectContext.build(contextName: 'contextName', contextGroup: 'contextGroup')
        when:
        this.projectExportService.generateProjectContext(this.markupBuilder, projectContext)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.PROJECT_CONTEXT_MINIMAL
    }

    void "generateProjectContextItems #label"() {
        given:
        List<ProjectContextItem> projectContextItems = [ProjectContextItem.build()]  as List<ProjectContextItem>
        when:
        this.projectExportService.generateProjectContextItems(this.markupBuilder, projectContextItems)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.PROJECT_CONTEXT_ITEMS_MINIMAL
    }

    void "generateStepContextItem #label"() {
        given:
        StepContextItem stepContextItem = StepContextItem.build()
        when:
        this.projectExportService.generateStepContextItem(this.markupBuilder, stepContextItem)
        then:
        println(this.writer.toString())
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.STEP_CONTEXT_ITEM_MINIMAL

    }

    void "generateProjectContextItem #label"() {
        given:
        ProjectContextItem projectContextItem = ProjectContextItem.build()
        when:
        this.projectExportService.generateProjectContextItem(this.markupBuilder, projectContextItem)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.PROJECT_CONTEXT_ITEM_MINIMAL
    }

    void "generateProjectStep #label"() {
        given:
        final ProjectStep projectStep = ProjectStep.build()
        when:
        this.projectExportService.generateProjectStep(this.markupBuilder, projectStep)
        then:
        println this.writer.toString()
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label     | results
        "Minimal" | XmlTestSamples.PROJECT_STEP_MINIMAL
    }

    void "test generate Project #label"() {
        given: "A Project"
        final Project project = Project.build(projectName: projectName, groupType: groupType, description: description, readyForExtraction: ReadyForExtraction.Ready)
        when: "We attempt to generate a Project XML document"
        this.projectExportService.generateProject(this.markupBuilder, project)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label          | projectName     | groupType | description | results
        "Full Project" | "Project Name2" | 'Panel'   | "Broad"     | XmlTestSamples.PROJECT_WITH_DESCRIPTION
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
        final Project project = (Project) valueUnderTest.call()
        when:
        this.markupBuilder.root() {
            this.projectExportService.generateProjectLinks(this.markupBuilder, project)
        }
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                       | valueUnderTest                                                           | results
        "Project Links with External References"    | {def ex = ExternalReference.build(project: Project.build()); ex.project} | XmlTestSamples.PROJECT_LINKS_WITH_EXTERNAL_REFERENCE
        "Project Links with No External References" | {Project.build()}                                                        | XmlTestSamples.PROJECT_LINKS_WITHOUT_EXTERNAL_REFERENCE
    }
}
