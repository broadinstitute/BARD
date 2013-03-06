package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([BiologyElement, Element])
@Mock([BiologyElement, Element])
@Unroll
class BiologyElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = BiologyElement.buildWithoutSave()
    }


}
