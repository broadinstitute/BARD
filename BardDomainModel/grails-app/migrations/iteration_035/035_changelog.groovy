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

package iteration_035

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-035/01-remove-new-object-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/01-remove-new-object-role.sql", stripComments: true)
    }

    changeSet(author: "gwalzer", id: "iteration-035/02-drop-Replaced-By-Id-column-in-Element-table", dbms: "oracle", context: "standard") {
        dropColumn(columnName: "REPLACED_BY_ID", tableName: "ELEMENT")
    }


    changeSet(author: "ycruz", id: "iteration-035/04-update-contextitem-valuedisplay", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                                   bard_context.set_username('ycruz');
                                   END;
                                   """)
            }
        }
        sqlFile(path: "iteration_035/04-update-contextitem-valuedisplay.sql", stripComments: true, endDelimiter: ";")
    }

    changeSet(author: "jasiedu", id: "iteration-035/02-add-priority-to-experiment-measure", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/02-add-priority-to-experiment-measure.sql", stripComments: true)
    }

    changeSet(author: "jasiedu", id: "iteration-035/01-alter-expt-measure", dbms: "oracle", context: "standard") {

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
        sqlFile(path: "iteration_035/01-alter-expt-measure.sql", stripComments: true)

    }

    changeSet(author: "jasiedu", id: "iteration-035/02-update-expt-measure", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }


        grailsChange {
            change {
                /**
                 *  update experiment measures with stats_modifier and result type
                 */
                String allMeasures = """SELECT MEASURE_ID FROM EXPRMT_MEASURE"""
                sql.eachRow(allMeasures) { expt_measure_row ->
                    String resultTypeStatsMod = """SELECT RESULT_TYPE_ID, STATS_MODIFIER_ID FROM MEASURE WHERE MEASURE_ID=${expt_measure_row.MEASURE_ID}"""
                    sql.eachRow(resultTypeStatsMod) { measure_row ->
                        String updateStatement = """UPDATE EXPRMT_MEASURE SET RESULT_TYPE_ID=${measure_row.RESULT_TYPE_ID},STATS_MODIFIER_ID=${measure_row.STATS_MODIFIER_ID} WHERE MEASURE_ID=${expt_measure_row.MEASURE_ID}"""
                        sql.executeUpdate(updateStatement)
                    }
                }
            }
        }


    }

    changeSet(author: "jasiedu", id: "iteration-035/03-create-assay-context-expt-measure", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }


        sqlFile(path: "iteration_035/03-create-assay-context-expt-measure.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-035/04-update-assay-context-expt-measure", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
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
    changeSet(author: "jasiedu", id: "iteration-035/05-delete-measure-and-references", dbms: "oracle", context: "standard") {

        //Set the username in context
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_035/05-delete-measure-and-references.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-035/06-create-exp-panel", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('pmontgom');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_035/06-create-exp-panel.sql", stripComments: true)
   }

    changeSet(author: "pmontgom", id: "iteration-035/07-drop-old-constraint", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_035/07-drop-old-constraint.sql", stripComments: true)
    }

}
