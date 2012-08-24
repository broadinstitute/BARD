import bard.db.dictionary.Element
import grails.util.Environment
import groovy.sql.Sql

import javax.sql.DataSource

class BootStrap {
    DataSource dataSource
    def init = { servletContext ->
        switch (Environment.current.name) {
            case "test":
            case "development":
                if (Element.list().isEmpty()) {
                    final Sql sql = new Sql(dataSource)
                    createTreeTables(sql)
                    insertDictionaryRecords(sql)
                    insertAssayDefRecords(sql)
                    insertProjectRecords(sql)
                    insertExperiments(sql)
                }
                break;
        }

    }
    def destroy = {
    }
    /**
     * TODO: Don't use this for any production level testing
     * @param sql
     * @return
     */
    def createTreeTables(final Sql sql) {
        def UNIT_TREE = '''CREATE TABLE UNIT_TREE
        (
        NODE_ID        NUMBER(19,0) NOT NULL,
        PARENT_NODE_ID NUMBER(19,0),
        UNIT_ID       NUMBER(19,0) NOT NULL,
        UNIT           VARCHAR2(128) NOT NULL,
        DESCRIPTION    VARCHAR2(1000)
        )'''

        sql.execute(UNIT_TREE)

        def RESULT_TYPE_TREE = '''CREATE TABLE RESULT_TYPE_TREE
        (	NODE_ID NUMBER(19,0),
                PARENT_NODE_ID NUMBER(19,0),
        RESULT_TYPE_ID NUMBER(19,0),
                RESULT_TYPE_STATUS VARCHAR2(20),
        RESULT_TYPE_NAME VARCHAR2(128),
                DESCRIPTION VARCHAR2(1000),
        ABBREVIATION VARCHAR2(20),
                SYNONYMS VARCHAR2(1000),
        BASE_UNIT VARCHAR2(128)
        )'''
        sql.execute(RESULT_TYPE_TREE)


        def STAGE_TREE = '''CREATE TABLE STAGE_TREE
        (	NODE_ID NUMBER(19,0),
             PARENT_NODE_ID NUMBER(19,0),
        STAGE_ID NUMBER(19,0),
                STAGE_STATUS VARCHAR2(20),
        STAGE VARCHAR2(128),
                DESCRIPTION VARCHAR2(1000)
        )'''
        sql.execute(STAGE_TREE)


        def LAB_TREE = '''CREATE TABLE LABORATORY_TREE
        (	NODE_ID NUMBER(19,0),
            PARENT_NODE_ID NUMBER(19,0),
            LABORATORY_ID NUMBER(19,0),
            LABORATORY_STATUS VARCHAR2(20),
            LABORATORY VARCHAR2(128),
            DESCRIPTION VARCHAR2(1000)
        )'''
        sql.execute(LAB_TREE)

        def ASSAY_DESCRIPTOR_TREE = '''
 CREATE TABLE ASSAY_DESCRIPTOR_TREE
   (NODE_ID NUMBER(19,0),
PARENT_NODE_ID NUMBER(19,0),
ELEMENT_ID NUMBER(19,0),
ELEMENT_STATUS VARCHAR2(20),
LABEL VARCHAR2(128),
DESCRIPTION VARCHAR2(1000),
ABBREVIATION VARCHAR2(20),
SYNONYMS VARCHAR2(1000),
EXTERNAL_URL VARCHAR2(1000),
UNIT VARCHAR2(128)
   )'''
        sql.execute(ASSAY_DESCRIPTOR_TREE)


        def BIOLOGY_DESCRIPTOR_TREE = '''
 CREATE TABLE BIOLOGY_DESCRIPTOR_TREE
   (NODE_ID NUMBER(19,0),
PARENT_NODE_ID NUMBER(19,0),
ELEMENT_ID NUMBER(19,0),
ELEMENT_STATUS VARCHAR2(20),
LABEL VARCHAR2(128),
DESCRIPTION VARCHAR2(1000),
ABBREVIATION VARCHAR2(20),
SYNONYMS VARCHAR2(1000),
EXTERNAL_URL VARCHAR2(1000),
UNIT VARCHAR2(128)
   )'''
        sql.execute(BIOLOGY_DESCRIPTOR_TREE)

        def INSTANCE_DESCRIPTOR_TREE = '''
 CREATE TABLE INSTANCE_DESCRIPTOR_TREE
   (NODE_ID NUMBER(19,0),
PARENT_NODE_ID NUMBER(19,0),
ELEMENT_ID NUMBER(19,0),
ELEMENT_STATUS VARCHAR2(20),
LABEL VARCHAR2(128),
DESCRIPTION VARCHAR2(1000),
ABBREVIATION VARCHAR2(20),
SYNONYMS VARCHAR2(1000),
EXTERNAL_URL VARCHAR2(1000),
UNIT VARCHAR2(128)
   )'''
        sql.execute(INSTANCE_DESCRIPTOR_TREE)
    }

