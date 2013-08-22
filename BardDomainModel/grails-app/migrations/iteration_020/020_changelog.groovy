package iteration_020

databaseChangeLog = {
    changeSet(author: "pmontgom", id: "iteration-020/01-create-experiment-file.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_020/01-create-experiment-file.sql", stripComments: true)
    }
}
