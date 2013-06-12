package bard.db.registration

import bard.db.dictionary.Element
import bard.db.model.AbstractContextItemIntegrationSpec
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Unroll

import static bard.db.enums.ExpectedValueType.*
import static bard.db.model.AbstractContextItem.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec<AssayContextItem> {

    @Before
    void doSetup() {
        domainInstance = AssayContextItem.buildWithoutSave()
        domainInstance.attributeElement.save()
    }

    void "test attributeType constraints #desc attributeType: '#valueUnderTest'"() {

        final String field = 'attributeType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        RuntimeException e = thrown()
        e.message == 'Unknown attributeType: null'

        where:
        desc   | valueUnderTest
        'null' | null
    }

}