package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.model.AbstractContextUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/8/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Experiment, ExperimentContext, ExperimentContextItem, Element])
@Mock([Experiment, ExperimentContext, ExperimentContextItem, Element])
@Unroll
class ExperimentContextUnitSpec extends AbstractContextUnitSpec<ExperimentContext> {
    @Before
    @Override
    void doSetup() {
        domainInstance = ExperimentContext.buildWithoutSave()
    }
}
