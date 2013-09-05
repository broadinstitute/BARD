package iteration_023

databaseChangeLog = {
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/

    changeSet(author: "pmontgom", id: "iteration-023/01-populate-qualifier", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_023/01-populate-qualifier.sql", stripComments: true)
    }

    changeSet(author: "jasiedu", id: "iteration-023/03-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_023/03-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-023/04-update-parent-child-relationship-to-experiment", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_023/04-update-parent-child-relationship-to-experiment.sql", stripComments: true)
    }

}

