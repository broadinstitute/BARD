/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.AssayCommand.SMALL_MOLECULE_FORMAT_LABEL

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
        Element.build(label: SMALL_MOLECULE_FORMAT_LABEL)// want this to have ID 6

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
        SMALL_MOLECULE_FORMAT_LABEL    | []                   | []
        'not mall-molecule format' | ['biology']          | ['biology']
    }

    void "test AssayCommand non biology contexts"() {
        given: 'an assay command with an assay format set'
        final Element assayFormatValue = Element.findByLabel(SMALL_MOLECULE_FORMAT_LABEL) ?: Element.build(label: SMALL_MOLECULE_FORMAT_LABEL)
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
