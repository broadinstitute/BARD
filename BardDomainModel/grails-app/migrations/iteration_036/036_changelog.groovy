package iteration_036

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-036/01-update-null-owner-role-on-assays", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)

                String BARD_ADMIN_ROLE_SQL = """SELECT ROLE_ID FROM ROLE WHERE AUTHORITY='ROLE_BARD_ADMINISTRATOR'"""
                String BARD_ADMIN_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='ROLE_BARD_ADMINISTRATOR'"""
                String OWNER_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='FOR USE IN ACL_OBJECT_IDENITY OWNER_SID ONLY'"""
                String ASSAY_ACL_CLASS_SQL = """SELECT ID FROM ACL_CLASS WHERE CLASS='bard.db.registration.Assay'"""

                def ADMIN_SID_ID = sql.firstRow(BARD_ADMIN_SID_SQL)?.ID
                def OWNER_SID_ID = sql.firstRow(OWNER_SID_SQL)?.ID
                def ASSAY_CLASS_ID = sql.firstRow(ASSAY_ACL_CLASS_SQL)?.ID
                def ADMIN_ROLE_ID = sql.firstRow(BARD_ADMIN_ROLE_SQL)?.ROLE_ID

                if (ASSAY_CLASS_ID) {
                    String ASSAY_NO_OWNER = "SELECT ASSAY_ID FROM ASSAY WHERE ASSAY_ID NOT IN (SELECT OBJECT_ID_IDENTITY FROM ACL_OBJECT_IDENTITY WHERE ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS=" + ASSAY_CLASS_ID + ")"
                    sql.eachRow(ASSAY_NO_OWNER) { row ->
                        def assay_id = row.ASSAY_ID
                        // println(assay_id)

                        def ACL_OBJECT_IDENTITY_ID = sql.firstRow("SELECT ACL_OBJECT_IDENTITY_ID_SEQ.nextval as ID from dual").ID
                        sql.executeInsert """
                INSERT INTO ACL_OBJECT_IDENTITY (ID,OBJECT_ID_CLASS,ENTRIES_INHERITING,OBJECT_ID_IDENTITY,OWNER_SID) VALUES (?,?,?,?,?)
                    """, [ACL_OBJECT_IDENTITY_ID, ASSAY_CLASS_ID, 1, assay_id, OWNER_SID_ID]


                        sql.executeInsert """
                    INSERT INTO ACL_ENTRY (ID,ACE_ORDER,ACL_OBJECT_IDENTITY,AUDIT_FAILURE,AUDIT_SUCCESS,GRANTING,MASK,SID) VALUES (ACL_ENTRY_ID_SEQ.nextval,?,?,?,?,?,?,?)
                """, [0, ACL_OBJECT_IDENTITY_ID, 0, 0, 1, 16, ADMIN_SID_ID]


                    }
                    if (ADMIN_ROLE_ID) {
                        String updateStatement = """UPDATE ASSAY SET OWNER_ROLE_ID=${ADMIN_ROLE_ID} WHERE OWNER_ROLE_ID IS NULL"""
                        sql.executeUpdate(updateStatement)
                    }
                }
            }
        }
    }
    changeSet(author: "jasiedu", id: "iteration-036/02-update-null-owner-role-on-projects", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)

                String BARD_ADMIN_ROLE_SQL = """SELECT ROLE_ID FROM ROLE WHERE AUTHORITY='ROLE_BARD_ADMINISTRATOR'"""
                String BARD_ADMIN_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='ROLE_BARD_ADMINISTRATOR'"""
                String OWNER_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='FOR USE IN ACL_OBJECT_IDENITY OWNER_SID ONLY'"""
                String PROJECT_ACL_CLASS_SQL = """SELECT ID FROM ACL_CLASS WHERE CLASS='bard.db.project.Project'"""

                def ADMIN_SID_ID = sql.firstRow(BARD_ADMIN_SID_SQL)?.ID
                def OWNER_SID_ID = sql.firstRow(OWNER_SID_SQL)?.ID
                def PROJECT_CLASS_ID = sql.firstRow(PROJECT_ACL_CLASS_SQL)?.ID
                def ADMIN_ROLE_ID = sql.firstRow(BARD_ADMIN_ROLE_SQL)?.ROLE_ID

                if (PROJECT_CLASS_ID) {
                    String PROJECT_NO_OWNER = "SELECT PROJECT_ID FROM PROJECT WHERE PROJECT_ID NOT IN (SELECT OBJECT_ID_IDENTITY FROM ACL_OBJECT_IDENTITY WHERE ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS=" + PROJECT_CLASS_ID + ")"
                    sql.eachRow(PROJECT_NO_OWNER) { row ->
                        def project_id = row.PROJECT_ID
                        // println( project_id)

                        def ACL_OBJECT_IDENTITY_ID = sql.firstRow("SELECT ACL_OBJECT_IDENTITY_ID_SEQ.nextval as ID from dual").ID
                        sql.executeInsert """
                INSERT INTO ACL_OBJECT_IDENTITY (ID,OBJECT_ID_CLASS,ENTRIES_INHERITING,OBJECT_ID_IDENTITY,OWNER_SID) VALUES (?,?,?,?,?)
                    """, [ACL_OBJECT_IDENTITY_ID, PROJECT_CLASS_ID, 1, project_id, OWNER_SID_ID]


                        sql.executeInsert """
                    INSERT INTO ACL_ENTRY (ID,ACE_ORDER,ACL_OBJECT_IDENTITY,AUDIT_FAILURE,AUDIT_SUCCESS,GRANTING,MASK,SID) VALUES (ACL_ENTRY_ID_SEQ.nextval,?,?,?,?,?,?,?)
                """, [0, ACL_OBJECT_IDENTITY_ID, 0, 0, 1, 16, ADMIN_SID_ID]


                    }
                    String updateStatement = """UPDATE PROJECT SET OWNER_ROLE_ID=${ADMIN_ROLE_ID} WHERE OWNER_ROLE_ID IS NULL"""
                    sql.executeUpdate(updateStatement)
                }
            }
        }
    }

    changeSet(author: "jasiedu", id: "iteration-036/03-update-null-owner-role-on-experiments", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)

                String BARD_ADMIN_ROLE_SQL = """SELECT ROLE_ID FROM ROLE WHERE AUTHORITY='ROLE_BARD_ADMINISTRATOR'"""
                String BARD_ADMIN_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='ROLE_BARD_ADMINISTRATOR'"""
                String OWNER_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='FOR USE IN ACL_OBJECT_IDENITY OWNER_SID ONLY'"""
                String EXPERIMENT_ACL_CLASS_SQL = """SELECT ID FROM ACL_CLASS WHERE CLASS='bard.db.experiment.Experiment'"""

                def ADMIN_SID_ID = sql.firstRow(BARD_ADMIN_SID_SQL)?.ID
                def OWNER_SID_ID = sql.firstRow(OWNER_SID_SQL)?.ID
                def EXPERIMENT_CLASS_ID = sql.firstRow(EXPERIMENT_ACL_CLASS_SQL)?.ID
                def ADMIN_ROLE_ID = sql.firstRow(BARD_ADMIN_ROLE_SQL)?.ROLE_ID

                if (EXPERIMENT_CLASS_ID) {
                    String EXPERIMENT_NO_OWNER = "SELECT EXPERIMENT_ID FROM EXPERIMENT WHERE EXPERIMENT_ID NOT IN (SELECT OBJECT_ID_IDENTITY FROM ACL_OBJECT_IDENTITY WHERE ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS=" + EXPERIMENT_CLASS_ID + ")"
                    sql.eachRow(EXPERIMENT_NO_OWNER) { row ->
                        def experiment_id = row.EXPERIMENT_ID
                        //println( experiment_id)

                        def ACL_OBJECT_IDENTITY_ID = sql.firstRow("SELECT ACL_OBJECT_IDENTITY_ID_SEQ.nextval as ID from dual").ID
                        sql.executeInsert """
                INSERT INTO ACL_OBJECT_IDENTITY (ID,OBJECT_ID_CLASS,ENTRIES_INHERITING,OBJECT_ID_IDENTITY,OWNER_SID) VALUES (?,?,?,?,?)
                    """, [ACL_OBJECT_IDENTITY_ID, EXPERIMENT_CLASS_ID, 1, experiment_id, OWNER_SID_ID]


                        sql.executeInsert """
                    INSERT INTO ACL_ENTRY (ID,ACE_ORDER,ACL_OBJECT_IDENTITY,AUDIT_FAILURE,AUDIT_SUCCESS,GRANTING,MASK,SID) VALUES (ACL_ENTRY_ID_SEQ.nextval,?,?,?,?,?,?,?)
                """, [0, ACL_OBJECT_IDENTITY_ID, 0, 0, 1, 16, ADMIN_SID_ID]
                    }


                    String updateStatement = """UPDATE EXPERIMENT SET OWNER_ROLE_ID=${ADMIN_ROLE_ID} WHERE OWNER_ROLE_ID IS NULL"""
                    sql.executeUpdate(updateStatement)
                }

            }
        }
    }

    changeSet(author: "jasiedu", id: "iteration-036/04-update-null-owner-role-on-panels", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)

                String BARD_ADMIN_ROLE_SQL = """SELECT ROLE_ID FROM ROLE WHERE AUTHORITY='ROLE_BARD_ADMINISTRATOR'"""
                String BARD_ADMIN_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='ROLE_BARD_ADMINISTRATOR'"""
                String OWNER_SID_SQL = """SELECT ID FROM ACL_SID WHERE SID='FOR USE IN ACL_OBJECT_IDENITY OWNER_SID ONLY'"""
                String PANEL_ACL_CLASS_SQL = """SELECT ID FROM ACL_CLASS WHERE CLASS='bard.db.registration.Panel'"""

                def ADMIN_SID_ID = sql.firstRow(BARD_ADMIN_SID_SQL)?.ID
                def OWNER_SID_ID = sql.firstRow(OWNER_SID_SQL)?.ID
                def PANEL_CLASS_ID = sql.firstRow(PANEL_ACL_CLASS_SQL)?.ID
                def ADMIN_ROLE_ID = sql.firstRow(BARD_ADMIN_ROLE_SQL)?.ROLE_ID
                if (PANEL_CLASS_ID) {

                    String PANEL_NO_OWNER = "SELECT PANEL_ID FROM PANEL WHERE PANEL_ID NOT IN (SELECT OBJECT_ID_IDENTITY FROM ACL_OBJECT_IDENTITY WHERE ACL_OBJECT_IDENTITY.OBJECT_ID_CLASS=" + PANEL_CLASS_ID + ")"
                    sql.eachRow(PANEL_NO_OWNER) { row ->
                        def panel_id = row.PANEL_ID
                        // println( panel_id)

                        def ACL_OBJECT_IDENTITY_ID = sql.firstRow("SELECT ACL_OBJECT_IDENTITY_ID_SEQ.nextval as ID from dual").ID
                        sql.executeInsert """
                INSERT INTO ACL_OBJECT_IDENTITY (ID,OBJECT_ID_CLASS,ENTRIES_INHERITING,OBJECT_ID_IDENTITY,OWNER_SID) VALUES (?,?,?,?,?)
                    """, [ACL_OBJECT_IDENTITY_ID, PANEL_CLASS_ID, 1, panel_id, OWNER_SID_ID]


                        sql.executeInsert """
                    INSERT INTO ACL_ENTRY (ID,ACE_ORDER,ACL_OBJECT_IDENTITY,AUDIT_FAILURE,AUDIT_SUCCESS,GRANTING,MASK,SID) VALUES (ACL_ENTRY_ID_SEQ.nextval,?,?,?,?,?,?,?)
                """, [0, ACL_OBJECT_IDENTITY_ID, 0, 0, 1, 16, ADMIN_SID_ID]


                    }
                    String updateStatement = """UPDATE PANEL SET OWNER_ROLE_ID=${ADMIN_ROLE_ID} WHERE OWNER_ROLE_ID IS NULL"""
                    sql.executeUpdate(updateStatement)
                }
            }
        }
    }
    changeSet(author: "jasiedu", id: "iteration-036/05-add-not-null-owner-role-on-entities", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_036/05-add-not-null-owner-role-on-entities.sql", stripComments: true, endDelimiter: ";")
    }

}
