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

    changeSet(author: "gwalzer", id: "iteration-037/create-bard-news-table", context: "standard") {
        createTable(tableName: "BARD_NEWS") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "BARD_NEWS_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "entry_id", type: "VARCHAR2(255)") {
                constraints(nullable: "false")
            }

            column(name: "Date_Published", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "Entry_Date_Updated", type: "TIMESTAMP(6)") {
                constraints(nullable: "true")
            }

            column(name: "title", type: "VARCHAR2(1023)") {
                constraints(nullable: "false")
            }

            column(name: "content", type: "CLOB") {
                constraints(nullable: "true")
            }

            column(name: "link", type: "VARCHAR2(255)") {
                constraints(nullable: "false")
            }

            column(name: "author_name", type: "VARCHAR2(127)") {
                constraints(nullable: "true")
            }

            column(name: "author_email", type: "VARCHAR2(127)") {
                constraints(nullable: "true")
            }

            column(name: "author_uri", type: "VARCHAR2(255)") {
                constraints(nullable: "true")
            }

            column(name: "Date_Created", type: "TIMESTAMP(6)", defaultValueComputed: "SYSDATE") {
                constraints(nullable: "false")
            }

            column(name: "Last_Updated", type: "TIMESTAMP(6)") {
                constraints(nullable: "true")
            }

            column(name: "MODIFIED_BY", type: "VARCHAR2(40)") {
                constraints(nullable: "true")
            }
        }
        createSequence(sequenceName: "BARD_NEWS_ID_SEQ")
    }
}
