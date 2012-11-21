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
    //We include a dummy header, just so we can validate, otherwise the XML is not well-formed
    static final String EXPERIMENTS_LINK_UNIT = '''
<dummyHeader>
  <link rel='related' title='Link to Assay' type='assayMediaType' href='null' />
  <link rel='up' title='List Experiments' type='experimentsMediaType' href='null' />
  <link rel='related' title='List Related Results' type='resultsMediaType' href='null' />
  <link rel='edit' title='Use link to edit Experiment' type='experimentMediaType' href='null' />
</dummyHeader>
    '''

    static final String RESULT_CONTEXT_ITEM_UNIT = '''
  <resultContextItem resultContextItemId='null' qualifier='%' valueDisplay='20 %' valueNum='2.0' valueMin='1.0' valueMax='3.0'>
    <attribute label='attrribute'>
      <link rel='related' href='null' type='elementMediaType' />
    </attribute>
    <valueControlled label='valueControlled'>
      <link rel='related' href='null' type='elementMediaType' />
    </valueControlled>
  </resultContextItem>
'''
    static final String RESULT_CONTEXT_ITEM_UNIT_NO_CHILD_ELEMENTS = '''
  <resultContextItem resultContextItemId='null' qualifier='%' valueDisplay='20 %' valueNum='2.0' valueMin='1.0' valueMax='3.0' />
'''
    static final String RESULT_CONTEXT_ITEMS_UNIT = '''
<resultContextItems>
  <resultContextItem resultContextItemId='null' qualifier='%' valueDisplay='20 %' valueNum='2.0' valueMin='1.0' valueMax='3.0'>
    <attribute label='attrribute'>
      <link rel='related' href='null' type='elementMediaType' />
    </attribute>
    <valueControlled label='valueControlled'>
      <link rel='related' href='null' type='elementMediaType' />
    </valueControlled>
  </resultContextItem>
</resultContextItems>
'''
    static final String RESULT_CONTEXT_ITEMS_UNIT_NO_CHILD_ELEMENTS = '''
<resultContextItems>
  <resultContextItem resultContextItemId='null' qualifier='%' valueDisplay='20 %' valueNum='2.0' valueMin='1.0' valueMax='3.0' />
</resultContextItems>
'''

    static final String PROJECT_STEPS_UNIT = '''
<projectSteps>
  <projectStep>
    <description>description</description>
    <precedingExperiment id='null'>
      <link rel='related' href='null' type='experimentMediaType' />
    </precedingExperiment>
    <link rel='related' href='null' type='projectMediaType' />
  </projectStep>
</projectSteps>
'''
    static final String PROJECT_STEP_UNIT = '''
  <projectStep>
    <description>description</description>
    <precedingExperiment id='null'>
      <link rel='related' href='null' type='experimentMediaType' />
    </precedingExperiment>
    <link rel='related' href='null' type='projectMediaType' />
   </projectStep>
'''
    static final String PROJECT_STEP_UNIT_NO_CHILD_ELEMENTS = '''
    <projectStep>
    <description>description</description>
    <link rel='related' href='null' type='projectMediaType' />
    </projectStep>
'''
    static final String PROJECT_STEPS_UNIT_NO_CHILD_ELEMENTS = '''
    <projectSteps>
    <projectStep>
    <description>description</description>
    <link rel='related' href='null' type='projectMediaType' />
    </projectStep>
</projectSteps>
'''
    static final String EXTERNAL_REFERENCES_UNT = '''
<externalReferences>
<externalReference>
    <externalAssayRef>External Assay Ref</externalAssayRef>
  <externalSystem name='systemName' owner='owner'>
    <systemUrl>http://broad.org</systemUrl>
  </externalSystem>
    <link rel='related' href='null' type='projectMediaType' />
    </externalReference>
    </externalReferences>
    '''
    static final String EXTERNAL_REFERENCE_UNT = '''
<externalReference>
    <externalAssayRef>External Assay Ref</externalAssayRef>
  <externalSystem name='systemName' owner='owner'>
    <systemUrl>http://broad.org</systemUrl>
  </externalSystem>
    <link rel='related' href='null' type='projectMediaType' />
    </externalReference>
    '''

    static final String EXPERIMENT_UNIT_ONLY_ATTRIBUTES = '''
<experiment experimentId='' experimentName='Experiment1' status='Published' readyForExtraction='Pending' holdUntilDate='1969-12-31T19:00:00.000-05:00' runDateFrom='1969-12-31T19:00:00.000-05:00' runDateTo='1969-12-31T19:00:00.000-05:00'>
  <link rel='related' title='Link to Assay' type='assayMediaType' href='null' />
  <link rel='up' title='List Experiments' type='experimentsMediaType' href='null' />
  <link rel='related' title='List Related Results' type='resultsMediaType' href='null' />
  <link rel='edit' title='Use link to edit Experiment' type='experimentMediaType' href='null' />
</experiment>
    '''
    static final String EXPERIMENT_UNIT_ATTRIBUTES_AND_ELEMENTS = '''
<experiment experimentId='' experimentName='Experiment1' status='Published' readyForExtraction='Pending' holdUntilDate='1969-12-31T19:00:00.000-05:00' runDateFrom='1969-12-31T19:00:00.000-05:00' runDateTo='1969-12-31T19:00:00.000-05:00'>
  <description>Broad</description>
  <experimentContextItems>
    <experimentContextItem experimentContextItemId='' qualifier='&lt;' valueDisplay='&lt; 20 uM' valueNum='1.0' valueMin='5.0' valueMax='20.0'>
      <attribute label='attribute'>
        <link rel='related' href='null' type='elementMediaType' />
      </attribute>
      <valueControlled label='valueControlled'>
        <link rel='related' href='null' type='elementMediaType' />
      </valueControlled>
    </experimentContextItem>
  </experimentContextItems>
  <projectSteps>
    <projectStep>
      <description>Broad</description>
      <precedingExperiment id='null'>
        <link rel='related' href='null' type='experimentMediaType' />
      </precedingExperiment>
      <link rel='related' href='null' type='projectMediaType' />
    </projectStep>
  </projectSteps>
  <externalReferences>
    <externalReference>
      <externalAssayRef>External Assay Ref</externalAssayRef>
      <externalSystem name='systemName' owner='owner'>
        <systemUrl>http://broad.org</systemUrl>
      </externalSystem>
      <link rel='related' href='null' type='projectMediaType' />
    </externalReference>
  </externalReferences>
  <link rel='related' title='Link to Assay' type='assayMediaType' href='null' />
  <link rel='up' title='List Experiments' type='experimentsMediaType' href='null' />
  <link rel='related' title='List Related Results' type='resultsMediaType' href='null' />
  <link rel='edit' title='Use link to edit Experiment' type='experimentMediaType' href='null' />
</experiment>
'''
    static final String EXPERIMENTS_2_RECORDS_UNIT = '''
   <experiments count='2'>
  <link rel='related' type='experimentMediaType' href='null' />
  <link rel='related' type='experimentMediaType' href='null' />
</experiments>
'''
    static final String EXPERIMENTS_2_RECORDS_WITH_NEXT_UNIT = '''
<experiments count='2'>
  <link rel='related' type='experimentMediaType' href='null' />
  <link rel='related' type='experimentMediaType' href='null' />
</experiments>
'''
    static final String EXPERIMENT_SINGLE_RECORD_UNIT = '''
<experiments count='2'>
  <link rel='related' type='experimentMediaType' href='null' />
  <link rel='related' type='experimentMediaType' href='null' />
  <link rel='next' title='List Experiments' type='experimentsMediaType' href='null' />
</experiments>
'''

    static final String PROJECTS = '''
<projects count='2'>
    <project projectId='1' readyForExtraction='Ready' groupType='Project'>
    <projectName>Scripps special project #1</projectName>
    <projectSteps>
      <projectStep projectStepId='1'>
        <description>2126 - MLPCN Malaria - Inhibitor</description>
    <link rel='related' href='http://localhost:8080/dataExport/api/experiments/1' type='application/vnd.bard.cap+xml;type=experiment' />
    </projectStep>
      <projectStep projectStepId='2'>
        <description>2127 - MLPCN Malaria2 - Inhibitor</description>
    <link rel='related' href='http://localhost:8080/dataExport/api/experiments/2' type='application/vnd.bard.cap+xml;type=experiment' />
    </projectStep>
    </projectSteps>
    <link rel='edit' href='http://localhost:8080/dataExport/api/projects/1' type='application/vnd.bard.cap+xml;type=project' />
    <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
  <project projectId='2' readyForExtraction='Ready' groupType='Project'>
    <projectName>2126 - MLPCN Malaria - Inhibitor</projectName>
    <link rel='edit' href='http://localhost:8080/dataExport/api/projects/2' type='application/vnd.bard.cap+xml;type=project' />
    <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
</projects>
'''


    static final String PROJECT = '''
    <project projectId='1' readyForExtraction='Ready' groupType='Project'>
    <projectName>Scripps special project #1</projectName>
  <projectSteps>
    <projectStep projectStepId='1'>
      <description>2126 - MLPCN Malaria - Inhibitor</description>
    <link rel='related' href='http://localhost:8080/dataExport/api/experiments/1' type='application/vnd.bard.cap+xml;type=experiment' />
    </projectStep>
    <projectStep projectStepId='2'>
      <description>2127 - MLPCN Malaria2 - Inhibitor</description>
    <link rel='related' href='http://localhost:8080/dataExport/api/experiments/2' type='application/vnd.bard.cap+xml;type=experiment' />
    </projectStep>
  </projectSteps>
    <link rel='edit' href='http://localhost:8080/dataExport/api/projects/1' type='application/vnd.bard.cap+xml;type=project' />
    <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
'''
    static final String PROJECT_WITH_DESCRIPTION = '''
<project projectId='' readyForExtraction='Ready' groupType='Panel'>
  <projectName>Project Name2</projectName>
  <description>Broad</description>
  <link rel='edit' href='null' type='projectMediaType' />
  <link rel='up' href='null' type='projectsMediaType' />
</project>
'''
    static final String PROJECT_NO_DESCRIPTION = '''
<project projectId='' readyForExtraction='Ready' groupType='Project'>
  <projectName>Project Name1</projectName>
  <link rel='edit' href='null' type='projectMediaType' />
  <link rel='up' href='null' type='projectsMediaType' />
</project>
'''
    static final String ASSAY_NO_DESIGNER_UNIT = '''
        <assay assayId='1' readyForExtraction='Pending' assayVersion='assayVersi' assayType='Regular' status='Pending'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <link rel='edit' href='null' type='xml' />
          <link rel='self' href='null' type='xml' />
          <link rel='up' href='null' type='xml' />
        </assay> '''
    static final String ASSAY_WITH_DESIGNER_NAME = '''
        <assay assayId='1' readyForExtraction='Pending' assayVersion='assayVersi' assayType='Regular' status='Pending'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <designedBy>Broad</designedBy>
          <link rel='edit' href='null' type='xml' />
          <link rel='self' href='null' type='xml' />
          <link rel='up' href='null' type='xml' />
        </assay> '''
    static final String ASSAY_WITH_DOCUMENT = '''
         <assay assayId='1' readyForExtraction='Pending' assayVersion='assayVersi' assayType='Regular' status='Pending'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <link rel='edit' href='null' type='xml' />
          <link rel='self' href='null' type='xml' />
          <link rel='up' href='null' type='xml' />
          <link rel='item' href='null' type='xml' />
        </assay> '''

    static final String ASSAY_DOCUMENT_SERVER = '''
        <assayDocument documentType='Protocol'>
            <documentName>Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)</documentName>
            <documentContent>Some Document1</documentContent>
            <link rel='item' href='http://localhost:8080/dataExport/api/assayDocument/1' type='application/vnd.bard.cap+xml;type=assayDoc' />
        </assayDocument>'''

    static final String MINIMAL_ASSAY_DOCUMENT = '''
        <assayDocument documentType='Description'>
            <documentName>documentName</documentName>
            <link rel='self' href='null' type='xml' />
            <link rel='related' href='null' type='xml' />
        </assayDocument>'''

    static final String ASSAY_DOCUMENT_WITH_CONTENT = '''
        <assayDocument documentType='Description'>
            <documentName>documentName</documentName>
            <documentContent>Content</documentContent>
            <link rel='self' href='null' type='xml' />
            <link rel='related' href='null' type='xml' />
        </assayDocument>'''

    static final String ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE = '''
        <assayContextItem assayContextItemId='1' displayOrder='0' qualifier='&lt;' valueDisplay='Display' valueNum='5.0' valueMin='6.0' valueMax='7.0'>
          <attributeId attributeType='Fixed' label='attributeLabel'>
            <link rel='related' href='null' type='xml' />
          </attributeId>
        </assayContextItem>'''

    static final String ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE = '''
        <assayContextItem assayContextItemId='1' displayOrder='0' qualifier='&lt;' valueDisplay='Display' valueNum='5.0' valueMin='6.0' valueMax='7.0'>
          <valueId label='valueLabel'>
            <link rel='related' href='null' type='xml' />
          </valueId>
          <attributeId attributeType='Fixed' label='attributeLabel'>
            <link rel='related' href='null' type='xml' />
          </attributeId>
        </assayContextItem>'''

    static final String MINIMAL_MEASURE = '''
        <measure measureId='1'>
          <resultTypeRef label='resultType'>
            <link rel='related' href='null' type='xml' />
          </resultTypeRef>
        </measure>'''
    static final String MINIMAL_MEASURE_WITH_PARENT_MEASURE_REF = '''
        <measure measureId='2' parentMeasureRef='1'>
          <resultTypeRef label='resultType'>
            <link rel='related' href='null' type='xml' />
          </resultTypeRef>
        </measure>'''

    static final String MINIMAL_MEASURE_WITH_STATS_MODIFIER_REF = '''
        <measure measureId='1'>
          <resultTypeRef label='resultType'>
            <link rel='related' href='null' type='xml' />
          </resultTypeRef>
          <statsModifierRef label='statsModifier'>
            <link rel='related' href='null' type='xml' />
          </statsModifierRef>
        </measure>'''

    static final String MINIMAL_MEASURE_WITH_ENTRY_UNIT_REF = '''
        <measure measureId='1'>
          <resultTypeRef label='resultType'>
            <link rel='related' href='null' type='xml' />
          </resultTypeRef>
          <entryUnitRef label='entryUnit'>
            <link rel='related' href='null' type='xml' />
          </entryUnitRef>
        </measure>'''
    static final String MINIMAL_MEASURE_WITH_ASSAY_CONTEXT_REFS = '''
        <measure measureId='1' >
          <resultTypeRef label='resultType'>
            <link rel='related' href='null' type='xml' />
          </resultTypeRef>
          <assayContextRefs>
            <assayContextRef>20</assayContextRef>
          </assayContextRefs>
        </measure>'''

    static final String MINIMAL_ASSAY_CONTEXT = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName>contextName</contextName>
        </assayContext> '''

    static final String MINIMAL_ASSAY_CONTEXT_WITH_CONTEXT_GROUP = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName>contextName</contextName>
          <contextGroup>contextGroup</contextGroup>
        </assayContext> '''

    static final String MINIMAL_ASSAY_CONTEXT_WITH_ASSAY_CONTEXT_ITEM = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName>contextName</contextName>
          <assayContextItems>
            <assayContextItem assayContextItemId='1' displayOrder='0'>
              <attributeId attributeType='Fixed' label='label1'>
                <link rel='related' href='null' type='xml' />
              </attributeId>
            </assayContextItem>
          </assayContextItems>
        </assayContext> '''

    static final String MINIMAL_ASSAY_CONTEXT_WITH_MEASURE_REFS = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName>contextName</contextName>
          <measureRefs>
            <measureRef>1</measureRef>
          </measureRefs>
        </assayContext> '''

    static String ASSAY_LINKS = '''
<links>
  <link rel='edit' href='http://localhost:8080/dataExport/api/assays/1' type='application/vnd.bard.cap+xml;type=assay' />
  <link rel='self' href='http://localhost:8080/dataExport/api/assays/1' type='application/vnd.bard.cap+xml;type=assay' />
  <link rel='up' href='http://localhost:8080/dataExport/api/assays' type='application/vnd.bard.cap+xml;type=assays' />
  <link rel='related' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost:8080/dataExport/api/experiments/1' />
  <link rel='related' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost:8080/dataExport/api/experiments/2' />
  <link rel='related' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost:8080/dataExport/api/experiments/23' />
</links>
'''
    static String PROJECT_FROM_SERVER = '''
   <project projectId='1' readyForExtraction='Ready' groupType='Project'>
   <projectName>Scripps special project #1</projectName>
   <link rel='edit' href='http://localhost:8080/dataExport/api/projects/1' type='application/vnd.bard.cap+xml;type=project' />
   <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
   </project>

    '''
    static String PROJECTS_FROM_SERVER = '''
<projects count='2'>  <project projectId='1' readyForExtraction='Ready' groupType='Project'>
    <projectName>Scripps special project #1</projectName>    <projectSteps>
        <projectStep projectStepId='2'>        <description>2127 - MLPCN Malaria2 - Inhibitor</description>
            <link rel='related' href='http://localhost:8080/dataExport/api/experiments/2' type='application/vnd.bard.cap+xml;type=experiment' />
        </projectStep>      <projectStep projectStepId='1'>        <description>2126 - MLPCN Malaria - Inhibitor</description>
            <link rel='related' href='http://localhost:8080/dataExport/api/experiments/1' type='application/vnd.bard.cap+xml;type=experiment' />
        </projectStep>
    </projectSteps>
    <link rel='edit' href='http://localhost:8080/dataExport/api/projects/1' type='application/vnd.bard.cap+xml;type=project' />
    <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
</project>
    <project projectId='2' readyForExtraction='Ready' groupType='Project'>
        <projectName>2126 - MLPCN Malaria - Inhibitor</projectName>
        <link rel='edit' href='http://localhost:8080/dataExport/api/projects/2' type='application/vnd.bard.cap+xml;type=project' />
        <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
</projects>
'''
    static String ASSAYS_FROM_SERVER = '''
    <assays count='1'>
    <link rel='related' title='1' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost:8080/dataExport/api/assays/1'/>
    </assays>
    '''

    static String ASSAYS = '''
<assays count='1'>
  <link rel='related' title='1' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost:8080/dataExport/api/assays/1' />
</assays>
'''

    static String ASSAY_DOCUMENTS = '''
       <assayDocuments>
    <assayDocument documentType='Protocol'>
    <documentName>Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)</documentName>
    <link rel='item' href='http://localhost:8080/dataExport/api/assayDocument/1' type='application/vnd.bard.cap+xml;type=assayDoc' />
    </assayDocument>
  <assayDocument documentType='Protocol'>
    <documentName>Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2) pt 2</documentName>
    <link rel='item' href='http://localhost:8080/dataExport/api/assayDocument/2' type='application/vnd.bard.cap+xml;type=assayDoc' />
    </assayDocument>
</assayDocuments>
    '''
    static String MEASURES = '''
    <measures>
    <measure assayContextRef='Context for IC50'>
    <resultTypeRef label='IC50'>
    <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/resultType/341' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
    <entryUnit unit='uM' />
    </measure>
</measures>
'''


    static String MEASURE_CONTEXT_ITEMS = '''
<assayContextItems>
  <assayContextItem assayContextItemId='36' assayContextItemRef='34' assayContextRef='Context for IC50' valueDisplay='0 - 4' valueMax='4.0'>
    <attributeId attributeType='Range' label='Number of exclusions'>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </assayContextItem>
  <assayContextItem assayContextItemId='34' assayContextRef='Context for IC50' valueDisplay='Assay Explorer '>
    <valueId label='Assay Explorer'>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
    </valueId>
    <attributeId attributeType='Fixed' label='software'>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </assayContextItem>
  <assayContextItem assayContextItemId='35' assayContextItemRef='34' assayContextRef='Context for IC50' valueDisplay='30' valueNum='30.0'>
    <attributeId attributeType='Fixed' label='Number of points'>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
    </attributeId>
  </assayContextItem>
</assayContextItems>
'''


    static String ASSAY_CONTEXTS = '''
<assayContexts>
  <assayContext>
    <contextName>Context for IC50</contextName>
    <assayContextItems>
      <assayContextItem assayContextItemId='34' assayContextRef='Context for IC50' valueDisplay='Assay Explorer '>
        <valueId label='Assay Explorer'>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
        </valueId>
        <attributeId attributeType='Fixed' label='software'>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
        </attributeId>
      </assayContextItem>
      <assayContextItem assayContextItemId='35' assayContextRef='Context for IC50' valueDisplay='30' valueNum='30.0'>
        <attributeId attributeType='Fixed' label='Number of points'>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
        </attributeId>
      </assayContextItem>
      <assayContextItem assayContextItemId='36' assayContextRef='Context for IC50' valueDisplay='0 - 4' valueMax='4.0'>
        <attributeId attributeType='Range' label='Number of exclusions'>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
        </attributeId>
      </assayContextItem>
    </assayContextItems>
  </assayContext>
</assayContexts>
'''

    static String BARD_DATA_EXPORT_UNIT = '''
<bardexport>
  <link rel='item' title='The BARD Dictionary' href='null' type='dictionaryMediaType' />
  <link rel='item' title='List of assays, ready for extraction' href='null' type='assaysMediaType' />
  <link rel='item' title='List of projects, ready for extraction' href='null' type='projectsMediaType' />
  <link rel='item' title='List of experiments, ready for extraction' type='experimentsMediaType' href='null' />
</bardexport>
'''
    static String BARD_DATA_EXPORT = '''
<bardexport>
  <link rel='item' title='The BARD Dictionary' href='http://localhost:8080/dataExport/api/dictionary' type='application/vnd.bard.cap+xml;type=dictionary' />
  <link rel='item' title='List of assays, ready for extraction' href='http://localhost:8080/dataExport/api/assays' type='application/vnd.bard.cap+xml;type=assays' />
  <link rel='item' title='List of projects, ready for extraction' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
  <link rel='item' title='List of experiments, ready for extraction' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost:8080/dataExport/api/experiments' />
</bardexport>
    '''
    static String UNIT_CONVERSION_FULL = '''
<unitConversion fromUnit='fromUnit' toUnit='toUnit' multiplier='1.0' offset='1.0'>
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
  <stage stageElement='IC50'>
    <stageName>construct variant assay</stageName>
    <description>Description</description>
    <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
  </stage>
</stages>
'''

    static String STAGE = '''
<stage stageElement='IC50'>
  <stageName>construct variant assay</stageName>
  <description>Description</description>
  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
</stage>
'''
    static String STAGE1 = '''
<stage stageElement='IC50'>
  <stageName>construct variant assay</stageName>
  <description>Description</description>
  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
</stage>
'''


    static String RESULT_TYPE = '''
    <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </resultType>
'''

    static String RESULT_TYPE1 = '''
<resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
  <resultTypeName>IC50</resultTypeName>
  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
</resultType>
'''
    static String ELEMENT = '''
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    '''

    static String DICTIONARY = '''
<dictionary>
  <elements>
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
      <label>concentration</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
      <label>unit of measurement</label>
      <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='372' readyForExtraction='Ready' elementStatus='Published'>
      <label>Assay Explorer</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='370' readyForExtraction='Ready' elementStatus='Published'>
      <label>Number of points</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='369' readyForExtraction='Ready' elementStatus='Published'>
      <label>Number of exclusions</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='368' readyForExtraction='Complete' elementStatus='Published'>
      <label>software</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
      <label>IC50</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </element>
  </elements>
  <elementHierarchies>
    <elementHierarchy>
      <childElement childElement='concentration'>
        <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
      </childElement>
      <parentElement parentElement='IC50'>
        <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
      </parentElement>
      <relationshipType>derives from</relationshipType>
    </elementHierarchy>
  </elementHierarchies>
  <resultTypes>
    <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
      <resultTypeName>IC50</resultTypeName>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </resultType>
  </resultTypes>
  <stages>
    <stage stageElement='IC50'>
      <stageName>construct variant assay</stageName>
      <description>Description</description>
      <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </stage>
  </stages>
  <descriptors>
    <descriptor descriptorElement='uM' descriptor='assay'>
      <elementStatus>Published</elementStatus>
      <label>assay phase</label>
      <description>It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.</description>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </descriptor>
    <descriptor descriptorElement='concentration' descriptor='biology'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
    </descriptor>
    <descriptor descriptorElement='unit of measurement' descriptor='instance'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
    </descriptor>
  </descriptors>
  <laboratories>
    <laboratory laboratoryElement='IC50'>
      <laboratoryName>LABORATORY</laboratoryName>
      <description>Singular root to ensure tree viewers work</description>
    </laboratory>
  </laboratories>
  <units>
    <unit unitElement='unit of measurement' unit='UNIT'>
      <description>Singular root to ensure tree viewers work</description>
    </unit>
    <unit unitElement='concentration' unit='concentration' />
    <unit unitElement='uM' parentUnit='concentration' unit='uM' />
  </units>
  <unitConversions>
    <unitConversion fromUnit='uM' toUnit='concentration' multiplier='2.5' offset='2.0'>
      <formula>2*2</formula>
    </unitConversion>
  </unitConversions>
</dictionary>
'''
    static String LABS = '''
<laboratories>
  <laboratory laboratoryElement='IC50'>
    <laboratoryName>LABORATORY</laboratoryName>
    <description>Singular root to ensure tree viewers work</description>
  </laboratory>
</laboratories>
  '''
    static String UNIT_CONVERSIONS = '''
<unitConversions>
  <unitConversion fromUnit='uM' toUnit='concentration' multiplier='2.5' offset='2.0'>
    <formula>2*2</formula>
  </unitConversion>
</unitConversions>
'''


    static String UNITS = '''
   <units>
  <unit unitElement='unit of measurement' unit='UNIT'>
    <description>Singular root to ensure tree viewers work</description>
  </unit>
  <unit unitElement='concentration' unit='concentration' />
  <unit unitElement='uM' parentUnit='concentration' unit='uM' />
</units>
    '''




    static String RESULT_TYPES = '''
<resultTypes>
  <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
    <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
  </resultType>
</resultTypes>
'''


    static String ELEMENTS = '''
<elements>
  <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
    <label>uM</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
    <label>concentration</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
    <label>unit of measurement</label>
    <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='372' readyForExtraction='Ready' elementStatus='Published'>
    <label>Assay Explorer</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/372' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='370' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of points</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/370' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='369' readyForExtraction='Ready' elementStatus='Published'>
    <label>Number of exclusions</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/369' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='368' readyForExtraction='Complete' elementStatus='Published'>
    <label>software</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/368' type='application/vnd.bard.cap+xml;type=element' />
  </element>
  <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
    <label>IC50</label>
    <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
  </element>
</elements>
'''


    static String ELEMENT_HIERARCHIES = '''
<elementHierarchies>
  <elementHierarchy>
    <childElement childElement='concentration'>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
    </childElement>
    <parentElement parentElement='IC50'>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </parentElement>
    <relationshipType>derives from</relationshipType>
  </elementHierarchy>
</elementHierarchies>
'''

    static String INSTANCE_DESCRIPTOR_UNIT = '''
<descriptor abbreviation='abb' externalUrl='http://broad.org' unit='cm' descriptor='instance'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</descriptor>
'''

    static String BIOLOGY_DESCRIPTOR_UNIT = '''
<descriptor abbreviation='abb' externalUrl='http://broad.org' unit='cm' descriptor='biology'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</descriptor>
'''


    static String ASSAY_DESCRIPTOR_UNIT = '''
<descriptor abbreviation='abb' externalUrl='http://broad.org' unit='cm' descriptor='assay'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</descriptor>
'''

    static String RESULT_TYPE_NO_PARENT = '''
<resultType resultTypeElement='label' abbreviation='abb' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
  <link rel='related' href='null' type='xml' />
</resultType>
'''

    static String RESULT_TYPE_FULL = '''
<resultType resultTypeElement='label' parentResultType='resultTypeName' abbreviation='abb' baseUnit='cm' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
  <link rel='related' href='null' type='xml' />
</resultType>
'''


    static String ELEMENT_FULL = '''
<element elementId='' readyForExtraction='Ready' elementStatus='Pending' abbreviation='abb'>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
  <link rel='related' href='null' type='xml' />
</element>
'''
    static String ELEMENT_NO_DESCRIPTION = '''
<element elementId='' readyForExtraction='Ready' elementStatus='Pending'>
  <label>label</label>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
  <link rel='related' href='null' type='xml' />
</element>
'''
    static String ELEMENT_HIERARCHY_FULL = '''
<elementHierarchy>
  <childElement childElement='child'>
    <link rel='edit' href='null' type='xml' />
  </childElement>
  <parentElement parentElement='parent'>
    <link rel='edit' href='null' type='xml' />
  </parentElement>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''

    static String ELEMENT_HIERARCHY_NO_PARENT = '''
<elementHierarchy>
  <childElement childElement='child'>
    <link rel='edit' href='null' type='xml' />
  </childElement>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''
    static String STAGE_FULL = '''
<stage stageElement='elementLabel' parentStageName='parentStage'>
  <stageName>Stage</stageName>
  <description>desc</description>
  <link rel='related' href='null' type='xml' />
</stage>
    '''

    static String STAGE_NO_PARENT = '''
<stage stageElement='elementLabel'>
  <stageName>Stage</stageName>
  <description>desc</description>
    <link rel='related' href='null' type='xml' />
</stage>
'''

    static String SINGLE_UNIT = '''
<unit unitElement='label' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String SINGLE_UNIT_NO_DESCRIPTION = '''
<unit unitElement='label' unit='cm'/>
'''
    static String SINGLE_UNIT_NO_PARENT = '''
<unit unitElement='label' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String LABORATORY_SAMPLE_FULL = '''
<laboratory laboratoryElement='Lab Element' parentLaboratory='Parent'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''

    static String LABORATORY_SAMPLE_NO_PARENT = '''
<laboratory laboratoryElement='Lab Element'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''
}
