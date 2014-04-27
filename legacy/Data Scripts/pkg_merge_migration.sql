--
-- PACKAGE: MERGE_MIGRATION
--

create or replace package Merge_Migration
as

    type r_cursor is ref cursor;

-- primary entry points ----------------------------------------------------------
    procedure cleanout_data_mig
        (av_table_name in varchar2,
         an_id    in  number default null,
         av_src_schema  in varchar2 default null);

    procedure merge_migrate
        (av_source_schema   in varchar2,
         an_assay_id    in number default null,
         ab_load_ref_data   in  boolean default true);

    PROCEDURE Generate_Migration_stats
        (avi_refresh IN VARCHAR2 DEFAULT 'Increment');

-- internal house-keeping ------------------------------------------------------
    procedure log_error
        (an_errnum   in  number,
         av_errmsg  in varchar2,
         av_location    in varchar2,
         av_comment in varchar2 default null);

    procedure log_statement
        (av_table   IN  varchar2,
         an_identifier  in number,
         av_action      in varchar2,
         av_statement   IN varchar2);

    PROCEDURE open_LOCAL_cursor
        (av_modified_by   in  varchar2,
        av_table_name  in  varchar2,
        an_identifier  in number,
        aco_cursor in out r_cursor);

-- manage the identity_mapping table ---------------------------------------------
    procedure save_mapping
        (av_src_schema   in varchar2,
         av_table_name  in  varchar2,
         an_src_id   in  number,
         an_trgt_id   in  number);

    procedure get_mapping_id
        (av_src_schema   in varchar2,
         av_table_name  in  varchar2,
         an_src_id   in  number,
         ano_trgt_id   out  number);
------------------------------------------------------------------------------------

end Merge_Migration;
/


create or replace package body Merge_Migration
as
    ------package constants and variables
    -- pv_src_schema    varchar2(31) :=  user;
    -- for testing use this.  in general this should be run from the sandbox schema
    pv_src_schema    varchar2(31) :=  'bard_qa';
    --pv_src_schema    varchar2(31) :=  lower(user);
    --type r_cursor is ref cursor;
    pb_logging  boolean := true;

-- these recorsds make the column order indepenent between the source and target system
TYPE R_assay IS RECORD (
		ASSAY_ID	ASSAY.ASSAY_ID%type,
		ASSAY_STATUS	ASSAY.ASSAY_STATUS%type,
		ASSAY_SHORT_NAME	ASSAY.ASSAY_SHORT_NAME%type,
		ASSAY_NAME	ASSAY.ASSAY_NAME%type,
		ASSAY_VERSION	ASSAY.ASSAY_VERSION%type,
		ASSAY_TYPE	ASSAY.ASSAY_TYPE%type,
		DESIGNED_BY	ASSAY.DESIGNED_BY%type,
		READY_FOR_EXTRACTION	ASSAY.READY_FOR_EXTRACTION%type,
		VERSION	ASSAY.VERSION%type,
		DATE_CREATED	ASSAY.DATE_CREATED%type,
		LAST_UPDATED	ASSAY.LAST_UPDATED%type,
		MODIFIED_BY	ASSAY.MODIFIED_BY%type
		);
TYPE R_assay_context  IS RECORD (
    ASSAY_CONTEXT_ID	ASSAY_CONTEXT.ASSAY_CONTEXT_ID%type,
		ASSAY_ID	ASSAY_CONTEXT.ASSAY_ID%type,
		CONTEXT_NAME	ASSAY_CONTEXT.CONTEXT_NAME%type,
    CONTEXT_GROUP ASSAY_CONTEXT.CONTEXT_GROUP%type,
    DISPLAY_ORDER ASSAY_CONTEXT.DISPLAY_ORDER%type,
		VERSION	ASSAY_CONTEXT.VERSION%type,
		DATE_CREATED	ASSAY_CONTEXT.DATE_CREATED%type,
		LAST_UPDATED	ASSAY_CONTEXT.LAST_UPDATED%type,
		MODIFIED_BY	ASSAY_CONTEXT.MODIFIED_BY%type
		);
TYPE R_assay_context_item  IS RECORD (
		ASSAY_CONTEXT_ITEM_ID	ASSAY_CONTEXT_ITEM.ASSAY_CONTEXT_ITEM_ID%type,
		DISPLAY_ORDER	ASSAY_CONTEXT_ITEM.DISPLAY_ORDER%type,
		ASSAY_CONTEXT_ID	ASSAY_CONTEXT_ITEM.ASSAY_CONTEXT_ID%type,
		ATTRIBUTE_TYPE	ASSAY_CONTEXT_ITEM.ATTRIBUTE_TYPE%type,
		ATTRIBUTE_ID	ASSAY_CONTEXT_ITEM.ATTRIBUTE_ID%type,
		QUALIFIER	ASSAY_CONTEXT_ITEM.QUALIFIER%type,
		VALUE_ID	ASSAY_CONTEXT_ITEM.VALUE_ID%type,
		EXT_VALUE_ID	ASSAY_CONTEXT_ITEM.EXT_VALUE_ID%type,
		VALUE_DISPLAY	ASSAY_CONTEXT_ITEM.VALUE_DISPLAY%type,
		VALUE_NUM	ASSAY_CONTEXT_ITEM.VALUE_NUM%type,
		VALUE_MIN	ASSAY_CONTEXT_ITEM.VALUE_MIN%type,
		VALUE_MAX	ASSAY_CONTEXT_ITEM.VALUE_MAX%type,
		VERSION	ASSAY_CONTEXT_ITEM.VERSION%type,
		DATE_CREATED	ASSAY_CONTEXT_ITEM.DATE_CREATED%type,
		LAST_UPDATED	ASSAY_CONTEXT_ITEM.LAST_UPDATED%type,
		MODIFIED_BY	ASSAY_CONTEXT_ITEM.MODIFIED_BY%type
		);
TYPE R_assay_context_measure  IS RECORD (
		ASSAY_CONTEXT_MEASURE_ID	ASSAY_CONTEXT_MEASURE.ASSAY_CONTEXT_MEASURE_ID%type,
		MEASURE_ID	ASSAY_CONTEXT_MEASURE.MEASURE_ID%type,
    ASSAY_CONTEXT_ID	ASSAY_CONTEXT_MEASURE.ASSAY_CONTEXT_ID%type,
		VERSION	ASSAY_CONTEXT_MEASURE.VERSION%type,
		DATE_CREATED	ASSAY_CONTEXT_MEASURE.DATE_CREATED%type,
		LAST_UPDATED	ASSAY_CONTEXT_MEASURE.LAST_UPDATED%type,
		MODIFIED_BY	ASSAY_CONTEXT_MEASURE.MODIFIED_BY%type
		);
TYPE R_assay_document  IS RECORD (
		ASSAY_DOCUMENT_ID	ASSAY_DOCUMENT.ASSAY_DOCUMENT_ID%type,
		ASSAY_ID	ASSAY_DOCUMENT.ASSAY_ID%type,
		DOCUMENT_NAME	ASSAY_DOCUMENT.DOCUMENT_NAME%type,
		DOCUMENT_TYPE	ASSAY_DOCUMENT.DOCUMENT_TYPE%type,
		DOCUMENT_CONTENT	ASSAY_DOCUMENT.DOCUMENT_CONTENT%type,
		VERSION	ASSAY_DOCUMENT.VERSION%type,
		DATE_CREATED	ASSAY_DOCUMENT.DATE_CREATED%type,
		LAST_UPDATED	ASSAY_DOCUMENT.LAST_UPDATED%type,
		MODIFIED_BY	ASSAY_DOCUMENT.MODIFIED_BY%type
		);
TYPE R_element IS RECORD (
		ELEMENT_ID	ELEMENT.ELEMENT_ID%type,
		ELEMENT_STATUS	ELEMENT.ELEMENT_STATUS%type,
		LABEL	ELEMENT.LABEL%type,
		DESCRIPTION	ELEMENT.DESCRIPTION%type,
		ABBREVIATION	ELEMENT.ABBREVIATION%type,
		SYNONYMS	ELEMENT.SYNONYMS%type,
		UNIT_ID	ELEMENT.UNIT_ID%type,
		BARD_URI	ELEMENT.BARD_URI%type,
		EXTERNAL_URL	ELEMENT.EXTERNAL_URL%type,
		READY_FOR_EXTRACTION	ELEMENT.READY_FOR_EXTRACTION%type,
		VERSION	ELEMENT.VERSION%type,
		DATE_CREATED	ELEMENT.DATE_CREATED%type,
		LAST_UPDATED	ELEMENT.LAST_UPDATED%type,
		MODIFIED_BY	ELEMENT.MODIFIED_BY%type
		);
TYPE R_element_hierarchy IS RECORD (
		ELEMENT_HIERARCHY_ID	ELEMENT_HIERARCHY.ELEMENT_HIERARCHY_ID%type,
		PARENT_ELEMENT_ID	ELEMENT_HIERARCHY.PARENT_ELEMENT_ID%type,
		CHILD_ELEMENT_ID	ELEMENT_HIERARCHY.CHILD_ELEMENT_ID%type,
		RELATIONSHIP_TYPE	ELEMENT_HIERARCHY.RELATIONSHIP_TYPE%type,
		VERSION	ELEMENT_HIERARCHY.VERSION%type,
		DATE_CREATED	ELEMENT_HIERARCHY.DATE_CREATED%type,
		LAST_UPDATED	ELEMENT_HIERARCHY.LAST_UPDATED%type,
		MODIFIED_BY	ELEMENT_HIERARCHY.MODIFIED_BY%type
		);
TYPE R_experiment IS RECORD (
		EXPERIMENT_ID	EXPERIMENT.EXPERIMENT_ID%type,
		EXPERIMENT_NAME	EXPERIMENT.EXPERIMENT_NAME%type,
		EXPERIMENT_STATUS	EXPERIMENT.EXPERIMENT_STATUS%type,
		READY_FOR_EXTRACTION	EXPERIMENT.READY_FOR_EXTRACTION%type,
		ASSAY_ID	EXPERIMENT.ASSAY_ID%type,
		RUN_DATE_FROM	EXPERIMENT.RUN_DATE_FROM%type,
		RUN_DATE_TO	EXPERIMENT.RUN_DATE_TO%type,
		HOLD_UNTIL_DATE	EXPERIMENT.HOLD_UNTIL_DATE%type,
		DESCRIPTION	EXPERIMENT.DESCRIPTION%type,
		VERSION	EXPERIMENT.VERSION%type,
		DATE_CREATED	EXPERIMENT.DATE_CREATED%type,
		LAST_UPDATED	EXPERIMENT.LAST_UPDATED%type,
		MODIFIED_BY	EXPERIMENT.MODIFIED_BY%type
		);
TYPE R_exprmt_context  IS RECORD (
    EXPRMT_CONTEXT_ID	EXPRMT_CONTEXT.EXPRMT_CONTEXT_ID%type,
		EXPERIMENT_ID	EXPRMT_CONTEXT.EXPERIMENT_ID%type,
		CONTEXT_NAME	EXPRMT_CONTEXT.CONTEXT_NAME%type,
    CONTEXT_GROUP EXPRMT_CONTEXT.CONTEXT_GROUP%type,
    DISPLAY_ORDER EXPRMT_CONTEXT.DISPLAY_ORDER%type,
		VERSION	EXPRMT_CONTEXT.VERSION%type,
		DATE_CREATED	EXPRMT_CONTEXT.DATE_CREATED%type,
		LAST_UPDATED	EXPRMT_CONTEXT.LAST_UPDATED%type,
		MODIFIED_BY	EXPRMT_CONTEXT.MODIFIED_BY%type
		);
TYPE r_exprmt_context_item IS RECORD (
		EXPRMT_CONTEXT_ITEM_ID	EXPRMT_CONTEXT_ITEM.EXPRMT_CONTEXT_ITEM_ID%type,
		EXPRMT_CONTEXT_ID	EXPRMT_CONTEXT_ITEM.EXPRMT_CONTEXT_ID%type,
		DISPLAY_ORDER	EXPRMT_CONTEXT_ITEM.DISPLAY_ORDER%type,
		ATTRIBUTE_ID	EXPRMT_CONTEXT_ITEM.ATTRIBUTE_ID%type,
		VALUE_ID	EXPRMT_CONTEXT_ITEM.VALUE_ID%type,
		EXT_VALUE_ID	EXPRMT_CONTEXT_ITEM.EXT_VALUE_ID%type,
		QUALIFIER	EXPRMT_CONTEXT_ITEM.QUALIFIER%type,
		VALUE_NUM	EXPRMT_CONTEXT_ITEM.VALUE_NUM%type,
		VALUE_MIN	EXPRMT_CONTEXT_ITEM.VALUE_MIN%type,
		VALUE_MAX	EXPRMT_CONTEXT_ITEM.VALUE_MAX%type,
		VALUE_DISPLAY	EXPRMT_CONTEXT_ITEM.VALUE_DISPLAY%type,
		VERSION	EXPRMT_CONTEXT_ITEM.VERSION%type,
		DATE_CREATED	EXPRMT_CONTEXT_ITEM.DATE_CREATED%type,
		LAST_UPDATED	EXPRMT_CONTEXT_ITEM.LAST_UPDATED%type,
		MODIFIED_BY	EXPRMT_CONTEXT_ITEM.MODIFIED_BY%type
		);
TYPE R_exprmt_measure  IS RECORD (
		EXPRMT_MEASURE_ID	exprmt_measure.EXPRMT_MEASURE_ID%type,
		MEASURE_ID	exprmt_measure.MEASURE_ID%type,
		PARENT_EXPRMT_MEASURE_ID	exprmt_measure.PARENT_EXPRMT_MEASURE_ID%type,
    EXPERIMENT_ID	exprmt_measure.EXPERIMENT_ID%type,
		VERSION	exprmt_measure.VERSION%type,
		DATE_CREATED	exprmt_measure.DATE_CREATED%type,
		LAST_UPDATED	exprmt_measure.LAST_UPDATED%type,
		MODIFIED_BY	exprmt_measure.MODIFIED_BY%type
		);
TYPE R_external_referencE_aid IS RECORD (
		EXT_ASSAY_REF	EXTERNAL_REFERENCE.EXT_ASSAY_REF%TYPE
    );
TYPE R_external_reference IS RECORD (
		EXTERNAL_REFERENCE_ID	EXTERNAL_REFERENCE.EXTERNAL_REFERENCE_ID%type,
		EXTERNAL_SYSTEM_ID	EXTERNAL_REFERENCE.EXTERNAL_SYSTEM_ID%type,
		EXPERIMENT_ID	EXTERNAL_REFERENCE.EXPERIMENT_ID%type,
		PROJECT_ID	EXTERNAL_REFERENCE.PROJECT_ID%type,
		EXT_ASSAY_REF	EXTERNAL_REFERENCE.EXT_ASSAY_REF%type,
		VERSION	EXTERNAL_REFERENCE.VERSION%type,
		DATE_CREATED	EXTERNAL_REFERENCE.DATE_CREATED%type,
		LAST_UPDATED	EXTERNAL_REFERENCE.LAST_UPDATED%type,
		MODIFIED_BY	EXTERNAL_REFERENCE.MODIFIED_BY%type
		);
TYPE R_external_system IS RECORD (
		EXTERNAL_SYSTEM_ID	EXTERNAL_SYSTEM.EXTERNAL_SYSTEM_ID%type,
		SYSTEM_NAME	EXTERNAL_SYSTEM.SYSTEM_NAME%type,
		OWNER	EXTERNAL_SYSTEM.OWNER%type,
		SYSTEM_URL	EXTERNAL_SYSTEM.SYSTEM_URL%type,
		VERSION	EXTERNAL_SYSTEM.VERSION%type,
		DATE_CREATED	EXTERNAL_SYSTEM.DATE_CREATED%type,
		LAST_UPDATED	EXTERNAL_SYSTEM.LAST_UPDATED%type,
		MODIFIED_BY	EXTERNAL_SYSTEM.MODIFIED_BY%type
		);
TYPE R_measure IS RECORD (
		MEASURE_ID	MEASURE.MEASURE_ID%type,
		ASSAY_ID	MEASURE.ASSAY_ID%type,
		PARENT_MEASURE_ID	MEASURE.PARENT_MEASURE_ID%type,
		RESULT_TYPE_ID	MEASURE.RESULT_TYPE_ID%type,
		STATS_MODIFIER_ID	MEASURE.STATS_MODIFIER_ID%type,
		ENTRY_UNIT_ID	MEASURE.ENTRY_UNIT_ID%type,
		VERSION	MEASURE.VERSION%type,
		DATE_CREATED	MEASURE.DATE_CREATED%type,
		LAST_UPDATED	MEASURE.LAST_UPDATED%type,
		MODIFIED_BY	MEASURE.MODIFIED_BY%type
		);
TYPE R_ontology IS RECORD (
		ONTOLOGY_ID	ONTOLOGY.ONTOLOGY_ID%type,
		ONTOLOGY_NAME	ONTOLOGY.ONTOLOGY_NAME%type,
		ABBREVIATION	ONTOLOGY.ABBREVIATION%type,
		SYSTEM_URL	ONTOLOGY.SYSTEM_URL%type,
		VERSION	ONTOLOGY.VERSION%type,
		DATE_CREATED	ONTOLOGY.DATE_CREATED%type,
		LAST_UPDATED	ONTOLOGY.LAST_UPDATED%type,
		MODIFIED_BY	ONTOLOGY.MODIFIED_BY%type
		);
TYPE R_ontology_item IS RECORD (
		ONTOLOGY_ITEM_ID	ONTOLOGY_ITEM.ONTOLOGY_ITEM_ID%type,
		ONTOLOGY_ID	ONTOLOGY_ITEM.ONTOLOGY_ID%type,
		ELEMENT_ID	ONTOLOGY_ITEM.ELEMENT_ID%type,
		ITEM_REFERENCE	ONTOLOGY_ITEM.ITEM_REFERENCE%type,
		VERSION	ONTOLOGY_ITEM.VERSION%type,
		DATE_CREATED	ONTOLOGY_ITEM.DATE_CREATED%type,
		LAST_UPDATED	ONTOLOGY_ITEM.LAST_UPDATED%type,
		MODIFIED_BY	ONTOLOGY_ITEM.MODIFIED_BY%type
		);
TYPE R_project IS RECORD (
		PROJECT_ID	PROJECT.PROJECT_ID%type,
		PROJECT_NAME	PROJECT.PROJECT_NAME%type,
		GROUP_TYPE	PROJECT.GROUP_TYPE%type,
		DESCRIPTION	PROJECT.DESCRIPTION%type,
		READY_FOR_EXTRACTION	PROJECT.READY_FOR_EXTRACTION%type,
		VERSION	PROJECT.VERSION%type,
		DATE_CREATED	PROJECT.DATE_CREATED%type,
		LAST_UPDATED	PROJECT.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT.MODIFIED_BY%type
		);
TYPE R_PROJECT_context  IS RECORD (
    PROJECT_context_ID	PROJECT_context.PROJECT_context_ID%type,
		PROJECT_ID	PROJECT_context.PROJECT_ID%type,
		CONTEXT_NAME	PROJECT_context.CONTEXT_NAME%type,
    CONTEXT_GROUP PROJECT_context.CONTEXT_GROUP%type,
    DISPLAY_ORDER PROJECT_context.DISPLAY_ORDER%type,
		VERSION	PROJECT_context.VERSION%type,
		DATE_CREATED	PROJECT_context.DATE_CREATED%type,
		LAST_UPDATED	PROJECT_context.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT_context.MODIFIED_BY%type
		);
TYPE R_project_context_item IS RECORD (
		PROJECT_CONTEXT_ITEM_ID	PROJECT_CONTEXT_ITEM.PROJECT_CONTEXT_ITEM_ID%type,
		PROJECT_CONTEXT_ID	PROJECT_CONTEXT_ITEM.PROJECT_CONTEXT_ID%type,
		DISPLAY_ORDER	PROJECT_CONTEXT_ITEM.DISPLAY_ORDER%type,
		ATTRIBUTE_ID	PROJECT_CONTEXT_ITEM.ATTRIBUTE_ID%type,
		QUALIFIER	PROJECT_CONTEXT_ITEM.QUALIFIER%type,
		VALUE_ID	PROJECT_CONTEXT_ITEM.VALUE_ID%type,
		EXT_VALUE_ID	PROJECT_CONTEXT_ITEM.EXT_VALUE_ID%type,
		VALUE_DISPLAY	PROJECT_CONTEXT_ITEM.VALUE_DISPLAY%type,
		VALUE_NUM	PROJECT_CONTEXT_ITEM.VALUE_NUM%type,
		VALUE_MIN	PROJECT_CONTEXT_ITEM.VALUE_MIN%type,
		VALUE_MAX	PROJECT_CONTEXT_ITEM.VALUE_MAX%type,
		VERSION	PROJECT_CONTEXT_ITEM.VERSION%type,
		DATE_CREATED	PROJECT_CONTEXT_ITEM.DATE_CREATED%type,
		LAST_UPDATED	PROJECT_CONTEXT_ITEM.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT_CONTEXT_ITEM.MODIFIED_BY%type
		);
TYPE R_project_document IS RECORD (
		PROJECT_DOCUMENT_ID	PROJECT_DOCUMENT.PROJECT_DOCUMENT_ID%type,
		PROJECT_ID	PROJECT_DOCUMENT.PROJECT_ID%type,
		DOCUMENT_NAME	PROJECT_DOCUMENT.DOCUMENT_NAME%type,
		DOCUMENT_TYPE	PROJECT_DOCUMENT.DOCUMENT_TYPE%type,
		DOCUMENT_CONTENT	PROJECT_DOCUMENT.DOCUMENT_CONTENT%type,
		VERSION	PROJECT_DOCUMENT.VERSION%type,
		DATE_CREATED	PROJECT_DOCUMENT.DATE_CREATED%type,
		LAST_UPDATED	PROJECT_DOCUMENT.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT_DOCUMENT.MODIFIED_BY%type
		);
TYPE R_project_EXPERIMENT IS RECORD (
		PROJECT_EXPERIMENT_ID	PROJECT_EXPERIMENT.PROJECT_EXPERIMENT_ID%type,
		PROJECT_ID	PROJECT_EXPERIMENT.PROJECT_ID%type,
		experiment_ID	PROJECT_EXPERIMENT.experiment_ID%type,
		STAGE_ID	PROJECT_EXPERIMENT.STAGE_ID%type,
		VERSION	PROJECT_EXPERIMENT.VERSION%type,
		DATE_CREATED	PROJECT_EXPERIMENT.DATE_CREATED%type,
		LAST_UPDATED	PROJECT_EXPERIMENT.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT_EXPERIMENT.MODIFIED_BY%type
		);
TYPE R_prjct_exprmt_context  IS RECORD (
    prjct_exprmt_CONTEXT_ID	prjct_exprmt_context.prjct_exprmt_CONTEXT_ID%type,
		PROJECT_EXPERIMENT_ID	prjct_exprmt_context.PROJECT_EXPERIMENT_ID%type,
		CONTEXT_NAME	prjct_exprmt_context.CONTEXT_NAME%type,
    CONTEXT_GROUP prjct_exprmt_context.CONTEXT_GROUP%type,
    DISPLAY_ORDER prjct_exprmt_context.DISPLAY_ORDER%type,
		VERSION	prjct_exprmt_context.VERSION%type,
		DATE_CREATED	prjct_exprmt_context.DATE_CREATED%type,
		LAST_UPDATED	prjct_exprmt_context.LAST_UPDATED%type,
		MODIFIED_BY	prjct_exprmt_context.MODIFIED_BY%type
		);
TYPE R_prjct_exprmt_context_item  IS RECORD (
		prjct_exprmt_context_ITEM_ID	prjct_exprmt_context_item.prjct_exprmt_context_ITEM_ID%type,
		prjct_exprmt_context_ID	prjct_exprmt_context_item.prjct_exprmt_context_ID%type,
		DISPLAY_ORDER	prjct_exprmt_context_item.DISPLAY_ORDER%type,
		ATTRIBUTE_ID	prjct_exprmt_context_item.ATTRIBUTE_ID%type,
		VALUE_ID	prjct_exprmt_context_item.VALUE_ID%type,
		EXT_VALUE_ID	prjct_exprmt_context_item.EXT_VALUE_ID%type,
		QUALIFIER	prjct_exprmt_context_item.QUALIFIER%type,
		VALUE_NUM	prjct_exprmt_context_item.VALUE_NUM%type,
		VALUE_MIN	prjct_exprmt_context_item.VALUE_MIN%type,
		VALUE_MAX	prjct_exprmt_context_item.VALUE_MAX%type,
		VALUE_DISPLAY	prjct_exprmt_context_item.VALUE_DISPLAY%type,
		VERSION	prjct_exprmt_context_item.VERSION%type,
		DATE_CREATED	prjct_exprmt_context_item.DATE_CREATED%type,
		LAST_UPDATED	prjct_exprmt_context_item.LAST_UPDATED%type,
		MODIFIED_BY	prjct_exprmt_context_item.MODIFIED_BY%type
		);
TYPE R_project_step IS RECORD (
		PROJECT_STEP_ID	PROJECT_STEP.PROJECT_STEP_ID%type,
		next_PROJECT_experiment_ID	PROJECT_STEP.next_PROJECT_experiment_ID%type,
		prev_PROJECT_experiment_ID	PROJECT_STEP.prev_PROJECT_experiment_ID%type,
		edge_name	PROJECT_STEP.edge_name%type,
		VERSION	PROJECT_STEP.VERSION%type,
		DATE_CREATED	PROJECT_STEP.DATE_CREATED%type,
		LAST_UPDATED	PROJECT_STEP.LAST_UPDATED%type,
		MODIFIED_BY	PROJECT_STEP.MODIFIED_BY%type
		);
TYPE R_result IS RECORD (
		RESULT_ID	RESULT.RESULT_ID%type,
		RESULT_STATUS	RESULT.RESULT_STATUS%type,
		READY_FOR_EXTRACTION	RESULT.READY_FOR_EXTRACTION%type,
		EXPERIMENT_ID	RESULT.EXPERIMENT_ID%type,
		SUBSTANCE_ID	RESULT.SUBSTANCE_ID%type,
		RESULT_TYPE_ID	RESULT.RESULT_TYPE_ID%type,
		STATS_MODIFIER_ID	RESULT.STATS_MODIFIER_ID%type,
		REPLICATE_NO	RESULT.REPLICATE_NO%type,
		VALUE_DISPLAY	RESULT.VALUE_DISPLAY%type,
		VALUE_NUM	RESULT.VALUE_NUM%type,
		VALUE_MIN	RESULT.VALUE_MIN%type,
		VALUE_MAX	RESULT.VALUE_MAX%type,
		QUALIFIER	RESULT.QUALIFIER%type,
		VERSION	RESULT.VERSION%type,
		DATE_CREATED	RESULT.DATE_CREATED%type,
		LAST_UPDATED	RESULT.LAST_UPDATED%type,
		MODIFIED_BY	RESULT.MODIFIED_BY%type
		);
TYPE R_result_hierarchy IS RECORD (
		RESULT_ID	RESULT_HIERARCHY.RESULT_ID%type,
		PARENT_RESULT_ID	RESULT_HIERARCHY.PARENT_RESULT_ID%type,
		HIERARCHY_TYPE	RESULT_HIERARCHY.HIERARCHY_TYPE%type,
		VERSION	RESULT_HIERARCHY.VERSION%type,
		DATE_CREATED	RESULT_HIERARCHY.DATE_CREATED%type,
		LAST_UPDATED	RESULT_HIERARCHY.LAST_UPDATED%type,
		MODIFIED_BY	RESULT_HIERARCHY.MODIFIED_BY%type
		);
