package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll


@Build([LaboratoryElement, Element])
@Mock([LaboratoryElement, Element])
@Unroll
class LaboratoryElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = LaboratoryElement.buildWithoutSave()
    }


}
