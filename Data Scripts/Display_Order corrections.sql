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

UPDATE project_context_item eci
SET project_context_item_id =
    (SELECT Count(*)
      FROM project_context_item  eci2
      WHERE eci2.project_context_item_id <= eci.project_context_item_id);

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



begin
    reset_sequences;
END;
/

BEGIN
update_assay_short_name;
END;
/

