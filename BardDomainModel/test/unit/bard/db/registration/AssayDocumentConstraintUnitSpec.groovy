package bard.db.registration

import bard.db.model.AbstractDocumentConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayDocument, Assay])
@Mock([AssayDocument, Assay])
@Unroll
class AssayDocumentConstraintUnitSpec extends AbstractDocumentConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave()
    }
}
