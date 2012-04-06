--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Tuesday, April 03, 2012 12:45:26
-- Target DBMS : Oracle 11g
--

drop user bard_dev cascade;

drop user mlbd_dev cascade;

-- 
-- USER: BARD_DEV 
--

CREATE USER BARD_DEV IDENTIFIED BY guest
;

GRANT CONNECT TO BARD_DEV
;
GRANT RESOURCE TO BARD_DEV
;

-- 
-- USER: MLBD_DEV 
--

CREATE USER MLBD_DEV IDENTIFIED BY guest
;

GRANT CONNECT TO MLBD_DEV
;
GRANT RESOURCE TO MLBD_DEV
;

-- 
-- SEQUENCE: MLBD_DEV.Assay_ID 
--

CREATE SEQUENCE MLBD_DEV.ASSAY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.Assay_ID TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.Assay_ID TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.Assay_status_ID 
--

CREATE SEQUENCE MLBD_DEV.ASSAY_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.Assay_status_ID TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.Assay_status_ID TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.element_id 
--

CREATE SEQUENCE MLBD_DEV.ELEMENT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.element_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.element_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.element_status_id 
--

CREATE SEQUENCE MLBD_DEV.ELEMENT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.element_status_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.element_status_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.experiment_id 
--

CREATE SEQUENCE MLBD_DEV.EXPERIMENT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.experiment_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.experiment_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.experiment_status_id 
--

CREATE SEQUENCE MLBD_DEV.EXPERIMENT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.experiment_status_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.experiment_status_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.external_system_id 
--

CREATE SEQUENCE MLBD_DEV.EXTERNAL_SYSTEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.external_system_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.external_system_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.laboratory_id 
--

CREATE SEQUENCE MLBD_DEV.LABORATORY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.laboratory_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.laboratory_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.measure_context_id 
--

CREATE SEQUENCE MLBD_DEV.MEASURE_CONTEXT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.measure_context_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.measure_context_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.measure_context_item_ID 
--

CREATE SEQUENCE MLBD_DEV.MEASURE_CONTEXT_ITEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.measure_context_item_ID TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.measure_context_item_ID TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.measure_id 
--

CREATE SEQUENCE MLBD_DEV.MEASURE_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.measure_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.measure_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.ontology_id 
--

CREATE SEQUENCE MLBD_DEV.ONTOLOGY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.ontology_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.ontology_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.ontology_item_id 
--

CREATE SEQUENCE MLBD_DEV.ONTOLOGY_ITEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.ontology_item_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.ontology_item_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.project_id 
--

CREATE SEQUENCE MLBD_DEV.PROJECT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.project_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.project_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.protocol_id 
--

CREATE SEQUENCE MLBD_DEV.PROTOCOL_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.protocol_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.protocol_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.result_context_ID 
--

CREATE SEQUENCE MLBD_DEV.RESULT_CONTEXT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.result_context_ID TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.result_context_ID TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.result_id 
--

CREATE SEQUENCE MLBD_DEV.RESULT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.result_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.result_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.result_status_id 
--

CREATE SEQUENCE MLBD_DEV.RESULT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD_DEV.result_status_id TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.result_status_id TO BARD_DEV
;


-- 
-- SEQUENCE: MLBD_DEV.result_type_id
--

CREATE SEQUENCE MLBD_DEV.result_type_id
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT SELECT ON MLBD_DEV.result_type_id TO BARD_DEV
;
GRANT ALTER ON MLBD_DEV.result_type_id TO BARD_DEV
;


-- 
-- TABLE: MLBD_DEV.ASSAY 
--

