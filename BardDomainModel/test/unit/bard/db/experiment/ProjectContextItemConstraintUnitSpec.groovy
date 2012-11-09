package bard.db.experiment

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll
import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.project.ProjectContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectContextItem)
@Unroll
class ProjectContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectContextItem.buildWithoutSave()
    }
}
