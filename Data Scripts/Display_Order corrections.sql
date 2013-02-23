BEGIN
p_pbs_context.set_username('schatwin');
END;
/




UPDATE assay_context_item aci
SET display_order =
    (SELECT Count(*)
     FROM assay_context_item aci2
     WHERE aci2.assay_context_id = aci.assay_context_id
       AND aci2.assay_context_item_id < aci.assay_context_item_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM assay_context_item aci2
     WHERE aci2.assay_context_id = aci.assay_context_id
       AND aci2.assay_context_item_id < aci.assay_context_item_id);

UPDATE exprmt_context_item aci
SET display_order =
    (SELECT Count(*)
     FROM exprmt_context_item aci2
     WHERE aci2.exprmt_context_id = aci.exprmt_context_id
       AND aci2.exprmt_context_item_id < aci.exprmt_context_item_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM exprmt_context_item aci2
     WHERE aci2.exprmt_context_id = aci.exprmt_context_id
       AND aci2.exprmt_context_item_id < aci.exprmt_context_item_id);

UPDATE prjct_exprmt_cntxt_item aci
SET display_order =
    (SELECT Count(*)
     FROM prjct_exprmt_cntxt_item aci2
     WHERE aci2.prjct_exprmt_context_id = aci.prjct_exprmt_context_id
       AND aci2.prjct_exprmt_cntxt_item_id < aci.prjct_exprmt_cntxt_item_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM prjct_exprmt_cntxt_item aci2
     WHERE aci2.prjct_exprmt_context_id = aci.prjct_exprmt_context_id
       AND aci2.prjct_exprmt_cntxt_item_id < aci.prjct_exprmt_cntxt_item_id);

UPDATE project_context_item aci
SET display_order =
    (SELECT Count(*)
     FROM project_context_item aci2
     WHERE aci2.project_context_id = aci.project_context_id
       AND aci2.project_context_item_id < aci.project_context_item_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM project_context_item aci2
     WHERE aci2.project_context_id = aci.project_context_id
       AND aci2.project_context_item_id < aci.project_context_item_id);

UPDATE step_context_item aci
SET display_order =
    (SELECT Count(*)
     FROM step_context_item aci2
     WHERE aci2.step_context_id = aci.step_context_id
       AND aci2.step_context_item_id < aci.step_context_item_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM step_context_item aci2
     WHERE aci2.step_context_id = aci.step_context_id
       AND aci2.step_context_item_id < aci.step_context_item_id);

UPDATE rslt_context_item aci
SET display_order =
    (SELECT Count(*)
     FROM rslt_context_item aci2
     WHERE aci2.result_id = aci.result_id
       AND aci2.rslt_context_item_id < aci.rslt_context_item_id)
WHERE display_order !=
    (SELECT Count(*)
      FROM rslt_context_item aci2
     WHERE aci2.result_id = aci.result_id
       AND aci2.rslt_context_item_id < aci.rslt_context_item_id);

----------------------------------------------------------------------------------------------------------
--
-----------------------------------------------------------------------------------------------------------
UPDATE step_context aci
SET display_order =
    (SELECT Count(*)
     FROM step_context aci2
     WHERE aci2.project_step_id = aci.project_step_id
       AND aci2.step_context_id < aci.step_context_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM step_context aci2
     WHERE aci2.project_step_id = aci.project_step_id
       AND aci2.step_context_id < aci.step_context_id);

UPDATE project_context aci
SET display_order =
    (SELECT Count(*)
     FROM project_context aci2
     WHERE aci2.project_id = aci.project_id
       AND aci2.project_context_id < aci.project_context_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM project_context aci2
     WHERE aci2.project_id = aci.project_id
       AND aci2.project_context_id < aci.project_context_id);

UPDATE assay_context aci
SET display_order =
    (SELECT Count(*)
     FROM assay_context aci2
     WHERE aci2.assay_id = aci.assay_id
       AND rpad(aci2.context_group, 256) || rpad(aci2.context_name, 128) || lpad( aci2.assay_context_id, 19)
          < rpad(aci.context_group, 256) || rpad(aci.context_name, 128) || lpad( aci.assay_context_id, 19) )
WHERE display_order !=
      (SELECT Count(*)
     FROM assay_context aci2
     WHERE aci2.assay_id = aci.assay_id
       AND rpad(aci2.context_group, 256) || rpad(aci2.context_name, 128) || lpad( aci2.assay_context_id, 19)
          < rpad(aci.context_group, 256) || rpad(aci.context_name, 128) || lpad( aci.assay_context_id, 19) );

UPDATE exprmt_context aci
SET display_order =
    (SELECT Count(*)
     FROM exprmt_context aci2
     WHERE aci2.experiment_id = aci.experiment_id
       AND aci2.exprmt_context_id < aci.exprmt_context_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM exprmt_context aci2
     WHERE aci2.experiment_id = aci.experiment_id
       AND aci2.exprmt_context_id < aci.exprmt_context_id);

UPDATE prjct_exprmt_context aci
SET display_order =
    (SELECT Count(*)
     FROM prjct_exprmt_context aci2
     WHERE aci2.project_experiment_id = aci.project_experiment_id
       AND aci2.prjct_exprmt_context_id < aci.prjct_exprmt_context_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM prjct_exprmt_context aci2
     WHERE aci2.project_experiment_id = aci.project_experiment_id
       AND aci2.prjct_exprmt_context_id < aci.prjct_exprmt_context_id);

COMMIT;

/*
UPDATE exprmt_context_item eci
SET exprmt_context_item_id =
    (SELECT Count(*)
      FROM exprmt_context_item  eci2
      WHERE eci2.exprmt_context_item_id <= eci.exprmt_context_item_id);

UPDATE prjct_exprmt_cntxt_item eci
SET prjct_exprmt_cntxt_item_id =
    (SELECT Count(*)
      FROM prjct_exprmt_cntxt_item  eci2
      WHERE eci2.prjct_exprmt_cntxt_item_id <= eci.prjct_exprmt_cntxt_item_id);

UPDATE exprmt_measure eci
SET exprmt_measure_id =
    (SELECT Count(*)
      FROM exprmt_measure  eci2
      WHERE eci2.exprmt_measure_id <= eci.exprmt_measure_id);

UPDATE assay_context_item eci
SET assay_context_item_id =
    (SELECT Count(*)
      FROM assay_context_item  eci2
      WHERE eci2.assay_context_item_id <= eci.assay_context_item_id);

UPDATE assay_context_measure eci
SET assay_context_measure_id =
    (SELECT Count(*)
      FROM assay_context_measure  eci2
      WHERE eci2.assay_context_measure_id <= eci.assay_context_measure_id);
*/

UPDATE element_hierarchy eci
SET element_hierarchy_id =
    (SELECT Count(*)
      FROM element_hierarchy  eci2
      WHERE eci2.element_hierarchy_id <= eci.element_hierarchy_id);


DECLARE
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

END;
/

