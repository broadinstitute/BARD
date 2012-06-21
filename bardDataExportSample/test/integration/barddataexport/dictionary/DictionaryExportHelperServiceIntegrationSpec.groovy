package barddataexport.dictionary

import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder

import common.tests.XmlTestAssertions

import common.tests.XmlTestSamples


class DictionaryExportHelperServiceIntegrationSpec extends IntegrationSpec {
    DictionaryExportHelperService dictionaryExportHelperService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test generate #label #descriptorType"() {
        when:
        this.dictionaryExportHelperService.generateDescriptors(this.markupBuilder, descriptorType)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label                 | descriptorType                     | results
        "Assay Descriptor"    | DescriptorType.ASSAY_DESCRIPTOR    | XmlTestSamples.ASSAY_DESCRIPTOR
        "Biology Descriptor"  | DescriptorType.BIOLOGY_DESCRIPTOR  | XmlTestSamples.BIOLOGY_DESCRIPTOR
        "Instance Descriptor" | DescriptorType.INSTANCE_DESCRIPTOR | XmlTestSamples.INSTANCE_DESCRIPTOR
    }

    void "test generate units #label"() {
        when:
        this.dictionaryExportHelperService.generateUnits(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label       | results
        "All Units" | XmlTestSamples.UNITS
    }

    void "test generate Unit Conversions #label"() {
        when:
        this.dictionaryExportHelperService.generateUnitConversions(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label                  | results
        "All Unit Conversions" | XmlTestSamples.UNIT_CONVERSIONS

    }

    void "test generate Result Types"() {
        when:
        this.dictionaryExportHelperService.generateResultTypes(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label          | results
        "Result Types" | XmlTestSamples.RESULT_TYPES

    }

    void "test generate Result Type"() {
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, new BigDecimal("341"))
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label         | results
        "Result Type" | XmlTestSamples.RESULT_TYPE

    }

    void "test generate Element #label"() {
        when:
        this.dictionaryExportHelperService.generateElementHierarchies(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label         | results
        "Hierarchies" | XmlTestSamples.ELEMENT_HIERARCHIES

    }

    void "test generate element with id"() {
        when:
        this.dictionaryExportHelperService.generateElementWithElementId(this.markupBuilder, new BigDecimal("386"))
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results,this.writer.toString())
        where:
        label      | results
        "Elements" | XmlTestSamples.ELEMENT
    }

    void "test generate elements #label"() {
        when:
        this.dictionaryExportHelperService.generateElements(this.markupBuilder)
        then:
        println writer.toString()
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label      | results
        "Elements" | XmlTestSamples.ELEMENTS

    }

    void "test generate Stages"() {
        when:
        this.dictionaryExportHelperService.generateStages(this.markupBuilder)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results,this.writer.toString())
        where:
        label    | results
        "Stages" | XmlTestSamples.STAGES

    }

    void "test generate Stage"() {
        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, new BigDecimal("341"))
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label   | results
        "Stage" | XmlTestSamples.STAGE

    }

    void "test generate Labs"() {
        when:
        this.dictionaryExportHelperService.generateLabs(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results,this.writer.toString())
        where:
        label  | results
        "Labs" | XmlTestSamples.LABS


    }

    void "test generate dictionary"() {
        when:
        this.dictionaryExportHelperService.generateDictionary(this.markupBuilder)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results,this.writer.toString())
        where:
        label        | results
        "Dictionary" | XmlTestSamples.DICTIONARY

    }

}
