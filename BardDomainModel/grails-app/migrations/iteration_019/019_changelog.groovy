package iteration_019

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration-019/01-add-confidencelevel-to-experiment.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_019/01-add-confidencelevel-to-experiment.sql", stripComments: true)
    }

    changeSet(author: "jasiedu", id: "iteration-019/02-make-confidencelevel-nullable-experiment.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "iteration_019/02-make-confidencelevel-nullable-experiment.sql", stripComments: true)
    }
}
