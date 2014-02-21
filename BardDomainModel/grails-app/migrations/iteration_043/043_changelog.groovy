package iteration_043

databaseChangeLog = {

    changeSet(author: "ycruz", id: "iteration-043/00-add_columns-teamrole-to-personrole", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")
                sql.execute("ALTER TABLE PERSON_ROLE ADD TEAM_ROLE VARCHAR2(40)")

                String ALL_PERSON_ROLE = """SELECT * FROM PERSON_ROLE"""
                String updateStatement = """UPDATE PERSON_ROLE SET TEAM_ROLE = 'Member' WHERE PERSON_ROLE_ID = ?"""
                println("Starting to update PERSON_ROLE records")
                sql.eachRow(ALL_PERSON_ROLE) { row ->
                    def person_role_id = row.PERSON_ROLE_ID
                    try{
                        sql.executeUpdate(updateStatement, [person_role_id])
                    }
                    catch(java.sql.SQLSyntaxErrorException e){
                        println("ERROR updating PERSON_ROLE record with ID: ${person_role_id} -> ${e.message}")
                        throw e
                    }
                }
                println("Finished updating PERSON_ROLE records")

                println("Finished updates for iteration_043")
            }
        }
    }

}