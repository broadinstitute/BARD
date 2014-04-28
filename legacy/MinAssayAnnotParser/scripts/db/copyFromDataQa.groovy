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

import groovy.sql.Sql

def dataqaSql = new Sql(createConnection("dataqa"))
dst = createDbTarget(args[0])

disableConstraints = """
begin
  for cur in (select fk.owner, fk.constraint_name , fk.table_name
      from user_constraints fk, user_constraints pk
           where fk.CONSTRAINT_TYPE = 'R' and
           fk.R_CONSTRAINT_NAME = pk.CONSTRAINT_NAME) loop
         execute immediate 'ALTER TABLE "'||cur.owner||'"."'||cur.table_name||'" MODIFY CONSTRAINT "'||cur.constraint_name||'" DISABLE';
    end loop;
end;
"""

enableConstraints = """
begin
  for cur in (select fk.owner, fk.constraint_name , fk.table_name
      from user_constraints fk, user_constraints pk
           where fk.CONSTRAINT_TYPE = 'R' and
            fk.R_CONSTRAINT_NAME = pk.CONSTRAINT_NAME) loop
         execute immediate 'ALTER TABLE "'||cur.owner||'"."'||cur.table_name||'" MODIFY CONSTRAINT "'||cur.constraint_name||'" ENABLE';
    end loop;
end;
"""

def assayPaths = [
            "ASSAY->ASSAY_CONTEXT.ASSAY_ID",
            "ASSAY_CONTEXT->ASSAY_CONTEXT_ITEM.ASSAY_CONTEXT_ID",
            "ASSAY->ASSAY_DOCUMENT.ASSAY_ID",
            "ASSAY->MEASURE.ASSAY_ID",
            "ASSAY_CONTEXT->ASSAY_CONTEXT_MEASURE.ASSAY_CONTEXT_ID",
            "ASSAY->EXPERIMENT.ASSAY_ID",
            "EXPERIMENT->EXPERIMENT_FILE.EXPERIMENT_ID",
            "EXPERIMENT->EXPRMT_CONTEXT.EXPERIMENT_ID",
            "EXPRMT_CONTEXT->EXPRMT_CONTEXT_ITEM.EXPRMT_CONTEXT_ID",
            "EXPERIMENT->EXTERNAL_REFERENCE.EXPERIMENT_ID",
            "EXPERIMENT->EXPRMT_MEASURE.EXPERIMENT_ID",
            "EXPERIMENT->RESULT.EXPERIMENT_ID",
            "RESULT->RESULT_HIERARCHY.RESULT_ID",
            "RESULT->RSLT_CONTEXT_ITEM.RESULT_ID",
	]

def projectPaths = [
         "PROJECT->PROJECT_EXPERIMENT.PROJECT_ID",
         "PROJECT->PROJECT_CONTEXT.PROJECT_ID",
         "PROJECT_CONTEXT->PROJECT_CONTEXT_ITEM.PROJECT_CONTEXT_ID",
         "PROJECT_EXPERIMENT->PRJCT_EXPRMT_CONTEXT.PROJECT_EXPERIMENT_ID",
         "PRJCT_EXPRMT_CONTEXT->PRJCT_EXPRMT_CNTXT_ITEM.PRJCT_EXPRMT_CONTEXT_ID",
         "PROJECT->PROJECT_DOCUMENT.PROJECT_ID",
	 "PROJECT_EXPERIMENT->PROJECT_STEP.NEXT_PROJECT_EXPERIMENT_ID",
         "PROJECT_STEP->STEP_CONTEXT.PROJECT_STEP_ID",
         "STEP_CONTEXT->STEP_CONTEXT_ITEM.STEP_CONTEXT_ID",
	 "PROJECT->EXTERNAL_REFERENCE.PROJECT_ID"
	]

def projectIds = dataqaSql.rows("select project_id from project").collect {it[0]}
def assayIds = dataqaSql.rows("select distinct assay_id from experiment order by assay_id").collect {it[0]}

src = createConnection("dataqa")
executeSql(dst, disableConstraints)
copyTree(src, dst, assayPaths, assayIds)
copyTree(src, dst, projectPaths, projectIds)
executeSql(dst, enableConstraints)
dst.close()

