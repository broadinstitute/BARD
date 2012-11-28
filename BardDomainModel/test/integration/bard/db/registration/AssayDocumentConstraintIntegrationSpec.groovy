package bard.db.registration

import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.model.AbstractDocument.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import bard.db.model.AbstractDocumentConstraintIntegrationSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayDocumentConstraintIntegrationSpec extends AbstractDocumentConstraintIntegrationSpec {


    @Before
    void doSetup() {
        domainInstance = AssayDocument.buildWithoutSave()
    }

}
