package bard.db.project

import bard.db.dictionary.Element
import bard.db.model.AbstractContextUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/8/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project, ProjectContext, ProjectContextItem, Element])
@Mock([Project, ProjectContext, ProjectContextItem, Element])
@Unroll
class ProjectContextUnitSpec extends AbstractContextUnitSpec<ProjectContext> {
    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectContext.buildWithoutSave()
    }
}
