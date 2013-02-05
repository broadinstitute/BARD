package bard.db.project

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll
import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.project.StepContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(StepContextItem)
@Unroll
class StepContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec{
    @Before
     void doSetup(){
        domainInstance = StepContextItem.buildWithoutSave()
    }
}