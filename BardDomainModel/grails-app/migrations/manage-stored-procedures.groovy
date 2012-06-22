databaseChangeLog = {

    /**
     * Had some difficulty get the stored procedures to pass thru liquid base so parsing on / and using groovy sql
     *
     * NOTE: for this should notice changes to the store procedures and run the change when anything is updated, so
     *       just modify the create-ontology-procedures.sql file directly, this way vcs and easily see diffs over time.
     */
    changeSet(author: 'ddurkin', id: 'create-ontology-procedures.sql', dbms: 'oracle', runOnChange: 'true') {

        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql/create-ontology-procedures.sql').text
            for (String sqlBlock in text.split('/')) {
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

    // NOTE this changeset will always be run
    changeSet(author: "ddurkin", id: "execute manage_ontology.make_trees() stored procedure", dbms: 'oracle', runAlways: 'true') {
        grailsChange {
            change {
                sql.call('''
                        begin
                        -- manage_ontology.make_trees('RESULT_TYPE');  -- example of refreshing a single tree--------------
                        manage_ontology.make_trees();
                        end;
                    ''')
            }
        }
    }

}