    def insertExperiments(final Sql sql) {
        sql.execute "INSERT INTO EXPERIMENT (EXPERIMENT_ID,EXPERIMENT_NAME,ASSAY_ID,EXPERIMENT_STATUS,READY_FOR_EXTRACTION,RUN_DATE_FROM,RUN_DATE_TO,HOLD_UNTIL_DATE,VERSION,DATE_CREATED) VALUES (1,'A cell-based HTS for delayed death inhibitors of the malarial parasite plastid Measured in Microorganism System Using Plate Reader - 2126-01_Inhibitor_Dose_DryPowder_Activity:1',1,'Approved','Ready',SYSDATE,SYSDATE,SYSDATE,0,SYSDATE)"
        sql.execute "INSERT INTO EXPERIMENT (EXPERIMENT_ID,EXPERIMENT_NAME,ASSAY_ID,EXPERIMENT_STATUS,READY_FOR_EXTRACTION,RUN_DATE_FROM,RUN_DATE_TO,HOLD_UNTIL_DATE,VERSION,DATE_CREATED) VALUES (2,'A cell-based HTS for delayed death inhibitors of the malarial parasite plastid Measured in Microorganism System Using Plate Reader - 2126-01_Inhibitor_Dose_DryPowder_Activity:2',1,'Approved','Ready',SYSDATE,SYSDATE,SYSDATE,0,SYSDATE)"
        sql.execute "INSERT INTO EXPERIMENT (EXPERIMENT_ID,EXPERIMENT_NAME,ASSAY_ID,EXPERIMENT_STATUS,READY_FOR_EXTRACTION,RUN_DATE_FROM,RUN_DATE_TO,HOLD_UNTIL_DATE,VERSION,DATE_CREATED) VALUES (23,'A cell-based HTS for delayed death inhibitors of the malarial parasite plastid Measured in Microorganism System Using Plate Reader - 2126-01_Inhibitor_Dose_DryPowder_Activity:23',1,'Approved','Complete',SYSDATE,SYSDATE,SYSDATE,0,SYSDATE)"


        sql.execute "INSERT INTO SUBSTANCE (SUBSTANCE_ID,COMPOUND_ID,SMILES,MOLECULAR_WEIGHT,SUBSTANCE_TYPE,VERSION,DATE_CREATED,LAST_UPDATED,MODIFIED_BY) VALUES(7976469,7976469,'CC', 12,'SMALL_MOLECULE',0,SYSDATE,SYSDATE,'schatwin')"


        sql.execute "INSERT INTO RESULT (RESULT_ID,VALUE_DISPLAY,VALUE_NUM,RESULT_STATUS,READY_FOR_EXTRACTION,EXPERIMENT_ID,SUBSTANCE_ID,RESULT_TYPE_ID,VERSION,DATE_CREATED,LAST_UPDATED,MODIFIED_BY) values (532,'101.3%',101.3,'Approved','Complete',1,7976469,341,0,SYSDATE,SYSDATE,'schatwin')"

        sql.execute "INSERT INTO RESULT (RESULT_ID,VALUE_DISPLAY,VALUE_NUM,RESULT_STATUS,READY_FOR_EXTRACTION,EXPERIMENT_ID,SUBSTANCE_ID,RESULT_TYPE_ID,VERSION,DATE_CREATED,LAST_UPDATED,MODIFIED_BY) values (533,'101.3%',101.3,'Approved','Ready',2,7976469,341,0,SYSDATE,SYSDATE,'schatwin')"

        //Result context item
        sql.execute "INSERT INTO RUN_CONTEXT_ITEM (DISCRIMINATOR,RUN_CONTEXT_ITEM_ID,EXPERIMENT_ID,ATTRIBUTE_ID,VALUE_DISPLAY,VALUE_NUM, QUALIFIER,VERSION,DATE_CREATED) VALUES ('Experiment',1,1,370,'66',66,'%',0,SYSDATE)"
        sql.execute "INSERT INTO RUN_CONTEXT_ITEM (DISCRIMINATOR,RUN_CONTEXT_ITEM_ID,EXPERIMENT_ID,ATTRIBUTE_ID,VALUE_DISPLAY,VALUE_NUM,QUALIFIER,VERSION,DATE_CREATED) VALUES ('Experiment',2,1,370,'66',66,'%',0,SYSDATE)"
        sql.execute "INSERT INTO RUN_CONTEXT_ITEM (DISCRIMINATOR,RUN_CONTEXT_ITEM_ID,EXPERIMENT_ID,ATTRIBUTE_ID,VALUE_DISPLAY,VALUE_NUM,QUALIFIER,VERSION,DATE_CREATED) VALUES ('Experiment',3,2,370,'66',66,'%',0,SYSDATE)"
        sql.execute "INSERT INTO RUN_CONTEXT_ITEM (DISCRIMINATOR,RUN_CONTEXT_ITEM_ID,EXPERIMENT_ID,ATTRIBUTE_ID,VALUE_DISPLAY,VALUE_NUM,QUALIFIER,VERSION,DATE_CREATED) VALUES ('Experiment',4,2,370,'66',66,'%',0,SYSDATE)"

        //TODO project step
      //  sql.execute "INSERT INTO PROJECT_EXPERIMENT (PROJECT_EXPERIMENT_ID,EXPERIMENT_ID,FOLLOWS_EXPERIMENT_ID,STAGE_ID,PROJECT_ID,DESCRIPTION,VERSION,DATE_CREATED, LAST_UPDATED, MODIFIED_BY) VALUES (1,1,2,17,1,'Description',0,SYSDATE,SYSDATE,'Broad')"
        sql.execute "INSERT INTO PROJECT_STEP (EXPERIMENT_ID,PROJECT_STEP_ID,PROJECT_ID,DESCRIPTION,VERSION,DATE_CREATED) VALUES (1,1,1,'2126 - MLPCN Malaria - Inhibitor',0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT_STEP (EXPERIMENT_ID,PROJECT_STEP_ID,PROJECT_ID,DESCRIPTION,VERSION,DATE_CREATED) VALUES (2,2,1,'2127 - MLPCN Malaria2 - Inhibitor',0,SYSDATE)"

        //External System
        sql.execute "INSERT INTO EXTERNAL_SYSTEM (EXTERNAL_SYSTEM_ID,SYSTEM_NAME,OWNER,SYSTEM_URL,VERSION,DATE_CREATED) VALUES (1,'PubChem','NIH','http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?',0,SYSDATE)"

        //External Reference
        sql.execute "INSERT INTO EXTERNAL_REFERENCE (EXTERNAL_SYSTEM_ID,EXTERNAL_REFERENCE_ID,EXPERIMENT_ID,PROJECT_ID,EXT_ASSAY_REF,VERSION,DATE_CREATED) VALUES (1,1,1,1,'aid=1007',0,SYSDATE)"

    }

