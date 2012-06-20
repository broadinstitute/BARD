package common.tests

/**
 * We put this here because we cannot find any place in grails
 * to put helper test classes.
 *
 * There is an outstanding ticket that asks for this implementation in Grails.
 * http://jira.grails.org/browse/GRAILS-7750
 *
 * Until that is done, we will keep this here and exclude it BuildConfig.groovy when we generate a war file
 *
 * grails.war.resources = { stagingDir ->
 delete(file:"${stagingDir}/WEB-INF/classes/common/tests/XmlTestSamples.class")
 delete(file:"${stagingDir}/WEB-INF/classes/common/tests/XmlTestAssertions.class")
 delete(dir:"${stagingDir}/WEB-INF/classes/common/tests")}*/
class XmlTestSamples {
    static String ASSAY_LINKS = '''
<links>
  <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/assay/1' type='application/vnd.bard.cap+xml;type=assay' />
  <link rel='self' href='http://localhost:8080/bardDataExportSample/api/assay/1' type='application/vnd.bard.cap+xml;type=assay' />
  <link rel='up' href='http://localhost:8080/bardDataExportSample/api/assays' type='application/vnd.bard.cap+xml;type=assays' />
</links>
'''
    static String ASSAYS = '''
<assays count='1'>
  <link rel='related' title='1' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost:8080/bardDataExportSample/api/assay/1' />
</assays>
'''

    static String EXTERNAL_ASSAY = '''
    <externalAssay externalAssayId='aid=644'>
    <externalSystem>
    <systemName>PubChem</systemName>
    <owner>NIH</owner>
    <systemUrl>http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=644</systemUrl>
    </externalSystem>
</externalAssay>
 '''
    static String EMPTY_EXTERNAL_ASSAY = '''
   <externalAssay externalAssayId='aid=666' />
    '''


    static String EXTERNAL_ASSAYS = '''
<externalAssays>
  <externalAssay externalAssayId='aid=644'>
    <externalSystem>
      <systemName>PubChem</systemName>
      <owner>NIH</owner>
      <systemUrl>http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=644</systemUrl>
    </externalSystem>
  </externalAssay>
</externalAssays>
'''
    static String EMPTY_EXTERNAL_ASSAYS = '''
    <externalAssays/>
    '''

    static String MEASURE_CONTEXT_ITEMS = '''
<measureContextItems>
  <measureContextItem measureContextItemId='34' measureContextRef='2' valueDisplay='Assay Explorer '>
    <valueId>
      <link rel='related' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
    </valueId>
    <attributeId attributeType='Fixed'>
      <link rel='related' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </measureContextItem>
  <measureContextItem measureContextItemId='35' measureContextItemRef='34' measureContextRef='2' valueDisplay='30' valueNum='30'>
    <attributeId attributeType='Fixed'>
      <link rel='related' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </measureContextItem>
  <measureContextItem measureContextItemId='36' measureContextItemRef='34' measureContextRef='2' valueDisplay='0 - 4' valueMin='0' valueMax='4'>
    <attributeId attributeType='Range'>
      <link rel='related' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </measureContextItem>
</measureContextItems>
'''
    static String MEASURE_CONTEXTS = '''
   <measureContexts>
  <measureContext measureContextId='2'>
    <contextName>Context for IC50</contextName>
  </measureContext>
</measureContexts>
'''
    static String EMPTY_MEASURE_CONTEXT_ITEMS = '''
<measureContextItems />
'''
    static String EMPTY_MEASURE_CONTEXTS = '''
     <measureContexts/>
     '''

