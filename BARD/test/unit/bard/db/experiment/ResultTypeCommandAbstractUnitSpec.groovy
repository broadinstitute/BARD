package bard.db.experiment

import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.enums.ExpectedValueType
import bard.db.enums.HierarchyType
import bard.db.project.DoseResultTypeCommand
import bard.db.project.ExperimentController
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@Unroll
class ResultTypeCommandAbstractUnitSpec extends Specification {

    OntologyDataAccessService ontologyDataAccessService
    ExperimentService experimentService

    def setup() {
        this.ontologyDataAccessService = Mock(OntologyDataAccessService)
        this.experimentService = Mock(ExperimentService)
    }
}
