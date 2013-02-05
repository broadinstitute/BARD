package bard.db.dictionary

import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/15/12
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
class BiologyDescriptorConstraintIntegrationSpec extends AbstractDescriptorConstraintIntegrationSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = BiologyDescriptor.buildWithoutSave()
        parent = BiologyDescriptor.build()
    }

}
