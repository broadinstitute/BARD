--
-- This script creates a set of views for use by a Spotfire visualization that
-- will show the overall progress of the data QA process for each MLPCN center.
-- The intended audience are the center PIs, the NIH, and the people performing
-- the data QA.
--

-- TODO: Add cycle # and open/closed from updated CARS file sent by Ajay
create or replace view vw_dashbd_prod_project as
select c.project_uid,
       prj.project_id,
       prj.project_name,
       ppm.ml_numbers,
       c.assay_center,
       c.aid_count,
       prj.DATE_CREATED as load_date,
       qad.qa_complete_date,
       case when (prj.project_status = 'Approved') then 'QA_Complete'
            when (prj.project_id is not null) then 'Ready_for_Center_QA'
            else 'Waiting_for_Curation_and_Loading' end as status
from (select project_uid, listagg(ml_number, ', ') within group (order by ml_number) as ML_numbers
	  from   probe_project_map
	  group by project_uid) ppm,
	  -- subquery to get all project UIDs from CARS data
	 (select project_uid, 
	         decode(min(assay_center),'SRI Screening','SRI',
	                               'Vanderbilt Chemistry','Vanderbilt',
	                               'Hopkins Screening','Hopkins',min(assay_center)) as assay_center,
	         count(aid) as aid_count
      from cars
      where project_uid is not null
      and suspect is null
      group by project_uid) c,
      -- subquery to get link between project UID and BARD project info
     (select er.project_id, ltrim(er.ext_assay_ref, 'project_UID=') as project_UID, p.PROJECT_NAME, p.project_status, p.DATE_CREATED
	  from bard_cap_prod.external_reference er,
	       bard_cap_prod.project p
	  where er.EXTERNAL_SYSTEM_ID = 2 
	  and   er.PROJECT_ID is not null
	  and   er.project_id = p.project_id) prj,
	  -- subquery to get QA completion date from audit logs
	 (select p.project_id, max(audit_timestamp) as QA_Complete_Date
	  from bard_cap_prod.audit_column_log acl,
	       bard_cap_prod.audit_row_log arl,
	       bard_cap_prod.project p
	  where acl.audit_id = arl.audit_id
	  and arl.table_name = 'PROJECT'
	  and acl.column_name = 'PROJECT_STATUS'
	  and arl.table_owner = 'BARD_CAP_PROD'
	  and arl.action = 'UPDATE'
	  and arl.primary_key = p.project_id
	  and p.PROJECT_STATUS = 'Approved'
	  group by p.project_id) qad
where c.project_uid = ppm.project_uid(+)
and   c.project_uid = prj.project_uid(+)
and   prj.project_id = qad.project_id(+)
;


create or replace view vw_dashbd_prod_experiment as
select distinct c.aid,
       c.project_uid,
       decode(c.assay_center,'SRI Screening','SRI',
	                         'Vanderbilt Chemistry','Vanderbilt',
	                         'Hopkins Screening','Hopkins',c.assay_center) as assay_center,
	   e.EXPERIMENT_ID,
	   e.ASSAY_ID,
	   e.EXPERIMENT_NAME,
	   ld.load_date,
	   qad.qa_complete_date,
       case when (e.EXPERIMENT_STATUS = 'Approved') then 'QA_Complete'
            when (e.EXPERIMENT_ID is not null and ld.load_date is not null) then 'Ready_for_Center_QA'
            else 'Waiting_for_Curation_and_Loading' end as status	   
from cars c,
     (select distinct experiment_id, ltrim(ext_assay_ref, 'aid=') as aid
	  from bard_cap_prod.external_reference er
	  where external_system_id = 1
	  and experiment_id is not null) ea,
     bard_cap_prod.experiment e,
     (select arl.primary_key as experiment_id, min(audit_timestamp) as Load_Date
	  from bard_cap_prod.audit_column_log acl,
	       bard_cap_prod.audit_row_log arl
	  where acl.audit_id = arl.audit_id
	  and arl.table_name = 'EXPERIMENT'
	  and acl.column_name = 'READY_FOR_EXTRACTION'
	  and arl.table_owner = 'BARD_CAP_PROD'
	  and arl.action = 'UPDATE'
	  group by arl.primary_key) ld,
	 (select e.experiment_id, max(audit_timestamp) as QA_Complete_Date
	  from bard_cap_prod.audit_column_log acl,
	       bard_cap_prod.audit_row_log arl,
	       bard_cap_prod.experiment e
	  where acl.audit_id = arl.audit_id
	  and arl.table_name = 'EXPERIMENT'
	  and acl.column_name = 'EXPERIMENT_STATUS'
	  and arl.table_owner = 'BARD_CAP_PROD'
	  and arl.action = 'UPDATE'
	  and arl.primary_key = e.EXPERIMENT_ID
	  and e.EXPERIMENT_STATUS = 'Approved'
	  group by e.experiment_id) qad	  
where c.SUSPECT is null
and c.aid = ea.aid(+)
and ea.experiment_id = e.experiment_id(+)
and e.EXPERIMENT_ID = ld.experiment_id(+)
and e.EXPERIMENT_ID = qad.experiment_id(+)
order by status
;
