--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Monday, April 02, 2012 18:21:14
-- Target DBMS : MySQL 5.x
--

-- 
-- TABLE: Assay 
--

CREATE TABLE Assay(
    Assay_ID           INT              AUTO_INCREMENT,
    Assay_Name         VARCHAR(128)     NOT NULL,
    Assay_status_ID    INT              NOT NULL,
    Assay_Version      VARCHAR(10)      DEFAULT 1 NOT NULL,
    Description        VARCHAR(1000),
    Designed_By        VARCHAR(100),
    Version            INT              DEFAULT 0 NOT NULL,
    Date_Created       DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated       DATETIME,
    Modified_by        VARCHAR(40),
    PRIMARY KEY (Assay_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Assay_Status 
--

CREATE TABLE Assay_Status(
    Assay_status_ID    INT            AUTO_INCREMENT,
    Status             VARCHAR(20)    NOT NULL,
    Version            INT            DEFAULT 0 NOT NULL,
    Date_Created       DATETIME       DEFAULT sysdate NOT NULL,
    Last_Updated       DATETIME,
    Modified_by        VARCHAR(40),
    PRIMARY KEY (Assay_status_ID)
)ENGINE=INNODB
COMMENT=''
;

insert into MLBD.Assay_Status (assay_status_ID, status) values ('1', 'Pending');
insert into MLBD.Assay_Status (assay_status_ID, status) values ('2', 'Active');
insert into MLBD.Assay_Status (assay_status_ID, status) values ('3', 'Superceded');
insert into MLBD.Assay_Status (assay_status_ID) values ('4', 'Retired');
commit;
-- 
-- TABLE: Element 
--

CREATE TABLE Element(
    Element_ID           INT              AUTO_INCREMENT,
    Parent_Element_ID    INT,
    Label                VARCHAR(128)     NOT NULL,
    Description          VARCHAR(1000),
    Abbreviation         VARCHAR(20),
    Acronym              VARCHAR(20),
    Synonyms             VARCHAR(1000),
    Element_Status_ID    INT              NOT NULL,
    Unit                 VARCHAR(100),
    Version              INT              DEFAULT 0 NOT NULL,
    Date_Created         DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated         DATETIME,
    Modified_by          VARCHAR(40),
    PRIMARY KEY (Element_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Element_Status 
--

CREATE TABLE Element_Status(
    Element_Status_ID    INT             AUTO_INCREMENT,
    Element_Status       VARCHAR(20)     NOT NULL,
    Capability           VARCHAR(256),
    Version              INT             DEFAULT 0 NOT NULL,
    Date_Created         DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated         DATETIME,
    Modified_by          VARCHAR(40),
    PRIMARY KEY (Element_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

insert into MLBD.Element_status (Element_status_id, status, Capability) values ('1', 'Pending', 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval');
insert into MLBD.Element_status (Element_status_id, status, Capability) values ('2', 'Published', 'Element can be used for any assay definiton or data upload');
insert into MLBD.Element_status (Element_status_id, status, Capability) values ('3', 'Deprecated', 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement');
insert into MLBD.Element_status (Element_status_id, status, Capability) values ('4', 'Retired', 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data');
commit;
-- 
-- TABLE: Experiment 
--

CREATE TABLE Experiment(
    Experiment_ID           INT              AUTO_INCREMENT,
    Experiment_Name         VARCHAR(256)     NOT NULL,
    Assay_ID                INT              NOT NULL,
    Project_ID              INT,
    Experiment_Status_ID    INT              NOT NULL,
    Run_Date_From           DATE,
    Run_Date_To             DATE,
    Hold_Until_Date         DATE             
                            CHECK (Hold_Until_Date <= sysdate + 366),
    Description             VARCHAR(1000),
    Source_ID               INT              NOT NULL,
    Version                 INT              DEFAULT 0 NOT NULL,
    Date_Created            DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated            DATETIME,
    Modified_by             VARCHAR(40),
    PRIMARY KEY (Experiment_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Experiment_Status 
--

CREATE TABLE Experiment_Status(
    Experiment_Status_ID    INT              AUTO_INCREMENT,
    Status                  VARCHAR(20)      NOT NULL,
    Capability              VARCHAR(1000),
    Version                 INT              DEFAULT 0 NOT NULL,
    Date_Created            DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated            DATETIME,
    Modified_by             VARCHAR(40),
    PRIMARY KEY (Experiment_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('1', 'Pending', 'Experiment is newly loaded and has not been approved for upload to the warehouse');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('2', 'Approved', 'Experiment has been approved as ready to upload.  It does not mena results are correct or cannot be changed');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('3', 'Rejected', 'Experiment data has been rejected as not scientifically valid.  This will not be uploaded to the warehouse');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('4', 'Held', 'Experiment data is private to the loading institution (Source Laboratory).  The Hold Until Date is set.  Though uploaded it cannot be queried except by the source laboratory');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('5', 'Uploaded', 'Experiment has been copied into the warehouse and results are available for querying');
insert into MLBD.Experiment_Status (Experiment_status_ID, status, Capability) values ('6', 'Mark for Deletion', 'Experiment has been confirmed as present in the warehouse and may be deleted at any time.');
commit;
-- 
-- TABLE: External_Assay 
--

CREATE TABLE External_Assay(
    External_System_ID    INT             NOT NULL,
    Assay_ID              INT             NOT NULL,
    Ext_Assay_ID          VARCHAR(128)    NOT NULL,
    Version               INT             DEFAULT 0 NOT NULL,
    Date_Created          DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated          DATETIME,
    Modified_by           VARCHAR(40),
    PRIMARY KEY (External_System_ID, Assay_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: External_System 
--

CREATE TABLE External_System(
    External_System_ID    INT              AUTO_INCREMENT,
    System_Name           VARCHAR(128)     NOT NULL,
    Owner                 VARCHAR(128),
    System_URL            VARCHAR(1000),
    Version               INT              DEFAULT 0 NOT NULL,
    Date_Created          DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated          DATETIME,
    Modified_by           VARCHAR(40),
    PRIMARY KEY (External_System_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Laboratory 
--

CREATE TABLE Laboratory(
    Lab_ID          INT              AUTO_INCREMENT,
    Lab_Name        VARCHAR(125)     NOT NULL,
    Abbreviation    VARCHAR(20),
    Description     VARCHAR(1000),
    Location        VARCHAR(250),
    Version         INT              DEFAULT 0 NOT NULL,
    Date_Created    DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (Lab_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Measure 
--

CREATE TABLE Measure(
    Measure_ID            INT             AUTO_INCREMENT,
    Assay_ID              INT             NOT NULL,
    Result_Type_ID        INT             NOT NULL,
    Entry_Unit            VARCHAR(100),
    Measure_Context_ID    INT,
    Version               INT             DEFAULT 0 NOT NULL,
    Date_Created          DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated          DATETIME,
    Modified_by           VARCHAR(40),
    PRIMARY KEY (Measure_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Measure_Context 
--

CREATE TABLE Measure_Context(
    Measure_Context_ID    INT             AUTO_INCREMENT,
    Context_Name          VARCHAR(128)    NOT NULL,
    Version               INT             DEFAULT 0 NOT NULL,
    Date_Created          DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated          DATETIME,
    Modified_by           VARCHAR(40),
    PRIMARY KEY (Measure_Context_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Measure_Context_Item 
--

CREATE TABLE Measure_Context_Item(
    measure_Context_Item_ID    INT             AUTO_INCREMENT,
    Assay_ID                   INT             NOT NULL,
    Measure_Context_ID         INT             NOT NULL,
    Group_No                   INT,
    Attribute_Type             VARCHAR(20)     NOT NULL
                               CHECK (Attribute_Type in ('Fixed', 'List', 'Range', 'Number')),
    Attribute_ID               INT             NOT NULL,
    Qualifier                  CHAR(2),
    Value_ID                   INT,
    Value_Display              VARCHAR(256),
    Value_num                  FLOAT(8, 0),
    Value_Min                  FLOAT(8, 0),
    value_Max                  FLOAT(8, 0),
    Version                    INT             DEFAULT 0 NOT NULL,
    Date_Created               DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated               DATETIME,
    Modified_by                VARCHAR(40),
    PRIMARY KEY (measure_Context_Item_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Ontology 
--

CREATE TABLE Ontology(
    Ontology_ID      INT              AUTO_INCREMENT,
    Ontology_Name    VARCHAR(256)     NOT NULL,
    Abbreviation     VARCHAR(20),
    System_URL       VARCHAR(1000),
    Version          INT              DEFAULT 0 NOT NULL,
    Date_Created     DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated     DATETIME,
    Modified_by      VARCHAR(40),
    PRIMARY KEY (Ontology_ID)
)ENGINE=INNODB
COMMENT='an external ontology or dictionary or other source of reference data'
;

-- 
-- TABLE: Ontology_Item 
--

CREATE TABLE Ontology_Item(
    Ontology_Item_ID    INT            AUTO_INCREMENT,
    Ontology_ID         INT            NOT NULL,
    Element_ID          INT,
    Item_Reference      CHAR(10),
    Result_Type_ID      INT,
    Version             INT            DEFAULT 0 NOT NULL,
    Date_Created        DATETIME       DEFAULT sysdate NOT NULL,
    Last_Updated        DATETIME,
    Modified_by         VARCHAR(40),
    PRIMARY KEY (Ontology_Item_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Project 
--

CREATE TABLE Project(
    Project_ID      INT              AUTO_INCREMENT,
    Project_Name    VARCHAR(256)     NOT NULL,
    Group_Type      VARCHAR(20)      DEFAULT 'Project' NOT NULL
                    CHECK (Group_Type in ('Project', 'Campaign', 'Panel', 'Study')),
    Description     VARCHAR(1000),
    Version         INT              DEFAULT 0 NOT NULL,
    Date_Created    DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (Project_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Project_Assay 
--

CREATE TABLE Project_Assay(
    Assay_ID               INT              NOT NULL,
    Project_ID             INT              NOT NULL,
    STAGE                  VARCHAR(20)      NOT NULL,
    Sequence_no            INT,
    Promotion_Threshold    FLOAT(8, 0),
    Promotion_Criteria     VARCHAR(1000),
    Version                INT              DEFAULT 0 NOT NULL,
    Date_Created           DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated           DATETIME,
    Modified_by            VARCHAR(40),
    PRIMARY KEY (Assay_ID, Project_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Protocol 
--

CREATE TABLE Protocol(
    Protocol_ID          INT             AUTO_INCREMENT,
    Protocol_Name        VARCHAR(500)    NOT NULL,
    Protocol_Document    LONGBLOB,
    Assay_ID             INT             NOT NULL,
    Version              INT             DEFAULT 0 NOT NULL,
    Date_Created         DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated         DATETIME,
    Modified_by          VARCHAR(40),
    PRIMARY KEY (Protocol_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Qualifier 
--

CREATE TABLE Qualifier(
    Qualifier       CHAR(2)          NOT NULL,
    Description     VARCHAR(1000),
    Version         INT              DEFAULT 0 NOT NULL,
    Date_Created    DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (Qualifier)
)ENGINE=INNODB
COMMENT=''
;

insert into mlbd.qualifier values ('=', 'equals');
insert into mlbd.qualifier values ('>=', 'greater than or equal');
insert into mlbd.qualifier values ('<=', 'less than or equal');
insert into mlbd.qualifier values ('<', 'greater than');
insert into mlbd.qualifier values ('>', 'less than');
insert into mlbd.qualifier values ('~', 'approximatley');
insert into mlbd.qualifier values ('>>', 'very much greater than');
insert into mlbd.qualifier values ('<<', 'very much less than');
commit;
-- 
-- TABLE: Result 
--

CREATE TABLE Result(
    Result_ID            INT             AUTO_INCREMENT,
    Value_Display        VARCHAR(256),
    value_num            FLOAT(8, 0),
    value_min            FLOAT(8, 0),
    Value_Max            FLOAT(8, 0),
    Qualifier            CHAR(2),
    Result_Status_ID     INT             NOT NULL,
    Experiment_ID        INT             NOT NULL,
    Substance_ID         INT             NOT NULL,
    Result_Context_ID    INT             NOT NULL,
    Entry_Unit           VARCHAR(100),
    Result_Type_ID       INT             NOT NULL,
    Version              INT             DEFAULT 0 NOT NULL,
    Date_Created         DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated         DATETIME,
    Modified_by          VARCHAR(40),
    PRIMARY KEY (Result_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Result_Context 
--

CREATE TABLE Result_Context(
    Result_Context_ID    INT             AUTO_INCREMENT,
    Context_Name         VARCHAR(125),
    Version              INT             DEFAULT 0 NOT NULL,
    Date_Created         DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated         DATETIME,
    Modified_by          VARCHAR(40),
    PRIMARY KEY (Result_Context_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Result_Context_Item 
--

CREATE TABLE Result_Context_Item(
    Result_Context_Item_ID    INT             AUTO_INCREMENT,
    Experiment_ID             INT             NOT NULL,
    Result_Context_ID         INT             NOT NULL,
    Group_No                  INT,
    Attribute_ID              INT             NOT NULL,
    Value_ID                  INT,
    Qualifier                 CHAR(2),
    Value_Display             VARCHAR(256),
    Value_num                 FLOAT(8, 0),
    Value_Min                 FLOAT(8, 0),
    value_Max                 FLOAT(8, 0),
    Version                   INT             DEFAULT 0 NOT NULL,
    Date_Created              DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated              DATETIME,
    Modified_by               VARCHAR(40),
    PRIMARY KEY (Result_Context_Item_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Result_Hierarchy 
--

CREATE TABLE Result_Hierarchy(
    Result_ID           INT            NOT NULL,
    Parent_Result_ID    INT            NOT NULL,
    Hierarchy_Type      VARCHAR(10)    NOT NULL
                        CHECK (Hierarchy_Type in ('Child', 'Derives')),
    Version             INT            DEFAULT 0 NOT NULL,
    Date_Created        DATETIME       DEFAULT sysdate NOT NULL,
    Last_Updated        DATETIME,
    Modified_by         VARCHAR(40),
    PRIMARY KEY (Result_ID, Parent_Result_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Result_Status 
--

CREATE TABLE Result_Status(
    Result_Status_ID    INT            AUTO_INCREMENT,
    Status              VARCHAR(20)    NOT NULL,
    Version             INT            DEFAULT 0 NOT NULL,
    Date_Created        DATETIME       DEFAULT sysdate NOT NULL,
    Last_Updated        DATETIME,
    Modified_by         VARCHAR(40),
    PRIMARY KEY (Result_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

insert into MLBD.Result_status (result_status_id, status) values ('1', 'Pending');
insert into MLBD.Result_status (result_status_id, status) values ('2', 'Approved');
insert into MLBD.Result_status (result_status_id, status) values ('3', 'Rejected');
insert into MLBD.Result_status (result_status_id, status) values ('4', 'Uploading');
insert into MLBD.Result_status (result_status_id, status) values ('5', 'Uploaded');
insert into MLBD.Result_status (result_status_id, status) values ('6', 'Mark for Deletion');
commit;
-- 
-- TABLE: Result_Type 
--

CREATE TABLE Result_Type(
    Result_Type_ID           INT              AUTO_INCREMENT,
    Parent_Result_type_ID    INT,
    Result_Type_Name         VARCHAR(128)     NOT NULL,
    Description              VARCHAR(1000),
    Result_Type_Status_ID    INT              NOT NULL,
    Base_Unit                VARCHAR(100),
    Version                  INT              DEFAULT 0 NOT NULL,
    Date_Created             DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated             DATETIME,
    Modified_by              VARCHAR(40),
    PRIMARY KEY (Result_Type_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Stage 
--

CREATE TABLE Stage(
    Stage           VARCHAR(20)      NOT NULL,
    Description     VARCHAR(1000),
    Version         INT              DEFAULT 0 NOT NULL,
    Date_Created    DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (Stage)
)ENGINE=INNODB
COMMENT=''
;

insert into MLBD.Stage (Stage) values ('Primary');
insert into MLBD.Stage (Stage) values ('Secondary');
insert into MLBD.Stage (Stage) values ('Confirmation');
insert into MLBD.Stage (Stage) values ('Tertiary');
insert into MLBD.Stage (Stage) values ('Counter-screen');
insert into MLBD.Stage (Stage) values ('TBD');
Commit;
-- 
-- TABLE: Substance 
--

CREATE TABLE Substance(
    Substance_ID        INT               AUTO_INCREMENT,
    Compound_ID         INT,
    SMILES              VARCHAR(4000),
    Molecular_Weight    DECIMAL(10, 3),
    Substance_Type      VARCHAR(20)       NOT NULL
                        CHECK (Substance_Type in ('small molecule', 'protein', 'peptide', 'antibody', 'cell', 'oligonucleotide')),
    Version             INT               DEFAULT 0 NOT NULL,
    Date_Created        DATETIME          DEFAULT sysdate NOT NULL,
    Last_Updated        DATETIME,
    Modified_by         VARCHAR(40),
    PRIMARY KEY (Substance_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Unit 
--

CREATE TABLE Unit(
    Unit            VARCHAR(100)     NOT NULL,
    Description     VARCHAR(1000),
    Version         INT              DEFAULT 0 NOT NULL,
    Date_Created    DATETIME         DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (Unit)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Unit_Conversion 
--

CREATE TABLE Unit_Conversion(
    From_Unit       VARCHAR(100)    NOT NULL,
    To_Unit         VARCHAR(100)    NOT NULL,
    Multiplier      FLOAT(8, 0),
    Offset          FLOAT(8, 0),
    Formula         VARCHAR(256),
    Version         INT             DEFAULT 0 NOT NULL,
    Date_Created    DATETIME        DEFAULT sysdate NOT NULL,
    Last_Updated    DATETIME,
    Modified_by     VARCHAR(40),
    PRIMARY KEY (From_Unit, To_Unit)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- INDEX: FK_ASSAY_ASSAY_STATUS_ID 
--

CREATE INDEX FK_ASSAY_ASSAY_STATUS_ID ON Assay(Assay_status_ID)
;
-- 
-- INDEX: AK_Assay_Status 
--

CREATE UNIQUE INDEX AK_Assay_Status ON Assay_Status(Status)
;
-- 
-- INDEX: FK_ELEMENT_ELEMENT_STATUS 
--

CREATE INDEX FK_ELEMENT_ELEMENT_STATUS ON Element(Element_Status_ID)
;
-- 
-- INDEX: FK_ELEMENT_UNIT 
--

CREATE INDEX FK_ELEMENT_UNIT ON Element(Unit)
;
-- 
-- INDEX: FK_ELEMENT_PARENT_ELEMENT 
--

CREATE INDEX FK_ELEMENT_PARENT_ELEMENT ON Element(Parent_Element_ID)
;
-- 
-- INDEX: FK_EXPERIMENT_ASSAY 
--

CREATE INDEX FK_EXPERIMENT_ASSAY ON Experiment(Assay_ID)
;
-- 
-- INDEX: FK_PROJECT_EXPERIMENT 
--

CREATE INDEX FK_PROJECT_EXPERIMENT ON Experiment(Project_ID)
;
-- 
-- INDEX: FK_EXPERIMENT_EXPRT_STATUS 
--

CREATE INDEX FK_EXPERIMENT_EXPRT_STATUS ON Experiment(Experiment_Status_ID)
;
-- 
-- INDEX: FK_EXPERIMENT_SOURCE_LAB 
--

CREATE INDEX FK_EXPERIMENT_SOURCE_LAB ON Experiment(Source_ID)
;
-- 
-- INDEX: FK_EXT_ASSAY_ASSAY 
--

CREATE INDEX FK_EXT_ASSAY_ASSAY ON External_Assay(Assay_ID)
;
-- 
-- INDEX: `FK_EXT_ASSAY_EXT_SYSTEM` 
--

CREATE INDEX `FK_EXT_ASSAY_EXT_SYSTEM` ON External_Assay(External_System_ID)
;
-- 
-- INDEX: FK_MEASURE_ASSAY 
--

CREATE INDEX FK_MEASURE_ASSAY ON Measure(Assay_ID)
;
-- 
-- INDEX: FK_MEASURE_RESULT_TYPE 
--

CREATE INDEX FK_MEASURE_RESULT_TYPE ON Measure(Result_Type_ID)
;
-- 
-- INDEX: FK_MEASURE_UNIT 
--

CREATE INDEX FK_MEASURE_UNIT ON Measure(Entry_Unit)
;
-- 
-- INDEX: FK_MEASURE_M_CONTEXT_ITEM 
--

CREATE INDEX FK_MEASURE_M_CONTEXT_ITEM ON Measure(Measure_Context_ID)
;
-- 
-- INDEX: AK_Measure_Context_item 
--

CREATE UNIQUE INDEX AK_Measure_Context_item ON Measure_Context_Item(Measure_Context_ID, Group_No, Attribute_ID, Value_Display)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_M_CONTEXT 
--

CREATE INDEX FK_M_CONTEXT_ITEM_M_CONTEXT ON Measure_Context_Item(Measure_Context_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX FK_M_CONTEXT_ITEM_ATTRIBUTE ON Measure_Context_Item(Attribute_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_VALUE 
--

CREATE INDEX FK_M_CONTEXT_ITEM_VALUE ON Measure_Context_Item(Value_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_ASSAY 
--

CREATE INDEX FK_M_CONTEXT_ITEM_ASSAY ON Measure_Context_Item(Assay_ID)
;
-- 
-- INDEX: FK_M_CONTEXT_ITEM_QUALIFIER 
--

CREATE INDEX FK_M_CONTEXT_ITEM_QUALIFIER ON Measure_Context_Item(Qualifier)
;
-- 
-- INDEX: FK_ONTOLOGY_ITEM_ONTOLOGY 
--

CREATE INDEX FK_ONTOLOGY_ITEM_ONTOLOGY ON Ontology_Item(Ontology_ID)
;
-- 
-- INDEX: FK_ONTOLOGY_ITEM_ELEMENT 
--

CREATE INDEX FK_ONTOLOGY_ITEM_ELEMENT ON Ontology_Item(Element_ID)
;
-- 
-- INDEX: FK_ONTOLOGY_ITEM_RESULT_TYPE 
--

CREATE INDEX FK_ONTOLOGY_ITEM_RESULT_TYPE ON Ontology_Item(Result_Type_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_ASSAY 
--

CREATE INDEX FK_PROJECT_ASSAY_ASSAY ON Project_Assay(Assay_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_PROJECT 
--

CREATE INDEX FK_PROJECT_ASSAY_PROJECT ON Project_Assay(Project_ID)
;
-- 
-- INDEX: FK_PROJECT_ASSAY_STAGE 
--

CREATE INDEX FK_PROJECT_ASSAY_STAGE ON Project_Assay(STAGE)
;
-- 
-- INDEX: FK_PROTOCOL_ASSAY 
--

CREATE INDEX FK_PROTOCOL_ASSAY ON Protocol(Assay_ID)
;
-- 
-- INDEX: FK_RESULT_RESULT_STATUS 
--

CREATE INDEX FK_RESULT_RESULT_STATUS ON Result(Result_Status_ID)
;
-- 
-- INDEX: FK_RESULT_EXPERIMENT 
--

CREATE INDEX FK_RESULT_EXPERIMENT ON Result(Experiment_ID)
;
-- 
-- INDEX: FK_RESULT_RESULT_CONTEXT 
--

CREATE INDEX FK_RESULT_RESULT_CONTEXT ON Result(Result_Context_ID)
;
-- 
-- INDEX: FK_RESULT_SUBSTANCE 
--

CREATE INDEX FK_RESULT_SUBSTANCE ON Result(Substance_ID)
;
-- 
-- INDEX: FK_RESULT_UNIT 
--

CREATE INDEX FK_RESULT_UNIT ON Result(Entry_Unit)
;
-- 
-- INDEX: FK_RESULT_RESULT_TYPE 
--

CREATE INDEX FK_RESULT_RESULT_TYPE ON Result(Result_Type_ID)
;
-- 
-- INDEX: FK_RESULT_QUALIFIER 
--

CREATE INDEX FK_RESULT_QUALIFIER ON Result(Qualifier)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_EXPERIMENT 
--

CREATE INDEX FK_R_CONTEXT_ITEM_EXPERIMENT ON Result_Context_Item(Experiment_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_R_CONTEXT 
--

CREATE INDEX FK_R_CONTEXT_ITEM_R_CONTEXT ON Result_Context_Item(Result_Context_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_ATTRIBUTE 
--

CREATE INDEX FK_R_CONTEXT_ITEM_ATTRIBUTE ON Result_Context_Item(Attribute_ID)
;
-- 
-- INDEX: FK_R_CONTEXT_ITEM_VALUE 
--

CREATE INDEX FK_R_CONTEXT_ITEM_VALUE ON Result_Context_Item(Value_ID)
;
-- 
-- INDEX: FK_RESULT_HIERARCHY_RSLT_PRNT 
--

CREATE INDEX FK_RESULT_HIERARCHY_RSLT_PRNT ON Result_Hierarchy(Result_ID)
;
-- 
-- INDEX: FK_RESULT_HIERARCHY_RESULT 
--

CREATE INDEX FK_RESULT_HIERARCHY_RESULT ON Result_Hierarchy(Parent_Result_ID)
;
-- 
-- INDEX: FK_RESULT_TYPE_ELEMENT_STATUS 
--

CREATE INDEX FK_RESULT_TYPE_ELEMENT_STATUS ON Result_Type(Result_Type_Status_ID)
;
-- 
-- INDEX: FK_RESULT_TYPE_UNIT 
--

CREATE INDEX FK_RESULT_TYPE_UNIT ON Result_Type(Base_Unit)
;
-- 
-- INDEX: FK_RESULT_TYPE_RSLT_TYP_PRNT 
--

CREATE INDEX FK_RESULT_TYPE_RSLT_TYP_PRNT ON Result_Type(Parent_Result_type_ID)
;
-- 
-- INDEX: FK_UNIT_CONVERSION_FROM_UNIT 
--

CREATE INDEX FK_UNIT_CONVERSION_FROM_UNIT ON Unit_Conversion(From_Unit)
;
-- 
-- INDEX: FK_UNIT_CONVERSION_TO_UNIT 
--

CREATE INDEX FK_UNIT_CONVERSION_TO_UNIT ON Unit_Conversion(To_Unit)
;
-- 
-- TABLE: Assay 
--

ALTER TABLE Assay ADD CONSTRAINT FK_assaY_assay_status_id 
    FOREIGN KEY (Assay_status_ID)
    REFERENCES Assay_Status(Assay_status_ID)
;


-- 
-- TABLE: Element 
--

ALTER TABLE Element ADD CONSTRAINT FK_Element_element_status 
    FOREIGN KEY (Element_Status_ID)
    REFERENCES Element_Status(Element_Status_ID)
;

ALTER TABLE Element ADD CONSTRAINT FK_element_parent_element 
    FOREIGN KEY (Parent_Element_ID)
    REFERENCES Element(Element_ID)
;

ALTER TABLE Element ADD CONSTRAINT FK_Element_Unit 
    FOREIGN KEY (Unit)
    REFERENCES Unit(Unit)
;


-- 
-- TABLE: Experiment 
--

ALTER TABLE Experiment ADD CONSTRAINT FK_experiment_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;

ALTER TABLE Experiment ADD CONSTRAINT FK_experiment_exprt_status 
    FOREIGN KEY (Experiment_Status_ID)
    REFERENCES Experiment_Status(Experiment_Status_ID)
;

ALTER TABLE Experiment ADD CONSTRAINT FK_Experiment_source_lab 
    FOREIGN KEY (Source_ID)
    REFERENCES Laboratory(Lab_ID)
;

ALTER TABLE Experiment ADD CONSTRAINT FK_Project_experiment 
    FOREIGN KEY (Project_ID)
    REFERENCES Project(Project_ID)
;


-- 
-- TABLE: External_Assay 
--

ALTER TABLE External_Assay ADD CONSTRAINT fk_ext_assay_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;

ALTER TABLE External_Assay ADD CONSTRAINT FK_ext_assay_ext_system 
    FOREIGN KEY (External_System_ID)
    REFERENCES External_System(External_System_ID)
;


-- 
-- TABLE: Measure 
--

ALTER TABLE Measure ADD CONSTRAINT FK_Measure_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;

ALTER TABLE Measure ADD CONSTRAINT FK_measure_M_context_Item 
    FOREIGN KEY (Measure_Context_ID)
    REFERENCES Measure_Context(Measure_Context_ID)
;

ALTER TABLE Measure ADD CONSTRAINT FK_Measure_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES Result_Type(Result_Type_ID)
;

ALTER TABLE Measure ADD CONSTRAINT FK_Measure_Unit 
    FOREIGN KEY (Entry_Unit)
    REFERENCES Unit(Unit)
;


-- 
-- TABLE: Measure_Context_Item 
--

ALTER TABLE Measure_Context_Item ADD CONSTRAINT FK_M_Context_item_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;

ALTER TABLE Measure_Context_Item ADD CONSTRAINT FK_M_context_item_attribute 
    FOREIGN KEY (Attribute_ID)
    REFERENCES Element(Element_ID)
;

ALTER TABLE Measure_Context_Item ADD CONSTRAINT FK_M_context_Item_M_Context 
    FOREIGN KEY (Measure_Context_ID)
    REFERENCES Measure_Context(Measure_Context_ID)
;

ALTER TABLE Measure_Context_Item ADD CONSTRAINT FK_M_context_item_qualifier 
    FOREIGN KEY (Qualifier)
    REFERENCES Qualifier(Qualifier)
;

ALTER TABLE Measure_Context_Item ADD CONSTRAINT fk_M_context_item_value 
    FOREIGN KEY (Value_ID)
    REFERENCES Element(Element_ID)
;


-- 
-- TABLE: Ontology_Item 
--

ALTER TABLE Ontology_Item ADD CONSTRAINT FK_ontology_item_element 
    FOREIGN KEY (Element_ID)
    REFERENCES Element(Element_ID)
;

ALTER TABLE Ontology_Item ADD CONSTRAINT FK_ontology_item_Ontology 
    FOREIGN KEY (Ontology_ID)
    REFERENCES Ontology(Ontology_ID)
;

ALTER TABLE Ontology_Item ADD CONSTRAINT FK_Ontology_item_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES Result_Type(Result_Type_ID)
;


-- 
-- TABLE: Project_Assay 
--

ALTER TABLE Project_Assay ADD CONSTRAINT FK_Project_assay_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;

ALTER TABLE Project_Assay ADD CONSTRAINT FK_project_assay_project 
    FOREIGN KEY (Project_ID)
    REFERENCES Project(Project_ID)
;


-- 
-- TABLE: Protocol 
--

ALTER TABLE Protocol ADD CONSTRAINT FK_Protocol_assay 
    FOREIGN KEY (Assay_ID)
    REFERENCES Assay(Assay_ID)
;


-- 
-- TABLE: Result 
--

ALTER TABLE Result ADD CONSTRAINT FK_result_experiment 
    FOREIGN KEY (Experiment_ID)
    REFERENCES Experiment(Experiment_ID)
;

ALTER TABLE Result ADD CONSTRAINT FK_Result_Qualifier 
    FOREIGN KEY (Qualifier)
    REFERENCES Qualifier(Qualifier)
;

ALTER TABLE Result ADD CONSTRAINT fk_result_result_context 
    FOREIGN KEY (Result_Context_ID)
    REFERENCES Result_Context(Result_Context_ID)
;

ALTER TABLE Result ADD CONSTRAINT FK_result_result_status 
    FOREIGN KEY (Result_Status_ID)
    REFERENCES Result_Status(Result_Status_ID)
;

ALTER TABLE Result ADD CONSTRAINT FK_Result_result_type 
    FOREIGN KEY (Result_Type_ID)
    REFERENCES Result_Type(Result_Type_ID)
;

ALTER TABLE Result ADD CONSTRAINT FK_Result_substance 
    FOREIGN KEY (Substance_ID)
    REFERENCES Substance(Substance_ID)
;

ALTER TABLE Result ADD CONSTRAINT FK_result_unit 
    FOREIGN KEY (Entry_Unit)
    REFERENCES Unit(Unit)
;


-- 
-- TABLE: Result_Context_Item 
--

ALTER TABLE Result_Context_Item ADD CONSTRAINT FK_R_context_item_attribute 
    FOREIGN KEY (Attribute_ID)
    REFERENCES Element(Element_ID)
;

ALTER TABLE Result_Context_Item ADD CONSTRAINT fk_R_Context_item_experiment 
    FOREIGN KEY (Experiment_ID)
    REFERENCES Experiment(Experiment_ID)
;

ALTER TABLE Result_Context_Item ADD CONSTRAINT FK_R_context_item_qualifier 
    FOREIGN KEY (Qualifier)
    REFERENCES Qualifier(Qualifier)
;

ALTER TABLE Result_Context_Item ADD CONSTRAINT FK_R_context_item_R_context 
    FOREIGN KEY (Result_Context_ID)
    REFERENCES Result_Context(Result_Context_ID)
;

ALTER TABLE Result_Context_Item ADD CONSTRAINT FK_R_Context_item_value 
    FOREIGN KEY (Value_ID)
    REFERENCES Element(Element_ID)
;


-- 
-- TABLE: Result_Hierarchy 
--

ALTER TABLE Result_Hierarchy ADD CONSTRAINT FK_result_hierarchy_result 
    FOREIGN KEY (Parent_Result_ID)
    REFERENCES Result(Result_ID)
;

ALTER TABLE Result_Hierarchy ADD CONSTRAINT FK_result_hierarchy_rslt_Prnt 
    FOREIGN KEY (Result_ID)
    REFERENCES Result(Result_ID)
;


-- 
-- TABLE: Result_Type 
--

ALTER TABLE Result_Type ADD CONSTRAINT FK_result_type_element_status 
    FOREIGN KEY (Result_Type_Status_ID)
    REFERENCES Element_Status(Element_Status_ID)
;

ALTER TABLE Result_Type ADD CONSTRAINT FK_result_type_rslt_typ_prnt 
    FOREIGN KEY (Parent_Result_type_ID)
    REFERENCES Result_Type(Result_Type_ID)
;

ALTER TABLE Result_Type ADD CONSTRAINT FK_result_type_unit 
    FOREIGN KEY (Base_Unit)
    REFERENCES Unit(Unit)
;


-- 
-- TABLE: Unit_Conversion 
--

ALTER TABLE Unit_Conversion ADD CONSTRAINT FK_Unit_conversion_from_Unit 
    FOREIGN KEY (From_Unit)
    REFERENCES Unit(Unit)
;

ALTER TABLE Unit_Conversion ADD CONSTRAINT FK_Unit_conversion_to_unit 
    FOREIGN KEY (To_Unit)
    REFERENCES Unit(Unit)
;


