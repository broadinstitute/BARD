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

    changeSet(author: "jasiedu", id: "iteration-025/01-recreate-context-item-constraints", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_025/01-recreate-context-item-constraints.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-025/02-rename-comments-column-element", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_025/02-rename-comments-column-element.sql", stripComments: true)
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

    changeSet(author: "ddurkin", id: "iteration_025/03-add-element-columns.sql", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_025/03-add-element-columns.sql", stripComments: true)
    }

    changeSet(author: "ddurkin", id: "iteration_025/04-element-update-data-for-2-new-columns.sql", dbms: "oracle", context: "production-data-update") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('schatwin');
                               END;
                               """)
            }
        }
        sqlFile(path: "${migrationsDir}/iteration_025/04-element-update-data-for-2-new-columns.sql", stripComments: false) // there was a --- in a string that was getting stripped and messing up the executed sql
    }

}

