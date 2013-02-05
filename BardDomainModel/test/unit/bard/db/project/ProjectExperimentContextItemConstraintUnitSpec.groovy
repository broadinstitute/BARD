package bard.db.project

import bard.db.model.AbstractContextItemConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectExperimentContextItem)
@Unroll
class ProjectExperimentContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec{
    @Before
     void doSetup(){
        domainInstance = ProjectExperimentContextItem.buildWithoutSave()
    }
}