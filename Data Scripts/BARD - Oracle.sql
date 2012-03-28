--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Thursday, March 22, 2012 17:58:44
-- Target DBMS : Oracle 10g
--

-- 
-- USER: BARD 
--

CREATE USER BARD IDENTIFIED BY VALUES 'BARD'
;

GRANT DBA TO BARD
;
GRANT connect TO BARD
;

-- 
-- USER: MLBD 
--

CREATE USER MLBD IDENTIFIED BY VALUES 'MLBD'
;

GRANT DBA TO MLBD
;
GRANT connect TO MLBD
;

-- 
-- SEQUENCE: MLBD.Assay_ID 
--

CREATE SEQUENCE MLBD.ASSAY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.Assay_ID TO BARD
;
GRANT SELECT ON MLBD.Assay_ID TO BARD
;


-- 
-- SEQUENCE: MLBD.Assay_status_ID 
--

CREATE SEQUENCE MLBD.ASSAY_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.Assay_status_ID TO BARD
;
GRANT SELECT ON MLBD.Assay_status_ID TO BARD
;


-- 
-- SEQUENCE: MLBD.element_id 
--

CREATE SEQUENCE MLBD.ELEMENT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.element_id TO BARD
;
GRANT SELECT ON MLBD.element_id TO BARD
;


-- 
-- SEQUENCE: MLBD.element_status_id 
--

CREATE SEQUENCE MLBD.ELEMENT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.element_status_id TO BARD
;
GRANT SELECT ON MLBD.element_status_id TO BARD
;


-- 
-- SEQUENCE: MLBD.experiment_id 
--

CREATE SEQUENCE MLBD.EXPERIMENT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.experiment_id TO BARD
;
GRANT SELECT ON MLBD.experiment_id TO BARD
;


-- 
-- SEQUENCE: MLBD.experiment_status_id 
--

CREATE SEQUENCE MLBD.EXPERIMENT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.experiment_status_id TO BARD
;
GRANT SELECT ON MLBD.experiment_status_id TO BARD
;


-- 
-- SEQUENCE: MLBD.external_system_id 
--

CREATE SEQUENCE MLBD.EXTERNAL_SYSTEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.external_system_id TO BARD
;
GRANT SELECT ON MLBD.external_system_id TO BARD
;


-- 
-- SEQUENCE: MLBD.laboratory_id 
--

CREATE SEQUENCE MLBD.LABORATORY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.laboratory_id TO BARD
;
GRANT SELECT ON MLBD.laboratory_id TO BARD
;


-- 
-- SEQUENCE: MLBD.measure_context_id 
--

CREATE SEQUENCE MLBD.MEASURE_CONTEXT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.measure_context_id TO BARD
;
GRANT SELECT ON MLBD.measure_context_id TO BARD
;


-- 
-- SEQUENCE: MLBD.measure_context_item_ID 
--

CREATE SEQUENCE MLBD.MEASURE_CONTEXT_ITEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.measure_context_item_ID TO BARD
;
GRANT SELECT ON MLBD.measure_context_item_ID TO BARD
;


-- 
-- SEQUENCE: MLBD.measure_id 
--

CREATE SEQUENCE MLBD.MEASURE_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.measure_id TO BARD
;
GRANT SELECT ON MLBD.measure_id TO BARD
;


-- 
-- SEQUENCE: MLBD.ontology_id 
--

CREATE SEQUENCE MLBD.ONTOLOGY_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.ontology_id TO BARD
;
GRANT SELECT ON MLBD.ontology_id TO BARD
;


-- 
-- SEQUENCE: MLBD.ontology_item_id 
--

CREATE SEQUENCE MLBD.ONTOLOGY_ITEM_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.ontology_item_id TO BARD
;
GRANT SELECT ON MLBD.ontology_item_id TO BARD
;


-- 
-- SEQUENCE: MLBD.project_id 
--

CREATE SEQUENCE MLBD.PROJECT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.project_id TO BARD
;
GRANT SELECT ON MLBD.project_id TO BARD
;


-- 
-- SEQUENCE: MLBD.protocol_id 
--

CREATE SEQUENCE MLBD.PROTOCOL_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.protocol_id TO BARD
;
GRANT SELECT ON MLBD.protocol_id TO BARD
;


-- 
-- SEQUENCE: MLBD.result_context_ID 
--

CREATE SEQUENCE MLBD.RESULT_CONTEXT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.result_context_ID TO BARD
;
GRANT SELECT ON MLBD.result_context_ID TO BARD
;


-- 
-- SEQUENCE: MLBD.result_id 
--

CREATE SEQUENCE MLBD.RESULT_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.result_id TO BARD
;
GRANT SELECT ON MLBD.result_id TO BARD
;


-- 
-- SEQUENCE: MLBD.result_status_id 
--

CREATE SEQUENCE MLBD.RESULT_STATUS_ID
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.result_status_id TO BARD
;
GRANT SELECT ON MLBD.result_status_id TO BARD
;


-- 
-- SEQUENCE: MLBD.result_type_id 
--

CREATE SEQUENCE MLBD.result_type_id
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 2
    NOORDER
;
GRANT ALTER ON MLBD.result_type_id TO BARD
;
GRANT SELECT ON MLBD.result_type_id TO BARD
;


-- 
-- TABLE: MLBD.Assay 
--

CREATE TABLE MLBD.Assay(
    Assay_ID           NUMBER(10, 0)     NOT NULL,
    Assay_Name         VARCHAR2(128)     NOT NULL,
    Assay_status_ID    NUMBER(10, 0)     NOT NULL,
    Version            VARCHAR2(10)      DEFAULT 1 NOT NULL,
    Description        VARCHAR2(1000),
    Designed_By        VARCHAR2(100),
    CONSTRAINT PK_Assay PRIMARY KEY (Assay_ID)
)
;



GRANT INSERT ON MLBD.Assay TO BARD
;
GRANT SELECT ON MLBD.Assay TO BARD
;
GRANT UPDATE ON MLBD.Assay TO BARD
;
GRANT DELETE ON MLBD.Assay TO BARD
;

-- 
-- TABLE: MLBD.Assay_Status 
--

CREATE TABLE MLBD.Assay_Status(
    Assay_status_ID    NUMBER(10, 0)    NOT NULL,
    Status             VARCHAR2(20)     NOT NULL,
    CONSTRAINT PK_Assay_Status PRIMARY KEY (Assay_status_ID)
)
;



