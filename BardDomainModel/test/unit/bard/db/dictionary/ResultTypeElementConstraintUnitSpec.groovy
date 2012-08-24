package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(ResultTypeElement)
@Unroll
class ResultTypeElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = ResultTypeElement.buildWithoutSave()
    }


}
