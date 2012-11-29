package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ElementStatus
import bard.db.dictionary.StageTree
import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import spock.lang.Unroll

import javax.sql.DataSource
import bard.db.dictionary.ResultTypeTree

@Unroll
class DictionaryExportHelperServiceIntegrationSpec extends IntegrationSpec {
    DictionaryExportHelperService dictionaryExportHelperService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate units #label"() {
        when:
        this.dictionaryExportHelperService.generateUnits(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label       | results
        "All Units" | XmlTestSamples.UNITS
    }

    void "test generate Unit Conversions #label"() {
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
        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
        2.times {def resultTypeTree = ResultTypeTree.build(); resultTypeTree.save(flush: true)}
        when:
        this.dictionaryExportHelperService.generateResultTypes(this.markupBuilder)
        then:
        println(this.writer.toString())
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label          | results
        "Result Types" | XmlTestSamples.RESULT_TYPES

    }

    void "test generate Result Type"() {
        given:
        final ResultType resultType = new ResultType(resultTypeStatus: "Published", baseUnit: "uM", resultTypeLabel: "IC50", resultTypeName: "IC50", resultTypeId: 341)
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, resultType)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label         | results
        "Result Type" | XmlTestSamples.RESULT_TYPE1

    }

    void "test generate Element hierarchies #label"() {
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
        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
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
        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
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
        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
        2.times {def stageTree = StageTree.build(); stageTree.save(flush: true)}

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
        when:
        this.dictionaryExportHelperService.generateLabs(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label  | results
        "Labs" | XmlTestSamples.LABS


    }

    void "test generate dictionary #label"() {
        when:
        this.dictionaryExportHelperService.generateDictionary(this.markupBuilder)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        where:
        label        | results
        "Dictionary" | XmlTestSamples.DICTIONARY

    }

}
