package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([ResultTypeElement, Element])
@Mock([ResultTypeElement, Element])
@Unroll
class ResultTypeElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = ResultTypeElement.buildWithoutSave()
    }


}