GRANT DELETE ON MLBD.Assay_Status TO BARD
;
GRANT INSERT ON MLBD.Assay_Status TO BARD
;
GRANT SELECT ON MLBD.Assay_Status TO BARD
;
GRANT UPDATE ON MLBD.Assay_Status TO BARD
;

-- 
-- TABLE: MLBD.Attribute_Type 
--

CREATE TABLE MLBD.ATTRIBUTE_TYPE(
    ATTRIBUTE_TYPE    VARCHAR2(20)      NOT NULL,
    CAPABILITIES      VARCHAR2(1000),
    CONSTRAINT PK_Attribute_type PRIMARY KEY (ATTRIBUTE_TYPE)
)
;

GRANT DELETE ON MLBD.Attribute_Type TO BARD
;
GRANT INSERT ON MLBD.Attribute_Type TO BARD
;
GRANT SELECT ON MLBD.Attribute_Type TO BARD
;
GRANT UPDATE ON MLBD.Attribute_Type TO BARD
;

-- 
-- TABLE: MLBD.Element 
--

CREATE TABLE MLBD.Element(
    Element_ID           NUMBER(10, 0)     NOT NULL,
    Parent_Element_ID    NUMBER(10, 0),
    Label                VARCHAR2(128)     NOT NULL,
    Description          VARCHAR2(1000)    NOT NULL,
    Abbreviation         VARCHAR2(20),
    Acronym              VARCHAR2(20),
    Synonyms             VARCHAR2(1000),
    Element_Status_ID    NUMBER(10, 0)     NOT NULL,
    Unit                 VARCHAR2(100),
    CONSTRAINT PK_Element PRIMARY KEY (Element_ID)
)
;



GRANT DELETE ON MLBD.Element TO BARD
;
GRANT INSERT ON MLBD.Element TO BARD
;
GRANT SELECT ON MLBD.Element TO BARD
;
GRANT UPDATE ON MLBD.Element TO BARD
;

-- 
-- TABLE: MLBD.Element_Status 
--

CREATE TABLE MLBD.Element_Status(
    Element_Status_ID    NUMBER(10, 0)    NOT NULL,
    Element_Status       VARCHAR2(20)     NOT NULL,
    Capability           VARCHAR2(256),
    CONSTRAINT PK_Element_Status PRIMARY KEY (Element_Status_ID)
)
;



COMMENT ON COLUMN MLBD.Element_Status.Capability IS 'Description of the actions allowed when elements are in this state'
;
GRANT DELETE ON MLBD.Element_Status TO BARD
;
GRANT INSERT ON MLBD.Element_Status TO BARD
;
GRANT SELECT ON MLBD.Element_Status TO BARD
;
GRANT UPDATE ON MLBD.Element_Status TO BARD
;

-- 
-- TABLE: MLBD.Experiment 
--

CREATE TABLE MLBD.Experiment(
    Experiment_ID           NUMBER(10, 0)     NOT NULL,
    Experiment_Name         VARCHAR2(256)     NOT NULL,
    Assay_ID                NUMBER(10, 0)     NOT NULL,
    Project_ID              NUMBER(10, 0),
    Experiment_Status_ID    NUMBER(10, 0)     NOT NULL,
    Run_Date                TIMESTAMP(6),
    Hold_Until_Date         DATE              
                            CONSTRAINT CK_HOLD_UNTIL_DATE CHECK (Hold_Until_Date <= sysdate + 366),
    Description             VARCHAR2(1000)    NOT NULL,
    Source_ID               NUMBER(10, 0)     NOT NULL,
    CONSTRAINT PK_Experiment PRIMARY KEY (Experiment_ID)
)
;



COMMENT ON COLUMN MLBD.Experiment.Hold_Until_Date IS 'can only be set a max of 1 year in the future'
;
GRANT DELETE ON MLBD.Experiment TO BARD
;
GRANT INSERT ON MLBD.Experiment TO BARD
;
GRANT SELECT ON MLBD.Experiment TO BARD
;
GRANT UPDATE ON MLBD.Experiment TO BARD
;

-- 
-- TABLE: MLBD.Experiment_Status 
--

CREATE TABLE MLBD.Experiment_Status(
    Experiment_Status_ID    NUMBER(10, 0)     NOT NULL,
    Status                  VARCHAR2(20)      NOT NULL,
    Capability              VARCHAR2(1000),
    CONSTRAINT PK_Experiment_Status PRIMARY KEY (Experiment_Status_ID)
)
;



COMMENT ON COLUMN MLBD.Experiment_Status.Capability IS 'describes the actions that can be done with this experiment status and the limitations (this is help text)'
;
GRANT DELETE ON MLBD.Experiment_Status TO BARD
;
GRANT INSERT ON MLBD.Experiment_Status TO BARD
;
GRANT SELECT ON MLBD.Experiment_Status TO BARD
;
GRANT UPDATE ON MLBD.Experiment_Status TO BARD
;

-- 
-- TABLE: MLBD.External_Assay 
--

CREATE TABLE MLBD.External_Assay(
    External_System_ID    NUMBER(10, 0)    NOT NULL,
    Assay_ID              NUMBER(10, 0)    NOT NULL,
    Ext_Assay_ID          VARCHAR2(128)    NOT NULL,
    CONSTRAINT PK_External_Assay PRIMARY KEY (External_System_ID, Assay_ID)
)
;



GRANT DELETE ON MLBD.External_Assay TO BARD
;
GRANT INSERT ON MLBD.External_Assay TO BARD
;
GRANT SELECT ON MLBD.External_Assay TO BARD
;
GRANT UPDATE ON MLBD.External_Assay TO BARD
;

-- 
-- TABLE: MLBD.External_System 
--

CREATE TABLE MLBD.External_System(
    External_System_ID    NUMBER(10, 0)     NOT NULL,
    System_Name           VARCHAR2(128)     NOT NULL,
    Owner                 VARCHAR2(128),
    System_URL            VARCHAR2(1000),
    CONSTRAINT PK_External_System PRIMARY KEY (External_System_ID)
)
;



GRANT DELETE ON MLBD.External_System TO BARD
;
GRANT INSERT ON MLBD.External_System TO BARD
;
GRANT SELECT ON MLBD.External_System TO BARD
;
GRANT UPDATE ON MLBD.External_System TO BARD
;

-- 
-- TABLE: MLBD.Laboratory 
--

CREATE TABLE MLBD.Laboratory(
    Lab_ID          NUMBER(10, 0)     NOT NULL,
    Lab_Name        VARCHAR2(125)     NOT NULL,
    Abbreviation    VARCHAR2(20),
    Description     VARCHAR2(1000)    NOT NULL,
    Location        VARCHAR2(250),
    CONSTRAINT PK_Laboratory PRIMARY KEY (Lab_ID)
)
;



