package iteration_017

databaseChangeLog = {
    changeSet(author: "pmontgom", id: "iteration-017/01-create-bard-tree.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_017/01-create-bard-tree.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-017/02-rename-papers.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_017/02-rename-papers.sql", stripComments: true)
    }
}


