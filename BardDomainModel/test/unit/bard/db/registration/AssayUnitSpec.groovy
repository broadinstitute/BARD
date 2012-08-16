package bard.db.registration

import grails.buildtestdata.mixin.Build
import org.junit.Before
import test.bard.db.registration.AbstractAssaySpec

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Build(Assay)
class AssayUnitSpec extends AbstractAssaySpec {

    @Before
    void doSetup() {
        domainInstance = Assay.buildWithoutSave(assayVersion: '2')
    }

}

