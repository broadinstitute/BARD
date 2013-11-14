package iteration_037

databaseChangeLog = {

    changeSet(author: "sbrudz", id: "iteration-037/01-remove-vestigial-columns-from-query-cart", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("alter table sc_shoppable drop column NUM_ASSAY_ACTIVE")
                sql.execute("alter table sc_shoppable drop column NUM_ASSAY_TESTED")
            }
        }
    }

}
