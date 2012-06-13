package barddataexport.dictionary

import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit


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
        assertResults(results)
        where:
        label                 | descriptorType                     | results
        "Assay Descriptor"    | DescriptorType.ASSAY_DESCRIPTOR    | DictionaryIntegrationXml.ASSAY_DESCRIPTOR
        "Biology Descriptor"  | DescriptorType.BIOLOGY_DESCRIPTOR  | DictionaryIntegrationXml.BIOLOGY_DESCRIPTOR
        "Instance Descriptor" | DescriptorType.INSTANCE_DESCRIPTOR | DictionaryIntegrationXml.INSTANCE_DESCRIPTOR
    }

    void "test generate units #label"() {
        when:
        this.dictionaryExportHelperService.generateUnits(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label       | results
        "All Units" | DictionaryIntegrationXml.UNITS
    }

    void "test generate Unit Conversions #label"() {
        when:
        this.dictionaryExportHelperService.generateUnitConversions(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label                  | results
        "All Unit Conversions" | DictionaryIntegrationXml.UNIT_CONVERSIONS

    }

    void "test generate Result Types"() {
        when:
        this.dictionaryExportHelperService.generateResultTypes(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label          | results
        "Result Types" | DictionaryIntegrationXml.RESULT_TYPES

    }

    void "test generate Result Type"() {
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, new BigDecimal("341"))
        then:
        assertResults(results)
        where:
        label         | results
        "Result Type" | DictionaryIntegrationXml.RESULT_TYPE

    }

    void "test generate Element #label"() {
        when:
        this.dictionaryExportHelperService.generateElementHierarchies(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label         | results
        "Hierarchies" | DictionaryIntegrationXml.ELEMENT_HIERARCHIES

    }

    void "test generate element with id"() {
        when:
        this.dictionaryExportHelperService.generateElementWithElementId(this.markupBuilder, new BigDecimal("386"))
        then:
        assertResults(results)
        where:
        label      | results
        "Elements" | DictionaryIntegrationXml.ELEMENT
    }

    void "test generate #label"() {
        when:
        this.dictionaryExportHelperService.generateElements(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label      | results
        "Elements" | DictionaryIntegrationXml.ELEMENTS

    }

    void "test generate Stages"() {
        when:
        this.dictionaryExportHelperService.generateStages(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label    | results
        "Stages" | DictionaryIntegrationXml.STAGES

    }

    void "test generate Stage"() {
        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, new BigDecimal("341"))
        then:
        assertResults(results)
        where:
        label   | results
        "Stage" | DictionaryIntegrationXml.STAGE

    }

    void "test generate Labs"() {
        when:
        this.dictionaryExportHelperService.generateLabs(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label  | results
        "Labs" | DictionaryIntegrationXml.LABS


    }

    void "test generate dictionary"() {
        when:
        this.dictionaryExportHelperService.generateDictionary(this.markupBuilder)
        then:
        assertResults(results)
        where:
        label        | results
        "Dictionary" | DictionaryIntegrationXml.DICTIONARY

    }

    void assertResults(final String results) {
        XMLUnit.setIgnoreWhitespace(true)
        Diff xmlDiff = new Diff(this.writer.toString(), results)
        assert true == xmlDiff.similar()
    }
}
class DictionaryIntegrationXml {
    static String LABS = '''
    <laboratories>
    <laboratory laboratoryId='341' laboratoryStatus='Published'>
    <laboratoryName>LABORATORY</laboratoryName>
      <description>Singular root to ensure tree viewers work</description>
    </laboratory>
  </laboratories>
  '''
    static String UNIT_CONVERSIONS = '''
<unitConversions>
  <unitConversion fromUnit='uM' toUnit='concentration' multiplier='2.5' offset='2'>
    <formula>2*2</formula>
  </unitConversion>
</unitConversions>
'''
    static String UNITS = '''
<units>
  <unit unitId='123' unit='UNIT'>
    <description>Singular root to ensure tree viewers work</description>
  </unit>
  <unit unitId='366' unit='concentration' />
  <unit unitId='386' parentUnitId='366' unit='uM' />
</units>
'''

    static String INSTANCE_DESCRIPTOR = '''
<instanceDescriptor descriptorId='12' elementId='123'>
  <elementStatus>Published</elementStatus>
  <label>macromolecule description</label>
  <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
</instanceDescriptor>
'''

    static String BIOLOGY_DESCRIPTOR = '''
<biologyDescriptor descriptorId='4' elementId='366'>
  <elementStatus>Published</elementStatus>
  <label>macromolecule description</label>
  <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
</biologyDescriptor>
'''
    static String ASSAY_DESCRIPTOR = '''
<assayDescriptor descriptorId='287' elementId='386'>
  <elementStatus>Published</elementStatus>
  <label>assay phase</label>
  <description>It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.</description>
</assayDescriptor>
'''
    static String STAGE = '''
    <stage stageId='341' stageStatus='Published'>
    <stageName>construct variant assay</stageName>
    </stage>
    '''
    static String STAGES = '''
    <stages>
    <stage stageId='341' stageStatus='Published'>
    <stageName>construct variant assay</stageName>
    </stage>
    </stages>
    '''
    static String DICTIONARY = '''
<dictionary>
  <elements>
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
      <label>concentration</label>
      <link rel='edit' href='http://localhost/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
      <label>unit of measurement</label>
      <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
      <link rel='edit' href='http://localhost/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
      <label>IC50</label>
      <link rel='edit' href='http://localhost/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </element>
  </elements>
  <elementHierarchies>
    <elementHierarchy parentElementId='341' childElementId='366'>
      <relationshipType>derives from</relationshipType>
    </elementHierarchy>
  </elementHierarchies>
  <resultTypes>
    <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
      <resultTypeName>IC50</resultTypeName>
    </resultType>
  </resultTypes>
  <stages>
    <stage stageId='341' stageStatus='Published'>
      <stageName>construct variant assay</stageName>
    </stage>
  </stages>
  <biologyDescriptors>
    <biologyDescriptor descriptorId='4' elementId='366'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
    </biologyDescriptor>
  </biologyDescriptors>
  <assayDescriptors>
    <assayDescriptor descriptorId='287' elementId='386'>
      <elementStatus>Published</elementStatus>
      <label>assay phase</label>
      <description>It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.</description>
    </assayDescriptor>
  </assayDescriptors>
  <instanceDescriptors>
    <instanceDescriptor descriptorId='12' elementId='123'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
    </instanceDescriptor>
  </instanceDescriptors>
  <laboratories>
    <laboratory laboratoryId='341' laboratoryStatus='Published'>
      <laboratoryName>LABORATORY</laboratoryName>
      <description>Singular root to ensure tree viewers work</description>
    </laboratory>
  </laboratories>
  <units>
    <unit unitId='123' unit='UNIT'>
      <description>Singular root to ensure tree viewers work</description>
    </unit>
    <unit unitId='366' unit='concentration' />
    <unit unitId='386' parentUnitId='366' unit='uM' />
  </units>
  <unitConversions>
    <unitConversion fromUnit='uM' toUnit='concentration' multiplier='2.5' offset='2'>
      <formula>2*2</formula>
    </unitConversion>
  </unitConversions>
</dictionary>
'''

    static String RESULT_TYPES = '''
<resultTypes>
  <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
  </resultType>
</resultTypes>
'''
    static String RESULT_TYPE = '''
  <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
  </resultType>
'''

    static String ELEMENTS = '''
<elements>
  <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
    <label>uM</label>
    <link rel='edit' href='http://localhost/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
    <label>concentration</label>
    <link rel='edit' href='http://localhost/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
    <label>unit of measurement</label>
    <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
    <link rel='edit' href='http://localhost/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
    <label>IC50</label>
    <link rel='edit' href='http://localhost/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
  </element>
</elements>
'''
    static String ELEMENT = '''
  <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
    <label>uM</label>
    <link rel='edit' href='http://localhost/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
  </element>
'''

    static String ELEMENT_HIERARCHIES = '''
<elementHierarchies>
  <elementHierarchy parentElementId='341' childElementId='366'>
    <relationshipType>derives from</relationshipType>
  </elementHierarchy>
</elementHierarchies>
'''
}