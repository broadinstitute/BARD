package iteration_026

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)


    changeSet(author: "jasiedu", id: "iteration-026/01-add-display-name-column-to-Role", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_026/01-add-display-name-column-to-Role.sql", stripComments: true)
    }
}

