package bard.db.project

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

import static bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule.*

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 10/15/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project])
class ProjectUnitSpec extends Specification {

    void 'test that OneItemPerNonFixedAttributeElement is wired into assay'() {
        given: 'an assay with some context items'
        final Project project = Project.build()
        expect:
        project.getGuidanceRules()*.class*.simpleName == ['MinimumOfOneBiologyGuidanceRule']
    }
}