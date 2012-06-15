databaseChangeLog = {
    changeSet(author: "ddurkin", id: "01-oracle-ddl-baseline.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/01-oracle-ddl-baseline.sql')
    }
    changeSet(author: "ddurkin", id: "02-element-inserts.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/02-element-inserts.sql')
    }
    changeSet(author: "ddurkin", id: "03-element-inserts.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/03-element-inserts.sql')
    }
    changeSet(author: "ddurkin", id: "04-element_hierarchy-inserts.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/04-element_hierarchy-inserts.sql')
    }
    changeSet(author: "ddurkin", id: "05-ontology-insert.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/05-ontology-insert.sql')
    }
    changeSet(author: "ddurkin", id: "06-ontology_item-inserts.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/06-ontology_item-inserts.sql')
    }
    changeSet(author: "ddurkin", id: "07-tree_root-inserts.sql", dbms:'oracle') {
        sqlFile(path: 'iteration-001/sql/07-tree_root-inserts.sql')
    }

}
