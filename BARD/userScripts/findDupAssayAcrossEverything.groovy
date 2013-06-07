import bard.db.experiment.Experiment

createTmpTables = { -> 
    dropTablesSql = [
        "drop table tmp_fixed_context_items",
        "drop table tmp_named_unique_items",
        "drop table tmp_unique_item_to_id"
        ]

    createTablesSql = [
"""create table tmp_fixed_context_items as
select assay_context_item_id, attribute_id, nvl(qualifier, 'NULL') qualifier, 
nvl(ext_value_id, 'NULL') ext_value_id, 
nvl(value_display, 'NULL') value_display, 
nvl(value_num, -1) value_num, 
nvl(value_min, -1) value_min, 
nvl(value_max, -1) value_max
from assay_context_item i 
where attribute_Type = 'Fixed'""",

"""create table tmp_named_unique_items as
select rownum id, x.* from (
select 
attribute_id, 
qualifier, 
ext_value_id, 
value_display,
value_num, 
value_min, 
value_max 
from tmp_fixed_context_items i 
group by
attribute_id, 
qualifier, 
ext_value_id, 
value_display,
value_num, 
value_min, 
value_max ) x""",

"""create table tmp_unique_item_to_id as
select a.id, b.assay_context_item_id 
from tmp_named_unique_items a, tmp_fixed_context_items b
where
a.attribute_id = b.attribute_id and
a.qualifier = b.qualifier and 
a.ext_value_id = b.ext_value_id and 
a.value_display = b.value_display and
a.value_num = b.value_num and
a.value_min = b.value_min and
a.value_max = b.value_max"""
]

  Experiment.withSession { session ->
    for(stmt in dropTablesSql) {
    try {
//      println(stmt)
      session.createSQLQuery(stmt).executeUpdate()
    } catch(Exception ex) {
    }
    }

    for(stmt in createTablesSql) {
//      println(stmt)
      session.createSQLQuery(stmt).executeUpdate()
    }
  }
}

tanimoto = { a, b ->
  def difference = (float)((b-a)+(a-b)).size()
  
  return 1.0 - difference / ((a+b).size())
}


// starts here
createTmpTables()

// load items per assay id
import bard.db.experiment.Experiment
results = Experiment.withSession { session -> 
  return session.createSQLQuery("""
select ac.assay_id, i.id 
from tmp_unique_item_to_id i 
join assay_context_item aci on aci.assay_context_item_id = i.assay_context_item_id
join assay_context ac on aci.assay_context_id = ac.assay_context_id
""").setCacheable(false).list()
}
byAssayId = results.groupBy { (long)it[0] }
u = byAssayId.collectEntries { k, v -> [k, v.collect(new HashSet()){ it[1] }] }

// only select assays

capIds = []
import bard.db.experiment.Experiment
//Experiment.withSession { session -> 
//  capIds = session.createSQLQuery("""
//select distinct e.assay_id 
//from external_reference r 
//join experiment e on r.experiment_id = e.experiment_id
//join (select vpaj.aid from bard_data_qa_dashboard.vw_project_aid_join vpaj
//      join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
//      where vpaj.project_uid in (
//            select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=(
//                   select id from bard_data_qa_dashboard.dataset where name = '002'))
//      and ai.is_summary_aid = 'n') da on 'aid='||da.aid = r.ext_assay_ref
//  """).setCacheable(false).list().collect {it.longValue()}
//}
capIds.addAll(byAssayId.keySet())
println(capIds)

// find assay ids where score is 1.0
matches = []
comparison = []
for(a in capIds) {
  for(b in capIds) { 
    if(u.containsKey(a) && u.containsKey(b) && a < b && u[a].size() == u[b].size() && u[a].size() > 5 && u[b].size() > 5) {
      comparison << [a, b, tanimoto(u[a], u[b]) ]
      if( tanimoto(u[a], u[b]) > (1.0 - 1e-7) ) {
          matches << [a,b]
      }
    }
  }
}

// find singletons
nonSingletons = matches.collectMany(new HashSet()) { it }
singletons = (capIds as Set) - nonSingletons

// collapse matching pairs into sets of assays which are the same
owningSet = [:]
addToSet = { a, b -> 
    if(owningSet.containsKey(a)) {
        if(owningSet.containsKey(b)) {
            owningSet[a].addAll(owningSet[b])
            owningSet[b] = owningSet[a]
        } else {
            owningSet[a].add(b)
            owningSet[b] = owningSet[a]
        }
    } else if(owningSet.containsKey(b)) {
        owningSet[a] = owningSet[b]
        owningSet[b].add(a)
    } else {
        def s = [a,b] as Set
        owningSet[a] = s
        owningSet[b] = s
    }
}
for(match in matches) {
    addToSet(match[0], match[1])
}
uniqueSets = new HashSet(owningSet.values())

w = new FileWriter("to-merge.txt")
for( x in uniqueSets ) {
    w.write(x.join(",")+"\n")
}
w.close()

// print the various groups
import bard.db.registration.Assay


w = new FileWriter("to-merge.csv")
w.write("group,aid,adid,name\n")
groupCount = 1
for (x in (uniqueSets + singletons) ) {
  groupCount += 1
  for (y in x) {
    a = Assay.get(y)
    pAid = (a.experiments.collectMany { it.externalReferences }).collect { it.extAssayRef.replace("aid=","") }
    pAid = pAid.size() == 0 ? "" : pAid[0]
    w.write("${groupCount},${pAid},${a.id},\"${a.assayName}\"\n")
  }
}
w.close()