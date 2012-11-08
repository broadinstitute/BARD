package bard.db.registration

import grails.buildtestdata.mixin.Build
import org.junit.Before

import spock.lang.Unroll
import bard.db.model.AbstractDocumentConstraintUnitSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayDocument, Assay])
@Unroll
class AssayDocumentConstraintUnitSpec extends AbstractDocumentConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave()
    }
}
