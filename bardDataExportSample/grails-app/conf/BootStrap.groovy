import bard.db.dictionary.Element
import grails.util.Environment
import groovy.sql.Sql

import javax.sql.DataSource

class BootStrap {
    DataSource dataSource
    def init = { servletContext ->
        //insert element
        //insert descriptors, assay, biology, instance

        switch (Environment.current.name) {
            case "test":
            case "development":
                if (Element.list().isEmpty()) {
                    //Create test data
                    final Sql sql = new Sql(dataSource)
                    insertTestRecords(sql)
                }
                break;
        }

    }
    def destroy = {
    }

    def insertTestRecords(final Sql sql) {

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (386,'uM','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (366,'concentration','Published','Ready',0,SYSDATE)"
        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,DESCRIPTION,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION,DATE_CREATED) values (123,'unit of measurement','It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.','Published','Ready',0,SYSDATE)"


        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (0,0,123,'UNIT','Singular root to ensure tree viewers work')"
        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (3,0,366,'concentration',null)"
        sql.execute "Insert into UNIT (NODE_ID,PARENT_NODE_ID,UNIT_ID,UNIT,DESCRIPTION) values (4,3,386,'uM',null)"

        sql.execute "INSERT INTO ELEMENT (ELEMENT_ID,LABEL,UNIT,ELEMENT_STATUS,READY_FOR_EXTRACTION,VERSION, DATE_CREATED) values (341,'IC50','uM','Published','Ready',0,SYSDATE)"

        sql.execute "INSERT INTO ELEMENT_HIERARCHY (ELEMENT_HIERARCHY_ID,PARENT_ELEMENT_ID,CHILD_ELEMENT_ID,RELATIONSHIP_TYPE,VERSION, DATE_CREATED) values (651,341,366,'derives from',0,SYSDATE)"

        sql.execute "INSERT INTO RESULT_TYPE (NODE_ID,RESULT_TYPE_ID,RESULT_TYPE_NAME,BASE_UNIT,RESULT_TYPE_STATUS) values (2,341,'IC50','uM','Published')"

        sql.execute "INSERT INTO STAGE (NODE_ID,STAGE_ID,STAGE, STAGE_STATUS) values (17,341,'construct variant assay','Published')"

        sql.execute "INSERT INTO LABORATORY (NODE_ID,LABORATORY_ID,LABORATORY,DESCRIPTION, LABORATORY_STATUS) values (0,341,'LABORATORY','Singular root to ensure tree viewers work','Published')"

        sql.execute "INSERT INTO ASSAY_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (287,386,'Published','assay phase','It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.')"
        sql.execute "INSERT INTO BIOLOGY_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (4,366,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"
        sql.execute "INSERT INTO INSTANCE_DESCRIPTOR (NODE_ID,ELEMENT_ID,ELEMENT_STATUS,LABEL,DESCRIPTION) values (12,123,'Published','macromolecule description','A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).')"

        sql.execute "INSERT INTO UNIT_CONVERSION (FROM_UNIT,TO_UNIT,FORMULA,MULTIPLIER,OFFSET,VERSION,DATE_CREATED) values ('uM','concentration','2*2',2.5,2.0, 0,SYSDATE)"

    }

}
