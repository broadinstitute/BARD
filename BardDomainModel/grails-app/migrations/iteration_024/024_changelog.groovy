package iteration_024

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "pmontgom", id: "iteration-024/01-add-experiment-file-pk", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/01-add-experiment-file-pk.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/01-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/01-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/03-add-display-order-unique-constraint", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_024/03-add-display-order-unique-constraint.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "add parent child relationship to measures", dbms: 'oracle', context:'standard') {
        grailsChange {
            change {
                sql.eachRow("SELECT DISTINCT MEASURE_ID, PARENT_CHILD_RELATIONSHIP FROM EXPRMT_MEASURE WHERE PARENT_CHILD_RELATIONSHIP IS NOT NULL ORDER BY MEASURE_ID") {row ->
                    String updateStatement = "UPDATE MEASURE SET PARENT_CHILD_RELATIONSHIP='${row.PARENT_CHILD_RELATIONSHIP}' WHERE MEASURE_ID=${row.MEASURE_ID}"
                    sql.executeUpdate(updateStatement)
                }
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'update parent child for mesures', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.executeUpdate("""UPDATE MEASURE SET PARENT_CHILD_RELATIONSHIP='supported by'
                                      WHERE PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_MEASURE_ID IS NOT NULL
                                  """)
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'add parent child constraint to EXPRMT_MEASURE', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""ALTER TABLE EXPRMT_MEASURE ADD CONSTRAINT CK_EXPRMT_MEASURE_PARENT
                                    CHECK ((PARENT_CHILD_RELATIONSHIP IS NOT NULL AND PARENT_EXPRMT_MEASURE_ID IS NOT NULL)
                                    OR
                                    (PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_EXPRMT_MEASURE_ID IS NULL))
                                  """)
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'add parent child constraint to MEASURE_PARENT', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""ALTER TABLE MEASURE ADD CONSTRAINT CK_MEASURE_PARENT
                                CHECK ((PARENT_CHILD_RELATIONSHIP IS NOT NULL AND PARENT_MEASURE_ID IS NOT NULL)
                                OR (PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_MEASURE_ID IS NULL))
                                  """)
            }
        }
    }
}

