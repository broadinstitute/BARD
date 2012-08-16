package bard.db.registration

import grails.buildtestdata.mixin.Build
import spock.lang.Unroll
import org.junit.Before
import test.bard.db.registration.AbstractAssayDocumentSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayDocument,Assay])
@Unroll
class AssayDocumentUnitSpec extends AbstractAssayDocumentSpec {

    @Before
    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave(assay: Assay.buildWithoutSave(assayVersion: '2'))
    }

}
