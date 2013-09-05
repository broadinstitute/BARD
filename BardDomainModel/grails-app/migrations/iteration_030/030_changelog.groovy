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
    changeSet(author: "gwalzer", id: "iteration-030/01-create-experiment-document-table", dbms: "oracle", context: "standard") {

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
        sqlFile(path: "iteration_030/01-create-experiment-document-table.sql", stripComments: true)
    }
}

