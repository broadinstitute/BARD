--
-- PACKAGE: LOAD_DATA
--
CREATE OR REPLACE package load_data
as
    procedure reset_sequence;

    procedure Load_reference;

    procedure Load_assay (an_assay_id in number default null);

    procedure Load_assay_with_result (an_assay_id in number default null);

    procedure load_assay (av_assay_set in VARCHAR2,
                          ab_w_results  IN BOOLEAN DEFAULT false);

end load_data;
/
CREATE OR REPLACE package body load_data
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
    procedure reset_sequence
    as
    cursor cur_sequence
    is
    select sequence_name
    from user_sequences
    WHERE sequence_name LIKE '%_ID_SEQ';

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
                    || ' increment by 1 nominvalue maxvalue 2147483648 nocycle ';
            IF rec_sequence.sequence_name = 'RESULT_ID_SEQ'
            THEN
                lv_create_sql := lv_create_sql || 'cache 10000 noorder';
            ELSE
                lv_create_sql := lv_create_sql || 'cache 20 noorder';
            END IF;
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

END reset_sequence;


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
                UNIT_ID,
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
                UNIT_ID,      ----------------- WAIT FOR dATA mIG
                BARD_URI,
                EXTERNAL_URL,
                READY_FOR_EXTRACTION,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.element
            order by nvl(unit_ID, -1);

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
                (UNIT_CONVERSION_ID,
                FROM_UNIT_ID,
                TO_UNIT_ID,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select UNIT_CONVERSION_ID,
                FROM_UNIT_ID,
                TO_UNIT_ID,
                MULTIPLIER,
                OFFSET,
                FORMULA,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.unit_conversion;

            insert into bard_tree (          NODE_ID,
                                             PARENT_NODE_ID,
                                             ELEMENT_ID,
                                             ELEMENT_STATUS,
                                             LABEL,
                                             IS_LEAF,
                                             FULL_PATH,
                                             DESCRIPTION,
                                             ABBREVIATION,
                                             SYNONYMS,
                                             EXTERNAL_URL,
                                             UNIT_ID
            )
              select
                NODE_ID,
                PARENT_NODE_ID,
                ELEMENT_ID,
                ELEMENT_STATUS,
                LABEL,
                IS_LEAF,
                FULL_PATH,
                DESCRIPTION,
                ABBREVIATION,
                SYNONYMS,
                EXTERNAL_URL,
                UNIT_ID
              from data_mig.bard_tree d where not exists ( select 1 from bard_tree s where s.node_id = d.node_id);

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

        load_assay (an_assay_id);

        for rec_assay in cur_assay
        LOOP
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
                    REPLICATE_NO,
                    VALUE_DISPLAY,
                    VALUE_NUM,
                    VALUE_MIN,
                    VALUE_MAX,
                    QUALIFIER,
                    EXPERIMENT_ID,
                    SUBSTANCE_ID,
                    RESULT_TYPE_ID,
                    STATS_MODIFIER_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select RESULT_ID,
                    RESULT_STATUS,
                    READY_FOR_EXTRACTION,
                    REPLICATE_NO,
                    VALUE_DISPLAY,
                    VALUE_NUM,
                    VALUE_MIN,
                    VALUE_MAX,
                    QUALIFIER,
                    EXPERIMENT_ID,
                    SUBSTANCE_ID,
                    RESULT_TYPE_ID,
                    STATS_MODIFIER_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.result
                where experiment_id = rec_experiment.experiment_id;

                insert into rslt_context_item
                    (RSLT_CONTEXT_ITEM_ID,
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
                    MODIFIED_BY)
                select RSLT_CONTEXT_ITEM_ID,
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
                from data_mig.rslt_context_item  rci
                where EXISTS (SELECT 1
                      FROM data_mig.result r
                      WHERE r.result_id = rci.result_id
                        AND r.experiment_id = rec_experiment.experiment_id);

                -- ASSUMES ALL HIERARCHIES EXIST ONLY WITHIN THE CONTEXT OF AN EXPERIMENT
                insert into result_hierarchy
                    (RESULT_HIERARCHY_ID,
                    RESULT_ID,
                    PARENT_RESULT_ID,
                    HIERARCHY_TYPE,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select RESULT_HIERARCHY_ID,
                    RESULT_ID,
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

        -- reset_sequence;

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
                ASSAY_SHORT_NAME,
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
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select ASSAY_CONTEXT_ID,
                ASSAY_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.assay_context
            where assay_id = rec_assay.assay_id;

            insert into measure
                (MEASURE_ID,
                ASSAY_ID,
                PARENT_MEASURE_ID,
                RESULT_TYPE_ID,
                STATS_MODIFIER_ID,
                ENTRY_UNIT_ID,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
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
            from data_mig.measure
            where assay_id = rec_assay.assay_id
            connect by prior measure_id = parent_measure_id
            start with (parent_measure_id is NULL
                    OR parent_measure_id = measure_id);

            for rec_experiment in cur_experiment(rec_assay.assay_id)
            loop
                insert into experiment
                    (EXPERIMENT_ID,
                    EXPERIMENT_NAME,
                    EXPERIMENT_STATUS,
                    READY_FOR_EXTRACTION,
                    CONFIDENCE_LEVEL,
                    ASSAY_ID,
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
                    CONFIDENCE_LEVEL,
                    ASSAY_ID,
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

                INSERT INTO exprmt_context
                    (exprmt_context_id,
                    experiment_id,
                    context_name,
                    context_group,
                    version,
                    date_created,
                    last_updated,
                    modified_by
                    )
                SELECT exprmt_context_id,
                    experiment_id,
                    context_name,
                    context_group,
                    version,
                    date_created,
                    last_updated,
                    modified_by
                FROM data_mig.exprmt_context
                WHERE experiment_id = rec_experiment.experiment_id;

                INSERT INTO exprmt_measure
                    (exprmt_measure_id,
                    experiment_id,
                    measure_id,
                    parent_exprmt_measure_id,
                    parent_child_relationship,
                    version,
                    date_created,
                    last_updated,
                    modified_by
                    )
                SELECT exprmt_measure_id,
                    experiment_id,
                    measure_id,
                    parent_exprmt_measure_id,
                    parent_child_relationship,
                    version,
                    date_created,
                    last_updated,
                    modified_by
                FROM data_mig.exprmt_measure
                WHERE experiment_id = rec_experiment.experiment_id
                ORDER BY Nvl(parent_exprmt_measure_id, 0);

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
                     where experiment_id = rec_experiment.experiment_id)
                 and not exists (select 1 from project p
                            where p.project_id = dp.project_id);

                insert into project_experiment
                    (PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    EXPERIMENT_ID,
                    STAGE_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
                select PROJECT_EXPERIMENT_ID,
                    PROJECT_ID,
                    EXPERIMENT_ID,
                    STAGE_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
                from data_mig.project_EXPERIMENT pe
                where experiment_id = rec_experiment.experiment_id
                  AND NOT EXISTS (SELECT 1
                          FROM project_EXPERIMENT ps
                          WHERE ps.project_experiment_id = pe.project_experiment_id);

                -- assumes all the external systems have been loaded (see load_reference)
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


            end loop;   -- for each experiment


            commit; -- for each assay

        end loop;

         IF an_assay_id IS NULL
         then--- insert into project (for all ones not loaded yet)
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

        END IF;

        INSERT INTO project_step
               (project_step_id,
                next_project_experiment_id,
                prev_project_experiment_id,
                edge_name,
                version,
                date_created,
                last_updated,
                modified_by)
        SELECT  project_step_id,
                next_project_experiment_id,
                prev_project_experiment_id,
                edge_name,
                version,
                date_created,
                last_updated,
                modified_by
        FROM data_mig.project_step ps
        WHERE EXISTS (SELECT 1
            FROM project_experiment pe
            WHERE ps.next_project_experiment_id = pe.project_experiment_id)
         AND exists (SELECT 1
            FROM project_experiment pe
            WHERE ps.prev_project_experiment_id = pe.project_experiment_id)
         AND NOT EXISTS (SELECT 1
            from project_step ps2
            WHERE ps2.project_step_id = ps.project_step_id);

        INSERT INTO project_experiment
            SELECT PROJECT_EXPERIMENT_ID ,
                    EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY
            FROM data_mig.project_experiment pe
            WHERE EXISTS (select 1 from experiment e
                        where e.experiment_id = pe.experiment_id)
              AND EXISTS (select 1 from project e
                        where e.project_id = pe.project_id)
              AND NOT EXISTS (select 1 from project_experiment e
                        where e.project_experiment_id = pe.project_experiment_id);


        INSERT INTO assay_context_measure
              (assay_context_measure_id,
              assay_context_id,
              measure_id,
              version,
              date_created,
              last_updated,
              modified_by)
        SELECT
              assay_context_measure_id,
              assay_context_id,
              measure_id,
              version,
              date_created,
              last_updated,
              modified_by
        from data_mig.assay_context_measure acm
        where NOT EXISTS (SELECT 1
                FROM assay_context_measure acm2
                WHERE acm2.assay_context_measure_id = acm.assay_context_measure_id)
          AND EXISTS (SELECT 1
                FROM assay_context ac
                WHERE ac.assay_context_id = acm.assay_context_id)
          AND eXISTS (SELECT 1
                FROM measure ac
                WHERE ac.measure_id = acm.measure_id);


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
        insert into project_context
            (PROJECT_CONTEXT_ID,
            PROJECT_ID,
            CONTEXT_NAME,
            CONTEXT_GROUP,
            DISPLAY_ORDER,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY)
        select PROJECT_CONTEXT_ID,
            PROJECT_ID,
            CONTEXT_NAME,
            CONTEXT_GROUP,
            DISPLAY_ORDER,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.project_context dpc
        where EXISTS (SELECT 1
                      FROM project p
                      WHERE p.project_id = dpc.project_id)
          AND NOT EXISTS (SELECT 1
                      FROM project_context pc
                      WHERE pc.project_context_id = dpc.project_context_id);

        insert into project_context_item
            (PROJECT_CONTEXT_ITEM_ID,
            PROJECT_CONTEXT_ID,
            DISPLAY_ORDER,
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
            PROJECT_CONTEXT_ID,
            DISPLAY_ORDER,
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
        from data_mig.project_context_item dpci
        where NOT EXISTS (SELECT 1
                      FROM project_context_item pci
                      WHERE pci.project_context_item_id = dpci.project_context_item_id)
          AND EXISTS (SELECT 1
                      FROM project_context pc
                      WHERE pc.project_context_id = dpci.project_context_id);

        -- insert prjct_exprmt context
        insert into prjct_exprmt_context
            (prjct_exprmt_CONTEXT_ID,
            PROJECT_EXPERIMENT_ID,
            CONTEXT_NAME,
            CONTEXT_GROUP,
            DISPLAY_ORDER,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY)
        select prjct_exprmt_CONTEXT_ID,
            PROJECT_EXPERIMENT_ID,
            CONTEXT_NAME,
            CONTEXT_GROUP,
            DISPLAY_ORDER,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        from data_mig.prjct_exprmt_context dpc
        where EXISTS (SELECT 1
                      FROM project_experiment p
                      WHERE p.project_experiment_id = dpc.project_experiment_id)
          AND NOT EXISTS (SELECT 1
                      FROM prjct_exprmt_context pc
                      WHERE pc.prjct_exprmt_CONTEXT_ID = dpc.prjct_exprmt_CONTEXT_ID);

        insert into prjct_exprmt_cntxt_item
            (prjct_exprmt_cntxt_ITEM_ID,
            PRJCT_EXPRMT_CONTEXT_ID,
            DISPLAY_ORDER,
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
        select prjct_exprmt_cntxt_ITEM_ID,
            PRJCT_EXPRMT_CONTEXT_ID,
            DISPLAY_ORDER,
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
        from data_mig.prjct_exprmt_cntxt_item dpci
        where NOT EXISTS (SELECT 1
                      FROM prjct_exprmt_cntxt_item pci
                      WHERE pci.prjct_exprmt_cntxt_ITEM_ID = dpci.prjct_exprmt_cntxt_ITEM_ID)
          AND EXISTS (SELECT 1
                      FROM prjct_exprmt_context pc
                      WHERE pc.PRJCT_EXPRMT_CONTEXT_ID = dpci.PRJCT_EXPRMT_CONTEXT_ID);

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
        from data_mig.assay_context_item  aci
        where EXISTS (SELECT 1
                    from assay_context ac
                    where ac.assay_context_id = aci.assay_context_id)
          AND NOT EXISTS (SELECT 1
                    from assay_context_item aci2
                    where aci2.assay_context_Item_id = aci.assay_context_item_id);


        INSERT INTO exprmt_context_Item
              (EXPRMT_CONTEXT_ITEM_ID,
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
                MODIFIED_BY)
          SELECT EXPRMT_CONTEXT_ITEM_ID,
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
          FROM data_mig.exprmt_CONTEXT_ITEM  eci
          WHERE EXISTS (SELECT 1
                    from exprmt_context ec
                    where eci.exprmt_context_id = eci.exprmt_context_id)
            AND NOT EXISTS (SELECT 1
                    from exprmt_context_item eci2
                    where eci2.exprmt_context_Item_id = eci.exprmt_context_item_id);

          INSERT INTO project_step
              (PROJECT_STEP_ID,
              VERSION,
              NEXT_PROJECT_EXPERIMENT_ID,
              PREV_PROJECT_EXPERIMENT_ID,
              DATE_CREATED,
              EDGE_NAME,
              LAST_UPDATED,
              MODIFIED_BY)
          SELECT PROJECT_STEP_ID,
              VERSION,
              NEXT_PROJECT_EXPERIMENT_ID,
              PREV_PROJECT_EXPERIMENT_ID,
              DATE_CREATED,
              EDGE_NAME,
              LAST_UPDATED,
              MODIFIED_BY
          FROM data_mig.project_step ps
          WHERE NOT EXISTS (SELECT 1
                    FROM project_step ps2
                    WHERE ps2.project_step_id = ps.project_step_id)
            AND EXISTS (SELECT 1
                    FROM project_experiment pe
                    WHERE pe.project_experiment_id = ps.prev_project_experiment_id)
            AND EXISTS (SELECT 1
                    FROM project_experiment pe
                    WHERE pe.project_experiment_id = ps.next_project_experiment_id);

                    -- insert step context
            insert into step_context
                (STEP_CONTEXT_ID,
                PROJECT_STEP_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY)
            select STEP_CONTEXT_ID,
                PROJECT_STEP_ID,
                CONTEXT_NAME,
                CONTEXT_GROUP,
                DISPLAY_ORDER,
                VERSION,
                DATE_CREATED,
                LAST_UPDATED,
                MODIFIED_BY
            from data_mig.step_context dpc
            where EXISTS (SELECT 1
                          FROM project_step p
                          WHERE p.project_step_id = dpc.project_step_id)
              AND NOT EXISTS (SELECT 1
                          FROM step_context pc
                          WHERE pc.step_context_id = dpc.step_context_id);

            insert into step_context_item
                (STEP_CONTEXT_ITEM_ID,
                STEP_CONTEXT_ID,
                DISPLAY_ORDER,
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
                MODIFIED_BY)
            select STEP_CONTEXT_ITEM_ID,
                STEP_CONTEXT_ID,
                DISPLAY_ORDER,
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
            from data_mig.step_context_item dpci
            where NOT EXISTS (SELECT 1
                          FROM step_context_item pci
                          WHERE pci.step_context_item_id = dpci.step_context_item_id)
              AND EXISTS (SELECT 1
                          FROM step_context pc
                          WHERE pc.step_context_id = dpci.step_context_id);


        commit;

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

        reset_sequence;

     end load_assay;
end load_data;
/