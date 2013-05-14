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
    changeSet(author: 'jasiedu', id: 'create-manage-names-package.sql', dbms: 'oracle', context: 'standard', runOnChange: 'true') {

        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql/pkg_manage_names.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {

                sqlBlocks.add(sqlBlock)
            }

            change {
                for (String sqlBlock in sqlBlocks) {
                   // println sqlBlock
                    sql.call(sqlBlock)
                }
            }

            checkSum(text)
        }


    }
}