databaseChangeLog = {


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


}