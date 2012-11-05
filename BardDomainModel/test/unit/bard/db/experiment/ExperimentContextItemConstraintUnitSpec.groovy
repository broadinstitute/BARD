package bard.db.experiment

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.AssayContextItem.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(ExperimentContextItem)
@Unroll
class ExperimentContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = ExperimentContextItem.buildWithoutSave()
    }
}
