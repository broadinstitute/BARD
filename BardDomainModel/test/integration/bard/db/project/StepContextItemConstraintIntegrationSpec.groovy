package bard.db.project

import org.junit.Before
import bard.db.model.AbstractContextItemIntegrationSpec
import bard.db.project.StepContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
class StepContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = StepContextItem.buildWithoutSave()
        domainInstance.attributeElement.save()
    }
}
