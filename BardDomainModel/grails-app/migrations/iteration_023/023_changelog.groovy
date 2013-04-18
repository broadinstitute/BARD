package iteration_023

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/

    changeSet(author: "pmontgom", id: "iteration-023/01-populate-qualifier", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_023/01-populate-qualifier.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-023/02-fix-conversions", dbms: 'oracle', context: 'standard') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('iteration_023/02-fix-conversions.sql').text
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
    changeSet(author: "jasiedu", id: "iteration-023/03-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_023/03-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }

}

