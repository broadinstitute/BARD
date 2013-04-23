package iteration_024

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    changeSet(author: "jasiedu", id: "iteration-024/01-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/01-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }

}

