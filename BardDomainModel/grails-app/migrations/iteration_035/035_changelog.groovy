package iteration_035

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-035/01-remove-new-object-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/01-remove-new-object-role.sql", stripComments: true)
    }
}