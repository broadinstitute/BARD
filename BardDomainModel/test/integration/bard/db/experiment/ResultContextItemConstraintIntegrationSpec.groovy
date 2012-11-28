package bard.db.experiment

import org.junit.Before
import spock.lang.Unroll
import bard.db.model.AbstractContextItemIntegrationSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ResultContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = ResultContextItem.buildWithoutSave()
        domainInstance.result?.save()
        domainInstance.attributeElement?.save()
        domainInstance.valueElement?.save()
    }

}
