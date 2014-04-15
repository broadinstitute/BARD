package iteration_046

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration_046/01_recreate_status_contraint", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_046/01_recreate_status_contraint.sql", stripComments: true)
    }
}

