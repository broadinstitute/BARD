package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

@Build(Element)
@Unroll
class ElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = Element.buildWithoutSave()
    }
    void "test addChildMethod constraints #desc addChildMethod: '#valueUnderTest'"() {
        final String field = 'addChildMethod'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the it can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest             | valid | errorCode
        'null not value' | null                       | false | 'nullable'
        'valid value'    | AddChildMethod.NO          | true  | null
        'valid value'    | AddChildMethod.DIRECT      | true  | null
        'valid value'    | AddChildMethod.RDM_REQUEST | true  | null
    }

//    void "test expectedValueType constraints #desc expectedValueType: '#valueUnderTest'"() {
//        final String field = 'expectedValueType'
//
//        when: 'a value is set for the field under test'
//        domainInstance[(field)] = valueUnderTest
//        domainInstance.save()
//
//        then: 'verify valid or invalid for expected reason'
//        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)
//
//        and: 'verify the it can be persisted to the db'
//        if (valid) {
//            domainInstance == domainInstance.save(flush: true)
//        }
//
//        where:
//        desc             | valueUnderTest                      | valid | errorCode
//        'null not value' | null                                | false | 'nullable'
//        'valid value'    | ExpectedValueType.NONE              | true  | null
//        'valid value'    | ExpectedValueType.ELEMENT           | true  | null
//        'valid value'    | ExpectedValueType.EXTERNAL_ONTOLOGY | true  | null
//        'valid value'    | ExpectedValueType.FREE_TEXT         | true  | null
//        'valid value'    | ExpectedValueType.NUMERIC           | true  | null
//    }


}
