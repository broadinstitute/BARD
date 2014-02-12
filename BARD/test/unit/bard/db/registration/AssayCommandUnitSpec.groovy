package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by ddurkin on 2/11/14.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@Unroll
class AssayCommandUnitSpec extends Specification {

    static
    final List<String> ATTRIBUTE_LABELS = ['biology', 'assay format', 'assay type', 'assay method', 'assay footprint',
            'assay readout', 'readout type', 'readout signal direction', 'measured component', 'detection role',
            'detection method type', 'detection instrument name', 'assay component name', 'assay component type',
            'assay component role']

    SpringSecurityService springSecurityService = Mock()

    void setup() {
        TestDataConfigurationHolder.reset()
        5.times { Element.build() }
        Element.build(label: 'small-molecule format')// want this to have ID 6

        ATTRIBUTE_LABELS.each { Element.build(label: it) }
    }

    void "test AssayCommand biologyContextItems for assayFormat #assayFormatValueLabel"() {
        given: 'an assay command with an assay format set'
        final Element assayFormatValue = Element.findByLabel(assayFormatValueLabel) ?: Element.build(label: assayFormatValueLabel)
        final AssayCommand assayCommand = new AssayCommand()
        assayCommand.springSecurityService = springSecurityService
        assayCommand.assayFormatValueId = assayFormatValue.id.longValue()

        when: 'a new assay is created'
        Assay assay = assayCommand.createNewAssay()

        then:
        assay != null
        assay.groupBiology().value*.contextName == expectedContextNames
        assay.groupBiology().value*.contextItems*.attributeElement?.label.flatten() == expectedBiologyContextItemLabels

        where:
        assayFormatValueLabel      | expectedContextNames | expectedBiologyContextItemLabels
        'small-molecule format'    | []                   | []
        'not mall-molecule format' | ['biology']          | ['biology']
    }

    void "test AssayCommand non biology contexts"() {
        given: 'an assay command with an assay format set'
        final String assayFormatValueLabel = 'small-molecule format'
        final Element assayFormatValue = Element.findByLabel(assayFormatValueLabel) ?: Element.build(label: assayFormatValueLabel)
        final AssayCommand assayCommand = new AssayCommand()
        assayCommand.springSecurityService = springSecurityService
        assayCommand.assayFormatValueId = assayFormatValue.id.longValue()

        when: 'a new assay is created'
        Assay assay = assayCommand.createNewAssay()
        assert assay != null

        then: 'verify Assay Protocal secton'
        assay.groupAssayProtocol().value*.contextName == ['assay format']
        assay.groupAssayProtocol().value*.contextItems*.attributeElement?.label == [['assay format', 'assay type', 'assay method']]
        assay.groupAssayProtocol().value[0].contextItems.find {it.attributeElement.label == 'assay format'}.valueElement?.label == assayFormatValue.label

        and: 'verify Assay Design secton'
        assay.groupAssayDesign().value*.contextName == ['assay footprint']
        assay.groupAssayDesign().value*.contextItems*.attributeElement?.label == [['assay footprint']]

        and: 'verify Assay Readout secton'
        assay.groupAssayReadout().value*.contextName == ['assay readout', 'detection method', 'detection method type']
        assay.groupAssayReadout().value*.contextItems*.attributeElement?.label == [['assay readout', 'readout type', 'readout signal direction'], ['measured component', 'detection role'], ['detection method type', 'detection instrument name']]

        and: 'verify Assay Component secton'
        assay.groupAssayComponents().value*.contextName == ['assay component 1']
        assay.groupAssayComponents().value*.contextItems*.attributeElement?.label == [['assay component name', 'assay component type', 'assay component role']]
    }

}
