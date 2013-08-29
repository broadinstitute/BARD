package iteration_032

import bard.db.enums.DocumentType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentDocument
import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

databaseChangeLog = {
    changeSet(author: "pmontgom", id: "iteration-032/01-remap-context-group", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('pmontgom');
                               END;
                               """)
            }
        }

        //Create the new EXPERIMENT_DOCUMENT table
        sqlFile(path: "iteration_032/01-remap-context-group.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-032/02-add-constraint-expt-doc", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }

        //Create the new EXPERIMENT_DOCUMENT table
        sqlFile(path: "iteration_032/02-add-constraint-expt-doc.sql", stripComments: true)
    }
}

