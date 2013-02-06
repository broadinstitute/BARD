ALTER TABLE ELEMENT DROP CONSTRAINT CK_ELEMENT_EXTRACTION
;
ALTER TABLE ELEMENT ADD CONSTRAINT CK_ELEMENT_EXTRACTION
CHECK (ready_for_extraction IN ('Not Ready', 'Ready', 'Started', 'Complete'))
;
ALTER TABLE EXPERIMENT DROP CONSTRAINT CK_EXPERIMENT_EXTRACTION
;
ALTER TABLE EXPERIMENT ADD CONSTRAINT CK_EXPERIMENT_EXTRACTION
CHECK (ready_for_extraction IN ('Not Ready', 'Ready', 'Started', 'Complete'))
;
ALTER TABLE PROJECT DROP CONSTRAINT CK_PROJECT_EXTRACTION
;
UPDATE project
SET ready_for_extraction = 'Not Ready'
WHERE ready_for_extraction = 'Pending'
;
ALTER TABLE PROJECT ADD CONSTRAINT CK_PROJECT_EXTRACTION
CHECK (ready_for_extraction IN ('Not Ready', 'Ready', 'Started', 'Complete'))
;
ALTER TABLE RESULT DROP CONSTRAINT CK_RESULT_EXTRACTION
;
ALTER TABLE RESULT ADD CONSTRAINT CK_RESULT_EXTRACTION
CHECK (Ready_For_Extraction IN ('Not Ready', 'Ready', 'Started', 'Complete'))
;
ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_EXTRACTION
;
ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_STATUS
;
ALTER TABLE ASSAY MODIFY(ASSAY_STATUS  DEFAULT 'Draft')
;
ALTER TABLE ASSAY ADD CONSTRAINT CK_ASSAY_EXTRACTION
CHECK (ready_for_extraction IN ('Not Ready', 'Ready', 'Started', 'Complete'))
;
UPDATE assay
SET assay_status = Decode( assay_status,
                    'Pending', 'Draft',
                    'Active', 'Finished',
                    assay_status)
;
ALTER TABLE ASSAY ADD CONSTRAINT CK_ASSAY_STATUS
CHECK (Assay_Status IN ('Draft', 'Witnessed', 'Finished', 'Measures Done', 'Annotations Done', 'Retired'))
;
