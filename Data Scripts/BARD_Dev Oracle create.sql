--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Friday, April 27, 2012 14:28:58
-- Target DBMS : Oracle 11g
--

-- 
-- SEQUENCE: ASSAY_ID_SEQ 
--

CREATE SEQUENCE ASSAY_ID_SEQ
    START WITH 3
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: ASSAY_STATUS_ID_SEQ 
--

CREATE SEQUENCE ASSAY_STATUS_ID_SEQ
    START WITH 6
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

-- 
-- SEQUENCE: ELEMENT_ID_SEQ 
--

CREATE SEQUENCE ELEMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: ELEMENT_STATUS_ID_SEQ 
--

CREATE SEQUENCE ELEMENT_STATUS_ID_SEQ
    START WITH 5
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

-- 
-- SEQUENCE: EXPERIMENT_ID_SEQ 
--

CREATE SEQUENCE EXPERIMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: EXPERIMENT_STATUS_ID_SEQ 
--

CREATE SEQUENCE EXPERIMENT_STATUS_ID_SEQ
    START WITH 6
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

-- 
-- SEQUENCE: EXTERNAL_SYSTEM_ID_SEQ 
--

CREATE SEQUENCE EXTERNAL_SYSTEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

-- 
-- SEQUENCE: MEASURE_CONTEXT_ID_SEQ 
--

CREATE SEQUENCE MEASURE_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: MEASURE_CONTEXT_ITEM_ID_SEQ 
--

CREATE SEQUENCE MEASURE_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: MEASURE_ID_SEQ 
--

CREATE SEQUENCE MEASURE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: NODE_ID_SEQ 
--

CREATE SEQUENCE NODE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: ONTOLOGY_ID_SEQ 
--

CREATE SEQUENCE ONTOLOGY_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: ONTOLOGY_ITEM_ID_SEQ 
--

CREATE SEQUENCE ONTOLOGY_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: PROJECT_ID_SEQ 
--

CREATE SEQUENCE PROJECT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: PROTOCOL_ID_SEQ 
--

CREATE SEQUENCE PROTOCOL_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: RESULT_CONTEXT_ITEM_ID_SEQ 
--

CREATE SEQUENCE RESULT_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: RESULT_ID_SEQ 
--

CREATE SEQUENCE RESULT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: RESULT_STATUS_ID_SEQ 
--

CREATE SEQUENCE RESULT_STATUS_ID_SEQ
    START WITH 7
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

-- 
-- SEQUENCE: RESULT_TYPE_ID_SEQ 
--

CREATE SEQUENCE RESULT_TYPE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- SEQUENCE: SUBSTANCE_ID_SEQ 
--

CREATE SEQUENCE SUBSTANCE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;

-- 
-- TABLE: ASSAY 
--

CREATE TABLE ASSAY(
    ASSAY_ID           NUMBER(19, 0)     NOT NULL,
    ASSAY_NAME         VARCHAR2(128)     NOT NULL,
    ASSAY_STATUS_ID    NUMBER(19, 0)     NOT NULL,
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



COMMENT ON COLUMN ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: ASSAY_DESCRIPTOR 
--

CREATE TABLE ASSAY_DESCRIPTOR(
    NODE_ID              NUMBER(19, 0)     NOT NULL,
    PARENT_NODE_ID       NUMBER(19, 0),
    ELEMENT_ID           NUMBER(19, 0)     NOT NULL,
    LABEL                VARCHAR2(128)     NOT NULL,
    DESCRIPTION          VARCHAR2(1000),
    ABBREVIATION         VARCHAR2(20),
    ACRONYM              VARCHAR2(20),
    SYNONYMS             VARCHAR2(1000),
    EXTERNAL_URL         VARCHAR2(1000),
    UNIT                 VARCHAR2(128),
    ELEMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    CONSTRAINT PK_ASSAY_DESCRIPTOR PRIMARY KEY (NODE_ID)
)
;



-- 
-- TABLE: ASSAY_STATUS 
--

CREATE TABLE ASSAY_STATUS(
    ASSAY_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    STATUS             VARCHAR2(20)     NOT NULL,
    VERSION            NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created       TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated       TIMESTAMP(6),
    MODIFIED_BY        VARCHAR2(40),
    CONSTRAINT PK_ASSAY_STATUS PRIMARY KEY (ASSAY_STATUS_ID)
)
;



COMMENT ON COLUMN ASSAY_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Assay_Status (assay_status_ID, status) values ('1', 'Pending');
insert into Assay_Status (assay_status_ID, status) values ('2', 'Active');
insert into Assay_Status (assay_status_ID, status) values ('3', 'Superceded');
insert into Assay_Status (assay_status_ID, status) values ('4', 'Retired');
commit;
-- 
-- TABLE: BIOLOGY_DESCRIPTOR 
--

CREATE TABLE BIOLOGY_DESCRIPTOR(
    NODE_ID              VARCHAR2(20)      NOT NULL,
    PARENT_NODE_ID       VARCHAR2(20),
    ELEMENT_ID           NUMBER(19, 0)     NOT NULL,
    LABEL                VARCHAR2(128)     NOT NULL,
    DESCRIPTION          VARCHAR2(1000),
    ABBREVIATION         VARCHAR2(20),
    ACRONYM              VARCHAR2(20),
    SYNONYMS             VARCHAR2(1000),
    EXTERNAL_URL         VARCHAR2(1000),
    UNIT                 VARCHAR2(128),
    ELEMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    CONSTRAINT PK_BIOLOGY_DESCRIPTOR PRIMARY KEY (NODE_ID)
)
;



-- 
-- TABLE: DATABASECHANGELOG 
--

CREATE TABLE DATABASECHANGELOG(
    ID               VARCHAR2(63)     NOT NULL,
    AUTHOR           VARCHAR2(63)     NOT NULL,
    FILENAME         VARCHAR2(200)    NOT NULL,
    DATEEXECUTED     TIMESTAMP(6)     NOT NULL,
    ORDEREXECUTED    NUMBER           NOT NULL,
    EXECTYPE         VARCHAR2(10)     NOT NULL,
    MD5SUM           VARCHAR2(35),
    DESCRIPTION      VARCHAR2(255),
    COMMENTS         VARCHAR2(255),
    TAG              VARCHAR2(255),
    LIQUIBASE        VARCHAR2(20),
    CONSTRAINT PK_DATABASECHANGELOG PRIMARY KEY (ID, AUTHOR, FILENAME)
)
;



-- 
-- TABLE: DATABASECHANGELOGLOCK 
--

CREATE TABLE DATABASECHANGELOGLOCK(
    ID             NUMBER           NOT NULL,
    LOCKED         NUMBER(1, 0)     NOT NULL,
    LOCKGRANTED    TIMESTAMP(6),
    LOCKEDBY       VARCHAR2(255),
    CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID)
)
;



