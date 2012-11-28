databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id:   'iteration-014/sql/01-syncing-with-latest-model.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration-014/sql/01-syncing-with-latest-model.sql", stripComments: true)
    }

}




