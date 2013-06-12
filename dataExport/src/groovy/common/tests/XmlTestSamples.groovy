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
    //We include a root, just so we can validate, otherwise the XML is not well-formed
    static final String EXPERIMENTS_LINK_MINIMAL = '''
                                                <root>
                                                  <link rel='related' href='null' type='assayMediaType' />
                                                  <link rel='up' href='null' type='experimentsMediaType' />
                                                  <link rel='related' href='null' type='resultsMediaType' />
                                                  <link rel='edit' href='null' type='experimentMediaType' />
                                                  <link rel='item' href='null' type='externalReferenceMediaType' />
                                                </root>
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

    static final String EXPERIMENTS_NONE_READY = '''
        <experiments count='0' />
    '''
    static final String EXPERIMENTS_ONE_READY = '''
        <experiments count='1'>
          <link rel='related' href='http://localhost:8080/dataExport/api/experiments/1' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiments>
    '''

    static final String EXPERIMENT_MEASURE_MINIMAL = '''
        <experimentMeasure experimentMeasureId='1' measureRef='1'  />
    '''
    static final String EXPERIMENT_MEASURE_WITH_PARENT_REF = '''
        <experimentMeasure experimentMeasureId='2' measureRef='2'  parentExperimentMeasureRef='1'  />
    '''
    static final String EXPERIMENT_MEASURE_WITH_PARENT_REF_AND_RELATIONSHIP = '''
        <experimentMeasure experimentMeasureId='2' measureRef='2'  parentExperimentMeasureRef='1' parentChildRelationship='supported by' />
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

    static final String EXPERIMENT_MINIMAL = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_OPTIONAL_PROPERTIES = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1' lastUpdated='1969-12-31T19:00:00.000-05:00' holdUntilDate='1969-12-31T19:00:00.000-05:00' runDateFrom='1969-12-31T19:00:00.000-05:00' runDateTo='1969-12-31T19:00:00.000-05:00'>
            <experimentName>experimentName</experimentName>
            <description>description</description>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_ONE_EXT_REF = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_TWO_EXT_REF = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_ONE_CONTEXT = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_TWO_CONTEXT = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
                <context id='2' displayOrder='1'>
                  <contextName />
                </context>
            </contexts>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_ONE_CONTEXT_ONE_EXPERIMENT_MEASURE = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
            <experimentMeasures>
                <experimentMeasure experimentMeasureId='1' measureRef='1'/>
            </experimentMeasures>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''

    static final String EXPERIMENT_WITH_ONE_EXPERIMENT_MEASURE = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <experimentMeasures>
                <experimentMeasure experimentMeasureId='1' measureRef='1'/>
            </experimentMeasures>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
        </experiment>
    '''
    static final String EXPERIMENT_WITH_TWO_EXPERIMENT_MEASURE = '''
        <experiment experimentId='1' status='Pending' readyForExtraction='Not Ready' confidenceLevel='1'>
            <experimentName>experimentName</experimentName>
            <experimentMeasures>
                <experimentMeasure experimentMeasureId='1' measureRef='1'/>
                <experimentMeasure experimentMeasureId='2' measureRef='2'/>
            </experimentMeasures>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=experiments' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=result' />
             <link rel='related' href='null' type='application/json;type=results' />
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
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

    static final String PROJECTS_NO_PROJECTS_READY = '''
        <projects count='0' />
    '''

    static final String PROJECTS_ONE_PROJEC_READY = '''
        <projects count='1'>
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=project' />
        </projects>
    '''

    static final String PROJECTS = '''
<projects count='2'>
    <project projectId='1' readyForExtraction='Not Ready' groupType='Project'>
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
  <project projectId='2' readyForExtraction='Not Ready' groupType='Project'>
    <projectName>2126 - MLPCN Malaria - Inhibitor</projectName>
    <link rel='edit' href='http://localhost:8080/dataExport/api/projects/2' type='application/vnd.bard.cap+xml;type=project' />
    <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
</projects>
'''


    static final String PROJECT = '''
    <project projectId='1' readyForExtraction='Not Ready' groupType='Project'>
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

    static final String PROJECT_DOCUMENT_MINIMAL = '''
        <projectDocument documentType='Description'>
          <documentName>documentName</documentName>
          <link rel='self' href='null' type='application/vnd.bard.cap+xml;type=projectDoc' />
          <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=project' />
        </projectDocument>
    '''
    static final String PROJECT_DOCUMENT_WITH_CONTENT = '''
        <projectDocument documentType='Description'>
          <documentName>documentName</documentName>
          <documentContent>documentContent</documentContent>
          <link rel='self' href='null' type='application/vnd.bard.cap+xml;type=projectDoc' />
          <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=project' />
        </projectDocument>
    '''
    static final String CONTEXT_MINIMAL = '''
         <context id='1' displayOrder='0'>
            <contextName />
         </context>
     '''

    static final String CONTEXT_MINIMAL_WITH_NAME = '''
         <context id='1' displayOrder='0'>
            <contextName>contextName</contextName>
         </context>
     '''
    static final String CONTEXT_MINIMAL_WITH_GROUP = '''
         <context id='1' displayOrder='0'>
            <contextName/>
            <contextGroup>contextGroup</contextGroup>
         </context>
     '''
    static final String CONTEXT_MINIMAL_WITH_ONE_ITEM = '''
         <context id='1' displayOrder='0'>
            <contextName />
            <contextItems>
                <contextItem displayOrder='0'>
                  <attributeId label='label1'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
                  </attributeId>
                </contextItem>
            </contextItems>
         </context>
     '''
    static final String CONTEXT_MINIMAL_WITH_TWO_ITEMS = '''
         <context id='1' displayOrder='0'>
            <contextName />
            <contextItems>
                <contextItem displayOrder='0'>
                  <attributeId label='label1'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
                  </attributeId>
                </contextItem>
                <contextItem displayOrder='1'>
                  <attributeId label='label2'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
                  </attributeId>
                </contextItem>
            </contextItems>
         </context>
     '''
    static final String PROJECT_CONTEXTS_ONE = '''
        <contexts>
          <context id='1' displayOrder='0'>
            <contextName />
          </context>
        </contexts>
    '''
    static final String PROJECT_CONTEXTS_TWO = '''
            <contexts>
              <context id='1' displayOrder='0'>
                <contextName />
              </context>
              <context id='2' displayOrder='1'>
                  <contextName />
              </context>
            </contexts>
        '''
    static final String PROJECT_EXPERIMENT_MINIMAL = '''
        <projectExperiment projectExperimentId='1'>
            <experimentRef label='experimentName'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
            </experimentRef>
        </projectExperiment>
    '''
    static final String PROJECT_EXPERIMENT_WITH_STAGEREF = '''
        <projectExperiment projectExperimentId='1'>
            <experimentRef label='experimentName'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
            </experimentRef>
            <stageRef label='stage'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
            </stageRef>
        </projectExperiment>
    '''
    static final String PROJECT_EXPERIMENT_WITH_CONTEXT = '''
        <projectExperiment projectExperimentId='1'>
            <experimentRef label='experimentName'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
            </experimentRef>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
        </projectExperiment>
    '''
    static final String PROJECT_STEP_MINIMAL = '''
          <projectStep projectStepId='1' nextProjectExperimentRef='1' precedingProjectExperimentRef='1' />
    '''
    static final String PROJECT_STEP_WITH_EDGE_NAME = '''
        <projectStep projectStepId='1' nextProjectExperimentRef='1' precedingProjectExperimentRef='1'>
            <edgeName>edge</edgeName>
        </projectStep>
    '''
    static final String PROJECT_STEP_WITH_CONTEXT = '''
        <projectStep projectStepId='1' nextProjectExperimentRef='1' precedingProjectExperimentRef='1'>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
        </projectStep>
    '''

    static final String PROJECT_MINIMAL = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_DESCRIPTION = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <description>description</description>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_ONE_EXTERNAL_REFERENCE = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
        </project>
    '''
    static final String PROJECT_TWO_EXTERNAL_REFERENCES = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=externalReference' />
        </project>
    '''
    static final String PROJECT_ONE_DOCUMENT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=projectDoc' />
        </project>
    '''
    static final String PROJECT_TWO_DOCUMENTS = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=projectDoc' />
            <link rel='item' href='null' type='application/vnd.bard.cap+xml;type=projectDoc' />
        </project>
    '''
    static final String PROJECT_WITH_ONE_CONTEXT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_TWO_CONTEXT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
                <context id='2' displayOrder='1'>
                  <contextName />
                </context>
            </contexts>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_ONE_CONTEXT_ONE_EXPERIMENT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <contexts>
                <context id='1' displayOrder='0'>
                  <contextName />
                </context>
            </contexts>
            <projectExperiments>
                <projectExperiment projectExperimentId='1'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                </projectExperiment>
            </projectExperiments>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''

    static final String PROJECT_WITH_EXPERIMENT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <projectExperiments>
                <projectExperiment projectExperimentId='1'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                </projectExperiment>
            </projectExperiments>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_EXPERIMENT_WITH_ONE_CONTEXT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <projectExperiments>
                <projectExperiment projectExperimentId='1'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                  <contexts>
                    <context id='1' displayOrder='0'>
                      <contextName />
                    </context>
                  </contexts>
                </projectExperiment>
              </projectExperiments>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_EXPERIMENT_WITH_TWO_CONTEXT = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <projectExperiments>
                <projectExperiment projectExperimentId='1'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                  <contexts>
                    <context id='1' displayOrder='0'>
                      <contextName />
                    </context>
                    <context id='2' displayOrder='1'>
                      <contextName />
                    </context>
                  </contexts>
                </projectExperiment>
              </projectExperiments>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''
    static final String PROJECT_WITH_TWO_EXPERIMENTS_ONE_PROJECT_STEP = '''
        <project projectId='1' readyForExtraction='Ready' groupType='Project'>
            <projectName>name</projectName>
            <projectExperiments>
                <projectExperiment projectExperimentId='1'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                </projectExperiment>
                <projectExperiment projectExperimentId='2'>
                  <experimentRef label='experimentName'>
                    <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=experiment' />
                  </experimentRef>
                </projectExperiment>
            </projectExperiments>
            <projectSteps>
                <projectStep projectStepId='1' nextProjectExperimentRef='2' precedingProjectExperimentRef='1' />
            </projectSteps>
            <link rel='edit' href='null' type='application/vnd.bard.cap+xml;type=project' />
            <link rel='up' href='null' type='application/vnd.bard.cap+xml;type=projects' />
        </project>
    '''

    static final String ASSAY_NO_DESIGNER_UNIT = '''
        <assay assayId='1' readyForExtraction='Not Ready' assayVersion='assayVersi' assayType='Regular' status='Draft'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <link rel='edit' href='null' type='xml' />
          <link rel='self' href='null' type='xml' />
          <link rel='up' href='null' type='xml' />
        </assay> '''
    static final String EXTERNAL_SYSTEM = '''
                                            <externalSystem name='systemName'>
                                            <link rel='self' href='null' type='xml' />
                                            <link rel='up' href='null' type='xml' />
                                            </externalSystem>
                                            '''
    static final String EXTERNAL_SYSTEMS = ''' <externalSystems count='1'>
                                                <externalSystem name='systemName'>
                                                <link rel='self' href='null' type='xml' />
                                                <link rel='up' href='null' type='xml' />
                                                </externalSystem>
                                                </externalSystems>
                                               '''
    static final String EXTERNAL_REFERENCES = '''
