import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'rename-changelog-filenames.sql', dbms: 'oracle', context: 'rename-changelog') {
        sqlFile(path: "${migrationsDir}/sql/rename-changelog-filenames.sql", stripComments: true)
    }

    /* this needs to be created early in the process because some of the changelogs manually call set_context */
    changeSet(author: 'ddurkin', id: 'create-bard-context.sql', dbms: 'oracle', context: 'standard, auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/create-bard-context.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
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

    include file: 'manage-audit-procedures.groovy'
    include file: 'manage-stored-procedures.groovy'
    include file: 'manage-names-pkg.groovy'
    include file: 'drop-retired-tables.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'grant-selects.groovy'
}



