import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    File sqlDir = new File(migrationsDir, 'iteration-004/sql')
    sqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->
        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle') {
            sqlFile(path: "${sqlDir}/${fileName}", stripComments:true)
        }
    }
}

