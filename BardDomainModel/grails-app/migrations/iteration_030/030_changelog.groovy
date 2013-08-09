package iteration_030

import bard.db.enums.DocumentType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentDocument
import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "gwalzer", id: "iteration-030/01-move-assay-documents-to-experiment", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('gwalzer');
                               END;
                               """)
            }
        }

        //Create the new EXPERIMENT_DOCUMENT table
        sqlFile(path: "${migrationsDir}/iteration_030/01-create-experiment-document-table.sql", stripComments: true)

        //Delete everything from ASSAY_DOCUMENT
        grailsChange {
            change {
                sql.call("""delete from assay_document where DOCUMENT_TYPE in ('Description', 'Protocol', 'Comments');""")
            }
        }

        //Populate EXPERIMENT_DOCUMENT from DATA_MIG
        grailsChange {
            change {
                sql.call("""
                    insert into EXPERIMENT_DOCUMENT ed
                        (EXPERIMENT_DOCUMENT_ID, EXPERIMENT_ID, DOCUMENT_NAME, DOCUMENT_TYPE, DOCUMENT_CONTENT, VERSION, DATE_CREATED, LAST_UPDATED, MODIFIED_BY)
                    select EXPERIMENT_DOCUMENT_ID_SEQ.nextval,exp.EXPERIMENT_ID, dm_ad.DOCUMENT_NAME, dm_ad.DOCUMENT_TYPE, DM_AD.DOCUMENT_CONTENT, dm_ad.VERSION, dm_ad.DATE_CREATED, dm_ad.LAST_UPDATED, dm_ad.MODIFIED_BY
                    from    DATA_MIG.EXPERIMENT dm_exp,
                            DATA_MIG.EXTERNAL_REFERENCE dm_extref,
                            DATA_MIG.EXTERNAL_SYSTEM dm_extsys,
                            DATA_MIG.ASSAY_DOCUMENT dm_ad,
                            assay a,
                            experiment exp,
                            external_reference extref,
                            external_system extsys
                    where   DM_EXP.EXPERIMENT_ID = DM_EXTREF.EXPERIMENT_ID
                            and DM_EXTREF.EXTERNAL_SYSTEM_ID = DM_EXTSYS.EXTERNAL_SYSTEM_ID
                            and DM_EXTSYS.SYSTEM_NAME = 'PubChem'
                            and DM_AD.ASSAY_ID = DM_EXP.ASSAY_ID
                            and A.ASSAY_ID = EXP.ASSAY_ID
                            and EXP.EXPERIMENT_ID = EXTREF.EXPERIMENT_ID
                            and EXTREF.EXTERNAL_SYSTEM_ID = EXTSYS.EXTERNAL_SYSTEM_ID
                            and EXTSYS.SYSTEM_NAME = 'PubChem'
                            and EXTREF.EXT_ASSAY_REF = DM_EXTREF.EXT_ASSAY_REF
                            and DM_AD.DOCUMENT_TYPE in ('Description', 'Protocol', 'Comments');
                            """)
            }
        }
    }
}