-- 
-- TABLE: ELEMENT 
--

CREATE TABLE ELEMENT(
    ELEMENT_ID           NUMBER(19, 0)     NOT NULL,
    LABEL                VARCHAR2(128)     NOT NULL,
    DESCRIPTION          VARCHAR2(1000),
    ABBREVIATION         VARCHAR2(20),
    ACRONYM              VARCHAR2(20),
    SYNONYMS             VARCHAR2(1000),
    EXTERNAL_URL         VARCHAR2(1000),
    UNIT                 VARCHAR2(128),
    ELEMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    VERSION              NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT PRIMARY KEY (ELEMENT_ID)
)
;



COMMENT ON COLUMN ELEMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: ELEMENT_HIERARCHY 
--

CREATE TABLE ELEMENT_HIERARCHY(
    PARENT_ELEMENT_ID    NUMBER(19, 0),
    CHILD_ELEMENT_ID     NUMBER(19, 0)    NOT NULL,
    RELATIONSHIP_TYPE    VARCHAR2(40)     NOT NULL,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40)
)
;



COMMENT ON COLUMN ELEMENT_HIERARCHY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: ELEMENT_STATUS 
--

CREATE TABLE ELEMENT_STATUS(
    ELEMENT_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    ELEMENT_STATUS       VARCHAR2(20)     NOT NULL,
    CAPABILITY           VARCHAR2(256),
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT_STATUS PRIMARY KEY (ELEMENT_STATUS_ID)
)
;



