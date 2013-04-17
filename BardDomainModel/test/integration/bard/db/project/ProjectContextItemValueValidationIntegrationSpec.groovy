package bard.db.project

import bard.db.model.StandardContextItemValueValidationIntegrationSpec
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/12/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectContextItemValueValidationIntegrationSpec extends StandardContextItemValueValidationIntegrationSpec<ProjectContextItem> {
    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectContextItem.buildWithoutSave()
        domainInstance.attributeElement.save()
    }
}
