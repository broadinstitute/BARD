/**
 * Simon's tools version tables to aid in data migration the versioned tables end in a timestamp/number sequence.
 *
 * like ASSAY_06202012001641000
 *
 * So we'll always look for these tables and drop them.
 */
databaseChangeLog = {
    changeSet(author: "ddurkin", id: "drop retired tables", dbms: 'oracle', runAlways: 'true') {
        grailsChange {
            change {
                sql.eachRow("SELECT TABLE_NAME FROM USER_TABLES WHERE REGEXP_LIKE(TABLE_NAME, '[^_]+_[0-9]+')") {row ->
                    String dropStatement = "DROP TABLE ${row.TABLE_NAME}"
                    sql.execute(dropStatement)
                }
            }
        }
    }
}