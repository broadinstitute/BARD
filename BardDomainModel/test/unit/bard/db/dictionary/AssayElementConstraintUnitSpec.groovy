package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(AssayElement)
@Unroll
class AssayElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = AssayElement.buildWithoutSave()
    }


}
