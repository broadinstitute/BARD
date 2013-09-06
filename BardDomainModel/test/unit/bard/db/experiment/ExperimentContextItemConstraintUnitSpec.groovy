package bard.db.experiment

import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(ExperimentContextItem)
@Unroll
class ExperimentContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec<ExperimentContextItem> {

    @Before
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    ExperimentContextItem constructInstance(Map props) {
        def instance = ExperimentContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }
}
