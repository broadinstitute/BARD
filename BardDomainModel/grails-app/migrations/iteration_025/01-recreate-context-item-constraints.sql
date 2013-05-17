ALTER TABLE ASSAY_CONTEXT DROP CONSTRAINT UK_ASSAY_CONTEXT
;
ALTER TABLE ASSAY_CONTEXT ADD CONSTRAINT UK_ASSAY_CONTEXT_ORDER UNIQUE(ASSAY_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE ASSAY_CONTEXT_ITEM DROP CONSTRAINT UK_ASSAY_CONTEXT_ITEM
;
ALTER TABLE ASSAY_CONTEXT_ITEM_ORDER ADD CONSTRAINT UK_ASSAY_CONTEXT_ITEM UNIQUE(ASSAY_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE EXPRMT_CONTEXT DROP CONSTRAINT UK_EXPRMT_CONTEXT
;
ALTER TABLE EXPRMT_CONTEXT ADD CONSTRAINT UK_EXPRMT_CONTEXT_ORDER UNIQUE(EXPERIMENT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE EXPRMT_CONTEXT_ITEM DROP CONSTRAINT UK_EXPRMT_CONTEXT_ITEM
;
ALTER TABLE EXPRMT_CONTEXT_ITEM ADD CONSTRAINT UK_EXPRMT_CONTEXT_ITEM_ORDER UNIQUE(EXPRMT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PRJCT_EXPRMT_CNTXT_ITEM DROP CONSTRAINT UK_PRJCT_EXPRMT_CNTXT_ITEM
;
ALTER TABLE PRJCT_EXPRMT_CNTXT_ITEM ADD CONSTRAINT UK_PRJCT_EXPRMT_CNTXT_ITEM_ORDER UNIQUE(PRJCT_EXPRMT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT DROP CONSTRAINT UK_PRJCT_EXPRMT_CNTXT
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT ADD CONSTRAINT UK_PRJCT_EXPRMT_CNTXT_ORDER UNIQUE(PROJECT_EXPERIMENT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PROJECT_CONTEXT DROP CONSTRAINT UK_PROJECT_CONTEXT
;
ALTER TABLE PROJECT_CONTEXT ADD CONSTRAINT UK_PROJECT_CONTEXT_ORDER UNIQUE(PROJECT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PROJECT_CONTEXT_ITEM DROP CONSTRAINT UK_PROJECT_CONTEXT_ITEM
;
ALTER TABLE PROJECT_CONTEXT_ITEM ADD CONSTRAINT UK_PROJECT_CONTEXT_ITEM_ORDER UNIQUE(PROJECT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE STEP_CONTEXT DROP CONSTRAINT UK_STEP_CONTEXT
;
ALTER TABLE STEP_CONTEXT ADD CONSTRAINT UK_STEP_CONTEXT_ORDER UNIQUE(PROJECT_STEP_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE STEP_CONTEXT_ITEM DROP CONSTRAINT UK_STEP_CONTEXT_ITEM
;
ALTER TABLE STEP_CONTEXT_ITEM ADD CONSTRAINT UK_STEP_CONTEXT_ITEM_ORDER UNIQUE(STEP_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
