-- Try to make last updated field not null to match GORM
update EXPERIMENT_DOCUMENT set LAST_UPDATED = DATE_CREATED WHERE LAST_UPDATED IS NULL;
-- Add not null constraint to LAST Updated column
--alter table EXPERIMENT_DOCUMENT modify (LAST_UPDATED not null);
-- Drop the constraint on the document type
alter table EXPERIMENT_DOCUMENT drop constraint CK_EXPERIMENT_DOCUMENT_TYPE;
-- Re-apply constraint with ; replace Paper with Publication
alter table EXPERIMENT_DOCUMENT add constraint CK_EXPERIMENT_DOCUMENT_TYPE CHECK (Document_Type In ('Description', 'Protocol', 'Comments', 'Publication', 'External URL', 'Other') );
