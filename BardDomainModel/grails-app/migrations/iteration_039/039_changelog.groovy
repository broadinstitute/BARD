package iteration_039

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration-039/01-modify-authority-constraint-on-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/01-modify-authority-constraint-on-role.sql", stripComments: true, endDelimiter: ";")
    }
}
