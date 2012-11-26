--
-- PACKAGE: LOAD_DATA
--

create or replace package load_data
as
    type r_cursor is ref cursor;

    procedure Load_reference;

    procedure Load_assay (an_assay_id in number default null);

    procedure Load_assay_with_result (an_assay_id in number default null);

    procedure load_assay (av_assay_set in VARCHAR2,
                          ab_w_results  IN BOOLEAN DEFAULT false);

    procedure open_src_cursor
        (av_modified_by   in  varchar2,
         av_table_name  in  varchar2,
         an_identifier  in number,
         aco_cursor in out r_cursor);

end load_data;
/

create or replace package body load_data
as
    --------------------------------------------------------------------
    --  This depends on the source and targe schemas being identical (columns in same order in every table)
    --
    --  schatwin 7/2/12 initial version
    --
    --
    --
    --
    ----------------------------------------------------------------------


    procedure open_src_cursor
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
                ASSAY_TITLE,
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

        elsif av_table_name = 'ASSAY_CONTEXT'
        then
            open aco_cursor for
            select ASSAY_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from assay_context
            where assay_id = an_identifier;

        elsif av_table_name = 'ASSAY_CONTEXT_ITEM'
        then
            -- just ensure the group...ID is not nulled!
            open aco_cursor for
            select ASSAY_CONTEXT_ITEM_ID, --was MEASURE_CONTEXT_ITEM_ID,-- SJC 8/17/12
                ASSAY_CONTEXT_ID,
                DISPLAY_ORDER,   -- was nvl(GROUP_MEASURE_CONTEXT_ITEM_ID, ASSAY_CONTEXT_ITEM_ID) -- sjc 8/17/12
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
                UNIT,
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
            order by nvl(unit, ' '), element_id;

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

        elsif av_table_name = 'EXPERIMENT'
        then
            open aco_cursor for
            select EXPERIMENT_ID,
                EXPERIMENT_NAME,
                EXPERIMENT_STATUS,
                READY_FOR_EXTRACTION,
                ASSAY_ID,
                --LABORATORY_ID,
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

        elsif av_table_name = 'MEASURE'
        then
            -- this has a parantage circular relationship
            -- so we need to be careful of the order of insertion
            -- DBMS_output.put_line('arrived in open src cursor, assay_id='  || to_char(an_identifier));
            open aco_cursor for
            select MEASURE_ID,
                ASSAY_ID,
                ASSAY_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from measure
            where assay_id = an_identifier
            connect by prior measure_id = parent_measure_id
            start with (parent_measure_id is NULL
                    OR parent_measure_id = measure_id);

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
            -- must sort these to ensure the parents go into the target first
            -- just ensure the group...ID is not nulled!
            --dbms_output.put_line ('open_src_cursor, result=' ||to_char(an_identifier));
            open aco_cursor for
            select PROJECT_CONTEXT_ITEM_ID,
                nvl(GROUP_PROJECT_CONTEXT_ID,PROJECT_CONTEXT_ITEM_ID),
                PROJECT_ID,
                PROJECT_STEP_ID,
                DISCRIMINATOR,
                ATTRIBUTE_ID,
                VALUE_ID,
                EXT_VALUE_ID,
                QUALIFIER,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_context_item
            where project_id = an_identifier
            CONNECT BY PRIOR group_project_context_id = project_context_item_id
               START WITH (group_project_context_id = project_context_item_id
                          OR group_project_context_id IS NULL);

        elsif av_table_name = 'PROJECT_CONTEXT_ITEM_step'
        then
             open aco_cursor for
            select PROJECT_CONTEXT_ITEM_ID,
                nvl(GROUP_PROJECT_CONTEXT_ID,PROJECT_CONTEXT_ITEM_ID),
                PROJECT_ID,
                PROJECT_STEP_ID,
                DISCRIMINATOR,
                ATTRIBUTE_ID,
                VALUE_ID,
                EXT_VALUE_ID,
                QUALIFIER,
                VALUE_DISPLAY,
                VALUE_NUM,
                VALUE_MIN,
                VALUE_MAX,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_context_item
            where project_step_id = an_identifier
               START WITH (group_project_context_id = project_context_item_id
                          OR group_project_context_id IS NULL);


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

        elsif av_table_name = 'PROJECT_STEP'
        then
            -- beware this one, it may retrieve follows_experiments that you
            -- have not yet migrated
            open aco_cursor for
            select PROJECT_STEP_ID,
                PROJECT_ID,
                --STAGE_ID,
                EXPERIMENT_ID,
                FOLLOWS_EXPERIMENT_ID,
                DESCRIPTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_step
            where experiment_id = an_identifier
               or nvl(follows_experiment_id, an_identifier) = an_identifier;

        elsif av_table_name = 'RESULT'
        then
            open aco_cursor for
            select RESULT_ID,
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
            from result
            where experiment_id = an_identifier;

        elsif av_table_name = 'RESULT_HIERARCHY'
        then
            -- this gets all hierarchy records for the experiment
            -- assumes there is a match in result_ids
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
                    AND r.experiment_id = an_identifier);

        elsif av_table_name = 'RUN_CONTEXT_ITEM'
        then
            -- must sort these to ensure the parents go into the target first
            -- just ensure the group...ID is not nulled!
            --dbms_output.put_line ('open_src_cursor, result=' ||to_char(an_identifier));
            open aco_cursor for
            select RUN_CONTEXT_ITEM_ID,
                nvl(GROUP_RUN_CONTEXT_ID,RUN_CONTEXT_ITEM_ID),
                EXPERIMENT_ID,
                RESULT_ID,
                DISCRIMINATOR,
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
            from run_context_item
            where result_id = an_identifier
            CONNECT BY PRIOR group_run_context_id = run_context_item_id
            START WITH (group_run_context_id = run_context_item_id
                        OR
                        group_run_context_id IS NULL);

        elsif av_table_name = 'RUN_CONTEXT_ITEM_experiment'
        then
             open aco_cursor for
            select RUN_CONTEXT_ITEM_ID,
                nvl(GROUP_RUN_CONTEXT_ID,RUN_CONTEXT_ITEM_ID),
                EXPERIMENT_ID,
                RESULT_ID,
                DISCRIMINATOR,
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
            from run_context_item
            where experiment_id = an_identifier
              and result_id is null
            CONNECT BY PRIOR group_run_context_id = run_context_item_id
            START WITH (group_run_context_id = run_context_item_id
                        OR
                        group_run_context_id IS NULL);


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

        elsif av_table_name = 'UNIT_CONVERSION'
        then
            --get all of them - the PK is not helpful here (not an ID)
            open aco_cursor for
            select FROM_UNIT,
                TO_UNIT,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from unit_conversion;

        else
            raise le_no_table_defined;
        end if;

    exception
        when le_no_table_defined
        then
            raise_application_error(-20002, 'No cursor defined for the table in this source - open_src_cursor');
        when others
        then
            raise_application_error (sqlcode, sqlerrm);
    end open_src_cursor;

    procedure Load_reference
    as
        cursor cur_element
        is
        select element_id from element;

        ln_element_id   number := null;

    begin
        -- load data for all the reference tables (the top level ones in RI)
        -- NOTE we use order SQL statements to make sure the self-referential FKs work OK
        --If any of these contains data skip the whole thing
        open cur_element;
        fetch cur_element into ln_element_id;
        close cur_element;

        if ln_element_id is null
        then

            insert into element
                (ELEMENT_ID,
                ELEMENT_STATUS,
                LABEL,
                DESCRIPTION,
                ABBREVIATION,
                SYNONYMS,
                UNIT,
                BARD_URI,
                EXTERNAL_URL,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ELEMENT_ID,
                ELEMENT_STATUS,
                LABEL,
                DESCRIPTION,
                ABBREVIATION,
                SYNONYMS,
                UNIT,
                BARD_URI,
                EXTERNAL_URL,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.element
            order by nvl(unit, ' ');

            insert into element_hierarchy
                (ELEMENT_HIERARCHY_ID,
                PARENT_ELEMENT_ID,
                CHILD_ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ELEMENT_HIERARCHY_ID,
                PARENT_ELEMENT_ID,
                CHILD_ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
         from data_mig.element_hierarchy;

            insert into ontology
                (ONTOLOGY_ID,
                ONTOLOGY_NAME,
                ABBREVIATION,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ONTOLOGY_ID,
                ONTOLOGY_NAME,
                ABBREVIATION,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.ontology;

            insert into tree_root
                (TREE_ROOT_ID,
                TREE_NAME,
                ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select TREE_ROOT_ID,
                TREE_NAME,
                ELEMENT_ID,
                RELATIONSHIP_TYPE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.tree_root;

            insert into external_system
                (EXTERNAL_SYSTEM_ID,
                SYSTEM_NAME,
                OWNER,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select EXTERNAL_SYSTEM_ID,
                SYSTEM_NAME,
                OWNER,
                SYSTEM_URL,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.external_system;
            commit;

            insert into ontology_item
                (ONTOLOGY_ITEM_ID,
                ONTOLOGY_ID,
                ELEMENT_ID,
                ITEM_REFERENCE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ONTOLOGY_ITEM_ID,
                ONTOLOGY_ID,
                ELEMENT_ID,
                ITEM_REFERENCE,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.ontology_item;

            insert into unit_conversion
                (FROM_UNIT,
                TO_UNIT,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select FROM_UNIT,
                TO_UNIT,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.unit_conversion;

            manage_ontology.make_trees;
            commit;

            --reset_sequence;

        end if;


    end load_reference;

    procedure Load_assay_with_result (an_assay_id in number default null)
    as
        cursor cur_assay
        is
        select assay_id from data_mig.assay
        where assay_id = an_assay_id
        or an_assay_id is null;

        cursor cur_experiment (cn_assay_id number)
        is
        select experiment_id
        from data_mig.experiment
        where assay_id = cn_assay_id;

    begin
        if an_assay_id is null
        then
            begin
               load_reference;    -- this could be handled on the fly, but we want them all
            exception
            when others
            then
                null;   --trap the error if reference is already loaded
            end;
        end if;

        for rec_assay in cur_assay
        LOOP
            load_assay (rec_assay.assay_id);
             -- insert into assay
             -- insert into assay_document
             -- insert into assay_context
             -- insert into measure
             -- insert into assay_context_item
             -- loop over experiments
                -- insert into experiment
                -- insert into external_reference
                -- insert into project (??)
                -- insert into project_step


            for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into result
                    (RESULT_ID,
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
                    MODIFIED_BY)
                select RESULT_ID,
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
                from data_mig.result
                where experiment_id = rec_experiment.experiment_id;

                insert into run_context_item
                    (RUN_CONTEXT_ITEM_ID,
                    GROUP_RUN_CONTEXT_ID,
                    EXPERIMENT_ID,
                    RESULT_ID,
                    ATTRIBUTE_ID,
                    DISCRIMINATOR,
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
                    MODIFIED_BY)
                select RUN_CONTEXT_ITEM_ID,
                    GROUP_RUN_CONTEXT_ID,
                    EXPERIMENT_ID,
                    RESULT_ID,
                    DISCRIMINATOR,
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
                from data_mig.run_context_item  rci
                where experiment_id = rec_experiment.experiment_id
                   OR EXISTS (SELECT 1
                      FROM data_mig.result r
                      WHERE r.result_id = rci.result_id
                        AND r.experiment_id = rec_experiment.experiment_id);

                insert into result_hierarchy
                    (RESULT_ID,
                    PARENT_RESULT_ID,
                    HIERARCHY_TYPE,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select RESULT_ID,
                    PARENT_RESULT_ID,
                    HIERARCHY_TYPE,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.result_hierarchy rh
                where EXISTS (SELECT 1
                        FROM data_mig.result r
                        WHERE r.result_id = rh.result_id
                        AND r.experiment_id = rec_experiment.experiment_id);


            end loop;

            commit; -- for each assay

        end loop;

        -- insert into external_reference (for projects)
        -- insert into project_context_item (for both steps and projects)
        -- pick up any projects that have no descendant experiments

        -- loop again to get the project experiments
        -- with predecessors

--        if an_assay_id is null
--        then
--            reset_sequence;
----            null;
--        end if;

    end load_assay_with_result;

    procedure Load_assay (an_assay_id in number default null)
    as
        cursor cur_assay
        is
        select assay_id from data_mig.assay
        where assay_id = an_assay_id
        or an_assay_id is null;

        cursor cur_experiment (cn_assay_id number)
        is
        select experiment_id
        from data_mig.experiment
        where assay_id = cn_assay_id;

    begin
        if an_assay_id is null
        then
            begin
               load_reference;    -- this could be handled on the fly, but we want them all
            exception
            when others
            then
                null;   --trap the error if reference is already loaded
            end;
        end if;

        for rec_assay in cur_assay
        loop
            insert into assay
                (ASSAY_ID,
                ASSAY_STATUS,
                ASSAY_TITLE,
                ASSAY_NAME,
                ASSAY_VERSION,
                ASSAY_TYPE,
                DESIGNED_BY,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ASSAY_ID,
                ASSAY_STATUS,
                ASSAY_TITLE,
                ASSAY_NAME,
                ASSAY_VERSION,
                ASSAY_TYPE,
                DESIGNED_BY,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.assay
            where assay_id = rec_assay.assay_id;

            insert into assay_document
                (ASSAY_DOCUMENT_ID,
                ASSAY_ID,
                DOCUMENT_NAME,
                DOCUMENT_TYPE,
                DOCUMENT_CONTENT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ASSAY_DOCUMENT_ID,
                ASSAY_ID,
                DOCUMENT_NAME,
                DOCUMENT_TYPE,
                DOCUMENT_CONTENT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.assay_document
            where assay_id = rec_assay.assay_id;

            insert into assay_context
                (ASSAY_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ASSAY_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.assay_context
            where assay_id = rec_assay.assay_id;

            insert into measure
                (MEASURE_ID,
                ASSAY_ID,
                ASSAY_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select MEASURE_ID,
                ASSAY_ID,
                ASSAY_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.measure
            where assay_id = rec_assay.assay_id
            connect by prior measure_id = parent_measure_id
            start with (parent_measure_id is NULL
                    OR parent_measure_id = measure_id);

            insert into assay_context_item
                (ASSAY_CONTEXT_ITEM_ID,
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
                MODIFIED_BY)
            select ASSAY_CONTEXT_ITEM_ID,
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
            from data_mig.assay_context_item
            where assay_context_id in
                (select assay_context_id
                 from assay_context
                 where assay_id = rec_assay.assay_id);

             for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into experiment
                    (EXPERIMENT_ID,
                    EXPERIMENT_NAME,
                    EXPERIMENT_STATUS,
                    READY_FOR_EXTRACTION,
                    ASSAY_ID,
                    --LABORATORY_ID,
                    RUN_DATE_FROM,
                    RUN_DATE_TO,
                    HOLD_UNTIL_DATE,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select EXPERIMENT_ID,
                    EXPERIMENT_NAME,
                    EXPERIMENT_STATUS,
                    READY_FOR_EXTRACTION,
                    ASSAY_ID,
                    --LABORATORY_ID,
                    RUN_DATE_FROM,
                    RUN_DATE_TO,
                    HOLD_UNTIL_DATE,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.experiment
                where experiment_id = rec_experiment.experiment_id;

                insert into project
                select PROJECT_ID,
                    PROJECT_NAME,
                    GROUP_TYPE,
                    DESCRIPTION,
                    READY_FOR_EXTRACTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project dp
                where project_id in
                    (select project_id from data_mig.project_step
                     where experiment_id = rec_experiment.experiment_id
                     or follows_experiment_id = rec_experiment.experiment_id)
                 and not exists (select 1 from project p
                            where p.project_id = dp.project_id);

                insert into project_step
                    (PROJECT_STEP_ID,
                    PROJECT_ID,
                    --STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_STEP_ID,
                    PROJECT_ID,
                    --STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_step pe
                where experiment_id = rec_experiment.experiment_id
                 and follows_experiment_id is null;

                -- insert project context
                insert into project_context_item
                    (PROJECT_CONTEXT_ITEM_ID,
                    GROUP_PROJECT_CONTEXT_ID,
                    PROJECT_ID,
                    PROJECT_STEP_ID,
                    DISCRIMINATOR,
                    ATTRIBUTE_ID,
                    VALUE_ID,
                    EXT_VALUE_ID,
                    QUALIFIER,
                    VALUE_DISPLAY,
                    VALUE_NUM,
                    VALUE_MIN,
                    VALUE_MAX,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                    )
                select PROJECT_CONTEXT_ITEM_ID,
                    GROUP_PROJECT_CONTEXT_ID,
                    PROJECT_ID,
                    PROJECT_STEP_ID,
                    DISCRIMINATOR,
                    ATTRIBUTE_ID,
                    VALUE_ID,
                    EXT_VALUE_ID,
                    QUALIFIER,
                    VALUE_DISPLAY,
                    VALUE_NUM,
                    VALUE_MIN,
                    VALUE_MAX,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_context_item pci
                where project_step_id in
                        (select project_step_id
                         from project_step e
                         where e.experiment_id = rec_experiment.experiment_id)
                  and not exists (select 1
                        from project_context_item pci2
                        where pci2.project_context_item_id = pci.project_context_item_id)
                ORDER by decode (project_context_item_id,
                                group_project_context_id, 0,
                                project_context_item_id);

                insert into external_reference
                    (EXTERNAL_REFERENCE_ID,
                    EXTERNAL_SYSTEM_ID,
                    EXPERIMENT_ID,
                    PROJECT_ID,
                    EXT_ASSAY_REF,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select EXTERNAL_REFERENCE_ID,
                    EXTERNAL_SYSTEM_ID,
                    EXPERIMENT_ID,
                    PROJECT_ID,
                    EXT_ASSAY_REF,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.external_reference
                where experiment_id = rec_experiment.experiment_id;


            end loop;

            commit; -- for each assay

        end loop;

         --- insert into project (for all ones not loaded yet)
         insert into project
            (PROJECT_ID,
            PROJECT_NAME,
            GROUP_TYPE,
            DESCRIPTION,
            READY_FOR_EXTRACTION,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY)
        select PROJECT_ID,
            PROJECT_NAME,
            GROUP_TYPE,
            DESCRIPTION,
            READY_FOR_EXTRACTION,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.project p
        where not exists (select 1
                from project pp
                where pp.project_id = p.project_id);

        insert into project_document
            (PROJECT_DOCUMENT_ID,
            PROJECT_ID,
            DOCUMENT_NAME,
            DOCUMENT_TYPE,
            DOCUMENT_CONTENT,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY)
        select PROJECT_DOCUMENT_ID,
            PROJECT_ID,
            DOCUMENT_NAME,
            DOCUMENT_TYPE,
            DOCUMENT_CONTENT,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.project_document dpd
        where EXISTS (SELECT 1
                  FROM project p
                  WHERE p.project_id = dpd.project_id)
          AND NOT EXISTS (SELECT 1
                  FROM project_document pd
                  WHERE pd.project_document_id = dpd.project_document_id);

                -- insert project context
        insert into project_context_item
            (PROJECT_CONTEXT_ITEM_ID,
            GROUP_PROJECT_CONTEXT_ID,
            PROJECT_ID,
            PROJECT_STEP_ID,
            DISCRIMINATOR,
            ATTRIBUTE_ID,
            VALUE_ID,
            EXT_VALUE_ID,
            QUALIFIER,
            VALUE_DISPLAY,
            VALUE_NUM,
            VALUE_MIN,
            VALUE_MAX,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
            )
        select PROJECT_CONTEXT_ITEM_ID,
            GROUP_PROJECT_CONTEXT_ID,
            PROJECT_ID,
            PROJECT_STEP_ID,
            DISCRIMINATOR,
            ATTRIBUTE_ID,
            VALUE_ID,
            EXT_VALUE_ID,
            QUALIFIER,
            VALUE_DISPLAY,
            VALUE_NUM,
            VALUE_MIN,
            VALUE_MAX,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.project_context_item pci
        where project_id in
                (select project_id
                 from project)
          and not exists (select 1
                from project_context_item pci2
                where pci2.project_context_item_id = pci.project_context_item_id)
        ORDER by decode (project_context_item_id,
                        group_project_context_id, 0,
                        project_context_item_id);

        insert into external_reference
            (EXTERNAL_REFERENCE_ID,
            EXTERNAL_SYSTEM_ID,
            EXPERIMENT_ID,
            PROJECT_ID,
            EXT_ASSAY_REF,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY)
        select EXTERNAL_REFERENCE_ID,
            EXTERNAL_SYSTEM_ID,
            EXPERIMENT_ID,
            PROJECT_ID,
            EXT_ASSAY_REF,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.external_reference der
        where project_id in
                (select project_id from project)
          and not exists (select 1 from external_reference er
                where er.external_reference_id = der.external_reference_id);

        -- loop again to get the project experiments
        -- with predecessors
        for rec_assay in cur_assay
        loop
            for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into project_step
                    (PROJECT_STEP_ID,
                    PROJECT_ID,
                    --STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_STEP_ID,
                    PROJECT_ID,
                    --STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_step pe
                where experiment_id = rec_experiment.experiment_id
                 and exists (select 1 from experiment e2
                        where e2.experiment_id = pe.follows_experiment_id);
            end loop;

        end loop;

        commit;

        if an_assay_id is null
        then
            --reset_sequence;
            null;
        end if;

    end load_assay;

    procedure load_assay (av_assay_set in varchar2,
                          ab_w_results  IN BOOLEAN DEFAULT false)
    as
    -- this version parses the string into an array and then calls the assay by assay version
        TYPE t_assay_id IS TABLE OF NUMBER(19) INDEX BY BINARY_INTEGER;
        la_assay_ids   t_assay_id;
        lv_number       VARCHAR2(38);
        lv_assay_set    VARCHAR2(4000);
        ln_pos          number;
        lv_range_separator    char(1) := ',';
        i         BINARY_INTEGER;
        ln_limit  binary_integer := 0;

        function is_numeric (lv_string in varchar2) return boolean
        as
            -- watch for null propogation, nulls tanslate to true
            ln_number   number;
        begin
            ln_number := to_number(lv_string);
            return true;

        exception
            when others
            then
                return false;
        end is_numeric;

    BEGIN
        lv_assay_set := av_assay_set;
        WHILE Length(lv_assay_set) >0
        loop
            ln_pos := instr(lv_assay_set, lv_range_separator);
            if ln_pos = 0
            then
                ln_pos := Length(lv_assay_set)+1;
            end if;

            lv_number := trim(substr(lv_assay_set, 1, ln_pos - 1));
            lv_assay_set := trim(substr(lv_assay_set, ln_pos + 1));

            if is_numeric(lv_number)
            then
               ln_limit := ln_limit +1;
               la_assay_ids(ln_limit) := to_number(lv_number);
             END IF;
        END LOOP;

        for i IN 1 .. ln_limit
        loop
            if i = 1
            then
                begin
                    load_reference;    -- this could be handled on the fly, but we want them all
                exception
                when others
                then
                    null;   --trap the error if reference is already loaded
                end;
            end if;

            IF ab_w_results
            THEN
                load_assay_with_result (la_assay_ids(i) );
            ELSE
                load_assay (la_assay_ids(i) );
            END IF;

            commit;

        end loop;

--        reset_sequence;

     end load_assay;
end load_data;
/

GRANT EXECUTE ON LOAD_DATA TO SCHATWIN
;
GRANT EXECUTE ON LOAD_DATA TO BARD_DEV
;
GRANT EXECUTE ON LOAD_DATA TO SBRUDZ
;
GRANT EXECUTE ON LOAD_DATA TO DATA_MIG
;
GRANT EXECUTE ON LOAD_DATA TO YCRUZ
;
GRANT EXECUTE ON LOAD_DATA TO SOUTHERN
;
GRANT EXECUTE ON LOAD_DATA TO BARD_QA
;
GRANT EXECUTE ON LOAD_DATA TO BARD_CI
;
GRANT EXECUTE ON LOAD_DATA TO DSTONICH
;
GRANT EXECUTE ON LOAD_DATA TO BALEXAND
;
GRANT EXECUTE ON LOAD_DATA TO DDURKIN
;
GRANT EXECUTE ON LOAD_DATA TO JASIEDU
;

BEGIN
load_data.load_assay;
END;
/