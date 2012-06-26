import static groovy.io.FileType.FILES

databaseChangeLog = {

    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    String iteration002SqlRelativeDir = 'iteration-002/sql'
    File relativeSqlDir = new File(migrationsDir, iteration002SqlRelativeDir)
    relativeSqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->
        String fileName = file.name
        String relativeFileName = "${iteration002SqlRelativeDir}/${fileName}"
        changeSet(author: "ddurkin", id: relativeFileName, dbms: 'oracle') {
            sqlFile(path: relativeFileName)
        }
    }
}
