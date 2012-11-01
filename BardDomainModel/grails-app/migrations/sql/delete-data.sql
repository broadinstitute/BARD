--
-- Empty all tables of data
--
-- materialized views
delete from ASSAY_DESCRIPTOR_TREE;
delete from BIOLOGY_DESCRIPTOR_TREE;
delete from INSTANCE_DESCRIPTOR_TREE;
delete from RESULT_TYPE_TREE;
delete from UNIT_TREE;
delete from LABORATORY_TREE;
delete from STAGE_TREE;
-- results
delete from RESULT_HIERARCHY;
delete from RSLT_CONTEXT_ITEM;
delete from RESULT;
delete from SUBSTANCE;
-- experiments
delete from EXTERNAL_SYSTEM;
delete from EXTERNAL_REFERENCE;
delete from EXPRMT_CONTEXT;
delete from EXPRMT_CONTEXT_ITEM;
delete from EXPERIMENT;
-- projects
delete from STEP_CONTEXT_ITEM;
delete from STEP_CONTEXT;
delete from PROJECT_STEP;
delete from PROJECT_DOCUMENT;
delete from PROJECT_CONTEXT_ITEM;
delete from PROJECT_CONTEXT;
delete from PROJECT;
-- assays
delete from ASSAY_CONTEXT_MEASURE;
delete from MEASURE;
delete from ASSAY_CONTEXT_ITEM;
delete from ASSAY_CONTEXT;
delete from ASSAY_DOCUMENT;
delete from ASSAY;
-- dictionary
delete from UNIT_CONVERSION;
delete from ELEMENT_HIERARCHY;
delete from ONTOLOGY_ITEM;
delete from TREE_ROOT;
delete from ONTOLOGY;
delete from ELEMENT;
-- People
-- delete from FAVORITE;
-- delete from PERSON_ROLE;
-- delete from TEAM_MEMBER;
-- delete from PERSON;
-- delete from ROLE;
-- delete from TEAM;