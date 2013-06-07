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

