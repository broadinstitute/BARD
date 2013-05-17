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

}

