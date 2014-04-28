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

package dataexport.dictionary

import bard.db.dictionary.*
import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.fixtures.FixtureLoader
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import spock.lang.Unroll

import javax.sql.DataSource
import bard.db.audit.BardContextUtils
import org.hibernate.SessionFactory

@Unroll
class DictionaryExportHelperServiceIntegrationSpec extends IntegrationSpec {
    DictionaryExportHelperService dictionaryExportHelperService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    FixtureLoader fixtureLoader
    SessionFactory sessionFactory

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)

        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')

        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
//        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate units #label"() {
        given:

        def fixture = fixtureLoader.build {
            concentrationElement(Element, label: 'concentration')
            uM(Element, label: 'uM')
            unitTreeConcentration(UnitTree, element: concentrationElement)
            unitTreeUm(UnitTree, element: ref('uM'), parent: ref('unitTreeConcentration'))
        }

        when:
        this.dictionaryExportHelperService.generateUnits(this.markupBuilder)
        then:

        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label       | results
        "All Units" | XmlTestSamples.UNITS
    }

    void "test generate Unit Conversions #label"() {
        given:
        def fixture = fixtureLoader.build {
            fromUnit(Element, label: 'micromolar')
            toUnit(Element, label: 'millimolar')
            unitConversion(UnitConversion, fromUnit: fromUnit, toUnit: toUnit, multiplier: 1000.1, offset: 5.1, formula: '2*2')
        }

        when:
        this.dictionaryExportHelperService.generateUnitConversions(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                  | results
        "All Unit Conversions" | XmlTestSamples.UNIT_CONVERSIONS

    }

    void "test generate Result Types"() {
        given:

        def fixture = fixtureLoader.build {
            element(Element, label: 'IC50', elementStatus: ElementStatus.Published)
            baseUnit(Element, label: 'uM')
            resultTypeTree(ResultTypeTree, element: element, baseUnit: baseUnit)
        }

        when:
        this.dictionaryExportHelperService.generateResultTypes(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label          | results
        "Result Types" | XmlTestSamples.RESULT_TYPES
    }

    void "test generate Result Type"() {
        given:

        def fixture = fixtureLoader.build {
            element(Element, label: 'IC50', elementStatus: ElementStatus.Published)
            baseUnit(Element, label: 'uM')
            resultTypeTree(ResultTypeTree, element: element, baseUnit: baseUnit)
        }

        fixture.resultTypeTree.save(flush: true)

        //final ResultType resultType = new ResultType(resultTypeStatus: "Published", baseUnit: "uM", resultTypeLabel: "IC50", resultTypeName: "IC50", resultTypeId: 341)
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, fixture.resultTypeTree)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label         | results
        "Result Type" | XmlTestSamples.RESULT_TYPE1

    }

    void "test generate Element hierarchies #label"() {
        given:

        Element parentElement = Element.build()
        parentElement.label = "IC50"
        Element childElement = Element.build()
        childElement.label = "log IC50"
        ElementHierarchy elementHierarchy = new ElementHierarchy(relationshipType: "subClassOf",
                parentElement: parentElement, childElement: childElement, dateCreated: new Date())
        assert elementHierarchy.save()
        parentElement.parentHierarchies.add(elementHierarchy)
        childElement.childHierarchies.add(elementHierarchy)


//        def fixture = fixtureLoader.build {
//            parentElement(Element, label: 'IC50')
//            childElement(Element, label: 'log IC50')
//            elementHierarchy(ElementHierarchy, relationshipType: 'subClassOf',
//                    parentElement: ref('parentElement'), childElement: ref('childElement'))
//        }
        when:
        this.dictionaryExportHelperService.generateElementHierarchies(this.markupBuilder)
        then:

        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label         | results
        "Hierarchies" | XmlTestSamples.ELEMENT_HIERARCHIES

    }

    void "test generate element with id"() {
        given:

        final Element element = Element.build(label: 'uM', readyForExtraction: ReadyForExtraction.READY, elementStatus: ElementStatus.Published)
        when:
        this.dictionaryExportHelperService.generateElement(this.markupBuilder, element)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        where:
        label      | results
        "Elements" | XmlTestSamples.ELEMENT
    }


    void "test generate elements #label"() {
        given:

        2.times { Element.build() }
        when:
        this.dictionaryExportHelperService.generateElements(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label      | results
        "Elements" | XmlTestSamples.ELEMENTS

    }

    void "test generate Stages"() {
        given:
        2.times { def treeNode = StageTree.build(); treeNode.save(flush: true) }

        when:
        this.dictionaryExportHelperService.generateStages(this.markupBuilder)

        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())

        where:
        label    | results
        "Stages" | XmlTestSamples.STAGES
    }

    void "test generate Stage"() {
        given:
        def fixture = fixtureLoader.build {
            element(Element, label: 'IC50', description: 'Description')
            stageTree(StageTree, element: element)
        }

        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, fixture.stageTree)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label   | results
        "Stage" | XmlTestSamples.STAGE1

    }

    void "test generate Labs"() {
        given:

        2.times { def treeNode = LaboratoryTree.build(); treeNode.save(flush: true) }
        when:
        this.dictionaryExportHelperService.generateLabs(this.markupBuilder)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label  | results
        "Labs" | XmlTestSamples.LABS


    }

    void "test generate dictionary #label"() {
        given:


        Element parentElement = Element.build(label: 'IC50')
        Element childElement = Element.build(label: 'log IC50')
        ElementHierarchy.build(relationshipType: 'subClassOf', parentElement: parentElement, childElement: childElement)

        ResultTypeTree.build(id: 1)
        StageTree.build(id: 1)
        AssayDescriptor.build(id: 1)
        BiologyDescriptor.build(id: 1)
        InstanceDescriptor.build(id: 1)
        LaboratoryTree.build(id: 1)
        UnitTree.build(id: 1)
        UnitConversion.build(fromUnit: Element.build(label: 'micromolar'),
                toUnit: Element.build(label: 'millimolar'),
                multiplier: 1000)

        when:
        this.dictionaryExportHelperService.generateDictionary(this.markupBuilder)
        then:

        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        where:
        label        | results
        "Dictionary" | XmlTestSamples.DICTIONARY

    }

}
