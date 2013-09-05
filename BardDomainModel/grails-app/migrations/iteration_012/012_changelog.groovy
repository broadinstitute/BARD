package iteration_012

databaseChangeLog = {
    changeSet(author: 'ddurkin', id: 'iteration-012/sql/01-sequence-changes.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_012/sql/01-sequence-changes.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-012/sql/02-sequence-changes.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_012/sql/02-table-changes.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-012/sql/03-create-update-indexes-after-copy.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_012/sql/03-create-update-indexes-after-copy.sql", stripComments: true)
    }
}




