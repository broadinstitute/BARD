package bard.db.experiment

import bard.db.model.AbstractDocumentConstraintUnitSpec
import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/10/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([ExperimentDocument, Experiment])
@Mock([ExperimentDocument, Experiment])
@Unroll
class ExperimentDocumentConstraintUnitSpec extends AbstractDocumentConstraintUnitSpec {

    @Before
    void doSetup() {
        domainInstance = ExperimentDocument.buildWithoutSave()
    }
}
