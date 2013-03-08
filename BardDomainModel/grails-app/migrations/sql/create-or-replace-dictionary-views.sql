-- Update Views SQL, explicitly call out the column with table name although it is not necessary.

CREATE OR REPLACE VIEW ASSAY_ELEMENT AS
SELECT El.ELEMENT_ID Element_ID, El.ELEMENT_STATUS Element_Status, El.LABEL Label, El.DESCRIPTION Description, El.ABBREVIATION Abbreviation, El.SYNONYMS Synonyms, El.UNIT_ID Unit_ID, El.BARD_URI BARD_URI, El.EXTERNAL_URL External_URL, El.READY_FOR_EXTRACTION ready_for_extraction, El.VERSION Version, El.DATE_CREATED Date_Created, El.LAST_UPDATED Last_Updated, El.MODIFIED_BY Modified_by
FROM ELEMENT El
WHERE El.ELEMENT_ID in (select ASSAY_DESCRIPTOR_TREE.ELEMENT_ID from ASSAY_DESCRIPTOR_TREE)
;
CREATE OR REPLACE VIEW Biology_element AS
SELECT El.Element_ID Element_ID, El.Element_Status Element_Status, El.Label Label, El.Description Description, El.Abbreviation Abbreviation, El.Synonyms Synonyms, El.Unit_ID Unit_ID, El.BARD_URI BARD_URI, El.External_URL External_URL, El.ready_for_extraction ready_for_extraction, El.Version Version, El.Date_Created Date_Created, El.Last_Updated Last_Updated, El.Modified_by Modified_by
FROM Element El
WHERE el.element_id in (select biology_descriptor_tree.element_id from biology_descriptor_tree)
;
CREATE OR REPLACE VIEW INSTANCE_ELEMENT AS
SELECT El.ELEMENT_ID Element_ID, El.ELEMENT_STATUS Element_Status, El.LABEL Label, El.DESCRIPTION Description, El.ABBREVIATION Abbreviation, El.SYNONYMS Synonyms, El.UNIT_ID Unit_ID, El.BARD_URI BARD_URI, El.EXTERNAL_URL External_URL, El.READY_FOR_EXTRACTION ready_for_extraction, El.VERSION Version, El.DATE_CREATED Date_Created, El.LAST_UPDATED Last_Updated, El.MODIFIED_BY Modified_by
FROM ELEMENT El
WHERE el.ELEMENT_ID in (select INSTANCE_DESCRIPTOR_TREE.ELEMENT_ID from INSTANCE_DESCRIPTOR_TREE)
;
CREATE OR REPLACE VIEW LABORATORY_ELEMENT
(Element_ID, Element_Status, Label, Description, Abbreviation, Synonyms, Unit_ID, BARD_URI, External_URL, ready_for_extraction, Version, Date_Created, Last_Updated, Modified_by) AS
SELECT El.ELEMENT_ID, El.ELEMENT_STATUS, El.LABEL, El.DESCRIPTION, El.ABBREVIATION, El.SYNONYMS, El.UNIT_ID, El.BARD_URI, El.EXTERNAL_URL, El.READY_FOR_EXTRACTION, El.VERSION, El.DATE_CREATED, El.LAST_UPDATED, El.MODIFIED_BY
FROM ELEMENT El
WHERE ELEMENT_ID in (select LABORATORY_TREE.LABORATORY_ID from LABORATORY_TREE)
;
CREATE OR REPLACE VIEW RESULT_TYPE_ELEMENT AS
SELECT El.ELEMENT_ID Element_ID, El.ELEMENT_STATUS Element_Status, El.LABEL Label, El.DESCRIPTION Description, El.ABBREVIATION Abbreviation, El.SYNONYMS Synonyms, El.UNIT_ID Unit_ID, El.BARD_URI BARD_URI, El.EXTERNAL_URL External_URL, El.READY_FOR_EXTRACTION ready_for_extraction, El.VERSION Version, El.DATE_CREATED Date_Created, El.LAST_UPDATED Last_Updated, El.MODIFIED_BY Modified_by
FROM ELEMENT El
WHERE el.ELEMENT_ID in (select RESULT_TYPE_TREE.RESULT_TYPE_ID from RESULT_TYPE_TREE)
;
CREATE OR REPLACE VIEW STAGE_ELEMENT AS
SELECT El.ELEMENT_ID Element_ID, El.ELEMENT_STATUS Element_Status, El.LABEL Label, El.DESCRIPTION Description, El.ABBREVIATION Abbreviation, El.SYNONYMS Synonyms, El.UNIT_ID Unit_ID, El.BARD_URI BARD_URI, El.EXTERNAL_URL External_URL, El.READY_FOR_EXTRACTION ready_for_extraction, El.VERSION Version, El.DATE_CREATED Date_Created, El.LAST_UPDATED Last_Updated, El.MODIFIED_BY Modified_by
FROM ELEMENT El
WHERE el.ELEMENT_ID in (select STAGE_TREE.STAGE_ID from STAGE_TREE)
;
CREATE OR REPLACE VIEW UNIT_ELEMENT AS
SELECT El.ELEMENT_ID Element_ID, El.ELEMENT_STATUS Element_Status, El.LABEL Label, El.DESCRIPTION Description, El.ABBREVIATION Abbreviation, El.SYNONYMS Synonyms, El.UNIT_ID Unit_ID, El.BARD_URI BARD_URI, El.EXTERNAL_URL External_URL, El.READY_FOR_EXTRACTION ready_for_extraction, El.VERSION Version, El.DATE_CREATED Date_Created, El.LAST_UPDATED Last_Updated, El.MODIFIED_BY Modified_by
FROM ELEMENT El
WHERE El.ELEMENT_ID in (select UNIT_TREE.UNIT_ID from UNIT_TREE)
;