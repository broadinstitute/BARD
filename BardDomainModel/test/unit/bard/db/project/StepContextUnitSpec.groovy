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
@Build([ProjectStep, StepContext, StepContextItem, Element])
@Mock([ProjectStep, StepContext, StepContextItem, Element])
@Unroll
class StepContextUnitSpec extends AbstractContextUnitSpec<StepContext> {
    @Before
    @Override
    void doSetup() {
        domainInstance = StepContext.buildWithoutSave()
    }
}
