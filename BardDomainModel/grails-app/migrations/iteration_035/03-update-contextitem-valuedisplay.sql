-- Remove = sign from value display column for all context items

update ASSAY_CONTEXT_ITEM set value_display = trim(replace(ASSAY_CONTEXT_ITEM.value_display, '=')) where qualifier = '='  and value_type = 'numeric';
update EXPRMT_CONTEXT_ITEM set value_display = trim(replace(EXPRMT_CONTEXT_ITEM.value_display, '=')) where qualifier = '='  and value_type = 'numeric';
update PROJECT_CONTEXT_ITEM set value_display = trim(replace(PROJECT_CONTEXT_ITEM.value_display, '=')) where qualifier = '='  and value_type = 'numeric';
update RSLT_CONTEXT_ITEM set value_display = trim(replace(RSLT_CONTEXT_ITEM.value_display, '=')) where qualifier = '='  and value_type = 'numeric';
update STEP_CONTEXT_ITEM set value_display = trim(replace(STEP_CONTEXT_ITEM.value_display, '=')) where qualifier = '='  and value_type = 'numeric';