ALTER TABLE MEASURE ADD (Parent_Child_Relationship VARCHAR2(20)  NULL)
;
ALTER TABLE MEASURE
    ADD CONSTRAINT CK_MEASURE_PC_RELATIONSHIP
CHECK (Parent_Child_Relationship in('is calculated from', 'is related to'))
;