COMMENT ON COLUMN MLBD.Laboratory.Location IS 'Address or other location (website?) for the lab'
;
GRANT DELETE ON MLBD.Laboratory TO BARD
;
GRANT INSERT ON MLBD.Laboratory TO BARD
;
GRANT SELECT ON MLBD.Laboratory TO BARD
;
GRANT UPDATE ON MLBD.Laboratory TO BARD
;

-- 
-- TABLE: MLBD.Measure 
--

CREATE TABLE MLBD.Measure(
    Measure_ID            NUMBER(10, 0)    NOT NULL,
    Assay_ID              NUMBER(10, 0)    NOT NULL,
    Result_Type_ID        NUMBER(10, 0)    NOT NULL,
    Entry_Unit            VARCHAR2(100),
    Measure_Context_ID    NUMBER(10, 0),
    CONSTRAINT PK_Measure PRIMARY KEY (Measure_ID)
)
;



GRANT DELETE ON MLBD.Measure TO BARD
;
GRANT INSERT ON MLBD.Measure TO BARD
;
GRANT SELECT ON MLBD.Measure TO BARD
;
GRANT UPDATE ON MLBD.Measure TO BARD
;

-- 
-- TABLE: MLBD.Measure_Context 
--

CREATE TABLE MLBD.Measure_Context(
    Measure_Context_ID    NUMBER(10, 0)    NOT NULL,
    Context_Name          VARCHAR2(128)    NOT NULL,
    CONSTRAINT PK_Measure_Context PRIMARY KEY (Measure_Context_ID)
)
;



COMMENT ON COLUMN MLBD.Measure_Context.Context_Name IS 'default name should be Assay.Name || (select count(*) + 1 from measure_context where assay_ID = assay.assay_ID)'
;
GRANT DELETE ON MLBD.Measure_Context TO BARD
;
GRANT INSERT ON MLBD.Measure_Context TO BARD
;
GRANT SELECT ON MLBD.Measure_Context TO BARD
;
GRANT UPDATE ON MLBD.Measure_Context TO BARD
;

-- 
-- TABLE: MLBD.Measure_Context_Item 
--

CREATE TABLE MLBD.Measure_Context_Item(
    measure_Context_Item_ID    NUMBER(10, 0)    NOT NULL,
    Assay_ID                   NUMBER(10, 0)    NOT NULL,
    Measure_Context_ID         NUMBER(10, 0)    NOT NULL,
    Group_No                   NUMBER(10, 0),
    Attribute_Type             VARCHAR2(10)     
                               CONSTRAINT CK_CONTEXT_ITEM_TYPE CHECK (Attribute_Type in ('Fixed', 'Range', 'List', 'Number')),
    Attribute_ID               NUMBER(10, 0)    NOT NULL,
    Value_ID                   NUMBER(10, 0),
    Qualifier                  CHAR(2)          
                               CONSTRAINT CK_QUALIFIER CHECK (Qualifier in ('=', '>', '<', '<=', '>=', '~')),
    Value_Display              VARCHAR2(256),
    Value_num                  BINARY_FLOAT,
    Value_Min                  BINARY_FLOAT,
    value_Max                  BINARY_FLOAT,
    CONSTRAINT PK_Measure_context_item PRIMARY KEY (measure_Context_Item_ID)
)
;



COMMENT ON COLUMN MLBD.Measure_Context_Item.Assay_ID IS 'This column is useful for creating the assay defintion before the measures and their contexts have been properly grouped.'
;
COMMENT ON COLUMN MLBD.Measure_Context_Item.Group_No IS 'rows with the same group_no in the measure context combine to a single group for UI purposes'
;
COMMENT ON COLUMN MLBD.Measure_Context_Item.Attribute_Type IS 'Describes how the application handles the rows.  Fixed = not needed for experiment result entry, all others are.  Range means the experimental value must lie in the min/max range, List means the value_ID must come from the dictionary and be one of the value_ID members, Number means a simple number is expected.  Free text entry is not permitted.'
;
COMMENT ON COLUMN MLBD.Measure_Context_Item.Value_Display IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
GRANT DELETE ON MLBD.Measure_Context_Item TO BARD
;
GRANT INSERT ON MLBD.Measure_Context_Item TO BARD
;
GRANT SELECT ON MLBD.Measure_Context_Item TO BARD
;
GRANT UPDATE ON MLBD.Measure_Context_Item TO BARD
;

-- 
-- TABLE: MLBD.Ontology 
--

CREATE TABLE MLBD.Ontology(
    Ontology_ID      NUMBER(10, 0)     NOT NULL,
    Ontology_Name    VARCHAR2(256)     NOT NULL,
    Abbreviation     VARCHAR2(20),
    System_URL       VARCHAR2(1000),
    CONSTRAINT PK_Ontology PRIMARY KEY (Ontology_ID)
)
;



COMMENT ON TABLE MLBD.Ontology IS 'an external ontology or dictionary or other source of reference data'
;
GRANT DELETE ON MLBD.Ontology TO BARD
;
GRANT INSERT ON MLBD.Ontology TO BARD
;
GRANT SELECT ON MLBD.Ontology TO BARD
;
GRANT UPDATE ON MLBD.Ontology TO BARD
;

-- 
-- TABLE: MLBD.Ontology_Item 
--

CREATE TABLE MLBD.Ontology_Item(
    Ontology_Item_ID    NUMBER(10, 0)    NOT NULL,
    Ontology_ID         NUMBER(10, 0)    NOT NULL,
    Element_ID          NUMBER(10, 0),
    Item_Reference      CHAR(10),
    Result_Type_ID      NUMBER(10, 0),
    CONSTRAINT PK_Ontology_item PRIMARY KEY (Ontology_Item_ID)
)
;



COMMENT ON COLUMN MLBD.Ontology_Item.Item_Reference IS 'Concatenate this with the Ontology.system_URL for a full URI for the item'
;
GRANT DELETE ON MLBD.Ontology_Item TO BARD
;
GRANT INSERT ON MLBD.Ontology_Item TO BARD
;
GRANT SELECT ON MLBD.Ontology_Item TO BARD
;
GRANT UPDATE ON MLBD.Ontology_Item TO BARD
;

-- 
-- TABLE: MLBD.Project 
--

