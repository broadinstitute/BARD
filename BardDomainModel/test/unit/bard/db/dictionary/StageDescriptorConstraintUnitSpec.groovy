package bard.db.dictionary

import org.junit.Before
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/5/13
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([StageDescriptor, Element])
@Unroll
class StageDescriptorConstraintUnitSpec extends AbstractDescriptorConstraintUnitSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = StageDescriptor.buildWithoutSave()
        parent = StageDescriptor.build()
    }
}
