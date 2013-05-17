import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'rename-changelog-filenames.sql', dbms: 'oracle', context: 'rename-changelog') {
        sqlFile(path: "${migrationsDir}/sql/rename-changelog-filenames.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'baseline-structure-ddl.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/sql/baseline-structure-ddl.sql", stripComments: true)
    }

    migrationsDir.traverse(type: DIRECTORIES, nameFilter: ~/iteration_\d+/, maxDepth: 0, sort: { a, b -> a.name <=> b.name }) { dir ->
        dir.traverse(type: FILES, nameFilter: ~/.*changelog.*\.groovy/, maxDepth: 0, sort: { a, b -> a.name <=> b.name }) { file ->
            include(file: "${dir.name}/${file.name}")
        }
    }
    // views
    changeSet(author: 'ddurkin', id: 'create-or-replace-dictionary-views.sql', dbms: 'oracle', context: 'standard', runAlways: 'true') {
        sqlFile(path: "${migrationsDir}/sql/create-or-replace-dictionary-views.sql", stripComments: true)
    }

    // do last

    include file: 'manage-stored-procedures.groovy'
    include file: 'manage-names-pkg.groovy'
    include file: 'drop-retired-tables.groovy'
    include file: 'manage-audit-procedures.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'grant-selects.groovy'
}