CREATE TABLE MLBD.Project(
    Project_ID      NUMBER(10, 0)     NOT NULL,
    Project_Name    VARCHAR2(256)     NOT NULL,
    Group_Type      VARCHAR2(20)      DEFAULT 'Project' NOT NULL
                    CONSTRAINT CK_PROJECT_TYPE CHECK (Group_Type in ('Project', 'Campaign', 'Panel', 'Study')),
    Description     VARCHAR2(1000)    NOT NULL,
    CONSTRAINT PK_Project PRIMARY KEY (Project_ID)
)
;



GRANT DELETE ON MLBD.Project TO BARD
;
GRANT INSERT ON MLBD.Project TO BARD
;
GRANT SELECT ON MLBD.Project TO BARD
;
GRANT UPDATE ON MLBD.Project TO BARD
;

-- 
-- TABLE: MLBD.Project_Assay 
--

CREATE TABLE MLBD.Project_Assay(
    Assay_ID               NUMBER(10, 0)     NOT NULL,
    Project_ID             NUMBER(10, 0)     NOT NULL,
    Stage                  VARCHAR2(20)      NOT NULL,
    Sequence_no            NUMBER(10, 0),
    Promotion_Threshold    BINARY_FLOAT,
    Promotion_Criteria     VARCHAR2(1000),
    CONSTRAINT PK_Project_Assay PRIMARY KEY (Assay_ID, Project_ID)
)
;



COMMENT ON COLUMN MLBD.Project_Assay.Sequence_no IS 'defines the promotion order (and often the running order) of a set of assays in a project'
;
GRANT DELETE ON MLBD.Project_Assay TO BARD
;
GRANT INSERT ON MLBD.Project_Assay TO BARD
;
GRANT SELECT ON MLBD.Project_Assay TO BARD
;
GRANT UPDATE ON MLBD.Project_Assay TO BARD
;

-- 
-- TABLE: MLBD.Protocol 
--

CREATE TABLE MLBD.Protocol(
    Protocol_ID          NUMBER(10, 0)    NOT NULL,
    Protocol_Name        VARCHAR2(500)    NOT NULL,
    Protocol_Document    LONG RAW,
    Assay_ID             NUMBER(10, 0)    NOT NULL,
    CONSTRAINT PK_Protocol PRIMARY KEY (Protocol_ID)
)
;



GRANT DELETE ON MLBD.Protocol TO BARD
;
GRANT INSERT ON MLBD.Protocol TO BARD
;
GRANT SELECT ON MLBD.Protocol TO BARD
;
GRANT UPDATE ON MLBD.Protocol TO BARD
;

-- 
-- TABLE: MLBD.Result 
--

CREATE TABLE MLBD.Result(
    Result_ID            NUMBER(10, 0)    NOT NULL,
    Value_Display        VARCHAR2(256),
    value_num            BINARY_FLOAT,
    value_min            BINARY_FLOAT,
    Value_Max            BINARY_FLOAT,
    Qualifier            CHAR(2)          
                         CONSTRAINT CK_RESULT_QUALIFIER CHECK (Qualifier in ('=', '<', '>', '<=', '>=', '~')),
    Result_Status_ID     NUMBER(10, 0)    NOT NULL,
    Experiment_ID        NUMBER(10, 0)    NOT NULL,
    Substance_ID         NUMBER(10, 0)    NOT NULL,
    Result_Context_ID    NUMBER(10, 0)    NOT NULL,
    Entry_Unit           VARCHAR2(100),
    Result_Type_ID       NUMBER(10, 0)    NOT NULL,
    CONSTRAINT PK_Result PRIMARY KEY (Result_ID)
)
;



COMMENT ON COLUMN MLBD.Result.Entry_Unit IS 'This is the units that were used in the UI when uploading the data.  The numerical values are stored in base_Unit and NOT in this unit to make future calculaton easier.  This Entry_Unit is recorded to allow accurate re-display of entered results and for data lineage reasons'
;
GRANT DELETE ON MLBD.Result TO BARD
;
GRANT INSERT ON MLBD.Result TO BARD
;
GRANT SELECT ON MLBD.Result TO BARD
;
GRANT UPDATE ON MLBD.Result TO BARD
;

-- 
-- TABLE: MLBD.Result_Context 
--

CREATE TABLE MLBD.Result_Context(
    Result_Context_ID    NUMBER(10, 0)    NOT NULL,
    Context_Name         VARCHAR2(125),
    CONSTRAINT PK_result_Context PRIMARY KEY (Result_Context_ID)
)
;



GRANT DELETE ON MLBD.Result_Context TO BARD
;
GRANT INSERT ON MLBD.Result_Context TO BARD
;
GRANT SELECT ON MLBD.Result_Context TO BARD
;
GRANT UPDATE ON MLBD.Result_Context TO BARD
;

-- 
-- TABLE: MLBD.Result_Context_Item 
--

CREATE TABLE MLBD.Result_Context_Item(
    Result_Context_Item_ID    NUMBER(10, 0)    NOT NULL,
    Experiment_ID             NUMBER(10, 0)    NOT NULL,
    Result_Context_ID         NUMBER(10, 0)    NOT NULL,
    Group_No                  NUMBER(10, 0),
    Attribute_ID              NUMBER(10, 0)    NOT NULL,
    Value_ID                  NUMBER(10, 0),
    Qualifier                 CHAR(2)          
                              CONSTRAINT CK_QUALIFIER_1 CHECK (Qualifier in ('=', '>', '<', '<=', '>=', '~')),
    Value_Display             VARCHAR2(256),
    Value_num                 BINARY_FLOAT,
    Value_Min                 BINARY_FLOAT,
    value_Max                 BINARY_FLOAT,
    CONSTRAINT PK_Measure_context_item_1 PRIMARY KEY (Result_Context_Item_ID)
)
;



COMMENT ON COLUMN MLBD.Result_Context_Item.Group_No IS 'rows with the same group_no in the measure context combine to a single group for UI purposes'
;
COMMENT ON COLUMN MLBD.Result_Context_Item.Value_Display IS 'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
GRANT DELETE ON MLBD.Result_Context_Item TO BARD
;
GRANT INSERT ON MLBD.Result_Context_Item TO BARD
;
GRANT SELECT ON MLBD.Result_Context_Item TO BARD
;
GRANT UPDATE ON MLBD.Result_Context_Item TO BARD
;

-- 
-- TABLE: MLBD.Result_Hierarchy 
--

CREATE TABLE MLBD.Result_Hierarchy(
    Result_ID           NUMBER(10, 0)    NOT NULL,
    Parent_Result_ID    NUMBER(10, 0)    NOT NULL,
    Hierarchy_Type      VARCHAR2(10)     NOT NULL
                        CONSTRAINT CK_RESULT_HIERARCHY_TYPE CHECK (Hierarchy_Type in ('Child', 'Derives')),
    CONSTRAINT PK_Result_Hierarchy PRIMARY KEY (Result_ID, Parent_Result_ID)
)
;



