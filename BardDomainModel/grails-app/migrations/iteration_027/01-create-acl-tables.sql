--------------------------------------------------------
--  DDL for Table ACL_CLASS
--------------------------------------------------------
CREATE TABLE ACL_CLASS
(
ID NUMBER(19,0),
CLASS VARCHAR2(255 CHAR)
)
;
CREATE UNIQUE INDEX AK_ACL_CLASS ON ACL_CLASS (ID)
;
--------------------------------------------------------
--  Constraints for Table ACL_CLASS
--------------------------------------------------------
ALTER TABLE ACL_CLASS ADD PRIMARY KEY (ID)
;

--------------------------------------------------------
--  DDL for Table ACL_SID
--------------------------------------------------------
CREATE TABLE ACL_SID
(
ID NUMBER(19,0),
PRINCIPAL NUMBER(1,0),
SID VARCHAR2(255 CHAR)
)
;
--------------------------------------------------------
--  DDL for Index SYS_C0010604548
--------------------------------------------------------
CREATE UNIQUE INDEX AK_ACL_SID ON ACL_SID (SID, PRINCIPAL)
;
--------------------------------------------------------
--  Constraints for Table ACL_SID
--------------------------------------------------------
ALTER TABLE ACL_SID ADD PRIMARY KEY (ID)
;
 
ALTER TABLE ACL_SID ADD UNIQUE (SID, PRINCIPAL)
;

--------------------------------------------------------
--  DDL for Table ACL_OBJECT_IDENTITY
--------------------------------------------------------

CREATE TABLE ACL_OBJECT_IDENTITY
(
ID NUMBER(19,0),
OBJECT_ID_CLASS NUMBER(19,0),
ENTRIES_INHERITING NUMBER(1,0),
OBJECT_ID_IDENTITY NUMBER(19,0),
OWNER_SID NUMBER(19,0),
PARENT_OBJECT NUMBER(19,0)
)
;
--------------------------------------------------------
--  DDL for Index SYS_C0010604543
--------------------------------------------------------
CREATE UNIQUE INDEX AK_ACL_OBJECT_IDENTITY ON ACL_OBJECT_IDENTITY (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY)
;
--------------------------------------------------------
--  Constraints for Table ACL_OBJECT_IDENTITY
--------------------------------------------------------
ALTER TABLE ACL_OBJECT_IDENTITY ADD PRIMARY KEY (ID)
;
 
ALTER TABLE ACL_OBJECT_IDENTITY ADD UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY)
;
--------------------------------------------------------
--  Ref Constraints for Table ACL_OBJECT_IDENTITY
--------------------------------------------------------
ALTER TABLE ACL_OBJECT_IDENTITY ADD CONSTRAINT FK_OBJECT_IDENTITY_CLASS FOREIGN KEY (OBJECT_ID_CLASS) REFERENCES ACL_CLASS (ID)
;

ALTER TABLE ACL_OBJECT_IDENTITY ADD CONSTRAINT FK_OBJECT_IDENTITY_OID FOREIGN KEY (OWNER_SID) REFERENCES ACL_SID (ID)
;
 
ALTER TABLE ACL_OBJECT_IDENTITY ADD CONSTRAINT FK_OBJECT_IDENTITY_PID FOREIGN KEY (PARENT_OBJECT) REFERENCES ACL_OBJECT_IDENTITY (ID)
;

--------------------------------------------------------
--  DDL for Table ACL_ENTRY
--------------------------------------------------------

CREATE TABLE ACL_ENTRY
(
ID NUMBER(19,0),
ACE_ORDER NUMBER(10,0),
ACL_OBJECT_IDENTITY NUMBER(19,0),
AUDIT_FAILURE NUMBER(1,0),
AUDIT_SUCCESS NUMBER(1,0),
GRANTING NUMBER(1,0),
MASK NUMBER(10,0),
SID NUMBER(19,0)
)
;
--------------------------------------------------------
--  DDL for Index SYS_C0010604536
--------------------------------------------------------

CREATE UNIQUE INDEX AK_ACL_ENTRY ON ACL_ENTRY (ID)
;
--------------------------------------------------------
--  DDL for Index SYS_C0010604537
--------------------------------------------------------

CREATE UNIQUE INDEX AK_ACL_ENTRY_IDENTITY ON ACL_ENTRY (ACL_OBJECT_IDENTITY, ACE_ORDER)
;
--------------------------------------------------------
--  Constraints for Table ACL_ENTRY
--------------------------------------------------------

ALTER TABLE ACL_ENTRY ADD PRIMARY KEY (ID)
;

ALTER TABLE ACL_ENTRY ADD UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER)
;
--------------------------------------------------------
--  Ref Constraints for Table ACL_ENTRY
--------------------------------------------------------

ALTER TABLE ACL_ENTRY ADD CONSTRAINT FK_ACL_ENTRY_SID FOREIGN KEY (SID) REFERENCES ACL_SID (ID)
;
 
ALTER TABLE ACL_ENTRY ADD CONSTRAINT FK_ACL_ENTRY_OBJECT_ID FOREIGN KEY (ACL_OBJECT_IDENTITY) REFERENCES ACL_OBJECT_IDENTITY (ID)
;
