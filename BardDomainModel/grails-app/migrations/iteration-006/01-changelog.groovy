databaseChangeLog = {


    changeSet(author: 'ddurkin', id: 'set null assay_type to Regular', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.executeUpdate('''UPDATE ASSAY
                                     SET ASSAY_TYPE = 'Regular'
                                     WHERE ASSAY_TYPE IS NULL''')
            }
        }
    }



    changeSet(author: 'ddurkin', id: 'assay_type NOT NULL', dbms: 'oracle', context: 'standard') {
        addNotNullConstraint(tableName: "ASSAY", columnName: "assay_type")
    }


}