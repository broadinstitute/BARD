package iteration_020

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "pmontgom", id: "iteration-020/01-create-experiment-file.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration_020/01-create-experiment-file.sql", stripComments: true)
    }
}