<externalReferences count='1'>
    <externalReference>
        <externalAssayRef>extAssayRef</externalAssayRef>
        <link rel='related' href='null' type='xml' />
        <link rel='self' href='null' type='xml' />
        <link rel='up' href='null' type='xml' />
    </externalReference>
    </externalReferences>
    '''
    static final String EXTERNAL_REFERENCE = '''
    <externalReference>
        <externalAssayRef>extAssayRef</externalAssayRef>
        <link rel='related' href='null' type='xml' />
        <link rel='self' href='null' type='xml' />
        <link rel='up' href='null' type='xml' />
    </externalReference>
    '''
    static final String ASSAY_WITH_DESIGNER_NAME = '''
        <assay assayId='1' readyForExtraction='Not Ready' assayVersion='assayVersi' assayType='Regular' status='Draft'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <designedBy>Broad</designedBy>
          <link rel='edit' href='null' type='xml' />
          <link rel='self' href='null' type='xml' />
          <link rel='up' href='null' type='xml' />
        </assay> '''
    static final String ASSAY_WITH_DOCUMENT = '''
         <assay assayId='1' readyForExtraction='Not Ready' assayVersion='assayVersi' assayType='Regular' status='Draft'>
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

    static final String ASSAY_DOCUMENT_MINIMAL = '''
        <assayDocument documentType='Description'>
            <documentName>documentName</documentName>
            <link rel='self' href='null' type='application/vnd.bard.cap+xml;type=assayDoc' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
        </assayDocument>'''

    static final String ASSAY_DOCUMENT_WITH_CONTENT = '''
        <assayDocument documentType='Description'>
            <documentName>documentName</documentName>
            <documentContent>documentContent</documentContent>
            <link rel='self' href='null' type='application/vnd.bard.cap+xml;type=assayDoc' />
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=assay' />
        </assayDocument>'''

    static final String ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE = '''
        <assayContextItem displayOrder='0' attributeType='Fixed'  valueDisplay='Display' >
          <valueId label='valueLabel'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </valueId>
          <attributeId label='attributeLabel'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </attributeId>
        </assayContextItem>'''

    static final String MEASURE_MINIMAL = '''
        <measure measureId='1'>
          <resultTypeRef label='label1'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
        </measure>'''
    static final String MEASURE_WITH_PARENT_MEASURE_REF = '''
        <measure measureId='2' parentMeasureRef='1'>
          <resultTypeRef label='label2'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
        </measure>'''

    static final String MEASURE_WITH_STATS_MODIFIER_REF = '''
        <measure measureId='1'>
          <resultTypeRef label='label1'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
          <statsModifierRef label='statsModifier'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </statsModifierRef>
        </measure>'''

    static final String MEASURE_WITH_ENTRY_UNIT_REF = '''
        <measure measureId='1'>
          <resultTypeRef label='label1'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
          <entryUnitRef label='entryUnit'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </entryUnitRef>
        </measure>'''
    static final String MEASURE_WITH_ONE_ASSAY_CONTEXT_REF = '''
        <measure measureId='1' >
          <resultTypeRef label='label1'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
          <assayContextRefs>
            <assayContextRef>1</assayContextRef>
          </assayContextRefs>
        </measure>'''
    static final String MEASURE_WITH_TWO_ASSAY_CONTEXT_REF = '''
        <measure measureId='1' >
          <resultTypeRef label='label1'>
            <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
          </resultTypeRef>
          <assayContextRefs>
            <assayContextRef>1</assayContextRef>
            <assayContextRef>2</assayContextRef>
          </assayContextRefs>
        </measure>
     '''

    static final String ASSAY_CONTEXT_MINIMAL = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
        </assayContext>
     '''
    static final String ASSAY_CONTEXT_WITH_CONTEXT_NAME = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName>contextName</contextName>
        </assayContext>
     '''

    static final String ASSAY_CONTEXT_WITH_CONTEXT_GROUP = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
          <contextGroup>contextGroup</contextGroup>
        </assayContext>
     '''

    static final String ASSAY_CONTEXT_WITH_ONE_CONTEXT_ITEM = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
          <assayContextItems>
            <assayContextItem displayOrder='0' attributeType='Fixed'>
              <attributeId label='label1'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
              </attributeId>
            </assayContextItem>
          </assayContextItems>
        </assayContext>
     '''
    static final String ASSAY_CONTEXT_WITH_TWO_CONTEXT_ITEMS = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
          <assayContextItems>
            <assayContextItem displayOrder='0' attributeType='Fixed'>
              <attributeId label='label1'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
              </attributeId>
            </assayContextItem>
            <assayContextItem displayOrder='1' attributeType='Fixed'>
              <attributeId label='label2'>
                <link rel='related' href='null' type='application/vnd.bard.cap+xml;type=element' />
              </attributeId>
            </assayContextItem>
          </assayContextItems>
        </assayContext>
     '''
    static final String MINIMAL_ASSAY_CONTEXT_WITH_ONE_MEASURE_REF = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
          <measureRefs>
            <measureRef>1</measureRef>
          </measureRefs>
        </assayContext>
     '''

    static final String MINIMAL_ASSAY_CONTEXT_WITH_TWO_MEASURE_REFS = '''
        <assayContext assayContextId='1' displayOrder='0'>
          <contextName/>
          <measureRefs>
            <measureRef>1</measureRef>
            <measureRef>2</measureRef>
          </measureRefs>
        </assayContext>
     '''

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

    static String ASSAY_FULL_DOC = '''
        <assay assayId='1' readyForExtraction='Not Ready' assayVersion='assayVersi' assayType='Regular' status='Draft'>
          <assayShortName>assayShortName</assayShortName>
          <assayName>assayName</assayName>
          <assayContexts>
            <assayContext assayContextId='1' displayOrder='0'>
              <contextName />
              <assayContextItems>
                <assayContextItem displayOrder='0' attributeType='Fixed' valueDisplay='non null valueDisplay'>
                  <attributeId label='label1'>
                    <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
                  </attributeId>
                </assayContextItem>
              </assayContextItems>
              <measureRefs>
                <measureRef>1</measureRef>
              </measureRefs>
            </assayContext>
          </assayContexts>
          <measures>
            <measure measureId='1'>
              <resultTypeRef label='label1'>
                <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
              </resultTypeRef>
              <assayContextRefs>
                <assayContextRef>1</assayContextRef>
              </assayContextRefs>
            </measure>
          </measures>
          <link rel='edit' href='http://localhost:8080/dataExport/api/assays/1' type='application/vnd.bard.cap+xml;type=assay' />
          <link rel='self' href='http://localhost:8080/dataExport/api/assays/1' type='application/vnd.bard.cap+xml;type=assay' />
          <link rel='up' href='http://localhost:8080/dataExport/api/assays' type='application/vnd.bard.cap+xml;type=assays' />
          <link rel='item' href='http://localhost:8080/dataExport/api/assayDocument/1' type='application/vnd.bard.cap+xml;type=assayDoc' />
        </assay>
     '''
    static String PROJECT_FROM_SERVER = '''
   <project projectId='1' readyForExtraction='Not Ready' groupType='Project'>
   <projectName>Scripps special project #1</projectName>
   <link rel='edit' href='http://localhost:8080/dataExport/api/projects/1' type='application/vnd.bard.cap+xml;type=project' />
   <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
   </project>

    '''
    static String PROJECTS_FROM_SERVER = '''
<projects count='2'>  <project projectId='1' readyForExtraction='Not Ready' groupType='Project'>
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
    <project projectId='2' readyForExtraction='Not Ready' groupType='Project'>
        <projectName>2126 - MLPCN Malaria - Inhibitor</projectName>
        <link rel='edit' href='http://localhost:8080/dataExport/api/projects/2' type='application/vnd.bard.cap+xml;type=project' />
        <link rel='up' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
    </project>
</projects>
'''
    static String ASSAYS_FROM_SERVER = '''
    <assays count='1'>
        <link rel='related' assayShortName='assayShortName' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost:8080/dataExport/api/assays/1'/>
    </assays>
    '''

    static String ASSAYS = '''
<assays count='1'>
  <link rel='related' title='1' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost:8080/dataExport/api/assays/1' />
</assays>
'''

    static String ASSAY_DOCUMENT = '''

          <assayDocument documentType='Protocol'>
            <documentName>a doc</documentName>
            <documentContent>content</documentContent>
            <link rel='self' href='http://localhost:8080/dataExport/api/assayDocument/1' type='application/vnd.bard.cap+xml;type=assayDoc' />
            <link rel='related' href='http://localhost:8080/dataExport/api/assays/1' type='application/vnd.bard.cap+xml;type=assay' />
          </assayDocument>

    '''
    static String ASSAY_CONTEXT_WITH_MEASURES = '''
        <assayContexts>
          <assayContext assayContextId='1' displayOrder='0'>
            <contextName>Context for IC50</contextName>
            <measureRefs>
              <measureRef>1</measureRef>
            </measureRefs>
          </assayContext>
        </assayContexts>
    '''


    static String ASSAY_CONTEXTS = '''
        <assayContexts>
          <assayContext assayContextId='1' displayOrder='0'>
            <contextName>Context for IC50</contextName>
            <assayContextItems>
              <assayContextItem displayOrder='0' valueDisplay='Assay Explorer' attributeType='Fixed'>
                <attributeId label='software'>
                  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
                </attributeId>
              </assayContextItem>
              <assayContextItem displayOrder='1' attributeType='Fixed' valueDisplay='non null valueDisplay'>
                <attributeId label='a label'>
                  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
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
                                          <link rel='item' title='List of external references' type='externalReferences' href='null' />
                                          <link rel='item' title='List of external systems' type='externalSystems' href='null' />
                                        </bardexport>
                                        '''
    static String BARD_DATA_EXPORT = '''
        <bardexport>
          <link rel='item' title='The BARD Dictionary' href='http://localhost:8080/dataExport/api/dictionary' type='application/vnd.bard.cap+xml;type=dictionary' />
          <link rel='item' title='List of assays, ready for extraction' href='http://localhost:8080/dataExport/api/assays' type='application/vnd.bard.cap+xml;type=assays' />
          <link rel='item' title='List of projects, ready for extraction' href='http://localhost:8080/dataExport/api/projects' type='application/vnd.bard.cap+xml;type=projects' />
          <link rel='item' title='List of experiments, ready for extraction' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost:8080/dataExport/api/experiments' />
          <link rel='item' title='List of external references' type='application/vnd.bard.cap+xml;type=externalReferences' href='http://localhost:8080/dataExport/api/externalReferences' />
          <link rel='item' title='List of external systems' type='application/vnd.bard.cap+xml;type=externalSystems' href='http://localhost:8080/dataExport/api/externalSystems' />
        </bardexport> '''

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
          <stage stageElement='label1'>
            <stageName>label1</stageName>
            <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
          </stage>
          <stage stageElement='label2'>
            <stageName>label2</stageName>
            <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
          </stage>
        </stages>'''

    static String STAGE = '''
        <stage stageElement='IC50'>
          <stageName>IC50</stageName>
          <description>Description</description>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
        </stage> '''

    static String STAGE1 = '''
        <stage stageElement='IC50'>
          <stageName>IC50</stageName>
          <description>Description</description>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
        </stage> '''


    static String RESULT_TYPE = '''
    <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
  <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
    </resultType>
