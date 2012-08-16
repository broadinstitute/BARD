package bard.db.registration

import test.bard.db.registration.AbstractAssayDocumentSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDocumentIntegrationSpec extends AbstractAssayDocumentSpec {

    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave()
    }

}
