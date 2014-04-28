/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
            }
        }
    }

    changeSet(author: "ycruz", id: "iteration-043/01_modify_approvedby_add_foreign_key_in_entities", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")
            }
        }
        sqlFile(path: "iteration_043/01_modify_approvedby_add_foreign_key_in_entities.sql", stripComments: true, endDelimiter: ";")
    }

    changeSet(author: "ycruz", id: "iteration-043/02_populate_approvedby_in_entities", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")
                String PERSON_BY_NAME = """SELECT * FROM PERSON WHERE FULL_NAME = ?"""

                String ALL_ASSAYS = """SELECT * FROM ASSAY"""
                String UPDATE_ASSAY_APPROVED_BY = """UPDATE ASSAY SET APPROVED_BY = ? WHERE ASSAY_ID = ?"""
                println("Starting to update ASSAY records")
                sql.eachRow(ALL_ASSAYS) { row ->
                    def tempApprovedBy = row.APPROVED_BY_TEMP
                    if(tempApprovedBy){
                        def person = sql.firstRow(PERSON_BY_NAME, [tempApprovedBy])
                        if(person){
                            try{
                                sql.executeUpdate(UPDATE_ASSAY_APPROVED_BY, [person.PERSON_ID, row.ASSAY_ID])
                            }
                            catch(java.sql.SQLException e){
                                println("ERROR updating APPROVED_BY (new value: ${person.PERSON_ID}) field in Assay ID: ${row.ASSAY_ID} -> ${e.message}")
                                throw e
                            }
                        }
                    }
                }
                println("Finished updating ASSAY records")

                String ALL_PROJECTS = """SELECT * FROM PROJECT"""
                String UPDATE_PROJECT_APPROVED_BY = """UPDATE PROJECT SET APPROVED_BY = ? WHERE PROJECT_ID = ?"""
                println("Starting to update PROJECT records")
                sql.eachRow(ALL_PROJECTS) { row ->
                    def tempApprovedBy = row.APPROVED_BY_TEMP
                    if(tempApprovedBy){
                        def person = sql.firstRow(PERSON_BY_NAME, [tempApprovedBy])
                        if(person){
                            try{
                                sql.executeUpdate(UPDATE_PROJECT_APPROVED_BY, [person.PERSON_ID, row.PROJECT_ID])
                            }
                            catch(java.sql.SQLException e){
                                println("ERROR updating APPROVED_BY (new value: ${person.PERSON_ID}) field in PROJECT ID: ${row.PROJECT_ID} -> ${e.message}")
                                throw e
                            }
                        }
                    }
                }
                println("Finished updating PROJECT records")

                String ALL_EXPERIMENTS = """SELECT * FROM EXPERIMENT"""
                String UPDATE_EXPERIMENT_APPROVED_BY = """UPDATE EXPERIMENT SET APPROVED_BY = ? WHERE EXPERIMENT_ID = ?"""
                println("Starting to update EXPERIMENT records")
                sql.eachRow(ALL_EXPERIMENTS) { row ->
                    def tempApprovedBy = row.APPROVED_BY_TEMP
                    if(tempApprovedBy){
                        def person = sql.firstRow(PERSON_BY_NAME, [tempApprovedBy])
                        if(person){
                            try{
                                sql.executeUpdate(UPDATE_EXPERIMENT_APPROVED_BY, [person.PERSON_ID, row.EXPERIMENT_ID])
                            }
                            catch(java.sql.SQLException e){
                                println("ERROR updating APPROVED_BY (new value: ${person.PERSON_ID}) field in EXPERIMENT ID: ${row.ASSAY_ID} -> ${e.message}")
                                throw e
                            }
                        }
                    }
                }
                println("Finished updating EXPERIMENT records")
            }
        }
    }

    changeSet(author: "ycruz", id: "iteration-043/03_drop_old_approvedby_columns", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                println("Dropping old approved_by columns")
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")
                sql.execute("ALTER TABLE ASSAY DROP COLUMN APPROVED_BY_TEMP")
                sql.execute("ALTER TABLE PROJECT DROP COLUMN APPROVED_BY_TEMP")
                sql.execute("ALTER TABLE EXPERIMENT DROP COLUMN APPROVED_BY_TEMP")
                println("Finished dropping columns")
            }
        }
    }

}
