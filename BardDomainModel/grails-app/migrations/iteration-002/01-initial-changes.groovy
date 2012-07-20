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
    File sqlDir = new File(migrationsDir, 'iteration-002/sql')
    sqlDir.traverse(type: FILES, nameFilter: ~/\d+.*.sql/, maxDepth: 0, sort: {a, b -> a.name <=> b.name }) {file ->
        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle',context:'standard') {
            sqlFile(path: "${sqlDir}/${fileName}")
        }
    }

}
