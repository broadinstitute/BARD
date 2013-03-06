package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([InstanceElement, Element])
@Mock([InstanceElement, Element])
@Unroll
class InstanceElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = InstanceElement.buildWithoutSave()
    }


}
