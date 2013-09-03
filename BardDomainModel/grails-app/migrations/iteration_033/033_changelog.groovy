package iteration_033

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration-033/01-update-expt-measure", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }

        //
        sqlFile(path: "iteration_033/01-update-expt-measure.sql", stripComments: true)

        grailsChange {
            change {
                /**
                 *  update experiment measures with stats_modifier and result type
                 */
                String allMeasures = """SELECT MEASURE_ID FROM EXPRMT_MEASURE"""
                sql.eachRow(allMeasures) { expt_measure_row ->
                    String resultTypeStatsMod = """SELECT RESULT_TYPE_ID, STATS_MODIFIER_ID FROM MEASURE WHERE MEASURE_ID=${expt_measure_row.MEASURE_ID}"""
                    sql.eachRow(resultTypeStatsMod) { measure_row ->
                        String updateStatement = """UPDATE EXPRMT_MEASURE SET RESULT_TYPE_ID=${measure_row.RESULT_TYPE_ID},STATS_MODIFIER_ID=${measure_row.STATS_MODIFIER_ID} WHERE EXPRMT_MEASURE_ID=${expt_measure_row.MEASURE_ID}"""
                        sql.executeUpdate(updateStatement)
                    }
                }
//                sql.execute("""ALTER TABLE EXPRMT_MEASURE ADD CONSTRAINT CK_EXPM_RESULT_TYPE_ID CHECK (RESULT_TYPE_ID IS NOT NULL)""")
            }
            //add not null constraint
        }


    }

    changeSet(author: "jasiedu", id: "iteration-033/02-create-assay-context-expt-measure", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }


       sqlFile(path: "iteration_033/02-create-assay-context-expt-measure.sql", stripComments: true)
//        What we would need to do is:
//
//        1) For each row in the old table (Assay_Context_Measure table)
//
//        -- Find all of the experiment measures with the given measure_id( from the experiment-measure table)
//        --- For each experiment_measure
//        insert the pair of Assay_Context_Id,Experiment_Measure_Id into the new join table (Assay_Context-Experiment_Measure)
        grailsChange {
            change {
                /**
                 *  update experiment measures with stats_modifier and result type
                 */
                String contextMeasures = """SELECT ASSAY_CONTEXT_ID,MEASURE_ID,MODIFIED_BY FROM Assay_Context_Measure"""
                sql.eachRow(contextMeasures) { assay_context_measure_row ->
                    //println(assay_context_measure_row.MEASURE_ID)

                    String experimentMeasures = """SELECT EXPRMT_MEASURE_ID FROM EXPRMT_MEASURE WHERE MEASURE_ID=${assay_context_measure_row.MEASURE_ID}"""

                   // println(experimentMeasures)
                    sql.eachRow(experimentMeasures) { experiment_measures_row ->

                        String insertStatement = """
                        INSERT INTO ASSAY_CTXT_EXP_MEASURE(ASSAY_CTXT_EXP_MEASURE_ID,ASSAY_CONTEXT_ID,EXPERIMENT_MEASURE_ID,MODIFIED_BY) VALUES (assay_ctxt_exp_measure_ID_SEQ.nextval,${assay_context_measure_row.ASSAY_CONTEXT_ID},${experiment_measures_row.EXPRMT_MEASURE_ID},'jasiedu')
                        """
                       // println(insertStatement)
                        sql.execute(insertStatement)
                    }
                }
            }
        }
    }
    changeSet(author: "jasiedu", id: "iteration-033/03-delete-measure-and-references", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_033/03-delete-measure-and-references.sql", stripComments: true)
    }
        //Code:
    // Remove AssayContextMeasure from AssayContext
    //Remove AssayContextMeasure from Measure
    //Drop AssayContextMeasure table
    //Remove Measure column from Assay
    //Remove Measure from ExperimentMeasure
    //Remove Measure from Result - transient
    //Drop Measure table

    //-Sql
    // DROP ASSAY_CONTEXT_MEASURE
    // Remove MEASURE_ID FROM EXPRMT_MEASURE
    //DROP MEASURE


}

