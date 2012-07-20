import org.springframework.context.ApplicationContext

import static groovy.io.FileType.FILES

databaseChangeLog = {
    /*
    Note: this is cheating a bit, liquibase.resource.ResourceAccessor doesn't have a property call baseDirectory.
    The baseDirectory is only available on the liquibase.resource.FileSystemResourceAccessor which is the concrete type.

    Also, this was configured as part of the BardDomainModelGrailsPlugin.groovy initialization, see the doWithSpring section
    based on the initialization that happens in the DatabaseMigrationGrailsPlugin.groovy
    */

    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    File sqlDir = new File(migrationsDir, 'iteration-001/sql')
    sqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->
        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle',context:'standard') {
            sqlFile(path: "${sqlDir}/${fileName}")
        }
    }
}


