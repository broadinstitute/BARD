package iteration_041

databaseChangeLog = {

    changeSet(author: "ycruz", id: "iteration-041/00-add_columns-approvedby-and-date-to-entities", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                                   bard_context.set_username('ycruz');
                                   END;
                                   """)
            }
        }
        sqlFile(path: "iteration_041/00-add_columns-approvedby-and-date-to-entities.sql", stripComments: true, endDelimiter: ";")
    }

}