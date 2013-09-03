package iteration_018

databaseChangeLog = {
    changeSet(author: "dlahr", id: "iteration-018/01-change-statuses.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_018/01-change-statuses.sql", stripComments: true)
    }

	changeSet(author: "dlahr", id: "iteration-018/02-add-bard-tree-parent-constraint.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_018/02-add-bard-tree-parent-constraint.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-018/03-simplify-substance-table.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_018/03-simplify-substance-table.sql", stripComments: true)
    }
}
