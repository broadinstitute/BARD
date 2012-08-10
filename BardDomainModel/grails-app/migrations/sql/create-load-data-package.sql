--
-- PACKAGE: LOAD_DATA
--

create or replace package load_data
as
    type r_cursor is ref cursor;

    procedure reset_sequence;

    procedure Load_reference;

    procedure Load_assay (an_assay_id in number default null);

    procedure Load_assay_without_result (an_assay_id in number default null);

    procedure load_assay (av_assay_range in varchar2);

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
    --  schatwin 7;2;12 initial version
    --
    --
    --
    --
    ----------------------------------------------------------------------
    procedure reset_sequence
    as
        cursor cur_sequence
        is
        select sequence_name
        from user_sequences;

        lv_max_sql  varchar2(1000);
        lv_drop_sql varchar2(1000);
        lv_create_sql   varchar2(1000);
        lv_grant_sql    varchar2(1000);
        lv_table_name   varchar2(50);
        lv_primary_key  varchar2(50);
        ln_max_id   number;

    begin
        for rec_sequence in cur_sequence
        loop
            lv_table_name := replace(rec_sequence.sequence_name, '_ID_SEQ', null);
            lv_primary_key := replace(rec_sequence.sequence_name, '_SEQ', null);

            lv_max_sql := 'select nvl(max(' || lv_primary_key || '), 0) from ' || lv_table_name;
            begin
                --dbms_output.put_line(lv_max_sql);
                EXECUTE IMMEDIATE lv_max_sql INTO ln_max_ID;

                lv_drop_sql := 'drop sequence ' || rec_sequence.sequence_name;
                --dbms_output.put_line(lv_drop_sql);
                EXECUTE IMMEDIATE lv_drop_sql;

                lv_create_sql := 'create sequence ' || rec_sequence.sequence_name
                                || ' start with ' || to_char(ln_max_id + 1)
                                || ' increment by 1 nominvalue maxvalue 2147483648 nocycle cache 2 noorder';
                --dbms_output.put_line(lv_create_sql);

                lv_grant_sql := 'grant select on ' || rec_sequence.sequence_name
                                || ' to schatwin';
                --dbms_output.put_line(lv_grant_sql);
                EXECUTE IMMEDIATE lv_create_sql;
                EXECUTE IMMEDIATE lv_grant_sql;

            exception
                when others
                then
                    null;   --dbms_output.put_line (to_char(sqlcode) || ', ' || sqlerrm);

            end;

        end loop;

        if cur_sequence%isopen
        then
            close cur_sequence;
        end if;

    end reset_sequence;

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
                LABORATORY_ID,
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

        elsif av_table_name = 'PROJECT_EXPERIMENT'
        then
            -- beware this one, it may retrieve follows_experiments that you
            -- have not yet migrated
            open aco_cursor for
            select PROJECT_EXPERIMENT_ID,
                PROJECT_ID,
                STAGE_ID,
                EXPERIMENT_ID,
                FOLLOWS_EXPERIMENT_ID,
                DESCRIPTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from project_experiment
            where experiment_id = an_identifier
               or nvl(follows_experiment_id, an_identifier) = an_identifier;

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

        elsif av_table_name = 'RESULT_CONTEXT_ITEM'
        then
            -- must sort these to ensure the parents go into the target first
            -- just ensure the group...ID is not nulled!
            --dbms_output.put_line ('open_src_cursor, result=' ||to_char(an_identifier));
            open aco_cursor for
            select RESULT_CONTEXT_ITEM_ID,
                nvl(GROUP_RESULT_CONTEXT_ID,RESULT_CONTEXT_ITEM_ID),
                EXPERIMENT_ID,
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
            from result_context_item
            where result_id = an_identifier
            order by decode(nvl(GROUP_RESULT_CONTEXT_ID,RESULT_CONTEXT_ITEM_ID),
                     RESULT_CONTEXT_ITEM_ID, 0, RESULT_CONTEXT_ITEM_ID);

        elsif av_table_name = 'RESULT_CONTEXT_ITEM_experiment'
        then
             open aco_cursor for
            select RESULT_CONTEXT_ITEM_ID,
                nvl(GROUP_RESULT_CONTEXT_ID,RESULT_CONTEXT_ITEM_ID),
                EXPERIMENT_ID,
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
            from result_context_item
            where experiment_id = an_identifier
              and result_id is null
            order by decode(nvl(GROUP_RESULT_CONTEXT_ID,RESULT_CONTEXT_ITEM_ID),
                     RESULT_CONTEXT_ITEM_ID, 0, RESULT_CONTEXT_ITEM_ID);


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
            from result_hierarchy rh,
                 result c,
                 result p
            where c.result_id = rh.result_id
              and p.result_id = rh.parent_result_id
              and c.experiment_id = an_identifier
              and p.experiment_id = an_identifier;

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

        elsif av_table_name = 'MEASURE_CONTEXT_ITEM'
        then
            -- must sort these to ensure the parents go into the target first
            -- just ensure the group...ID is not nulled!
            open aco_cursor for
            select MEASURE_CONTEXT_ITEM_ID,
                nvl(GROUP_MEASURE_CONTEXT_ITEM_ID, MEASURE_CONTEXT_ITEM_ID),
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            from measure_context_item
            where assay_id = an_identifier
            order by decode(nvl(group_measure_context_item_id, measure_context_item_id),
                     measure_context_item_id, 0, measure_context_item_id);

        elsif av_table_name = 'MEASURE_CONTEXT'
        then
            open aco_cursor for
            select MEASURE_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from measure_context
            where assay_id = an_identifier;

        elsif av_table_name = 'MEASURE'
        then
            -- this has a parantage circular relationship
            -- so we need to be careful of the order of insertion
            --DBMS_output.put_line('arrived in open src cursor, assay_id='  || to_char(an_identifier));
            open aco_cursor for
            select MEASURE_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            start with parent_measure_id is null;

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
                UNIT,
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

            insert into measure_context
                (MEASURE_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select MEASURE_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.measure_context
            where assay_id = rec_assay.assay_id;

            insert into measure
                (MEASURE_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select MEASURE_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.measure
            where assay_id = rec_assay.assay_id;

            insert into measure_context_item
                (MEASURE_CONTEXT_ITEM_ID,
                GROUP_MEASURE_CONTEXT_ITEM_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            select MEASURE_CONTEXT_ITEM_ID,
                GROUP_MEASURE_CONTEXT_ITEM_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            from data_mig.measure_context_item
            where assay_id = rec_assay.assay_id;

            for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into experiment
                    (EXPERIMENT_ID,
                    EXPERIMENT_NAME,
                    EXPERIMENT_STATUS,
                    READY_FOR_EXTRACTION,
                    ASSAY_ID,
                    LABORATORY_ID,
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
                    LABORATORY_ID,
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

                insert into result_context_item
                    (RESULT_CONTEXT_ITEM_ID,
                    GROUP_RESULT_CONTEXT_ID,
                    EXPERIMENT_ID,
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
                    MODIFIED_BY)
                select RESULT_CONTEXT_ITEM_ID,
                    GROUP_RESULT_CONTEXT_ID,
                    EXPERIMENT_ID,
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
                from data_mig.result_context_item
                where experiment_id = rec_experiment.experiment_id;

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
                from data_mig.result_hierarchy
                where result_id in
                    (select result_id
                     from result
                     where experiment_id = rec_experiment.experiment_id);

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
                    (select project_id from data_mig.project_experiment
                     where experiment_id = rec_experiment.experiment_id
                     or follows_experiment_id = rec_experiment.experiment_id)
                 and not exists (select 1 from project p
                            where p.project_id = dp.project_id);

                insert into project_experiment
                    (PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_experiment pe
                where experiment_id = rec_experiment.experiment_id
                 and follows_experiment_id is null;

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

        -- pick up any projects that have no descendant experiments
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

        -- loop again to get the project experiments
        -- with predecessors
        for rec_assay in cur_assay
        loop
            for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into project_experiment
                    (PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_experiment pe
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

    procedure Load_assay_without_result (an_assay_id in number default null)
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

            insert into measure_context
                (MEASURE_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select MEASURE_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.measure_context
            where assay_id = rec_assay.assay_id;

            insert into measure
                (MEASURE_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select MEASURE_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                ENTRY_UNIT,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.measure
            where assay_id = rec_assay.assay_id;

            insert into measure_context_item
                (MEASURE_CONTEXT_ITEM_ID,
                GROUP_MEASURE_CONTEXT_ITEM_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            select MEASURE_CONTEXT_ITEM_ID,
                GROUP_MEASURE_CONTEXT_ITEM_ID,
                ASSAY_ID,
                MEASURE_CONTEXT_ID,
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
            from data_mig.measure_context_item
            where assay_id = rec_assay.assay_id;

             for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into experiment
                    (EXPERIMENT_ID,
                    EXPERIMENT_NAME,
                    EXPERIMENT_STATUS,
                    READY_FOR_EXTRACTION,
                    ASSAY_ID,
                    LABORATORY_ID,
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
                    LABORATORY_ID,
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
                    (select project_id from data_mig.project_experiment
                     where experiment_id = rec_experiment.experiment_id
                     or follows_experiment_id = rec_experiment.experiment_id)
                 and not exists (select 1 from project p
                            where p.project_id = dp.project_id);

                insert into project_experiment
                    (PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_experiment pe
                where experiment_id = rec_experiment.experiment_id
                 and follows_experiment_id is null;

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
                insert into project_experiment
                    (PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    EXPERIMENT_ID,
                    FOLLOWS_EXPERIMENT_ID,
                    DESCRIPTION,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_experiment pe
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

    end load_assay_without_result;

    procedure load_assay (av_assay_range in varchar2)
    as
    -- this version parses the string into a range and then calls the assay by assay version
        lv_range_min    varchar2(100);
        lv_range_max    varchar2(100);
        ln_pos          number;
        ln_start_id     number;
        ln_end_id       number;
        lv_range_separator    char(1) := '-';
        le_not_a_range exception;

        cursor cur_assay (cn_start_id number, cn_end_id number)
        is
        select assay_id from data_mig.assay
        where assay_id between cn_start_id and cn_end_id;

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

    begin
        ln_pos := instr(av_assay_range, lv_range_separator);
        if ln_pos = 0
        then
            raise le_not_a_range;
        end if;

        lv_range_min := trim(substr(av_assay_range, 1, ln_pos - 1));
        lv_range_max := trim(substr(av_assay_range, ln_pos + 1));

        if is_numeric(lv_range_min)
        then
            ln_start_id := to_number(lv_range_min);

            if is_numeric(lv_range_max)
            then
                ln_end_id := to_number(lv_range_max);
                if ln_end_id is null
                then
                    select max(assay_id)
                    into ln_end_id
                    from data_mig.assay;

                end if;

                for rec_assay in cur_assay(ln_start_id, ln_end_id)
                loop
                    if rec_assay.assay_id = ln_start_id
                    then
                        begin
                            load_reference;    -- this could be handled on the fly, but we want them all
                        exception
                        when others
                        then
                            null;   --trap the error if reference is already loaded
                        end;
                    end if;

                    load_assay (rec_assay.assay_id);
                    commit;

                end loop;

                --reset_sequence;
            end if;
        end if;

    exception
    when le_not_a_range
    then
        null;
    end load_assay;
end load_data;

/