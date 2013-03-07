package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([AssayElement, Element])
@Mock([AssayElement, Element])
@Unroll
class AssayElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = AssayElement.buildWithoutSave()
    }


}
