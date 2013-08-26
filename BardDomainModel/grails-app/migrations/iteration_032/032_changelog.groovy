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
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

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
        sqlFile(path: "${migrationsDir}/iteration_032/01-remap-context-group.sql", stripComments: true)
    }
}

