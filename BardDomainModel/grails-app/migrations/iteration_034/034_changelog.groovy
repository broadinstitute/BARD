package iteration_034

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-034/01-create-downtimescheduler", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_034/01-create-downtimescheduler.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-034/02-add-owner-role-to-entities", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_034/02-add-owner-role-to-entities.sql", stripComments: true)
    }
}