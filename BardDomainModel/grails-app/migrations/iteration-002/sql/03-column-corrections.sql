ALTER TABLE measure_context_item MODIFY (ext_value_id VARCHAR2(100));
ALTER TABLE measure_context_item MODIFY (value_display VARCHAR2(512));

ALTER TABLE RESULT_CONTEXT_ITEM ADD ext_value_id varchar2(100) NULL ;
ALTER TABLE result_context_item MODIFY (value_display VARCHAR2(512));
