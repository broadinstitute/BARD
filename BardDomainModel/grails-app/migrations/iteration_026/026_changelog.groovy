package iteration_026

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-026/01-add-display-name-column-to-Role", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_026/01-add-display-name-column-to-Role.sql", stripComments: true)
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.clear_username();
                               END;
                               """)
            }
        }
    }
    changeSet(author: "jasiedu", id: "iteration-026/02-add-curators-and-Role", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_026/02-add-curators-and-Role.sql", stripComments: true)
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.clear_username();
                               END;
                               """)
            }
        }
    }
}

