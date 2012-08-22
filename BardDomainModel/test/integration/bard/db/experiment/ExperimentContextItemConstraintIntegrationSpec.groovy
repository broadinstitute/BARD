package bard.db.experiment

import bard.db.registration.AttributeType
import org.junit.Before

import static bard.db.registration.AssayContextItem.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExperimentContextItemConstraintIntegrationSpec extends RunContextItemIntegrationSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = ExperimentContextItem.buildWithoutSave()
        domainInstance.experiment?.save()
        domainInstance.attributeElement?.save()
        domainInstance.valueElement?.save()
    }

}