COMMENT ON COLUMN MLBD.Result_Hierarchy.Hierarchy_Type IS 'two types of hierarchy are allowed: parent/child where one result is dependant on or grouped with another; derived from where aresult is used to claculate another (e.g. PI used for IC50).  The hierarchy types are mutually exclusive.'
;
GRANT DELETE ON MLBD.Result_Hierarchy TO BARD
;
GRANT INSERT ON MLBD.Result_Hierarchy TO BARD
;
GRANT SELECT ON MLBD.Result_Hierarchy TO BARD
;
GRANT UPDATE ON MLBD.Result_Hierarchy TO BARD
;

-- 
-- TABLE: MLBD.Result_Status 
--

CREATE TABLE MLBD.Result_Status(
    Result_Status_ID    NUMBER(10, 0)    NOT NULL,
    Status              VARCHAR2(20)     NOT NULL,
    CONSTRAINT PK_Result_Status PRIMARY KEY (Result_Status_ID)
)
;



GRANT DELETE ON MLBD.Result_Status TO BARD
;
GRANT INSERT ON MLBD.Result_Status TO BARD
;
GRANT SELECT ON MLBD.Result_Status TO BARD
;
GRANT UPDATE ON MLBD.Result_Status TO BARD
;

-- 
-- TABLE: MLBD.Result_Type 
--

CREATE TABLE MLBD.Result_Type(
    Result_Type_ID           NUMBER(10, 0)     NOT NULL,
    Parent_Result_type_ID    NUMBER(10, 0),
    Result_Type_Name         VARCHAR2(128)     NOT NULL,
    Description              VARCHAR2(1000),
    Result_Type_Status_ID    NUMBER(10, 0)     NOT NULL,
    Base_Unit                VARCHAR2(100),
    CONSTRAINT PK_Result_Type PRIMARY KEY (Result_Type_ID)
)
;



GRANT DELETE ON MLBD.Result_Type TO BARD
;
GRANT INSERT ON MLBD.Result_Type TO BARD
;
GRANT SELECT ON MLBD.Result_Type TO BARD
;
GRANT UPDATE ON MLBD.Result_Type TO BARD
;

-- 
-- TABLE: MLBD.Stage 
--

CREATE TABLE MLBD.Stage(
    Stage    VARCHAR2(20)    NOT NULL,
    CONSTRAINT PK_Stage PRIMARY KEY (Stage)
)
;



GRANT DELETE ON MLBD.Stage TO BARD
;
GRANT INSERT ON MLBD.Stage TO BARD
;
GRANT SELECT ON MLBD.Stage TO BARD
;
GRANT UPDATE ON MLBD.Stage TO BARD
;

-- 
-- TABLE: MLBD.Substance 
--

CREATE TABLE MLBD.Substance(
    Substance_ID        NUMBER(10, 0)     NOT NULL,
    Compound_ID         NUMBER(10, 0),
    SMILES              VARCHAR2(4000),
    Molecular_Weight    NUMBER(10, 3),
    Substance_Type      VARCHAR2(20)      NOT NULL,
    CONSTRAINT PK_Substance PRIMARY KEY (Substance_ID)
)
;



GRANT DELETE ON MLBD.Substance TO BARD
;
GRANT INSERT ON MLBD.Substance TO BARD
;
GRANT SELECT ON MLBD.Substance TO BARD
;
GRANT UPDATE ON MLBD.Substance TO BARD
;

-- 
-- TABLE: MLBD.Unit 
--

CREATE TABLE MLBD.Unit(
    Unit           VARCHAR2(100)     NOT NULL,
    Description    VARCHAR2(1000)    NOT NULL,
    CONSTRAINT PK_Unit PRIMARY KEY (Unit)
)
;



GRANT DELETE ON MLBD.Unit TO BARD
;
GRANT INSERT ON MLBD.Unit TO BARD
;
GRANT SELECT ON MLBD.Unit TO BARD
;
GRANT UPDATE ON MLBD.Unit TO BARD
;

-- 
-- TABLE: MLBD.Unit_Conversion 
--

CREATE TABLE MLBD.Unit_Conversion(
    From_Unit     VARCHAR2(100)    NOT NULL,
    To_Unit       VARCHAR2(100)    NOT NULL,
    Multiplier    BINARY_FLOAT,
    Offset        BINARY_FLOAT,
    Formula       VARCHAR2(256),
    CONSTRAINT PK_Unit_Conversion PRIMARY KEY (From_Unit, To_Unit)
)
;



GRANT DELETE ON MLBD.Unit_Conversion TO BARD
;
GRANT INSERT ON MLBD.Unit_Conversion TO BARD
;
GRANT SELECT ON MLBD.Unit_Conversion TO BARD
;
GRANT UPDATE ON MLBD.Unit_Conversion TO BARD
;

-- 
-- VIEW: BARD.ASSAY 
--

CREATE VIEW BARD.ASSAY AS
select *
from mlbd.Assay
;

-- 
-- VIEW: BARD.ASSAY_STATUS 
--

CREATE VIEW BARD.ASSAY_STATUS AS
select *
from mlbd.Assay_Status
;

-- 
-- VIEW: BARD.ATTRIBUTE_TYPE 
--

CREATE VIEW BARD.ATTRIBUTE_TYPE AS
select *
from mlbd.ATTRIBUTE_TYPE 
;

-- 
-- VIEW: BARD.element 
--

CREATE VIEW BARD.element AS
select *
from mlbd.Element
;

-- 
-- VIEW: BARD.ELEMENT_STATUS 
--

CREATE VIEW BARD.Element_Status AS
select *
from mlbd.Element_Status
;

-- 
-- VIEW: BARD.EXPERIMENT 
--

CREATE VIEW BARD.EXPERIMENT AS
select *
from mlbd.Experiment
;

-- 
-- VIEW: BARD.EXPERIMENT_STATUS 
--

CREATE VIEW BARD.EXPERIMENT_STATUS AS
SELECT *
FROM MLBD.Experiment_Status
;

-- 
-- VIEW: BARD.EXTERNAL_ASSAY 
--

CREATE VIEW BARD.EXTERNAL_ASSAY AS
SELECT *
FROM MLBD.External_Assay
;

-- 
-- VIEW: BARD.EXTERNAL_SYSTEM 
--

CREATE VIEW BARD.EXTERNAL_SYSTEM AS
SELECT *
FROM MLBD.External_System
;

