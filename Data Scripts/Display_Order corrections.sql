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
       AND aci2.assay_context_id < aci.assay_context_id)
WHERE display_order !=
      (SELECT Count(*)
     FROM assay_context aci2
     WHERE aci2.assay_id = aci.assay_id
       AND aci2.assay_context_id < aci.assay_context_id);

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

COMMIT;



