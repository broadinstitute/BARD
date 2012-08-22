package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(BiologyElement)
@Unroll
class BiologyElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = BiologyElement.buildWithoutSave()
    }


}
