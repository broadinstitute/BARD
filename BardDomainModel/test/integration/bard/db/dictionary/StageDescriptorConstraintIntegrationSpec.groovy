package bard.db.dictionary

import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/5/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
class StageDescriptorConstraintIntegrationSpec extends AbstractDescriptorConstraintIntegrationSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = StageDescriptor.buildWithoutSave()
        domainInstance.element.save()
        parent = StageDescriptor.build()
    }
}
