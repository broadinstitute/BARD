databaseChangeLog = {

    /**
     * the (?m) is a flag to treat the input as multiline
     * the rest is lookin for lines with only a / on it , it can have whitespace on either side
     */
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/
    /**
     * Had some difficulty get the stored procedures to pass thru liquid base so parsing on / and using groovy sql
     *
     * NOTE: for this should notice changes to the store procedures and run the change when anything is updated, so
     *       just modify the create-ontology-procedures.sql file directly, this way vcs and easily see diffs over time.
     */

    changeSet(author: 'ddurkin', id: 'pkg_auditing_setup.sql', dbms: 'oracle', context: 'auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing_setup.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }
    changeSet(author: 'ddurkin', id: 'pkg_auditing_init.sql', dbms: 'oracle', context: 'auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing_init.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }
    changeSet(author: 'ddurkin', id: 'pkg_auditing.sql', dbms: 'oracle', context: 'auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }

    changeSet(author: "ddurkin", id: "call auditing_setup.setup_tables();", context: 'auditing', dbms: 'oracle') {
        grailsChange {
            change {
                sql.call('call auditing_setup.setup_tables()')
            }
        }
    }

    changeSet(author: "ddurkin", id: "refresh audit_settings and triggers", context: 'auditing', dbms: 'oracle', runAlways: 'true' ) {
        grailsChange {
            change {
                sql.call('''call auditing_setup.update_settings('', 'Refresh')''')
                sql.call('''call auditing_init.make_triggers()''')
            }
        }
    }

    /**
     *  here as a developer utility
     */
    changeSet(author: "ddurkin", id: "drop audit triggers audit_settings and triggers", context: 'drop-auditing-triggers', dbms: 'oracle', runAlways: 'true') {
        grailsChange {
            change {
                String dropQueryTrigger = """SELECT 'drop trigger ' || trigger_name as dropSql FROM user_triggers WHERE TRIGGER_NAME LIKE 'ADT_%' """
                sql.eachRow(dropQueryTrigger){row ->
                    println(row.dropSql)
                    sql.executeUpdate(row.dropSql)
                }
            }
        }
    }
}