package iteration_027

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)


    changeSet(author: "jasiedu", id: "iteration-027/01-create-acl-tables", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_027/01-create-acl-tables.sql", stripComments: true)
    }
}

