--
-- Empty all tables of data
--
--delete from DATABASECHANGELOGLOCK;
--delete from DATABASECHANGELOG;
-- materialized views
delete from ASSAY_DESCRIPTOR_TREE;
delete from BIOLOGY_DESCRIPTOR_TREE;
delete from INSTANCE_DESCRIPTOR_TREE;
delete from RESULT_TYPE_TREE;
delete from UNIT_TREE;
delete from LABORATORY_TREE;
delete from STAGE_TREE;

-- data tables in correct order
-- results
delete from RESULT_HIERARCHY;
delete from RUN_CONTEXT_ITEM;
delete from RESULT;
delete from SUBSTANCE;

-- experiments
delete from EXTERNAL_REFERENCE;
delete from PROJECT_CONTEXT_ITEM;
delete from PROJECT_STEP;
delete from EXPERIMENT;
delete from PROJECT;

-- assays
delete from MEASURE;
delete from ASSAY_CONTEXT_ITEM;
delete from ASSAY_CONTEXT;
delete from ASSAY_DOCUMENT;
delete from ASSAY;

-- dictionary
delete from EXTERNAL_SYSTEM;
delete from UNIT_CONVERSION;
delete from ELEMENT_HIERARCHY;
delete from ONTOLOGY_ITEM;
delete from TREE_ROOT;
delete from ONTOLOGY;
delete from ELEMENT;
