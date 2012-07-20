import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    File sqlDir = new File(migrationsDir, 'iteration-004/sql')
    sqlDir.traverse(type: FILES, nameFilter: ~/\d+.*.sql/, maxDepth: 0, sort: {a, b -> a.name <=> b.name }) {file ->
        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle', context:'standard') {
            sqlFile(path: "${sqlDir}/${fileName}", stripComments:true)
        }
    }
}