    def insertProjectRecords(final Sql sql) {

        sql.execute "INSERT INTO PROJECT (project_id,project_name,group_type,description,ready_for_extraction,VERSION,date_created) VALUES (1,'Scripps special project #1','Project',NULL,'Ready',0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT (project_id,project_name,group_type,description,ready_for_extraction,VERSION,date_created) VALUES (2,'2126 - MLPCN Malaria - Inhibitor','Project',NULL,'Ready',0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT (project_id,project_name,group_type,description,ready_for_extraction,VERSION,date_created) VALUES (3,'2126 - MLPCN Malaria - Inhibitor','Project',NULL,'Complete',0,SYSDATE)"


        sql.execute "INSERT INTO PROJECT_CONTEXT_ITEM (DISCRIMINATOR,PROJECT_CONTEXT_ITEM_ID,GROUP_PROJECT_CONTEXT_ID,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values ('Project',34,null,368,null,372,'Assay Explorer ',null,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT_CONTEXT_ITEM (DISCRIMINATOR,PROJECT_CONTEXT_ITEM_ID,GROUP_PROJECT_CONTEXT_ID,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values ('Project',35,34,370,null,null,'30',30,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT_CONTEXT_ITEM (DISCRIMINATOR,PROJECT_CONTEXT_ITEM_ID,GROUP_PROJECT_CONTEXT_ID,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values ('Step',36,34,369,null,null,'0 - 4',null,0,4,0,SYSDATE)"
    }