-- 
-- VIEW: BARD.LABORATORY 
--

CREATE VIEW BARD.laboratory AS
SELECT *
FROM MLBD.Laboratory
;

-- 
-- VIEW: BARD.MEASURE 
--

CREATE VIEW BARD.MEASURE AS
SELECT *
FROM MLBD.Measure
;

-- 
-- VIEW: BARD.MEASURE_CONTEXT 
--

CREATE VIEW BARD.MEASURE_CONTEXT AS
SELECT *
FROM MLBD.Measure_Context
;

-- 
-- VIEW: BARD.MEASURE_CONTEXT_ITEM 
--

CREATE VIEW BARD.MEASURE_CONTEXT_ITEM AS
SELECT *
FROM MLBD.Measure_Context_Item
;

-- 
-- VIEW: BARD.ONTOLOGY 
--

CREATE VIEW BARD.ONTOLOGY AS
SELECT *
FROM MLBD.Ontology
;

-- 
-- VIEW: BARD.ONTOLOGY_ITEM 
--

CREATE VIEW BARD.ONTOLOGY_ITEM AS
SELECT *
FROM MLBD.Ontology_Item
;

-- 
-- VIEW: BARD.PROJECT 
--

CREATE VIEW BARD.PROJECT AS
SELECT *
FROM MLBD.Project
;

-- 
-- VIEW: BARD.PROJECT_ASSAY 
--

CREATE VIEW BARD.PROJECT_ASSAY AS
SELECT *
FROM MLBD.Project_Assay
;

-- 
-- VIEW: BARD.PROTOCOL 
--

CREATE VIEW BARD.PROTOCOL AS
SELECT *
FROM MLBD.Protocol
;

-- 
-- VIEW: BARD.RESULT 
--

CREATE VIEW BARD.RESULT AS
SELECT *
FROM MLBD.Result
;

-- 
-- VIEW: BARD.RESULT_CONTEXT 
--

CREATE VIEW BARD.RESULT_CONTEXT AS
SELECT *
FROM MLBD.Result_Context
;

-- 
-- VIEW: BARD.RESULT_CONTEXT_ITEM 
--

CREATE VIEW BARD.RESULT_CONTEXT_ITEM AS
SELECT *
FROM MLBD.Result_Context_Item
;

-- 
-- VIEW: BARD.RESULT_HIERARCHY 
--

CREATE VIEW BARD.RESULT_HIERARCHY AS
SELECT *
FROM MLBD.Result_Hierarchy
;

-- 
-- VIEW: BARD.RESULT_STATUS 
--

CREATE VIEW BARD.RESULT_STATUS AS
SELECT *
FROM MLBD.Result_Status
;

-- 
-- VIEW: BARD.RESULT_TYPE 
--

CREATE VIEW BARD.RESULT_TYPE AS
SELECT *
FROM MLBD.Result_Type
;

-- 
-- VIEW: BARD.STAGE 
--

CREATE VIEW BARD.STAGE AS
SELECT *
FROM MLBD.Stage
;

-- 
-- VIEW: BARD.SUBSTANCE 
--

CREATE VIEW BARD.SUBSTANCE AS
SELECT *
FROM MLBD.Substance
;

-- 
-- VIEW: BARD.UNIT 
--

CREATE VIEW BARD.UNIT AS
SELECT *
FROM MLBD.Unit
;

-- 
-- VIEW: BARD.UNIT_CONVERSION 
--

CREATE VIEW BARD.UNIT_CONVERSION AS
SELECT *
FROM MLBD.Unit_Conversion
;

-- 
-- INDEX: MLBD.AK_Assay_Status 
--

CREATE UNIQUE INDEX MLBD.AK_Assay_Status ON MLBD.Assay_Status(Status)
;
-- 
-- INDEX: MLBD.AK_Measure_Context_item 
--

CREATE UNIQUE INDEX MLBD.AK_Measure_Context_item ON MLBD.Measure_Context_Item(Measure_Context_ID, Group_No, Attribute_ID, Value_Display)
;
-- 
-- INDEX: MLBD.AK_Measure_Context_item_1 
--

CREATE UNIQUE INDEX MLBD.AK_Measure_Context_item_1 ON MLBD.Result_Context_Item(Group_No, Attribute_ID, Value_Display)
;
-- 
-- TABLE: MLBD.Assay 
--

ALTER TABLE MLBD.Assay ADD CONSTRAINT FK_assaY_assay_status_id 
    FOREIGN KEY (Assay_status_ID)
    REFERENCES MLBD.Assay_Status(Assay_status_ID)
;


-- 
-- TABLE: MLBD.Element 
--

ALTER TABLE MLBD.Element ADD CONSTRAINT FK_Element_element_status 
    FOREIGN KEY (Element_Status_ID)
    REFERENCES MLBD.Element_Status(Element_Status_ID)
;

ALTER TABLE MLBD.Element ADD CONSTRAINT FK_element_parent_element 
    FOREIGN KEY (Parent_Element_ID)
    REFERENCES MLBD.Element(Element_ID)
;

ALTER TABLE MLBD.Element ADD CONSTRAINT FK_Element_Unit 
    FOREIGN KEY (Unit)
    REFERENCES MLBD.Unit(Unit)
;


-- 
-- TABLE: MLBD.Experiment 
--

ALTER TABLE MLBD.Experiment ADD CONSTRAINT FK_experiment_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;

ALTER TABLE MLBD.Experiment ADD CONSTRAINT FK_experiment_exprt_status 
    FOREIGN KEY (Experiment_Status_ID)
    REFERENCES MLBD.Experiment_Status(Experiment_Status_ID)
;

ALTER TABLE MLBD.Experiment ADD CONSTRAINT FK_Experiment_source_lab 
    FOREIGN KEY (Source_ID)
    REFERENCES MLBD.Laboratory(Lab_ID)
;

ALTER TABLE MLBD.Experiment ADD CONSTRAINT FK_Project_experiment 
    FOREIGN KEY (Project_ID)
    REFERENCES MLBD.Project(Project_ID)
;


-- 
-- TABLE: MLBD.External_Assay 
--

ALTER TABLE MLBD.External_Assay ADD CONSTRAINT fk_ext_assay_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;

ALTER TABLE MLBD.External_Assay ADD CONSTRAINT FK_ext_assay_ext_system 
    FOREIGN KEY (External_System_ID)
    REFERENCES MLBD.External_System(External_System_ID)
;


-- 
-- TABLE: MLBD.Measure 
--

ALTER TABLE MLBD.Measure ADD CONSTRAINT FK_Measure_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;

