package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextItemConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Ignore
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayContextItem, Element])
@Unroll
class AssayContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec {
    @Before
    void doSetup() {
        domainInstance = AssayContextItem.buildWithoutSave()
    }

    void "test validateContextItemBeforeDelete has No experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        AssayContextItem.validateContextItemBeforeDelete(domainInstance)
        then:
        assertFieldValidationExpectations(domainInstance, "attributeType", valid, errorCode)

        where:
        desc                   | attributeType       | valid | errorCode
        "Fixed Attribute Type" | AttributeType.Fixed | true  | null
        "Free Attribute Type"  | AttributeType.Free  | true  | null
        "List Attribute Type"  | AttributeType.List  | true  | null
        "Range Attribute Type" | AttributeType.Range | true  | null
    }

    void "test validateContextItemBeforeDelete has experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        Assay assay = domainInstance.assayContext.assay
        assay.experiments = [new Experiment()]
        when:
        AssayContextItem.validateContextItemBeforeDelete(domainInstance)
        then:
        assertFieldValidationExpectations(domainInstance, "attributeType", valid, errorCode)

        where:
        desc                   | attributeType       | valid | errorCode
        "Fixed Attribute Type" | AttributeType.Fixed | true  | null
        "Free Attribute Type"  | AttributeType.Free  | false | "assayContextItem.label.cannotdelete"
        "List Attribute Type"  | AttributeType.List  | false | "assayContextItem.label.cannotdelete"
        "Range Attribute Type" | AttributeType.Range | false | "assayContextItem.label.cannotdelete"


    }
    void "test validateContextItemBeforeDelete has experiments before delete #desc"() {
        given:
        domainInstance.attributeType = attributeType
        Assay assay = domainInstance.assayContext.assay
        assay.experiments = [new Experiment()]
        when:
        domainInstance.beforeDelete()
        then:
        assertFieldValidationExpectations(domainInstance, "attributeType", valid, errorCode)

        where:
        desc                   | attributeType       | valid | errorCode
        "Fixed Attribute Type" | AttributeType.Fixed | true  | null
        "Free Attribute Type"  | AttributeType.Free  | false | "assayContextItem.label.cannotdelete"
        "List Attribute Type"  | AttributeType.List  | false | "assayContextItem.label.cannotdelete"
        "Range Attribute Type" | AttributeType.Range | false | "assayContextItem.label.cannotdelete"


    }
    void "test before rejectDeletionOfContextItem #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        AssayContextItem.rejectDeletionOfContextItem(domainInstance)
        then:
        assertFieldValidationExpectations(domainInstance, "attributeType", valid, errorCode)

        where:
        desc                   | attributeType       | valid | errorCode
        "Fixed Attribute Type" | AttributeType.Fixed | true  | null
        "Free Attribute Type"  | AttributeType.Free  | false | "assayContextItem.label.cannotdelete"
        "List Attribute Type"  | AttributeType.List  | false | "assayContextItem.label.cannotdelete"
        "Range Attribute Type" | AttributeType.Range | false | "assayContextItem.label.cannotdelete"

    }

    @Ignore
    void "test attributeType constraints #desc attributeType: '#valueUnderTest'"() {

        final String field = 'attributeType'

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
        desc                  | valueUnderTest      | valid | errorCode
        'null'                | null                | false | 'nullable'

        'AttributeType.Fixed' | AttributeType.Fixed | true  | null
        'AttributeType.List'  | AttributeType.List  | true  | null
        'AttributeType.Range' | AttributeType.Range | true  | null
        'AttributeType.Free'  | AttributeType.Free  | true  | null

    }
}
