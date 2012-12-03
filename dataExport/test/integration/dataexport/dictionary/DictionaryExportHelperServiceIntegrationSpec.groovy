package dataexport.dictionary

import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import spock.lang.Unroll

import javax.sql.DataSource

import bard.db.dictionary.*

@Unroll
class DictionaryExportHelperServiceIntegrationSpec extends IntegrationSpec {
    DictionaryExportHelperService dictionaryExportHelperService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def fixtureLoader

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)

        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
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
            unitConversion(UnitConversion, fromUnit: fromUnit, toUnit: toUnit, multiplier: 1000, offset: 5, formula: '2*2')
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

        def fixture = fixtureLoader.build {
            parentElement(Element, label: 'IC50')
            childElement(Element, label: 'log IC50')
            elementHierarchy(ElementHierarchy, relationshipType: 'subClassOf',
                    parentElement: ref('parentElement'), childElement: ref('childElement'))
        }
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

        final Element element = Element.build(label: 'uM', readyForExtraction: ReadyForExtraction.Ready, elementStatus: ElementStatus.Published)
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

        2.times {Element.build()}
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

        2.times {def treeNode = StageTree.build(); treeNode.save(flush: true)}
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
        final Stage stage = new Stage(stageName: 'construct variant assay', description: 'Description', stageId: 341, label: 'IC50')
        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, stage)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label   | results
        "Stage" | XmlTestSamples.STAGE1

    }

    void "test generate Labs"() {
        given:

        2.times {def treeNode = LaboratoryTree.build(); treeNode.save(flush: true)}
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

        def fixture = fixtureLoader.build {
            parentElement(Element, label: 'IC50')
            childElement(Element, label: 'log IC50')
            elementHierarchy(ElementHierarchy, relationshipType: 'subClassOf', parentElement: ref('parentElement'), childElement: ref('childElement'))
        }
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