ALTER TABLE MLBD.Measure ADD CONSTRAINT FK_measure_M_context_Item 
    FOREIGN KEY (Measure_Context_ID)
    REFERENCES MLBD.Measure_Context(Measure_Context_ID)
;

ALTER TABLE MLBD.Measure ADD CONSTRAINT FK_Measure_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES MLBD.Result_Type(Result_Type_ID)
;

ALTER TABLE MLBD.Measure ADD CONSTRAINT FK_Measure_Unit 
    FOREIGN KEY (Entry_Unit)
    REFERENCES MLBD.Unit(Unit)
;


-- 
-- TABLE: MLBD.Measure_Context_Item 
--

ALTER TABLE MLBD.Measure_Context_Item ADD CONSTRAINT FK_M_Context_item_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;

ALTER TABLE MLBD.Measure_Context_Item ADD CONSTRAINT FK_M_context_item_attribute 
    FOREIGN KEY (Attribute_ID)
    REFERENCES MLBD.Element(Element_ID)
;

ALTER TABLE MLBD.Measure_Context_Item ADD CONSTRAINT FK_M_context_Item_M_Context 
    FOREIGN KEY (Measure_Context_ID)
    REFERENCES MLBD.Measure_Context(Measure_Context_ID)
;

ALTER TABLE MLBD.Measure_Context_Item ADD CONSTRAINT fk_M_context_item_value 
    FOREIGN KEY (Value_ID)
    REFERENCES MLBD.Element(Element_ID)
;


-- 
-- TABLE: MLBD.Ontology_Item 
--

ALTER TABLE MLBD.Ontology_Item ADD CONSTRAINT FK_ontology_item_element 
    FOREIGN KEY (Element_ID)
    REFERENCES MLBD.Element(Element_ID)
;

ALTER TABLE MLBD.Ontology_Item ADD CONSTRAINT FK_ontology_item_Ontology 
    FOREIGN KEY (Ontology_ID)
    REFERENCES MLBD.Ontology(Ontology_ID)
;

ALTER TABLE MLBD.Ontology_Item ADD CONSTRAINT FK_Ontology_item_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES MLBD.Result_Type(Result_Type_ID)
;


-- 
-- TABLE: MLBD.Project_Assay 
--

ALTER TABLE MLBD.Project_Assay ADD CONSTRAINT FK_Project_assay_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;

ALTER TABLE MLBD.Project_Assay ADD CONSTRAINT FK_project_assay_project 
    FOREIGN KEY (Project_ID)
    REFERENCES MLBD.Project(Project_ID)
;

ALTER TABLE MLBD.Project_Assay ADD CONSTRAINT FK_Project_assay_stage 
    FOREIGN KEY (Stage)
    REFERENCES MLBD.Stage(Stage)
;


-- 
-- TABLE: MLBD.Protocol 
--

ALTER TABLE MLBD.Protocol ADD CONSTRAINT FK_Protocol_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES MLBD.Assay(Assay_ID)
;


-- 
-- TABLE: MLBD.Result 
--

ALTER TABLE MLBD.Result ADD CONSTRAINT FK_result_experiment 
    FOREIGN KEY (Experiment_ID)
    REFERENCES MLBD.Experiment(Experiment_ID)
;

ALTER TABLE MLBD.Result ADD CONSTRAINT fk_result_result_context 
    FOREIGN KEY (Result_Context_ID)
    REFERENCES MLBD.Result_Context(Result_Context_ID)
;

ALTER TABLE MLBD.Result ADD CONSTRAINT FK_result_result_status 
    FOREIGN KEY (Result_Status_ID)
    REFERENCES MLBD.Result_Status(Result_Status_ID)
;

ALTER TABLE MLBD.Result ADD CONSTRAINT FK_Result_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES MLBD.Result_Type(Result_Type_ID)
;

ALTER TABLE MLBD.Result ADD CONSTRAINT FK_Result_substance 
    FOREIGN KEY (Substance_ID)
    REFERENCES MLBD.Substance(Substance_ID)
;

ALTER TABLE MLBD.Result ADD CONSTRAINT FK_result_unit 
    FOREIGN KEY (Entry_Unit)
    REFERENCES MLBD.Unit(Unit)
;


-- 
-- TABLE: MLBD.Result_Context_Item 
--

ALTER TABLE MLBD.Result_Context_Item ADD CONSTRAINT FK_R_context_item_attribute 
    FOREIGN KEY (Attribute_ID)
    REFERENCES MLBD.Element(Element_ID)
;

ALTER TABLE MLBD.Result_Context_Item ADD CONSTRAINT fk_R_Context_item_experiment 
    FOREIGN KEY (Experiment_ID)
    REFERENCES MLBD.Experiment(Experiment_ID)
;

ALTER TABLE MLBD.Result_Context_Item ADD CONSTRAINT FK_R_context_item_R_context 
    FOREIGN KEY (Result_Context_ID)
    REFERENCES MLBD.Result_Context(Result_Context_ID)
;

ALTER TABLE MLBD.Result_Context_Item ADD CONSTRAINT FK_R_Context_item_value 
    FOREIGN KEY (Value_ID)
    REFERENCES MLBD.Element(Element_ID)
;


-- 
-- TABLE: MLBD.Result_Hierarchy 
--

ALTER TABLE MLBD.Result_Hierarchy ADD CONSTRAINT FK_result_hierarchy_result 
    FOREIGN KEY (Parent_Result_ID)
    REFERENCES MLBD.Result(Result_ID)
;

ALTER TABLE MLBD.Result_Hierarchy ADD CONSTRAINT FK_result_hierarchy_rslt_Prnt 
    FOREIGN KEY (Result_ID)
    REFERENCES MLBD.Result(Result_ID)
;


-- 
-- TABLE: MLBD.Result_Type 
--

ALTER TABLE MLBD.Result_Type ADD CONSTRAINT FK_result_type_element_status 
    FOREIGN KEY (Result_Type_Status_ID)
    REFERENCES MLBD.Element_Status(Element_Status_ID)
;

ALTER TABLE MLBD.Result_Type ADD CONSTRAINT FK_result_type_rslt_typ_prnt 
    FOREIGN KEY (Parent_Result_type_ID)
    REFERENCES MLBD.Result_Type(Result_Type_ID)
;

ALTER TABLE MLBD.Result_Type ADD CONSTRAINT FK_result_type_unit 
    FOREIGN KEY (Base_Unit)
    REFERENCES MLBD.Unit(Unit)
;


-- 
-- TABLE: MLBD.Unit_Conversion 
--