COMMENT ON COLUMN ELEMENT_STATUS.CAPABILITY IS 'Description of the actions allowed when elements are in this state'
;
COMMENT ON COLUMN ELEMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
--
--
insert into Element_status (Element_status_id, element_status, Capability) values ('1', 'Pending', 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval');
insert into Element_status (Element_status_id, element_status, Capability) values ('2', 'Published', 'Element can be used for any assay definiton or data upload');
insert into Element_status (Element_status_id, element_status, Capability) values ('3', 'Deprecated', 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement');
insert into Element_status (Element_status_id, element_status, Capability) values ('4', 'Retired', 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data');
commit;
--
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (0, 'Root', 'single origin for all hierarchies in the dictionary', '', '', '', '', '', 2);
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (1, 'assay', 'terms for describing the assay design, etc.', '', '', '', '', '', 2);
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (2, 'biology', 'terms for describing the assay biology', '', '', '', '', '', 2);
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (3, 'result', 'terms describing differrent result types in assays', '', '', '', '', '', 2);
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (4, 'project management', 'terms describing data gathered at assay run time - the assay instance', '', '', '', '', '', 2);
INSERT INTO element ( ELEMENT_ID, LABEL, DESCRIPTION, ABBREVIATION, ACRONYM, SYNONYMS, EXTERNAL_URL, UNIT, ELEMENT_STATUS_ID)
values (5, 'unit of measurement', 'units of measure used throughout', '', '', '', '', '', 2);
commit;
--
--
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values ('', 0, 'is_a');
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values (0, 1, 'is_a');
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values (0, 2, 'is_a');
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values (0, 3, 'is_a');
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values (0, 4, 'is_a');
insert into element_hierarchy ( PARENT_ELEMENT_ID, CHILD_ELEMENT_ID, RELATIONSHIP_TYPE)
values (0, 5, 'is_a');
commit;
--
--
-- 
-- TABLE: EXPERIMENT 
--

CREATE TABLE EXPERIMENT(
    EXPERIMENT_ID           NUMBER(19, 0)     NOT NULL,
    EXPERIMENT_NAME         VARCHAR2(256)     NOT NULL,
    ASSAY_ID                NUMBER(19, 0)     NOT NULL,
    PROJECT_ID              NUMBER(19, 0),
    EXPERIMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    RUN_DATE_FROM           DATE,
    RUN_DATE_TO             DATE,
    HOLD_UNTIL_DATE         DATE,
    DESCRIPTION             VARCHAR2(1000),
    VERSION                 NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT PRIMARY KEY (EXPERIMENT_ID)
)
;



COMMENT ON COLUMN EXPERIMENT.HOLD_UNTIL_DATE IS 'can only be set a max of 1 year in the future'
;
COMMENT ON COLUMN EXPERIMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: EXPERIMENT_STATUS 
--

CREATE TABLE EXPERIMENT_STATUS(
    EXPERIMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    STATUS                  VARCHAR2(20)      NOT NULL,
    CAPABILITY              VARCHAR2(1000),
    VERSION                 NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT_STATUS PRIMARY KEY (EXPERIMENT_STATUS_ID)
)
;



COMMENT ON COLUMN EXPERIMENT_STATUS.CAPABILITY IS 'describes the actions that can be done with this experiment status and the limitations (this is help text)'
;
COMMENT ON COLUMN EXPERIMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('2', 'Approved', 'Experiment has been approved as ready to upload.  It does not mena results are correct or cannot be changed');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('3', 'Rejected', 'Experiment data has been rejected as not scientifically valid.  This will not be uploaded to the warehouse');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('4', 'Held', 'Experiment data is private to the loading institution (Source Laboratory).  The Hold Until Date is set.  Though uploaded it cannot be queried except by the source laboratory');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('5', 'Uploaded', 'Experiment has been copied into the warehouse and results are available for querying');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('6', 'Mark for Deletion', 'Experiment has been confirmed as present in the warehouse and may be deleted at any time.');
commit;
-- 
-- TABLE: EXTERNAL_ASSAY 
--

CREATE TABLE EXTERNAL_ASSAY(
    EXTERNAL_SYSTEM_ID    NUMBER(19, 0)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    EXT_ASSAY_ID          VARCHAR2(128)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_EXTERNAL_ASSAY PRIMARY KEY (EXTERNAL_SYSTEM_ID, ASSAY_ID)
)
;



COMMENT ON COLUMN EXTERNAL_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: EXTERNAL_SYSTEM 
--

CREATE TABLE EXTERNAL_SYSTEM(
    EXTERNAL_SYSTEM_ID    NUMBER(19, 0)     NOT NULL,
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



COMMENT ON COLUMN EXTERNAL_SYSTEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: INSTANCE_DESCRIPTOR 
--

CREATE TABLE INSTANCE_DESCRIPTOR(
    NODE_ID              NUMBER(19, 0)     NOT NULL,
    PARENT_NODE_ID       NUMBER(19, 0),
    ELEMENT_ID           NUMBER(19, 0)     NOT NULL,
    LABEL                VARCHAR2(128)     NOT NULL,
    DESCRIPTION          VARCHAR2(1000),
    ABBREVIATION         VARCHAR2(20),
    ACRONYM              VARCHAR2(20),
    SYNONYMS             VARCHAR2(1000),
    EXTERNAL_URL         VARCHAR2(1000),
    UNIT                 VARCHAR2(128),
    ELEMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    CONSTRAINT PK_INSTANCE_DESCRIPTOR PRIMARY KEY (NODE_ID)
)
;



-- 
-- TABLE: MEASURE 
--

CREATE TABLE MEASURE(
    MEASURE_ID            NUMBER(19, 0)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    MEASURE_CONTEXT_ID    NUMBER(19, 0)    NOT NULL,
    RESULT_TYPE_ID        NUMBER(19, 0)    NOT NULL,
    ENTRY_UNIT            VARCHAR2(128),
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE PRIMARY KEY (MEASURE_ID)
)
;



COMMENT ON COLUMN MEASURE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: MEASURE_CONTEXT 
--

CREATE TABLE MEASURE_CONTEXT(
    MEASURE_CONTEXT_ID    NUMBER(19, 0)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    CONTEXT_NAME          VARCHAR2(128)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT PRIMARY KEY (MEASURE_CONTEXT_ID)
)
;



COMMENT ON COLUMN MEASURE_CONTEXT.CONTEXT_NAME IS 'default name should be ''Context for '' || Result_Type_Name'
;
COMMENT ON COLUMN MEASURE_CONTEXT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: MEASURE_CONTEXT_ITEM 
--

CREATE TABLE MEASURE_CONTEXT_ITEM(
    MEASURE_CONTEXT_ITEM_ID          NUMBER(19, 0)    NOT NULL,
    GROUP_MEASURE_CONTEXT_ITEM_ID    NUMBER(19, 0),
    ASSAY_ID                         NUMBER(19, 0)    NOT NULL,
    MEASURE_CONTEXT_ID               NUMBER(19, 0),
    ATTRIBUTE_TYPE                   VARCHAR2(20)     NOT NULL
                                     CONSTRAINT CK_ATTRIBUTE_TYPE CHECK (ATTRIBUTE_TYPE in ('Fixed', 'List', 'Range', 'Number')),
    ATTRIBUTE_ID                     NUMBER(19, 0)    NOT NULL,
    QUALIFIER                        CHAR(2),
    VALUE_ID                         NUMBER(19, 0),
    VALUE_DISPLAY                    VARCHAR2(256),
    VALUE_NUM                        FLOAT(126),
    VALUE_MIN                        FLOAT(126),
    VALUE_MAX                        FLOAT(126),
    VERSION                          NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created                     TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated                     TIMESTAMP(6),
    MODIFIED_BY                      VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT_ITEM PRIMARY KEY (MEASURE_CONTEXT_ITEM_ID)
)
;



COMMENT ON COLUMN MEASURE_CONTEXT_ITEM.ASSAY_ID IS 'This column is useful for creating the assay defintion before the measures and their contexts have been properly grouped.'
;
COMMENT ON COLUMN MEASURE_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN MEASURE_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: ONTOLOGY 
--

CREATE TABLE ONTOLOGY(
    ONTOLOGY_ID      NUMBER(19, 0)     NOT NULL,
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



COMMENT ON TABLE ONTOLOGY IS 'an external ontology or dictionary or other source of reference data'
;
COMMENT ON COLUMN ONTOLOGY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: ONTOLOGY_ITEM 
--

CREATE TABLE ONTOLOGY_ITEM(
    ONTOLOGY_ITEM_ID    NUMBER(19, 0)    NOT NULL,
    ONTOLOGY_ID         NUMBER(19, 0)    NOT NULL,
    ELEMENT_ID          NUMBER(19, 0),
    ITEM_REFERENCE      CHAR(10),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_ONTOLOGY_ITEM PRIMARY KEY (ONTOLOGY_ITEM_ID)
)
;



COMMENT ON COLUMN ONTOLOGY_ITEM.ITEM_REFERENCE IS 'Concatenate this with the Ontology.system_URL for a full URI for the item'
;
COMMENT ON COLUMN ONTOLOGY_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: PROJECT 
--

CREATE TABLE PROJECT(
    PROJECT_ID      NUMBER(19, 0)     NOT NULL,
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



COMMENT ON COLUMN PROJECT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: PROJECT_ASSAY 
--

CREATE TABLE PROJECT_ASSAY(
    PROJECT_ASSAY_ID       NUMBER(19, 0)     NOT NULL,
    PROJECT_ID             NUMBER(19, 0)     NOT NULL,
    ASSAY_ID               NUMBER(19, 0)     NOT NULL,
    STAGE                  VARCHAR2(128)     NOT NULL,
    RELATED_ASSAY_ID       NUMBER(19, 0),
    SEQUENCE_NO            NUMBER(38, 0),
    PROMOTION_THRESHOLD    FLOAT(126),
    PROMOTION_CRITERIA     VARCHAR2(1000),
    VERSION                NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created           TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated           TIMESTAMP(6),
    MODIFIED_BY            VARCHAR2(40),
    CONSTRAINT PK_PROJECT_ASSAY PRIMARY KEY (PROJECT_ASSAY_ID)
)
;



COMMENT ON COLUMN PROJECT_ASSAY.SEQUENCE_NO IS 'defines the promotion order (and often the running order) of a set of assays in a project'
;
COMMENT ON COLUMN PROJECT_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: PROTOCOL 
--

CREATE TABLE PROTOCOL(
    PROTOCOL_ID          NUMBER(19, 0)    NOT NULL,
    ASSAY_ID             NUMBER(19, 0)    NOT NULL,
    PROTOCOL_NAME        VARCHAR2(500)    NOT NULL,
    PROTOCOL_DOCUMENT    LONG RAW,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_PROTOCOL PRIMARY KEY (PROTOCOL_ID)
)
;



COMMENT ON COLUMN PROTOCOL.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: QUALIFIER 
--

CREATE TABLE QUALIFIER(
    QUALIFIER       CHAR(2)           NOT NULL,
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_QUALIFIER PRIMARY KEY (QUALIFIER)
)
;



COMMENT ON COLUMN QUALIFIER.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into qualifier (qualifier, description) values ('=', 'equals');
insert into qualifier (qualifier, description) values ('>=', 'greater than or equal');
insert into qualifier (qualifier, description) values ('<=', 'less than or equal');
insert into qualifier (qualifier, description) values ('<', 'greater than');
insert into qualifier (qualifier, description) values ('>', 'less than');
insert into qualifier (qualifier, description) values ('~', 'approximately');
insert into qualifier (qualifier, description) values ('>>', 'very much greater than');
insert into qualifier (qualifier, description) values ('<<', 'very much less than');
commit;
-- 
-- TABLE: RESULT 
--

CREATE TABLE RESULT(
    RESULT_ID           NUMBER(19, 0)    NOT NULL,
    VALUE_DISPLAY       VARCHAR2(256),
    VALUE_NUM           FLOAT(126),
    VALUE_MIN           FLOAT(126),
    VALUE_MAX           FLOAT(126),
    QUALIFIER           CHAR(2),
    RESULT_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    EXPERIMENT_ID       NUMBER(19, 0)    NOT NULL,
    SUBSTANCE_ID        NUMBER(19, 0)    NOT NULL,
    RESULT_TYPE_ID      NUMBER(19, 0)    NOT NULL,
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT PRIMARY KEY (RESULT_ID)
)
;



COMMENT ON COLUMN RESULT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: RESULT_CONTEXT_ITEM 
--

CREATE TABLE RESULT_CONTEXT_ITEM(
    RESULT_CONTEXT_ITEM_ID     NUMBER(19, 0)    NOT NULL,
    GROUP_RESULT_CONTEXT_ID    NUMBER(19, 0),
    EXPERIMENT_ID              NUMBER(19, 0)    NOT NULL,
    RESULT_ID                  NUMBER(19, 0),
    ATTRIBUTE_ID               NUMBER(19, 0)    NOT NULL,
    VALUE_ID                   NUMBER(19, 0),
    QUALIFIER                  CHAR(2),
    VALUE_DISPLAY              VARCHAR2(256),
    VALUE_NUM                  FLOAT(126),
    VALUE_MIN                  FLOAT(126),
    VALUE_MAX                  FLOAT(126),
    VERSION                    NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created               TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated               TIMESTAMP(6),
    MODIFIED_BY                VARCHAR2(40),
    CONSTRAINT PK_Result_context_item PRIMARY KEY (RESULT_CONTEXT_ITEM_ID)
)
;



COMMENT ON COLUMN RESULT_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN RESULT_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: RESULT_HIERARCHY 
--

CREATE TABLE RESULT_HIERARCHY(
    RESULT_ID           NUMBER(19, 0)    NOT NULL,
    PARENT_RESULT_ID    NUMBER(19, 0)    NOT NULL,
    HIERARCHY_TYPE      VARCHAR2(10)     NOT NULL
                        CONSTRAINT CK_RESULT_HIERARCHY_TYPE CHECK (HIERARCHY_TYPE in ('Child', 'Derives')),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_HIERARCHY PRIMARY KEY (RESULT_ID, PARENT_RESULT_ID)
)
;



COMMENT ON COLUMN RESULT_HIERARCHY.HIERARCHY_TYPE IS 'two types of hierarchy are allowed: parent/child where one result is dependant on or grouped with another; derived from where aresult is used to claculate another (e.g. PI used for IC50).  The hierarchy types are mutually exclusive.'
;
COMMENT ON COLUMN RESULT_HIERARCHY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: RESULT_STATUS 
--

CREATE TABLE RESULT_STATUS(
    RESULT_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    STATUS              VARCHAR2(20)     NOT NULL,
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_STATUS PRIMARY KEY (RESULT_STATUS_ID)
)
;



COMMENT ON COLUMN RESULT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Result_status (result_status_id, status) values ('1', 'Pending');
insert into Result_status (result_status_id, status) values ('2', 'Approved');
insert into Result_status (result_status_id, status) values ('3', 'Rejected');
insert into Result_status (result_status_id, status) values ('4', 'Uploading');
insert into Result_status (result_status_id, status) values ('5', 'Uploaded');
insert into Result_status (result_status_id, status) values ('6', 'Mark for Deletion');
commit;
-- 
-- TABLE: RESULT_TYPE 
--

CREATE TABLE RESULT_TYPE(
    NODE_ID                  NUMBER(19, 0)     NOT NULL,
    Parent_node_id           NUMBER(19, 0),
    RESULT_TYPE_ID           NUMBER(19, 0)     NOT NULL,
    RESULT_TYPE_NAME         VARCHAR2(128)     NOT NULL,
    DESCRIPTION              VARCHAR2(1000),
    ABBREVIATION             VARCHAR2(20),
    SYNONYMS                 VARCHAR2(1000),
    BASE_UNIT                VARCHAR2(128),
    RESULT_TYPE_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    CONSTRAINT PK_RESULT_TYPE PRIMARY KEY (NODE_ID)
)
;



-- 
-- TABLE: SUBSTANCE 
--

CREATE TABLE SUBSTANCE(
    SUBSTANCE_ID        NUMBER(19, 0)     NOT NULL,
    COMPOUND_ID         NUMBER(38, 0),
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



COMMENT ON COLUMN SUBSTANCE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: TREE_ROOT 
--

CREATE TABLE TREE_ROOT(
    TREE_ROOT_ID         NUMBER(19, 0)    NOT NULL,
    TREE_NAME            VARCHAR2(30)     NOT NULL,
    ELEMENT_ID           NUMBER(19, 0)    NOT NULL,
    RELATIONSHIP_TYPE    VARCHAR2(20),
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_TREE_ROOT PRIMARY KEY (TREE_ROOT_ID)
)
;



COMMENT ON COLUMN TREE_ROOT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into tree_root ( TREE_ROOT_ID, TREE_NAME, ELEMENT_ID, RELATIONSHIP_TYPE)
values (1, 'ASSAY_DESCRIPTOR', 0, 'is_a');
insert into tree_root ( TREE_ROOT_ID, TREE_NAME, ELEMENT_ID, RELATIONSHIP_TYPE)
values (2, 'BIOLOGY_DESCRIPTOR', 0, 'is_a');
insert into tree_root ( TREE_ROOT_ID, TREE_NAME, ELEMENT_ID, RELATIONSHIP_TYPE)
values (3, 'INSTANCE_DESCRIPTOR', 0, 'is_a');
insert into tree_root ( TREE_ROOT_ID, TREE_NAME, ELEMENT_ID, RELATIONSHIP_TYPE)
values (4, 'RESULT_TYPE', 0, 'is_a');
insert into tree_root ( TREE_ROOT_ID, TREE_NAME, ELEMENT_ID, RELATIONSHIP_TYPE)
values (5, 'UNIT', 0, 'is_a');
COMMIT;
-- 
-- TABLE: UNIT 
--

CREATE TABLE UNIT(
    NODE_ID           NUMBER(19, 0)     NOT NULL,
    PARENT_NODE_ID    NUMBER(19, 0),
    UNIT_ID           NUMBER(19, 0)     NOT NULL,
    UNIT              VARCHAR2(128)     NOT NULL,
    DESCRIPTION       VARCHAR2(1000),
    CONSTRAINT PK_UNIT PRIMARY KEY (NODE_ID)
)
;



-- 
-- TABLE: UNIT_CONVERSION 
--

CREATE TABLE UNIT_CONVERSION(
    FROM_UNIT       VARCHAR2(128)    NOT NULL,
    TO_UNIT         VARCHAR2(128)    NOT NULL,
    MULTIPLIER      FLOAT(126),
    OFFSET          FLOAT(126),
    FORMULA         VARCHAR2(256),
    VERSION         NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    Date_Created    TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    Last_Updated    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_UNIT_CONVERSION PRIMARY KEY (FROM_UNIT, TO_UNIT)
)
;



COMMENT ON COLUMN UNIT_CONVERSION.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- INDEX: FK_ASSAY_ASSAY_STATUS_ID 
--

CREATE INDEX FK_ASSAY_ASSAY_STATUS_ID ON ASSAY(ASSAY_STATUS_ID)
;
-- 
-- INDEX: FK_ASSAY_DESCRIPTOR_PARENT_SLF 
--

CREATE INDEX FK_ASSAY_DESCRIPTOR_PARENT_SLF ON ASSAY_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: FK_ASSAY_DESCRIPTOR_STATUS 
--

CREATE INDEX FK_ASSAY_DESCRIPTOR_STATUS ON ASSAY_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: AK_ASSAY_STATUS 
--

CREATE UNIQUE INDEX AK_ASSAY_STATUS ON ASSAY_STATUS(STATUS)
;
-- 
-- INDEX: FK_BIOLOGY_DESCRIPTOR_PRNT_SLF 
--

CREATE INDEX FK_BIOLOGY_DESCRIPTOR_PRNT_SLF ON BIOLOGY_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: FK_BIOLOGY_DESCRIPTOR_STATUS 
--

CREATE INDEX FK_BIOLOGY_DESCRIPTOR_STATUS ON BIOLOGY_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: FK_ELEMENT_UNIT 
--

CREATE INDEX FK_ELEMENT_UNIT ON ELEMENT(UNIT)
;
-- 
-- INDEX: AK_ELEMENT_LABEL 
--

CREATE UNIQUE INDEX AK_ELEMENT_LABEL ON ELEMENT(LABEL)
;
-- 
-- INDEX: FK_ELEMENT_ELEMENT_STATUS 
--

CREATE INDEX FK_ELEMENT_ELEMENT_STATUS ON ELEMENT(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: AK_ELEMENT_HIERARCHY 
--

CREATE UNIQUE INDEX AK_ELEMENT_HIERARCHY ON ELEMENT_HIERARCHY(CHILD_ELEMENT_ID, PARENT_ELEMENT_ID, RELATIONSHIP_TYPE)
;
-- 
-- INDEX: FK_E_HIERARCHY_PARENT_ELEM_ID 
--

CREATE INDEX FK_E_HIERARCHY_PARENT_ELEM_ID ON ELEMENT_HIERARCHY(PARENT_ELEMENT_ID)
;
-- 
-- INDEX: FK_E_HIERARCHY_CHILD_ELEM_ID 
--

CREATE INDEX FK_E_HIERARCHY_CHILD_ELEM_ID ON ELEMENT_HIERARCHY(CHILD_ELEMENT_ID)
;
-- 
-- INDEX: FK_EXPERIMENT_ASSAY 
--

CREATE INDEX FK_EXPERIMENT_ASSAY ON EXPERIMENT(ASSAY_ID)
;
-- 
-- INDEX: FK_PROJECT_EXPERIMENT 
--

CREATE INDEX FK_PROJECT_EXPERIMENT ON EXPERIMENT(PROJECT_ID)
;
-- 
-- INDEX: FK_EXPERIMENT_EXPRT_STATUS 
--

CREATE INDEX FK_EXPERIMENT_EXPRT_STATUS ON EXPERIMENT(EXPERIMENT_STATUS_ID)
;
-- 
-- INDEX: FK_EXT_ASSAY_ASSAY 
--

CREATE INDEX FK_EXT_ASSAY_ASSAY ON EXTERNAL_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: "FK_EXT_ASSAY_EXT_SYSTEM" 
--

CREATE INDEX "FK_EXT_ASSAY_EXT_SYSTEM" ON EXTERNAL_ASSAY(EXTERNAL_SYSTEM_ID)
;
-- 
-- INDEX: FK_INSTANCE_DESCRIPTR_PRNT_SLF 
--

CREATE INDEX FK_INSTANCE_DESCRIPTR_PRNT_SLF ON INSTANCE_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: FK_INSTANCE_DESCRIPTOR_STATUS 
--

CREATE INDEX FK_INSTANCE_DESCRIPTOR_STATUS ON INSTANCE_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: FK_MEASURE_RESULT_TYPE 
--

CREATE INDEX FK_MEASURE_RESULT_TYPE ON MEASURE(RESULT_TYPE_ID)
;
-- 
-- INDEX: FK_MEASURE_M_CONTEXT 
--

CREATE INDEX FK_MEASURE_M_CONTEXT ON MEASURE(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: FK_MEASURE_ELEMENT_UNIT 
--

CREATE INDEX FK_MEASURE_ELEMENT_UNIT ON MEASURE(ENTRY_UNIT)
;
-- 
-- INDEX: AK_MEASURE_CONTEXT_ASSAY 
--

CREATE UNIQUE INDEX AK_MEASURE_CONTEXT_ASSAY ON MEASURE_CONTEXT(ASSAY_ID, MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: AK_MEASURE_CONTEXT_ITEM 
--

CREATE UNIQUE INDEX AK_MEASURE_CONTEXT_ITEM ON MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID, GROUP_MEASURE_CONTEXT_ITEM_ID, ATTRIBUTE_ID, VALUE_DISPLAY)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_M_CONTEXT 
--

CREATE INDEX FK_M_CONTEXT_ITEM_M_CONTEXT ON MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX FK_M_CONTEXT_ITEM_ATTRIBUTE ON MEASURE_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_VALUE 
--

CREATE INDEX FK_M_CONTEXT_ITEM_VALUE ON MEASURE_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_ASSAY 
--

CREATE INDEX FK_M_CONTEXT_ITEM_ASSAY ON MEASURE_CONTEXT_ITEM(ASSAY_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX FK_M_CONTEXT_ITEM_QUALIFIER ON MEASURE_CONTEXT_ITEM(QUALIFIER)
;
-- 
-- INDEX: FK_MEASURE_CONTEXT_ITEM_GROUP 
--

CREATE INDEX FK_MEASURE_CONTEXT_ITEM_GROUP ON MEASURE_CONTEXT_ITEM(GROUP_MEASURE_CONTEXT_ITEM_ID)
;
-- 
-- INDEX: FK_ONTOLOGY_ITEM_ONTOLOGY 
--

CREATE INDEX FK_ONTOLOGY_ITEM_ONTOLOGY ON ONTOLOGY_ITEM(ONTOLOGY_ID)
;
-- 
-- INDEX: FK_ONTOLOGY_ITEM_ELEMENT 
--

CREATE INDEX FK_ONTOLOGY_ITEM_ELEMENT ON ONTOLOGY_ITEM(ELEMENT_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_ASSAY 
--

CREATE INDEX FK_PROJECT_ASSAY_ASSAY ON PROJECT_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_PROJECT 
--

CREATE INDEX FK_PROJECT_ASSAY_PROJECT ON PROJECT_ASSAY(PROJECT_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_STAGE 
--

CREATE INDEX FK_PROJECT_ASSAY_STAGE ON PROJECT_ASSAY(STAGE)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_RELATED_ASSAY 
--

CREATE INDEX FK_PROJECT_ASSAY_RELATED_ASSAY ON PROJECT_ASSAY(RELATED_ASSAY_ID)
;
-- 
-- INDEX: FK_PROTOCOL_ASSAY 
--

CREATE INDEX FK_PROTOCOL_ASSAY ON PROTOCOL(ASSAY_ID)
;
-- 
-- INDEX: FK_RESULT_RESULT_STATUS 
--

CREATE INDEX FK_RESULT_RESULT_STATUS ON RESULT(RESULT_STATUS_ID)
;
-- 
-- INDEX: FK_RESULT_EXPERIMENT 
--

CREATE INDEX FK_RESULT_EXPERIMENT ON RESULT(EXPERIMENT_ID)
;
-- 
-- INDEX: FK_RESULT_SUBSTANCE 
--

CREATE INDEX FK_RESULT_SUBSTANCE ON RESULT(SUBSTANCE_ID)
;
-- 
-- INDEX: FK_RESULT_RESULT_TYPE 
--

CREATE INDEX FK_RESULT_RESULT_TYPE ON RESULT(RESULT_TYPE_ID)
;
-- 
-- INDEX: FK_RESULT_QUALIFIER 
--

CREATE INDEX FK_RESULT_QUALIFIER ON RESULT(QUALIFIER)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_EXPERIMENT 
--

CREATE INDEX FK_R_CONTEXT_ITEM_EXPERIMENT ON RESULT_CONTEXT_ITEM(EXPERIMENT_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX FK_R_CONTEXT_ITEM_ATTRIBUTE ON RESULT_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_VALUE 
--

CREATE INDEX FK_R_CONTEXT_ITEM_VALUE ON RESULT_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX FK_R_CONTEXT_ITEM_QUALIFIER ON RESULT_CONTEXT_ITEM(QUALIFIER)
;
-- 
-- INDEX: FK_RESULT_CONTEXT_ITEM_RESULT 
--

CREATE INDEX FK_RESULT_CONTEXT_ITEM_RESULT ON RESULT_CONTEXT_ITEM(RESULT_ID)
;
-- 
-- INDEX: FK_RESULT_CONTEXT_ITEM_GROUP 
--

CREATE INDEX FK_RESULT_CONTEXT_ITEM_GROUP ON RESULT_CONTEXT_ITEM(GROUP_RESULT_CONTEXT_ID)
;
-- 
-- INDEX: FK_RESULT_HIERARCHY_RSLT_PRNT 
--

CREATE INDEX FK_RESULT_HIERARCHY_RSLT_PRNT ON RESULT_HIERARCHY(RESULT_ID)
;
-- 
-- INDEX: FK_RESULT_HIERARCHY_RESULT 
--

CREATE INDEX FK_RESULT_HIERARCHY_RESULT ON RESULT_HIERARCHY(PARENT_RESULT_ID)
;
-- 
-- INDEX: FK_RESULT_TYPE_PARENT_SELF 
--

CREATE INDEX FK_RESULT_TYPE_PARENT_SELF ON RESULT_TYPE(Parent_node_id)
;
-- 
-- INDEX: FK_RESULT_TYPE_STATUS 
--

CREATE INDEX FK_RESULT_TYPE_STATUS ON RESULT_TYPE(RESULT_TYPE_STATUS_ID)
;
-- 
-- INDEX: AK_TREE_ROOT_NAME 
--

CREATE UNIQUE INDEX AK_TREE_ROOT_NAME ON TREE_ROOT(TREE_NAME)
;
-- 
-- INDEX: FK_TREE_ROOT_ELEMENT 
--

CREATE INDEX FK_TREE_ROOT_ELEMENT ON TREE_ROOT(ELEMENT_ID)
;
-- 
-- INDEX: FK_UNIT_PARENT_SELF 
--

CREATE INDEX FK_UNIT_PARENT_SELF ON UNIT(PARENT_NODE_ID)
;
-- 
-- INDEX: FK_UNIT_CONVERSN_FRM_UNT_ELMNT 
--

CREATE INDEX FK_UNIT_CONVERSN_FRM_UNT_ELMNT ON UNIT_CONVERSION(FROM_UNIT)
;
-- 
-- INDEX: FK_UNIT_CONVERSN_TO_UNT_ELMNT 
--

CREATE INDEX FK_UNIT_CONVERSN_TO_UNT_ELMNT ON UNIT_CONVERSION(TO_UNIT)
;
-- 
-- TABLE: ELEMENT 
--

ALTER TABLE ELEMENT ADD 
    CONSTRAINT AK_ELEMENT_LABEL UNIQUE (LABEL)
    USING INDEX AK_ELEMENT_LABEL
;

-- 
-- TABLE: ASSAY 
--

ALTER TABLE ASSAY ADD CONSTRAINT FK_ASSAY_ASSAY_STATUS_ID 
    FOREIGN KEY (ASSAY_STATUS_ID)
    REFERENCES ASSAY_STATUS(ASSAY_STATUS_ID)
;


-- 
-- TABLE: ASSAY_DESCRIPTOR 
--

ALTER TABLE ASSAY_DESCRIPTOR ADD CONSTRAINT FK_ASSAY_DESCRIPTOR_PARENT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES ASSAY_DESCRIPTOR(NODE_ID)
;

ALTER TABLE ASSAY_DESCRIPTOR ADD CONSTRAINT FK_ASSAY_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: BIOLOGY_DESCRIPTOR 
--

ALTER TABLE BIOLOGY_DESCRIPTOR ADD CONSTRAINT FK_BIOLOGY_DESCRIPTOR_PRNT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BIOLOGY_DESCRIPTOR(NODE_ID)
;

ALTER TABLE BIOLOGY_DESCRIPTOR ADD CONSTRAINT FK_BIOLOGY_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: ELEMENT 
--

ALTER TABLE ELEMENT ADD CONSTRAINT FK_ELEMENT_ELEMENT_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE ELEMENT ADD CONSTRAINT FK_Element_Unit 
    FOREIGN KEY (UNIT)
    REFERENCES ELEMENT(LABEL)
;


-- 
-- TABLE: ELEMENT_HIERARCHY 
--

ALTER TABLE ELEMENT_HIERARCHY ADD CONSTRAINT FK_E_HIERARCHY_CHILD_ELEM_ID 
    FOREIGN KEY (CHILD_ELEMENT_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE ELEMENT_HIERARCHY ADD CONSTRAINT FK_E_HIERARCHY_PARENT_ELEM_ID 
    FOREIGN KEY (PARENT_ELEMENT_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: EXPERIMENT 
--

ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_EXPRT_STATUS 
    FOREIGN KEY (EXPERIMENT_STATUS_ID)
    REFERENCES EXPERIMENT_STATUS(EXPERIMENT_STATUS_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_PROJECT_EXPERIMENT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES PROJECT(PROJECT_ID)
;


-- 
-- TABLE: EXTERNAL_ASSAY 
--

ALTER TABLE EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;

ALTER TABLE EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_EXT_SYSTEM 
    FOREIGN KEY (EXTERNAL_SYSTEM_ID)
    REFERENCES EXTERNAL_SYSTEM(EXTERNAL_SYSTEM_ID)
;


-- 
-- TABLE: INSTANCE_DESCRIPTOR 
--

ALTER TABLE INSTANCE_DESCRIPTOR ADD CONSTRAINT FK_INSTANCE_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE INSTANCE_DESCRIPTOR ADD CONSTRAINT FK_INSTANCE_DESCRIPTR_PRNT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES INSTANCE_DESCRIPTOR(NODE_ID)
;


-- 
-- TABLE: MEASURE 
--

ALTER TABLE MEASURE ADD CONSTRAINT FK_Measure_Element_Unit 
    FOREIGN KEY (ENTRY_UNIT)
    REFERENCES ELEMENT(LABEL)
;

ALTER TABLE MEASURE ADD CONSTRAINT FK_MEASURE_M_CONTEXT 
    FOREIGN KEY (ASSAY_ID, MEASURE_CONTEXT_ID)
    REFERENCES MEASURE_CONTEXT(ASSAY_ID, MEASURE_CONTEXT_ID)
;

ALTER TABLE MEASURE ADD CONSTRAINT FK_MEASURE_RESULT_TYPE 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: MEASURE_CONTEXT 
--

ALTER TABLE MEASURE_CONTEXT ADD CONSTRAINT FK_MEASURE_CONTEXT_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;


-- 
-- TABLE: MEASURE_CONTEXT_ITEM 
--

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_M_CONTEXT 
    FOREIGN KEY (MEASURE_CONTEXT_ID)
    REFERENCES MEASURE_CONTEXT(MEASURE_CONTEXT_ID)
;

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES QUALIFIER(QUALIFIER)
;

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_MEASURE_CONTEXT_ITEM_GROUP 
    FOREIGN KEY (GROUP_MEASURE_CONTEXT_ITEM_ID)
    REFERENCES MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ITEM_ID)
;


-- 
-- TABLE: ONTOLOGY_ITEM 
--

ALTER TABLE ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ELEMENT 
    FOREIGN KEY (ELEMENT_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ONTOLOGY 
    FOREIGN KEY (ONTOLOGY_ID)
    REFERENCES ONTOLOGY(ONTOLOGY_ID)
;


-- 
-- TABLE: PROJECT_ASSAY 
--

ALTER TABLE PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;

ALTER TABLE PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_PROJECT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES PROJECT(PROJECT_ID)
;

ALTER TABLE PROJECT_ASSAY ADD CONSTRAINT FK_Project_Assay_Related_Assay 
    FOREIGN KEY (RELATED_ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;

ALTER TABLE PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_STAGE 
    FOREIGN KEY (STAGE)
    REFERENCES ELEMENT(LABEL)
;


-- 
-- TABLE: PROTOCOL 
--

ALTER TABLE PROTOCOL ADD CONSTRAINT FK_PROTOCOL_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES ASSAY(ASSAY_ID)
;


-- 
-- TABLE: RESULT 
--

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES QUALIFIER(QUALIFIER)
;

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_RESULT_STATUS 
    FOREIGN KEY (RESULT_STATUS_ID)
    REFERENCES RESULT_STATUS(RESULT_STATUS_ID)
;

ALTER TABLE RESULT ADD CONSTRAINT FK_Result_Result_Type 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_SUBSTANCE 
    FOREIGN KEY (SUBSTANCE_ID)
    REFERENCES SUBSTANCE(SUBSTANCE_ID)
;


-- 
-- TABLE: RESULT_CONTEXT_ITEM 
--

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_context_item_qualifier 
    FOREIGN KEY (QUALIFIER)
    REFERENCES QUALIFIER(QUALIFIER)
;

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_RESULT_CONTEXT_ITEM_GROUP 
    FOREIGN KEY (GROUP_RESULT_CONTEXT_ID)
    REFERENCES RESULT_CONTEXT_ITEM(RESULT_CONTEXT_ITEM_ID)
;

ALTER TABLE RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_RESULT_CONTEXT_ITEM_RESULT 
    FOREIGN KEY (RESULT_ID)
    REFERENCES RESULT(RESULT_ID)
;


-- 
-- TABLE: RESULT_HIERARCHY 
--

ALTER TABLE RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RESULT 
    FOREIGN KEY (PARENT_RESULT_ID)
    REFERENCES RESULT(RESULT_ID)
;

ALTER TABLE RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RSLT_PRNT 
    FOREIGN KEY (RESULT_ID)
    REFERENCES RESULT(RESULT_ID)
;


-- 
-- TABLE: RESULT_TYPE 
--

ALTER TABLE RESULT_TYPE ADD CONSTRAINT FK_Result_Type_Parent_Self 
    FOREIGN KEY (Parent_node_id)
    REFERENCES RESULT_TYPE(NODE_ID)
;

ALTER TABLE RESULT_TYPE ADD CONSTRAINT FK_Result_Type_status 
    FOREIGN KEY (RESULT_TYPE_STATUS_ID)
    REFERENCES ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: TREE_ROOT 
--

ALTER TABLE TREE_ROOT ADD CONSTRAINT FK_TREE_ROOT_ELEMENT 
    FOREIGN KEY (ELEMENT_ID)
    REFERENCES ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: UNIT 
--

ALTER TABLE UNIT ADD CONSTRAINT FK_UNIT_PARENT_SELF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES UNIT(NODE_ID)
;


-- 
-- TABLE: UNIT_CONVERSION 
--

ALTER TABLE UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSN_FRM_UNT_ELMNT 
    FOREIGN KEY (FROM_UNIT)
    REFERENCES ELEMENT(LABEL)
;

ALTER TABLE UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSN_TO_UNT_ELMNT 
    FOREIGN KEY (TO_UNIT)
    REFERENCES ELEMENT(LABEL)
;


