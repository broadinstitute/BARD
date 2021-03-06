ALTER TABLE ASSAY RENAME COLUMN APPROVED_BY TO APPROVED_BY_TEMP;
ALTER TABLE ASSAY ADD APPROVED_BY NUMBER;

ALTER TABLE ASSAY ADD CONSTRAINT FK_ASSAY_APPROVED_BY_PERSON
FOREIGN KEY (APPROVED_BY)
REFERENCES PERSON (PERSON_ID)
ENABLE VALIDATE
;

ALTER TABLE PROJECT RENAME COLUMN APPROVED_BY TO APPROVED_BY_TEMP;
ALTER TABLE PROJECT ADD APPROVED_BY NUMBER;

ALTER TABLE PROJECT ADD CONSTRAINT FK_PROJECT_APPROVED_BY_PERSON
FOREIGN KEY (APPROVED_BY)
REFERENCES PERSON (PERSON_ID)
ENABLE VALIDATE
;

ALTER TABLE EXPERIMENT RENAME COLUMN APPROVED_BY TO APPROVED_BY_TEMP;
ALTER TABLE EXPERIMENT ADD APPROVED_BY NUMBER;

ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXPMT_APPROVED_BY_PERSON
FOREIGN KEY (APPROVED_BY)
REFERENCES PERSON (PERSON_ID)
ENABLE VALIDATE
;