    static String BARD_DATA_EXPORT_UNIT = '''
   <bardexport>
    <link rel='item' title='The BARD Dictionary' href='null' type='xml' />
    </bardexport>
'''
    static String BARD_DATA_EXPORT = '''
<bardexport>
    <link rel='item' title='The BARD Dictionary' href='http://localhost:8080/bardDataExportSample/api/dictionary' type='application/vnd.bard.cap+xml;type=dictionary' />
    </bardexport>
    '''
    static String UNIT_CONVERSION_FULL = '''
<unitConversion fromUnit='fromUnit' toUnit='toUnit' multiplier='1' offset='1'>
  <formula>formula</formula>
</unitConversion>
'''
    static String UNIT_CONVERSION_NO_MULTIPLIER = '''
    <unitConversion fromUnit='fromUnit' toUnit='toUnit'>
    <formula>formula</formula>
</unitConversion>
'''
    static String STAGES = '''
<stages>
    <stage stageId='341' stageStatus='Published'>
      <stageName>construct variant assay</stageName>
    </stage>
    </stages>
'''
    static String STAGE = '''
    <stage stageId='341' stageStatus='Published'>
      <stageName>construct variant assay</stageName>
    </stage>
'''
    static String RESULT_TYPE = '''
    <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
      <resultTypeName>IC50</resultTypeName>
    </resultType>
'''
    static String ELEMENT = '''
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    '''
    static String DICTIONARY = '''
<dictionary>
<elements>
  <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
    <label>uM</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
    <label>concentration</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
    <label>unit of measurement</label>
    <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='372' readyForExtraction='Ready' elementStatus='Published'>
    <label>Assay Explorer</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='370' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of points</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='369' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of exclusions</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='368' readyForExtraction='Ready' elementStatus='Published'>
    <label>software</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
    <label>IC50</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
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

    static String RESULT_TYPES = '''
<resultTypes>
  <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
  </resultType>
</resultTypes>
'''


    static String ELEMENTS = '''
<elements>
  <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
    <label>uM</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
    <label>concentration</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
    <label>unit of measurement</label>
    <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='372' readyForExtraction='Ready' elementStatus='Published'>
    <label>Assay Explorer</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='370' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of points</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='369' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of exclusions</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='368' readyForExtraction='Ready' elementStatus='Published'>
    <label>software</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
    <label>IC50</label>
    <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
  </element>
</elements>
'''


    static String ELEMENT_HIERARCHIES = '''
<elementHierarchies>
  <elementHierarchy parentElementId='341' childElementId='366'>
    <relationshipType>derives from</relationshipType>
  </elementHierarchy>
</elementHierarchies>
'''

    static String INSTANCE_DESCRIPTOR_UNIT = '''
<instanceDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</instanceDescriptor>
'''

    static String BIOLOGY_DESCRIPTOR_UNIT = '''
<biologyDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</biologyDescriptor>
'''
    static String ASSAY_DESCRIPTOR_UNIT = '''
<assayDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</assayDescriptor>
'''

    static String RESULT_TYPE_NO_PARENT = '''
<resultType resultTypeId='1' abbreviation='abb' baseUnit='cm' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
</resultType>
'''

    static String RESULT_TYPE_FULL = '''
<resultType resultTypeId='1' parentResultTypeId='1' abbreviation='abb' baseUnit='cm' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
</resultType>
'''


    static String ELEMENT_FULL = '''
<element elementId='1' readyForExtraction='ready' elementStatus='status' abbreviation='abb' unit='cm'>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
</element>
'''
    static String ELEMENT_NO_DESCRIPTION = '''
<element elementId='1' readyForExtraction='ready' elementStatus='status' unit='cm'>
  <label>label</label>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
</element>
'''
    static String ELEMENT_HIERARCHY_FULL = '''
<elementHierarchy parentElementId='1' childElementId='2'>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''

    static String ELEMENT_HIERARCHY_NO_PARENT = '''
<elementHierarchy childElementId='2'>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''
    static String STAGE_FULL = '''
    <stage stageId='1' parentStageId='2' stageStatus='Status'>
    <stageName>Stage</stageName>
  <description>desc</description>
    </stage>
    '''

    static String STAGE_NO_PARENT = '''
<stage stageId='1' stageStatus='Status'>
  <stageName>Stage</stageName>
  <description>desc</description>
</stage>
'''

    static String SINGLE_UNIT = '''
<unit unitId='1' parentUnitId='1' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String SINGLE_UNIT_NO_DESCRIPTION = '''
<unit unitId='1' parentUnitId='1' unit='cm'>
</unit>
'''
    static String SINGLE_UNIT_NO_PARENT = '''
<unit unitId='1' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String LABORATORY_SAMPLE_FULL = '''
<laboratory laboratoryId='1' parentLaboratoryId='2' laboratoryStatus='Status'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''

    static String LABORATORY_SAMPLE_NO_PARENT = '''
<laboratory laboratoryId='1' laboratoryStatus='Status'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''
}
