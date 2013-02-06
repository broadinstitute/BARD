databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "dlahr", id: "iteration-018/01-change-statuses.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-018/01-change-statuses.sql", stripComments: true)
    }
	
	changeSet(author: "dlahr", id: "iteration-018/02-add-bard-tree-parent-constraint.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-018/02-add-bard-tree-parent-constraint.sql", stripComments: true)
    }
	
	changeSet(author: "dlahr", id: "iteration-018/03-drop-tables-update-views.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-018/03-drop-tables-update-views.sql", stripComments: true)
    }
}


