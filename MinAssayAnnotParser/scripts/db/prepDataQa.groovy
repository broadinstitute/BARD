import groovy.sql.Sql

def datasetId = args[0]
dst = createDbTarget(args[1])
def outputDir = args(args[2])

dataqa = createConnection("dataqa")

def dstSql = new Sql(createConnection(args[1]))
// check for dblink
def inBcbDev = !(dstSql.firstRow("select count(*) from user_db_links where db_link = 'BARDDEV'")[0] > 0)
println "inBcbDev=${inBcbDev}"

if (!inBcbDev) {
	executeSql(dst,"""create or replace view result_map as select * from southern.result_map@barddev""")
} else {
	executeSql(dst,"""create or replace view result_map as select * from southern.result_map""")
}

def dataqaSql = new Sql(dataqa)

/////////////////////////////////////////////////////////////////////
// Write to files the ids in this dataset 
////////////////////////////////////////////////////////////////////

def adids = dataqaSql.rows("""select distinct e.assay_id
	from data_mig.external_reference@barddev r 
	join data_mig.experiment@barddev e on r.experiment_id = e.experiment_id
	join bard_data_qa_dashboard.vw_dataset_aid v on 'aid='||v.aid=r.ext_assay_ref
	where v.dataset_id=${datasetId}""").collect {it.ASSAY_ID}

def aids = dataqaSql.rows("""select v.aid
	from bard_data_qa_dashboard.vw_dataset_aid v 
	where v.dataset_id=${datasetId}""").collect {it.AID}

void writeToFile(filename, values) {
	println("Writing ${filename}")
	def w = new FileWriter(filename)
	for(v in values) {
		w.write("${v}\n")
	}
	w.close()
}

writeToFile("${outputDir}/dataset${datasetId}-aids.txt", aids)
writeToFile("${outputDir}/dataset${datasetId}-adids.txt", adids)

// drop triggers in destination
executeSql(dst,"""begin
  for my_rec in (select trigger_name from user_triggers) 
  loop
    execute immediate 'drop trigger '||my_rec.trigger_name;
  end loop;
end;
""")


//////////////////////////////////////////////////////////////////////
// Copy data for these aids from datamig to the dst
//////////////////////////////////////////////////////////////////////

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
            "EXPERIMENT->RESULT.EXPERIMENT_ID"
	]

datamig = createConnection("datamig")
executeSql(dst, disableConstraints)
copy(datamig, dst, ["EXTERNAL_SYSTEM", "ELEMENT", "ELEMENT_HIERARCHY", "TREE_ROOT"])
copyTree(datamig, dst, assayPaths, adids)
executeSql(dst, enableConstraints)

println ("Copied ${adids.size()} assays")

def cleanup="""
delete from step_context_item
/
delete from step_context
/
delete from project_step
/
delete from prjct_exprmt_cntxt_item
/
delete from prjct_exprmt_context
/
delete from project_experiment
/
delete from project_context_item
/
delete from project_context
/
delete from external_reference where project_id is not null and
experiment_id is null
/
delete from project_document
/
delete from project
/
delete from assay_context_item where modified_by <> 'BAO_1000'
/
delete from assay_context c where not exists ( select 1 from
assay_context_item i where i.assay_context_id = c.assay_context_id )
/
delete from exprmt_context_item where modified_by <> 'BAO_1000'
/
delete from exprmt_context c where not exists ( select 1 from
exprmt_context_item i where i.exprmt_context_id = c.exprmt_context_id
)
/
delete from exprmt_measure
/
delete from measure
/

-- renumber assay context items
declare 
  new_display_index number;
begin 
  for rec in (select assay_context_id from assay_context) loop
    new_display_index := 0;
    for irec in (select assay_context_item_id from assay_context_item where assay_context_id = rec.assay_context_id order by display_order ) loop
      update assay_context_item set display_order = new_display_index where assay_context_item_id = irec.assay_context_item_id;
      new_display_index := new_display_index + 1;
    end loop;
  end loop;
end;
/

-- renumber assay contexts
declare 
  new_display_index number;
begin 
  for rec in (select assay_id from assay) loop
    new_display_index := 0;
    for irec in (select assay_context_id from assay_context where assay_id = rec.assay_id order by display_order ) loop
      update assay_context set display_order = new_display_index where assay_context_id = irec.assay_context_id;
      new_display_index := new_display_index + 1;
    end loop;
  end loop;
end;
/

-- renumber experiment context items
declare 
  new_display_index number;
begin 
  for rec in (select exprmt_context_id from exprmt_context) loop
    new_display_index := 0;
    for irec in (select exprmt_context_item_id from exprmt_context_item where exprmt_context_id = rec.exprmt_context_id order by display_order ) loop
      update exprmt_context_item set display_order = new_display_index where exprmt_context_item_id = irec.exprmt_context_item_id;
      new_display_index := new_display_index + 1;
    end loop;
  end loop;
end;
/

-- update display order on experiment contexts
declare 
  new_display_index number;
begin 
  for rec in (select experiment_id from experiment) loop
    new_display_index := 0;
    for irec in (select exprmt_context_id from exprmt_context where experiment_id = rec.experiment_id order by display_order ) loop
      update exprmt_context set display_order = new_display_index where exprmt_context_id = irec.exprmt_context_id;
      new_display_index := new_display_index + 1;
    end loop;
  end loop;
end;
/
"""
for(cleanupSql in cleanup.split("\n/\n")){
	executeSql(dst, cleanupSql)
}
println("Finished cleaning up schema")


//Create local aliases in dataqa that reference the
//production sequences.  Since it all is within the same database, I
//think that should work.

if(!inBcbDev) {
executeSql(dst, """
begin
  for my_rec in (select sequence_name from seq)
  loop
    execute immediate 'drop sequence '||my_rec.sequence_name;
    execute immediate 'create synonym ' ||my_rec.sequence_name||' for
BARD_CAP_PROD.'||my_rec.sequence_name;
  end loop;
end;
""")
}

println("Recreated sequences as aliases to production schema")

for(chunk in new File("scripts/db/packages.sql").text.split("\n/\n")) {
	executeSql(dst, chunk)
}

executeSql(dst, """ begin
 manage_ontology.make_trees;
end;""")

dst.close()

