package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.ReadyForExtraction
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.Assay.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Build([Assay, Element, Measure])
@Mock([Assay, Element, Measure])
@Unroll
class AssayConstraintUnitSpec extends Specification {
    Assay domainInstance

    @Before
    void doSetup() {
        domainInstance = Assay.buildWithoutSave()
    }

    void "test assayStatus constraints #desc assayStatus: '#valueUnderTest'"() {
        final String field = 'assayStatus'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest               | valid | errorCode
        'null not valid' | null                         | false | 'nullable'
        'valid value'    | AssayStatus.DRAFT            | true  | null
        'valid value'    | AssayStatus.WITNESSED        | true  | null
        'valid value'    | AssayStatus.FINISHED         | true  | null
        'valid value'    | AssayStatus.MEASURES_DONE    | true  | null
        'valid value'    | AssayStatus.ANNOTATIONS_DONE | true  | null
        'valid value'    | AssayStatus.RETIRED          | true  | null

    }

    void "test assayShortName constraints #desc assayShortName: "() {

        final String field = 'assayShortName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                              | valid | errorCode
        'null not valid'   | null                                        | false | 'nullable'
        'blank not valid'  | ''                                          | false | 'blank'
        'blank not valid'  | '   '                                       | false | 'blank'

        'too long'         | createString(ASSAY_SHORT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(ASSAY_SHORT_NAME_MAX_SIZE)     | true  | null
    }

    void "test assayName constraints #desc assayName: "() {

        final String field = 'assayName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                        | valid | errorCode
        'null not valid'   | null                                  | false | 'nullable'
        'blank not valid'  | ''                                    | false | 'blank'
        'blank not valid'  | '   '                                 | false | 'blank'

        'too long'         | createString(ASSAY_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(ASSAY_NAME_MAX_SIZE)     | true  | null


    }

    void "test assayVersion constraints #desc assayVersion: '#valueUnderTest'"() {

        final String field = 'assayVersion'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc                           | valueUnderTest                            | valid | errorCode
        'null not valid'               | null                                      | false | 'nullable'
        'blank not valid'              | ''                                        | false | 'blank'
        'blank not valid'              | '   '                                     | false | 'blank'

        'too long'                     | createString(ASSAY_VERSION_MAX_SIZE + 1)  | false | 'maxSize.exceeded'

        'exactly at limit, not digits' | createString(ASSAY_VERSION_MAX_SIZE)      | true  | null

        'exactly at limit, digits'     | createString(ASSAY_VERSION_MAX_SIZE, '9') | true  | null
    }

    void "test designedBy constraints #desc designedBy: '#valueUnderTest'"() {

        final String field = 'designedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(DESIGNED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(DESIGNED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
        'blank valid'      | ''                                     | true  | null
        'blank valid'      | '  '                                   | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {

        final String field = 'readyForExtraction'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest               | valid | errorCode
        'null not valid' | null                         | false | 'nullable'

        'valid value'    | ReadyForExtraction.NOT_READY | true  | null
        'valid value'    | ReadyForExtraction.READY     | true  | null
        'valid value'    | ReadyForExtraction.STARTED   | true  | null
        'valid value'    | ReadyForExtraction.COMPLETE  | true  | null
    }

    void "test assayType constraints #desc assayType: '#valueUnderTest'"() {

        final String field = 'assayType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest  | valid | errorCode
        'null valid'       | null            | false | null
        'value not inList' | 'Foo'           | false | 'not.inList'

        'valid value'      | 'Regular'       | true  | null
        'valid value'      | 'Panel - Array' | true  | null
        'valid value'      | 'Panel - Group' | true  | null
        'valid value'      | 'Template'      | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

    void "test getChildrenSorted by displayLabel case insensitive input:#input expected: #expected"() {
        when:
        for (String label in input) {
            domainInstance.addToMeasures(Measure.build(resultType: Element.build(label: label), assay: domainInstance))
        }

        then:
        domainInstance.getRootMeasuresSorted()*.displayLabel == expected

        where:
        input           | expected
        ['b', 'a']      | ['a', 'b']
        ['B', 'a']      | ['a', 'B']
        ['c', 'B', 'a'] | ['a', 'B', 'c']
    }

}

