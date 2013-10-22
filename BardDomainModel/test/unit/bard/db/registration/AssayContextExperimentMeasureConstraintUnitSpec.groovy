package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.ExternalReference.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextExperimentMeasure, Experiment, ExperimentMeasure, Element])
@Mock([Assay, AssayContext, AssayContextExperimentMeasure, Experiment, ExperimentMeasure, Element])
@Unroll
class AssayContextExperimentMeasureConstraintUnitSpec extends Specification {

    def domainInstance

    @Before
    void doSetup() {
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        AssayContext assayContext = AssayContext.build(assay: assay)
        ExperimentMeasure measure = ExperimentMeasure.build(experiment: experiment, resultType: Element.build())
        domainInstance = AssayContextExperimentMeasure.buildWithoutSave(assayContext: assayContext, experimentMeasure: measure)
    }

    void "test assayContext constraints #desc assayContext: '#valueUnderTest'"() {

        final String field = 'assayContext'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                 | valueUnderTest           | valid | errorCode
        'null not valid'     | { null }                 | false | 'nullable'
        'valid assayContext' | { AssayContext.build() } | true  | null

    }

    void "test experiment measure constraints #desc measure: '#valueUnderTest'"() {

        final String field = 'experimentMeasure'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest                | valid | errorCode
        'null not valid' | { null }                      | false | 'nullable'
        'valid measure'  | { ExperimentMeasure.build() } | true  | null

    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)



        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                     | false | 'blank'
        'blank valid'      | '  '                                   | false | 'blank'

        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
        final String field = 'dateCreated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}
