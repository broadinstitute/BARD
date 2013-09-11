package bard.db.registration

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Panel, PanelAssay])
@Mock([Assay, Panel, PanelAssay])
@Unroll
class PanelAssayConstraintUnitSpec extends Specification {

    def domainInstance

    @Before
    void doSetup() {
        Assay assay = Assay.build()
        Panel panel = Panel.build()
        domainInstance = PanelAssay.buildWithoutSave(assay: assay, panel: panel)
    }


    void "test assay constraints #desc assay: '#valueUnderTest'"() {

        final String field = 'assay'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest    | valid | errorCode
        'null not valid' | { null }          | false | 'nullable'
        'valid assay'    | { Assay.build() } | true  | null

    }

    void "test panel constraints #desc panel: '#valueUnderTest'"() {

        final String field = 'panel'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest    | valid | errorCode
        'null not valid' | { null }          | false | 'nullable'
        'valid measure'  | { Panel.build() } | true  | null

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
