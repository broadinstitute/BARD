package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

@Build([UnitElement, Element])
@Mock([UnitElement, Element])
@Unroll
class UnitElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = UnitElement.buildWithoutSave()
    }


}
