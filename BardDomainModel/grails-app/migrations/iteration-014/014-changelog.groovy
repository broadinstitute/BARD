databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'iteration-014/sql/01-syncing-with-latest-model.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration-014/sql/01-syncing-with-latest-model.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-014/sql/02-project-step-changes.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration-014/sql/02-project-step-changes.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-014/sql/03-rename-project_experiment_context_item.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration-014/sql/03-normalize-naming-project_experiment_context_item.sql", stripComments: true)
    }
}




