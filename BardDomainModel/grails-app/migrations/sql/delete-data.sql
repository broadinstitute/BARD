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
delete from DICTIONARY_TREE;
delete from STATS_MODIFIER_TREE;
-- results
delete from RESULT_HIERARCHY;
delete from RSLT_CONTEXT_ITEM;
delete from RESULT;
delete from SUBSTANCE;
-- external systems and external references hae fk from projects and experiments
delete from EXTERNAL_REFERENCE;
delete from EXTERNAL_SYSTEM;
-- projects
delete from STEP_CONTEXT_ITEM;
delete from STEP_CONTEXT;
delete from PROJECT_STEP;
delete from PRJCT_EXPRMT_CONTEXT;
delete from PRJCT_EXPRMT_CONTEXT_ITEM;
delete from PROJECT_EXPERIMENT;
delete from PROJECT_DOCUMENT;
delete from PROJECT_CONTEXT_ITEM;
delete from PROJECT_CONTEXT;
delete from PROJECT;
-- experiments
delete from EXPRMT_CONTEXT_ITEM;
delete from EXPRMT_CONTEXT;
delete from EXPRMT_MEASURE;
delete from EXPERIMENT;
-- assays
delete from ASSAY_CONTEXT_MEASURE;
delete from MEASURE;
delete from ASSAY_CONTEXT_ITEM;
delete from ASSAY_CONTEXT;
delete from ASSAY_DOCUMENT;
delete from ASSAY;
-- dictionary
delete from ONTOLOGY_ITEM;
delete from ONTOLOGY;
delete from TREE_ROOT;
delete from UNIT_CONVERSION;
delete from ELEMENT_HIERARCHY;
delete from ELEMENT;
-- People
-- delete from FAVORITE;
-- delete from PERSON_ROLE;
-- delete from TEAM_MEMBER;
-- delete from PERSON;
-- delete from ROLE;
-- delete from TEAM;