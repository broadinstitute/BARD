package bard.db.experiment

import bard.db.model.AbstractDocumentConstraintIntegrationSpec
import bard.db.registration.AssayDocument
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
class ExperimentDocumentConstraintIntegrationSpec extends AbstractDocumentConstraintIntegrationSpec {


    @Before
    void doSetup() {
        domainInstance = ExperimentDocument.buildWithoutSave()
    }

}
