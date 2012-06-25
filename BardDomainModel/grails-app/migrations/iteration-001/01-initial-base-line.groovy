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
    String iteration001SqlRelativeDir = 'iteration-001/sql'
    File iteration001SqlDir = new File(migrationsDir, iteration001SqlRelativeDir)
    iteration001SqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->
        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle') {
            sqlFile(path: "${iteration001SqlRelativeDir}/${fileName}")
        }
    }
}
