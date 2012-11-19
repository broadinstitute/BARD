package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(LaboratoryElement)
@Unroll
class LaboratoryElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = LaboratoryElement.buildWithoutSave()
    }


}
