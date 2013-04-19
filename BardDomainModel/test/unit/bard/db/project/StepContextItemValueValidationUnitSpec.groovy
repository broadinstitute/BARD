package bard.db.project

import bard.db.model.StandardContextItemValueValidationUnitSpec
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
@Build(StepContextItem)
@Unroll
class StepContextItemValueValidationUnitSpec extends StandardContextItemValueValidationUnitSpec<StepContextItem> {
    @Before
    void doSetup() {
        domainInstance = StepContextItem.buildWithoutSave()
    }
}