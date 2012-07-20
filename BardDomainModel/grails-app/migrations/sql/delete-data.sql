--
-- Empty all tables of data
--
--delete from DATABASECHANGELOGLOCK;
--delete from DATABASECHANGELOG;
-- materialized views
delete from ASSAY_DESCRIPTOR;
delete from BIOLOGY_DESCRIPTOR;
delete from INSTANCE_DESCRIPTOR;
delete from RESULT_TYPE;
delete from UNIT;
delete from LABORATORY;
delete from STAGE;

-- data tables in correct order
-- results
delete from RESULT_HIERARCHY;
delete from RESULT_CONTEXT_ITEM;
delete from RESULT;
delete from SUBSTANCE;

-- experiments
delete from EXTERNAL_REFERENCE;
delete from PROJECT_EXPERIMENT;
delete from EXPERIMENT;
delete from PROJECT;

-- assays
delete from MEASURE;
delete from MEASURE_CONTEXT_ITEM;
delete from MEASURE_CONTEXT;
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
