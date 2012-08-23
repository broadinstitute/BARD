package bard.db.experiment

import org.junit.Before
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectContextItem)
@Unroll
class ProjectContextItemConstraintUnitSpec extends AbstractProjectContextItemConstraintUnitSpec{
    @Before
     void doSetup(){
        domainInstance = ProjectContextItem.buildWithoutSave()
    }
}
