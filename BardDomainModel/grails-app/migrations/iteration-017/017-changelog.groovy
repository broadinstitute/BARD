databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "pmontgom", id: "iteration-017/01-create-bard-tree.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-017/01-create-bard-tree.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-017/02-rename-papers.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-017/02-rename-papers.sql", stripComments: true)
    }
}


