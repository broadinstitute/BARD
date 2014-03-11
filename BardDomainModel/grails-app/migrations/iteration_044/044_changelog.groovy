package iteration_044

databaseChangeLog = {
    changeSet(author: "ddurkin", id: "iteration_044/01_create_offline_validation_table", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_044/01_create_offline_validation_table.sql", stripComments: true)
    }
}

