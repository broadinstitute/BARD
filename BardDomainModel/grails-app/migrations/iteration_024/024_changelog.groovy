package iteration_024

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "pmontgom", id: "iteration-024/01-add-experiment-file-pk", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/01-add-experiment-file-pk.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/01-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/01-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/03-add-display-order-unique-constraint", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/03-add-display-order-unique-constraint.sql", stripComments: true)
    }
}

