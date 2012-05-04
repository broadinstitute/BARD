--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Monday, April 30, 2012 18:59:56
-- Target DBMS : Oracle 11g
--

-- 
-- TABLE: BARD_QA.ASSAY 
--

CREATE TABLE BARD_QA.ASSAY(
    ASSAY_ID           NUMBER(19, 0)     NOT NULL,
    ASSAY_NAME         VARCHAR2(128)     NOT NULL,
    ASSAY_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    ASSAY_VERSION      VARCHAR2(10)      DEFAULT 1 NOT NULL,
    DESCRIPTION        VARCHAR2(1000),
    DESIGNED_BY        VARCHAR2(100),
    VERSION            NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED       TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED       TIMESTAMP(6),
    MODIFIED_BY        VARCHAR2(40),
    CONSTRAINT PK_ASSAY PRIMARY KEY (ASSAY_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.ASSAY_DESCRIPTOR 
--

CREATE TABLE BARD_QA.ASSAY_DESCRIPTOR(
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
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.ASSAY_STATUS 
--

CREATE TABLE BARD_QA.ASSAY_STATUS(
    ASSAY_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    STATUS             VARCHAR2(20)     NOT NULL,
    VERSION            NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED       TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED       TIMESTAMP(6),
    MODIFIED_BY        VARCHAR2(40),
    CONSTRAINT PK_ASSAY_STATUS PRIMARY KEY (ASSAY_STATUS_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ASSAY_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Assay_Status (assay_status_ID, status) values ('1', 'Pending');
insert into Assay_Status (assay_status_ID, status) values ('2', 'Active');
insert into Assay_Status (assay_status_ID, status) values ('3', 'Superceded');
insert into Assay_Status (assay_status_ID, status) values ('4', 'Retired');
commit;
-- 
-- TABLE: BARD_QA.BIOLOGY_DESCRIPTOR 
--

CREATE TABLE BARD_QA.BIOLOGY_DESCRIPTOR(
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
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.DATABASECHANGELOG 
--

CREATE TABLE BARD_QA.DATABASECHANGELOG(
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
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.DATABASECHANGELOGLOCK 
--

CREATE TABLE BARD_QA.DATABASECHANGELOGLOCK(
    ID             NUMBER           NOT NULL,
    LOCKED         NUMBER(1, 0)     NOT NULL,
    LOCKGRANTED    TIMESTAMP(6),
    LOCKEDBY       VARCHAR2(255),
    CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID)
)
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.ELEMENT 
--

CREATE TABLE BARD_QA.ELEMENT(
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
    DATE_CREATED         TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT PRIMARY KEY (ELEMENT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ELEMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.ELEMENT_HIERARCHY 
--

CREATE TABLE BARD_QA.ELEMENT_HIERARCHY(
    PARENT_ELEMENT_ID    NUMBER(19, 0),
    CHILD_ELEMENT_ID     NUMBER(19, 0)    NOT NULL,
    RELATIONSHIP_TYPE    VARCHAR2(40)     NOT NULL,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ELEMENT_HIERARCHY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.ELEMENT_STATUS 
--

CREATE TABLE BARD_QA.ELEMENT_STATUS(
    ELEMENT_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    ELEMENT_STATUS       VARCHAR2(20)     NOT NULL,
    CAPABILITY           VARCHAR2(256),
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_ELEMENT_STATUS PRIMARY KEY (ELEMENT_STATUS_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ELEMENT_STATUS.CAPABILITY IS 'Description of the actions allowed when elements are in this state'
;
COMMENT ON COLUMN BARD_QA.ELEMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
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
-- TABLE: BARD_QA.EXPERIMENT 
--

CREATE TABLE BARD_QA.EXPERIMENT(
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
    DATE_CREATED            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT PRIMARY KEY (EXPERIMENT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.EXPERIMENT.HOLD_UNTIL_DATE IS 'can only be set a max of 1 year in the future'
;
COMMENT ON COLUMN BARD_QA.EXPERIMENT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.EXPERIMENT_STATUS 
--

CREATE TABLE BARD_QA.EXPERIMENT_STATUS(
    EXPERIMENT_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    STATUS                  VARCHAR2(20)      NOT NULL,
    CAPABILITY              VARCHAR2(1000),
    VERSION                 NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED            TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED            TIMESTAMP(6),
    MODIFIED_BY             VARCHAR2(40),
    CONSTRAINT PK_EXPERIMENT_STATUS PRIMARY KEY (EXPERIMENT_STATUS_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.EXPERIMENT_STATUS.CAPABILITY IS 'describes the actions that can be done with this experiment status and the limitations (this is help text)'
;
COMMENT ON COLUMN BARD_QA.EXPERIMENT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('2', 'Approved', 'Experiment has been approved as ready to upload.  It does not mena results are correct or cannot be changed');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('3', 'Rejected', 'Experiment data has been rejected as not scientifically valid.  This will not be uploaded to the warehouse');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('4', 'Held', 'Experiment data is private to the loading institution (Source Laboratory).  The Hold Until Date is set.  Though uploaded it cannot be queried except by the source laboratory');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('5', 'Uploaded', 'Experiment has been copied into the warehouse and results are available for querying');
insert into Experiment_Status (Experiment_status_ID, status, Capability) values ('6', 'Mark for Deletion', 'Experiment has been confirmed as present in the warehouse and may be deleted at any time.');
commit;
-- 
-- TABLE: BARD_QA.EXTERNAL_ASSAY 
--

CREATE TABLE BARD_QA.EXTERNAL_ASSAY(
    EXTERNAL_SYSTEM_ID    NUMBER(19, 0)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    EXT_ASSAY_ID          VARCHAR2(128)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_EXTERNAL_ASSAY PRIMARY KEY (EXTERNAL_SYSTEM_ID, ASSAY_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.EXTERNAL_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.EXTERNAL_SYSTEM 
--

CREATE TABLE BARD_QA.EXTERNAL_SYSTEM(
    EXTERNAL_SYSTEM_ID    NUMBER(19, 0)     NOT NULL,
    SYSTEM_NAME           VARCHAR2(128)     NOT NULL,
    OWNER                 VARCHAR2(128),
    SYSTEM_URL            VARCHAR2(1000),
    VERSION               NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED          TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_EXTERNAL_SYSTEM PRIMARY KEY (EXTERNAL_SYSTEM_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.EXTERNAL_SYSTEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.INSTANCE_DESCRIPTOR 
--

CREATE TABLE BARD_QA.INSTANCE_DESCRIPTOR(
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
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.MEASURE 
--

CREATE TABLE BARD_QA.MEASURE(
    MEASURE_ID            NUMBER(19, 0)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    RESULT_TYPE_ID        NUMBER(19, 0)    NOT NULL,
    MEASURE_CONTEXT_ID    NUMBER(19, 0)    NOT NULL,
    ENTRY_UNIT            VARCHAR2(128),
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE PRIMARY KEY (MEASURE_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.MEASURE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.MEASURE_CONTEXT 
--

CREATE TABLE BARD_QA.MEASURE_CONTEXT(
    MEASURE_CONTEXT_ID    NUMBER(19, 0)    NOT NULL,
    CONTEXT_NAME          VARCHAR2(128)    NOT NULL,
    ASSAY_ID              NUMBER(19, 0)    NOT NULL,
    VERSION               NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED          TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED          TIMESTAMP(6),
    MODIFIED_BY           VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT PRIMARY KEY (MEASURE_CONTEXT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.MEASURE_CONTEXT.CONTEXT_NAME IS 'default name should be ''Context for '' || Result_Type_Name'
;
COMMENT ON COLUMN BARD_QA.MEASURE_CONTEXT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.MEASURE_CONTEXT_ITEM 
--

CREATE TABLE BARD_QA.MEASURE_CONTEXT_ITEM(
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
    DATE_CREATED                     TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED                     TIMESTAMP(6),
    MODIFIED_BY                      VARCHAR2(40),
    CONSTRAINT PK_MEASURE_CONTEXT_ITEM PRIMARY KEY (MEASURE_CONTEXT_ITEM_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.MEASURE_CONTEXT_ITEM.ASSAY_ID IS 'This column is useful for creating the assay defintion before the measures and their contexts have been properly grouped.'
;
COMMENT ON COLUMN BARD_QA.MEASURE_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN BARD_QA.MEASURE_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.ONTOLOGY 
--

CREATE TABLE BARD_QA.ONTOLOGY(
    ONTOLOGY_ID      NUMBER(19, 0)     NOT NULL,
    ONTOLOGY_NAME    VARCHAR2(256)     NOT NULL,
    ABBREVIATION     VARCHAR2(20),
    SYSTEM_URL       VARCHAR2(1000),
    VERSION          NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED     TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED     TIMESTAMP(6),
    MODIFIED_BY      VARCHAR2(40),
    CONSTRAINT PK_ONTOLOGY PRIMARY KEY (ONTOLOGY_ID)
)
TABLESPACE bard_dat
;



COMMENT ON TABLE BARD_QA.ONTOLOGY IS 'an external ontology or dictionary or other source of reference data'
;
COMMENT ON COLUMN BARD_QA.ONTOLOGY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.ONTOLOGY_ITEM 
--

CREATE TABLE BARD_QA.ONTOLOGY_ITEM(
    ONTOLOGY_ITEM_ID    NUMBER(19, 0)    NOT NULL,
    ONTOLOGY_ID         NUMBER(19, 0)    NOT NULL,
    ELEMENT_ID          NUMBER(19, 0),
    ITEM_REFERENCE      CHAR(10),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_ONTOLOGY_ITEM PRIMARY KEY (ONTOLOGY_ITEM_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.ONTOLOGY_ITEM.ITEM_REFERENCE IS 'Concatenate this with the Ontology.system_URL for a full URI for the item'
;
COMMENT ON COLUMN BARD_QA.ONTOLOGY_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.PROJECT 
--

CREATE TABLE BARD_QA.PROJECT(
    PROJECT_ID      NUMBER(19, 0)     NOT NULL,
    PROJECT_NAME    VARCHAR2(256)     NOT NULL,
    GROUP_TYPE      VARCHAR2(20)      DEFAULT 'Project' NOT NULL
                    CONSTRAINT CK_PROJECT_TYPE CHECK (GROUP_TYPE in ('Project', 'Campaign', 'Panel', 'Study')),
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_PROJECT PRIMARY KEY (PROJECT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.PROJECT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.PROJECT_ASSAY 
--

CREATE TABLE BARD_QA.PROJECT_ASSAY(
    PROJECT_ASSAY_ID       NUMBER(19, 0)     NOT NULL,
    PROJECT_ID             NUMBER(19, 0)     NOT NULL,
    ASSAY_ID               NUMBER(19, 0)     NOT NULL,
    STAGE                  VARCHAR2(128)     NOT NULL,
    RELATED_ASSAY_ID       NUMBER(19, 0),
    SEQUENCE_NO            NUMBER(38, 0),
    PROMOTION_THRESHOLD    FLOAT(126),
    PROMOTION_CRITERIA     VARCHAR2(1000),
    VERSION                NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED           TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED           TIMESTAMP(6),
    MODIFIED_BY            VARCHAR2(40),
    CONSTRAINT PK_PROJECT_ASSAY PRIMARY KEY (PROJECT_ASSAY_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.PROJECT_ASSAY.SEQUENCE_NO IS 'defines the promotion order (and often the running order) of a set of assays in a project'
;
COMMENT ON COLUMN BARD_QA.PROJECT_ASSAY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.PROTOCOL 
--

CREATE TABLE BARD_QA.PROTOCOL(
    PROTOCOL_ID          NUMBER(19, 0)    NOT NULL,
    ASSAY_ID             NUMBER(19, 0)    NOT NULL,
    PROTOCOL_NAME        VARCHAR2(500)    NOT NULL,
    PROTOCOL_DOCUMENT    LONG RAW,
    VERSION              NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED         TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED         TIMESTAMP(6),
    MODIFIED_BY          VARCHAR2(40),
    CONSTRAINT PK_PROTOCOL PRIMARY KEY (PROTOCOL_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.PROTOCOL.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.QUALIFIER 
--

CREATE TABLE BARD_QA.QUALIFIER(
    QUALIFIER       CHAR(2)           NOT NULL,
    DESCRIPTION     VARCHAR2(1000),
    VERSION         NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED    TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_QUALIFIER PRIMARY KEY (QUALIFIER)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.QUALIFIER.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into qualifier (qualifier, description) values ('=', 'equals');
insert into qualifier (qualifier, description) values ('>=', 'greater than or equal');
insert into qualifier (qualifier, description) values ('<=', 'less than or equal');
insert into qualifier (qualifier, description) values ('<', 'greater than');
insert into qualifier (qualifier, description) values ('>', 'less than');
insert into qualifier (qualifier, description) values ('~', 'approximatley');
insert into qualifier (qualifier, description) values ('>>', 'very much greater than');
insert into qualifier (qualifier, description) values ('<<', 'very much less than');
commit;
-- 
-- TABLE: BARD_QA.RESULT 
--

CREATE TABLE BARD_QA.RESULT(
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
    DATE_CREATED        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT PRIMARY KEY (RESULT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.RESULT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.RESULT_CONTEXT_ITEM 
--

CREATE TABLE BARD_QA.RESULT_CONTEXT_ITEM(
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
    DATE_CREATED               TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED               TIMESTAMP(6),
    MODIFIED_BY                VARCHAR2(40),
    CONSTRAINT PK_RESULT_CONTEXT_ITEM PRIMARY KEY (RESULT_CONTEXT_ITEM_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.RESULT_CONTEXT_ITEM.VALUE_DISPLAY IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
COMMENT ON COLUMN BARD_QA.RESULT_CONTEXT_ITEM.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.RESULT_HIERARCHY 
--

CREATE TABLE BARD_QA.RESULT_HIERARCHY(
    RESULT_ID           NUMBER(19, 0)    NOT NULL,
    PARENT_RESULT_ID    NUMBER(19, 0)    NOT NULL,
    HIERARCHY_TYPE      VARCHAR2(10)     NOT NULL
                        CONSTRAINT CK_RESULT_HIERARCHY_TYPE CHECK (HIERARCHY_TYPE in ('Child', 'Derives')),
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_HIERARCHY PRIMARY KEY (RESULT_ID, PARENT_RESULT_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.RESULT_HIERARCHY.HIERARCHY_TYPE IS 'two types of hierarchy are allowed: parent/child where one result is dependant on or grouped with another; derived from where aresult is used to claculate another (e.g. PI used for IC50).  The hierarchy types are mutually exclusive.'
;
COMMENT ON COLUMN BARD_QA.RESULT_HIERARCHY.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.RESULT_STATUS 
--

CREATE TABLE BARD_QA.RESULT_STATUS(
    RESULT_STATUS_ID    NUMBER(19, 0)    NOT NULL,
    STATUS              VARCHAR2(20)     NOT NULL,
    VERSION             NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED        TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_RESULT_STATUS PRIMARY KEY (RESULT_STATUS_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.RESULT_STATUS.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
insert into Result_status (result_status_id, status) values ('1', 'Pending');
insert into Result_status (result_status_id, status) values ('2', 'Approved');
insert into Result_status (result_status_id, status) values ('3', 'Rejected');
insert into Result_status (result_status_id, status) values ('4', 'Uploading');
insert into Result_status (result_status_id, status) values ('5', 'Uploaded');
insert into Result_status (result_status_id, status) values ('6', 'Mark for Deletion');
commit;
-- 
-- TABLE: BARD_QA.RESULT_TYPE 
--

CREATE TABLE BARD_QA.RESULT_TYPE(
    NODE_ID                  NUMBER(19, 0)     NOT NULL,
    PARENT_NODE_ID           NUMBER(19, 0),
    RESULT_TYPE_ID           NUMBER(19, 0)     NOT NULL,
    RESULT_TYPE_NAME         VARCHAR2(128)     NOT NULL,
    DESCRIPTION              VARCHAR2(1000),
    ABBREVIATION             VARCHAR2(20),
    SYNONYMS                 VARCHAR2(1000),
    BASE_UNIT                VARCHAR2(128),
    RESULT_TYPE_STATUS_ID    NUMBER(19, 0)     NOT NULL,
    CONSTRAINT PK_RESULT_TYPE PRIMARY KEY (NODE_ID)
)
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.SUBSTANCE 
--

CREATE TABLE BARD_QA.SUBSTANCE(
    SUBSTANCE_ID        NUMBER(19, 0)     NOT NULL,
    COMPOUND_ID         NUMBER(38, 0),
    SMILES              VARCHAR2(4000),
    MOLECULAR_WEIGHT    NUMBER(10, 3),
    SUBSTANCE_TYPE      VARCHAR2(20)      NOT NULL
                        CONSTRAINT CK_SUBSTANCE_TYPE CHECK (SUBSTANCE_TYPE in ('small molecule', 'protein', 'peptide', 'antibody', 'cell', 'oligonucleotide')),
    VERSION             NUMBER(38, 0)     DEFAULT 0 NOT NULL,
    DATE_CREATED        TIMESTAMP(6)      DEFAULT sysdate NOT NULL,
    LAST_UPDATED        TIMESTAMP(6),
    MODIFIED_BY         VARCHAR2(40),
    CONSTRAINT PK_SUBSTANCE PRIMARY KEY (SUBSTANCE_ID)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.SUBSTANCE.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- TABLE: BARD_QA.TREE_ROOT 
--

CREATE TABLE BARD_QA.TREE_ROOT(
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
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.TREE_ROOT.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
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
-- TABLE: BARD_QA.UNIT 
--

CREATE TABLE BARD_QA.UNIT(
    NODE_ID           NUMBER(19, 0)     NOT NULL,
    PARENT_NODE_ID    NUMBER(19, 0),
    UNIT_ID           NUMBER(19, 0)     NOT NULL,
    UNIT              VARCHAR2(128)     NOT NULL,
    DESCRIPTION       VARCHAR2(1000),
    CONSTRAINT PK_UNIT PRIMARY KEY (NODE_ID)
)
TABLESPACE bard_dat
;



-- 
-- TABLE: BARD_QA.UNIT_CONVERSION 
--

CREATE TABLE BARD_QA.UNIT_CONVERSION(
    FROM_UNIT       VARCHAR2(128)    NOT NULL,
    TO_UNIT         VARCHAR2(128)    NOT NULL,
    MULTIPLIER      FLOAT(126),
    OFFSET          FLOAT(126),
    FORMULA         VARCHAR2(256),
    VERSION         NUMBER(38, 0)    DEFAULT 0 NOT NULL,
    DATE_CREATED    TIMESTAMP(6)     DEFAULT sysdate NOT NULL,
    LAST_UPDATED    TIMESTAMP(6),
    MODIFIED_BY     VARCHAR2(40),
    CONSTRAINT PK_UNIT_CONVERSION PRIMARY KEY (FROM_UNIT, TO_UNIT)
)
TABLESPACE bard_dat
;



COMMENT ON COLUMN BARD_QA.UNIT_CONVERSION.VERSION IS 'Update_version is used by Hibernate to resolve the "lost Update" problem when used in optimistic locking'
;
-- 
-- INDEX: BARD_QA.FK_ASSAY_ASSAY_STATUS_ID 
--

CREATE INDEX BARD_QA.FK_ASSAY_ASSAY_STATUS_ID ON BARD_QA.ASSAY(ASSAY_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.FK_ASSAY_DESCRIPTOR_PARENT_SLF 
--

CREATE INDEX BARD_QA.FK_ASSAY_DESCRIPTOR_PARENT_SLF ON BARD_QA.ASSAY_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: BARD_QA.FK_ASSAY_DESCRIPTOR_STATUS 
--

CREATE INDEX BARD_QA.FK_ASSAY_DESCRIPTOR_STATUS ON BARD_QA.ASSAY_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.AK_ASSAY_STATUS 
--

CREATE UNIQUE INDEX BARD_QA.AK_ASSAY_STATUS ON BARD_QA.ASSAY_STATUS(STATUS)
;
-- 
-- INDEX: BARD_QA.FK_BIOLOGY_DESCRIPTOR_PRNT_SLF 
--

CREATE INDEX BARD_QA.FK_BIOLOGY_DESCRIPTOR_PRNT_SLF ON BARD_QA.BIOLOGY_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: BARD_QA.FK_BIOLOGY_DESCRIPTOR_STATUS 
--

CREATE INDEX BARD_QA.FK_BIOLOGY_DESCRIPTOR_STATUS ON BARD_QA.BIOLOGY_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.FK_ELEMENT_UNIT 
--

CREATE INDEX BARD_QA.FK_ELEMENT_UNIT ON BARD_QA.ELEMENT(UNIT)
;
-- 
-- INDEX: BARD_QA.FK_ELEMENT_ELEMENT_STATUS 
--

CREATE INDEX BARD_QA.FK_ELEMENT_ELEMENT_STATUS ON BARD_QA.ELEMENT(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.AK_ELEMENT_HIERARCHY 
--

CREATE UNIQUE INDEX BARD_QA.AK_ELEMENT_HIERARCHY ON BARD_QA.ELEMENT_HIERARCHY(CHILD_ELEMENT_ID, PARENT_ELEMENT_ID, RELATIONSHIP_TYPE)
;
-- 
-- INDEX: BARD_QA.FK_E_HIERARCHY_PARENT_ELEM_ID 
--

CREATE INDEX BARD_QA.FK_E_HIERARCHY_PARENT_ELEM_ID ON BARD_QA.ELEMENT_HIERARCHY(PARENT_ELEMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_E_HIERARCHY_CHILD_ELEM_ID 
--

CREATE INDEX BARD_QA.FK_E_HIERARCHY_CHILD_ELEM_ID ON BARD_QA.ELEMENT_HIERARCHY(CHILD_ELEMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_EXPERIMENT_ASSAY 
--

CREATE INDEX BARD_QA.FK_EXPERIMENT_ASSAY ON BARD_QA.EXPERIMENT(ASSAY_ID)
;
-- 
-- INDEX: BARD_QA.FK_PROJECT_EXPERIMENT 
--

CREATE INDEX BARD_QA.FK_PROJECT_EXPERIMENT ON BARD_QA.EXPERIMENT(PROJECT_ID)
;
-- 
-- INDEX: BARD_QA.FK_EXPERIMENT_EXPRT_STATUS 
--

CREATE INDEX BARD_QA.FK_EXPERIMENT_EXPRT_STATUS ON BARD_QA.EXPERIMENT(EXPERIMENT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.FK_EXT_ASSAY_ASSAY 
--

CREATE INDEX BARD_QA.FK_EXT_ASSAY_ASSAY ON BARD_QA.EXTERNAL_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: BARD_QA."FK_EXT_ASSAY_EXT_SYSTEM" 
--

CREATE INDEX BARD_QA."FK_EXT_ASSAY_EXT_SYSTEM" ON BARD_QA.EXTERNAL_ASSAY(EXTERNAL_SYSTEM_ID)
;
-- 
-- INDEX: BARD_QA.FK_INSTANCE_DESCRIPTR_PRNT_SLF 
--

CREATE INDEX BARD_QA.FK_INSTANCE_DESCRIPTR_PRNT_SLF ON BARD_QA.INSTANCE_DESCRIPTOR(PARENT_NODE_ID)
;
-- 
-- INDEX: BARD_QA.FK_INSTANCE_DESCRIPTOR_STATUS 
--

CREATE INDEX BARD_QA.FK_INSTANCE_DESCRIPTOR_STATUS ON BARD_QA.INSTANCE_DESCRIPTOR(ELEMENT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.FK_MEASURE_RESULT_TYPE 
--

CREATE INDEX BARD_QA.FK_MEASURE_RESULT_TYPE ON BARD_QA.MEASURE(RESULT_TYPE_ID)
;
-- 
-- INDEX: BARD_QA.FK_MEASURE_M_CONTEXT 
--

CREATE INDEX BARD_QA.FK_MEASURE_M_CONTEXT ON BARD_QA.MEASURE(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: BARD_QA.FK_MEASURE_ELEMENT_UNIT 
--

CREATE INDEX BARD_QA.FK_MEASURE_ELEMENT_UNIT ON BARD_QA.MEASURE(ENTRY_UNIT)
;
-- 
-- INDEX: BARD_QA.AK_MEASURE_CONTEXT_ITEM 
--

CREATE UNIQUE INDEX BARD_QA.AK_MEASURE_CONTEXT_ITEM ON BARD_QA.MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID, GROUP_MEASURE_CONTEXT_ITEM_ID, ATTRIBUTE_ID, VALUE_DISPLAY)
;
-- 
-- INDEX: BARD_QA.FK_M_CONTEXT_ITEM_M_CONTEXT 
--

CREATE INDEX BARD_QA.FK_M_CONTEXT_ITEM_M_CONTEXT ON BARD_QA.MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ID)
;
-- 
-- INDEX: BARD_QA.FK_M_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX BARD_QA.FK_M_CONTEXT_ITEM_ATTRIBUTE ON BARD_QA.MEASURE_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: BARD_QA.FK_M_CONTEXT_ITEM_VALUE 
--

CREATE INDEX BARD_QA.FK_M_CONTEXT_ITEM_VALUE ON BARD_QA.MEASURE_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: BARD_QA.FK_M_CONTEXT_ITEM_ASSAY 
--

CREATE INDEX BARD_QA.FK_M_CONTEXT_ITEM_ASSAY ON BARD_QA.MEASURE_CONTEXT_ITEM(ASSAY_ID)
;
-- 
-- INDEX: BARD_QA.FK_M_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX BARD_QA.FK_M_CONTEXT_ITEM_QUALIFIER ON BARD_QA.MEASURE_CONTEXT_ITEM(QUALIFIER)
;
-- 
-- INDEX: BARD_QA.FK_MEASURE_CONTEXT_ITEM_GROUP 
--

CREATE INDEX BARD_QA.FK_MEASURE_CONTEXT_ITEM_GROUP ON BARD_QA.MEASURE_CONTEXT_ITEM(GROUP_MEASURE_CONTEXT_ITEM_ID)
;
-- 
-- INDEX: BARD_QA.FK_ONTOLOGY_ITEM_ONTOLOGY 
--

CREATE INDEX BARD_QA.FK_ONTOLOGY_ITEM_ONTOLOGY ON BARD_QA.ONTOLOGY_ITEM(ONTOLOGY_ID)
;
-- 
-- INDEX: BARD_QA.FK_ONTOLOGY_ITEM_ELEMENT 
--

CREATE INDEX BARD_QA.FK_ONTOLOGY_ITEM_ELEMENT ON BARD_QA.ONTOLOGY_ITEM(ELEMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_PROJECT_ASSAY_RELATED_ASSAY 
--

CREATE INDEX BARD_QA.FK_PROJECT_ASSAY_RELATED_ASSAY ON BARD_QA.PROJECT_ASSAY(RELATED_ASSAY_ID)
;
-- 
-- INDEX: BARD_QA.FK_PROJECT_ASSAY_ASSAY 
--

CREATE INDEX BARD_QA.FK_PROJECT_ASSAY_ASSAY ON BARD_QA.PROJECT_ASSAY(ASSAY_ID)
;
-- 
-- INDEX: BARD_QA.FK_PROJECT_ASSAY_PROJECT 
--

CREATE INDEX BARD_QA.FK_PROJECT_ASSAY_PROJECT ON BARD_QA.PROJECT_ASSAY(PROJECT_ID)
;
-- 
-- INDEX: BARD_QA.FK_PROJECT_ASSAY_STAGE 
--

CREATE INDEX BARD_QA.FK_PROJECT_ASSAY_STAGE ON BARD_QA.PROJECT_ASSAY(STAGE)
;
-- 
-- INDEX: BARD_QA.FK_PROTOCOL_ASSAY 
--

CREATE INDEX BARD_QA.FK_PROTOCOL_ASSAY ON BARD_QA.PROTOCOL(ASSAY_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_RESULT_STATUS 
--

CREATE INDEX BARD_QA.FK_RESULT_RESULT_STATUS ON BARD_QA.RESULT(RESULT_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_EXPERIMENT 
--

CREATE INDEX BARD_QA.FK_RESULT_EXPERIMENT ON BARD_QA.RESULT(EXPERIMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_SUBSTANCE 
--

CREATE INDEX BARD_QA.FK_RESULT_SUBSTANCE ON BARD_QA.RESULT(SUBSTANCE_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_RESULT_TYPE 
--

CREATE INDEX BARD_QA.FK_RESULT_RESULT_TYPE ON BARD_QA.RESULT(RESULT_TYPE_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_QUALIFIER 
--

CREATE INDEX BARD_QA.FK_RESULT_QUALIFIER ON BARD_QA.RESULT(QUALIFIER)
;
-- 
-- INDEX: BARD_QA.FK_R_CONTEXT_ITEM_EXPERIMENT 
--

CREATE INDEX BARD_QA.FK_R_CONTEXT_ITEM_EXPERIMENT ON BARD_QA.RESULT_CONTEXT_ITEM(EXPERIMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_R_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX BARD_QA.FK_R_CONTEXT_ITEM_ATTRIBUTE ON BARD_QA.RESULT_CONTEXT_ITEM(ATTRIBUTE_ID)
;
-- 
-- INDEX: BARD_QA.FK_R_CONTEXT_ITEM_VALUE 
--

CREATE INDEX BARD_QA.FK_R_CONTEXT_ITEM_VALUE ON BARD_QA.RESULT_CONTEXT_ITEM(VALUE_ID)
;
-- 
-- INDEX: BARD_QA.FK_R_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX BARD_QA.FK_R_CONTEXT_ITEM_QUALIFIER ON BARD_QA.RESULT_CONTEXT_ITEM(QUALIFIER)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_CONTEXT_ITEM_RESULT 
--

CREATE INDEX BARD_QA.FK_RESULT_CONTEXT_ITEM_RESULT ON BARD_QA.RESULT_CONTEXT_ITEM(RESULT_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_CONTEXT_ITEM_GROUP 
--

CREATE INDEX BARD_QA.FK_RESULT_CONTEXT_ITEM_GROUP ON BARD_QA.RESULT_CONTEXT_ITEM(GROUP_RESULT_CONTEXT_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_HIERARCHY_RSLT_PRNT 
--

CREATE INDEX BARD_QA.FK_RESULT_HIERARCHY_RSLT_PRNT ON BARD_QA.RESULT_HIERARCHY(RESULT_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_HIERARCHY_RESULT 
--

CREATE INDEX BARD_QA.FK_RESULT_HIERARCHY_RESULT ON BARD_QA.RESULT_HIERARCHY(PARENT_RESULT_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_TYPE_PARENT_SELF 
--

CREATE INDEX BARD_QA.FK_RESULT_TYPE_PARENT_SELF ON BARD_QA.RESULT_TYPE(PARENT_NODE_ID)
;
-- 
-- INDEX: BARD_QA.FK_RESULT_TYPE_STATUS 
--

CREATE INDEX BARD_QA.FK_RESULT_TYPE_STATUS ON BARD_QA.RESULT_TYPE(RESULT_TYPE_STATUS_ID)
;
-- 
-- INDEX: BARD_QA.AK_TREE_ROOT_NAME 
--

CREATE UNIQUE INDEX BARD_QA.AK_TREE_ROOT_NAME ON BARD_QA.TREE_ROOT(TREE_NAME)
;
-- 
-- INDEX: BARD_QA.FK_TREE_ROOT_ELEMENT 
--

CREATE INDEX BARD_QA.FK_TREE_ROOT_ELEMENT ON BARD_QA.TREE_ROOT(ELEMENT_ID)
;
-- 
-- INDEX: BARD_QA.FK_UNIT_PARENT_SELF 
--

CREATE INDEX BARD_QA.FK_UNIT_PARENT_SELF ON BARD_QA.UNIT(PARENT_NODE_ID)
;
-- 
-- INDEX: BARD_QA.FK_UNIT_CONVERSN_FRM_UNT_ELMNT 
--

CREATE INDEX BARD_QA.FK_UNIT_CONVERSN_FRM_UNT_ELMNT ON BARD_QA.UNIT_CONVERSION(FROM_UNIT)
;
-- 
-- INDEX: BARD_QA.FK_UNIT_CONVERSN_TO_UNT_ELMNT 
--

CREATE INDEX BARD_QA.FK_UNIT_CONVERSN_TO_UNT_ELMNT ON BARD_QA.UNIT_CONVERSION(TO_UNIT)
;
-- 
-- TABLE: BARD_QA.ELEMENT 
--

ALTER TABLE BARD_QA.ELEMENT ADD 
    UNIQUE (LABEL)
;

-- 
-- TABLE: BARD_QA.MEASURE_CONTEXT 
--

ALTER TABLE BARD_QA.MEASURE_CONTEXT ADD 
    UNIQUE (ASSAY_ID, MEASURE_CONTEXT_ID)
;

-- 
-- TABLE: BARD_QA.ASSAY 
--

ALTER TABLE BARD_QA.ASSAY ADD CONSTRAINT FK_ASSAY_ASSAY_STATUS_ID 
    FOREIGN KEY (ASSAY_STATUS_ID)
    REFERENCES BARD_QA.ASSAY_STATUS(ASSAY_STATUS_ID)
;


-- 
-- TABLE: BARD_QA.ASSAY_DESCRIPTOR 
--

ALTER TABLE BARD_QA.ASSAY_DESCRIPTOR ADD CONSTRAINT FK_ASSAY_DESCRIPTOR_PARENT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BARD_QA.ASSAY_DESCRIPTOR(NODE_ID)
;

ALTER TABLE BARD_QA.ASSAY_DESCRIPTOR ADD CONSTRAINT FK_ASSAY_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES BARD_QA.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: BARD_QA.BIOLOGY_DESCRIPTOR 
--

ALTER TABLE BARD_QA.BIOLOGY_DESCRIPTOR ADD CONSTRAINT FK_BIOLOGY_DESCRIPTOR_PRNT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BARD_QA.BIOLOGY_DESCRIPTOR(NODE_ID)
;

ALTER TABLE BARD_QA.BIOLOGY_DESCRIPTOR ADD CONSTRAINT FK_BIOLOGY_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES BARD_QA.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: BARD_QA.ELEMENT 
--

ALTER TABLE BARD_QA.ELEMENT ADD CONSTRAINT FK_ELEMENT_ELEMENT_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES BARD_QA.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE BARD_QA.ELEMENT ADD CONSTRAINT FK_ELEMENT_UNIT 
    FOREIGN KEY (UNIT)
    REFERENCES BARD_QA.ELEMENT(LABEL)
;


-- 
-- TABLE: BARD_QA.ELEMENT_HIERARCHY 
--

ALTER TABLE BARD_QA.ELEMENT_HIERARCHY ADD CONSTRAINT FK_E_HIERARCHY_CHILD_ELEM_ID 
    FOREIGN KEY (CHILD_ELEMENT_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.ELEMENT_HIERARCHY ADD CONSTRAINT FK_E_HIERARCHY_PARENT_ELEM_ID 
    FOREIGN KEY (PARENT_ELEMENT_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: BARD_QA.EXPERIMENT 
--

ALTER TABLE BARD_QA.EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;

ALTER TABLE BARD_QA.EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_EXPRT_STATUS 
    FOREIGN KEY (EXPERIMENT_STATUS_ID)
    REFERENCES BARD_QA.EXPERIMENT_STATUS(EXPERIMENT_STATUS_ID)
;

ALTER TABLE BARD_QA.EXPERIMENT ADD CONSTRAINT FK_PROJECT_EXPERIMENT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES BARD_QA.PROJECT(PROJECT_ID)
;


-- 
-- TABLE: BARD_QA.EXTERNAL_ASSAY 
--

ALTER TABLE BARD_QA.EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;

ALTER TABLE BARD_QA.EXTERNAL_ASSAY ADD CONSTRAINT FK_EXT_ASSAY_EXT_SYSTEM 
    FOREIGN KEY (EXTERNAL_SYSTEM_ID)
    REFERENCES BARD_QA.EXTERNAL_SYSTEM(EXTERNAL_SYSTEM_ID)
;


-- 
-- TABLE: BARD_QA.INSTANCE_DESCRIPTOR 
--

ALTER TABLE BARD_QA.INSTANCE_DESCRIPTOR ADD CONSTRAINT FK_INSTANCE_DESCRIPTOR_STATUS 
    FOREIGN KEY (ELEMENT_STATUS_ID)
    REFERENCES BARD_QA.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;

ALTER TABLE BARD_QA.INSTANCE_DESCRIPTOR ADD CONSTRAINT FK_INSTANCE_DESCRIPTR_PRNT_SLF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BARD_QA.INSTANCE_DESCRIPTOR(NODE_ID)
;


-- 
-- TABLE: BARD_QA.MEASURE 
--

ALTER TABLE BARD_QA.MEASURE ADD CONSTRAINT FK_MEASURE_ELEMENT_UNIT 
    FOREIGN KEY (ENTRY_UNIT)
    REFERENCES BARD_QA.ELEMENT(LABEL)
;

ALTER TABLE BARD_QA.MEASURE ADD CONSTRAINT FK_MEASURE_M_CONTEXT 
    FOREIGN KEY (ASSAY_ID, MEASURE_CONTEXT_ID)
    REFERENCES BARD_QA.MEASURE_CONTEXT(ASSAY_ID, MEASURE_CONTEXT_ID)
;

ALTER TABLE BARD_QA.MEASURE ADD CONSTRAINT FK_MEASURE_RESULT_TYPE 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: BARD_QA.MEASURE_CONTEXT 
--

ALTER TABLE BARD_QA.MEASURE_CONTEXT ADD CONSTRAINT FK_MEASURE_CONTEXT_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;


-- 
-- TABLE: BARD_QA.MEASURE_CONTEXT_ITEM 
--

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_M_CONTEXT 
    FOREIGN KEY (MEASURE_CONTEXT_ID)
    REFERENCES BARD_QA.MEASURE_CONTEXT(MEASURE_CONTEXT_ID)
;

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES BARD_QA.QUALIFIER(QUALIFIER)
;

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_M_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.MEASURE_CONTEXT_ITEM ADD CONSTRAINT FK_MEASURE_CONTEXT_ITEM_GROUP 
    FOREIGN KEY (GROUP_MEASURE_CONTEXT_ITEM_ID)
    REFERENCES BARD_QA.MEASURE_CONTEXT_ITEM(MEASURE_CONTEXT_ITEM_ID)
;


-- 
-- TABLE: BARD_QA.ONTOLOGY_ITEM 
--

ALTER TABLE BARD_QA.ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ELEMENT 
    FOREIGN KEY (ELEMENT_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.ONTOLOGY_ITEM ADD CONSTRAINT FK_ONTOLOGY_ITEM_ONTOLOGY 
    FOREIGN KEY (ONTOLOGY_ID)
    REFERENCES BARD_QA.ONTOLOGY(ONTOLOGY_ID)
;


-- 
-- TABLE: BARD_QA.PROJECT_ASSAY 
--

ALTER TABLE BARD_QA.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;

ALTER TABLE BARD_QA.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_PROJECT 
    FOREIGN KEY (PROJECT_ID)
    REFERENCES BARD_QA.PROJECT(PROJECT_ID)
;

ALTER TABLE BARD_QA.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_RELATED_ASSAY 
    FOREIGN KEY (RELATED_ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;

ALTER TABLE BARD_QA.PROJECT_ASSAY ADD CONSTRAINT FK_PROJECT_ASSAY_STAGE 
    FOREIGN KEY (STAGE)
    REFERENCES BARD_QA.ELEMENT(LABEL)
;


-- 
-- TABLE: BARD_QA.PROTOCOL 
--

ALTER TABLE BARD_QA.PROTOCOL ADD CONSTRAINT FK_PROTOCOL_ASSAY 
    FOREIGN KEY (ASSAY_ID)
    REFERENCES BARD_QA.ASSAY(ASSAY_ID)
;


-- 
-- TABLE: BARD_QA.RESULT 
--

ALTER TABLE BARD_QA.RESULT ADD CONSTRAINT FK_RESULT_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES BARD_QA.EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE BARD_QA.RESULT ADD CONSTRAINT FK_RESULT_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES BARD_QA.QUALIFIER(QUALIFIER)
;

ALTER TABLE BARD_QA.RESULT ADD CONSTRAINT FK_RESULT_RESULT_STATUS 
    FOREIGN KEY (RESULT_STATUS_ID)
    REFERENCES BARD_QA.RESULT_STATUS(RESULT_STATUS_ID)
;

ALTER TABLE BARD_QA.RESULT ADD CONSTRAINT FK_Result_Result_Type 
    FOREIGN KEY (RESULT_TYPE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.RESULT ADD CONSTRAINT FK_RESULT_SUBSTANCE 
    FOREIGN KEY (SUBSTANCE_ID)
    REFERENCES BARD_QA.SUBSTANCE(SUBSTANCE_ID)
;


-- 
-- TABLE: BARD_QA.RESULT_CONTEXT_ITEM 
--

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_ATTRIBUTE 
    FOREIGN KEY (ATTRIBUTE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_EXPERIMENT 
    FOREIGN KEY (EXPERIMENT_ID)
    REFERENCES BARD_QA.EXPERIMENT(EXPERIMENT_ID)
;

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_QUALIFIER 
    FOREIGN KEY (QUALIFIER)
    REFERENCES BARD_QA.QUALIFIER(QUALIFIER)
;

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_R_CONTEXT_ITEM_VALUE 
    FOREIGN KEY (VALUE_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_RESULT_CONTEXT_ITEM_GROUP 
    FOREIGN KEY (GROUP_RESULT_CONTEXT_ID)
    REFERENCES BARD_QA.RESULT_CONTEXT_ITEM(RESULT_CONTEXT_ITEM_ID)
;

ALTER TABLE BARD_QA.RESULT_CONTEXT_ITEM ADD CONSTRAINT FK_RESULT_CONTEXT_ITEM_RESULT 
    FOREIGN KEY (RESULT_ID)
    REFERENCES BARD_QA.RESULT(RESULT_ID)
;


-- 
-- TABLE: BARD_QA.RESULT_HIERARCHY 
--

ALTER TABLE BARD_QA.RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RESULT 
    FOREIGN KEY (PARENT_RESULT_ID)
    REFERENCES BARD_QA.RESULT(RESULT_ID)
;

ALTER TABLE BARD_QA.RESULT_HIERARCHY ADD CONSTRAINT FK_RESULT_HIERARCHY_RSLT_PRNT 
    FOREIGN KEY (RESULT_ID)
    REFERENCES BARD_QA.RESULT(RESULT_ID)
;


-- 
-- TABLE: BARD_QA.RESULT_TYPE 
--

ALTER TABLE BARD_QA.RESULT_TYPE ADD CONSTRAINT FK_RESULT_TYPE_PARENT_SELF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BARD_QA.RESULT_TYPE(NODE_ID)
;

ALTER TABLE BARD_QA.RESULT_TYPE ADD CONSTRAINT FK_RESULT_TYPE_STATUS 
    FOREIGN KEY (RESULT_TYPE_STATUS_ID)
    REFERENCES BARD_QA.ELEMENT_STATUS(ELEMENT_STATUS_ID)
;


-- 
-- TABLE: BARD_QA.TREE_ROOT 
--

ALTER TABLE BARD_QA.TREE_ROOT ADD CONSTRAINT FK_TREE_ROOT_ELEMENT 
    FOREIGN KEY (ELEMENT_ID)
    REFERENCES BARD_QA.ELEMENT(ELEMENT_ID)
;


-- 
-- TABLE: BARD_QA.UNIT 
--

ALTER TABLE BARD_QA.UNIT ADD CONSTRAINT FK_UNIT_PARENT_SELF 
    FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES BARD_QA.UNIT(NODE_ID)
;


-- 
-- TABLE: BARD_QA.UNIT_CONVERSION 
--

ALTER TABLE BARD_QA.UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSN_FRM_UNT_ELMNT 
    FOREIGN KEY (FROM_UNIT)
    REFERENCES BARD_QA.ELEMENT(LABEL)
;

ALTER TABLE BARD_QA.UNIT_CONVERSION ADD CONSTRAINT FK_UNIT_CONVERSN_TO_UNT_ELMNT 
    FOREIGN KEY (TO_UNIT)
    REFERENCES BARD_QA.ELEMENT(LABEL)
;