'''

    static String RESULT_TYPE1 = '''
        <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
          <resultTypeName>IC50</resultTypeName>
          <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
        </resultType>'''

    static String ELEMENT = '''
    <element elementId='1' readyForExtraction='Not Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    '''

    static String DICTIONARY = '''
        <dictionary>
          <elements>
            <element elementId='1' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>IC50</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='2' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>log IC50</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='3' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label1</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/3' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='4' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label2</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/4' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='5' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label3</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/5' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='6' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label4</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/6' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='7' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label5</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/7' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='8' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label6</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/8' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='9' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>label7</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/9' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='10' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>micromolar</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/10' type='application/vnd.bard.cap+xml;type=element' />
            </element>
            <element elementId='11' readyForExtraction='Not Ready' elementStatus='Pending'>
              <label>millimolar</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/11' type='application/vnd.bard.cap+xml;type=element' />
            </element>
          </elements>
          <elementHierarchies>
            <elementHierarchy>
              <childElement childElement='log IC50'>
                <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
              </childElement>
              <parentElement parentElement='IC50'>
                <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
              </parentElement>
              <relationshipType>subClassOf</relationshipType>
            </elementHierarchy>
          </elementHierarchies>
          <resultTypes>
            <resultType resultTypeElement='label1' resultTypeStatus='Pending'>
              <resultTypeName>label1</resultTypeName>
              <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/3' type='application/vnd.bard.cap+xml;type=element' />
            </resultType>
          </resultTypes>
          <stages>
            <stage stageElement='label2'>
              <stageName>label2</stageName>
              <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/4' type='application/vnd.bard.cap+xml;type=element' />
            </stage>
          </stages>
          <descriptors>
            <descriptor descriptorElement='label3' descriptor='label'>
              <elementStatus>Pending</elementStatus>
              <label>label3</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/5' type='application/vnd.bard.cap+xml;type=element' />
            </descriptor>
            <descriptor descriptorElement='label4' descriptor='label'>
              <elementStatus>Pending</elementStatus>
              <label>label4</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/6' type='application/vnd.bard.cap+xml;type=element' />
            </descriptor>
            <descriptor descriptorElement='label5' descriptor='label'>
              <elementStatus>Pending</elementStatus>
              <label>label5</label>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/7' type='application/vnd.bard.cap+xml;type=element' />
            </descriptor>
          </descriptors>
          <laboratories>
            <laboratory laboratoryElement='label6'>
              <laboratoryName>label</laboratoryName>
              <description />
            </laboratory>
          </laboratories>
          <units>
            <unit unitElement='label7' />
          </units>
          <unitConversions>
            <unitConversion fromUnit='micromolar' toUnit='millimolar' multiplier='1000.0' />
          </unitConversions>
        </dictionary>
    '''

    static String LABS = '''
        <laboratories>
          <laboratory laboratoryElement="label1">
            <laboratoryName>label</laboratoryName>
            <description />
          </laboratory>
          <laboratory laboratoryElement="label2">
            <laboratoryName>label</laboratoryName>
            <description />
          </laboratory>
        </laboratories>'''

    static String UNIT_CONVERSIONS = '''
        <unitConversions>
          <unitConversion fromUnit='micromolar' toUnit='millimolar' multiplier='1000.1' offset='5.1'>
            <formula>2*2</formula>
          </unitConversion>
        </unitConversions> '''
    static String UNITS = '''
        <units>
            <unit unitElement='concentration' />
            <unit unitElement='uM' parentUnit='concentration' />
        </units>'''

    static String RESULT_TYPES = '''
<resultTypes>
  <resultType resultTypeElement='IC50' baseUnit='uM' resultTypeStatus='Published'>
    <resultTypeName>IC50</resultTypeName>
    <link rel='related' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
  </resultType>
</resultTypes>
'''


    static String ELEMENTS = '''
        <elements>
          <element elementId='1' readyForExtraction='Not Ready' elementStatus='Pending'>
            <label>label1</label>
            <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
          </element>
          <element elementId='2' readyForExtraction='Not Ready' elementStatus='Pending'>
            <label>label2</label>
            <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
          </element>
        </elements>'''


    static String ELEMENT_HIERARCHIES = '''
        <elementHierarchies>
          <elementHierarchy>
            <childElement childElement='log IC50'>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/2' type='application/vnd.bard.cap+xml;type=element' />
            </childElement>
            <parentElement parentElement='IC50'>
              <link rel='edit' href='http://localhost:8080/dataExport/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
            </parentElement>
            <relationshipType>subClassOf</relationshipType>
          </elementHierarchy>
        </elementHierarchies> '''


    static String ASSAY_DESCRIPTOR_UNIT = '''
        <descriptor descriptorElement='assay' abbreviation='abb' externalUrl='http://broad.org' unit='cm' descriptor='assay'>
          <elementStatus>Pending</elementStatus>
          <label>assay</label>
          <description>desc</description>
          <synonyms>syn</synonyms>
          <link rel='edit' href='null' type='xml' />
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

  static String ONTOLOGY_FULL='''
