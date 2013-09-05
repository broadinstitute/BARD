import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/

    changeSet(author: 'pmontgom', id: 'invalid-context', dbms: 'oracle', context: 'invalidContext', runAlways: true) {
        grailsChange {
            change {
                throw new RuntimeException("Woah there cowboy!  Always run dbm-update with --contexts=...")
            }
        }
    }

    changeSet(author: 'ddurkin', id: 'rename-changelog-filenames.sql', dbms: 'oracle', context: 'rename-changelog') {
        sqlFile(path: "sql/rename-changelog-filenames.sql", stripComments: true)
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
        sqlFile(path: "sql/baseline-structure-ddl.sql", stripComments: true)
    }

    include(file: "iteration_006/006_changelog.groovy")
    include(file: "iteration_007/007_changelog.groovy")
    include(file: "iteration_012/012_changelog.groovy")
    include(file: "iteration_014/014_changelog.groovy")
    include(file: "iteration_017/017_changelog.groovy")
    include(file: "iteration_018/018_changelog.groovy")
    include(file: "iteration_019/019_changelog.groovy")
    include(file: "iteration_020/020_changelog.groovy")
    include(file: "iteration_021/021_changelog.groovy")
    include(file: "iteration_023/023_changelog.groovy")
    include(file: "iteration_024/024_changelog.groovy")
    include(file: "iteration_025/025_changelog.groovy")
    include(file: "iteration_026/026_changelog.groovy")
    include(file: "iteration_027/027_changelog.groovy")
    include(file: "iteration_028/028_changelog.groovy")
    include(file: "iteration_030/030_changelog.groovy")
    include(file: "iteration_032/032_changelog.groovy")

    // views
    changeSet(author: 'ddurkin', id: 'create-or-replace-dictionary-views.sql', dbms: 'oracle', context: 'standard', runAlways: 'true') {
        sqlFile(path: "sql/create-or-replace-dictionary-views.sql", stripComments: true)
    }

    // do last

    include file: 'manage-audit-procedures.groovy'
    include file: 'manage-stored-procedures.groovy'
    include file: 'manage-names-pkg.groovy'
    include file: 'drop-retired-tables.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'grant-selects.groovy'
}



