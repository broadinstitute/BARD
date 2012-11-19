package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(InstanceElement)
@Unroll
class InstanceElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = InstanceElement.buildWithoutSave()
    }


}