<ontology name='name' abbreviation='BAO' sourceUrl='http://purl.org/obo/owl/UO#UO_0000244' />
'''
  static final String ONTOLOGY_MISSING_ATTRIBUTES ="<ontology name='name' />"
    static String ONTOLOGIES_FULL='''<ontologies>
    <ontology name='name1' abbreviation='BAO' sourceUrl='http://purl.org/obo/owl/UO#UO_0000244' />
    <ontology name='name2' />
    </ontologies>'''

    static final String ONTOLOGIES_MISSING_ATTRIBUTES ='''
   <ontologies>
  <ontology name='name' />
</ontologies>
'''
  static final String ELEMENT_WITH_ONTOLOGY=  '''
<element elementId='' readyForExtraction='Ready' elementStatus='Pending'>
  <label>label</label>
  <externalUrl>http://www.broad.org</externalUrl>
  <ontologies>
    <ontology name='name' />
  </ontologies>
  <link rel='edit' href='null' type='xml' />
  <link rel='related' href='null' type='xml' />
</element>
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
  <stageName>elementLabel</stageName>
  <description>desc</description>
  <link rel='related' href='null' type='xml' />
</stage>
    '''

    static String STAGE_NO_PARENT = '''
        <stage stageElement='elementLabel'>
          <stageName>elementLabel</stageName>
          <description>desc</description>
            <link rel='related' href='null' type='xml' />
        </stage>
     '''

    static String SINGLE_DESCRIPTOR_ELEMENTS = '''
        <elementStatus>Pending</elementStatus>
        <label>assay</label>
        <link rel='edit' href='null' type='xml' />
    '''.stripIndent().trim()

    static String SINGLE_DESCRIPTOR_ALL_ELEMENTS = '''
        <elementStatus>Pending</elementStatus>
        <label>assay</label>
        <description>des</description>
        <synonyms>syn</synonyms>
        <link rel='edit' href='null' type='xml' />
     '''.stripIndent().trim()

    static String SINGLE_UNIT = '''
        <unit unitElement='cm' parentUnit='length unit'>
          <description>Centimetres</description>
        </unit>
    '''
    static String SINGLE_UNIT_NO_DESCRIPTION = '''
        <unit unitElement='cm'/>
    '''
    static String SINGLE_UNIT_NO_PARENT = '''
        <unit unitElement='cm'>
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
