package bard.db.project

import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.model.AbstractContextItemUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/8/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectContextItem)
@Unroll
class ProjectContextItemUnitSpec extends AbstractContextItemUnitSpec{

    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectContextItem.buildWithoutSave()
    }
}
