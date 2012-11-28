package bard.db.experiment

import grails.buildtestdata.mixin.Build
import org.junit.Before

import spock.lang.Unroll

import bard.db.model.AbstractContextItemConstraintUnitSpec

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
