import bard.db.dictionary.Element
import grails.util.Environment
import groovy.sql.Sql

import javax.sql.DataSource

class BootStrap {
    DataSource dataSource
    def init = { servletContext ->
        switch (Environment.current.name) {
            case "test":
                if (Element.list().isEmpty()) {
                    final Sql sql = new Sql(dataSource)
                    insertTestDictionaryRecords(sql)
                    insertTestAssayDefRecords(sql)
                    insertTestProjectRecords(sql)
                }
                break;
        }

    }
    def destroy = {
    }
    def insertTestProjectRecords(final Sql sql) {
        sql.execute "INSERT INTO PROJECT (project_id,project_name,group_type,description,ready_for_extraction,VERSION,date_created) VALUES (1,'Scripps special project #1','Project',NULL,'Ready',0,SYSDATE)"
        sql.execute "INSERT INTO PROJECT (project_id,project_name,group_type,description,ready_for_extraction,VERSION,date_created) VALUES (2,'2126 - MLPCN Malaria - Inhibitor','Project',NULL,'Ready',0,SYSDATE)"
    }
    def insertTestAssayDefRecords(final Sql sql) {
        sql.execute "INSERT INTO ASSAY (ASSAY_ID,ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESIGNED_BY,READY_FOR_EXTRACTION,VERSION, DATE_CREATED, ASSAY_TYPE) values (1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)','Active','1','Scripps Florida','Ready',0,SYSDATE,'Regular')"
        sql.execute "INSERT INTO ASSAY (ASSAY_ID,ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESIGNED_BY,READY_FOR_EXTRACTION,VERSION, DATE_CREATED, ASSAY_TYPE) values (2,'Some nice label','Active','1.1','Broad Institute','Complete',0,SYSDATE,'Regular')"

        //REM INSERTING into MEASURE_CONTEXT
        sql.execute "INSERT INTO MEASURE_CONTEXT (MEASURE_CONTEXT_ID,ASSAY_ID,CONTEXT_NAME,VERSION, DATE_CREATED) values (2,1,'Context for IC50',0, SYSDATE)"

        sql.execute "INSERT INTO MEASURE (MEASURE_ID,ASSAY_ID,MEASURE_CONTEXT_ID,RESULT_TYPE_ID,ENTRY_UNIT,VERSION,DATE_CREATED) values (2,1,2,341,'uM',0, SYSDATE)"

        // REM INSERTING into MEASURE_CONTEXT_ITEM
        sql.execute "INSERT INTO MEASURE_CONTEXT_ITEM (MEASURE_CONTEXT_ITEM_ID,GROUP_MEASURE_CONTEXT_ITEM_ID,ASSAY_ID,MEASURE_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (34,null,1,2,'Fixed',368,null,372,'Assay Explorer ',null,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO MEASURE_CONTEXT_ITEM (MEASURE_CONTEXT_ITEM_ID,GROUP_MEASURE_CONTEXT_ITEM_ID,ASSAY_ID,MEASURE_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (35,34,1,2,'Fixed',370,null,null,'30',30,null,null,0,SYSDATE)"
        sql.execute "INSERT INTO MEASURE_CONTEXT_ITEM (MEASURE_CONTEXT_ITEM_ID,GROUP_MEASURE_CONTEXT_ITEM_ID,ASSAY_ID,MEASURE_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX,VERSION,DATE_CREATED) values (36,34,1,2,'Range',369,null,null,'0 - 4',null,0,4,0,SYSDATE)"

        sql.execute "INSERT INTO ASSAY_DOCUMENT (ASSAY_DOCUMENT_ID,ASSAY_ID,DOCUMENT_NAME,DOCUMENT_TYPE,DOCUMENT_CONTENT, DATE_CREATED,VERSION) values (1,1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)','Protocol','Some Document1',SYSDATE,0)"
        sql.execute "INSERT INTO ASSAY_DOCUMENT (ASSAY_DOCUMENT_ID,ASSAY_ID,DOCUMENT_NAME,DOCUMENT_TYPE,DOCUMENT_CONTENT, DATE_CREATED,VERSION) values (2,1,'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2) pt 2','Protocol','Some Document2',SYSDATE,0)"

    }

    def insertTestDictionaryRecords(final Sql sql) {

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (386,'uM','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (366,'concentration','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,DESCRIPTION,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (123,'unit of measurement','It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (372,'Assay Explorer','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (370,'Number of points','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (369,'Number of exclusions','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (368,'software','Published','Ready',0,SYSDATE)"


        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (0,0,123,'UNIT','Singular root to ensure tree viewers work')"
        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (3,0,366,'concentration',null)"
        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (4,3,386,'uM',null)"

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,UNIT,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION, DATE_CREATED) values (341,'IC50','uM','Published','Ready',0,SYSDATE)"

        sql.execute "INSERT INTO ELEMENT_HIERARCHY (ELEMENT_HIERARCHY_ID,PARENT_ELEMENT_ID,CHILD_ELEMENT_ID,RELATIONSHIP_TYPE,VERSION, DATE_CREATED) values (651,341,366,'derives from',0,SYSDATE)"

        sql.execute "INSERT INTO RESULT_TYPE (NODE_ID,RESULT_TYPE_ID,RESULT_TYPE_NAME,BASE_UNIT,RESULT_TYPE_STATUS) values (2,341,'IC50','uM','Published')"

        sql.execute "INSERT INTO STAGE (NODE_ID,STAGE_ID,DESCRIPTION,STAGE) values (17,341,'Description','construct variant assay')"

        sql.execute "INSERT INTO LABORATORY (NODE_ID,LABORATORY_ID,LABORATORY,DESCRIPTION) values (0,341,'LABORATORY','Singular root to ensure tree viewers work')"

        sql.execute "INSERT INTO ASSAY_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (287,386,'Published','assay phase','It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.')"
        sql.execute "INSERT INTO BIOLOGY_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (4,366,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"
        sql.execute "INSERT INTO INSTANCE_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (12,123,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"

        sql.execute "INSERT INTO UNIT_CONVERSION (FROM_UNIT,TO_UNIT,FORMULA,MULTIPLIER,OFFSET,VERSION,DATE_CREATED) values ('uM','concentration','2*2',2.5,2.0, 0,SYSDATE)"

    }

}
