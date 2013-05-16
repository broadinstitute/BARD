package iteration_025

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)


    changeSet(author: 'jasiedu', id: 'add trigger to recompute assay short name on assayType update', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""CREATE OR REPLACE TRIGGER TR_UPD_ASSAY_TYPE
      AFTER UPDATE OF ASSAY_TYPE ON ASSAY
      FOR EACH ROW BEGIN
      MANAGE_NAMES.UPDATE_ASSAY_SHORT_NAME(:OLD.ASSAY_ID);
END;
""")
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'drop trigger to recompute assay short name on assayType update', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""DROP TRIGGER TR_UPD_ASSAY_TYPE""")
            }
        }
    }

    changeSet(author: 'ddurkin', id: 'drop update name procedures', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                String query = """SELECT OBJECT_NAME  ,OBJECT_TYPE
                                  FROM USER_PROCEDURES
                                  WHERE OBJECT_NAME IN ('UPDATE_CONTEXT_NAME','UPDATE_PROJECT_CONTEXT_NAME',
                                                        'UPDATE_ASSAY_SHORT_NAME','UPDATE_EXPRMT_CONTEXT_NAME',
                                                        'UPDATE_STEP_CONTEXT_NAME', 'LOAD_DATA')
                                  GROUP BY OBJECT_NAME  ,OBJECT_TYPE"""
                sql.eachRow(query){ row ->
                    String dropStatement = "DROP ${row.OBJECT_TYPE} ${row.OBJECT_NAME}"
                    println(dropStatement)
                    sql.execute(dropStatement)
                }
            }
        }
    }


    changeSet(author: "ddurkin", id: "iteration_025/01-element-data-update.sql", dbms: "oracle", context: "production-data-update") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('schatwin');
                               manage_ontology.swap_element_id(1865, 1853);
                               manage_ontology.swap_element_id(1954, 1527);
                               manage_ontology.swap_element_id(1955, 735);
                               manage_ontology.swap_element_id(1956, 1396);
                               manage_ontology.swap_element_id(1957, 1632);
                               manage_ontology.swap_element_id(1958, 1853);
                               manage_ontology.swap_element_id(1959, 1446);
                               manage_ontology.swap_element_id(1961, 790);
                               manage_ontology.swap_element_id(1963, 1853);
                               manage_ontology.swap_element_id(1964, 1842);
                               manage_ontology.swap_element_id(1965, 1459);
                               manage_ontology.swap_element_id(1966, 1890);
                               manage_ontology.swap_element_id(1967, 1866);
                               manage_ontology.swap_element_id(1968, 726);
                               manage_ontology.swap_element_id(1969, 1938);
                               manage_ontology.swap_element_id(2008, 1850);
                               manage_ontology.swap_element_id(2015, 1896);
                               manage_ontology.swap_element_id(2019, 415);
                               manage_ontology.swap_element_id(2021, 1389);
                               END;
                               """)
                sql.execute("delete from element where element_id = 1865")
                sql.execute("delete from element where element_id = 1954")
                sql.execute("delete from element where element_id = 1955")
                sql.execute("delete from element where element_id = 1956")
                sql.execute("delete from element where element_id = 1957")
                sql.execute("delete from element where element_id = 1958")
                sql.execute("delete from element where element_id = 1959")
                sql.execute("delete from element where element_id = 1961")
                sql.execute("delete from element where element_id = 1963")
                sql.execute("delete from element where element_id = 1964")
                sql.execute("delete from element where element_id = 1965")
                sql.execute("delete from element where element_id = 1966")
                sql.execute("delete from element where element_id = 1967")
                sql.execute("delete from element where element_id = 1968")
                sql.execute("delete from element where element_id = 1969")
                sql.execute("delete from element where element_id = 2008")
                sql.execute("delete from element where element_id = 2015")
                sql.execute("delete from element where element_id = 2019")
                sql.execute("delete from element where element_id = 2021")

            }
        }
        sqlFile(path: "${migrationsDir}/iteration_025/01-element-data-update.sql", stripComments: true)
    }

    changeSet(author: "ddurkin", id: "iteration_025/02-add-element-columns.sql", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_025/02-add-element-columns.sql", stripComments: true)
        grailsChange {
            change {
                sql.execute("DROP SEQUENCE bard_ontology_seq")
            }
        }
    }

    changeSet(author: "ddurkin", id: "iteration_025/03-update-element-data.sql", dbms: "oracle", context: "production-data-update") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('schatwin');
                               END;
                               """)
            }
        }
        sqlFile(path: "${migrationsDir}/iteration_025/03-update-element-data.sql", stripComments: false) // there was a --- in a string that was getting stripped and messing up the executed sql
    }

}

