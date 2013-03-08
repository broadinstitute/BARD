package iteration_006

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'set null assay_type to Regular', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.executeUpdate("""UPDATE ASSAY
                                     SET ASSAY_TYPE = 'Regular'
                                     WHERE ASSAY_TYPE IS NULL
                                  """)
            }
        }
    }

    changeSet(author: 'ddurkin', id: 'assay_type NOT NULL', dbms: 'oracle', context: 'standard') {
        addNotNullConstraint(tableName: "ASSAY", columnName: "assay_type")
    }

    changeSet(author: 'ddurkin', id: 'check constraint updates', dbms: 'oracle', context: 'standard', runOnChange: 'true') {
        // change spelling for to Superseded for CK_ASSAY_STATUS
        sql("ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_STATUS")
        sql("""UPDATE ASSAY SET ASSAY_STATUS = 'Superseded'
               WHERE ASSAY_STATUS = 'Superceded'
            """)
        sql("""ALTER TABLE ASSAY ADD ( CONSTRAINT CK_ASSAY_STATUS
               CHECK ( Assay_Status IN ('Pending', 'Active', 'Superseded', 'Retired') ))
            """)

        sql("""ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_EXTRACTION
            """)
        sql("""ALTER TABLE ASSAY ADD ( CONSTRAINT CK_ASSAY_EXTRACTION
               CHECK ( ready_for_extraction IN ('Pending', 'Ready', 'Started', 'Complete')))
            """)


    }

    changeSet(author: 'ddurkin', id: 'iteration-006/01-assay-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/01-assay-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/02-element-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/02-element-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/03-project-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/03-project-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/04-result-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/04-result-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/05-identifier_mapping-index.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/05-identifier_mapping-index.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-006/06-project-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/06-project-related-refactoring.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-006/07-more-element-changes.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "${migrationsDir}/iteration_006/sql/07-more-element-changes.sql", stripComments: true)
    }


}