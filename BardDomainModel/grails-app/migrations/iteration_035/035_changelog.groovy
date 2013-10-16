package iteration_035

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-035/01-remove-new-object-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/01-remove-new-object-role.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-035/02-reset-auditing-on-Person-Table", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.call('''call auditing_setup.update_settings('', 'Refresh')''')
                sql.call('''call auditing_init.make_triggers()''')
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'update user names with email address', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
                String query = """SELECT EMAIL_ADDRESS
                                  FROM PERSON
                                  WHERE EMAIL_ADDRESS IS NOT NULL"""
                sql.eachRow(query) { row ->
                    String updateStatement = "UPDATE PERSON SET USERNAME='${row.EMAIL_ADDRESS}' WHERE EMAIL_ADDRESS='${row.EMAIL_ADDRESS}'"
                    //println("${updateStatement}")
                    sql.executeUpdate(updateStatement)
                }
            }
        }
    }
}