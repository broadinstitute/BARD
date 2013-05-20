-- add display name column, to hold human readable role names
ALTER TABLE ROLE ADD DISPLAY_NAME VARCHAR2(40) NULL
;
-- copy current authority column to the display name column
UPDATE ROLE R1 SET R1.DISPLAY_NAME =(SELECT R2.AUTHORITY FROM ROLE R2 WHERE R1.ROLE_ID = R2.ROLE_ID)
;
-- upper case all the current authorities
UPDATE ROLE SET AUTHORITY = UPPER(AUTHORITY)
;
-- Replace single space with underscore
UPDATE ROLE SET AUTHORITY = REPLACE(ROLE.AUTHORITY, ' ', '_')
;
--Add new CURATOR role
INSERT INTO ROLE (ROLE_ID, AUTHORITY,DISPLAY_NAME,VERSION,MODIFIED_BY,DATE_CREATED) VALUES (ROLE_ID_SEQ.NEXTVAL,'CURATOR','Curator',0,'jasiedu',SYSDATE)
;
