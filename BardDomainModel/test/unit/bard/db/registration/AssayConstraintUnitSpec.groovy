package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.model.AbstractContextOwner.*
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
        desc             | valueUnderTest       | valid | errorCode
        'null not valid' | null                 | false | 'nullable'
        'valid value'    | AssayStatus.DRAFT    | true  | null
        'valid value'    | AssayStatus.APPROVED | true  | null
        'valid value'    | AssayStatus.RETIRED  | true  | null

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
        'too long'         | createString(ASSAY_SHORT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'blank valid'      | ''                                          | true  | null
        'blank valid'      | '   '                                       | true  | null
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
        desc          | valueUnderTest        | valid | errorCode
        'null valid'  | null                  | false | 'nullable'
        'valid value' | AssayType.REGULAR     | true  | null
        'valid value' | AssayType.PANEL_ARRAY | true  | null
        'valid value' | AssayType.PANEL_GROUP | true  | null
        'valid value' | AssayType.TEMPLATE    | true  | null
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
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
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

    void "test groupBySection('#section')"() {
        when:
        ContextGroup contextGroup = domainInstance.groupBySection(section)

        then:
        contextGroup.key == expectedSection
        contextGroup.description == expectedSection

        where:
        section                        | expectedSection
        SECTION_BIOLOGY                | SECTION_BIOLOGY
        SECTION_ASSAY_PROTOCOL         | SECTION_ASSAY_PROTOCOL
        SECTION_ASSAY_DESIGN           | SECTION_ASSAY_DESIGN
        SECTION_ASSAY_READOUT          | SECTION_ASSAY_READOUT
        SECTION_ASSAY_COMPONENTS       | SECTION_ASSAY_COMPONENTS
        SECTION_EXPERIMENTAL_VARIABLES | SECTION_EXPERIMENTAL_VARIABLES
        SECTION_UNCLASSIFIED           | SECTION_UNCLASSIFIED
        ''                             | SECTION_UNCLASSIFIED
        ' '                            | SECTION_UNCLASSIFIED
        'someUnknownSection'           | SECTION_UNCLASSIFIED
        null                           | SECTION_UNCLASSIFIED
    }

    void "test getSectionKeyForContextGroup #desc "() {

        when:
        String actualKey = domainInstance.getSectionKeyForContextGroup(value)

        then:
        actualKey == expectedKey

        where:
        desc                     | value                                      | expectedKey
        "biology"                | 'Biology'                                  | SECTION_BIOLOGY
        "biology"                | 'biology>'                                 | SECTION_BIOLOGY
        "biology"                | 'biology> molecular interaction>'          | SECTION_BIOLOGY
        "Assay Protocol"         | 'Assay Protocol'                           | SECTION_ASSAY_PROTOCOL
        "Assay Protocol"         | 'assay protocol> assay type>'              | SECTION_ASSAY_PROTOCOL
        "Assay Protocol"         | 'assay protocol> assay format>'            | SECTION_ASSAY_PROTOCOL
        "Assay Design"           | 'Assay Design'                             | SECTION_ASSAY_DESIGN
        "Assay Design"           | 'assay protocol> assay design>'            | SECTION_ASSAY_DESIGN
        "Assay Readout"          | 'Assay Readout'                            | SECTION_ASSAY_READOUT
        "Assay Readout"          | 'assay protocol> assay readout>'           | SECTION_ASSAY_READOUT
        "Assay Components"       | 'Assay Components'                         | SECTION_ASSAY_COMPONENTS
        "Assay Components"       | 'assay protocol> assay component>'         | SECTION_ASSAY_COMPONENTS
        "Experimental Variables" | 'Experimental Variables'                   | SECTION_EXPERIMENTAL_VARIABLES
        "Experimental Variables" | 'project management> project information>' | SECTION_EXPERIMENTAL_VARIABLES
        "Experimental Variables" | 'project management> experiment>'          | SECTION_EXPERIMENTAL_VARIABLES
        "Unclassified"           | 'Unclassified'                             | SECTION_UNCLASSIFIED
        "Unclassified"           | 'unclassified>'                            | SECTION_UNCLASSIFIED
        "Unclassified"           | ''                                         | SECTION_UNCLASSIFIED
        "Unclassified"           | 'some unknown string'                      | SECTION_UNCLASSIFIED
        "Unclassified"           | null                                       | SECTION_UNCLASSIFIED


    }

}

