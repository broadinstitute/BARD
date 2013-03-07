package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/30/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([BardDescriptor, Element])
@Mock([BardDescriptor, Element])
@Unroll
class BardDescriptorConstraintUnitSpec extends AbstractDescriptorConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = BardDescriptor.buildWithoutSave()
        parent = BardDescriptor.build()
    }
}
