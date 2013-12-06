package iteration_039

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration-039/01-modify-authority-constraint-on-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/01-modify-authority-constraint-on-role.sql", stripComments: true, endDelimiter: ";")
    }

    changeSet(author: "pmontgom", id: "iteration-039/02-schema-cleanup", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/02-schema-cleanup.sql", stripComments: true)

        grailsChange {
            change {
                sql.execute("""DECLARE
                       l_sql  VARCHAR2(1000);
                    BEGIN
                      SELECT constraint_name
                      INTO l_sql
                      FROM user_constraints
                      WHERE table_name = 'PANEL_EXPERIMENT'
                        AND constraint_type = 'P';

                      l_sql := 'ALTER TABLE PANEL_EXPERIMENT RENAME CONSTRAINT '|| l_sql ||' TO PK_PANEL_EXPERIMENT';
                      EXECUTE IMMEDIATE l_sql;

                    END;""")
            }
        }

    }
}