    def insertAssayDefRecords(final Sql sql) {
        sql.execute "INSERT INTO ASSAY (ASSAY_TITLE,ASSAY_ID,ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESIGNED_BY,READY_FOR_EXTRACTION,VERSION, DATE_CREATED, ASSAY_TYPE) values ('Title1',1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)','Active','1','Scripps Florida','Ready',0,SYSDATE,'Regular')"
        sql.execute "INSERT INTO ASSAY (ASSAY_TITLE,ASSAY_ID,ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESIGNED_BY,READY_FOR_EXTRACTION,VERSION, DATE_CREATED, ASSAY_TYPE) values ('Title2',2,'Some nice label','Active','1.1','Broad Institute','Complete',0,SYSDATE,'Regular')"

        //REM INSERTING into ASSAY_CONTEXT
        sql.execute "INSERT INTO ASSAY_CONTEXT (ASSAY_CONTEXT_ID,ASSAY_ID,CONTEXT_NAME,VERSION, DATE_CREATED) values (2,1,'Context for IC50',0, SYSDATE)"

        sql.execute "INSERT INTO MEASURE (MEASURE_ID,ASSAY_ID,ASSAY_CONTEXT_ID,RESULT_TYPE_ID,ENTRY_UNIT,VERSION,DATE_CREATED) values (2,1,2,341,'uM',0, SYSDATE)"

        // REM INSERTING into ASSAY_CONTEXT_ITEM
        sql.execute "INSERT INTO ASSAY_CONTEXT_ITEM (DISPLAY_ORDER,ASSAY_CONTEXT_ITEM_ID,ASSAY_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (0,34,2,'Fixed',368,null,372,'Assay Explorer ',null,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO ASSAY_CONTEXT_ITEM (DISPLAY_ORDER,ASSAY_CONTEXT_ITEM_ID,ASSAY_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (1,35,2,'Fixed',370,null,null,'30',30,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO ASSAY_CONTEXT_ITEM (DISPLAY_ORDER,ASSAY_CONTEXT_ITEM_ID,ASSAY_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (2,36,2,'Range',369,null,null,'0 - 4',null,0,4,0,SYSDATE)"

        sql.execute "INSERT INTO ASSAY_DOCUMENT (ASSAY_DOCUMENT_ID,ASSAY_ID,DOCUMENT_NAME,DOCUMENT_TYPE,DOCUMENT_CONTENT, DATE_CREATED,VERSION) values (1,1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)','Protocol','Some Document1',SYSDATE,0)"
        sql.execute "INSERT INTO ASSAY_DOCUMENT (ASSAY_DOCUMENT_ID,ASSAY_ID,DOCUMENT_NAME,DOCUMENT_TYPE,DOCUMENT_CONTENT, DATE_CREATED,VERSION) values (2,1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2) pt 2','Protocol','Some Document2',SYSDATE,0)"

    }

    def insertDictionaryRecords(final Sql sql) {

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (386,'uM','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (366,'concentration','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,DESCRIPTION,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (123,'unit of measurement','It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (372,'Assay Explorer','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (370,'Number of points','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (369,'Number of exclusions','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (368,'software','Published','Complete',0,SYSDATE)"


        sql.execute "Insert into UNIT_TREE (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (0,0,123,'UNIT','Singular root to ensure tree viewers work')"
        sql.execute "Insert into UNIT_TREE (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (3,0,366,'concentration',null)"
        sql.execute "Insert into UNIT_TREE (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (4,3,386,'uM',null)"

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,UNIT,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION, DATE_CREATED) values (341,'IC50','uM','Published','Ready',0,SYSDATE)"

        sql.execute "INSERT INTO ELEMENT_HIERARCHY (ELEMENT_HIERARCHY_ID,PARENT_ELEMENT_ID,CHILD_ELEMENT_ID,RELATIONSHIP_TYPE,VERSION, DATE_CREATED) values (651,341,366,'derives from',0,SYSDATE)"

        sql.execute "INSERT INTO RESULT_TYPE_TREE (NODE_ID,RESULT_TYPE_ID,RESULT_TYPE_NAME,BASE_UNIT,RESULT_TYPE_STATUS) values (2,341,'IC50','uM','Published')"

        sql.execute "INSERT INTO STAGE_TREE (NODE_ID,STAGE_ID,DESCRIPTION,STAGE) values (17,341,'Description','construct variant assay')"

        sql.execute "INSERT INTO LABORATORY_TREE (NODE_ID,LABORATORY_ID,LABORATORY,DESCRIPTION) values (0,341,'LABORATORY','Singular root to ensure tree viewers work')"

        sql.execute "INSERT INTO ASSAY_DESCRIPTOR_TREE (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (287,386,'Published','assay phase','It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.')"
        sql.execute "INSERT INTO BIOLOGY_DESCRIPTOR_TREE (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (4,366,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"
        sql.execute "INSERT INTO INSTANCE_DESCRIPTOR_TREE (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (12,123,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"

        sql.execute "INSERT INTO UNIT_CONVERSION (FROM_UNIT,TO_UNIT,FORMULA,MULTIPLIER,OFFSET,VERSION,DATE_CREATED) values ('uM','concentration','2*2',2.5,2.0, 0,SYSDATE)"

    }

}
