databaseChangeLog = {
    String fileName = 'iteration-002/sql/01-initial-changes.sql'
    changeSet(author: "ddurkin", id: fileName, dbms: 'oracle') {
        sqlFile(path: fileName)
    }
}
