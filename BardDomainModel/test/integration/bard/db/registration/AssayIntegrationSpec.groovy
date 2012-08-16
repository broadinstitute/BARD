package bard.db.registration

import test.bard.db.registration.AbstractAssaySpec

/**
 * Integration tests for Asssay
 */
class AssayIntegrationSpec extends AbstractAssaySpec {



    void doSetup() {
        domainInstance = Assay.buildWithoutSave()
    }


}
