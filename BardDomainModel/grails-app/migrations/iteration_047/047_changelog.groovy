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

package iteration_047

databaseChangeLog = {

    changeSet(author: "ycruz", id: "iteration-047/00-add-notnull-to-resulttypeid-in-exprmtmeasure", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")
                sql.execute("alter table exprmt_measure modify result_type_id not null")
                println("Finished adding NOT NULL constraint to result_type_id column in table exprmt_measure")
            }
        }
    }

    changeSet(author: "ycruz", id: "iteration-047/01-add-Unique-index-to-ASSAY_CTXT_EXP_MEASURE", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('ycruz'); end;")

                String DUPLICATES_QUERY = """select experiment_measure_id, assay_context_id, count(*) from ASSAY_CTXT_EXP_MEASURE group by experiment_measure_id, assay_context_id having count(*) > 1"""
                String DUPLICATE_RECORDS = """select * from ASSAY_CTXT_EXP_MEASURE where experiment_measure_id = ? and  assay_context_id = ?"""
                String delete_duplicate = """delete from ASSAY_CTXT_EXP_MEASURE where ASSAY_CTXT_EXP_MEASURE_ID = ?"""

                def rows = sql.rows(DUPLICATES_QUERY)
                if(rows.size() > 0){
                    println("Delete duplicates ASSAY_CTXT_EXP_MEASURE. Size: " + rows.size())
                    for(row in rows){
                        def exp_measure_id = row.EXPERIMENT_MEASURE_ID
                        def assay_context_id = row.ASSAY_CONTEXT_ID
                        println("Duplicate EXPERIMENT_MEASURE_ID = ${exp_measure_id}, ASSAY_CONTEXT_ID = ${assay_context_id}")
                        def duplicateRows = sql.rows(DUPLICATE_RECORDS, [exp_measure_id, assay_context_id])
                        for(int i = duplicateRows.size(); i > 1; i--){
                            def index = i - 1
                            def dupRow = duplicateRows.get(index)
                            def ASSAY_CTXT_EXP_MEASURE_ID = dupRow.ASSAY_CTXT_EXP_MEASURE_ID
                            println("Deleting at index ${index}: ASSAY_CTXT_EXP_MEASURE_ID = ${ASSAY_CTXT_EXP_MEASURE_ID}")
                            sql.execute(delete_duplicate, [ASSAY_CTXT_EXP_MEASURE_ID])
                            println("Succesfully deleted duplicate ASSAY_CTXT_EXP_MEASURE with ASSAY_CTXT_EXP_MEASURE_ID = ${ASSAY_CTXT_EXP_MEASURE_ID}")
                        }
                    }
                }
                else{
                    println("No duplicates ASSAY_CTXT_EXP_MEASUREs found. Size: ") + rows.size()
                }

                sql.execute("CREATE UNIQUE INDEX ak_assay_ctxt_exp_measure ON assay_ctxt_exp_measure(experiment_measure_id, assay_context_id)")
                println("Finished adding Unique index to ASSAY_CTXT_EXP_MEASURE")
            }
        }
    }
}