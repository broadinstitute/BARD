
-- Sequence Alter SQL

CREATE SEQUENCE PROJECT_STEP_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

-- Standard Alter Table SQL

ALTER TABLE PROJECT DROP CONSTRAINT CK_PROJECT_EXTRACTION
;
ALTER TABLE PROJECT MODIFY(READY_FOR_EXTRACTION  DEFAULT 'Pending')
;
ALTER TABLE PROJECT ADD CONSTRAINT CK_PROJECT_EXTRACTION
CHECK (ready_for_extraction IN ('Pending', 'Ready', 'Started', 'Complete'))
;
COMMENT ON COLUMN PROJECT_STEP_ITEM.VALUE_DISPLAY IS
'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
