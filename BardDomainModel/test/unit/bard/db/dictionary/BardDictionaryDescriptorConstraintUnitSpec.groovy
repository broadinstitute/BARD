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
@Build([BardDictionaryDescriptor, Element])
@Mock([BardDictionaryDescriptor, Element])
@Unroll
class BardDictionaryDescriptorConstraintUnitSpec extends AbstractDescriptorConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = BardDictionaryDescriptor.buildWithoutSave()
        parent = BardDictionaryDescriptor.build()
    }
}
