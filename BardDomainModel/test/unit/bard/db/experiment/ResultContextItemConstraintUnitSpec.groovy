package bard.db.experiment

import org.junit.Before
import spock.lang.Unroll
import grails.buildtestdata.mixin.Build
import bard.db.model.AbstractContextItemConstraintUnitSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@Build(ResultContextItem)
class ResultContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec {
    @Before
    @Override
    void doSetup() {
        domainInstance = ResultContextItem.buildWithoutSave()
    }
}
