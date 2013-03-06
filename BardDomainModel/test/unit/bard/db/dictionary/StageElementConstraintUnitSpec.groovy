package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([StageElement, Element])
@Mock([StageElement, Element])
@Unroll
class StageElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = StageElement.buildWithoutSave()
    }


}