TYPE R_rslt_context_item IS RECORD (
		RSLT_CONTEXT_ITEM_ID	RSLT_CONTEXT_ITEM.RSLT_CONTEXT_ITEM_ID%type,
    RESULT_ID	RSLT_CONTEXT_ITEM.RESULT_ID%type,
		ATTRIBUTE_ID	RSLT_CONTEXT_ITEM.ATTRIBUTE_ID%type,
		DISPLAY_ORDER    RSLT_CONTEXT_ITEM.DISPLAY_ORDER%type,
		VALUE_ID	RSLT_CONTEXT_ITEM.VALUE_ID%type,
		EXT_VALUE_ID	RSLT_CONTEXT_ITEM.EXT_VALUE_ID%type,
		QUALIFIER	RSLT_CONTEXT_ITEM.QUALIFIER%type,
		VALUE_NUM	RSLT_CONTEXT_ITEM.VALUE_NUM%type,
		VALUE_MIN	RSLT_CONTEXT_ITEM.VALUE_MIN%type,
		VALUE_MAX	RSLT_CONTEXT_ITEM.VALUE_MAX%type,
		VALUE_DISPLAY	RSLT_CONTEXT_ITEM.VALUE_DISPLAY%type,
		VERSION	RSLT_CONTEXT_ITEM.VERSION%type,
		DATE_CREATED	RSLT_CONTEXT_ITEM.DATE_CREATED%type,
		LAST_UPDATED	RSLT_CONTEXT_ITEM.LAST_UPDATED%type,
		MODIFIED_BY	RSLT_CONTEXT_ITEM.MODIFIED_BY%type
		);
TYPE R_step_context  IS RECORD (
    STEP_CONTEXT_ID	step_context.STEP_CONTEXT_ID%type,
		PROJECT_STEP_ID	step_context.PROJECT_STEP_ID%type,
		CONTEXT_NAME	step_context.CONTEXT_NAME%type,
    CONTEXT_GROUP step_context.CONTEXT_GROUP%type,
    DISPLAY_ORDER step_context.DISPLAY_ORDER%type,
		VERSION	step_context.VERSION%type,
		DATE_CREATED	step_context.DATE_CREATED%type,
		LAST_UPDATED	step_context.LAST_UPDATED%type,
		MODIFIED_BY	step_context.MODIFIED_BY%type
		);
TYPE R_step_context_item  IS RECORD (
		step_context_ITEM_ID	step_context_item.step_context_ITEM_ID%type,
		step_context_ID	step_context_item.step_context_ID%type,
		DISPLAY_ORDER	step_context_item.DISPLAY_ORDER%type,
		ATTRIBUTE_ID	step_context_item.ATTRIBUTE_ID%type,
		VALUE_ID	step_context_item.VALUE_ID%type,
		EXT_VALUE_ID	step_context_item.EXT_VALUE_ID%type,
		QUALIFIER	step_context_item.QUALIFIER%type,
		VALUE_NUM	step_context_item.VALUE_NUM%type,
		VALUE_MIN	step_context_item.VALUE_MIN%type,
		VALUE_MAX	step_context_item.VALUE_MAX%type,
		VALUE_DISPLAY	step_context_item.VALUE_DISPLAY%type,
		VERSION	step_context_item.VERSION%type,
		DATE_CREATED	step_context_item.DATE_CREATED%type,
		LAST_UPDATED	step_context_item.LAST_UPDATED%type,
		MODIFIED_BY	step_context_item.MODIFIED_BY%type
		);
TYPE R_tree_root IS RECORD (
		TREE_ROOT_ID	TREE_ROOT.TREE_ROOT_ID%type,
		TREE_NAME	TREE_ROOT.TREE_NAME%type,
		ELEMENT_ID	TREE_ROOT.ELEMENT_ID%type,
		RELATIONSHIP_TYPE	TREE_ROOT.RELATIONSHIP_TYPE%type,
		VERSION	TREE_ROOT.VERSION%type,
		DATE_CREATED	TREE_ROOT.DATE_CREATED%type,
		LAST_UPDATED	TREE_ROOT.LAST_UPDATED%type,
		MODIFIED_BY	TREE_ROOT.MODIFIED_BY%type
		);
TYPE R_unit_conversion IS RECORD (
		FROM_UNIT_ID	UNIT_CONVERSION.FROM_UNIT_ID%type,
		TO_UNIT_ID	UNIT_CONVERSION.TO_UNIT_ID%type,
		MULTIPLIER	UNIT_CONVERSION.MULTIPLIER%type,
		OFFSET	UNIT_CONVERSION.OFFSET%type,
		FORMULA	UNIT_CONVERSION.FORMULA%type,
		VERSION	UNIT_CONVERSION.VERSION%type,
		DATE_CREATED	UNIT_CONVERSION.DATE_CREATED%type,
		LAST_UPDATED	UNIT_CONVERSION.LAST_UPDATED%type,
		MODIFIED_BY	UNIT_CONVERSION.MODIFIED_BY%type
		);


-----------------------------------------------------------------------------------
-- BASIC PROCESSING RULES
-- the get_<table> procedure queries the source and extablishes the RI dependency
-- hierarchy we must follow by calling the get_ procedures in the correct order
-- and from inside the correct loops.
-- the get_ procedures all use open_src_cursor to pull data from the source.
-- The mapping of columns is done in the query
--
-- use map_<table> to map the source columns to the target columns (pretty much a 1:1)
-- and to adjust the relevant IDs from source to target.  map_ uses
--  IDENTITY_MAPPING to find an existing ID for the target.
--
-- then use save_<table> to put the row into the target
-- if match returns a null ID, then we need to use the sequence
--    and do an INSERT in save<table>
-- if not we can UPDATE the existing record in the target
--.
-- General Conversation:
-- for some target tables we allow duplicate rows from different migration sources
-- these are: assay_context_item, assay_context, measure, assay_document,
-- this means we must transfer the Modified_By column direct from the source .If
-- it's null then put in the source schema owner as the default
-- we cannot allow duplicates in external_reference because this is primary lookup
-- spot for discovering an assay or experiment with an unknown ID
-- in these cases we use the mapping to find the ID and delete it before uploading the row
-- result, rslt_context_item, result_hierarchy are drop and replace tables where we only
-- track the mapping for result_id

-- the element requires special handling due to RI constraints.the Element_ID is
-- used in assay_context_item, project_step, result, measure, run_context_id
-- so any change to the element_ID (due to external factors) requires an update in those tables.
-- we won't do that.  We'll cross-map the element_IDs externally (Excel, anyone?)
-- the ontology (ELEMENT, ELEMENT_HIERARCHY, ONTOLOGY, ONTOLOGY_ITEM, TREE_ROOT)
-- are all designed to be identical and loaded from an external script into DATA_MIG



-----------------------------------------------------------------------
-- the order of these procedures matters
-- they must be declared before they are called!
-----------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------
    procedure open_src_cursor
        (av_src_schema   in  varchar2,
         av_table_name  in  varchar2,
         an_identifier  in number,
         aco_cursor in out r_cursor)

    as
        -- Use this for any query against the source schema.  the use of a REF CURSOR
        -- means that we can the the same code with different schemata
        le_no_source_defined exception;

    begin

        if av_src_schema = 'schatwin'
        then
            -- call the procedure in the source schema
            schatwin.MERGE_MIGRATION.open_LOCAL_cursor
                      (av_src_schema,   -- used as the name in the modified_by column
                       av_table_name,
                       an_identifier,
                       aco_cursor);
            -- an error in the source system will appear here
            -- and trickles thru to be logged under "when others" below


--        elsif av_src_schema = 'dstonich'
--        then
--            -- call the procedure in the source schema
--            dstonich.load_data.open_LOCAL_cursor
--                      (av_src_schema,   -- used as the name in the modified_by column
--                       av_table_name,
--                       an_identifier,
--                       aco_cursor);
            -- an error in the source system will appear here
            -- and trickles thru to be logged under "when others" below

--        elsif av_src_schema = 'bard_dev'
--        then
--            -- call the procedure in the source schema
--            bard_dev.load_data.open_LOCAL_cursor
--                      (av_src_schema,   -- used as the name in the modified_by column
--                       av_table_name,
--                       an_identifier,
--                       aco_cursor);
            -- an error in the source system will appear here
            -- and trickles thru to be logged under "when others" below

--        elsif av_src_schema = 'bard_qa'
--        then
--            -- call the procedure in the source schema
--            bard_qa.load_data.open_LOCAL_cursor
--                      (av_src_schema,   -- used as the name in the modified_by column
--                       av_table_name,
--                       an_identifier,
--                       aco_cursor);
            -- an error in the source system will appear here
            -- and trickles thru to be logged under "when others" below

        else
             raise le_no_source_defined;
        end if;
    exception
        when le_no_source_defined
        then
            log_error(-20002, 'No cursors defined for this source', 'open_src_cursor',
                'Source = ' || pv_src_schema || ', Table = ' || av_table_name);
        when others
        then
            log_error(sqlcode, sqlerrm, 'open_src_cursor');

    end open_src_cursor;


    function map_assay
        (ar_src in r_assay,
         aro_trgt out r_assay)
         return boolean
    as
        lv_src_external_reference    external_reference.ext_assay_ref%type;
        ln_trgt_assay_id  number;

        cursor cur_assay
        is
        select assay_id
        from assay
        where ASSAY_STATUS = ar_src.ASSAY_STATUS
          and ASSAY_SHORT_NAME = ar_src.ASSAY_SHORT_NAME
          and ASSAY_NAME = ar_src.ASSAY_NAME
          and nvl(ASSAY_VERSION, '######') = nvl(ar_src.ASSAY_VERSION, '######')
          and ASSAY_TYPE = ar_src.ASSAY_TYPE
          and nvl(DESIGNED_BY, '######') = nvl(ar_src.DESIGNED_BY, '######')
          and READY_FOR_EXTRACTION = ar_src.READY_FOR_EXTRACTION;

       cursor cur_AID_assay (cv_AID_no varchar2)
        is
        select e.assay_id
        from experiment e,
            external_reference er
        where e.experiment_id = er.experiment_id
         and er.ext_assay_ref = cv_AID_no;

      -- this really needs to be schema insensitive
      cur_external_reference    r_cursor;

    begin
        get_mapping_id(pv_src_schema, 'ASSAY', ar_src.assay_id, ln_trgt_assay_id);
        -- if we have no mapping try to find it by the AID number
        if ln_trgt_assay_id is null
        then
            open_src_cursor(pv_src_schema, 'EXTERNAL_REFERENCE_AID', ar_src.assay_id, cur_external_reference);

            fetch cur_external_reference into lv_src_external_reference;
            ---- no weird characters expected, just letters and =, ? symbols
            close cur_external_reference;

            if lv_src_external_reference is not null
            then
                open cur_AID_assay(lv_src_external_reference);
                fetch cur_AID_assay into ln_trgt_assay_id;
                close cur_AID_assay;
            end if;


            if ln_trgt_assay_id is null
            then
                -- failed on AID # (which is pretty weird)
                -- so try again using the whole assay parameters
                open cur_assay;
                fetch cur_assay into ln_trgt_assay_id;
                close cur_assay;
            end if;


        end if;

        aro_trgt := ar_src;
        -- now edit the oddball ones
        aro_trgt.ASSAY_ID := ln_trgt_assay_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);

        return true;

    exception
        when others
        then
            if cur_external_reference%isopen
            then
                close cur_external_reference;
            end if;

            if cur_AID_assay%isopen
            then
                close cur_AID_assay;
            end if;

            if cur_assay%isopen
            then
                close cur_assay;
            end if;

            return false;
    end map_assay;

    function map_experiment
        (ar_src in r_experiment,
         aro_trgt out r_experiment)
         return boolean
    as
        cursor cur_experiment_AID (cv_AID_no in varchar2)
        is
        select experiment_id
        from external_reference
        where ext_assay_ref = cv_AID_no;


        cur_external_reference  r_cursor;
        ln_trgt_experiment_id number;
        ln_trgt_assay_id      number;
        ln_trgt_laboratory_id number := null;
        lr_src_external_reference   R_external_reference;
        le_null_assay_id    exception;
        le_null_element_id    exception;


    begin
        get_mapping_id(pv_src_schema, 'EXPERIMENT', ar_src.experiment_id, ln_trgt_experiment_id);

        if ln_trgt_experiment_id is null
        then
            -- if mapping isn't there try using the AID no
            open_src_cursor(pv_src_schema, 'EXTERNAL_REFERENCE', ar_src.experiment_id, cur_external_reference);

            fetch cur_external_reference into lr_src_external_reference;
            close cur_external_reference;

            if lr_src_external_reference.ext_assay_ref is not null
            then
                -- use this AID to find the destination experiment_id
                open cur_experiment_AID (lr_src_external_reference.ext_assay_ref);
                fetch cur_experiment_AID into ln_trgt_experiment_id;
                close cur_experiment_AID;
            end if;
        end if;

        -- the assay mapping is pretty much guaranteed to be there because the
        -- assay is higher in the dependency list than experiment
        get_mapping_id(pv_src_schema, 'ASSAY', ar_src.assay_id, ln_trgt_assay_id);
        if ln_trgt_assay_id is null
        then
            raise le_null_assay_id;
        end if;

--        if ar_src.laboratory_id is not null
--        then
--            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.laboratory_id, ln_trgt_laboratory_id);
--            if ln_trgt_laboratory_id is null
--            then
--                raise le_null_element_id;
--            end if;
--        end if;

        -- if we couldn't find the experiment_id return a null
        aro_trgt := ar_src;
        --aro_trgt.LABORATORY_ID := ln_trgt_laboratory_id;
        aro_trgt.EXPERIMENT_ID := ln_trgt_experiment_id;
        aro_trgt.ASSAY_ID := ln_trgt_assay_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);

        return true;

    exception
        when le_null_assay_id
        then
            if cur_external_reference%isopen
            then
                close cur_external_reference;
            end if;
            if cur_experiment_AID%isopen
            then
                close cur_experiment_AID;
            end if;
            log_error(-20001, 'Could not find the destination AID#', 'map_experiment',
                    'source experiment_id = ' || to_char(ar_src.experiment_id));
            return false;
