package bard.db.registration

import bard.db.model.AbstractDocumentConstraintIntegrationSpec
import org.junit.Before
import spock.lang.Unroll

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
