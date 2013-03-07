package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/15/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([InstanceDescriptor, Element])
@Mock([InstanceDescriptor, Element])
@Unroll
class InstanceDescriptorConstraintUnitSpec extends AbstractDescriptorConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = InstanceDescriptor.buildWithoutSave()
    }

}