--        when le_null_element_id
--        then
--            if cur_external_reference%isopen
--            then
--                close cur_external_reference;
--            end if;
--            if cur_experiment_AID%isopen
--            then
--                close cur_experiment_AID;
--            end if;
--            log_error(-20001, 'Could not find the Laboratory', 'map_experiment',
--                    'source laboratory_id = ' || to_char(ar_src.laboratory_id));
--            return false;
        when others
        then
            if cur_external_reference%isopen
            then
                close cur_external_reference;
            end if;
            if cur_experiment_AID%isopen
            then
                close cur_experiment_AID;
            end if;
            log_error(sqlcode, sqlerrm, 'map_experiment');

            return false;

    end map_experiment;

    function map_project
        (ar_src in r_project,
         aro_trgt out r_project)
         return boolean
    as
        ln_trgt_project_id    number;

    begin
        get_mapping_id(pv_src_schema, 'PROJECT', ar_src.project_id, ln_trgt_project_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.project_ID := ln_trgt_project_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);

        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_project');
            return false;
    end map_project;

    function map_external_system
        (ar_src in r_external_system,
         aro_trgt out r_external_system)
         return boolean
    as
        ln_trgt_external_system_id    number;

    begin
        get_mapping_id(pv_src_schema, 'EXTERNAL_SYSTEM', ar_src.external_system_id, ln_trgt_external_system_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.EXTERNAL_SYSTEM_ID := ln_trgt_external_system_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);

        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_external_system');
            return false;
    end map_external_system;

    function map_assay_document
        (ar_src in r_assay_document,
         aro_trgt out r_assay_document)
         return boolean
    as
        ln_trgt_assay_id    number;
        ln_trgt_assay_document_id    number;
        le_assay_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'ASSAY', ar_src.assay_id, ln_trgt_assay_id);

        if ln_trgt_assay_id is null
        then
            -- we're in deep doo-doo without a target assay_id
            raise le_assay_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'ASSAY_DOCUMENT', ar_src.assay_document_id, ln_trgt_assay_document_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.ASSAY_DOCUMENT_ID := ln_trgt_assay_document_id;
        aro_trgt.ASSAY_ID := ln_trgt_assay_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);

        return true;

    exception
        when le_assay_id_null
        then
            log_error(-20001, 'target Assay_id is null', 'map_assay_document',
                 'source assay_id = ' || to_char(ar_src.assay_id));
            return false;

        when others
        then
            log_error(sqlcode, sqlerrm, 'map_assay_document');
            return false;
    end map_assay_document;

    procedure save_external_system
        (ar_row in r_external_system,
         an_src_external_system_id   in  number,
         ano_external_system_id out number)
    as

        ln_trgt_external_system_id  number;
        lv_statement    varchar2(4000);

        cursor cur_external_system_AK
        is
        select external_system_id
        from external_system
        where system_name = ar_row.system_name;

    begin

        ln_trgt_external_system_id := ar_row.external_system_id;
        lv_statement := ar_row.SYSTEM_NAME
                    || ', ' || ar_row.OWNER
                    || ', ' || ar_row.SYSTEM_URL;

        if ln_trgt_external_system_id is null
        then
            open cur_external_system_AK;
            fetch cur_external_system_AK into ln_trgt_external_system_id;
            close cur_external_system_AK;
        end if;

        if ln_trgt_external_system_id is not null
        then

        -- if it exists, update the external_system record
            update external_system set
                SYSTEM_NAME = ar_row.SYSTEM_NAME,
                OWNER = ar_row.OWNER,
                SYSTEM_URL = ar_row.SYSTEM_URL,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where external_system_id = ln_trgt_external_system_id;

           if sql%rowcount < 1
           then
              ln_trgt_external_system_id := null;
           else
              save_mapping (pv_src_schema, 'EXTERNAL_SYSTEM', an_src_external_system_id, ln_trgt_external_system_id);
              log_statement('EXTERNAL_SYSTEM', ln_trgt_external_system_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_external_system_id is  null
        then
        -- if not insert a new row
            select external_system_id_seq.nextval
            into ln_trgt_external_system_id
            from dual;

            insert into external_system (
                EXTERNAL_SYSTEM_ID,
                SYSTEM_NAME,
                OWNER,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_external_system_id,
                ar_row.SYSTEM_NAME,
                ar_row.OWNER,
                ar_row.SYSTEM_URL,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'EXTERNAL_SYSTEM', an_src_external_system_id, ln_trgt_external_system_id);

            log_statement('EXTERNAL_SYSTEM', ln_trgt_external_system_id, 'INSERT',
                'src ID ' || to_char(an_src_external_system_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_external_system_id := ln_trgt_external_system_id;

    exception
        when others
        then
            if cur_external_system_AK%isopen
            then
                close cur_external_system_AK;
            end if;
            log_error(sqlcode, sqlerrm, 'external_system');
    end save_external_system;

    procedure get_external_system
        (an_external_system_id    in  number default null)
    as
        cur_external_system r_cursor;
        lr_src_external_system  r_external_system;
        lr_trgt_external_system  r_external_system;
        ln_trgt_external_system_id  number;

    begin
        open_src_cursor(pv_src_schema, 'EXTERNAL_SYSTEM', an_external_system_id, cur_external_system);

        loop
            fetch cur_external_system into lr_src_external_system;
            exit when cur_external_system%notfound;

            if map_external_system (lr_src_external_system, lr_trgt_external_system)
            then

                save_external_system (lr_trgt_external_system, lr_src_external_system.external_system_id, ln_trgt_external_system_id);

            end if;
        end loop;
        close cur_external_system;

    exception
        when others
        then
            if cur_external_system%isopen
            then
                close cur_external_system;
            end if;
            log_error(sqlcode, sqlerrm, 'get_external_system');
    end get_external_system;

    function map_external_reference
        (ar_src in r_external_reference,
         aro_trgt out r_external_reference)
         return boolean
    as
        ln_trgt_experiment_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_project_id    number := null;       -- preset these in case they are skipped below
        ln_trgt_external_system_id    number;
        ln_trgt_external_reference_id    number;
        le_experiment_id_null    exception;
        le_project_id_null    exception;
        le_both_id_null    exception;

    begin
        -- there are two possible FK in external_reference, to Experiment and Project
        -- you can't use both at the same time, but must use one
        if ar_src.experiment_id is not null
        then
            get_mapping_id(pv_src_schema, 'EXPERIMENT', ar_src.experiment_id, ln_trgt_experiment_id);

            if ln_trgt_experiment_id is null
            then
                -- we're in deep doo-doo without a target experiment_id
                raise le_experiment_id_null;
            end if;

        elsif ar_src.project_id is not null
        then
            get_mapping_id(pv_src_schema, 'PROJECT', ar_src.project_id, ln_trgt_project_id);

             if ln_trgt_project_id is null
            then
                -- if it's still not mapped we're in doo-doo
                raise le_project_id_null;
            end if;

        else
            -- both IDs are null.  Shouldn't be possible, but whatever.
            raise le_both_id_null;
        end if;

        if ar_src.external_system_id is not null
        then
            get_mapping_id(pv_src_schema, 'EXTERNAL_SYSTEM', ar_src.external_system_id, ln_trgt_external_system_id);

            if ln_trgt_external_system_id is null
            then
                --let's go off and get this so we populate the external_system table on demand
                get_external_system (ar_src.external_system_id);
                get_mapping_id(pv_src_schema, 'EXTERNAL_SYSTEM', ar_src.external_system_id, ln_trgt_external_system_id);

            end if;
        end if;

        get_mapping_id(pv_src_schema, 'EXTERNAL_REFERENCE', ar_src.external_reference_id, ln_trgt_external_reference_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.EXTERNAL_REFERENCE_ID := ln_trgt_external_reference_id;
        aro_trgt.EXTERNAL_SYSTEM_ID := ln_trgt_external_system_id;
        aro_trgt.EXPERIMENT_ID := ln_trgt_experiment_id;
        aro_trgt.PROJECT_ID := ln_trgt_project_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_experiment_id_null
        then
            log_error(-20001, 'target Experiment_id is null', 'map_external_reference',
                 'source experiment_id = ' || to_char(ar_src.experiment_id));
            return false;
        when le_project_id_null
        then
            log_error(-20001, 'target Project_id is null', 'map_external_reference',
                 'source Project_id = ' || to_char(ar_src.project_id));
            return false;
        when le_both_id_null
        then
            log_error(-20001, 'both source experiment/project IDs are null', 'map_external_reference');
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_external_reference');
            return false;
    end map_external_reference;

    function map_measure
        (ar_src in r_measure,
         aro_trgt out r_measure)
         return boolean
    as
        ln_trgt_assay_id    number;
        ln_trgt_parent_measure_id   number := null;
        ln_trgt_measure_id    number;
        ln_trgt_result_type_id    number := null;
        le_assay_id_null    exception;
        le_element_id_null    exception;
        le_parent_measure_id_null    exception;
        le_assay_context_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'ASSAY', ar_src.assay_id, ln_trgt_assay_id);

        if ln_trgt_assay_id is null
        then
            -- we're in deep doo-doo without a target assay_id
            raise le_assay_id_null;
        end if;

        if ar_src.parent_measure_id is not null
        then
            get_mapping_id(pv_src_schema, 'MEASURE', ar_src.parent_measure_id, ln_trgt_parent_measure_id);
            IF ln_trgt_parent_measure_id is null
            then
                raise le_parent_measure_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.RESULT_TYPE_id, ln_trgt_result_type_id);
        IF ln_trgt_result_type_id is null
        then
            raise le_element_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'MEASURE', ar_src.measure_id, ln_trgt_measure_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.MEASURE_ID := ln_trgt_measure_id;
        aro_trgt.PARENT_MEASURE_ID := ln_trgt_parent_measure_id;
        aro_trgt.RESULT_TYPE_ID := ln_trgt_result_type_id;
        aro_trgt.ASSAY_ID := ln_trgt_assay_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_assay_id_null
        then
            log_error(-20001, 'target Assay_id is null', 'map_measure',
                 'source assay_id = ' || to_char(ar_src.assay_id));
            return false;
        when le_parent_measure_id_null
        then
            log_error(-20001, 'target parent_measure_id is null', 'map_measure',
                 'source parent_measure_id = ' || to_char(ar_src.parent_measure_id));
            return false;
        when le_element_id_null
        then
            log_error(-20001, 'target resut_type_id is null', 'map_measure',
                 'source result_type_id = ' || to_char(ar_src.measure_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_measure');
            return false;
    end map_measure;

    function map_assay_context
        (ar_src in r_assay_context,
         aro_trgt out r_assay_context)
         return boolean
    as
        ln_trgt_assay_id    number;
        ln_trgt_assay_context_id    number;
        le_assay_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'ASSAY', ar_src.assay_id, ln_trgt_assay_id);

        if ln_trgt_assay_id is null
        then
            -- we're in deep doo-doo without a target assay_id
            raise le_assay_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'ASSAY_CONTEXT', ar_src.assay_context_id, ln_trgt_assay_context_id);

        aro_trgt := ar_src;
        aro_trgt.assay_context_ID := ln_trgt_assay_context_id;
        aro_trgt.ASSAY_ID := ln_trgt_assay_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;


    exception
        when le_assay_id_null
        then
            log_error(-20001, 'target Assay_id is null', 'map_assay_context',
                 'source assay_id = ' || to_char(ar_src.assay_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_assay_context');
            return false;
    end map_assay_context;

    function map_exprmt_context
        (ar_src in r_exprmt_context,
         aro_trgt out r_exprmt_context)
         return boolean
    as
        ln_trgt_exprmt_id    number;
        ln_trgt_exprmt_context_id    number;
        le_exprmt_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'EXPERIMENT', ar_src.experiment_id, ln_trgt_exprmt_id);

        if ln_trgt_exprmt_id is null
        then
            -- we're in deep doo-doo without a target exprmt_id
            raise le_exprmt_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'EXPRMT_CONTEXT', ar_src.exprmt_context_id, ln_trgt_exprmt_context_id);

        aro_trgt := ar_src;
        aro_trgt.exprmt_context_ID := ln_trgt_exprmt_context_id;
        aro_trgt.EXPERIMENT_ID := ln_trgt_exprmt_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;


    exception
        when le_exprmt_id_null
        then
            log_error(-20001, 'target exprmt_id is null', 'map_exprmt_context',
                 'source exprmt_id = ' || to_char(ar_src.experiment_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_exprmt_context');
            return false;
    end map_exprmt_context;

    function map_project_context
        (ar_src in r_project_context,
         aro_trgt out r_project_context)
         return boolean
    as
        ln_trgt_project_id    number;
        ln_trgt_project_context_id    number;
        le_project_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'PROJECT', ar_src.project_id, ln_trgt_project_id);

        if ln_trgt_project_id is null
        then
            -- we're in deep doo-doo without a target project_id
            raise le_project_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'PROJECT_CONTEXT', ar_src.project_context_id, ln_trgt_project_context_id);

        aro_trgt := ar_src;
        aro_trgt.project_context_ID := ln_trgt_project_context_id;
        aro_trgt.PROJECT_ID := ln_trgt_project_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;


    exception
        when le_project_id_null
        then
            log_error(-20001, 'target project_id is null', 'map_project_context',
                 'source project_id = ' || to_char(ar_src.project_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_project_context');
            return false;
    end map_project_context;

    function map_step_context
        (ar_src in r_step_context,
         aro_trgt out r_step_context)
         return boolean
    as
        ln_trgt_step_id    number;
        ln_trgt_step_context_id    number;
        le_step_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'PROJECT_STEP', ar_src.project_step_id, ln_trgt_step_id);

        if ln_trgt_step_id is null
        then
            -- we're in deep doo-doo without a target step_id
            raise le_step_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'step_CONTEXT', ar_src.step_context_id, ln_trgt_step_context_id);

        aro_trgt := ar_src;
        aro_trgt.step_context_ID := ln_trgt_step_context_id;
        aro_trgt.PROJECT_step_ID := ln_trgt_step_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;


    exception
        when le_step_id_null
        then
            log_error(-20001, 'target step_id is null', 'map_step_context',
                 'source step_id = ' || to_char(ar_src.project_step_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_step_context');
            return false;
    end map_step_context;

    function map_assay_context_item
        (ar_src in r_assay_context_item,
         aro_trgt out r_assay_context_item)
         return boolean
    as
        ln_trgt_assay_context_id    number := null;
        ln_trgt_assay_context_itm_id    number;
        ln_trgt_attribute_id    number := null;
        ln_trgt_value_id    number := null;
        le_element_id_null    exception;

    begin

        if ar_src.assay_context_id is not null
        then
            get_mapping_id(pv_src_schema, 'ASSAY_CONTEXT', ar_src.assay_context_id, ln_trgt_assay_context_id);
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.attribute_id, ln_trgt_attribute_id);
        if ln_trgt_attribute_id is null
        then
            raise le_element_id_null;
        end if;

        if ar_src.value_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.value_id, ln_trgt_value_id);
            if ln_trgt_value_id is null
            then
                raise le_element_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'ASSAY_CONTEXT_ITEM', ar_src.assay_context_item_id, ln_trgt_assay_context_itm_id);

        -- look in save_assay_context_item for handling of the group...id
        aro_trgt := ar_src;
        aro_trgt.assay_context_ITEM_ID := ln_trgt_assay_context_itm_id;
        aro_trgt.assay_context_ID := ln_trgt_assay_context_id;
        aro_trgt.ATTRIBUTE_ID := ln_trgt_attribute_id;
        aro_trgt.VALUE_ID := ln_trgt_value_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_element_id_null
        then
            log_error(-20001, 'target Attibute or Value_id is null', 'assay_context_item',
                 'source attribute_id = ' || to_char(ar_src.attribute_id)
                 || ', value_id = ' || to_char(ar_src.value_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'assay_context_item');
            return false;
    end map_assay_context_item;

    procedure save_project
        (ar_row in r_project,
         an_src_project_id    in  number,
         ano_project_id  out number)
    as
        ln_trgt_project_id  number;
        lv_statement    varchar2(4000);

        cursor cur_project_AK
        is
        select project_id
        from project
        where project_name = ar_row.project_name;

    begin

        ln_trgt_project_id := ar_row.project_id;
        if length(ar_row.description) > 497
        then
            lv_statement := ar_row.PROJECT_NAME
                    || ', ' || ar_row.GROUP_TYPE
                    || ', ' || substr(ar_row.DESCRIPTION, 1, 497)
                    || '..., ' || ar_row.READY_FOR_EXTRACTION;
        else
            lv_statement := ar_row.PROJECT_NAME
                    || ', ' || ar_row.GROUP_TYPE
                    || ', ' || ar_row.DESCRIPTION
                    || ', ' || ar_row.READY_FOR_EXTRACTION;
        end if;


        if ln_trgt_project_id is null
        then
            open cur_project_ak;
            fetch cur_project_ak into ln_trgt_project_id;
            close cur_project_AK;
        end if;

        if ln_trgt_project_id is not null
        then

        -- if it exists, update the external_system record
            update project set
                PROJECT_NAME = ar_row.PROJECT_NAME,
                GROUP_TYPE = ar_row.GROUP_TYPE,
                DESCRIPTION = ar_row.DESCRIPTION,
                READY_FOR_EXTRACTION = ar_row.READY_FOR_EXTRACTION,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where project_id = ln_trgt_project_id;

           if sql%rowcount < 1
           then
              ln_trgt_project_id := null;
           else
              save_mapping (pv_src_schema, 'PROJECT', an_src_project_id, ln_trgt_project_id);
              log_statement('PROJECT', ln_trgt_project_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_project_id is  null
        then
        -- if not insert a new row
            select project_id_seq.nextval
            into ln_trgt_project_id
            from dual;

            insert into project (
                PROJECT_ID,
                PROJECT_NAME,
                GROUP_TYPE,
                DESCRIPTION,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_project_id,
                ar_row.PROJECT_NAME,
                ar_row.GROUP_TYPE,
                ar_row.DESCRIPTION,
                ar_row.READY_FOR_EXTRACTION,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'PROJECT', an_src_project_id, ln_trgt_project_id);
            log_statement('PROJECT', ln_trgt_project_id, 'INSERT',
                'src ID ' || to_char(an_src_project_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_project_id := ln_trgt_project_id;

    exception
        when others
        then
            if cur_project_AK%isopen
            then
                close cur_project_AK;
            end if;
            log_error(sqlcode, sqlerrm, 'save_project');
    end save_project;

    procedure save_external_reference
        (ar_row in r_external_reference,
         an_src_external_reference_id   in  number,
         ano_external_reference_id   out number)
    as
        ln_trgt_external_reference_id   number;
        lv_statement    varchar2(4000);

        cursor cur_external_reference_AK
        is
        select external_reference_id
        from external_reference
        where external_system_id = ar_row.external_system_id
          and experiment_id = ar_row.experiment_id
          and project_id is null
        union all
        select external_reference_id
        from external_reference
        where external_system_id = ar_row.external_system_id
          and experiment_id is null
          and project_id = ar_row.project_id;

    begin
        ln_trgt_external_reference_id := ar_row.external_reference_id;
        lv_statement := to_char(ar_row.EXTERNAL_SYSTEM_ID)
                || ', ' || to_char(ar_row.EXPERIMENT_ID)
                || ', ' || to_char(ar_row.PROJECT_ID)
                || ', ' || ar_row.EXT_ASSAY_REF;

        if ln_trgt_external_reference_id is null
        then
            open cur_external_reference_AK;
            fetch cur_external_reference_AK into ln_trgt_external_reference_id;
            close cur_external_reference_AK;
        end if;

        if ln_trgt_external_reference_id is not null
        then

        -- if it exists, update the external_reference record
            update external_reference set
                EXTERNAL_SYSTEM_ID = ar_row.EXTERNAL_SYSTEM_ID,
                EXPERIMENT_ID = ar_row.EXPERIMENT_ID,
                PROJECT_ID = ar_row.PROJECT_ID,
                EXT_ASSAY_REF = ar_row.EXT_ASSAY_REF,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.last_updated,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where external_reference_id = ln_trgt_external_reference_id;

           if sql%rowcount < 1
           then
              ln_trgt_external_reference_id := null;
           else
             save_mapping(pv_src_schema, 'EXTERNAL_REFERENCE',
                    an_src_external_reference_id,
                    ln_trgt_external_reference_id);
             log_statement('EXTERNAL_REFERENCE', ln_trgt_external_reference_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_external_reference_id is  null
        then
        -- if not insert a new row
            select external_reference_id_seq.nextval
            into ln_trgt_external_reference_id
            from dual;

            insert into external_reference (
                EXTERNAL_REFERENCE_ID,
                EXTERNAL_SYSTEM_ID,
                EXPERIMENT_ID,
                PROJECT_ID,
                EXT_ASSAY_REF,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_external_reference_id,
                ar_row.EXTERNAL_SYSTEM_ID,
                ar_row.EXPERIMENT_ID,
                ar_row.PROJECT_ID,
                ar_row.EXT_ASSAY_REF,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'EXTERNAL_REFERENCE',
                    an_src_external_reference_id,
                    ln_trgt_external_reference_id);

            log_statement('EXTERNAL_REFERENCE',ln_trgt_external_reference_id,
                  'INSERT', lv_statement);
        end if;

        ano_external_reference_id := ln_trgt_external_reference_id;

    exception
        when others
        then
            if cur_external_reference_AK%isopen
            then
                close cur_external_reference_ak;
            end if;
            log_error(sqlcode, sqlerrm, 'save_external_reference');

    end save_external_reference;

    procedure get_external_reference_proj
        (an_src_project_id    in  number)
    as
        cur_external_reference  r_cursor;
        ln_trgt_external_reference_id   number;
        lr_trgt_external_reference  r_external_reference;
        lr_src_external_reference  r_external_reference;

    begin

        open_src_cursor(pv_src_schema, 'EXTERNAL_REFERENCE_project', an_src_project_id, cur_external_reference);

        loop
            -- this loop is identical to get_external_reference loop
            -- the differences are in map_external_reference
            fetch cur_external_reference into lr_src_external_reference;
            exit when cur_external_reference%notfound;

            if map_external_reference(lr_src_external_reference, lr_trgt_external_reference)
            then

                save_external_reference (lr_trgt_external_reference,
                            lr_src_external_reference.external_reference_id,
                            ln_trgt_external_reference_id);
            end if;
        end loop;

        close cur_external_reference;

    exception
        when others
        then
            if cur_external_reference%isopen
            then
                close cur_external_reference;
            end if;
    end get_external_reference_proj;

    procedure save_project_context
        (ar_row in r_project_context,
         an_src_project_context_id   in  number,
         ano_project_context_id  out number)
    as
        ln_trgt_project_context_id   number;
        lv_statement    varchar2(4000);

        cursor cur_project_context_AK
        is
        select project_context_id
        from project_context
        where PROJECT_id = ar_row.project_id
          and context_name = ar_row.context_name;

    begin
        ln_trgt_project_context_id := ar_row.project_context_id;
        lv_statement := to_char(ar_row.project_ID)
        || ', ' || ar_row.CONTEXT_NAME;

       -- try finding the ID by the alternate key if it's not mapped already
        if ln_trgt_project_context_id is null
        then
            open cur_project_context_AK;
            fetch cur_project_context_AK into ln_trgt_project_context_id;
            close cur_project_context_AK;
        end if;

        if ln_trgt_project_context_id is not null
        then

        -- if it exists, update the MEASURE record
            update project_context set
                project_ID = ar_row.project_ID,
                CONTEXT_NAME = ar_row.CONTEXT_NAME,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where project_context_id = ln_trgt_project_context_id;

           if sql%rowcount < 1
           then
              ln_trgt_project_context_id := null;
           else
              save_mapping(pv_src_schema, 'PROJECT_CONTEXT', an_src_project_context_id, ln_trgt_project_context_id);
              log_statement('PROJECT_CONTEXT', ln_trgt_project_context_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_project_context_id is  null
        then
        -- if not insert a new row
            select project_context_id_seq.nextval
            into ln_trgt_project_context_id
            from dual;

            insert into project_context (
                project_context_ID,
                project_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_project_context_id,
                ar_row.project_ID,
                ar_row.CONTEXT_NAME,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'PROJECT_CONTEXT', an_src_project_context_id, ln_trgt_project_context_id);

            log_statement('PROJECT_CONTEXT',ln_trgt_project_context_id,
                  'INSERT', lv_statement);
        end if;

        ano_project_context_id := ln_trgt_project_context_id;

    exception
        when others
        then
            if cur_project_context_AK%isopen
            then
                close cur_project_context_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_project_context');
    end save_project_context;

    function map_project_context_item
        (ar_src in r_project_context_item,
         aro_trgt out r_project_context_item)
         return boolean
    as
        ln_trgt_project_context_id    number := null;
        ln_trgt_project_context_itm_id    number;
        ln_trgt_attribute_id    number := null;
        ln_trgt_value_id    number := null;
        le_element_id_null    exception;

    begin

        if ar_src.project_context_id is not null
        then
            get_mapping_id(pv_src_schema, 'PROJECT_CONTEXT', ar_src.project_context_id, ln_trgt_project_context_id);
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.attribute_id, ln_trgt_attribute_id);
        if ln_trgt_attribute_id is null
        then
            raise le_element_id_null;
        end if;

        if ar_src.value_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.value_id, ln_trgt_value_id);
            if ln_trgt_value_id is null
            then
                raise le_element_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'PROJECT_CONTEXT_ITEM', ar_src.project_context_item_id, ln_trgt_project_context_itm_id);

        -- look in save_project_context_item for handling of the group...id
        aro_trgt := ar_src;
        aro_trgt.project_context_ITEM_ID := ln_trgt_project_context_itm_id;
        aro_trgt.project_context_ID := ln_trgt_project_context_id;
        aro_trgt.ATTRIBUTE_ID := ln_trgt_attribute_id;
        aro_trgt.VALUE_ID := ln_trgt_value_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_element_id_null
        then
            log_error(-20001, 'target Attibute or Value_id is null', 'project_context_item',
                 'source attribute_id = ' || to_char(ar_src.attribute_id)
                 || ', value_id = ' || to_char(ar_src.value_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'project_context_item');
            return false;
    end map_project_context_item;

    procedure save_project_context_item
        (ar_row in r_project_context_item,
         an_src_project_context_item_id   in  number,
         ano_project_context_item_id    out number)
    as
        ln_trgt_project_context_itm_id   number;
        lv_statement    varchar2(4000);

        cursor cur_mci_AK
        is
        select project_context_item_id
        from project_context_item
        where project_context_id = ar_row.project_context_id
          and display_order = ar_row.display_order
          and attribute_id = ar_row.attribute_id
          and nvl(value_display, '######') = nvl(ar_row.value_display, '######');

    begin
        ln_trgt_project_context_itm_id := ar_row.project_context_item_id;
        lv_statement := to_char(ar_row.DISPLAY_ORDER)
        || ', ' || to_char(ar_row.project_context_ID)
        || ', ' || to_char(ar_row.ATTRIBUTE_ID)
        || ', ' || ar_row.QUALIFIER
        || ', ' || to_char(ar_row.VALUE_ID)
        || ', ' || ar_row.EXT_VALUE_ID
        || ', ' || ar_row.VALUE_DISPLAY
        || ', ' || to_char(ar_row.VALUE_NUM)
        || ', ' || to_char(ar_row.VALUE_MIN)
        || ', ' || to_char(ar_row.VALUE_MAX);

        -- try to find the ID from the AK before inserting a new record
        if ln_trgt_project_context_itm_id is null and ar_row.display_order is not null
        then
            open cur_mci_AK;
            fetch cur_mci_AK into ln_trgt_project_context_itm_id;
            close cur_mci_AK;
        end if;

        if ln_trgt_project_context_itm_id is not null
        then

        -- if it exists, update the MEASURE record
            update project_context_item set
                DISPLAY_ORDER = ar_row.DISPLAY_ORDER,
                project_context_ID = ar_row.project_context_ID,
                ATTRIBUTE_ID = ar_row.ATTRIBUTE_ID,
                QUALIFIER = ar_row.QUALIFIER,
                VALUE_ID = ar_row.VALUE_ID,
                EXT_VALUE_ID = ar_row.EXT_VALUE_ID,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where project_context_item_id = ln_trgt_project_context_itm_id;

           if sql%rowcount < 1
           then
              ln_trgt_project_context_itm_id := null;
           else
              save_mapping(pv_src_schema, 'PROJECT_CONTEXT_ITEM',
                    an_src_project_context_item_id,
                    ln_trgt_project_context_itm_id);
              log_statement('PROJECT_CONTEXT_ITEM', ln_trgt_project_context_itm_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_project_context_itm_id is  null
        then
        -- if not insert a new row
            select project_context_item_id_seq.nextval
            into ln_trgt_project_context_itm_id
            from dual;

            insert into project_context_item (
                project_context_ITEM_ID,
                DISPLAY_ORDER,
                project_CONTEXT_ID,
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_project_context_itm_id,
                ar_row.DISPLAY_ORDER,
                ar_row.project_context_ID,
                ar_row.ATTRIBUTE_ID,
                ar_row.QUALIFIER,
                ar_row.VALUE_ID,
                ar_row.EXT_VALUE_ID,
                ar_row.VALUE_DISPLAY,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'PROJECT_CONTEXT_ITEM',
                    an_src_project_context_item_id,
                    ln_trgt_project_context_itm_id);

            log_statement('PROJECT_CONTEXT_ITEM',ln_trgt_project_context_itm_id,
                  'INSERT', lv_statement);
        end if;

        ano_project_context_item_id := ln_trgt_project_context_itm_id;

    exception
        when others
        then
            if cur_mci_AK%isopen
            then
                close cur_mci_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_project_context_item');
    end save_project_context_item;

    procedure get_project_context_item
        (an_src_project_context_id    in  number)
    as
        cur_project_context_item r_cursor;
        lr_src_project_context_item r_project_context_item;
        lr_trgt_project_context_item  r_project_context_item;
        ln_trgt_project_context_itm_id  number;

    begin
        -- the target measure rows have already been deleted

        open_src_cursor(pv_src_schema, 'PROJECT_CONTEXT_ITEM', an_src_project_context_id, cur_project_context_item);

        loop
            fetch cur_project_context_item into lr_src_project_context_item;
            exit when cur_project_context_item%notfound;

            if map_project_context_item (lr_src_project_context_item, lr_trgt_project_context_item)
            then

                save_project_context_item (lr_trgt_project_context_item, lr_src_project_context_item.project_context_item_id,
                    ln_trgt_project_context_itm_id);
            end if;
        end loop;
        close cur_project_context_item;


    exception
        when others
        then
            if cur_project_context_item%isopen
            then
                close cur_project_context_item;
            end if;

            log_error(sqlcode, sqlerrm, 'get_project_context_item');
    end get_project_context_item;

    procedure get_project_context
        (an_src_project_id    in  number)
    as
        cur_project_context r_cursor;
        lr_src_project_context r_project_context;
        lr_trgt_project_context  r_project_context;
        ln_trgt_project_context_id  number;
        ln_trgt_project_id    number  := null;

    begin
        -- find the rows in the target
        -- and delete them
        --   but relase the child rows by making the FK columns null
        -- also find the mapping rows and delete those
        get_mapping_id(pv_src_schema, 'PROJECT', an_src_project_id, ln_trgt_project_id);
        if ln_trgt_project_id is not null
        then
            cleanout_data_mig('PROJECT_CONTEXT', ln_trgt_project_id, pv_src_schema);
        end if;

        open_src_cursor(pv_src_schema, 'PROJECT_CONTEXT', an_src_project_id, cur_project_context);

        loop
            fetch cur_project_context into lr_src_project_context;
            exit when cur_project_context%notfound;

            if map_project_context (lr_src_project_context, lr_trgt_project_context)
            then

                save_project_context (lr_trgt_project_context, lr_src_project_context.project_context_id,
                    ln_trgt_project_context_id);
            end if;

            get_project_context_item(lr_src_project_context.project_context_id);

        end loop;
        close cur_project_context;

    exception
        when others
        then
            if cur_project_context%isopen
            then
                close cur_project_context;
            end if;

            log_error(sqlcode, sqlerrm, 'get_project_context');
    end get_project_context;

    procedure get_project
        (an_project_id    in  number default null)
    as
        cur_project r_cursor;
        lr_src_project  r_project;
        lr_trgt_project  r_project;
        ln_trgt_project_id  number;

    begin
        open_src_cursor(pv_src_schema, 'PROJECT', an_project_id, cur_project);

        loop
            fetch cur_project into lr_src_project;
            exit when cur_project%notfound;

            if map_project (lr_src_project, lr_trgt_project)
            then

                save_project (lr_trgt_project, lr_src_project.project_id, ln_trgt_project_id);

                get_external_reference_proj(lr_src_project.project_id);

                get_project_context(lr_src_project.project_id);

            end if;
        end loop;
        close cur_project;

    exception
        when others
        then
            if cur_project%isopen
            then
                close cur_project;
            end if;
            log_error(sqlcode, sqlerrm, 'get_project');
    end get_project;

    function map_project_step
        (ar_src in r_project_step,
         aro_trgt out r_project_step)
         return boolean
    as
        ln_trgt_nxt_prjct_exprmt_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_prv_prjct_exprmt_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_project_step_id    number;
        --ln_trgt_external_reference_id    number;
        le_nxt_prjct_exprmt_id_null    exception;
        le_prv_prjct_exprmt_id_null    exception;

    begin
        -- there are two possible FK in project_step to Project_Experiment

        get_mapping_id(pv_src_schema, 'PROJECT_EXPERIMENT', ar_src.next_project_experiment_id, ln_trgt_nxt_prjct_exprmt_id);

        if ln_trgt_nxt_prjct_exprmt_id is null
        then
            -- we can't insert this record until the other experiment arrives
            raise le_nxt_prjct_exprmt_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'PROJECT_EXPERIMENT', ar_src.prev_project_experiment_id, ln_trgt_prv_prjct_exprmt_id);

        if ln_trgt_prv_prjct_exprmt_id is null
        then
            -- we can't insert this record until the other experiment arrives
            raise le_prv_prjct_exprmt_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'PROJECT_STEP', ar_src.project_step_id, ln_trgt_project_step_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        -- assumes that the element table is identical - same IDs!!!
        aro_trgt := ar_src;
        aro_trgt.project_step_ID := ln_trgt_project_step_id;
        aro_trgt.NEXT_PROJECT_EXPERIMENT_ID := ln_trgt_nxt_prjct_exprmt_id;
        aro_trgt.PREV_PROJECT_EXPERIMENT_ID := ln_trgt_prv_prjct_exprmt_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_nxt_prjct_exprmt_id_null
        then
            log_error(-20001, 'target Next_Project_Experiment_id is null', 'map_project_step',
                 'source experiment_id = ' || to_char(ar_src.NEXT_PROJECT_EXPERIMENT_ID));
            return false;
        when le_prv_prjct_exprmt_id_null
        then
            log_error(-20001, 'target Prev_Project_Experiment_id is null', 'map_project_step',
                 'source experiment_id = ' || to_char(ar_src.PREV_PROJECT_EXPERIMENT_ID));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_project_step');
            return false;
    end map_project_step;

    function map_result
        (ar_src in r_result,
         aro_trgt out r_result)
         return boolean
    as
        ln_trgt_experiment_id    number;
        ln_trgt_result_id    number;
        ln_trgt_result_type_id    number;
        le_experiment_id_null    exception;
        le_result_type_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'EXPERIMENT', ar_src.experiment_id, ln_trgt_experiment_id);

        if ln_trgt_experiment_id is null
        then
            -- we're in deep doo-doo without a target experiment_id
            raise le_experiment_id_null;
        end if;


        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.result_type_id, ln_trgt_result_type_id);
        IF ln_trgt_result_type_id is null
        then
            raise le_result_type_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'RESULT', ar_src.result_id, ln_trgt_result_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.RESULT_ID := ln_trgt_result_id;
        aro_trgt.RESULT_TYPE_ID := ln_trgt_result_type_id;
        aro_trgt.EXPERIMENT_ID := ln_trgt_experiment_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_experiment_id_null
        then
            log_error(-20001, 'target experiment_id is null', 'map_result',
                 'source experiment_id = ' || to_char(ar_src.experiment_id));
            return false;
        when le_result_type_id_null
        then
            log_error(-20001, 'target resut_type_id is null', 'map_result',
                 'source result_type_id = ' || to_char(ar_src.result_type_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_result');
            return false;
    end map_result;

    function map_rslt_context_item
        (ar_src in R_rslt_context_item,
         aro_trgt out R_rslt_context_item)
         return boolean
    as
        ln_trgt_result_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_attribute_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_value_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_rslt_context_item_id    number := null;
        --ln_trgt_external_reference_id    number;
        le_result_id_null       exception;
        le_element_id_null       exception;

    begin
        -- there are two possible FK in rslt_context_item, 1 to Experiment and 1 to Result
        if ar_src.result_id is not null
        then
            get_mapping_id(pv_src_schema, 'RESULT', ar_src.result_id, ln_trgt_result_id);

            if ln_trgt_result_id is null
            then
                -- this is a problem, 'cos the result should have been migrated already
                raise le_result_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.attribute_id, ln_trgt_attribute_id);
        if ln_trgt_attribute_id is null
        then
            raise le_element_id_null;
        end if;

        if ar_src.value_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.value_id, ln_trgt_value_id);
            if ln_trgt_value_id is null
            then
                raise le_element_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'RSLT_CONTEXT_ITEM', ar_src.rslt_context_item_id, ln_trgt_rslt_context_item_id);

        aro_trgt := ar_src;
        aro_trgt.rslt_context_item_ID := ln_trgt_rslt_context_item_id;
        aro_trgt.RESULT_ID := ln_trgt_result_id;
        aro_trgt.ATTRIBUTE_ID := ln_trgt_attribute_id;
        aro_trgt.VALUE_ID := ln_trgt_value_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_result_id_null
        then
            log_error(-20001, 'target Result_id is null', 'map_rslt_context_item',
                 'source result_id = ' || to_char(ar_src.result_id));
            return false;
        when le_element_id_null
        then
            log_error(-20001, 'target Attibute or Value_id is null', 'rslt_context_item',
                 'source attribute_id = ' || to_char(ar_src.attribute_id)
                 || ', value_id = ' || to_char(ar_src.value_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_rslt_context_item');
            return false;
    end map_rslt_context_item;

    function map_element
        (ar_src in r_element,
         aro_trgt out r_element)
         return boolean
    as
        ln_trgt_element_id    number;

    begin
        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.element_id, ln_trgt_element_id);

        aro_trgt := ar_src;
        aro_trgt.ELEMENT_ID := ln_trgt_element_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_element');
            return false;
    end map_element;

    function map_project_experiment
        (ar_src in r_project_experiment,
         aro_trgt out r_project_experiment)
         return boolean
    as
        ln_trgt_project_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_stage_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_experiment_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_project_experiment_id    number;
        le_project_id_null    exception;

    begin
        -- there are two possible FK in assay_context_measure for measure and assay_context

        get_mapping_id(pv_src_schema, 'EXPERIMENT', ar_src.experiment_id, ln_trgt_experiment_id);
        IF ar_src.stage_id IS NOT NULL
        THEN
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.stage_id, ln_trgt_stage_id);
        END IF;

        get_mapping_id(pv_src_schema, 'PROJECT', ar_src.project_id, ln_trgt_project_id);
        if ln_trgt_project_id is null
        then
            -- we can't insert this record until the project arrives
            raise le_project_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'PROJECT_EXPERIMENT', ar_src.project_experiment_id, ln_trgt_project_experiment_id);

        aro_trgt := ar_src;
        aro_trgt.EXPERIMENT_ID := ln_trgt_experiment_id;
        aro_trgt.PROJECT_ID := ln_trgt_project_id;
        aro_trgt.stage_ID := ln_trgt_stage_id;
        aro_trgt.PROJECT_EXPERIMENT_ID := ln_trgt_project_experiment_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_project_id_null
        then
            -- let's not log all these errors, they don't mean a lot.
            --log_error(-20001, 'target measure_id is null', 'map_assay_context_measure',
            --     'source measure_id = ' || to_char(ar_src.measure_id));
            return false;
         when others
        then
            log_error(sqlcode, sqlerrm, 'map_project_experiment');
            return false;
    end map_project_experiment;


    function map_assay_context_measure
        (ar_src in r_assay_context_measure,
         aro_trgt out r_assay_context_measure)
         return boolean
    as
        ln_trgt_assay_context_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_measure_id    number := null;    -- preset these in case they are skipped below
        --ln_trgt_external_reference_id    number;
        le_assay_context_id_null    exception;
        le_measure_id_null    exception;

    begin
        -- there are two possible FK in assay_context_measure for measure and assay_context

        if ar_src.measure_id is not null
        then
            get_mapping_id(pv_src_schema, 'MEASURE', ar_src.measure_id, ln_trgt_measure_id);

            if ln_trgt_measure_id is null
            then
                -- the measure has not yet been migrated, need to wait for it
                raise le_measure_id_null;
            end if;

        end if;

        get_mapping_id(pv_src_schema, 'ASSAY_CONTEXT', ar_src.assay_context_id, ln_trgt_assay_context_id);

        if ln_trgt_assay_context_id is null
        then
            -- we can't insert this record until the context arrives
            raise le_assay_context_id_null;
        end if;

        -- we don't track mappings for assay_context_measure due to the cascade delete from measure and assay_context
        --  get_mapping_id(pv_src_schema, 'ELEMENT_HIERARCHY', ar_src.element_hierarchy_id, ln_trgt_element_hierarchy_id);

        aro_trgt := ar_src;
        aro_trgt.MEASURE_ID := ln_trgt_measure_id;
        aro_trgt.ASSAY_CONTEXT_ID := ln_trgt_assay_context_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_measure_id_null
        then
            -- let's not log all these errors, they don't mean a lot.
            --log_error(-20001, 'target measure_id is null', 'map_assay_context_measure',
            --     'source measure_id = ' || to_char(ar_src.measure_id));
            return false;
        when le_assay_context_id_null
        then
            --log_error(-20001, 'target assay_context_id is null', 'map_assay_context_measure',
            --     'source assay_context_id = ' || to_char(ar_src.assay_context_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_assay_context_measure');
            return false;
    end map_assay_context_measure;

    function map_element_hierarchy
        (ar_src in r_element_hierarchy,
         aro_trgt out r_element_hierarchy)
         return boolean
    as
        ln_trgt_parent_element_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_child_element_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_element_hierarchy_id    number;
        --ln_trgt_external_reference_id    number;
        le_parent_element_id_null    exception;
        le_child_element_id_null    exception;

    begin
        -- there are two possible FK in element_hierarchy for child and parent

        if ar_src.parent_element_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.parent_element_id, ln_trgt_parent_element_id);

            if ln_trgt_parent_element_id is null
            then
                -- the parent element has not yet been migrated, need to wait for it
                raise le_parent_element_id_null;
            end if;

        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.child_element_id, ln_trgt_child_element_id);

        if ln_trgt_child_element_id is null
        then
            -- we can't insert this record until the other experiment arrives
            raise le_child_element_id_null;
        end if;

        -- we don't track mappings for element_hierarchy due to the cascade delete from element
        --  get_mapping_id(pv_src_schema, 'ELEMENT_HIERARCHY', ar_src.element_hierarchy_id, ln_trgt_element_hierarchy_id);

        aro_trgt := ar_src;
        aro_trgt.ELEMENT_HIERARCHY_ID := null;
        aro_trgt.PARENT_ELEMENT_ID := ln_trgt_parent_element_id;
        aro_trgt.CHILD_ELEMENT_ID := ln_trgt_child_element_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_parent_element_id_null
        then
            -- let's not log all these errors, they don't mean a lot.
            --log_error(-20001, 'target parent_element_id is null', 'map_element_hierarchy',
            --     'source parent_element_id = ' || to_char(ar_src.parent_element_id));
            return false;
        when le_child_element_id_null
        then
            --log_error(-20001, 'target child_element_id is null', 'map_element_hierarchy',
            --     'source child_element_id = ' || to_char(ar_src.child_element_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_element_hierarchy');
            return false;
    end map_element_hierarchy;

    function map_tree_root
        (ar_src in r_tree_root,
         aro_trgt out r_tree_root)
         return boolean
    as
        ln_trgt_tree_root_id    number;
        ln_trgt_element_id    number;

    begin
        get_mapping_id(pv_src_schema, 'TREE_ROOT', ar_src.tree_root_id, ln_trgt_tree_root_id);
        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.element_id, ln_trgt_element_id);

        -- if the mapping is wiped out, this will make duplicates in the target
        aro_trgt := ar_src;
        aro_trgt.ELEMENT_ID := ln_trgt_element_id;
        aro_trgt.TREE_ROOT_ID := ln_trgt_tree_root_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_tree_root');
            return false;
    end map_tree_root;

    function map_result_hierarchy
        (ar_src in r_result_hierarchy,
         aro_trgt out r_result_hierarchy)
         return boolean
    as
        ln_trgt_parent_result_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_child_result_id    number := null;    -- preset these in case they are skipped below
        le_parent_result_id_null    exception;
        le_child_result_id_null    exception;

    begin
        -- there are two possible FK in element_hierarchy for child and parent

        get_mapping_id(pv_src_schema, 'RESULT', ar_src.parent_result_id, ln_trgt_parent_result_id);

        if ln_trgt_parent_result_id is null
        then
            -- the parent result has not yet been migrated, need to wait for it
            raise le_parent_result_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'RESULT', ar_src.result_id, ln_trgt_child_result_id);

        if ln_trgt_child_result_id is null
        then
            -- we can't insert this record until the other result arrives
            raise le_child_result_id_null;
        end if;

        -- we don't track mappings for result_hierarchy due to the compound primary key
        --  get_mapping_id(pv_src_schema, 'RESULT_HIERARCHY', ar_src.result_hierarchy_id, ln_trgt_result_hierarchy_id);

        aro_trgt := ar_src;
        aro_trgt.PARENT_RESULT_ID := ln_trgt_parent_result_id;
        aro_trgt.RESULT_ID := ln_trgt_child_result_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_parent_result_id_null
        then
            log_error(-20001, 'target parent_result_id is null', 'map_result_hierarchy',
                 'source parent_result_id = ' || to_char(ar_src.parent_result_id));
            return false;
        when le_child_result_id_null
        then
            log_error(-20001, 'target result_id is null', 'map_result_hierarchy',
                 'source child_result_id = ' || to_char(ar_src.result_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_result_hierarchy');
            return false;
    end map_result_hierarchy;

    function map_ontology
        (ar_src in r_ontology,
         aro_trgt out r_ontology)
         return boolean
    as
        ln_trgt_ontology_id    number;

    begin
        get_mapping_id(pv_src_schema, 'ONTOLOGY', ar_src.ontology_id, ln_trgt_ontology_id);

        aro_trgt := ar_src;
        aro_trgt.ontology_ID := ln_trgt_ontology_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_ontology');
            return false;
    end map_ontology;

    procedure save_ontology
        (ar_row in r_ontology,
         an_src_ontology_id   in  number,
         ano_ontology_id    out number)
    as
        ln_trgt_ontology_id  number;
        lv_statement    varchar2(4000);

        cursor cur_ontology_AK
        is
        select ontology_id
        from ontology
        where ontology_name = ar_row.ontology_name;


    begin

        ln_trgt_ontology_id := ar_row.ontology_id;
        lv_statement := ar_row.ONTOLOGY_NAME
                    || ', ' || ar_row.ABBREVIATION
                    || ', ' || ar_row.SYSTEM_URL;

        if ln_trgt_ontology_id is null
        then
            open cur_ontology_AK;
            fetch cur_ontology_AK into ln_trgt_ontology_id;
            close cur_ontology_AK;
        end if;

        if ln_trgt_ontology_id is not null
        then

        -- if it exists, update the external_system record
            update ontology set
                ONTOLOGY_NAME = ar_row.ONTOLOGY_NAME,
                ABBREVIATION = ar_row.ABBREVIATION,
                SYSTEM_URL = ar_row.SYSTEM_URL,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where ontology_id = ln_trgt_ontology_id;

           if sql%rowcount < 1
           then
              ln_trgt_ontology_id := null;
           else
              save_mapping (pv_src_schema, 'ONTOLOGY', an_src_ontology_id, ln_trgt_ontology_id);
              log_statement('ONTOLOGY', ln_trgt_ontology_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_ontology_id is  null
        then
        -- if not insert a new row
            select ontology_id_seq.nextval
            into ln_trgt_ontology_id
            from dual;

            insert into ontology (
                ONTOLOGY_ID,
                ONTOLOGY_NAME,
                ABBREVIATION,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_ontology_id,
                ar_row.ONTOLOGY_NAME,
                ar_row.ABBREVIATION,
                ar_row.SYSTEM_URL,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'ONTOLOGY', an_src_ontology_id, ln_trgt_ontology_id);

            log_statement('ONTOLOGY', ln_trgt_ontology_id, 'INSERT',
                'src ID ' || to_char(an_src_ontology_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_ontology_id := ln_trgt_ontology_id;

    exception
        when others
        then
            if cur_ontology_AK%isopen
            then
                close cur_ontology_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_ontology');
    end save_ontology;

    procedure get_ontology
        (an_ontology_id    in  number default null)
    as
        cur_ontology r_cursor;
        lr_src_ontology  r_ontology;
        lr_trgt_ontology  r_ontology;
        ln_trgt_ontology_id  number;

    begin
        open_src_cursor(pv_src_schema, 'ONTOLOGY', an_ontology_id, cur_ontology);

        loop
            fetch cur_ontology into lr_src_ontology;
            exit when cur_ontology%notfound;

            if map_ontology (lr_src_ontology, lr_trgt_ontology)
            then

                save_ontology (lr_trgt_ontology, lr_src_ontology.ontology_id, ln_trgt_ontology_id);
            end if;
        end loop;
        close cur_ontology;

    exception
        when others
        then
            if cur_ontology%isopen
            then
                close cur_ontology;
            end if;
            log_error(sqlcode, sqlerrm, 'get_ontology');
    end get_ontology;

    function map_ontology_item
        (ar_src in r_ontology_item,
         aro_trgt out r_ontology_item)
         return boolean
    as
        ln_trgt_ontology_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_element_id    number := null;    -- preset these in case they are skipped below
        ln_trgt_ontology_item_id    number;
        le_ontology_id_null    exception;
        le_element_id_null    exception;

    begin
        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.element_id, ln_trgt_element_id);
        if ln_trgt_element_id is null
        then
                -- we've failed to migrate the parent element
            raise le_element_id_null;
        end if;

        get_mapping_id(pv_src_schema, 'ONTOLOGY', ar_src.ontology_id, ln_trgt_ontology_id);

        if ln_trgt_ontology_id is null
        then
            --let's go off and get this, and populate the ontology table on demand
            get_ontology (ar_src.ontology_id);

            get_mapping_id(pv_src_schema, 'ONTOLOGY', ar_src.ontology_id, ln_trgt_ontology_id);
            if ln_trgt_ontology_id is null
            then
                -- we've failed to migrate the parent ontology
                raise le_ontology_id_null;
            end if;

        end if;

        get_mapping_id(pv_src_schema, 'ONTOLOGY_ITEM', ar_src.ontology_item_id, ln_trgt_ontology_item_id);

        aro_trgt := ar_src;
        aro_trgt.ONTOLOGY_ITEM_ID := ln_trgt_ontology_item_id;
        aro_trgt.ONTOLOGY_ID := ln_trgt_ontology_id;
        aro_trgt.ELEMENT_ID := ln_trgt_element_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_ontology_id_null
        then
            log_error(-20001, 'target Ontology_id is null', 'map_ontology_item',
                 'source ontology_id = ' || to_char(ar_src.ontology_id));
            return false;
        when le_element_id_null
        then
            log_error(-20001, 'target Element_id is null', 'map_ontology_item',
                 'source element_id = ' || to_char(ar_src.element_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_ontology_item');
            return false;
    end map_ontology_item;

    function map_unit_conversion
        (ar_src in r_unit_conversion,
         aro_trgt out r_unit_conversion)
         return boolean
    as
        ln_trgt_from_unit_id    number;

    begin
        -- no FK mappings to be had
        aro_trgt := ar_src;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'map_unit_conversion');
            return false;
    end map_unit_conversion;


    procedure cleanout_data_mig
        (av_table_name in varchar2,
         an_id    in  number default null,
         av_src_schema  in varchar2 default null)
    as

    ---------------------------------------------------------------------------------------------
    -- we have versions for each of the 'entity' tables

        cursor cur_assay
        is
        select assay_id
        from assay
        where assay_id = an_id
           or an_id is null;

        cursor cur_experiment (cn_assay_id number)
        is
        select experiment_id
        from experiment
        where assay_id = cn_assay_id;

        cursor cur_result (cn_experiment_id integer)
        is
        select result_id
        from result
        where experiment_id = cn_experiment_id;

        cursor cur_element
        is
        select element_id
        from element
        order by nvl(unit_ID, 0);

        cursor cur_assay_leftovers (cr_assay_id number)
        is
        select assay_id from assay_context
         where assay_id = cr_assay_id
         union all
         select assay_id from measure
         where assay_id = cr_assay_id
         union all
         select assay_id from experiment
         where assay_id = cr_assay_id
         union all
         select assay_id from assay_document
         where assay_id = cr_assay_id;

        cursor cur_exprmt_leftovers (cr_experiment_id number)
        is
        select experiment_id from external_reference
         where experiment_id = cr_experiment_id
         union all
         select experiment_id from project_experiment
         where experiment_id = cr_experiment_id
         union all
         select experiment_id from result
         where experiment_id = cr_experiment_id
         union all
         select experiment_id from exprmt_measure
         where experiment_id = cr_experiment_id
         union all
         select experiment_id from exprmt_context
         where experiment_id = cr_experiment_id;

       ln_leftover_id   number;
       le_bad_table_name    exception;

    begin

        if av_table_name = 'ASSAY'
        then
            for rec_assay in cur_assay
            loop

                cleanout_data_mig ('MEASURE', rec_assay.assay_id, av_src_schema);
                cleanout_data_mig ('ASSAY_CONTEXT_ITEM', rec_assay.assay_id, av_src_schema);
                cleanout_data_mig ('ASSAY_CONTEXT', rec_assay.assay_id, av_src_schema);
                cleanout_data_mig ('ASSAY_DOCUMENT', rec_assay.assay_id, av_src_schema);

                cleanout_data_mig ('EXPERIMENT', rec_assay.assay_id, av_src_schema);

                open cur_assay_leftovers (rec_assay.assay_id);
                fetch cur_assay_leftovers into ln_leftover_id;

                if cur_assay_leftovers%notfound
                then
                     delete from identifier_mapping
                    where target_id = rec_assay.assay_id
                      and table_name = av_table_name;

                    delete from assay
                    where assay_id = rec_assay.assay_id;

                end if;
                close cur_assay_leftovers;

                commit;

            end loop;

        elsif av_table_name = 'ASSAY_DOCUMENT'
        then
            delete from identifier_mapping
            where table_name = av_table_name
              and target_id in
                (select assay_document_id
                 from assay_document
                 where assay_id = an_id
                   and (modified_by = av_src_schema
                        or av_src_schema is null));

            delete from assay_document
            where assay_id = an_id;

        elsif av_table_name = 'ELEMENT'
        then
            cleanout_data_mig ('UNIT_CONVERSION');

            for rec_element in cur_element
            loop
                -- don't need this, the cascade delete handles it
                -- cleanout_data_mig ('ELEMENT_HIERARCHY', rec_element.element_id, av_src_schema);
                cleanout_data_mig ('ONTOLOGY_ITEM', rec_element.element_id, av_src_schema);
                cleanout_data_mig ('TREE_ROOT', rec_element.element_id, av_src_schema);

                delete from identifier_mapping
                where target_id = rec_element.element_id
                  and table_name = av_table_name;

                -- this will fail if the element_ID or label has been used in any
                -- attribute or value_id columns

                delete from element
                where element_id = rec_element.element_id;

            end loop;

        elsif av_table_name = 'ELEMENT_HIERARCHY'
        then
            -- nothing needed here, since cascade delete handles it
            null;
        elsif av_table_name = 'EXPERIMENT'
        then
            for rec_exp in cur_experiment (an_id)
            loop
                -- delete from the bottom of the dependency tree
                -- rslt_context_item
                -- result_hierarchy
                -- result
                -- external_reference
                -- project_step(two references)

                cleanout_data_mig ('EXTERNAL_REFERENCE', rec_exp.experiment_id, av_src_schema);
                cleanout_data_mig ('PROJECT_STEP', rec_exp.experiment_id, av_src_schema);
                cleanout_data_mig ('RESULT', rec_exp.experiment_id, av_src_schema);
                -- catch the left oever items that are not coupled to results
                cleanout_data_mig ('EXPRMT_CONTEXT', rec_exp.experiment_id, av_src_schema);

                -- need a big clause here to see if there are any constraints preventing deletion
                open cur_exprmt_leftovers (rec_exp.experiment_id);
                fetch cur_exprmt_leftovers into ln_leftover_id;

                if cur_exprmt_leftovers%notfound
                then
                    delete from identifier_mapping
                    where target_id = rec_exp.experiment_id
                      and table_name = av_table_name;

                    delete from experiment
                    where experiment_id = rec_exp.experiment_id;
                end if;
                close cur_exprmt_leftovers;

            end loop;

        elsif av_table_name = 'EXTERNAL_REFERENCE'
        then
            delete from identifier_mapping
            where target_id in
                (select external_reference_id
                 from external_reference
                 where experiment_id = an_id)
              and table_name = av_table_name;

            delete from external_reference
            where experiment_id = an_id;

        elsif av_table_name = 'EXTERNAL_SYSTEM'
        then
            -- this is a top level table, remove everything not used in external_reference
            delete from identifier_mapping
            where table_name = av_table_name
            and not exists (select external_system_id
                from external_reference er
                where er.external_system_id = target_id);


            delete from external_system es
            where not exists (select external_system_id
                from external_reference er
                where er.external_system_id = es.external_system_id);

        elsif av_table_name = 'MEASURE'
        then
           delete from identifier_mapping
            where table_name = av_table_name
              and target_id in
                (select measure_id
                 from measure
                 where assay_id = an_id
                   and (modified_by = av_src_schema
                        or av_src_schema is null));

            delete from measure
            where assay_id = an_id
              and (modified_by = av_src_schema
                   or av_src_schema is null);

        elsif av_table_name = 'ASSAY_CONTEXT'
        then
            delete from identifier_mapping
            where target_id in
                (select assay_context_id
                 from assay_context
                 where assay_id = an_id
                   and (modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name;

            delete from assay_context
            where assay_id = an_id
              and (modified_by = av_src_schema
                        or av_src_schema is null);

        elsif av_table_name = 'ASSAY_CONTEXT_ITEM'
        then
            delete from identifier_mapping
            where target_id in
                (select assay_context_item_id
                 from assay_context_item aci,
                      assay_context ac
                 where ac.assay_context_id = aci.assay_context_id
                   and ac.assay_id = an_id
                   and (aci.modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name;

            delete from assay_context_item
             where assay_context_id in
                (select assay_context_id
                 from assay_context
                 where assay_id = an_id)
              and (modified_by = av_src_schema
                  or av_src_schema is null);

        elsif av_table_name = 'ONTOLOGY'
        then
            -- this is a top level table, remove everything not used in ontology_item
            delete from identifier_mapping
            where table_name = av_table_name
            and not exists (select ontology_id
                from ontology_item oi
                where oi.ontology_id = target_id);

            delete from ontology o
            where not exists (select ontology_id
                from ontology_item oi
                where oi.ontology_id = o.ontology_id);

        elsif av_table_name = 'ONTOLOGY_ITEM'
        then
            delete from identifier_mapping
            where target_id in
                (select ontology_item_id
                 from ontology_item
                 where element_id = an_id)
              and table_name = av_table_name;

            delete from ontology_item
            where element_id = an_id;

        elsif av_table_name = 'PROJECT'
        then
            -- this is a top level table, remove everything not used in project_step
            -- or referernced in external_reference
            delete from identifier_mapping im
            where table_name = av_table_name
            and not exists (select 1
                from project_experiment pe
                where pe.project_id = im.target_id)
            and not exists (select 1
                from project_context pc
                where pc.project_id = im.target_id)
            and not exists (select project_id
                from external_reference er
                where er.project_id = im.target_id);

            delete from project p
            where not exists (select 1
                from project_experiment pe
                where pe.project_id = p.project_id)
            and not exists (select 1
                from project_context pc
                where pc.project_id = p.project_id)
            and not exists (select an_id
                from external_reference er
                where er.project_id = p.project_id);


        elsif av_table_name = 'PROJECT_EXPERIMENT'
        then
            delete from identifier_mapping
            where table_name = av_table_name
             AND target_id in
                (select project_experiment_id
                 from project_experiment
                 where experiment_id = an_id)
              and NOT EXISTS (SELECT 1
                  FROM prjct_exprmt_context pec
                  WHERE pec.project_experiment_id = target_id);

            delete from project_experiment pe
            where experiment_id = an_id
             and NOT EXISTS (SELECT 1
                  FROM prjct_exprmt_context pec
                  WHERE pec.project_experiment_id = pe.project_experiment_id);


        elsif av_table_name = 'PROJECT_DOCUMENT'
        then
            delete from identifier_mapping
            where table_name = av_table_name
              and target_id in
                (select project_document_id
                 from project_document
                 where project_id = an_id
                   and (modified_by = av_src_schema
                        or av_src_schema is null));

            delete from project_document
            where project_id = an_id;

        elsif av_table_name = 'RESULT'
        then

            for rec_result in cur_result(an_id)
            loop
                cleanout_data_mig ('RSLT_CONTEXT_ITEM', rec_result.result_id, av_src_schema);
                cleanout_data_mig ('RESULT_HIERARCHY', rec_result.result_id, av_src_schema);

                delete from identifier_mapping
                where target_id = rec_result.result_id
                  and table_name = av_table_name
                  and (source_schema = av_src_schema
                    or av_src_schema is null);

                delete from result
                where result_id = rec_result.result_id
                  and (modified_by = av_src_schema
                    or av_src_schema is null);

            end loop;

        elsif av_table_name = 'RSLT_CONTEXT_ITEM'
        then
            delete from rslt_context_item
            where result_id = an_id;

        elsif av_table_name = 'EXPRMT_CONTEXT'
        THEN
            -- DO BOTH context and context item in one fell swoop
            delete from identifier_mapping
            where target_id in
                (select exprmt_context_item_id
                 from exprmt_context_item aci,
                      exprmt_context ac
                 where ac.exprmt_context_id = aci.exprmt_context_id
                   and ac.experiment_id = an_id
                   and (aci.modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name || '_ITEM';

            delete from exprmt_context_item
             where exprmt_context_id in
                (select exprmt_context_id
                 from exprmt_context
                 where experiment_id = an_id)
              and (modified_by = av_src_schema
                  or av_src_schema is null);

            delete from identifier_mapping
            where target_id in
                (select exprmt_context_id
                 from exprmt_context ec
                 where experiment_id = an_id
                   AND NOT EXISTS (SELECT 1
                      FROM exprmt_context_item eci
                      WHERE eci.exprmt_context_id = ec.exprmt_context_id)
                  and (modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name;

            delete from exprmt_context ec
            where experiment_id = an_id
              AND NOT EXISTS (SELECT 1
                  FROM exprmt_context_item eci
                  WHERE eci.exprmt_context_id = ec.exprmt_context_id)
              and (modified_by = av_src_schema
                        or av_src_schema is null);


        elsif av_table_name = 'PROJECT_CONTEXT'
        then
            -- DO BOTH context and context item in one fell swoop
            delete from identifier_mapping
            where target_id in
                (select project_context_item_id
                 from project_context_item aci,
                      project_context ac
                 where ac.project_context_id = aci.project_context_id
                   and ac.project_id = an_id
                   and (aci.modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name || '_ITEM';

            delete from project_context_item
             where project_context_id in
                (select project_context_id
                 from project_context
                 where project_id = an_id)
              and (modified_by = av_src_schema
                  or av_src_schema is null);

            delete from identifier_mapping
            where target_id in
                (select project_context_id
                 from project_context ec
                 where project_id = an_id
                   AND NOT EXISTS (SELECT 1
                      FROM project_context_item eci
                      WHERE eci.project_context_id = ec.project_context_id)
                  and (modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name;

            delete from project_context ec
            where project_id = an_id
              AND NOT EXISTS (SELECT 1
                  FROM project_context_item eci
                  WHERE eci.project_context_id = ec.project_context_id)
              and (modified_by = av_src_schema
                        or av_src_schema is null);


        elsif av_table_name = 'STEP_CONTEXT'
        then
            -- DO BOTH context and context item in one fell swoop
            delete from identifier_mapping
            where target_id in
                (select step_context_item_id
                 from step_context_item aci,
                      step_context ac
                 where ac.step_context_id = aci.step_context_id
                   and ac.project_step_id = an_id
                   and (aci.modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name || '_ITEM';

            delete from step_context_item
             where step_context_id in
                (select step_context_id
                 from step_context
                 where project_step_id = an_id)
              and (modified_by = av_src_schema
                  or av_src_schema is null);

            delete from identifier_mapping
            where target_id in
                (select step_context_id
                 from step_context ec
                 where project_step_id = an_id
                   AND NOT EXISTS (SELECT 1
                      FROM step_context_item eci
                      WHERE eci.step_context_id = ec.step_context_id)
                  and (modified_by = av_src_schema
                        or av_src_schema is null))
              and table_name = av_table_name;

            delete from step_context ec
            where project_step_id = an_id
              AND NOT EXISTS (SELECT 1
                  FROM step_context_item eci
                  WHERE eci.step_context_id = ec.step_context_id)
              and (modified_by = av_src_schema
                        or av_src_schema is null);

        elsif av_table_name = 'RESULT_HIERARCHY'
        then
            -- do nothing here.  This is done by cascade delete FK from Result
            null;

        elsif av_table_name = 'TREE_ROOT'
        then
                delete from identifier_mapping
                where target_id IN
                    (select tree_root_id
                    from tree_root
                    where element_id = an_id)
                  and table_name = av_table_name;

                delete from tree_root
                where element_id = an_id;

        elsif av_table_name = 'UNIT_CONVERSION'
        then
            -- just delete everything as the primary key is compound
            -- (an_id does not work here)
            delete from unit_conversion;
        else
            raise le_bad_table_name;
        end if;


     exception     ---- we really need the errors to surface here! ----------
        when le_bad_table_name
        then
            log_error(-20001, 'Bad table name', 'Cleanout_data_mig', 'table = ' || av_table_name);
        when others
        then
            log_error(sqlcode, sqlerrm, 'cleanout_data_mig', 'table = ' || av_table_name);

    end cleanout_data_mig;

    procedure save_element
        (ar_row in r_element,
         an_src_element_id   in  number,
         ano_element_id out number)
    as
        ln_trgt_element_id  number;
        lv_statement    varchar2(4000);

        cursor cur_element_AK
        is
        select element_id
        from element
        where lower(label) = lower(ar_row.label);

    begin

        ln_trgt_element_id := ar_row.element_id;
        lv_statement := ar_row.ELEMENT_STATUS
                    || ', ' || ar_row.LABEL
                    || ', ' || ar_row.DESCRIPTION
                    || ', ' || ar_row.ABBREVIATION
                    || ', ' || ar_row.SYNONYMS
                    || ', ' || ar_row.UNIT_ID
                    || ', ' || ar_row.EXTERNAL_URL
                    || ', ' || ar_row.READY_FOR_EXTRACTION;


        -- second attempt to find using the natural key (label)
        if ln_trgt_element_id is null
        then
            open cur_element_AK;
            fetch cur_element_AK into ln_trgt_element_id;
            close cur_element_AK;
        end if;

        if ln_trgt_element_id is not null
        then

        -- if it exists, update the external_system record
            update element set
                ELEMENT_STATUS = ar_row.ELEMENT_STATUS,
                LABEL = ar_row.LABEL,
                DESCRIPTION = ar_row.DESCRIPTION,
                ABBREVIATION = ar_row.ABBREVIATION,
                SYNONYMS = ar_row.SYNONYMS,
                UNIT_ID = ar_row.UNIT_ID,
                BARD_URI = ar_row.BARD_URI,
                EXTERNAL_URL = ar_row.EXTERNAL_URL,
                READY_FOR_EXTRACTION = ar_row.READY_FOR_EXTRACTION,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where element_id = ln_trgt_element_id;

           if sql%rowcount < 1
           then
              ln_trgt_element_id := null;
           else
              save_mapping (pv_src_schema, 'ELEMENT', an_src_element_id, ln_trgt_element_id);
              log_statement('ELEMENT', ln_trgt_element_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_element_id is  null
        then
        -- if not insert a new row
            select element_id_seq.nextval
            into ln_trgt_element_id
            from dual;

            insert into element (
                ELEMENT_ID,
                ELEMENT_STATUS,
                LABEL,
                DESCRIPTION,
                ABBREVIATION,
                SYNONYMS,
                UNIT_ID,
                BARD_URI,
                EXTERNAL_URL,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_element_id,
                ar_row.ELEMENT_STATUS,
                ar_row.LABEL,
                ar_row.DESCRIPTION,
                ar_row.ABBREVIATION,
                ar_row.SYNONYMS,
                ar_row.UNIT_ID,
                ar_row.BARD_URI,
                ar_row.EXTERNAL_URL,
                ar_row.READY_FOR_EXTRACTION,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'ELEMENT', an_src_element_id, ln_trgt_element_id);

            log_statement('ELEMENT', ln_trgt_element_id, 'INSERT',
                'src ID ' || to_char(an_src_element_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_element_id := ln_trgt_element_id;

    exception
        when others
        then
            if cur_element_AK%isopen
            then
                close cur_element_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_element');
    end save_element;

    procedure save_element_hierarchy
        (ar_row in r_element_hierarchy,
         an_src_element_hierarchy_id   in  number,
         ano_element_hierarchy_id   out number)
    as
        ln_trgt_element_hierarchy_id   number;
        lv_statement    varchar2(4000);

        cursor cur_element_hierarchy_AK
        is
        Select element_hierarchy_id
        from element_hierarchy
        where nvl(parent_element_id, -100) = nvl(ar_row.PARENT_ELEMENT_ID, -100)
          and child_element_id = ar_row.CHILD_ELEMENT_ID
          and relationship_type =  ar_row.RELATIONSHIP_TYPE;


    begin
        ln_trgt_element_hierarchy_id := ar_row.element_hierarchy_id;
        lv_statement := to_char(ar_row.PARENT_ELEMENT_ID)
                || ', ' || to_char(ar_row.CHILD_ELEMENT_ID)
                || ', ' || ar_row.RELATIONSHIP_TYPE;

        if ln_trgt_element_hierarchy_id is null
        then
            -- try to find the row using the AK
            open cur_element_hierarchy_AK;
            fetch cur_element_hierarchy_AK into ln_trgt_element_hierarchy_id;
            close cur_element_hierarchy_AK;
        end if;

        if ln_trgt_element_hierarchy_id is not null
        then
        -- if it exists, update the element_hierarchy record
            update element_hierarchy set
                PARENT_ELEMENT_ID = ar_row.PARENT_ELEMENT_ID,
                CHILD_ELEMENT_ID = ar_row.CHILD_ELEMENT_ID,
                RELATIONSHIP_TYPE = ar_row.RELATIONSHIP_TYPE,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where element_hierarchy_id = ln_trgt_element_hierarchy_id;

           if sql%rowcount < 1
           then
              ln_trgt_element_hierarchy_id := null;
           else
              log_statement('ELEMENT_HIERARCHY', ln_trgt_element_hierarchy_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_element_hierarchy_id is  null
        then
            -- if not insert a new row
            select element_hierarchy_id_seq.nextval
            into ln_trgt_element_hierarchy_id
            from dual;

            insert into element_hierarchy (
                ELEMENT_HIERARCHY_ID,
                PARENT_ELEMENT_ID,
                CHILD_ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_element_hierarchy_id,
                ar_row.PARENT_ELEMENT_ID,
                ar_row.CHILD_ELEMENT_ID,
                ar_row.RELATIONSHIP_TYPE,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

                -- no point in saving a mapping -doesn't go anywhere
                --            save_mapping(pv_src_schema, 'ELEMENT_HIERARCHY',
                --                    an_src_element_hierarchy_id,
                --                    ln_trgt_element_hierarchy_id);

            log_statement('ELEMENT_HIERARCHY',ln_trgt_element_hierarchy_id,
                  'INSERT', lv_statement);
        end if;

        ano_element_hierarchy_id := ln_trgt_element_hierarchy_id;

    exception
        when others
        then
            if cur_element_hierarchy_AK%isopen
            then
                close cur_element_hierarchy_AK;
            end if;
            log_error(sqlcode, sqlerrm, 'save_element_hierarchy');
    end save_element_hierarchy;

    procedure get_element_hierarchy
        (an_element_id in number default null)
    as
        cur_element_hierarchy r_cursor;
        lr_src_element_hierarchy  r_element_hierarchy;
        lr_trgt_element_hierarchy  r_element_hierarchy;
        ln_trgt_element_hierarchy_id  number;

    begin
        open_src_cursor(pv_src_schema, 'ELEMENT_HIERARCHY', an_element_id, cur_element_hierarchy);

        loop
            fetch cur_element_hierarchy into lr_src_element_hierarchy;
            exit when cur_element_hierarchy%notfound;

            if map_element_hierarchy (lr_src_element_hierarchy, lr_trgt_element_hierarchy)
            then

                save_element_hierarchy (lr_trgt_element_hierarchy, lr_src_element_hierarchy.element_hierarchy_id, ln_trgt_element_hierarchy_id);
            end if;
        end loop;
        close cur_element_hierarchy;

    exception
        when others
        then
            if cur_element_hierarchy%isopen
            then
                close cur_element_hierarchy;
            end if;
            log_error(sqlcode, sqlerrm, 'get_element_hierarchy');
    end get_element_hierarchy;

    procedure save_ontology_item
        (ar_row in r_ontology_item,
         an_src_ontology_item_id   in  number,
         ano_ontology_item_id  out number)
    as
        ln_trgt_ontology_item_id   number;
        lv_statement    varchar2(4000);

    begin
        ln_trgt_ontology_item_id := ar_row.ontology_item_id;
        lv_statement := to_char(ar_row.ONTOLOGY_ID)
                || ', ' || TO_CHAR(ar_row.ELEMENT_ID)
                || ', ' || ar_row.ITEM_REFERENCE;

        if ln_trgt_ontology_item_id is not null
        then

        -- if it exists, update the ontology_item record
            update ontology_item set
                ONTOLOGY_ID = ar_row.ONTOLOGY_ID,
                ELEMENT_ID = ar_row.ELEMENT_ID,
                ITEM_REFERENCE = ar_row.ITEM_REFERENCE,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.last_updated,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where ontology_item_id = ln_trgt_ontology_item_id;

           if sql%rowcount < 1
           then
              ln_trgt_ontology_item_id := null;
           else
              save_mapping(pv_src_schema, 'ONTOLOGY_ITEM',
                    an_src_ontology_item_id,
                    ln_trgt_ontology_item_id);
              log_statement('ONTOLOGY_ITEM', ln_trgt_ontology_item_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_ontology_item_id is  null
        then
        -- if not insert a new row
            select ontology_item_id_seq.nextval
            into ln_trgt_ontology_item_id
            from dual;

            insert into ontology_item (
                ONTOLOGY_ITEM_ID,
                ONTOLOGY_ID,
                ELEMENT_ID,
                ITEM_REFERENCE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_ontology_item_id,
                ar_row.ONTOLOGY_ID,
                ar_row.ELEMENT_ID,
                ar_row.ITEM_REFERENCE,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'ONTOLOGY_ITEM',
                    an_src_ontology_item_id,
                    ln_trgt_ontology_item_id);

            log_statement('ONTOLOGY_ITEM',ln_trgt_ontology_item_id,
                  'INSERT', lv_statement);
        end if;

        ano_ontology_item_id := ln_trgt_ontology_item_id;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'save_ontology_item');

    end save_ontology_item;

    procedure get_ontology_item
        (an_element_id    in  number)
    as
        cur_ontology_item  r_cursor;
        ln_trgt_ontology_item_id   number;
        lr_trgt_ontology_item  r_ontology_item;
        lr_src_ontology_item  r_ontology_item;

    begin
        open_src_cursor(pv_src_schema, 'ONTOLOGY_ITEM', an_element_id, cur_ontology_item);

        loop
            fetch cur_ontology_item into lr_src_ontology_item;
            exit when cur_ontology_item%notfound;

            if map_ontology_item (lr_src_ontology_item, lr_trgt_ontology_item)
            then

                save_ontology_item (lr_trgt_ontology_item,
                            lr_src_ontology_item.ontology_item_id,
                            ln_trgt_ontology_item_id);
            end if;
        end loop;

        close cur_ontology_item;

    exception
        when others
        then
            if cur_ontology_item%isopen
            then
                close cur_ontology_item;
            end if;
    end get_ontology_item;

    procedure save_unit_conversion
        (ar_row in r_unit_conversion)
    as
        lv_statement    varchar2(4000);

    begin

        lv_statement := to_char(ar_row.MULTIPLIER)
                    || ', ' || to_char(ar_row.OFFSET)
                    || ', ' || ar_row.FORMULA;

        -- if it exists, update the external_system record
        update unit_conversion set
            MULTIPLIER = ar_row.MULTIPLIER,
            OFFSET = ar_row.OFFSET,
            FORMULA = ar_row.FORMULA,
            VERSION = ar_row.VERSION,
            DATE_CREATED = ar_row.DATE_CREATED,
            LAST_UPDATED = ar_row.LAST_UPDATED,
            MODIFIED_BY = ar_row.MODIFIED_BY
        where from_unit_id = ar_row.FROM_UNIT_id
          and to_unit_id = ar_row.TO_UNIT_id;

       if sql%rowcount >0
       then
          log_statement('UNIT_CONVERSION', -1, 'UPDATE', lv_statement);
       else
            insert into unit_conversion (
                FROM_UNIT_id,
                TO_UNIT_id,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ar_row.FROM_UNIT_id,
                ar_row.TO_UNIT_id,
                ar_row.MULTIPLIER,
                ar_row.OFFSET,
                ar_row.FORMULA,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            log_statement('ONTOLOGY', -1, 'INSERT',
                'src ID ' || To_Char(ar_row.FROM_UNIT_id) || '/' || To_Char(ar_row.TO_UNIT_ID)
                || ', ' || lv_statement);

       end if;


    exception
        when others
        then
            log_error (sqlcode, sqlerrm, 'save_unit_conversion');
    end save_unit_conversion;

    procedure get_unit_conversion
            (av_unit in varchar2 default null)
    as
        -- gotta do this in one big lump as the idemntifier_mapping and other stuff doesn't
        --like the varahcra2 primary key
        cur_unit_conversion  r_cursor;
        ln_trgt_from_unit   varchar2(128);
        lr_trgt_unit_conversion  r_unit_conversion;
        lr_src_unit_conversion  r_unit_conversion;

        ln_dummy_ID     number := null;
        lv_process_step varchar2(40);

    begin
        open_src_cursor(pv_src_schema, 'UNIT_CONVERSION', ln_dummy_ID, cur_unit_conversion);
        lv_process_step := 'after open src cursor';

        loop
            lv_process_step := 'before fetch';
            fetch cur_unit_conversion into lr_src_unit_conversion;
            exit when cur_unit_conversion%notfound;
            lv_process_step := 'before map_unit_conversion';

            if map_unit_conversion (lr_src_unit_conversion, lr_trgt_unit_conversion)
            then
                lv_process_step := 'before save_unit_conversion';
                save_unit_conversion (lr_trgt_unit_conversion);
            end if;
        end loop;
        lv_process_step := 'end of loop';

        close cur_unit_conversion;

    exception
        when others
        then
            if cur_unit_conversion%isopen
            then
                close cur_unit_conversion;
            end if;
            log_error(sqlcode, sqlerrm, 'get_unit_conversion', lv_process_step);
    end get_unit_conversion;

    procedure save_tree_root
        (ar_row in r_tree_root,
         an_src_tree_root_id   in  number,
         ano_tree_root_id out number)
    as
        ln_trgt_tree_root_id  number;
        lv_statement    varchar2(4000);

        cursor cur_tree_root_AK
        is
        select tree_root_id
        from tree_root
        where tree_name = ar_row.tree_name;

    begin

        ln_trgt_tree_root_id := ar_row.tree_root_id;
        lv_statement := ar_row.TREE_NAME
                    || ', ' || TO_CHAR(ar_row.ELEMENT_ID)
                    || ', ' || ar_row.RELATIONSHIP_TYPE;

        if ln_trgt_tree_root_id is null
        then
            open cur_tree_root_AK;
            fetch cur_tree_root_AK into ln_trgt_tree_root_id;
            close cur_tree_root_AK;
        end if;

        if ln_trgt_tree_root_id is not null
        then

        -- if it exists, update the external_system record
            update tree_root set
                TREE_NAME = ar_row.TREE_NAME,
                ELEMENT_ID = ar_row.ELEMENT_ID,
                RELATIONSHIP_TYPE = ar_row.RELATIONSHIP_TYPE,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where tree_root_id = ln_trgt_tree_root_id;

           if sql%rowcount < 1
           then
              ln_trgt_tree_root_id := null;
           else
              save_mapping (pv_src_schema, 'TREE_ROOT', an_src_tree_root_id, ln_trgt_tree_root_id);
              log_statement('TREE_ROOT', ln_trgt_tree_root_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_tree_root_id is  null
        then
        -- if not insert a new row, we don't have a sequence for this!
            select max(tree_root_id)
            into ln_trgt_tree_root_id
            from tree_root;

            if ln_trgt_tree_root_id is null
            then
                ln_trgt_tree_root_id := 1;
            else
                ln_trgt_tree_root_id := ln_trgt_tree_root_id +1;
            end if;

            insert into tree_root (
                TREE_ROOT_ID,
                TREE_NAME,
                ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_tree_root_id,
                ar_row.TREE_NAME,
                ar_row.ELEMENT_ID,
                ar_row.RELATIONSHIP_TYPE,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'TREE_ROOT', an_src_tree_root_id, ln_trgt_tree_root_id);

            log_statement('TREE_ROOT', ln_trgt_tree_root_id, 'INSERT',
                'src ID ' || to_char(an_src_tree_root_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_tree_root_id := ln_trgt_tree_root_id;

    exception
        when others
        then
            if cur_tree_root_AK%isopen
            then
                close cur_tree_root_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_tree_root');
    end save_tree_root;

    procedure get_tree_root
        (an_element_id in number default null)
    as
        cur_tree_root r_cursor;
        lr_src_tree_root  r_tree_root;
        lr_trgt_tree_root  r_tree_root;
        ln_trgt_tree_root_id  number;

    begin
        open_src_cursor(pv_src_schema, 'TREE_ROOT', an_element_id, cur_tree_root);

        loop
            fetch cur_tree_root into lr_src_tree_root;
            exit when cur_tree_root%notfound;

            if map_tree_root (lr_src_tree_root, lr_trgt_tree_root)
            then

                save_tree_root (lr_trgt_tree_root, lr_src_tree_root.tree_root_id, ln_trgt_tree_root_id);
            end if;
        end loop;
        close cur_tree_root;

    exception
        when others
        then
            if cur_tree_root%isopen
            then
                close cur_tree_root;
            end if;
            log_error(sqlcode, sqlerrm, 'get_tree_root');
    end get_tree_root;


    procedure get_element
        (an_element_id in number default null)
    as
        cur_element r_cursor;
        lr_src_element  r_element;
        lr_trgt_element  r_element;
        ln_trgt_element_id  number;

    begin
        open_src_cursor(pv_src_schema, 'ELEMENT', an_element_id, cur_element);


        loop
            fetch cur_element into lr_src_element;
            exit when cur_element%notfound;

            if map_element (lr_src_element, lr_trgt_element)
            then

                save_element (lr_trgt_element, lr_src_element.element_id, ln_trgt_element_id);
                -- the element_hierarchy mapping seesm to make this work without deletion
                -- cleanout_data_mig ('ELEMENT_HIERARCHY' ,ln_trgt_element_id, av_src_schema);

                get_element_hierarchy(lr_src_element.element_id);
                get_ontology_item(lr_src_element.element_id);
            end if;
        end loop;
        close cur_element;

        get_unit_conversion;

        get_tree_root;

        manage_ontology.make_trees;

    exception
        when others
        then
            if cur_element%isopen
            then
                close cur_element;
            end if;
            log_error(sqlcode, sqlerrm, 'get_element');
    end get_element;

    procedure save_assay
        (ar_row in r_assay,
         an_src_assay_id    in  number,
         ano_assay_id out number)
    as

        ln_trgt_assay_id  number;
        lv_src_modified_by   varchar2(40);
        lv_statement    varchar2(4000);

    begin

        ln_trgt_assay_id := ar_row.assay_id;
        if length(ar_row.assay_name) > 497
        then
            lv_statement := ar_row.assay_status
            || ', ' || ar_row.ASSAY_SHORT_NAME
            || ', ' || substr(ar_row.assay_name, 1, 497)
            || '..., ' || ar_row.assay_version
            || ', ' || ar_row.assay_type
            || ', ' || ar_row.designed_by
            || ', ' || ar_row.ready_for_extraction;
        else
            lv_statement := ar_row.assay_status
            || ', ' || ar_row.ASSAY_SHORT_NAME
            || ', ' || ar_row.assay_name
            || ', ' || ar_row.assay_version
            || ', ' || ar_row.assay_type
            || ', ' || ar_row.designed_by
            || ', ' || ar_row.ready_for_extraction;
        end if;

        if ln_trgt_assay_id is not null
        then

        -- if it exists, update the assay record
            update assay set
                ASSAY_STATUS = ar_row.ASSAY_STATUS,
                ASSAY_SHORT_NAME = ar_row.ASSAY_SHORT_NAME,
                ASSAY_NAME = ar_row.ASSAY_NAME,
                ASSAY_VERSION = ar_row.ASSAY_VERSION,
                ASSAY_TYPE = ar_row.ASSAY_TYPE,
                DESIGNED_BY = ar_row.DESIGNED_BY,
                READY_FOR_EXTRACTION = ar_row.READY_FOR_EXTRACTION,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where assay_id = ln_trgt_assay_id;

           if sql%rowcount < 1
           then
              ln_trgt_assay_id := null;
           else
              save_mapping (pv_src_schema, 'ASSAY', an_src_assay_id, ln_trgt_assay_id);
              log_statement('ASSAY', ln_trgt_assay_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_assay_id is  null
        then

        -- if not insert a new row
            select assay_id_seq.nextval
            into ln_trgt_assay_id
            from dual;

            insert into assay (
                ASSAY_ID,
                ASSAY_STATUS,
                ASSAY_SHORT_NAME,
                ASSAY_NAME,
                ASSAY_VERSION,
                ASSAY_TYPE,
                DESIGNED_BY,
                READY_FOR_EXTRACTION,
                MODIFIED_BY
            ) values (
                ln_trgt_assay_id,
                ar_row.ASSAY_STATUS,
                ar_row.ASSAY_SHORT_NAME,
                ar_row.ASSAY_NAME,
                ar_row.ASSAY_VERSION,
                ar_row.ASSAY_TYPE,
                ar_row.DESIGNED_BY,
                ar_row.READY_FOR_EXTRACTION,
                ar_row.MODIFIED_BY );

            save_mapping (pv_src_schema, 'ASSAY', an_src_assay_id, ln_trgt_assay_id);

            log_statement('ASSAY', ln_trgt_assay_id, 'INSERT',
                'src ID ' || to_char(an_src_assay_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_assay_id := ln_trgt_assay_id;

    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'save_assay');
    end save_assay;

    procedure save_project_step
        (ar_row in r_project_step,
         an_src_project_step_id   in  number,
         ano_project_step out number)
    as
        ln_trgt_project_step_id  number;
        lv_statement    varchar2(4000);

        cursor cur_project_step_AK
        is
        select project_step_id
        from project_step
        where next_project_experiment_id = ar_row.next_project_experiment_id
          and prev_project_experiment_id = ar_row.prev_project_experiment_id;

    begin

        ln_trgt_project_step_id := ar_row.project_step_id;
        lv_statement := to_char(ar_row.next_project_experiment_id)
--                    || ', ' || to_char(ar_row.STAGE_ID)
                    || ', ' || to_char(ar_row.prev_project_experiment_id)
                    || ', ' || substr(ar_row.edge_name, 1, 500);


        -- second attempt to find using the natural key (label)
        if ln_trgt_project_step_id is null
        then
            open cur_project_step_AK;
            fetch cur_project_step_AK into ln_trgt_project_step_id;
            close cur_project_step_AK;
        end if;

        if ln_trgt_project_step_id is not null
        then

        -- if it exists, update the external_system record
            update project_step set
                next_project_experiment_id = ar_row.next_project_experiment_id,
                prev_project_experiment_id = ar_row.prev_project_experiment_id,
                edge_name = ar_row.edge_name,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where project_step_id = ln_trgt_project_step_id;

           if sql%rowcount < 1
           then
              ln_trgt_project_step_id := null;
           else
              save_mapping (pv_src_schema, 'PROJECT_STEP', an_src_project_step_id, ln_trgt_project_step_id);
              log_statement('PROJECT_STEP', ln_trgt_project_step_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_project_step_id is  null
        then
        -- if not insert a new row
            select project_step_id_seq.nextval
            into ln_trgt_project_step_id
            from dual;

            insert into project_step (
                project_step_ID,
                next_project_experiment_id,
                prev_project_experiment_id,
                edge_name,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_project_step_id,
                ar_row.next_project_experiment_id,
                ar_row.prev_project_experiment_id,
                ar_row.edge_name,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping (pv_src_schema, 'PROJECT_STEP', an_src_project_step_id, ln_trgt_project_step_id);

            log_statement('PROJECT_STEP', ln_trgt_project_step_id, 'INSERT',
                'src ID ' || to_char(an_src_project_step_id)
                || ', ' || lv_statement);

        end if;

        -- and return the new project_step_id
        ano_project_step := ln_trgt_project_step_id;

    exception
        when others
        then
            if cur_project_step_AK%isopen
            then
                close cur_project_step_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_project_step');
    end save_project_step;

    function map_step_context_item
        (ar_src in r_step_context_item,
         aro_trgt out r_step_context_item)
         return boolean
    as
        ln_trgt_step_context_id    number := null;
        ln_trgt_step_context_itm_id    number;
        ln_trgt_attribute_id    number := null;
        ln_trgt_value_id    number := null;
        le_element_id_null    exception;

    begin

        if ar_src.step_context_id is not null
        then
            get_mapping_id(pv_src_schema, 'STEP_CONTEXT', ar_src.step_context_id, ln_trgt_step_context_id);
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.attribute_id, ln_trgt_attribute_id);
        if ln_trgt_attribute_id is null
        then
            raise le_element_id_null;
        end if;

        if ar_src.value_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.value_id, ln_trgt_value_id);
            if ln_trgt_value_id is null
            then
                raise le_element_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'STEP_CONTEXT_ITEM', ar_src.step_context_item_id, ln_trgt_step_context_itm_id);

        -- look in save_step_context_item for handling of the group...id
        aro_trgt := ar_src;
        aro_trgt.step_context_ITEM_ID := ln_trgt_step_context_itm_id;
        aro_trgt.step_context_ID := ln_trgt_step_context_id;
        aro_trgt.ATTRIBUTE_ID := ln_trgt_attribute_id;
        aro_trgt.VALUE_ID := ln_trgt_value_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_element_id_null
        then
            log_error(-20001, 'target Attibute or Value_id is null', 'step_context_item',
                 'source attribute_id = ' || to_char(ar_src.attribute_id)
                 || ', value_id = ' || to_char(ar_src.value_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'step_context_item');
            return false;
    end map_step_context_item;

    procedure save_step_context_item
        (ar_row in r_step_context_item,
         an_src_step_context_item_id   in  number,
         ano_step_context_item_id    out number)
    as
        ln_trgt_step_context_itm_id   number;
        lv_statement    varchar2(4000);

        cursor cur_mci_AK
        is
        select step_context_item_id
        from step_context_item
        where step_context_id = ar_row.step_context_id
          and display_order = ar_row.display_order
          and attribute_id = ar_row.attribute_id
          and nvl(value_display, '######') = nvl(ar_row.value_display, '######');

    begin
        ln_trgt_step_context_itm_id := ar_row.step_context_item_id;
        lv_statement := to_char(ar_row.DISPLAY_ORDER)
        || ', ' || to_char(ar_row.step_context_ID)
        || ', ' || to_char(ar_row.ATTRIBUTE_ID)
        || ', ' || ar_row.QUALIFIER
        || ', ' || to_char(ar_row.VALUE_ID)
        || ', ' || ar_row.EXT_VALUE_ID
        || ', ' || ar_row.VALUE_DISPLAY
        || ', ' || to_char(ar_row.VALUE_NUM)
        || ', ' || to_char(ar_row.VALUE_MIN)
        || ', ' || to_char(ar_row.VALUE_MAX);

        -- try to find the ID from the AK before inserting a new record
        if ln_trgt_step_context_itm_id is null and ar_row.display_order is not null
        then
            open cur_mci_AK;
            fetch cur_mci_AK into ln_trgt_step_context_itm_id;
            close cur_mci_AK;
        end if;

        if ln_trgt_step_context_itm_id is not null
        then

        -- if it exists, update the MEASURE record
            update step_context_item set
                DISPLAY_ORDER = ar_row.DISPLAY_ORDER,
                step_context_ID = ar_row.step_context_ID,
                ATTRIBUTE_ID = ar_row.ATTRIBUTE_ID,
                QUALIFIER = ar_row.QUALIFIER,
                VALUE_ID = ar_row.VALUE_ID,
                EXT_VALUE_ID = ar_row.EXT_VALUE_ID,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where step_context_item_id = ln_trgt_step_context_itm_id;

           if sql%rowcount < 1
           then
              ln_trgt_step_context_itm_id := null;
           else
              save_mapping(pv_src_schema, 'STEP_CONTEXT_ITEM',
                    an_src_step_context_item_id,
                    ln_trgt_step_context_itm_id);
              log_statement('STEP_CONTEXT_ITEM', ln_trgt_step_context_itm_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_step_context_itm_id is  null
        then
        -- if not insert a new row
            select step_context_item_id_seq.nextval
            into ln_trgt_step_context_itm_id
            from dual;

            insert into step_context_item (
                step_context_ITEM_ID,
                DISPLAY_ORDER,
                step_CONTEXT_ID,
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_step_context_itm_id,
                ar_row.DISPLAY_ORDER,
                ar_row.step_context_ID,
                ar_row.ATTRIBUTE_ID,
                ar_row.QUALIFIER,
                ar_row.VALUE_ID,
                ar_row.EXT_VALUE_ID,
                ar_row.VALUE_DISPLAY,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'STEP_CONTEXT_ITEM',
                    an_src_step_context_item_id,
                    ln_trgt_step_context_itm_id);

            log_statement('STEP_CONTEXT_ITEM',ln_trgt_step_context_itm_id,
                  'INSERT', lv_statement);
        end if;

        ano_step_context_item_id := ln_trgt_step_context_itm_id;

    exception
        when others
        then
            if cur_mci_AK%isopen
            then
                close cur_mci_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_step_context_item');
    end save_step_context_item;

    procedure get_step_context_item
        (an_src_step_context_id    in  number)
    as
        cur_step_context_item r_cursor;
        lr_src_step_context_item r_step_context_item;
        lr_trgt_step_context_item  r_step_context_item;
        ln_trgt_step_context_itm_id  number;

    begin
        -- the target measure rows have already been deleted

        open_src_cursor(pv_src_schema, 'step_CONTEXT_ITEM', an_src_step_context_id, cur_step_context_item);

        loop
            fetch cur_step_context_item into lr_src_step_context_item;
            exit when cur_step_context_item%notfound;

            if map_step_context_item (lr_src_step_context_item, lr_trgt_step_context_item)
            then

                save_step_context_item (lr_trgt_step_context_item, lr_src_step_context_item.step_context_item_id,
                    ln_trgt_step_context_itm_id);
            end if;
        end loop;
        close cur_step_context_item;


    exception
        when others
        then
            if cur_step_context_item%isopen
            then
                close cur_step_context_item;
            end if;

            log_error(sqlcode, sqlerrm, 'GET_step_CONTEXT_ITEM');
    end get_step_context_item;

    procedure save_step_context
        (ar_row in r_step_context,
         an_src_step_context_id   in  number,
         ano_step_context_id  out number)
    as
        ln_trgt_step_context_id   number;
        lv_statement    varchar2(4000);

        cursor cur_step_context_AK
        is
        select step_context_id
        from step_context
        where PROJECT_step_id = ar_row.project_step_id
          and context_name = ar_row.context_name;

    begin
        ln_trgt_step_context_id := ar_row.step_context_id;
        lv_statement := to_char(ar_row.project_step_ID)
        || ', ' || ar_row.CONTEXT_NAME;

       -- try finding the ID by the alternate key if it's not mapped already
        if ln_trgt_step_context_id is null
        then
            open cur_step_context_AK;
            fetch cur_step_context_AK into ln_trgt_step_context_id;
            close cur_step_context_AK;
        end if;

        if ln_trgt_step_context_id is not null
        then

        -- if it exists, update the MEASURE record
            update step_context set
                project_step_ID = ar_row.project_step_ID,
                CONTEXT_NAME = ar_row.CONTEXT_NAME,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where step_context_id = ln_trgt_step_context_id;

           if sql%rowcount < 1
           then
              ln_trgt_step_context_id := null;
           else
              save_mapping(pv_src_schema, 'STEP_CONTEXT', an_src_step_context_id, ln_trgt_step_context_id);
              log_statement('STEP_CONTEXT', ln_trgt_step_context_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_step_context_id is  null
        then
        -- if not insert a new row
            select step_context_id_seq.nextval
            into ln_trgt_step_context_id
            from dual;

            insert into step_context (
                step_context_ID,
                project_step_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_step_context_id,
                ar_row.project_step_ID,
                ar_row.CONTEXT_NAME,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'STEP_CONTEXT', an_src_step_context_id, ln_trgt_step_context_id);

            log_statement('STEP_CONTEXT',ln_trgt_step_context_id,
                  'INSERT', lv_statement);
        end if;

        ano_step_context_id := ln_trgt_step_context_id;

    exception
        when others
        then
            if cur_step_context_AK%isopen
            then
                close cur_step_context_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_step_context');
    end save_step_context;

    procedure get_step_context
        (an_src_step_id    in  number)
    as
        cur_step_context r_cursor;
        lr_src_step_context r_step_context;
        lr_trgt_step_context  r_step_context;
        ln_trgt_step_context_id  number;
        ln_trgt_step_id    number  := null;

    begin
        -- find the rows in the target
        -- and delete them
        --   but relase the child rows by making the FK columns null
        -- also find the mapping rows and delete those
        get_mapping_id(pv_src_schema, 'PROJECT_STEP', an_src_step_id, ln_trgt_step_id);
        if ln_trgt_step_id is not null
        then
            cleanout_data_mig('STEP_CONTEXT', ln_trgt_step_id, pv_src_schema);
        end if;

        open_src_cursor(pv_src_schema, 'STEP_CONTEXT', an_src_step_id, cur_step_context);

        loop
            fetch cur_step_context into lr_src_step_context;
            exit when cur_step_context%notfound;

            if map_step_context (lr_src_step_context, lr_trgt_step_context)
            then

                save_step_context (lr_trgt_step_context, lr_src_step_context.step_context_id,
                    ln_trgt_step_context_id);
            end if;

            get_step_context_item(lr_src_step_context.step_context_id);

        end loop;
        close cur_step_context;

    exception
        when others
        then
            if cur_step_context%isopen
            then
                close cur_step_context;
            end if;

            log_error(sqlcode, sqlerrm, 'get_step_context');
    end get_step_context;

    procedure get_project_step
        (an_src_experiment_id    in  number)
    as
        cur_project_step  r_cursor;
        ln_trgt_project_step_id   number;
        lr_trgt_project_step  r_project_step;
        lr_src_project_step  r_project_step;

    begin

        open_src_cursor(pv_src_schema, 'PROJECT_STEP', an_src_experiment_id, cur_project_step);

        loop
            fetch cur_project_step into lr_src_project_step;
            exit when cur_project_step%notfound;

            if map_project_step(lr_src_project_step, lr_trgt_project_step)
            then

                save_project_step (lr_trgt_project_step,
                            lr_src_project_step.project_step_id,
                            ln_trgt_project_step_id);

                get_step_context(lr_src_project_step.project_step_id);

            end if;
        end loop;

        close cur_project_step;

    exception
        when others
        then
            if cur_project_step%isopen
            then
                close cur_project_step;
            end if;
    end get_project_step;

    procedure get_external_reference
        (an_src_experiment_id    in  number)
    as
        cur_external_reference  r_cursor;
        ln_trgt_external_reference_id   number;
        lr_trgt_external_reference  r_external_reference;
        lr_src_external_reference  r_external_reference;

    begin

        open_src_cursor(pv_src_schema, 'EXTERNAL_REFERENCE', an_src_experiment_id, cur_external_reference);

        loop
            fetch cur_external_reference into lr_src_external_reference;
            exit when cur_external_reference%notfound;

            if map_external_reference(lr_src_external_reference, lr_trgt_external_reference)
            then

                save_external_reference (lr_trgt_external_reference,
                            lr_src_external_reference.external_reference_id,
                            ln_trgt_external_reference_id);
            end if;
        end loop;

        close cur_external_reference;

    exception
        when others
        then
            if cur_external_reference%isopen
            then
                close cur_external_reference;
            end if;
    end get_external_reference;

    procedure save_measure
        (ar_row in r_measure,
         an_src_measure_id   in  number,
         ano_measure_id out number)
    as
        ln_trgt_measure_id   number;
        lv_statement    varchar2(4000);

        cursor cur_measure_AK
        is
        select measure_id
        from measure
        where assay_id = ar_row.assay_id
          and result_type_id = ar_row.result_type_id
          and Nvl(stats_modifier_id, -100) = Nvl(ar_row.stats_modifier_id, -100)
          and Nvl(parent_measure_id, -200) = Nvl(ar_row.parent_measure_id, -200);

    begin
        ln_trgt_measure_id := ar_row.measure_id;
        lv_statement := to_char(ar_row.assay_ID)
        || ', ' || to_char(ar_row.PARENT_MEASURE_ID)
        || ', ' || to_char(ar_row.RESULT_TYPE_ID)
        || ', ' || to_char(ar_row.STATS_MODIFIER_ID)
        || ', ' || To_Char(ar_row.ENTRY_UNIT_ID);

        if ln_trgt_measure_id is null
            and ar_row.parent_measure_id is not null
        then
            open cur_measure_AK;
            fetch cur_measure_AK into ln_trgt_measure_id;
            close cur_measure_AK;
        end if;

        dbms_output.put_line('saving, ID=' || to_char(ln_trgt_measure_id));
        if ln_trgt_measure_id is not null
        then

        -- if it exists, update the MEASURE record
            update measure set
                ASSAY_ID = ar_row.ASSAY_ID,
                PARENT_MEASURE_ID = ar_row.PARENT_MEASURE_ID,
                RESULT_TYPE_ID = ar_row.RESULT_TYPE_ID,
                STATS_MODIFIER_ID = ar_row.STATS_MODIFIER_ID,
                ENTRY_UNIT_ID = ar_row.ENTRY_UNIT_ID,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where measure_id = ln_trgt_measure_id;

           if sql%rowcount < 1
           then
              ln_trgt_measure_id := null;
           else
              save_mapping(pv_src_schema, 'MEASURE', an_src_measure_id, ln_trgt_measure_id);
              log_statement('MEASURE', ln_trgt_measure_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_measure_id is  null
        then
        -- if not insert a new row
            select measure_id_seq.nextval
            into ln_trgt_measure_id
            from dual;
            dbms_output.put_line('inserting, ID=' || to_char(ln_trgt_measure_id));

            insert into measure (
                MEASURE_ID,
                ASSAY_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                STATS_MODIFIER_ID,
                ENTRY_UNIT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_measure_id,
                ar_row.ASSAY_ID,
                ar_row.PARENT_MEASURE_ID,
                ar_row.RESULT_TYPE_ID,
                ar_row.STATS_MODIFIER_ID,
                ar_row.ENTRY_UNIT_ID,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'MEASURE', an_src_measure_id, ln_trgt_measure_id);

            log_statement('MEASURE',ln_trgt_measure_id,
                  'INSERT', lv_statement);
        end if;

        ano_measure_id := ln_trgt_measure_id;

    exception
        when others
        then
            if cur_measure_AK%isopen
            then
                close cur_measure_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_measure');
    end save_measure;

    procedure save_assay_context_measure
        (ar_row in r_assay_context_measure,
         an_src_measure_id   in  number,
         ano_measure_id   out number)
    as
        ln_trgt_measure_id   number;
        lv_statement    varchar2(4000);

    begin
        ln_trgt_measure_id := ar_row.measure_id;
        lv_statement := to_char(ar_row.measure_ID)
                || ', ' || to_char(ar_row.assay_context_ID);

        if ln_trgt_measure_id is not NULL
        AND ar_row.assay_context_id IS NOT null
        then
            -- if not insert a new row (non duplicate !)
             insert into assay_context_measure (
                MEASURE_ID,
                ASSAY_CONTEXT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            SELECT ln_trgt_MEASURE_id,
                ar_row.ASSAY_CONTEXT_ID,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY
            FROM dual
            WHERE NOT EXISTS
                  (SELECT 1 FROM assay_context_measure acm
                   WHERE acm.measure_id = ln_trgt_measure_id
                     AND acm.assay_context_id = ar_row.assay_context_id);

                -- no point in saving a mapping -doesn't go anywhere
                --            save_mapping(pv_src_schema, 'ELEMENT_HIERARCHY',
                --                    an_src_element_hierarchy_id,
                --                    ln_trgt_element_hierarchy_id);

            log_statement('ASSAY_CONTEXT_MEASURE',ln_trgt_measure_id,
                  'INSERT', lv_statement);
        end if;

        ano_measure_id := ln_trgt_measure_id;

    exception
        when others
        then
             log_error(sqlcode, sqlerrm, 'save_ASSAY_CONTEXT_MEASURE');
    end save_ASSAY_CONTEXT_MEASURE;

    procedure get_assay_context_measure
        (an_measure_id in number default null)
    as
        cur_assay_context_measure r_cursor;
        lr_src_assay_context_measure  r_assay_context_measure;
        lr_trgt_assay_context_measure  r_assay_context_measure;
        ln_trgt_measure_id  number;

    begin
        open_src_cursor(pv_src_schema, 'ASSAY_CONTEXT_MEASURE', an_measure_id, cur_assay_context_measure);

        loop
            fetch cur_assay_context_measure into lr_src_assay_context_measure;
            exit when cur_assay_context_measure%notfound;

            if map_assay_context_measure (lr_src_assay_context_measure, lr_trgt_assay_context_measure)
            then
                 -- this is a fake!  since this tablke has a composite key it is not logged in Statement_log or identifier_mapping
                save_assay_context_measure (lr_trgt_assay_context_measure, lr_src_assay_context_measure.measure_id, ln_trgt_measure_id);
            end if;
        end loop;
        close cur_assay_context_measure;

    exception
        when others
        then
            if cur_assay_context_measure%isopen
            then
                close cur_assay_context_measure;
            end if;
            log_error(sqlcode, sqlerrm, 'get_assay_context_measure');
    end get_assay_context_measure;

    procedure get_measure
        (an_src_assay_id    in  number)
    as
        cur_measure r_cursor;
        lr_src_measure r_measure;
        lr_trgt_measure  r_measure;
        ln_trgt_measure_id  number;
        ln_trgt_assay_id    NUMBER;

    begin
        -- the target measure rows have already been deleted
        --dbms_output.put_line('arrived in get_measure, source schema=' ||pv_src_schema
        --        || ' assay_id=' || to_char(an_src_assay_id));

        get_mapping_id(pv_src_schema, 'ASSAY', an_src_assay_id, ln_trgt_assay_id);
        if ln_trgt_assay_id is not null
        then
            cleanout_data_mig('EXPRMT_MEASURE', ln_trgt_assay_id, pv_src_schema);
            -- DON'T CLEAN OUT mEASURE, THEN WE CAN UPDATE AND PREVENT DUPLICATES
        END IF;

        open_src_cursor(pv_src_schema, 'MEASURE', an_src_assay_id, cur_measure);

        loop
            fetch cur_measure into lr_src_measure;
            exit when cur_measure%notfound;
            --dbms_output.put_line('next row, ID=' || lr_src_measure.measure_id);

            if map_measure (lr_src_measure, lr_trgt_measure)
            then
                --dbms_output.put_line('row mapped, ID=' || lr_trgt_measure.measure_id);
                save_measure (lr_trgt_measure, lr_src_measure.measure_id,
                    ln_trgt_measure_id);

                get_assay_context_measure(lr_src_measure.measure_id);

            end if;
        end loop;
        close cur_measure;

    exception
        when others
        then
            if cur_measure%isopen
            then
                close cur_measure;
            end if;

            log_error(sqlcode, sqlerrm, 'get_measure');
    end get_measure;

    procedure save_assay_context_item
        (ar_row in r_assay_context_item,
         an_src_assay_context_item_id   in  number,
         ano_assay_context_item_id    out number)
    as
        ln_trgt_assay_context_itm_id   number;
        lv_statement    varchar2(4000);

        cursor cur_mci_AK
        is
        select assay_context_item_id
        from assay_context_item
        where assay_context_id = ar_row.assay_context_id
          and display_order = ar_row.display_order
          and attribute_id = ar_row.attribute_id
          and attribute_type = ar_row.attribute_type
          and nvl(value_display, '######') = nvl(ar_row.value_display, '######');

    begin
        ln_trgt_assay_context_itm_id := ar_row.assay_context_item_id;
        lv_statement := to_char(ar_row.DISPLAY_ORDER)
        || ', ' || to_char(ar_row.assay_context_ID)
        || ', ' || ar_row.ATTRIBUTE_TYPE
        || ', ' || to_char(ar_row.ATTRIBUTE_ID)
        || ', ' || ar_row.QUALIFIER
        || ', ' || to_char(ar_row.VALUE_ID)
        || ', ' || ar_row.EXT_VALUE_ID
        || ', ' || ar_row.VALUE_DISPLAY
        || ', ' || to_char(ar_row.VALUE_NUM)
        || ', ' || to_char(ar_row.VALUE_MIN)
        || ', ' || to_char(ar_row.VALUE_MAX);

        -- try to find the ID from the AK before inserting a new record
        if ln_trgt_assay_context_itm_id is null and ar_row.display_order is not null
        then
            open cur_mci_AK;
            fetch cur_mci_AK into ln_trgt_assay_context_itm_id;
            close cur_mci_AK;
        end if;

        if ln_trgt_assay_context_itm_id is not null
        then

        -- if it exists, update the MEASURE record
            update assay_context_item set
                DISPLAY_ORDER = ar_row.DISPLAY_ORDER,
                assay_context_ID = ar_row.assay_context_ID,
                ATTRIBUTE_TYPE = ar_row.ATTRIBUTE_TYPE,
                ATTRIBUTE_ID = ar_row.ATTRIBUTE_ID,
                QUALIFIER = ar_row.QUALIFIER,
                VALUE_ID = ar_row.VALUE_ID,
                EXT_VALUE_ID = ar_row.EXT_VALUE_ID,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where assay_context_item_id = ln_trgt_assay_context_itm_id;

           if sql%rowcount < 1
           then
              ln_trgt_assay_context_itm_id := null;
           else
              save_mapping(pv_src_schema, 'ASSAY_CONTEXT_ITEM',
                    an_src_assay_context_item_id,
                    ln_trgt_assay_context_itm_id);
              log_statement('ASSAY_CONTEXT_ITEM', ln_trgt_assay_context_itm_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_assay_context_itm_id is  null
        then
        -- if not insert a new row
            select assay_context_item_id_seq.nextval
            into ln_trgt_assay_context_itm_id
            from dual;

            insert into assay_context_item (
                assay_context_ITEM_ID,
                DISPLAY_ORDER,
                ASSAY_CONTEXT_ID,
                ATTRIBUTE_TYPE,
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_assay_context_itm_id,
                ar_row.DISPLAY_ORDER,
                ar_row.assay_context_ID,
                ar_row.ATTRIBUTE_TYPE,
                ar_row.ATTRIBUTE_ID,
                ar_row.QUALIFIER,
                ar_row.VALUE_ID,
                ar_row.EXT_VALUE_ID,
                ar_row.VALUE_DISPLAY,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'ASSAY_CONTEXT_ITEM',
                    an_src_assay_context_item_id,
                    ln_trgt_assay_context_itm_id);

            log_statement('ASSAY_CONTEXT_ITEM',ln_trgt_assay_context_itm_id,
                  'INSERT', lv_statement);
        end if;

        ano_assay_context_item_id := ln_trgt_assay_context_itm_id;

    exception
        when others
        then
            if cur_mci_AK%isopen
            then
                close cur_mci_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_assay_context_item');
    end save_assay_context_item;

    procedure get_assay_context_item
        (an_src_assay_context_id    in  number)
    as
        cur_assay_context_item r_cursor;
        lr_src_assay_context_item r_assay_context_item;
        lr_trgt_assay_context_item  r_assay_context_item;
        ln_trgt_assay_context_itm_id  number;

    begin
        -- the target measure rows have already been deleted

        open_src_cursor(pv_src_schema, 'ASSAY_CONTEXT_ITEM', an_src_assay_context_id, cur_assay_context_item);

        loop
            fetch cur_assay_context_item into lr_src_assay_context_item;
            exit when cur_assay_context_item%notfound;

            if map_assay_context_item (lr_src_assay_context_item, lr_trgt_assay_context_item)
            then

                save_assay_context_item (lr_trgt_assay_context_item, lr_src_assay_context_item.assay_context_item_id,
                    ln_trgt_assay_context_itm_id);
            end if;
        end loop;
        close cur_assay_context_item;


    exception
        when others
        then
            if cur_assay_context_item%isopen
            then
                close cur_assay_context_item;
            end if;

            log_error(sqlcode, sqlerrm, 'get_assay_context_item');
    end get_assay_context_item;

    procedure save_assay_context
        (ar_row in r_assay_context,
         an_src_assay_context_id   in  number,
         ano_assay_context_id  out number)
    as
        ln_trgt_assay_context_id   number;
        lv_statement    varchar2(4000);

        cursor cur_assay_context_AK
        is
        select assay_context_id
        from assay_context
        where assay_id = ar_row.assay_id
          and context_name = ar_row.context_name;

    begin
        ln_trgt_assay_context_id := ar_row.assay_context_id;
        lv_statement := to_char(ar_row.ASSAY_ID)
        || ', ' || ar_row.CONTEXT_NAME;

       -- try finding the ID by the alternate key if it's not mapped already
        if ln_trgt_assay_context_id is null
        then
            open cur_assay_context_AK;
            fetch cur_assay_context_AK into ln_trgt_assay_context_id;
            close cur_assay_context_AK;
        end if;

        if ln_trgt_assay_context_id is not null
        then

        -- if it exists, update the MEASURE record
            update assay_context set
                ASSAY_ID = ar_row.ASSAY_ID,
                CONTEXT_NAME = ar_row.CONTEXT_NAME,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where assay_context_id = ln_trgt_assay_context_id;

           if sql%rowcount < 1
           then
              ln_trgt_assay_context_id := null;
           else
              save_mapping(pv_src_schema, 'ASSAY_CONTEXT', an_src_assay_context_id, ln_trgt_assay_context_id);
              log_statement('ASSAY_CONTEXT', ln_trgt_assay_context_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_assay_context_id is  null
        then
        -- if not insert a new row
            select assay_context_id_seq.nextval
            into ln_trgt_assay_context_id
            from dual;

            insert into assay_context (
                assay_context_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_assay_context_id,
                ar_row.ASSAY_ID,
                ar_row.CONTEXT_NAME,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'ASSAY_CONTEXT', an_src_assay_context_id, ln_trgt_assay_context_id);

            log_statement('ASSAY_CONTEXT',ln_trgt_assay_context_id,
                  'INSERT', lv_statement);
        end if;

        ano_assay_context_id := ln_trgt_assay_context_id;

    exception
        when others
        then
            if cur_assay_context_AK%isopen
            then
                close cur_assay_context_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_assay_context');
    end save_assay_context;

    procedure get_assay_context
        (an_src_assay_id    in  number)
    as
        cur_assay_context r_cursor;
        lr_src_assay_context r_assay_context;
        lr_trgt_assay_context  r_assay_context;
        ln_trgt_assay_context_id  number;
        ln_trgt_assay_id    number  := null;

    begin
        -- find the rows in the target
        -- and delete them
        -- but relase the child rows by making the FK columns null
        -- also find the mapping rows and delete those
        get_mapping_id(pv_src_schema, 'ASSAY', an_src_assay_id, ln_trgt_assay_id);
        if ln_trgt_assay_id is not null
        then
            cleanout_data_mig('ASSAY_CONTEXT_ITEM', ln_trgt_assay_id, pv_src_schema);
            cleanout_data_mig('ASSAY_CONTEXT_MEASURE', ln_trgt_assay_id, pv_src_schema);
            cleanout_data_mig('ASSAY_CONTEXT', ln_trgt_assay_id, pv_src_schema);
        end if;

        open_src_cursor(pv_src_schema, 'ASSAY_CONTEXT', an_src_assay_id, cur_assay_context);

        loop
            fetch cur_assay_context into lr_src_assay_context;
            exit when cur_assay_context%notfound;

            if map_assay_context (lr_src_assay_context, lr_trgt_assay_context)
            then

                save_assay_context (lr_trgt_assay_context, lr_src_assay_context.assay_context_id,
                    ln_trgt_assay_context_id);
                get_assay_context_item(lr_src_assay_context.assay_context_id);
                -- Leave the assay_context_measure until we have the measures in place

            end if;


        end loop;
        close cur_assay_context;

     exception
        when others
        then
            if cur_assay_context%isopen
            then
                close cur_assay_context;
            end if;

            log_error(sqlcode, sqlerrm, 'get_assay_context');
    end get_assay_context;

    procedure save_result_hierarchy
        (ar_row in r_result_hierarchy,
         an_src_result_hierarchy_id   in  number,
         ano_result_hierarchy_id   out number)
    as
        ln_trgt_result_id   number;
        ln_result_id    number := null;
        lb_row_exists   boolean := false;
        lv_statement    varchar2(4000);

        cursor cur_result_hierarchy_AK
        is
        select result_id
        from result_hierarchy
        where result_id = ar_row.result_id
          and parent_result_id = ar_row.parent_result_id
          and hierarchy_type = ar_row.hierarchy_type;

    begin
        ln_trgt_result_id := ar_row.result_id;
        lv_statement := to_char(ar_row.PARENT_RESULT_ID)
                || ', ' || ar_row.HIERARCHY_TYPE;

        open cur_result_hierarchy_AK;
        fetch cur_result_hierarchy_AK into ln_result_id;
        lb_row_exists := cur_result_hierarchy_AK%found;
        close cur_result_hierarchy_AK;


        if not lb_row_exists
        then
            insert into result_hierarchy (
                RESULT_ID,
                PARENT_RESULT_ID,
                HIERARCHY_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_result_id,
                ar_row.PARENT_RESULT_ID,
                ar_row.HIERARCHY_TYPE,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

                -- no point in saving a mapping -doesn't go anywhere
                --            save_mapping(pv_src_schema, 'ELEMENT_HIERARCHY',
                --                    an_src_element_hierarchy_id,
                --                    ln_trgt_element_hierarchy_id);

                --log_statement('RESULT_HIERARCHY',ln_trgt_result_id,
                --      'INSERT', lv_statement);
        end if;

        ano_result_hierarchy_id := ln_trgt_result_id;

    exception
        when others
        then
            if cur_result_hierarchy_AK%isopen
            then
                close cur_result_hierarchy_AK;
            end if;
            log_error(sqlcode, sqlerrm, 'save_result_hierarchy');
    end save_result_hierarchy;

    procedure get_result_hierarchy
        (an_experiment_id   in  number)
    as
        -- this one is odd.
        --
        -- we get all result hierarchy for an entire experiment rather than doing it result by result
        -- this gives us fewer failures to log
        cur_result_hierarchy  r_cursor;
        ln_trgt_result_hierarchy_id   number;
        lr_trgt_result_hierarchy  r_result_hierarchy;
        lr_src_result_hierarchy  r_result_hierarchy;

    begin
        open_src_cursor(pv_src_schema, 'RESULT_HIERARCHY', an_experiment_id, cur_result_hierarchy);

        loop
            fetch cur_result_hierarchy into lr_src_result_hierarchy;
            exit when cur_result_hierarchy%notfound;

            if map_result_hierarchy (lr_src_result_hierarchy, lr_trgt_result_hierarchy)
            then

                save_result_hierarchy (lr_trgt_result_hierarchy,
                            lr_src_result_hierarchy.result_id, -- this is a trick, 'cos we're not saving a mapping
                            ln_trgt_result_hierarchy_id);
            end if;
        end loop;

        close cur_result_hierarchy;

    exception
        when others
        then
            if cur_result_hierarchy%isopen
            then
                close cur_result_hierarchy;
            end if;
            log_error(sqlcode, sqlerrm, 'get_result_hierarchy');
    end get_result_hierarchy;

    procedure save_rslt_context_item
        (ar_row in R_rslt_context_item,
         an_src_rslt_context_item_id   in  number,
         ano_rslt_context_item_id    out number)
    as
        ln_trgt_rslt_context_item_id   number;
        lv_statement    varchar2(4000);

    begin
        ln_trgt_rslt_context_item_id := ar_row.rslt_context_item_id;
        lv_statement := To_Char(ar_row.DISPLAY_ORDER)
            || ', ' || to_char(ar_row.RESULT_ID)
            || ', ' || to_char(ar_row.ATTRIBUTE_ID)
            || ', ' || to_char(ar_row.VALUE_ID)
            || ', ' || ar_row.EXT_VALUE_ID
            || ', ' || ar_row.QUALIFIER
            || ', ' || to_char(ar_row.VALUE_NUM)
            || ', ' || to_char(ar_row.VALUE_MIN)
            || ', ' || to_char(ar_row.VALUE_MAX)
            || ', ' || ar_row.VALUE_DISPLAY;

         if ln_trgt_rslt_context_item_id is not null
        then

        -- if it exists, update the rslt_context_item record
            update rslt_context_item set
                DISPLAY_ORDER = ar_row.DISPLAY_ORDER,
                RESULT_ID = ar_row.RESULT_ID,
                ATTRIBUTE_ID = ar_row.ATTRIBUTE_ID,
                VALUE_ID = ar_row.VALUE_ID,
                EXT_VALUE_ID = ar_row.EXT_VALUE_ID,
                QUALIFIER = ar_row.QUALIFIER,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.last_updated,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where rslt_context_item_id = ln_trgt_rslt_context_item_id;

           if sql%rowcount < 1
           then
              ln_trgt_rslt_context_item_id := null;
           else
              save_mapping(pv_src_schema, 'RSLT_CONTEXT_ITEM',
                    an_src_rslt_context_item_id,
                    ln_trgt_rslt_context_item_id);
              --log_statement('rslt_context_item', ln_trgt_rslt_context_item_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_rslt_context_item_id is  null
        then
        -- if not insert a new row
            select rslt_context_item_id_seq.nextval
            into ln_trgt_rslt_context_item_id
            from dual;

            insert into rslt_context_item (
                rslt_context_item_ID,
                DISPLAY_ORDER,
                RESULT_ID,
                ATTRIBUTE_ID,
                VALUE_ID,
                EXT_VALUE_ID,
                QUALIFIER,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VALUE_DISPLAY,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_rslt_context_item_id,
                ar_row.DISPLAY_ORDER,
                ar_row.RESULT_ID,
                ar_row.ATTRIBUTE_ID,
                ar_row.VALUE_ID,
                ar_row.EXT_VALUE_ID,
                ar_row.QUALIFIER,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.VALUE_DISPLAY,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'RSLT_CONTEXT_ITEM',
                    an_src_rslt_context_item_id,
                    ln_trgt_rslt_context_item_id);

            --log_statement('rslt_context_item',ln_trgt_rslt_context_item_id,
            --      'INSERT', lv_statement);
        end if;

        ano_rslt_context_item_id := ln_trgt_rslt_context_item_id;

    exception
        when others
        then
            log_error (sqlcode, sqlerrm, 'save_rslt_context_item','source rslt_context_item_id='
                    || to_char(an_src_rslt_context_item_id)
                    || ' target id=' || to_char(ln_trgt_rslt_context_item_id) );
    end save_rslt_context_item;

    procedure get_rslt_context_item
        (an_result_id   in  number,
         an_experiment_id    in  number)
    as
        cuR_rslt_context_item  r_cursor;
        ln_trgt_rslt_context_item_id   number;    -- for the return value
        lr_trgt_rslt_context_item  R_rslt_context_item;
        lr_src_rslt_context_item  R_rslt_context_item;
        le_FKs_null     exception;

    begin
--        dbms_output.put_line ('get_rslt_context_item, result_id=' ||to_char(an_result_id)
--                || ', experiment=' || to_char(an_experiment_id));
        open_src_cursor(pv_src_schema, 'RSLT_CONTEXT_ITEM', an_result_id, cuR_rslt_context_item);

        loop
            fetch cuR_rslt_context_item into lr_src_rslt_context_item;
            exit when cuR_rslt_context_item%notfound;
--            dbms_output.put_line ('get_rslt_context_item, result_id=' ||to_char(an_result_id)
--                || ', i/p experiment=' || to_char(an_experiment_id)
--                || ', QA experiment=' || to_char(lr_src_rslt_context_item.experiment_id)
--                || ', QA result=' || to_char(lr_src_rslt_context_item.result_id)
--                || ', modified_by=' || lr_src_rslt_context_item.modified_by);

            if map_rslt_context_item (lr_src_rslt_context_item, lr_trgt_rslt_context_item)
            then

                save_rslt_context_item (lr_trgt_rslt_context_item,
                            lr_src_rslt_context_item.rslt_context_item_id,
                            ln_trgt_rslt_context_item_id);
            end if;
        end loop;

        close cuR_rslt_context_item;

    exception
        when le_FKs_null
        then
            if cuR_rslt_context_item%isopen
            then
                close cuR_rslt_context_item;
            end if;
            log_error(-20001, 'both FK (exprt and rslt) are null', 'get_rslt_context_item',
                'src ID='|| to_char(lr_src_rslt_context_item.rslt_context_item_id));
        when others
        then
            if cuR_rslt_context_item%isopen
            then
                close cuR_rslt_context_item;
            end if;
            log_error(sqlcode, sqlerrm, 'get_rslt_context_item');
    end get_rslt_context_item;

    procedure save_result
        (ar_row in r_result,
         an_src_result_id   in  number,
         ano_result_id  out     number)
    as
        ln_trgt_result_id   number;
        lv_statement    varchar2(4000);

    begin
        ln_trgt_result_id := ar_row.result_id;
        lv_statement := ar_row.RESULT_STATUS
        || ', ' || ar_row.READY_FOR_EXTRACTION
        || ', ' || ar_row.VALUE_DISPLAY
        || ', ' || to_char(ar_row.VALUE_NUM)
        || ', ' || to_char(ar_row.VALUE_MIN)
        || ', ' || to_char(ar_row.VALUE_MAX)
        || ', ' || ar_row.QUALIFIER
        || ', ' || to_char(ar_row.EXPERIMENT_ID)
        || ', ' || to_char(ar_row.SUBSTANCE_ID)
        || ', ' || to_char(ar_row.RESULT_TYPE_ID);

         if ln_trgt_result_id is not null
        then

        -- if it exists, update the assay_document record
            update result set
                RESULT_STATUS = ar_row.RESULT_STATUS,
                READY_FOR_EXTRACTION = ar_row.READY_FOR_EXTRACTION,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                QUALIFIER = ar_row.QUALIFIER,
                EXPERIMENT_ID = ar_row.EXPERIMENT_ID,
                SUBSTANCE_ID = ar_row.SUBSTANCE_ID,
                RESULT_TYPE_ID = ar_row.RESULT_TYPE_ID,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.last_updated,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where result_id = ln_trgt_result_id;

           if sql%rowcount < 1
           then
              ln_trgt_result_id := null;
           else
              save_mapping(pv_src_schema, 'RESULT', an_src_result_id, ln_trgt_result_id);
              --log_statement('RESULT', ln_trgt_result_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_result_id is  null
        then
        -- if not insert a new row
            select result_id_seq.nextval
            into ln_trgt_result_id
            from dual;

            insert into result (
                RESULT_ID,
                RESULT_STATUS,
                READY_FOR_EXTRACTION,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                QUALIFIER,
                EXPERIMENT_ID,
                SUBSTANCE_ID,
                RESULT_TYPE_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_result_id,
                ar_row.RESULT_STATUS,
                ar_row.READY_FOR_EXTRACTION,
                ar_row.VALUE_DISPLAY,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.QUALIFIER,
                ar_row.EXPERIMENT_ID,
                ar_row.SUBSTANCE_ID,
                ar_row.RESULT_TYPE_ID,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'RESULT', an_src_result_id, ln_trgt_result_id);

            --log_statement('RESULT',ln_trgt_result_id,
            --     'INSERT', lv_statement);
        end if;

        ano_result_id := ln_trgt_result_id;

    exception
        when others
        then
            log_error (sqlcode, sqlerrm, 'save_result','source result_id='
                    || to_char(an_src_result_id)
                    || ' target id=' || to_char(ln_trgt_result_id) );
    end save_result;

    procedure get_result
        (an_experiment_id    in  number)
    as
        cur_result  r_cursor;
        ln_trgt_result_id   number;
        lr_trgt_result  r_result;
        lr_src_result  r_result;

    begin
        open_src_cursor(pv_src_schema, 'RESULT', an_experiment_id, cur_result);

        loop
            fetch cur_result into lr_src_result;
            exit when cur_result%notfound;

            if map_result(lr_src_result, lr_trgt_result)
            then

                save_result (lr_trgt_result,
                            lr_src_result.result_id,
                            ln_trgt_result_id);

                get_rslt_context_item(lr_src_result.result_id, lr_src_result.experiment_id);

                -- getting result_hierarchy here will fail 50% of the time as it attempts
                -- to add rows where only one parent exists.
                -- better to do it after the loop
            end if;
        end loop;
        close cur_result;

        get_result_hierarchy(lr_src_result.experiment_id);

    exception
        when others
        then
            if cur_result%isopen
            then
                close cur_result;
            end if;
            log_error(sqlcode, sqlerrm, 'get_result');
    end get_result;

    procedure save_experiment
        (ar_row in r_experiment,
         an_src_experiment_id    in  number,
         ano_experiment_id  out number)
    as
        ln_trgt_experiment_id  number;
        lv_src_modified_by   varchar2(40);
        lv_statement    varchar2(4000);
    begin

        ln_trgt_experiment_id := ar_row.experiment_id;
        if length(ar_row.experiment_name) > 497
        then
            lv_statement := to_char(ar_row.assay_id)
            || ', ' || ar_row.experiment_status
            || ', ' || substr(ar_row.experiment_name, 1, 497)
            || '..., ' || to_char(ar_row.run_date_from, 'MM/DD/YYYY')
            || ', ' || to_char(ar_row.run_date_to, 'MM/DD/YYYY')
            || ', ' || to_char(ar_row.hold_until_date, 'MM/DD/YYYY');
        else
            lv_statement := to_char(ar_row.assay_id)
            || ', ' || ar_row.experiment_status
            || ', ' || ar_row.experiment_name
            || ', ' || to_char(ar_row.run_date_from, 'MM/DD/YYYY')
            || ', ' || to_char(ar_row.run_date_to, 'MM/DD/YYYY')
            || ', ' || to_char(ar_row.hold_until_date, 'MM/DD/YYYY');
        end if;

        if length (ar_row.description) > 497
        then
            lv_statement := lv_statement
            || ', ' || substr(ar_row.description, 1, 497)
            || '..., ' || ar_row.ready_for_extraction;
        else
            lv_statement := lv_statement
            || ', ' || ar_row.description
            || ', ' || ar_row.ready_for_extraction;
        end if;


        if ln_trgt_experiment_id is not null
        then
        -- see if the AID# already exists

        -- if it exists, update the assay record
            update experiment set
                EXPERIMENT_STATUS = ar_row.EXPERIMENT_STATUS,
                EXPERIMENT_NAME = ar_row.EXPERIMENT_NAME,
                ASSAY_ID = ar_row.ASSAY_ID,
--                LABORATORY_ID = ar_row.LABORATORY_ID,
                RUN_DATE_FROM = ar_row.RUN_DATE_FROM,
                RUN_DATE_TO = ar_row.RUN_DATE_TO,
                HOLD_UNTIL_DATE = ar_row.HOLD_UNTIL_DATE,
                DESCRIPTION = ar_row.DESCRIPTION,
                READY_FOR_EXTRACTION = ar_row.READY_FOR_EXTRACTION,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where experiment_id = ln_trgt_experiment_id;

           if sql%rowcount < 1
           then
              ln_trgt_experiment_id := null;
           else
              save_mapping (pv_src_schema, 'EXPERIMENT', an_src_experiment_id, ln_trgt_experiment_id);
              log_statement('EXPERIMENT', ln_trgt_experiment_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_experiment_id is  null
        then
        -- if not insert a new row
            select experiment_id_seq.nextval
            into ln_trgt_experiment_id
            from dual;

            insert into experiment (
                EXPERIMENT_ID,
                EXPERIMENT_STATUS,
                EXPERIMENT_NAME,
                ASSAY_ID,
--                LABORATORY_ID,
                RUN_DATE_FROM,
                RUN_DATE_TO,
                HOLD_UNTIL_DATE,
                DESCRIPTION,
                READY_FOR_EXTRACTION,
                MODIFIED_BY
            ) values (
                ln_trgt_experiment_id,
                ar_row.EXPERIMENT_STATUS,
                ar_row.EXPERIMENT_NAME,
                ar_row.ASSAY_ID,
--                ar_row.LABORATORY_ID,
                ar_row.RUN_DATE_FROM,
                ar_row.RUN_DATE_TO,
                ar_row.HOLD_UNTIL_DATE,
                ar_row.DESCRIPTION,
                ar_row.READY_FOR_EXTRACTION,
                ar_row.MODIFIED_BY );

            save_mapping (pv_src_schema, 'EXPERIMENT', an_src_experiment_id, ln_trgt_experiment_id);
            log_statement('EXPERIMENT', ln_trgt_experiment_id, 'INSERT',
                'src ID ' || an_src_experiment_id
                || ', ' || lv_statement);

        end if;

        -- and return the new assay_id
        ano_experiment_id := ln_trgt_experiment_id;


    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'save_experiment');
    end save_experiment;

    function map_exprmt_context_item
        (ar_src in r_exprmt_context_item,
         aro_trgt out r_exprmt_context_item)
         return boolean
    as
        ln_trgt_exprmt_context_id    number := null;
        ln_trgt_exprmt_context_itm_id    number;
        ln_trgt_attribute_id    number := null;
        ln_trgt_value_id    number := null;
        le_element_id_null    exception;

    begin

        if ar_src.exprmt_context_id is not null
        then
            get_mapping_id(pv_src_schema, 'EXPRMT_CONTEXT', ar_src.exprmt_context_id, ln_trgt_exprmt_context_id);
        end if;

        get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.attribute_id, ln_trgt_attribute_id);
        if ln_trgt_attribute_id is null
        then
            raise le_element_id_null;
        end if;

        if ar_src.value_id is not null
        then
            get_mapping_id(pv_src_schema, 'ELEMENT', ar_src.value_id, ln_trgt_value_id);
            if ln_trgt_value_id is null
            then
                raise le_element_id_null;
            end if;
        end if;

        get_mapping_id(pv_src_schema, 'EXPRMT_CONTEXT_ITEM', ar_src.exprmt_context_item_id, ln_trgt_exprmt_context_itm_id);

        -- look in save_exprmt_context_item for handling of the group...id
        aro_trgt := ar_src;
        aro_trgt.exprmt_context_ITEM_ID := ln_trgt_exprmt_context_itm_id;
        aro_trgt.exprmt_context_ID := ln_trgt_exprmt_context_id;
        aro_trgt.ATTRIBUTE_ID := ln_trgt_attribute_id;
        aro_trgt.VALUE_ID := ln_trgt_value_id;
        aro_trgt.LAST_UPDATED := nvl(ar_src.LAST_UPDATED, sysdate);
        aro_trgt.MODIFIED_BY := nvl(ar_src.MODIFIED_BY, pv_src_schema);
        return true;

    exception
        when le_element_id_null
        then
            log_error(-20001, 'target Attibute or Value_id is null', 'exprmt_context_item',
                 'source attribute_id = ' || to_char(ar_src.attribute_id)
                 || ', value_id = ' || to_char(ar_src.value_id));
            return false;
        when others
        then
            log_error(sqlcode, sqlerrm, 'exprmt_context_item');
            return false;
    end map_exprmt_context_item;

    procedure save_exprmt_context_item
        (ar_row in r_exprmt_context_item,
         an_src_exprmt_context_item_id   in  number,
         ano_exprmt_context_item_id    out number)
    as
        ln_trgt_exprmt_context_itm_id   number;
        lv_statement    varchar2(4000);

        cursor cur_mci_AK
        is
        select exprmt_context_item_id
        from exprmt_context_item
        where exprmt_context_id = ar_row.exprmt_context_id
          and display_order = ar_row.display_order
          and attribute_id = ar_row.attribute_id
          and nvl(value_display, '######') = nvl(ar_row.value_display, '######');

    begin
        ln_trgt_exprmt_context_itm_id := ar_row.exprmt_context_item_id;
        lv_statement := to_char(ar_row.DISPLAY_ORDER)
        || ', ' || to_char(ar_row.exprmt_context_ID)
        || ', ' || to_char(ar_row.ATTRIBUTE_ID)
        || ', ' || ar_row.QUALIFIER
        || ', ' || to_char(ar_row.VALUE_ID)
        || ', ' || ar_row.EXT_VALUE_ID
        || ', ' || ar_row.VALUE_DISPLAY
        || ', ' || to_char(ar_row.VALUE_NUM)
        || ', ' || to_char(ar_row.VALUE_MIN)
        || ', ' || to_char(ar_row.VALUE_MAX);

        -- try to find the ID from the AK before inserting a new record
        if ln_trgt_exprmt_context_itm_id is null and ar_row.display_order is not null
        then
            open cur_mci_AK;
            fetch cur_mci_AK into ln_trgt_exprmt_context_itm_id;
            close cur_mci_AK;
        end if;

        if ln_trgt_exprmt_context_itm_id is not null
        then

        -- if it exists, update the MEASURE record
            update exprmt_context_item set
                DISPLAY_ORDER = ar_row.DISPLAY_ORDER,
                exprmt_context_ID = ar_row.exprmt_context_ID,
                ATTRIBUTE_ID = ar_row.ATTRIBUTE_ID,
                QUALIFIER = ar_row.QUALIFIER,
                VALUE_ID = ar_row.VALUE_ID,
                EXT_VALUE_ID = ar_row.EXT_VALUE_ID,
                VALUE_DISPLAY = ar_row.VALUE_DISPLAY,
                VALUE_NUM = ar_row.VALUE_NUM,
                VALUE_MIN = ar_row.VALUE_MIN,
                VALUE_MAX = ar_row.VALUE_MAX,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where exprmt_context_item_id = ln_trgt_exprmt_context_itm_id;

           if sql%rowcount < 1
           then
              ln_trgt_exprmt_context_itm_id := null;
           else
              save_mapping(pv_src_schema, 'EXPRMT_CONTEXT_ITEM',
                    an_src_exprmt_context_item_id,
                    ln_trgt_exprmt_context_itm_id);
              log_statement('EXPRMT_CONTEXT_ITEM', ln_trgt_exprmt_context_itm_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_exprmt_context_itm_id is  null
        then
        -- if not insert a new row
            select exprmt_context_item_id_seq.nextval
            into ln_trgt_exprmt_context_itm_id
            from dual;

            insert into exprmt_context_item (
                exprmt_context_ITEM_ID,
                DISPLAY_ORDER,
                exprmt_CONTEXT_ID,
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_exprmt_context_itm_id,
                ar_row.DISPLAY_ORDER,
                ar_row.exprmt_context_ID,
                ar_row.ATTRIBUTE_ID,
                ar_row.QUALIFIER,
                ar_row.VALUE_ID,
                ar_row.EXT_VALUE_ID,
                ar_row.VALUE_DISPLAY,
                ar_row.VALUE_NUM,
                ar_row.VALUE_MIN,
                ar_row.VALUE_MAX,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'EXPRMT_CONTEXT_ITEM',
                    an_src_exprmt_context_item_id,
                    ln_trgt_exprmt_context_itm_id);

            log_statement('EXPRMT_CONTEXT_ITEM',ln_trgt_exprmt_context_itm_id,
                  'INSERT', lv_statement);
        end if;

        ano_exprmt_context_item_id := ln_trgt_exprmt_context_itm_id;

    exception
        when others
        then
            if cur_mci_AK%isopen
            then
                close cur_mci_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_exprmt_context_item');
    end save_exprmt_context_item;

    procedure get_exprmt_context_item
        (an_src_exprmt_context_id    in  number)
    as
        cur_exprmt_context_item r_cursor;
        lr_src_exprmt_context_item r_exprmt_context_item;
        lr_trgt_exprmt_context_item  r_exprmt_context_item;
        ln_trgt_exprmt_context_itm_id  number;

    begin
        -- the target measure rows have already been deleted

        open_src_cursor(pv_src_schema, 'EXPRMT_CONTEXT_ITEM', an_src_exprmt_context_id, cur_exprmt_context_item);

        loop
            fetch cur_exprmt_context_item into lr_src_exprmt_context_item;
            exit when cur_exprmt_context_item%notfound;

            if map_exprmt_context_item (lr_src_exprmt_context_item, lr_trgt_exprmt_context_item)
            then

                save_exprmt_context_item (lr_trgt_exprmt_context_item, lr_src_exprmt_context_item.exprmt_context_item_id,
                    ln_trgt_exprmt_context_itm_id);
            end if;
        end loop;
        close cur_exprmt_context_item;


    exception
        when others
        then
            if cur_exprmt_context_item%isopen
            then
                close cur_exprmt_context_item;
            end if;

            log_error(sqlcode, sqlerrm, 'GET_EXPRMT_CONTEXT_ITEM');
    end get_exprmt_context_item;

    procedure save_exprmt_context
        (ar_row in r_exprmt_context,
         an_src_exprmt_context_id   in  number,
         ano_exprmt_context_id  out number)
    as
        ln_trgt_exprmt_context_id   number;
        lv_statement    varchar2(4000);

        cursor cur_exprmt_context_AK
        is
        select exprmt_context_id
        from exprmt_context
        where experiment_id = ar_row.experiment_id
          and context_name = ar_row.context_name;

    begin
        ln_trgt_exprmt_context_id := ar_row.exprmt_context_id;
        lv_statement := to_char(ar_row.experiment_ID)
        || ', ' || ar_row.CONTEXT_NAME;

       -- try finding the ID by the alternate key if it's not mapped already
        if ln_trgt_exprmt_context_id is null
        then
            open cur_exprmt_context_AK;
            fetch cur_exprmt_context_AK into ln_trgt_exprmt_context_id;
            close cur_exprmt_context_AK;
        end if;

        if ln_trgt_exprmt_context_id is not null
        then

        -- if it exists, update the MEASURE record
            update exprmt_context set
                EXPERIMENT_ID = ar_row.EXPERIMENT_ID,
                CONTEXT_NAME = ar_row.CONTEXT_NAME,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where exprmt_context_id = ln_trgt_exprmt_context_id;

           if sql%rowcount < 1
           then
              ln_trgt_exprmt_context_id := null;
           else
              save_mapping(pv_src_schema, 'EXPRMT_CONTEXT', an_src_exprmt_context_id, ln_trgt_exprmt_context_id);
              log_statement('EXPRMT_CONTEXT', ln_trgt_exprmt_context_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_exprmt_context_id is  null
        then
        -- if not insert a new row
            select exprmt_context_id_seq.nextval
            into ln_trgt_exprmt_context_id
            from dual;

            insert into exprmt_context (
                exprmt_context_ID,
                EXPERIMENT_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_exprmt_context_id,
                ar_row.EXPERIMENT_ID,
                ar_row.CONTEXT_NAME,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'EXPRMT_CONTEXT', an_src_exprmt_context_id, ln_trgt_exprmt_context_id);

            log_statement('EXPRMT_CONTEXT',ln_trgt_exprmt_context_id,
                  'INSERT', lv_statement);
        end if;

        ano_exprmt_context_id := ln_trgt_exprmt_context_id;

    exception
        when others
        then
            if cur_exprmt_context_AK%isopen
            then
                close cur_exprmt_context_AK;
            end if;
            log_error (sqlcode, sqlerrm, 'save_exprmt_context');
    end save_exprmt_context;

    procedure get_exprmt_context
        (an_src_exprmt_id    in  number)
    as
        cur_exprmt_context r_cursor;
        lr_src_exprmt_context r_exprmt_context;
        lr_trgt_exprmt_context  r_exprmt_context;
        ln_trgt_exprmt_context_id  number;
        ln_trgt_exprmt_id    number  := null;

    begin
        -- find the rows in the target
        -- and delete them
        --   but relase the child rows by making the FK columns null
        -- also find the mapping rows and delete those
        get_mapping_id(pv_src_schema, 'EXPERIMENT', an_src_exprmt_id, ln_trgt_exprmt_id);
        if ln_trgt_exprmt_id is not null
        then
            cleanout_data_mig('EXPRMT_CONTEXT', ln_trgt_exprmt_id, pv_src_schema);
        end if;

        open_src_cursor(pv_src_schema, 'EXPRMT_CONTEXT', an_src_exprmt_id, cur_exprmt_context);

        loop
            fetch cur_exprmt_context into lr_src_exprmt_context;
            exit when cur_exprmt_context%notfound;

            if map_exprmt_context (lr_src_exprmt_context, lr_trgt_exprmt_context)
            then

                save_exprmt_context (lr_trgt_exprmt_context, lr_src_exprmt_context.exprmt_context_id,
                    ln_trgt_exprmt_context_id);
            end if;

            get_exprmt_context_item(lr_src_exprmt_context.exprmt_context_id);

        end loop;
        close cur_exprmt_context;

    exception
        when others
        then
            if cur_exprmt_context%isopen
            then
                close cur_exprmt_context;
            end if;

            log_error(sqlcode, sqlerrm, 'get_exprmt_context');
    end get_exprmt_context;

    procedure get_prjct_exprmt_context
        (an_src_project_experiment_id    in  number)
    as
        cur_prjct_exprmt_context      r_cursor;
        lr_src_prjct_exprmt_context   r_prjct_exprmt_context;
        lr_trgt_prjct_exprmt_context  r_prjct_exprmt_context;
        ln_trgt_prjct_exprmt_context_id  number;
        ln_trgt_project_experiment_id    number  := null;

    begin
        -- find the rows in the target
        -- and delete them
        -- but relase the child rows by making the FK columns null
        -- also find the mapping rows and delete those
        get_mapping_id(pv_src_schema, 'PROJECT_EXPERIMENT', an_src_project_experiment_id, ln_trgt_project_experiment_id);
        if ln_trgt_project_experiment_id is not null
        then
            cleanout_data_mig('PRJCT_EXPRMT_CONTEXT_ITEM', ln_trgt_project_experiment_id, pv_src_schema);
        end if;

        open_src_cursor(pv_src_schema, 'PRJCT_EXPRMT_CONTEXT', an_src_project_experiment_id, cur_assay_context);

        loop
            fetch cur_assay_context into lr_src_assay_context;
            exit when cur_assay_context%notfound;

            if map_assay_context (lr_src_assay_context, lr_trgt_assay_context)
            then

                save_assay_context (lr_trgt_assay_context, lr_src_assay_context.assay_context_id,
                    ln_trgt_assay_context_id);
                get_assay_context_item(lr_src_assay_context.assay_context_id);
                -- Leave the assay_context_measure until we have the measures in place

            end if;


        end loop;
        close cur_assay_context;

     exception
        when others
        then
            if cur_assay_context%isopen
            then
                close cur_assay_context;
            end if;

            log_error(sqlcode, sqlerrm, 'get_assay_context');
    end get_prjct_exprmt_context;

    procedure save_project_experiment
        (ar_row in r_project_experiment,
         an_src_project_experiment_id   in  number,
         ano_project_experiment_id   out number)
    as
        ln_trgt_project_experiment_id   number;
        lv_statement    varchar2(4000);

        CURSOR cur_project_experiment_AK
        IS
        SELECT project_experiment_id
        FROM project_experiment
        WHERE project_id = ar_row.project_id
        AND experiment_id = ar_row.experiment_id;


    begin
        ln_trgt_project_experiment_id := ar_row.project_experiment_id;
        lv_statement := to_char(ar_row.project_experiment_ID)
                || ', ' || to_char(ar_row.project_ID)
                || ', ' || to_char(ar_row.experiment_ID)
                || ', ' || to_char(ar_row.stage_ID);

        if ln_trgt_project_experiment_id is NOT NULL
        THEN
             OPEN cur_project_experiment_ak;
             FETCH cur_project_experiment_ak INTO ln_trgt_project_experiment_id;
             CLOSE cur_project_experiment_ak;
        END IF;

        if ln_trgt_project_experiment_id is NOT NULL
        THEN
        -- if it exists, update the record
            update project_experiment set
                EXPERIMENT_ID = ar_row.EXPERIMENT_ID,
                project_id = ar_row.PROJECT_ID,
                STAGE_id = ar_row.STAGE_ID,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.LAST_UPDATED,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where project_experiment_id = ln_trgt_project_experiment_id;

           if sql%rowcount < 1
           then
              ln_trgt_project_experiment_id := null;
           else
              save_mapping(pv_src_schema, 'PROJECT_EXPERIMENT', an_src_project_experiment_id, ln_trgt_project_experiment_id);
              log_statement('PROJECT_EXPERIMENT', ln_trgt_project_experiment_id, 'UPDATE', lv_statement);
           end if;

        end if;

        IF ln_trgt_project_experiment_id is NULL    -- if not insert a new row (non duplicate !)
        THEN

            select project_experiment_id_seq.nextval
            into ln_trgt_project_experiment_id
            from dual;


             insert into project_experiment (
                PROJECT_EXPERIMENT_ID,
                EXPERIMENT_ID,
                PROJECT_ID,
                STAGE_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            SELECT ln_trgt_project_experiment_id,
                ar_row.EXPERIMENT_ID,
                ar_row.PROJECT_ID,
                ar_row.STAGE_ID,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY
            FROM dual
            WHERE NOT EXISTS
                  (SELECT 1 FROM project_experiment pe
                   WHERE pe.project_id = ar_row.project_id
                     AND pe.experiment_id = ar_row.experiment_id);

            save_mapping(pv_src_schema, 'PROJECT_EXPERIMENT',
                    an_src_project_experiment_id,
                    ln_trgt_project_experiment_id);

            log_statement('PROJECT_EXPERIMENT',ln_trgt_project_experiment_id,
                  'INSERT', lv_statement);
        end if;

        ano_project_experiment_id := ln_trgt_project_experiment_id;

    exception
        when others
        then
             log_error(sqlcode, sqlerrm, 'save_PROJECT_EXPERIMENT');
    end save_PROJECT_EXPERIMENT;

    procedure get_project_experiment
        (an_src_experiment_id    in  number)
    as
        cur_project_experiment  r_cursor;
        ln_trgt_project_experiment_id   number;
        ln_trgt_project_id   number;
        lr_trgt_project_experiment  r_project_experiment;
        lr_src_project_experiment  r_project_experiment;

    begin

        open_src_cursor(pv_src_schema, 'PROJECT_EXPERIMENT', an_src_experiment_id, cur_project_experiment);

        loop
            fetch cur_project_experiment into lr_src_project_experiment;
            exit when cur_project_experiment%notfound;

            -- if the project does not exist we need to insert it
            get_mapping_id(pv_src_schema, 'PROJECT', lr_src_project_experiment.PROJECT_ID, ln_trgt_project_id);
            if ln_trgt_project_id is null
            then
                get_project(lr_src_project_experiment.PROJECT_ID);
            end if;

            if map_project_experiment(lr_src_project_experiment, lr_trgt_project_experiment)
            then

                save_project_experiment (lr_trgt_project_experiment,
                            lr_src_project_experiment.project_experiment_id,
                            ln_trgt_project_experiment_id);

                get_prjct_exprmt_context(lr_src_project_experiment.project_experiment_id);

            end if;
        end loop;

        close cur_project_experiment;

    exception
        when others
        then
            if cur_project_experiment%isopen
            then
                close cur_project_experiment;
            end if;
    end get_project_experiment;



    procedure get_experiment
        (an_src_assay_id    in  number)
    as
        cur_experiment   r_cursor;
        lr_src_experiment    r_experiment;
        lr_trgt_experiment    r_experiment;
        ln_trgt_experiment_id number;

    begin
        -- find all the src experiments
        open_src_cursor(pv_src_schema, 'EXPERIMENT', an_src_assay_id, cur_experiment);

        --  using the mapping table cycle through them.  for each experiment go down to the next level (result, result_contect_item)
        loop
            fetch cur_experiment into lr_src_experiment;
            exit when cur_experiment%notfound;

            if map_experiment(lr_src_experiment, lr_trgt_experiment)
            then
                save_experiment(lr_trgt_experiment, lr_src_experiment.experiment_id, ln_trgt_experiment_id);

                get_project_experiment(lr_src_experiment.experiment_id);
                get_project_step(lr_src_experiment.experiment_id);
                get_external_reference(lr_src_experiment.experiment_id);

                cleanout_data_mig ('RESULT', ln_trgt_experiment_id, pv_src_schema);
                cleanout_data_mig ('EXPRMT_CONTEXT_ITEM', ln_trgt_experiment_id, pv_src_schema);
                cleanout_data_mig ('EXPRMT_CONTEXT', ln_trgt_experiment_id, pv_src_schema);

                get_exprmt_context(lr_src_experiment.experiment_id);
                get_result(lr_src_experiment.experiment_id);

            end if;
        end loop;
        close cur_experiment;
    exception
        when others
        then
            if cur_experiment%isopen
            then
                close cur_experiment;
            end if;
            log_error(sqlcode, sqlerrm, 'get_experiment');
    end get_experiment;

    procedure save_assay_document
        (ar_row in r_assay_document,
         an_src_assay_document_id    in  number,
         ano_assay_document_id out number)
    as
        ln_trgt_assay_document_id   number;
        lv_statement    varchar2(4000);

    begin
        ln_trgt_assay_document_id := ar_row.assay_document_id;
        if length(ar_row.document_name) > 497
        then
            lv_statement := to_char(ar_row.assay_ID)
            || ', ' || substr(ar_row.document_name, 1, 497)
            || '..., ' || ar_row.document_type;
        else
            lv_statement := to_char(ar_row.assay_ID)
            || ', ' || ar_row.document_name
            || ', ' || ar_row.document_type;
        end if;

        if length(ar_row.document_content) > 997 - length(lv_statement)
        then
            lv_statement := lv_statement
            || ', ' || substr(ar_row.document_content, 1, 997 - length(lv_statement))
            || '...' ;
        else
            lv_statement := lv_statement
            || ', ' || ar_row.document_content;
        end if;

        if ln_trgt_assay_document_id is not null
        then

        -- if it exists, update the assay_document record
            update assay_document set
                ASSAY_ID = ar_row.ASSAY_ID,
                DOCUMENT_NAME = ar_row.DOCUMENT_NAME,
                DOCUMENT_TYPE = ar_row.DOCUMENT_TYPE,
                DOCUMENT_CONTENT = ar_row.DOCUMENT_CONTENT,
                VERSION = ar_row.VERSION,
                DATE_CREATED = ar_row.DATE_CREATED,
                LAST_UPDATED = ar_row.last_updated,
                MODIFIED_BY = ar_row.MODIFIED_BY
            where assay_document_id = ln_trgt_assay_document_id;

           if sql%rowcount < 1
           then
              ln_trgt_assay_document_id := null;
           else
              save_mapping(pv_src_schema, 'ASSAY_DOCUMENT',
                    an_src_assay_document_id,
                    ln_trgt_assay_document_id);
              log_statement('ASSAY_DOCUMENT', ln_trgt_assay_document_id, 'UPDATE', lv_statement);
           end if;

        end if;

        if ln_trgt_assay_document_id is  null
        then
        -- if not insert a new row
            select assay_document_id_seq.nextval
            into ln_trgt_assay_document_id
            from dual;

            insert into assay_document (
                ASSAY_DOCUMENT_ID,
                ASSAY_ID,
                DOCUMENT_NAME,
                DOCUMENT_TYPE,
                DOCUMENT_CONTENT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            ) values (
                ln_trgt_assay_document_id,
                ar_row.ASSAY_ID,
                ar_row.DOCUMENT_NAME,
                ar_row.DOCUMENT_TYPE,
                ar_row.DOCUMENT_CONTENT,
                ar_row.VERSION,
                ar_row.DATE_CREATED,
                ar_row.LAST_UPDATED,
                ar_row.MODIFIED_BY);

            save_mapping(pv_src_schema, 'ASSAY_DOCUMENT',
                    an_src_assay_document_id,
                    ln_trgt_assay_document_id);

            log_statement('ASSAY_DOCUMENT',ln_trgt_assay_document_id,
                  'INSERT', lv_statement);
        end if;

        ano_assay_document_id := ln_trgt_assay_document_id;

    exception
        when others
        then
            log_error (sqlcode, sqlerrm, 'save_assay_document','source assay_document_id='
                    || to_char(an_src_assay_document_id)
                    || ' target id=' || to_char(ln_trgt_assay_document_id) );
    end save_assay_document;

    procedure get_assay_document
        (an_src_assay_id    in  number)
    as
        cur_assay_document  r_cursor;

        lr_src_assay_document   r_assay_document;
        lr_trgt_assay_document   r_assay_document;
        ln_trgt_assay_document_id   number;

    begin

        open_src_cursor(pv_src_schema, 'ASSAY_DOCUMENT', an_src_assay_id, cur_assay_document);

        loop
            fetch cur_assay_document into lr_src_assay_document;
            exit when cur_assay_document%notfound;

            if map_assay_document(lr_src_assay_document, lr_trgt_assay_document)
            then

                save_assay_document (lr_trgt_assay_document,
                            lr_src_assay_document.assay_document_id,
                            ln_trgt_assay_document_id);
            end if;
        end loop;

        close cur_assay_document;
    exception
        when others
        then
            if cur_assay_document%isopen
            then
                close cur_assay_document;
            end if;
    end get_assay_document;

    procedure get_assay
        (an_assay_id    in  number)
    as
    cur_assay   r_cursor;
    lr_src_assay    r_assay;
    lr_trgt_assay   r_assay;
    ln_assay_id     number;


    begin
        -- now loop thru each assay and walk down the relational integrity tree
        open_src_cursor(pv_src_schema, 'ASSAY', an_assay_id, cur_assay);

        loop
            fetch cur_assay into lr_src_assay;
            exit when cur_assay%notfound;
            log_statement('timer', 0, 'START',
                    'Assay '|| to_char(lr_src_assay.assay_id));
            commit;

            if map_assay(lr_src_assay, lr_trgt_assay)
            then
                save_assay (lr_trgt_assay, lr_src_assay.assay_id, ln_assay_id);

                get_assay_document(lr_src_assay.assay_id);
                get_assay_context(lr_src_assay.assay_id);
                get_measure(lr_src_assay.assay_id);
                -- This will work back up the tree to get the project related information for this assay
                get_experiment(lr_src_assay.assay_id);

                log_statement('timer', 0, 'END',
                   'Assay '|| to_char(lr_src_assay.assay_id));
                -- to prevent blowing out the rollback segments
                commit;     -- for each assay (with results an' all)
            end if;

        end loop;

        close cur_assay;

    exception
        when others
        then
            if cur_assay%isopen
            then
                close cur_assay;
            end if;
            null;
    end get_assay;

    procedure merge_migrate
        (av_source_schema   in   varchar2,
         an_assay_id    in number default null,
         ab_load_ref_data   in  boolean default true)
    as

    begin
        -- put this into a package level variable so it's available all over
        -- saves lots of parameter passing!!
        pv_src_schema := lower (av_source_schema);

        -- do the ontology first as the rest depends on this
        if ab_load_ref_data
        then
            log_statement('timer',0, 'START', 'Reference tables ');
            /*av_table   IN  varchar2,
         an_identifier  in number,
         av_action      in varchar2,
         av_statement*/
            get_ontology;   -- no children - thos come from element
            get_element;    -- and all its direct children
            get_external_system;    -- no children - those come from experiment and project
            --get_project;    -- this could be handled on the fly, but we want them all
            log_statement('timer',0, 'END', 'Reference tables ');
            commit;
        end if;

        get_assay (an_assay_id);

    end merge_migrate;

    procedure log_error
        (an_errnum   in  number,
         av_errmsg  in varchar2,
         av_location    in varchar2,
         av_comment in varchar2 default null)
    as
    begin
        insert into error_log
           ( ERROR_LOG_ID,
             ERROR_DATE,
             procedure_name,
             ERR_NUM,
             ERR_MSG,
             ERR_COMMENT
           ) values (
             ERROR_LOG_ID_SEQ.NEXTVAL,
             sysdate,
             av_location,
             an_errnum,
             av_errmsg,
             av_comment
           );


    exception
        when others
        then
            null;
    end log_error;

    procedure log_statement
        (av_table   IN  varchar2,
         an_identifier  in number,
         av_action      in varchar2,
         av_statement   IN varchar2)
    as
    begin
        if pb_logging
        then
            insert into statement_log
                ( TABLE_NAME,
                  IDENTIFIER,
                  ACTION_DATE,
                  ACTION,
                  DATA_CLAUSE
                ) values (
                  av_table,
                  an_identifier,
                  sysdate,
                  av_action,
                  substr(av_statement, 1, 1000)
                );
         end if;

    exception
        when others
        then
            null;
    end log_statement;

    procedure save_mapping
        (av_src_schema   in varchar2,
         av_table_name  in  varchar2,
         an_src_id   in  number,
         an_trgt_id   in  number)
    as
        ln_trgt_id    number;
    begin
        get_mapping_id (
            av_src_schema,
            av_table_name,
            an_src_id,
            ln_trgt_id);

        if ln_trgt_id is null
        then
            insert into identifier_mapping
            (   source_schema,
                table_name,
                source_id,
                target_id
            ) values (
                av_src_schema,
                av_table_name,
                an_src_id,
                an_trgt_id
            );
        else
            --dbms_output.put_line('update mapping');
            update identifier_mapping
            set target_id = ln_trgt_id
            where source_schema = av_src_schema
              and table_name = av_table_name
              and source_id = an_src_id;
        end if;


    exception
        when others
        then
            log_error(sqlcode, sqlerrm, 'save_mapping');
    end save_mapping;

    procedure get_mapping_id
        (av_src_schema   in varchar2,
         av_table_name  in  varchar2,
         an_src_id   in  number,
         ano_trgt_id   out  number)
    as
        cursor cur_mapping
        is
        select target_id
        from identifier_mapping
        where source_schema = av_src_schema
          and table_name = av_table_name
          and source_id = an_src_id;

       ln_trgt_id number := null;

    begin
       open cur_mapping;
       fetch cur_mapping into ln_trgt_id;
       close cur_mapping;

       ano_trgt_id := ln_trgt_id;

    exception
        when others
        then
            if cur_mapping%isopen
            then
                close cur_mapping;
            end if;

    end get_mapping_id;

-------------------------------------------------------------------------------------
-- added 9/5/12 -- schatwin
-- requires the table MIGRATION_ACTION, _EVENT, _DAY, _PERSON
------------------------------------------------------------------------------------
    PROCEDURE Generate_Migration_stats
        (avi_refresh IN VARCHAR2 DEFAULT 'Increment')
    AS
        CURSOR cur_action
        IS
        SELECT action_ref, count_sql
        FROM migration_action
        WHERE count_sql IS NOT NULL;

        --TYPE r_cursor IS REF CURSOR;
        TYPE r_event IS RECORD (aid migration_event.aid%type,
                        assay_id migration_event.assay_id%type,
                        experiment_id migration_event.experiment_id%type,
                        project_id migration_event.project_id%type,
                        event_count migration_event.event_count%type,
                        modified_by migration_person.person_name%type,
                        day_ref migration_event.day_ref%type);

        cur_event   r_cursor;
        lr_event    r_event;
        ln_action_ref NUMBER;
        ln_person_ref  NUMBER;
        lv_count_sql  VARCHAR2(32000);
        le_bad_parameter  EXCEPTION;
        ld_last_end_date  DATE;
        lv_modified_by  VARCHAR2(100) := ' ';

    BEGIN
        --  OUTLINE PSUEDO CODE -----------------------------------
        -- select from migration event to find the largest data so far
        --   hint: use the ref ID as its monotonically increasing with Date
        -- if the refresh is set, truncate the table (using execute immediate)
        --   and set the start date to null
        IF Lower(avi_refresh) = 'refresh'
        THEN
            EXECUTE IMMEDIATE 'truncate table migration_event';
            ld_last_end_date := To_Date('01/01/2000','MM/DD/YYYY');

        ELSIF Lower(avi_refresh) = 'increment'
        THEN
            SELECT migration_date
            INTO ld_last_end_date
            FROM migration_day md,
                (SELECT Max(day_ref) AS max_ref
                FROM migration_event) me
            WHERE md.day_ref = me.max_ref;

        ELSE
            RAISE le_bad_parameter;
        END IF;

        -- populate the migration_aid dimension with any new items
        INSERT INTO migration_aid
            (aid)
        SELECT DISTINCT ext_assay_ref
        FROM external_reference er
        WHERE NOT EXISTS
            (SELECT 1
            FROM migration_aid ma
            WHERE ma.aid = er.ext_assay_ref);

        -- get a cursor for the migration actions.  Each of these contains a SQL statement
        --   designed to extract data form the real tables and make it ready for insertion
        --   into MIGRATION_EVENT
        OPEN cur_action;
        LOOP
            FETCH cur_action INTO ln_action_ref, lv_count_sql;
            EXIT WHEN cur_action%NOTFOUND;
--            Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref));
            -- Open a cursor with the SQL using the start date as a parameter
            -- fetch into a standard rowtype for M_EVENT
            OPEN cur_event FOR lv_count_sql USING ld_last_end_date;
            LOOP
                FETCH cur_event INTO lr_event;
                EXIT WHEN cur_event%NOTFOUND;
--                Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                     || ' modified_by ' || lr_event.modified_by || ', '|| lv_modified_by);
                -- use a join with the dimensions to get the DAY_REF key value on the fly
                -- lookup the person_ref and insert if not found
                IF lr_event.modified_by != lv_modified_by
                then
                    BEGIN
                        SELECT person_ref INTO ln_person_ref
                        FROM migration_person
                        WHERE person_name = lr_event.modified_by;
                    EXCEPTION
                    WHEN No_data_found
                    THEN
--                    Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                          || ' new person ' || lr_event.modified_by);
                         SELECT Max(person_ref) + 1
                        INTO ln_person_ref
                        FROM migration_person;

                        INSERT INTO migration_person
                            (person_ref,
                            person_name)
                        VALUES
                            (ln_person_ref,
                            lr_event.modified_by);
                    END;
                    lv_modified_by := lr_event.modified_by;
                END IF;
                -- loop thru the cursor inserting a row at a time into the M_Event table
                -- this is generally efficient enough as its only a few rows when in increment mode
--               Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                     || ' insert day_ref = ' || To_Char(lr_event.day_ref) || ', '|| To_Char(ln_person_ref));
                 INSERT INTO migration_event
                        (aid,
                        assay_id,
                        experiment_id,
                        project_id,
                        event_count,
                        person_ref,
                        action_ref,
                        day_ref)
                VALUES (lr_event.aid,
                        lr_event.assay_id,
                        lr_event.experiment_id,
                        lr_event.project_id,
                        lr_event.event_count,
                        ln_person_ref,
                        ln_action_ref,
                        lr_event.day_ref);

            END LOOP;
            -- commit at the end of the loop
            COMMIT;

        END LOOP;

        -- and add the newly minted AID and equivalent items to the dimension table
        INSERT INTO migration_aid
            (aid)
        SELECT DISTINCT (aid)
            FROM migration_event me
        WHERE NOT EXISTS (SELECT 1
            FROM migration_aid ma
            WHERE ma.aid = me.aid);

        Commit;

    EXCEPTION
    WHEN le_bad_parameter THEN
        null;
--    WHEN OTHERS THEN
--        NULL;

    END Generate_Migration_stats;

    PROCEDURE open_LOCAL_cursor
            (av_modified_by   in  varchar2,
            av_table_name  in  varchar2,
            an_identifier  in number,
            aco_cursor in out r_cursor)
    as
        le_no_table_defined exception;

    BEGIN
        if av_table_name = 'ASSAY'
        then
            open aco_cursor for
            select ASSAY_ID,
                ASSAY_STATUS,
                ASSAY_SHORT_NAME,
                ASSAY_NAME,
                ASSAY_VERSION,
                ASSAY_TYPE,
                DESIGNED_BY,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay
            where assay_id = an_identifier
                or an_identifier is null;

        elsif av_table_name = 'ASSAY_COUNT'
        then
            -- used when an unlimited range is asked for
            open aco_cursor for
            select MAX(ASSAY_ID)
            from assay;

        elsif av_table_name = 'EXPERIMENT'
        then
            open aco_cursor for
            select EXPERIMENT_ID,
                EXPERIMENT_NAME,
                EXPERIMENT_STATUS,
                READY_FOR_EXTRACTION,
                ASSAY_ID,
                RUN_DATE_FROM,
                RUN_DATE_TO,
                HOLD_UNTIL_DATE,
                DESCRIPTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from experiment
            where assay_id = an_identifier;
            -- dbms_output.put_line('open curosr for experiment by assay');

        elsif av_table_name = 'EXTERNAL_REFERENCE_AID'
        then
            open aco_cursor for
            select ext_assay_ref
            from external_reference er,
                  experiment e
            where er.experiment_id = e.experiment_id
            and e.assay_id = an_identifier;

        elsif av_table_name = 'EXTERNAL_REFERENCE'
        then
            open aco_cursor for
            select EXTERNAL_REFERENCE_ID,
                EXTERNAL_SYSTEM_ID,
                EXPERIMENT_ID,
                PROJECT_ID,
                EXT_ASSAY_REF,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from external_reference
            where experiment_id = an_identifier;

        elsif av_table_name = 'EXTERNAL_REFERENCE_project'
        then
            open aco_cursor for
            select EXTERNAL_REFERENCE_ID,
                EXTERNAL_SYSTEM_ID,
                EXPERIMENT_ID,
                PROJECT_ID,
                EXT_ASSAY_REF,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from external_reference
            where project_id = an_identifier;

        elsif av_table_name = 'PROJECT_STEP'
        then
            -- beware this one, it may retrieve follows_experiments that you
            -- have not yet migrated
            open aco_cursor for
            select PROJECT_STEP_ID,
                next_project_experiment_id,
                prev_project_experiment_id,
                edge_name,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_step
            where next_project_experiment_id = an_identifier
                or prev_project_experiment_id = an_identifier;

        elsif av_table_name = 'ASSAY_DOCUMENT'
        then
            -- only get the ones that are relevant to the source schema
            -- expecially the blank CLOBs
            open aco_cursor for
            select ASSAY_DOCUMENT_ID,
                ASSAY_ID,
                DOCUMENT_NAME,
                DOCUMENT_TYPE,
                DOCUMENT_CONTENT,   -- careful!! this is a CLOB
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay_document
            where assay_id = an_identifier
              and length(document_content) > 0;
              --and nvl(modified_by, av_modified_by) = av_modified_by;

        elsif av_table_name = 'PROJECT_DOCUMENT'
        then
            -- only get the ones that are relevant to the source schema
            -- expecially the blank CLOBs
            open aco_cursor for
            select PROJECT_DOCUMENT_ID,
                PROJECT_ID,
                DOCUMENT_NAME,
                DOCUMENT_TYPE,
                DOCUMENT_CONTENT,   -- careful!! this is a CLOB
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_document
            where project_id = an_identifier
              and length(document_content) > 0;
              --and nvl(modified_by, av_modified_by) = av_modified_by;

        elsif av_table_name = 'RESULT'
        then
            open aco_cursor for
            select RESULT_ID,
                RESULT_STATUS,
                READY_FOR_EXTRACTION,
                EXPERIMENT_ID,
                SUBSTANCE_ID,
                RESULT_TYPE_ID,
                STATS_MODIFIER_ID,
                REPLICATE_NO,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                QUALIFIER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from result
            where experiment_id = an_identifier;

        elsif av_table_name = 'RSLT_CONTEXT_ITEM'
        then
            -- must sort these to ensure the parents go into the target first
            -- just ensure the group...ID is not nulled!
            --dbms_output.put_line ('open_src_cursor, result=' ||to_char(an_identifier));
            open aco_cursor for
            select RSLT_CONTEXT_ITEM_ID,
                  RESULT_ID,
                  ATTRIBUTE_ID ,
                  DISPLAY_ORDER,
                  VALUE_ID,
                  EXT_VALUE_ID,
                  QUALIFIER,
                  VALUE_NUM,
                  VALUE_MIN ,
                  VALUE_MAX,
                  VALUE_DISPLAY,
                  VERSION,
                  DATE_CREATED,
                  LAST_UPDATED,
                  MODIFIED_BY
            from rslt_context_item
            where result_id = an_identifier;

        elsif av_table_name = 'EXPRMT_CONTEXT_ITEM'
        then
              open aco_cursor for
            select EXPRMT_CONTEXT_ITEM_ID,
                EXPRMT_CONTEXT_ID,
                DISPLAY_ORDER,
                ATTRIBUTE_ID,
                VALUE_ID,
                EXT_VALUE_ID,
                QUALIFIER,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VALUE_DISPLAY,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from exprmt_context_item
            where EXPRMT_CONTEXT_ID = an_identifier;


        elsif av_table_name = 'STEP_CONTEXT_ITEM'
        then
              open aco_cursor for
            select STEP_CONTEXT_ITEM_ID,
                STEP_CONTEXT_ID,
                DISPLAY_ORDER,
                ATTRIBUTE_ID,
                VALUE_ID,
                EXT_VALUE_ID,
                QUALIFIER,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VALUE_DISPLAY,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from step_context_item
            where STEP_CONTEXT_ID = an_identifier;


        elsif av_table_name = 'RESULT_HIERARCHY'
        then
            -- this gets all hierarchy records for the experiment
            open aco_cursor for
            select rh.RESULT_ID,
                rh.PARENT_RESULT_ID,
                rh.HIERARCHY_TYPE,
                rh.VERSION,
                rh.DATE_CREATED,
                rh.LAST_UPDATED,
                rh.MODIFIED_BY
            from result_hierarchy rh
            where EXISTS (SELECT 1
                    FROM result r
                    WHERE r.result_id = rh.result_id
                    AND r.experiment_id = an_identifier)
              OR exists (SELECT 1
                    FROM result r
                    WHERE r.result_id = rh.parent_result_id
                    AND r.experiment_id = an_identifier);

        elsif av_table_name = 'EXTERNAL_SYSTEM'
        then
            open aco_cursor for
            select EXTERNAL_SYSTEM_ID,
                SYSTEM_NAME,
                OWNER,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from external_system
            where external_system_id = an_identifier
                or an_identifier is null;

        elsif av_table_name = 'PROJECT'
        then
            open aco_cursor for
            select PROJECT_ID,
                PROJECT_NAME,
                GROUP_TYPE,
                DESCRIPTION,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project
            where project_id = an_identifier
                or an_identifier is null;

        elsif av_table_name = 'PROJECT_CONTEXT_ITEM'
        then
            -- just ensure the group...ID is not nulled!
            open aco_cursor for
            select PROJECT_CONTEXT_ITEM_ID, --was MEASURE_CONTEXT_ITEM_ID,-- SJC 8/17/12
                PROJECT_CONTEXT_ID,
                DISPLAY_ORDER,   -- was nvl(GROUP_MEASURE_CONTEXT_ITEM_ID, ASSAY_CONTEXT_ITEM_ID) -- sjc 8/17/12
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_context_item
            where project_context_id = an_identifier;
            -- don't need an order any more as there's no circular reference

        elsif av_table_name = 'ASSAY_CONTEXT_ITEM'
        then
            -- just ensure the group...ID is not nulled!
            open aco_cursor for
            select ASSAY_CONTEXT_ITEM_ID, --was MEASURE_CONTEXT_ITEM_ID,-- SJC 8/17/12
                DISPLAY_ORDER,   -- was nvl(GROUP_MEASURE_CONTEXT_ITEM_ID, ASSAY_CONTEXT_ITEM_ID) -- sjc 8/17/12
                ASSAY_CONTEXT_ID,
                ATTRIBUTE_TYPE,
                ATTRIBUTE_ID,
                QUALIFIER,
                VALUE_ID,
                EXT_VALUE_ID,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay_context_item
            where assay_context_id = an_identifier;
            -- don't need an order any more as there's no circular reference

        elsif av_table_name = 'ASSAY_CONTEXT'
        then
            open aco_cursor for
            select ASSAY_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay_context
            where assay_id = an_identifier;

        elsif av_table_name = 'PROJECT_CONTEXT'
        then
            open aco_cursor for
            select PROJECT_CONTEXT_ID,
                PROJECT_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_context
            where project_id = an_identifier;

        elsif av_table_name = 'EXPRMT_CONTEXT'
        then
            open aco_cursor for
            select EXPRMT_CONTEXT_ID,
                EXPERIMENT_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from exprmt_context
            where experiment_id = an_identifier;

        elsif av_table_name = 'STEP_CONTEXT'
        then
            open aco_cursor for
            select STEP_CONTEXT_ID,
                PROJECT_STEP_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from step_context
            where project_step_id = an_identifier;

        elsif av_table_name = 'MEASURE'
        then
            -- this has a parantage circular relationship
            -- so we need to be careful of the order of insertion
            -- DBMS_output.put_line('arrived in open src cursor, assay_id='  || to_char(an_identifier));
            open aco_cursor for
            select MEASURE_ID,
                ASSAY_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                STATS_MODIFIER_ID,
                ENTRY_UNIT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from measure
            where assay_id = an_identifier
            connect by prior measure_id = parent_measure_id
            start with (parent_measure_id is NULL
                    OR parent_measure_id = measure_id);

        elsif av_table_name = 'ASSAY_CONTEXT_MEASURE'
        then
            open aco_cursor for
            select ASSAY_CONTEXT_MEASURE_ID,
                MEASURE_ID,
                ASSAY_CONTEXT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay_context_measure
            where measure_id = an_identifier;

        elsif av_table_name = 'TREE_ROOT'
        then
            open aco_cursor for
            select TREE_ROOT_ID,
                TREE_NAME,
                ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from tree_root
            where (element_id = an_identifier
                or an_identifier is null);

        elsif av_table_name = 'ELEMENT'
        then
            -- this has a parantage circular relationship
            -- so we need to be careful of the order of insertion
            open aco_cursor for
            select ELEMENT_ID,
                ELEMENT_STATUS,
                LABEL,
                DESCRIPTION,
                ABBREVIATION,
                SYNONYMS,
                UNIT_ID,
                BARD_URI,
                EXTERNAL_URL,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from element
            where (element_id = an_identifier
                or an_identifier is null)
            order by nvl(unit_id, ' '), element_id;

        elsif av_table_name = 'ELEMENT_HIERARCHY'
        then
            open aco_cursor for
            select ELEMENT_HIERARCHY_ID,
                PARENT_ELEMENT_ID,
                CHILD_ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from element_hierarchy
            where (parent_element_id = an_identifier
                or child_element_id = an_identifier);

        elsif av_table_name = 'ONTOLOGY_ITEM'
        then
            -- only get the ones that are relevant to the source schema
            open aco_cursor for
            select ONTOLOGY_ITEM_ID,
                ONTOLOGY_ID,
                ELEMENT_ID,
                ITEM_REFERENCE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from ontology_item
            where element_id = an_identifier;

        elsif av_table_name = 'UNIT_CONVERSION'
        then
            --get all of them - the PK is not helpful here (not an ID)
            open aco_cursor for
            select FROM_UNIT_ID,
                TO_UNIT_ID,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from unit_conversion;

        elsif av_table_name = 'ONTOLOGY'
        then
            open aco_cursor for
            select ONTOLOGY_ID,
                ONTOLOGY_NAME,
                ABBREVIATION,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from ontology
            where ontology_id = an_identifier
                or an_identifier is null;

        elsif av_table_name = 'EXPRMT_MEASURE'
        then
            open aco_cursor for
            select EXPRMT_MEASURE_ID,
		            MEASURE_ID,
		            PARENT_EXPRMT_MEASURE_ID,
                EXPERIMENT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from exprmt_measure
            where experiment_id = an_identifier
                or an_identifier is null;

        elsif av_table_name = 'PROJECT_EXPERIMENT'
        then
            open aco_cursor for
            select PROJECT_EXPERIMENT_ID,
		            PROJECT_ID,
		            EXPERIMENT_ID,
                STAGE_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_experiment
            where experiment_id = Nvl(an_identifier, EXPERIMENT_ID);

        elsif av_table_name = 'PRJCT_EXPRMT_CONTEXT'
        then
            open aco_cursor for
            select prjct_exprmt_context_ID,
		            PROJECT_EXPERIMENT_ID,
		            CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from prjct_exprmt_context
            where PROJECT_EXPERIMENT_ID = Nvl(an_identifier, PROJECT_EXPERIMENT_ID);

        elsif av_table_name = 'PRJCT_EXPRMT_CONTEXT_ITEM'
        then
            open aco_cursor for
            select prjct_exprmt_context_ITEM_ID,
		            prjct_exprmt_context_ID,
		            DISPLAY_ORDER,
		            ATTRIBUTE_ID,
		            VALUE_ID,
		            EXT_VALUE_ID,
		            QUALIFIER,
		            VALUE_NUM,
		            VALUE_MIN,
		            VALUE_MAX,
		            VALUE_DISPLAY,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from prjct_exprmt_context_item
            where prjct_exprmt_context_ID = an_identifier;

        else
            raise le_no_table_defined;
        end if;

    exception
        when le_no_table_defined
        then
            raise_application_error(-20002, 'No cursor defined for the table in this source - open_local_cursor');
        when others
        then
            raise_application_error (sqlcode, sqlerrm);
    end open_LOCAL_cursor;

end Merge_Migration;
/