ALTER TABLE MLBD.Unit_Conversion ADD CONSTRAINT FK_Unit_conversion_from_Unit 
    FOREIGN KEY (From_Unit)
    REFERENCES MLBD.Unit(Unit)
;

ALTER TABLE MLBD.Unit_Conversion ADD CONSTRAINT FK_Unit_conversion_to_unit 
    FOREIGN KEY (To_Unit)
    REFERENCES MLBD.Unit(Unit)
;


--
-- SYNONYMS
--

CONNECT BARD/BARD
;

-- create synonym Assay for bard.Assay;

create synonym Assay_ID for mlbd.Assay_ID;
-- create synonym assay_status for bard.assay_status;

create synonym assay_status_ID for mlbd.assay_status_ID;
-- create synonym element for bard.element;

create synonym element_ID for mlbd.element_ID;
-- create synonym element_status for bard.element_status;

create synonym element_status_ID for mlbd.element_status_ID;
-- create synonym experiment for bard.experiment;

create synonym experiment_ID for mlbd.experiment_ID;
-- create synonym experiment_status for bard.experiment_status;

create synonym experiment_status_ID for mlbd.experiment_status_ID;
-- create synonym external_assay for bard.external_assay;
-- create synonym external_system for bard.external_system;

create synonym external_system_ID for mlbd.external_system_ID;
-- create synonym laboratory for bard.laboratory;

create synonym laboratory_ID for mlbd.laboratory_ID;
-- create synonym measure for bard.measure;

create synonym measure_ID for mlbd.measure_ID;
-- create synonym measure_context for bard.measure_context;

create synonym measure_context_ID for mlbd.measure_context_ID;
-- create synonym measure_context_item for bard.measure_context_item;

create synonym measure_context_item_ID for mlbd.measure_context_item_ID;
-- create synonym ontology for bard.ontology;

create synonym ontology_ID for mlbd.ontology_ID;
-- create synonym ontology_item for bard.ontology_item;

create synonym ontology_item_ID for mlbd.ontology_item_ID;
-- create synonym project for bard.project;

create synonym project_ID for mlbd.project_ID;
-- create synonym project_assay for bard.project_assay;
-- create synonym protocol for bard.protocol;

create synonym protocol_ID for mlbd.protocol_ID;
-- create synonym result for bard.result;

create synonym result_ID for mlbd.result_ID;
-- create synonym result_context for bard.result_context;

create synonym result_context_ID for mlbd.result_context_ID;
-- create synonym result_context_item for bard.result_context_item;

create synonym result_context_item_ID for mlbd.result_context_item_ID;
-- create synonym result_hierarchy for bard.result_hierarchy;
-- create synonym result_status for bard.result_status;

create synonym result_status_ID for mlbd.result_status_ID;
-- create synonym result_type for bard.result_type;

create synonym result_type_ID for mlbd.result_type_ID;
-- create synonym stage for bard.stage;
-- create synonym substance for bard.substance;

create synonym substance_ID for mlbd.substance_ID;
-- create synonym unit for bard.unit;
-- create synonym unit_conversion for bard.unit_conversion;


--
-- Programming data
--

insert into MLBD.Attribute_type (attribute_type, capabilities) 
   values ('Fixed', 'Attribute has one invariant value defined at asssay design time - no data entry at experiment time is necessary');
insert into MLBD.Attribute_type (attribute_type, capabilities) 
   values ('List', 'Attribute has a set of values seelcted from the MLBD.Element. All values must be descendant (child, grand-child, great- ..., etc.) leaf nodes of the Element hierarchy.  List generally refers to a set of rows where assay_ID, attribute_ID and group_no all match');
insert into MLBD.Attribute_type (attribute_type, capabilities) 
   values ('Range', 'Attribute has a numerical value that must lie between value_min and value_max');
insert into MLBD.Attribute_type (attribute_type, capabilities) 
   values ('Variable', 'Attribute has a numerical value without limitation');
insert into MLBD.Element_status (Element_status_id, status, Capability) 
   values ('1', 'Pending', 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval');
insert into MLBD.Element_status (Element_status_id, status, Capability) 
   values ('2', 'Published', 'Element can be used for any assay definiton or data upload');
insert into MLBD.Element_status (Element_status_id, status, Capability) 
   values ('3', 'Deprecated', 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement');
insert into MLBD.Element_status (Element_status_id, status, Capability) 
    values ('4', 'Retired', 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data');
insert into MLBD.Result_status (result_status_id, status) 
   values ('1', 'Pending');
insert into MLBD.Result_status (result_status_id, status) 
   values ('2', 'Approved');
insert into MLBD.Result_status (result_status_id, status) 
   values ('3', 'Rejected');
insert into MLBD.Result_status (result_status_id, status) 
   values ('4', 'Uploading');
insert into MLBD.Result_status (result_status_id, status) 
   values ('5', 'Uploaded');
insert into MLBD.Result_status (result_status_id, status) 
   values ('6', 'Mark for Deletion');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) 
   values ('1', 'Pending', 'Experiment is newly loaded and has not been approved for upload to the warehouse');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) 
   values ('2', 'Approved', 'Experiment has been approved as ready to upload.  It does not mena results are correct or cannot be changed');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability)  
   values ('3', 'Rejected', 'Experiment data has been rejected as not scientifically valid.  This will not be uploaded to the warehouse');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability)  
   values ('4', 'Held', 'Experiment data is private to the loading institution (Source Laboratory).  The Hold Until Date is set.  Though uploaded it cannot be queried except by the source laboratory');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability)  
   values ('5', 'Uploaded', 'Experiment has been copied into the warehouse and results are available for querying');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability)  
   values ('6', 'Mark for Deletion', 'Experiment has been confirmed as present in the warehouse and may be deleted at any time.');
insert into MLBD.Assay_Status (assay_status_ID, status)  
   values ('1', 'Pending');
insert into MLBD.Assay_Status (assay_status_ID, status)  
   values ('2', 'Active');
insert into MLBD.Assay_Status (assay_status_ID, status)  
   values ('3', 'Superceded');
insert into MLBD.Assay_Status (assay_status_ID)  
   values ('4');
insert into MLBD.Stage (Stage)  
   values ('Primary');
insert into MLBD.Stage (Stage)  
   values ('Secondary');
insert into MLBD.Stage (Stage)  
   values ('Confirmation');
insert into MLBD.Stage (Stage)  
   values ('Tertiary');
insert into MLBD.Stage (Stage)  
   values ('Counter-screen');
insert into MLBD.Stage (Stage)  
   values ('TBD');
Commit;
