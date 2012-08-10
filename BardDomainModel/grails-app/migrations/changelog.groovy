import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'baseline-structure-ddl.sql', dbms: 'oracle', context:'standard') {
        sqlFile(path: "${migrationsDir}/sql/baseline-structure-ddl.sql", stripComments: true)
    }

    migrationsDir.traverse(type: DIRECTORIES, nameFilter: ~/iteration-\d+/, maxDepth: 0, sort: {a, b -> a.name <=> b.name }) {dir ->
        dir.traverse(type: FILES, nameFilter: ~/\d+.*\.groovy/, maxDepth: 0, sort: {a, b -> a.name <=> b.name }) {file ->
            include file: "${dir.name}/${file.name}"
        }
    }

    // do last
    include file: 'manage-stored-procedures.groovy'
    include file: 'execute-load-data.groovy'
    include file: 'drop-retired-tables.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'grant-selects.groovy'
}



