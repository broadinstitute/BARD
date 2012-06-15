databaseChangeLog = {
    include file: 'iteration-001/01-initial-base-line.groovy'

    /**
     * Had some difficulty get the stored procedures to pass thru liquid base so parsing on / and using groovy sql
     *
     * NOTE: for this should notice changes to the store procedures and run the change when anything is updated, so
     *       just modify the create-ontology-procedures.sql file directly, this way vcs and easily see diffs over time.
     */
    changeSet(author: 'ddurkin', id: 'create-ontology-procedures.sql', dbms: 'oracle', runOnChange: 'true') {

        grailsChange {
            final List<String> sqlBlocks = []
            File file = new File('grails-app/migrations/sql/create-ontology-procedures.sql')
            for (String sqlBlock in file.text.split('/')) {
                sqlBlocks.add(sqlBlock)
            }

            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }

            checkSum(sqlBlocks.join('/'))
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
