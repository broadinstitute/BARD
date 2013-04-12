package bard.db.registration

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/12/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(AssayContextItem)
@Unroll
class AssayContextItemValueValidationUnitSpec extends Specification {
    AssayContextItem domainInstance

    @Before
    void doSetup() {
        domainInstance = AssayContextItem.buildWithoutSave()
    }


}
