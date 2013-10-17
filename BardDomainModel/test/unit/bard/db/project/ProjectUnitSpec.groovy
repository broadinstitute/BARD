package bard.db.project

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

import static bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule.ONE_BIOLOGY_ATTRIBUTE_REQUIRED

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 10/15/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project, ProjectContext, ProjectContextItem])
class ProjectUnitSpec extends Specification {

    void 'test guidance for project desc'() {
        given:
        final Project project = Project.build()
        List<Map> itemMaps = attributeElementMaps.call()
        // putting each item in it's own context
        if (itemMaps) {
            itemMaps.each { Map map ->
                final ProjectContext projectContext = ProjectContext.build(project: project)
                project.addToContexts(projectContext)
                final Element attribute = Element.findByLabel(map.label) ?: Element.build(map)
                projectContext.addContextItem(ProjectContextItem.build(attributeElement: attribute, valueDisplay: null))
            }
        }

        when:
        final List<String> actualGuidanceMessages = project.guidance.message

        then:
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                     | attributeElementMaps                         | expectedGuidanceMessages
        'biology required'       | { null }                                     | [ONE_BIOLOGY_ATTRIBUTE_REQUIRED]
        'biology required'       | { [[label: 'biology']] }                     | []
        'more than 1 biology ok' | { [[label: 'biology'], [label: 'biology']] } | []
    }
}