CREATE TABLE MLBD_DEV.ASSAY(
    ASSAY_ID           NUMBER(38, 0)     NOT NULL,
    ASSAY_NAME         VARCHAR2(128)     NOT NULL,
    ASSAY_STATUS_ID    NUMBER(38, 0)     NOT NULL,
    ASSAY_VERSION      VARCHAR2(10)      DEFAULT 1 NOT NULL,
    DESCRIPTION        VARCHAR2(1000),
    DESIGNED_BY        VARCHAR2(100),
    VERSION            NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED       TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated       TIMESTAMP(6),
    MODIFIED_BY        VARCHAR2(40),
    CONSTRAINT PK_ASSAY PRIMARY KEY (ASSAY_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.ASSAY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ASSAY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ASSAY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ASSAY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.ASSAY_STATUS 
--

CREATE TABLE MLBD_DEV.ASSAY_STATUS(
    ASSAY_STATUS_ID    NUMBER(38, 0)    NOT NULL,
    STATUS             VARCHAR2(20)     NOT NULL,
    VERSION            NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created       TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated       TIMESTAMP(6),
    MODIFIED_BY        VARCHAR2(40),
    CONSTRAINT PK_ASSAY_STATUS PRIMARY KEY (ASSAY_STATUS_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.ASSAY_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.Assay_Status (assay_status_ID, status) values ('1', 'Pending');
insert into MLBD_DEV.Assay_Status (assay_status_ID, status) values ('2', 'Active');
insert into MLBD_DEV.Assay_Status (assay_status_ID, status) values ('3', 'Superceded');
insert into MLBD_DEV.Assay_Status (assay_status_ID, status) values ('4', 'Retired');
commit;
GRANT DELETE ON MLBD_DEV.ASSAY_STATUS TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ASSAY_STATUS TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ASSAY_STATUS TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ASSAY_STATUS TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.ELEMENT 
--

CREATE TABLE MLBD_DEV.ELEMENT(
    ELEMENT_ID           NUMBER(38, 0)     NOT NULL,
    PARENT_ELEMENT_ID    NUMBER(38, 0),
    LABEL                VARCHAR2(128)     NOT NULL,
    DESCRIPTION          VARCHAR2(1000),
    ABBREVIATION         VARCHAR2(20),
    ACRONYM              VARCHAR2(20),
    SYNONYMS             VARCHAR2(1000),
    ELEMENT_STATUS_ID    NUMBER(38, 0)     NOT NULL,
    UNIT                 VARCHAR2(100),
    VERSION              NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT PRIMARY KEY (ELEMENT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.ELEMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.ELEMENT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ELEMENT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ELEMENT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ELEMENT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.ELEMENT_STATUS 
--

CREATE TABLE MLBD_DEV.ELEMENT_STATUS(
    ELEMENT_STATUS_ID    NUMBER(38, 0)    NOT NULL,
    ELEMENT_STATUS       VARCHAR2(20)     NOT NULL,
    CAPABILITY           VARCHAR2(256),
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT_STATUS PRIMARY KEY (ELEMENT_STATUS_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.ELEMENT_STATUS.CAPABILITY IS 'Description of the actions allowed when elements are in this state'
;
COMMENT ON COLUMN MLBD_DEV.ELEMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.Element_status (Element_status_id, element_status, Capability) values ('1', 'Pending', 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval');
insert into MLBD_DEV.Element_status (Element_status_id, element_status, Capability) values ('2', 'Published', 'Element can be used for any assay definiton or data upload');
insert into MLBD_DEV.Element_status (Element_status_id, element_status, Capability) values ('3', 'Deprecated', 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement');
insert into MLBD_DEV.Element_status (Element_status_id, element_status, Capability) values ('4', 'Retired', 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data');
commit;
GRANT DELETE ON MLBD_DEV.ELEMENT_STATUS TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ELEMENT_STATUS TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ELEMENT_STATUS TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ELEMENT_STATUS TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.EXPERIMENT 
--

CREATE TABLE MLBD_DEV.EXPERIMENT(
    EXPERIMENT_ID           NUMBER(38, 0)     NOT NULL,
    EXPERIMENT_NAME         VARCHAR2(256)     NOT NULL,
    ASSAY_ID                NUMBER(38, 0)     NOT NULL,
    PROJECT_ID              NUMBER(38, 0),
    EXPERIMENT_STATUS_ID    NUMBER(38, 0)     NOT NULL,
    RUN_DATE_FROM           DATE,
    RUN_DATE_TO             DATE,
    HOLD_UNTIL_DATE         DATE,
    DESCRIPTION             VARCHAR2(1000),
    SOURCE_ID               NUMBER(38, 0)     NOT NULL,
    VERSION                 NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT PRIMARY KEY (EXPERIMENT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.EXPERIMENT.HOLD_UNTIL_DATE IS 'can only be set a max of 1 year in the future'
;
COMMENT ON COLUMN MLBD_DEV.EXPERIMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.EXPERIMENT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.EXPERIMENT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.EXPERIMENT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.EXPERIMENT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.EXPERIMENT_STATUS 
--

CREATE TABLE MLBD_DEV.EXPERIMENT_STATUS(
    EXPERIMENT_STATUS_ID    NUMBER(38, 0)     NOT NULL,
    STATUS                  VARCHAR2(20)      NOT NULL,
    CAPABILITY              VARCHAR2(1000),
    VERSION                 NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT_STATUS PRIMARY KEY (EXPERIMENT_STATUS_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.EXPERIMENT_STATUS.CAPABILITY IS 'describes the actions that can be done with this experiment status and the limitations (this is help text)'
;
COMMENT ON COLUMN MLBD_DEV.EXPERIMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.Experiment_Status (Experiment_status_ID, status, Capability) values ('2', 'Approved', 'Experiment has been approved as ready to upload.  It does not mena results are correct or cannot be changed');
insert into MLBD_DEV.Experiment_Status (Experiment_status_ID, status, Capability) values ('3', 'Rejected', 'Experiment data has been rejected as not scientifically valid.  This will not be uploaded to the warehouse');
insert into MLBD_DEV.Experiment_Status (Experiment_status_ID, status, Capability) values ('4', 'Held', 'Experiment data is private to the loading institution (Source Laboratory).  The Hold Until Date is set.  Though uploaded it cannot be queried except by the source laboratory');
insert into MLBD_DEV.Experiment_Status (Experiment_status_ID, status, Capability) values ('5', 'Uploaded', 'Experiment has been copied into the warehouse and results are available for querying');
insert into MLBD_DEV.Experiment_Status (Experiment_status_ID, status, Capability) values ('6', 'Mark for Deletion', 'Experiment has been confirmed as present in the warehouse and may be deleted at any time.');
commit;
GRANT DELETE ON MLBD_DEV.EXPERIMENT_STATUS TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.EXPERIMENT_STATUS TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.EXPERIMENT_STATUS TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.EXPERIMENT_STATUS TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.EXTERNAL_ASSAY 
--

CREATE TABLE MLBD_DEV.EXTERNAL_ASSAY(
    EXTERNAL_SYSTEM_ID    NUMBER(38, 0)    NOT NULL,
    ASSAY_ID              NUMBER(38, 0)    NOT NULL,
    EXT_ASSAY_ID          VARCHAR2(128)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_EXTERNAL_ASSAY PRIMARY KEY (EXTERNAL_SYSTEM_ID, ASSAY_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.EXTERNAL_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.EXTERNAL_ASSAY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.EXTERNAL_ASSAY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.EXTERNAL_ASSAY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.EXTERNAL_ASSAY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.EXTERNAL_SYSTEM 
--

CREATE TABLE MLBD_DEV.EXTERNAL_SYSTEM(
    EXTERNAL_SYSTEM_ID    NUMBER(38, 0)     NOT NULL,
    SYSTEM_NAME           VARCHAR2(128)     NOT NULL,
    OWNER                 VARCHAR2(128),
    SYSTEM_URL            VARCHAR2(1000),
    VERSION               NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_EXTERNAL_SYSTEM PRIMARY KEY (EXTERNAL_SYSTEM_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.EXTERNAL_SYSTEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.EXTERNAL_SYSTEM TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.EXTERNAL_SYSTEM TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.EXTERNAL_SYSTEM TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.EXTERNAL_SYSTEM TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.LABORATORY 
--

CREATE TABLE MLBD_DEV.LABORATORY(
    LAB_ID          NUMBER(38, 0)     NOT NULL,
    LAB_NAME        VARCHAR2(125)     NOT NULL,
    ABBREVIATION    VARCHAR2(20),
    DESCRIPTION     VARCHAR2(1000),
    LOCATION        VARCHAR2(250),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_LABORATORY PRIMARY KEY (LAB_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.LABORATORY.LOCATION IS 'Address or other location (website?) for the lab'
;
COMMENT ON COLUMN MLBD_DEV.LABORATORY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.LABORATORY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.LABORATORY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.LABORATORY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.LABORATORY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.MEASURE 
--

CREATE TABLE MLBD_DEV.MEASURE(
    MEASURE_ID            NUMBER(38, 0)    NOT NULL,
    ASSAY_ID              NUMBER(38, 0)    NOT NULL,
    RESULT_TYPE_ID        NUMBER(38, 0)    NOT NULL,
    ENTRY_UNIT            VARCHAR2(100),
    MEASURE_CONTEXT_ID    NUMBER(38, 0),
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE PRIMARY KEY (MEASURE_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.MEASURE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.MEASURE TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.MEASURE TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.MEASURE TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.MEASURE TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.MEASURE_CONTEXT 
--

CREATE TABLE MLBD_DEV.MEASURE_CONTEXT(
    MEASURE_CONTEXT_ID    NUMBER(38, 0)    NOT NULL,
    CONTEXT_NAME          VARCHAR2(128)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT PRIMARY KEY (MEASURE_CONTEXT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT.CONTEXT_NAME IS 'default name should be Assay.Name || (select count(*) + 1 from measure_context where assay_ID = assay.assay_ID)'
;
COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.MEASURE_CONTEXT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.MEASURE_CONTEXT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.MEASURE_CONTEXT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.MEASURE_CONTEXT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.MEASURE_CONTEXT_ITEM 
--

CREATE TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM(
    MEASURE_CONTEXT_ITEM_ID    NUMBER(38, 0)    NOT NULL,
    ASSAY_ID                   NUMBER(38, 0)    NOT NULL,
    MEASURE_CONTEXT_ID         NUMBER(38, 0)    NOT NULL,
    GROUP_NO                   NUMBER(10, 0),
    ATTRIBUTE_TYPE             VARCHAR2(20)     NOT NULL
                               CONSTRAINT CK_ATTRIBUTE_TYPE CHECK (ATTRIBUTE_TYPE in ('Fixed', 'List', 'Range', 'Number')),
    ATTRIBUTE_ID               NUMBER(38, 0)    NOT NULL,
    QUALIFIER                  CHAR(2),
    VALUE_ID                   NUMBER(38, 0),
    VALUE_DISPLAY              VARCHAR2(256),
    VALUE_NUM                  FLOAT,
    VALUE_MIN                  FLOAT,
    VALUE_MAX                  FLOAT,
    VERSION                    NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created               TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated               TIMESTAMP(6),
    MODIFIED_BY                VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT_ITEM PRIMARY KEY (MEASURE_CONTEXT_ITEM_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT_ITEM.ASSAY_ID IS 'This column is useful for creating the assay defintion before the measures and their contexts have been properly grouped.'
;
COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT_ITEM.GROUP_NO IS 'rows with the same group_no in the measure context combine to a single group for UI purposes'
;
COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN MLBD_DEV.MEASURE_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.MEASURE_CONTEXT_ITEM TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.MEASURE_CONTEXT_ITEM TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.MEASURE_CONTEXT_ITEM TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.MEASURE_CONTEXT_ITEM TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.ONTOLOGY 
--

CREATE TABLE MLBD_DEV.ONTOLOGY(
    ONTOLOGY_ID      NUMBER(38, 0)     NOT NULL,
    ONTOLOGY_NAME    VARCHAR2(256)     NOT NULL,
    ABBREVIATION     VARCHAR2(20),
    SYSTEM_URL       VARCHAR2(1000),
    VERSION          NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created     TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated     TIMESTAMP(6),
    MODIFIED_BY      VARCHAR2(40),
    CONSTRAINT PK_ONTOLOGY PRIMARY KEY (ONTOLOGY_ID)
)
;



COMMENT ON TABLE MLBD_DEV.ONTOLOGY IS 'an external ontology or dictionary or other source of reference data'
;
COMMENT ON COLUMN MLBD_DEV.ONTOLOGY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.ONTOLOGY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ONTOLOGY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ONTOLOGY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ONTOLOGY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.ONTOLOGY_ITEM 
--

CREATE TABLE MLBD_DEV.ONTOLOGY_ITEM(
    ONTOLOGY_ITEM_ID    NUMBER(38, 0)    NOT NULL,
    ONTOLOGY_ID         NUMBER(38, 0)    NOT NULL,
    ELEMENT_ID          NUMBER(38, 0),
    ITEM_REFERENCE      CHAR(10),
    RESULT_TYPE_ID      NUMBER(38, 0),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_ONTOLOGY_ITEM PRIMARY KEY (ONTOLOGY_ITEM_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.ONTOLOGY_ITEM.ITEM_REFERENCE IS 'Concatenate this with the Ontology.system_URL for a full URI for the item'
;
COMMENT ON COLUMN MLBD_DEV.ONTOLOGY_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.ONTOLOGY_ITEM TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.ONTOLOGY_ITEM TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.ONTOLOGY_ITEM TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.ONTOLOGY_ITEM TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.PROJECT 
--

CREATE TABLE MLBD_DEV.PROJECT(
    PROJECT_ID      NUMBER(38, 0)     NOT NULL,
    PROJECT_NAME    VARCHAR2(256)     NOT NULL,
    GROUP_TYPE      VARCHAR2(20)      DEFAULT 'Project' NOT NULL
                    CONSTRAINT CK_PROJECT_TYPE CHECK (GROUP_TYPE in ('Project', 'Campaign', 'Panel', 'Study')),
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_PROJECT PRIMARY KEY (PROJECT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.PROJECT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.PROJECT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.PROJECT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.PROJECT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.PROJECT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.PROJECT_ASSAY 
--

CREATE TABLE MLBD_DEV.PROJECT_ASSAY(
    ASSAY_ID               NUMBER(38, 0)     NOT NULL,
    PROJECT_ID             NUMBER(38, 0)     NOT NULL,
    STAGE                  VARCHAR2(20)      NOT NULL,
    SEQUENCE_NO            NUMBER(10, 0),
    PROMOTION_THRESHOLD    FLOAT,
    PROMOTION_CRITERIA     VARCHAR2(1000),
    VERSION                NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created           TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated           TIMESTAMP(6),
    MODIFIED_BY            VARCHAR2(40),
    CONSTRAINT PK_PROJECT_ASSAY PRIMARY KEY (ASSAY_ID, PROJECT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.PROJECT_ASSAY.SEQUENCE_NO IS 'defines the promotion order (and often the running order) of a set of assays in a project'
;
COMMENT ON COLUMN MLBD_DEV.PROJECT_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.PROJECT_ASSAY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.PROJECT_ASSAY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.PROJECT_ASSAY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.PROJECT_ASSAY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.PROTOCOL 
--

CREATE TABLE MLBD_DEV.PROTOCOL(
    PROTOCOL_ID          NUMBER(38, 0)    NOT NULL,
    PROTOCOL_NAME        VARCHAR2(500)    NOT NULL,
    PROTOCOL_DOCUMENT    LONG RAW,
    ASSAY_ID             NUMBER(38, 0)    NOT NULL,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_PROTOCOL PRIMARY KEY (PROTOCOL_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.PROTOCOL.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.PROTOCOL TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.PROTOCOL TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.PROTOCOL TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.PROTOCOL TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.QUALIFIER 
--

CREATE TABLE MLBD_DEV.QUALIFIER(
    QUALIFIER       CHAR(2)           NOT NULL,
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_QUALIFIER PRIMARY KEY (QUALIFIER)
)
;
GRANT DELETE ON MLBD_DEV.QUALIFIER TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.QUALIFIER TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.QUALIFIER TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.QUALIFIER TO BARD_DEV
;

COMMENT ON COLUMN MLBD_DEV.QUALIFIER.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.qualifier (qualifier, description) values ('=', 'equals');
insert into MLBD_DEV.qualifier (qualifier, description) values ('>=', 'greater than or equal');
insert into MLBD_DEV.qualifier (qualifier, description) values ('<=', 'less than or equal');
insert into MLBD_DEV.qualifier (qualifier, description) values ('<', 'greater than');
insert into MLBD_DEV.qualifier (qualifier, description) values ('>', 'less than');
insert into MLBD_DEV.qualifier (qualifier, description) values ('~', 'approximatley');
insert into MLBD_DEV.qualifier (qualifier, description) values ('>>', 'very much greater than');
insert into MLBD_DEV.qualifier (qualifier, description) values ('<<', 'very much less than');
commit;
-- 
-- TABLE: MLBD_DEV.RESULT 
--

CREATE TABLE MLBD_DEV.RESULT(
    RESULT_ID            NUMBER(38, 0)    NOT NULL,
    VALUE_DISPLAY        VARCHAR2(256),
    VALUE_NUM            FLOAT,
    VALUE_MIN            FLOAT,
    VALUE_MAX            FLOAT,
    QUALIFIER            CHAR(2),
    RESULT_STATUS_ID     NUMBER(38, 0)    NOT NULL,
    EXPERIMENT_ID        NUMBER(38, 0)    NOT NULL,
    SUBSTANCE_ID         NUMBER(38, 0)    NOT NULL,
    RESULT_CONTEXT_ID    NUMBER(38, 0)    NOT NULL,
    ENTRY_UNIT           VARCHAR2(100),
    RESULT_TYPE_ID       NUMBER(38, 0)    NOT NULL,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_RESULT PRIMARY KEY (RESULT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT.ENTRY_UNIT IS 'This is the units that were used in the UI when uploading the data.  The numerical values are stored in "base_Unit" and NOT in this unit to make future calculaton easier.  This Entry_Unit is recorded to allow accurate re-display of entered results and for data lineage reasons'
;
COMMENT ON COLUMN MLBD_DEV.RESULT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.RESULT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.RESULT_CONTEXT 
--

CREATE TABLE MLBD_DEV.RESULT_CONTEXT(
    RESULT_CONTEXT_ID    NUMBER(38, 0)    NOT NULL,
    CONTEXT_NAME         VARCHAR2(125),
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_RESULT_CONTEXT PRIMARY KEY (RESULT_CONTEXT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT_CONTEXT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.RESULT_CONTEXT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT_CONTEXT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT_CONTEXT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT_CONTEXT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.RESULT_CONTEXT_ITEM 
--

CREATE TABLE MLBD_DEV.RESULT_CONTEXT_ITEM(
    RESULT_CONTEXT_ITEM_ID    NUMBER(38, 0)    NOT NULL,
    EXPERIMENT_ID             NUMBER(38, 0)    NOT NULL,
    RESULT_CONTEXT_ID         NUMBER(38, 0)    NOT NULL,
    GROUP_NO                  NUMBER(10, 0),
    ATTRIBUTE_ID              NUMBER(38, 0)    NOT NULL,
    VALUE_ID                  NUMBER(38, 0),
    QUALIFIER                 CHAR(2),
    VALUE_DISPLAY             VARCHAR2(256),
    VALUE_NUM                 FLOAT,
    VALUE_MIN                 FLOAT,
    VALUE_MAX                 FLOAT,
    VERSION                   NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created              TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated              TIMESTAMP(6),
    MODIFIED_BY               VARCHAR2(40),
    CONSTRAINT PK_Result_context_item PRIMARY KEY (RESULT_CONTEXT_ITEM_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT_CONTEXT_ITEM.GROUP_NO IS 'rows with the same group_no in the measure context combine to a single group for UI purposes'
;
COMMENT ON COLUMN MLBD_DEV.RESULT_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN MLBD_DEV.RESULT_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.RESULT_CONTEXT_ITEM TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT_CONTEXT_ITEM TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT_CONTEXT_ITEM TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT_CONTEXT_ITEM TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.RESULT_HIERARCHY 
--

CREATE TABLE MLBD_DEV.RESULT_HIERARCHY(
    RESULT_ID           NUMBER(38, 0)    NOT NULL,
    PARENT_RESULT_ID    NUMBER(38, 0)    NOT NULL,
    HIERARCHY_TYPE      VARCHAR2(10)     NOT NULL
                        CONSTRAINT CK_RESULT_HIERARCHY_TYPE CHECK (HIERARCHY_TYPE in ('Child', 'Derives')),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_HIERARCHY PRIMARY KEY (RESULT_ID, PARENT_RESULT_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT_HIERARCHY.HIERARCHY_TYPE IS 'two types of hierarchy are allowed: parent/child where one result is dependant on or grouped with another; derived from where aresult is used to claculate another (e.g. PI used for IC50).  The hierarchy types are mutually exclusive.'
;
COMMENT ON COLUMN MLBD_DEV.RESULT_HIERARCHY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.RESULT_HIERARCHY TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT_HIERARCHY TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT_HIERARCHY TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT_HIERARCHY TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.RESULT_STATUS 
--

CREATE TABLE MLBD_DEV.RESULT_STATUS(
    RESULT_STATUS_ID    NUMBER(38, 0)    NOT NULL,
    STATUS              VARCHAR2(20)     NOT NULL,
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_STATUS PRIMARY KEY (RESULT_STATUS_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.Result_status (result_status_id, status) values ('1', 'Pending');
insert into MLBD_DEV.Result_status (result_status_id, status) values ('2', 'Approved');
insert into MLBD_DEV.Result_status (result_status_id, status) values ('3', 'Rejected');
insert into MLBD_DEV.Result_status (result_status_id, status) values ('4', 'Uploading');
insert into MLBD_DEV.Result_status (result_status_id, status) values ('5', 'Uploaded');
insert into MLBD_DEV.Result_status (result_status_id, status) values ('6', 'Mark for Deletion');
commit;
GRANT DELETE ON MLBD_DEV.RESULT_STATUS TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT_STATUS TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT_STATUS TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT_STATUS TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.RESULT_TYPE 
--

CREATE TABLE MLBD_DEV.RESULT_TYPE(
    RESULT_TYPE_ID           NUMBER(38, 0)     NOT NULL,
    PARENT_RESULT_TYPE_ID    NUMBER(38, 0),
    RESULT_TYPE_NAME         VARCHAR2(128)     NOT NULL,
    DESCRIPTION              VARCHAR2(1000),
    RESULT_TYPE_STATUS_ID    NUMBER(38, 0)     NOT NULL,
    BASE_UNIT                VARCHAR2(100),
    VERSION                  NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created             TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated             TIMESTAMP(6),
    MODIFIED_BY              VARCHAR2(40),
    CONSTRAINT PK_RESULT_TYPE PRIMARY KEY (RESULT_TYPE_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.RESULT_TYPE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.RESULT_TYPE TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.RESULT_TYPE TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.RESULT_TYPE TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.RESULT_TYPE TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.STAGE 
--

CREATE TABLE MLBD_DEV.STAGE(
    STAGE           VARCHAR2(20)      NOT NULL,
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_STAGE PRIMARY KEY (STAGE)
)
;



COMMENT ON COLUMN MLBD_DEV.STAGE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into MLBD_DEV.Stage (Stage) values ('Primary');
insert into MLBD_DEV.Stage (Stage) values ('Secondary');
insert into MLBD_DEV.Stage (Stage) values ('Confirmation');
insert into MLBD_DEV.Stage (Stage) values ('Tertiary');
insert into MLBD_DEV.Stage (Stage) values ('Counter-screen');
insert into MLBD_DEV.Stage (Stage) values ('TBD');
Commit;
GRANT DELETE ON MLBD_DEV.STAGE TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.STAGE TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.STAGE TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.STAGE TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.SUBSTANCE 
--

CREATE TABLE MLBD_DEV.SUBSTANCE(
    SUBSTANCE_ID        NUMBER(38, 0)     NOT NULL,
    COMPOUND_ID         NUMBER(10, 0),
    SMILES              VARCHAR2(4000),
    MOLECULAR_WEIGHT    NUMBER(10, 3),
    SUBSTANCE_TYPE      VARCHAR2(20)      NOT NULL
                        CONSTRAINT CK_SUBSTANCE_TYPE CHECK (Substance_Type in ('small molecule', 'protein', 'peptide', 'antibody', 'cell', 'oligonucleotide')),
    VERSION             NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_SUBSTANCE PRIMARY KEY (SUBSTANCE_ID)
)
;



COMMENT ON COLUMN MLBD_DEV.SUBSTANCE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.SUBSTANCE TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.SUBSTANCE TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.SUBSTANCE TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.SUBSTANCE TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.UNIT 
--

CREATE TABLE MLBD_DEV.UNIT(
    UNIT            VARCHAR2(100)     NOT NULL,
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_UNIT PRIMARY KEY (UNIT)
)
;



COMMENT ON COLUMN MLBD_DEV.UNIT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.UNIT TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.UNIT TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.UNIT TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.UNIT TO BARD_DEV
;

-- 
-- TABLE: MLBD_DEV.UNIT_CONVERSION 
--

CREATE TABLE MLBD_DEV.UNIT_CONVERSION(
    FROM_UNIT       VARCHAR2(100)    NOT NULL,
    TO_UNIT         VARCHAR2(100)    NOT NULL,
    MULTIPLIER      FLOAT,
    OFFSET          FLOAT,
    FORMULA         VARCHAR2(256),
    VERSION         NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_UNIT_CONVERSION PRIMARY KEY (FROM_UNIT, TO_UNIT)
)
;



COMMENT ON COLUMN MLBD_DEV.UNIT_CONVERSION.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
GRANT DELETE ON MLBD_DEV.UNIT_CONVERSION TO BARD_DEV
;
GRANT INSERT ON MLBD_DEV.UNIT_CONVERSION TO BARD_DEV
;
GRANT SELECT ON MLBD_DEV.UNIT_CONVERSION TO BARD_DEV
;
GRANT UPDATE ON MLBD_DEV.UNIT_CONVERSION TO BARD_DEV
;

-- 
-- VIEW: BARD_DEV.assay 
--

CREATE VIEW BARD_DEV.assay AS
SELECT Ass.ASSAY_ID, Ass.ASSAY_NAME, Ass.ASSAY_STATUS_ID, Ass.ASSAY_VERSION, Ass.DESCRIPTION, Ass.DESIGNED_BY, Ass.VERSION, Ass.Date_Created, Ass.Last_Updated, Ass.MODIFIED_BY
FROM MLBD_DEV.ASSAY Ass
;

-- 
-- VIEW: BARD_DEV.assay_status 
--

CREATE VIEW BARD_DEV.assay_status AS
SELECT Ass.ASSAY_STATUS_ID, Ass.STATUS, Ass.VERSION, Ass.Date_Created, Ass.Last_Updated, Ass.MODIFIED_BY
FROM MLBD_DEV.ASSAY_STATUS Ass
;

-- 
-- VIEW: BARD_DEV.element 
--

CREATE VIEW BARD_DEV.element AS
SELECT El.ELEMENT_ID, El.PARENT_ELEMENT_ID, El.LABEL, El.DESCRIPTION, El.ABBREVIATION, El.ACRONYM, El.SYNONYMS, El.ELEMENT_STATUS_ID, El.UNIT, El.VERSION, El.Date_Created, El.Last_Updated, El.MODIFIED_BY
FROM MLBD_DEV.ELEMENT El
;

-- 
-- VIEW: BARD_DEV.element_status 
--

CREATE VIEW BARD_DEV.element_status AS
SELECT El.ELEMENT_STATUS_ID, El.ELEMENT_STATUS, El.CAPABILITY, El.VERSION, El.Date_Created, El.Last_Updated, El.MODIFIED_BY
FROM MLBD_DEV.ELEMENT_STATUS El
;

-- 
-- VIEW: BARD_DEV.experiment 
--

CREATE VIEW BARD_DEV.experiment AS
SELECT Ex.EXPERIMENT_ID, Ex.EXPERIMENT_NAME, Ex.ASSAY_ID, Ex.PROJECT_ID, Ex.EXPERIMENT_STATUS_ID, Ex.RUN_DATE_FROM, Ex.RUN_DATE_TO, Ex.HOLD_UNTIL_DATE, Ex.DESCRIPTION, Ex.SOURCE_ID, Ex.VERSION, Ex.Date_Created, Ex.Last_Updated, Ex.MODIFIED_BY
FROM MLBD_DEV.EXPERIMENT Ex
;

-- 
-- VIEW: BARD_DEV.experiment_status 
--

CREATE VIEW BARD_DEV.experiment_status AS
SELECT Ex.EXPERIMENT_STATUS_ID, Ex.STATUS, Ex.CAPABILITY, Ex.VERSION, Ex.Date_Created, Ex.Last_Updated, Ex.MODIFIED_BY
FROM MLBD_DEV.EXPERIMENT_STATUS Ex
;

-- 
-- VIEW: BARD_DEV.external_assay 
--

CREATE VIEW BARD_DEV.external_assay AS
SELECT Ex.EXTERNAL_SYSTEM_ID, Ex.ASSAY_ID, Ex.EXT_ASSAY_ID, Ex.VERSION, Ex.Date_Created, Ex.Last_Updated, Ex.MODIFIED_BY
FROM MLBD_DEV.EXTERNAL_ASSAY Ex
;

-- 
-- VIEW: BARD_DEV.external_system 
--

CREATE VIEW BARD_DEV.external_system AS
SELECT Ex.EXTERNAL_SYSTEM_ID, Ex.SYSTEM_NAME, Ex.OWNER, Ex.SYSTEM_URL, Ex.VERSION, Ex.Date_Created, Ex.Last_Updated, Ex.MODIFIED_BY
FROM MLBD_DEV.EXTERNAL_SYSTEM Ex
;

-- 
-- VIEW: BARD_DEV.laboratory 
--

CREATE VIEW BARD_DEV.laboratory AS
SELECT La.LAB_ID, La.LAB_NAME, La.ABBREVIATION, La.DESCRIPTION, La.LOCATION, La.VERSION, La.Date_Created, La.Last_Updated, La.MODIFIED_BY
FROM MLBD_DEV.LABORATORY La
;

-- 
-- VIEW: BARD_DEV.measure 
--

CREATE VIEW BARD_DEV.measure AS
SELECT Me.MEASURE_ID, Me.ASSAY_ID, Me.RESULT_TYPE_ID, Me.ENTRY_UNIT, Me.MEASURE_CONTEXT_ID, Me.VERSION, Me.Date_Created, Me.Last_Updated, Me.MODIFIED_BY
FROM MLBD_DEV.MEASURE Me
;

-- 
-- VIEW: BARD_DEV.measure_context 
--

CREATE VIEW BARD_DEV.measure_context AS
SELECT Me.MEASURE_CONTEXT_ID, Me.CONTEXT_NAME, Me.VERSION, Me.Date_Created, Me.Last_Updated, Me.MODIFIED_BY
FROM MLBD_DEV.MEASURE_CONTEXT Me
;

-- 
-- VIEW: BARD_DEV.measure_context_item 
--

CREATE VIEW BARD_DEV.measure_context_item AS
SELECT Me.MEASURE_CONTEXT_ITEM_ID, Me.ASSAY_ID, Me.MEASURE_CONTEXT_ID, Me.GROUP_NO, Me.ATTRIBUTE_TYPE, Me.ATTRIBUTE_ID, Me.QUALIFIER, Me.VALUE_ID, Me.VALUE_DISPLAY, Me.VALUE_NUM, Me.VALUE_MIN, Me.VALUE_MAX, Me.VERSION, Me.Date_Created, Me.Last_Updated, Me.MODIFIED_BY
FROM MLBD_DEV.MEASURE_CONTEXT_ITEM Me
;

-- 
-- VIEW: BARD_DEV.ontology 
--

CREATE VIEW BARD_DEV.ontology AS
SELECT Ont.ONTOLOGY_ID, Ont.ONTOLOGY_NAME, Ont.ABBREVIATION, Ont.SYSTEM_URL, Ont.VERSION, Ont.Date_Created, Ont.Last_Updated, Ont.MODIFIED_BY
FROM MLBD_DEV.ONTOLOGY Ont
;

-- 
-- VIEW: BARD_DEV.ontology_item 
--

CREATE VIEW BARD_DEV.ontology_item AS
SELECT Ont.ONTOLOGY_ITEM_ID, Ont.ONTOLOGY_ID, Ont.ELEMENT_ID, Ont.ITEM_REFERENCE, Ont.RESULT_TYPE_ID, Ont.VERSION, Ont.Date_Created, Ont.Last_Updated, Ont.MODIFIED_BY
FROM MLBD_DEV.ONTOLOGY_ITEM Ont
;

-- 
-- VIEW: BARD_DEV.project 
--

CREATE VIEW BARD_DEV.project AS
SELECT Pr.PROJECT_ID, Pr.PROJECT_NAME, Pr.GROUP_TYPE, Pr.DESCRIPTION, Pr.VERSION, Pr.Date_Created, Pr.Last_Updated, Pr.MODIFIED_BY
FROM MLBD_DEV.PROJECT Pr
;

-- 
-- VIEW: BARD_DEV.project_assay 
--

CREATE VIEW BARD_DEV.project_assay AS
SELECT Pro.ASSAY_ID, Pro.PROJECT_ID, Pro.STAGE, Pro.SEQUENCE_NO, Pro.PROMOTION_THRESHOLD, Pro.PROMOTION_CRITERIA, Pro.VERSION, Pro.Date_Created, Pro.Last_Updated, Pro.MODIFIED_BY
FROM MLBD_DEV.PROJECT_ASSAY Pro
;

-- 
-- VIEW: BARD_DEV.protocol 
--

CREATE VIEW BARD_DEV.protocol AS
SELECT Pr.PROTOCOL_ID, Pr.PROTOCOL_NAME, Pr.PROTOCOL_DOCUMENT, Pr.ASSAY_ID, Pr.VERSION, Pr.Date_Created, Pr.Last_Updated, Pr.MODIFIED_BY
FROM MLBD_DEV.PROTOCOL Pr
;

-- 
-- VIEW: BARD_DEV.qualifier 
--

CREATE VIEW BARD_DEV.qualifier AS
SELECT Qu.QUALIFIER, Qu.DESCRIPTION, Qu.VERSION, Qu.Date_Created, Qu.Last_Updated, Qu.MODIFIED_BY
FROM MLBD_DEV.QUALIFIER Qu
;

-- 
-- VIEW: BARD_DEV.result 
--

CREATE VIEW BARD_DEV.result AS
SELECT Re.RESULT_ID, Re.VALUE_DISPLAY, Re.VALUE_NUM, Re.VALUE_MIN, Re.VALUE_MAX, Re.QUALIFIER, Re.RESULT_STATUS_ID, Re.EXPERIMENT_ID, Re.SUBSTANCE_ID, Re.RESULT_CONTEXT_ID, Re.ENTRY_UNIT, Re.RESULT_TYPE_ID, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT Re
;

-- 
-- VIEW: BARD_DEV.result_context 
--

CREATE VIEW BARD_DEV.result_context AS
SELECT Re.RESULT_CONTEXT_ID, Re.CONTEXT_NAME, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT_CONTEXT Re
;

-- 
-- VIEW: BARD_DEV.result_context_item 
--

CREATE VIEW BARD_DEV.result_context_item AS
SELECT Re.RESULT_CONTEXT_ITEM_ID, Re.EXPERIMENT_ID, Re.RESULT_CONTEXT_ID, Re.GROUP_NO, Re.ATTRIBUTE_ID, Re.VALUE_ID, Re.QUALIFIER, Re.VALUE_DISPLAY, Re.VALUE_NUM, Re.VALUE_MIN, Re.VALUE_MAX, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT_CONTEXT_ITEM Re
;

-- 
-- VIEW: BARD_DEV.result_hierarchy 
--

CREATE VIEW BARD_DEV.result_hierarchy AS
SELECT Re.RESULT_ID, Re.PARENT_RESULT_ID, Re.HIERARCHY_TYPE, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT_HIERARCHY Re
;

-- 
-- VIEW: BARD_DEV.result_status 
--

CREATE VIEW BARD_DEV.result_status AS
SELECT Re.RESULT_STATUS_ID, Re.STATUS, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT_STATUS Re
;

-- 
-- VIEW: BARD_DEV.result_type 
--

CREATE VIEW BARD_DEV.result_type AS
SELECT Re.RESULT_TYPE_ID, Re.PARENT_RESULT_TYPE_ID, Re.RESULT_TYPE_NAME, Re.DESCRIPTION, Re.RESULT_TYPE_STATUS_ID, Re.BASE_UNIT, Re.VERSION, Re.Date_Created, Re.Last_Updated, Re.MODIFIED_BY
FROM MLBD_DEV.RESULT_TYPE Re
;

-- 
-- VIEW: BARD_DEV.stage 
--

CREATE VIEW BARD_DEV.stage AS
SELECT St.STAGE, St.DESCRIPTION, St.VERSION, St.Date_Created, St.Last_Updated, St.MODIFIED_BY
FROM MLBD_DEV.STAGE St
;

-- 
-- VIEW: BARD_DEV.substance 
--

CREATE VIEW BARD_DEV.substance AS
SELECT Su.SUBSTANCE_ID, Su.COMPOUND_ID, Su.SMILES, Su.MOLECULAR_WEIGHT, Su.SUBSTANCE_TYPE, Su.VERSION, Su.Date_Created, Su.Last_Updated, Su.MODIFIED_BY
FROM MLBD_DEV.SUBSTANCE Su
;

-- 
-- VIEW: BARD_DEV.unit 
--

CREATE VIEW BARD_DEV.unit AS
SELECT Un.UNIT, Un.DESCRIPTION, Un.VERSION, Un.Date_Created, Un.Last_Updated, Un.MODIFIED_BY
FROM MLBD_DEV.UNIT Un
;

-- 
-- VIEW: BARD_DEV.unit_conversion 
--

CREATE VIEW BARD_DEV.unit_conversion AS
SELECT Un.FROM_UNIT, Un.TO_UNIT, Un.MULTIPLIER, Un.OFFSET, Un.FORMULA, Un.VERSION, Un.Date_Created, Un.Last_Updated, Un.MODIFIED_BY
FROM MLBD_DEV.UNIT_CONVERSION Un
;

-- 
-- INDEX: MLBD_DEV.FK_ASSAY_ASSAY_STATUS_ID 
--

CREATE INDEX MLBD_DEV.FK_ASSAY_ASSAY_STATUS_ID ON MLBD_DEV.ASSAY(ASSAY_STATUS_ID)
;
-- 
-- INDEX: MLBD_DEV.AK_ASSAY_STATUS 
--

CREATE UNIQUE INDEX MLBD_DEV.AK_ASSAY_STATUS ON MLBD_DEV.ASSAY_STATUS(STATUS)
;
-- 
-- INDEX: MLBD_DEV.FK_ELEMENT_ELEMENT_STATUS 
--

CREATE INDEX MLBD_DEV.FK_ELEMENT_ELEMENT_STATUS ON MLBD_DEV.ELEMENT(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_ELEMENT_UNIT 
--

CREATE INDEX MLBD_DEV.FK_ELEMENT_UNIT ON MLBD_DEV.ELEMENT(UNIT)
;
-- 
-- INDEX: MLBD_DEV.FK_ELEMENT_PARENT_ELEMENT 
--

CREATE INDEX MLBD_DEV.FK_ELEMENT_PARENT_ELEMENT ON MLBD_DEV.ELEMENT(PARENT_ELEMENT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_EXPERIMENT_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_EXPERIMENT_ASSAY ON MLBD_DEV.EXPERIMENT(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_PROJECT_EXPERIMENT 
--

CREATE INDEX MLBD_DEV.FK_PROJECT_EXPERIMENT ON MLBD_DEV.EXPERIMENT(PROJECT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_EXPERIMENT_EXPRT_STATUS 
--

CREATE INDEX MLBD_DEV.FK_EXPERIMENT_EXPRT_STATUS ON MLBD_DEV.EXPERIMENT(EXPERIMENT_STATUS_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_EXPERIMENT_SOURCE_LAB 
--

CREATE INDEX MLBD_DEV.FK_EXPERIMENT_SOURCE_LAB ON MLBD_DEV.EXPERIMENT(SOURCE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_EXT_ASSAY_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_EXT_ASSAY_ASSAY ON MLBD_DEV.EXTERNAL_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV."FK_EXT_ASSAY_EXT_SYSTEM" 
--

CREATE INDEX MLBD_DEV."FK_EXT_ASSAY_EXT_SYSTEM" ON MLBD_DEV.EXTERNAL_ASSAY(EXTERNAL_SYSTEM_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_MEASURE_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_MEASURE_ASSAY ON MLBD_DEV.MEASURE(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_MEASURE_RESULT_TYPE 
--

CREATE INDEX MLBD_DEV.FK_MEASURE_RESULT_TYPE ON MLBD_DEV.MEASURE(RESULT_TYPE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_MEASURE_UNIT 
--

CREATE INDEX MLBD_DEV.FK_MEASURE_UNIT ON MLBD_DEV.MEASURE(ENTRY_UNIT)
;
-- 
-- INDEX: MLBD_DEV.FK_MEASURE_M_CONTEXT_ITEM 
--

CREATE INDEX MLBD_DEV.FK_MEASURE_M_CONTEXT_ITEM ON MLBD_DEV.MEASURE(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: MLBD_DEV.AK_MEASURE_CONTEXT_ITEM 
--

CREATE UNIQUE INDEX MLBD_DEV.AK_MEASURE_CONTEXT_ITEM ON MLBD_DEV.MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID, GROUP_NO, ATTRIBUTE_ID, VALUE_DISPLAY)
;
-- 
-- INDEX: MLBD_DEV.FK_M_CONTEXT_ITEM_M_CONTEXT 
--

CREATE INDEX MLBD_DEV.FK_M_CONTEXT_ITEM_M_CONTEXT ON MLBD_DEV.MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_M_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX MLBD_DEV.FK_M_CONTEXT_ITEM_ATTRIBUTE ON MLBD_DEV.MEASURE_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_M_CONTEXT_ITEM_VALUE 
--

CREATE INDEX MLBD_DEV.FK_M_CONTEXT_ITEM_VALUE ON MLBD_DEV.MEASURE_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_M_CONTEXT_ITEM_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_M_CONTEXT_ITEM_ASSAY ON MLBD_DEV.MEASURE_CONTEXT_ITEM(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_M_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX MLBD_DEV.FK_M_CONTEXT_ITEM_QUALIFIER ON MLBD_DEV.MEASURE_CONTEXT_ITEM(QUALIFIER)
;
-- 
-- INDEX: MLBD_DEV.FK_ONTOLOGY_ITEM_ONTOLOGY 
--

CREATE INDEX MLBD_DEV.FK_ONTOLOGY_ITEM_ONTOLOGY ON MLBD_DEV.ONTOLOGY_ITEM(ONTOLOGY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_ONTOLOGY_ITEM_ELEMENT 
--

CREATE INDEX MLBD_DEV.FK_ONTOLOGY_ITEM_ELEMENT ON MLBD_DEV.ONTOLOGY_ITEM(ELEMENT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_ONTOLOGY_ITEM_RESULT_TYPE 
--

CREATE INDEX MLBD_DEV.FK_ONTOLOGY_ITEM_RESULT_TYPE ON MLBD_DEV.ONTOLOGY_ITEM(RESULT_TYPE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_PROJECT_ASSAY_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_PROJECT_ASSAY_ASSAY ON MLBD_DEV.PROJECT_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_PROJECT_ASSAY_PROJECT 
--

CREATE INDEX MLBD_DEV.FK_PROJECT_ASSAY_PROJECT ON MLBD_DEV.PROJECT_ASSAY(PROJECT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_PROJECT_ASSAY_STAGE 
--

CREATE INDEX MLBD_DEV.FK_PROJECT_ASSAY_STAGE ON MLBD_DEV.PROJECT_ASSAY(STAGE)
;
-- 
-- INDEX: MLBD_DEV.FK_PROTOCOL_ASSAY 
--

CREATE INDEX MLBD_DEV.FK_PROTOCOL_ASSAY ON MLBD_DEV.PROTOCOL(ASSAY_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_RESULT_STATUS 
--

CREATE INDEX MLBD_DEV.FK_RESULT_RESULT_STATUS ON MLBD_DEV.RESULT(RESULT_STATUS_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_EXPERIMENT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_EXPERIMENT ON MLBD_DEV.RESULT(EXPERIMENT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_RESULT_CONTEXT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_RESULT_CONTEXT ON MLBD_DEV.RESULT(RESULT_CONTEXT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_SUBSTANCE 
--

CREATE INDEX MLBD_DEV.FK_RESULT_SUBSTANCE ON MLBD_DEV.RESULT(SUBSTANCE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_UNIT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_UNIT ON MLBD_DEV.RESULT(ENTRY_UNIT)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_RESULT_TYPE 
--

CREATE INDEX MLBD_DEV.FK_RESULT_RESULT_TYPE ON MLBD_DEV.RESULT(RESULT_TYPE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_QUALIFIER 
--

CREATE INDEX MLBD_DEV.FK_RESULT_QUALIFIER ON MLBD_DEV.RESULT(QUALIFIER)
;
-- 
-- INDEX: MLBD_DEV.FK_R_CONTEXT_ITEM_EXPERIMENT 
--

CREATE INDEX MLBD_DEV.FK_R_CONTEXT_ITEM_EXPERIMENT ON MLBD_DEV.RESULT_CONTEXT_ITEM(EXPERIMENT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_R_CONTEXT_ITEM_R_CONTEXT 
--

CREATE INDEX MLBD_DEV.FK_R_CONTEXT_ITEM_R_CONTEXT ON MLBD_DEV.RESULT_CONTEXT_ITEM(RESULT_CONTEXT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_R_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX MLBD_DEV.FK_R_CONTEXT_ITEM_ATTRIBUTE ON MLBD_DEV.RESULT_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_R_CONTEXT_ITEM_VALUE 
--

CREATE INDEX MLBD_DEV.FK_R_CONTEXT_ITEM_VALUE ON MLBD_DEV.RESULT_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_HIERARCHY_RSLT_PRNT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_HIERARCHY_RSLT_PRNT ON MLBD_DEV.RESULT_HIERARCHY(RESULT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_HIERARCHY_RESULT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_HIERARCHY_RESULT ON MLBD_DEV.RESULT_HIERARCHY(PARENT_RESULT_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_TYPE_ELEMENT_STATUS 
--

CREATE INDEX MLBD_DEV.FK_RESULT_TYPE_ELEMENT_STATUS ON MLBD_DEV.RESULT_TYPE(RESULT_TYPE_STATUS_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_TYPE_UNIT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_TYPE_UNIT ON MLBD_DEV.RESULT_TYPE(BASE_UNIT)
;
-- 
-- INDEX: MLBD_DEV.FK_RESULT_TYPE_RSLT_TYP_PRNT 
--

CREATE INDEX MLBD_DEV.FK_RESULT_TYPE_RSLT_TYP_PRNT ON MLBD_DEV.RESULT_TYPE(PARENT_RESULT_TYPE_ID)
;
-- 
-- INDEX: MLBD_DEV.FK_UNIT_CONVERSION_FROM_UNIT 
--

CREATE INDEX MLBD_DEV.FK_UNIT_CONVERSION_FROM_UNIT ON MLBD_DEV.UNIT_CONVERSION(FROM_UNIT)
;
-- 
-- INDEX: MLBD_DEV.FK_UNIT_CONVERSION_TO_UNIT 
--

CREATE INDEX MLBD_DEV.FK_UNIT_CONVERSION_TO_UNIT ON MLBD_DEV.UNIT_CONVERSION(TO_UNIT)
;
-- 
-- TABLE: MLBD_DEV.ASSAY 
--

ALTER TABLE MLBD_DEV.ASSAY ADD CONSTRAINT FK_ASSAY_ASSAY_STATUS_ID 
    FOREIGN KEY (ASSAY_STATUS_ID)
    REFERENCES MLBD_DEV.ASSAY_STATUS(ASSAY_STATUS_ID)
;


-- 
-- TABLE: MLBD_DEV.ELEMENT 
--

ALTER TABLE MLBD_DEV.ELEMENT ADD CONSTRAINT FK_ELEMENT_ELEMENT_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES MLBD_DEV.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE MLBD_DEV.ELEMENT ADD CONSTRAINT FK_ELEMENT_PARENT_ELEMENT 
    FOREIGN KEY (PARENT_ELEMENT_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;

ALTER TABLE MLBD_DEV.ELEMENT ADD CONSTRAINT FK_ELEMENT_UNIT 
    FOREIGN KEY (UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;


-- 
-- TABLE: MLBD_DEV.EXPERIMENT 
--

ALTER TABLE MLBD_DEV.EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;

ALTER TABLE MLBD_DEV.EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_EXPRT_STATUS 
    FOREIGN KEY (EXPERIMENT_STATUS_ID)
    REFERENCES MLBD_DEV.EXPERIMENT_STATUS(EXPERIMENT_STATUS_ID)
;

ALTER TABLE MLBD_DEV.EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_SOURCE_LAB 
    FOREIGN KEY (SOURCE_ID)
    REFERENCES MLBD_DEV.LABORATORY(LAB_ID)
;

ALTER TABLE MLBD_DEV.EXPERIMENT ADD CONSTRAINT FK_PROJECT_EXPERIMENT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES MLBD_DEV.PROJECT(PROJECT_ID)
;


-- 
-- TABLE: MLBD_DEV.EXTERNAL_ASSAY 
--

ALTER TABLE MLBD_DEV.EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;

ALTER TABLE MLBD_DEV.EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_EXT_SYSTEM 
    FOREIGN KEY (EXTERNAL_SYSTEM_ID)
    REFERENCES MLBD_DEV.EXTERNAL_SYSTEM(EXTERNAL_SYSTEM_ID)
;


-- 
-- TABLE: MLBD_DEV.MEASURE 
--

ALTER TABLE MLBD_DEV.MEASURE ADD CONSTRAINT FK_MEASURE_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;

ALTER TABLE MLBD_DEV.MEASURE ADD CONSTRAINT FK_MEASURE_M_CONTEXT_ITEM 
    FOREIGN KEY (MEASURE_CONTEXT_ID)
    REFERENCES MLBD_DEV.MEASURE_CONTEXT(MEASURE_CONTEXT_ID)
;

ALTER TABLE MLBD_DEV.MEASURE ADD CONSTRAINT FK_MEASURE_RESULT_TYPE 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES MLBD_DEV.RESULT_TYPE(RESULT_TYPE_ID)
;

ALTER TABLE MLBD_DEV.MEASURE ADD CONSTRAINT FK_MEASURE_UNIT 
    FOREIGN KEY (ENTRY_UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;


-- 
-- TABLE: MLBD_DEV.MEASURE_CONTEXT_ITEM 
--

ALTER TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;

ALTER TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;

ALTER TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_M_CONTEXT 
    FOREIGN KEY (MEASURE_CONTEXT_ID)
    REFERENCES MLBD_DEV.MEASURE_CONTEXT(MEASURE_CONTEXT_ID)
;

ALTER TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES MLBD_DEV.QUALIFIER(QUALIFIER)
;

ALTER TABLE MLBD_DEV.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: MLBD_DEV.ONTOLOGY_ITEM 
--

ALTER TABLE MLBD_DEV.ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ELEMENT 
    FOREIGN KEY (ELEMENT_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;

ALTER TABLE MLBD_DEV.ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ONTOLOGY 
    FOREIGN KEY (ONTOLOGY_ID)
    REFERENCES MLBD_DEV.ONTOLOGY(ONTOLOGY_ID)
;

ALTER TABLE MLBD_DEV.ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_RESULT_TYPE 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES MLBD_DEV.RESULT_TYPE(RESULT_TYPE_ID)
;


-- 
-- TABLE: MLBD_DEV.PROJECT_ASSAY 
--

ALTER TABLE MLBD_DEV.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;

ALTER TABLE MLBD_DEV.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_PROJECT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES MLBD_DEV.PROJECT(PROJECT_ID)
;

ALTER TABLE MLBD_DEV.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_STAGE 
    FOREIGN KEY (STAGE)
    REFERENCES MLBD_DEV.STAGE(STAGE)
;


-- 
-- TABLE: MLBD_DEV.PROTOCOL 
--

ALTER TABLE MLBD_DEV.PROTOCOL ADD CONSTRAINT FK_PROTOCOL_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES MLBD_DEV.ASSAY(ASSAY_ID)
;


-- 
-- TABLE: MLBD_DEV.RESULT 
--

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES MLBD_DEV.EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES MLBD_DEV.QUALIFIER(QUALIFIER)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_RESULT_CONTEXT 
    FOREIGN KEY (RESULT_CONTEXT_ID)
    REFERENCES MLBD_DEV.RESULT_CONTEXT(RESULT_CONTEXT_ID)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_RESULT_STATUS 
    FOREIGN KEY (RESULT_STATUS_ID)
    REFERENCES MLBD_DEV.RESULT_STATUS(RESULT_STATUS_ID)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_RESULT_TYPE 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES MLBD_DEV.RESULT_TYPE(RESULT_TYPE_ID)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_SUBSTANCE 
    FOREIGN KEY (SUBSTANCE_ID)
    REFERENCES MLBD_DEV.SUBSTANCE(SUBSTANCE_ID)
;

ALTER TABLE MLBD_DEV.RESULT ADD CONSTRAINT FK_RESULT_UNIT 
    FOREIGN KEY (ENTRY_UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;


-- 
-- TABLE: MLBD_DEV.RESULT_CONTEXT_ITEM 
--

ALTER TABLE MLBD_DEV.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;

ALTER TABLE MLBD_DEV.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES MLBD_DEV.EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE MLBD_DEV.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_context_item_qualifier 
    FOREIGN KEY (QUALIFIER)
    REFERENCES MLBD_DEV.QUALIFIER(QUALIFIER)
;

ALTER TABLE MLBD_DEV.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_R_CONTEXT 
    FOREIGN KEY (RESULT_CONTEXT_ID)
    REFERENCES MLBD_DEV.RESULT_CONTEXT(RESULT_CONTEXT_ID)
;

ALTER TABLE MLBD_DEV.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES MLBD_DEV.ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: MLBD_DEV.RESULT_HIERARCHY 
--

ALTER TABLE MLBD_DEV.RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RESULT 
    FOREIGN KEY (PARENT_RESULT_ID)
    REFERENCES MLBD_DEV.RESULT(RESULT_ID)
;

ALTER TABLE MLBD_DEV.RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RSLT_PRNT 
    FOREIGN KEY (RESULT_ID)
    REFERENCES MLBD_DEV.RESULT(RESULT_ID)
;


-- 
-- TABLE: MLBD_DEV.RESULT_TYPE 
--

ALTER TABLE MLBD_DEV.RESULT_TYPE ADD CONSTRAINT FK_RESULT_TYPE_ELEMENT_STATUS 
    FOREIGN KEY (RESULT_TYPE_STATUS_ID)
    REFERENCES MLBD_DEV.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE MLBD_DEV.RESULT_TYPE ADD CONSTRAINT FK_RESULT_TYPE_RSLT_TYP_PRNT 
    FOREIGN KEY (PARENT_RESULT_TYPE_ID)
    REFERENCES MLBD_DEV.RESULT_TYPE(RESULT_TYPE_ID)
;

ALTER TABLE MLBD_DEV.RESULT_TYPE ADD CONSTRAINT FK_RESULT_TYPE_UNIT 
    FOREIGN KEY (BASE_UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;


-- 
-- TABLE: MLBD_DEV.UNIT_CONVERSION 
--

ALTER TABLE MLBD_DEV.UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSION_FROM_UNIT 
    FOREIGN KEY (FROM_UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;

ALTER TABLE MLBD_DEV.UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSION_TO_UNIT 
    FOREIGN KEY (TO_UNIT)
    REFERENCES MLBD_DEV.UNIT(UNIT)
;


