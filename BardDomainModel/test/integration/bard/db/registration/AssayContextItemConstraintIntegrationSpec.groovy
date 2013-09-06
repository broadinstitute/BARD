package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.model.AbstractContextItemIntegrationSpec
import bard.db.project.ProjectExperimentContextItem
import org.junit.Before
import spock.lang.Ignore
import spock.lang.Unroll

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
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    AssayContextItem constructInstance(Map props) {
        def instance = AssayContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }

    void "test canDelete has No experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        boolean canDelete = AssayContextItem.canDeleteContextItem(domainInstance)
        then:
        assert canDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | true
        "List Attribute Type"  | AttributeType.List  | true
        "Range Attribute Type" | AttributeType.Range | true
    }

    void "test canDelete has experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        Assay assay = domainInstance.assayContext.assay
        assay.experiments = [new Experiment()]
        when:
        boolean canDelete = AssayContextItem.canDeleteContextItem(domainInstance)
        then:
        assert canDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | false
        "List Attribute Type"  | AttributeType.List  | false
        "Range Attribute Type" | AttributeType.Range | false

    }


    void "test safeToDeleteContextItem #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        boolean safeToDelete = AssayContextItem.safeToDeleteContextItem(domainInstance)
        then:
        assert safeToDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | false
        "List Attribute Type"  | AttributeType.List  | false
        "Range Attribute Type" | AttributeType.Range | false

    }
    @Ignore
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