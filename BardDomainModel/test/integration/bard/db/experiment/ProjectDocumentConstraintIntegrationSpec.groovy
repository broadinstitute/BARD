package bard.db.experiment

import bard.db.model.AbstractDocumentConstraintIntegrationSpec
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/8/12
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectDocumentConstraintIntegrationSpec extends AbstractDocumentConstraintIntegrationSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectDocument.buildWithoutSave()
    }
}
