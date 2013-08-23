alter table assay_context_item add value_type varchar2(25);
alter table EXPRMT_CONTEXT_ITEM add value_type varchar2(25);
alter table PROJECT_CONTEXT_ITEM add value_type varchar2(25);
alter table RSLT_CONTEXT_ITEM add value_type varchar2(25);
alter table STEP_CONTEXT_ITEM add value_type varchar2(25);

update assay_context_item set value_type = case
  when qualifier is not null and value_num is not null then 'numeric'
  when value_min is not null and value_max is not null then 'range'
  when value_id is not null then 'element'
  when ext_value_id is not null then 'external ontology'
  when attribute_type = 'Free' then 'none'
  when value_display is not null and qualifier is null and value_id is null and ext_value_id is null and value_num is null and value_min is null and value_max is null then 'free text'
  else 'invalid' end;

-- fix missing = on items
update EXPRMT_CONTEXT_ITEM set qualifier = '= ' where qualifier is null and value_num is not null;

update EXPRMT_CONTEXT_ITEM set value_type = case
  when qualifier is not null and value_num is not null then 'numeric'
  when value_min is not null and value_max is not null then 'range'
  when value_id is not null then 'element'
  when ext_value_id is not null then 'external ontology'
  when value_display is not null and qualifier is null and value_id is null and ext_value_id is null and value_num is null and value_min is null and value_max is null then 'free text'
  else 'invalid' end;

update PROJECT_CONTEXT_ITEM set value_type = case
  when qualifier is not null and value_num is not null then 'numeric'
  when value_min is not null and value_max is not null then 'range'
  when value_id is not null then 'element'
  when ext_value_id is not null then 'external ontology'
  when value_display is not null and qualifier is null and value_id is null and ext_value_id is null and value_num is null and value_min is null and value_max is null then 'free text'
  else 'invalid' end;

update RSLT_CONTEXT_ITEM set value_type = case
  when qualifier is not null and value_num is not null then 'numeric'
  when value_min is not null and value_max is not null then 'range'
  when value_id is not null then 'element'
  when ext_value_id is not null then 'external ontology'
  when value_display is not null and qualifier is null and value_id is null and ext_value_id is null and value_num is null and value_min is null and value_max is null then 'free text'
  else 'invalid' end;

update STEP_CONTEXT_ITEM set value_type = case
  when qualifier is not null and value_num is not null then 'numeric'
  when value_min is not null and value_max is not null then 'range'
  when value_id is not null then 'element'
  when ext_value_id is not null then 'external ontology'
  when value_display is not null and qualifier is null and value_id is null and ext_value_id is null and value_num is null and value_min is null and value_max is null then 'free text'
  else 'invalid' end;

alter table assay_context_item add constraint ck_aci_value_type check (value_type in ('numeric', 'element', 'external ontology', 'free text', 'none', 'range'));
alter table EXPRMT_CONTEXT_ITEM add constraint ck_eci_value_type check (value_type in ('numeric', 'element', 'external ontology', 'free text', 'range'));
alter table PROJECT_CONTEXT_ITEM add constraint ck_pci_value_type check (value_type in ('numeric', 'element', 'external ontology', 'free text', 'range'));
alter table RSLT_CONTEXT_ITEM add constraint ck_rsltci_value_type check (value_type in ('numeric', 'element', 'external ontology', 'free text', 'range'));
alter table STEP_CONTEXT_ITEM add constraint ck_stepci_value_type check (value_type in ('numeric', 'element', 'external ontology', 'free text', 'range'));

