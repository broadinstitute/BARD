package iteration_035

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-035/01-remove-new-object-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/01-remove-new-object-role.sql", stripComments: true)
    }

    changeSet(author: "gwalzer", id: "iteration-035/02-drop-Replaced-By-Id-column-in-Element-table", dbms: "oracle", context: "standard") {
        dropColumn(columnName: "REPLACED_BY_ID", tableName: "ELEMENT")
    }

    changeSet(author: "ycruz", id: "iteration-035/03-update-contextitem-valuedisplay", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                                   bard_context.set_username('ycruz');
                                   END;
                                   """)
            }
        }
        sqlFile(path: "iteration_035/03-update-contextitem-valuedisplay.sql", stripComments: true, endDelimiter: ";")
    }

}