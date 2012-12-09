package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll


@Build(Element)
@Unroll
class ElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = Element.buildWithoutSave()
    }


}