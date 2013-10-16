package iteration_035

databaseChangeLog = {

    changeSet(author: "ycruz", id: "iteration-035/01-update-contextitem-valuedisplay", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                                   bard_context.set_username('ycruz');
                                   END;
                                   """)
            }
        }
        sqlFile(path: "iteration_035/01-update-contextitem-valuedisplay.sql", stripComments: true, endDelimiter: ";")
    }

}