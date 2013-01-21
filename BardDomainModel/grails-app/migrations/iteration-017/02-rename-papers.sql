ALTER TABLE assay_document drop constraint ck_assay_document_type
;

ALTER TABLE project_document drop constraint ck_project_document_type
;

update assay_document set document_type = 'Publication' where document_type = 'Paper'
;

update project_document set document_type = 'Publication' where document_type = 'Paper'
;

-- made check constraint a little more permissive until data-mig gets upgraded.  Otherwise
ALTER TABLE assay_document
  ADD CONSTRAINT ck_assay_document_type CHECK (
    Document_Type IN ('Description', 'Protocol', 'Comments', 'Publication', 'External URL', 'Other'))
;

ALTER TABLE project_document
  ADD CONSTRAINT ck_project_document_type CHECK (
    Document_Type IN ('Description', 'Protocol', 'Comments', 'Publication', 'External URL', 'Other'))
;

