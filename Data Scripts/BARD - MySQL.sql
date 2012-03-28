--
-- ER/Studio Data Architect 9.1 SQL Code Generation
-- Project :      CAP and Data entry.DM1
--
-- Date Created : Wednesday, March 21, 2012 23:16:25
-- Target DBMS : MySQL 5.x
--

-- 
-- TABLE: Assay 
--

CREATE TABLE Assay(
    Assay_ID           INT              AUTO_INCREMENT,
    Assay_Name         VARCHAR(128)     NOT NULL,
    Assay_status_ID    INT              NOT NULL,
    Version            VARCHAR(10)      DEFAULT 1 NOT NULL,
    Description        VARCHAR(1000),
    Designed_By        VARCHAR(100),
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
    PRIMARY KEY (Assay_status_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Element 
--

CREATE TABLE Element(
    Element_ID           INT              AUTO_INCREMENT,
    Parent_Element_ID    INT,
    Label                VARCHAR(128)     NOT NULL,
    Description          VARCHAR(1000)    NOT NULL,
    Abbreviation         VARCHAR(20),
    Acronym              VARCHAR(20),
    Synonyms             VARCHAR(1000),
    Element_Status_ID    INT              NOT NULL,
    Unit                 VARCHAR(100),
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
    PRIMARY KEY (Element_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Experiment 
--

CREATE TABLE Experiment(
    Experiment_ID           INT              AUTO_INCREMENT,
    Experiment_Name         VARCHAR(256)     NOT NULL,
    Assay_ID                INT              NOT NULL,
    Project_ID              INT,
    Experiment_Status_ID    INT              NOT NULL,
    Run_Date                DATETIME,
    Hold_Until_Date         DATE             
                            CHECK (Hold_Until_Date <= sysdate + 366),
    Description             VARCHAR(1000)    NOT NULL,
    Source_ID               INT              NOT NULL,
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
    PRIMARY KEY (Experiment_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: External_Assay 
--

CREATE TABLE External_Assay(
    External_System_ID    INT             NOT NULL,
    Assay_ID              INT             NOT NULL,
    Ext_Assay_ID          VARCHAR(128)    NOT NULL,
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
    Description     VARCHAR(1000)    NOT NULL,
    Location        VARCHAR(250),
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
    Attribute_Type             VARCHAR(10)     
                               CHECK (Attribute_Type in ('Fixed', 'Range', 'List', 'Number')),
    Attribute_ID               INT             NOT NULL,
    Value_ID                   INT,
    Qualifier                  CHAR(2)         
                               CHECK (Qualifier in ('=', '>', '<', '<=', '>=', '~')),
    Value_Display              VARCHAR(256),
    Value_num                  FLOAT(8, 0),
    Value_Min                  FLOAT(8, 0),
    value_Max                  FLOAT(8, 0),
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
    PRIMARY KEY (Ontology_ID)
)ENGINE=INNODB
COMMENT='an external ontology or dictionary or other source of reference data'
;

-- 
-- TABLE: Ontology_Item 
--

CREATE TABLE Ontology_Item(
    Ontology_Item_ID    INT         AUTO_INCREMENT,
    Ontology_ID         INT         NOT NULL,
    Element_ID          INT,
    Item_Reference      CHAR(10),
    Result_Type_ID      INT,
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
    Description     VARCHAR(1000)    NOT NULL,
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
    Stage                  VARCHAR(20)      NOT NULL,
    Sequence_no            INT,
    Promotion_Threshold    FLOAT(8, 0),
    Promotion_Criteria     VARCHAR(1000),
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
    PRIMARY KEY (Protocol_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Result 
--

CREATE TABLE Result(
    Result_ID            INT             AUTO_INCREMENT,
    Value_Display        VARCHAR(256),
    value_num            FLOAT(8, 0),
    value_min            FLOAT(8, 0),
    Value_Max            FLOAT(8, 0),
    Qualifier            CHAR(2)         
                         CHECK (Qualifier in ('=', '<', '>', '<=', '>=', '~')),
    Result_Status_ID     INT             NOT NULL,
    Experiment_ID        INT             NOT NULL,
    Substance_ID         INT             NOT NULL,
    Result_Context_ID    INT             NOT NULL,
    Entry_Unit           VARCHAR(100),
    Result_Type_ID       INT             NOT NULL,
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
    Qualifier                 CHAR(2)         
                              CHECK (Qualifier in ('=', '>', '<', '<=', '>=', '~')),
    Value_Display             VARCHAR(256),
    Value_num                 FLOAT(8, 0),
    Value_Min                 FLOAT(8, 0),
    value_Max                 FLOAT(8, 0),
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
    PRIMARY KEY (Result_Status_ID)
)ENGINE=INNODB
COMMENT=''
;

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
    PRIMARY KEY (Result_Type_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Stage 
--

CREATE TABLE Stage(
    Stage    VARCHAR(20)    NOT NULL,
    PRIMARY KEY (Stage)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Substance 
--

CREATE TABLE Substance(
    Substance_ID        INT               AUTO_INCREMENT,
    Compound_ID         INT,
    SMILES              VARCHAR(4000),
    Molecular_Weight    DECIMAL(10, 3),
    Substance_Type      VARCHAR(20)       NOT NULL,
    PRIMARY KEY (Substance_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Unit 
--

CREATE TABLE Unit(
    Unit           VARCHAR(100)     NOT NULL,
    Description    VARCHAR(1000)    NOT NULL,
    PRIMARY KEY (Unit)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: Unit_Conversion 
--

CREATE TABLE Unit_Conversion(
    From_Unit     VARCHAR(100)    NOT NULL,
    To_Unit       VARCHAR(100)    NOT NULL,
    Multiplier    FLOAT(8, 0),
    Offset        FLOAT(8, 0),
    Formula       VARCHAR(256),
    PRIMARY KEY (From_Unit, To_Unit)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- INDEX: FK_assaY_assay_status_id 
--

CREATE INDEX FK_assaY_assay_status_id ON Assay(Assay_status_ID)
;
-- 
-- INDEX: AK_Assay_Status 
--

CREATE UNIQUE INDEX AK_Assay_Status ON Assay_Status(Status)
;
-- 
-- INDEX: FK_Element_element_status 
--

CREATE INDEX FK_Element_element_status ON Element(Element_Status_ID)
;
-- 
-- INDEX: FK_Element_Unit 
--

CREATE INDEX FK_Element_Unit ON Element(Unit)
;
-- 
-- INDEX: FK_element_parent_element 
--

CREATE INDEX FK_element_parent_element ON Element(Parent_Element_ID)
;
-- 
-- INDEX: FK_experiment_assay 
--

CREATE INDEX FK_experiment_assay ON Experiment(Assay_ID)
;
-- 
-- INDEX: FK_Project_experiment 
--

CREATE INDEX FK_Project_experiment ON Experiment(Project_ID)
;
-- 
-- INDEX: FK_experiment_exprt_status 
--

CREATE INDEX FK_experiment_exprt_status ON Experiment(Experiment_Status_ID)
;
-- 
-- INDEX: FK_Experiment_source_lab 
--

CREATE INDEX FK_Experiment_source_lab ON Experiment(Source_ID)
;
-- 
-- INDEX: fk_ext_assay_assay 
--

CREATE INDEX fk_ext_assay_assay ON External_Assay(Assay_ID)
;
-- 
-- INDEX: `FK_ext_assay_ext_system` 
--

CREATE INDEX `FK_ext_assay_ext_system` ON External_Assay(External_System_ID)
;
-- 
-- INDEX: FK_Measure_assay 
--

CREATE INDEX FK_Measure_assay ON Measure(Assay_ID)
;
-- 
-- INDEX: FK_Measure_Unit 
--

CREATE INDEX FK_Measure_Unit ON Measure(Entry_Unit)
;
-- 
-- INDEX: FK_measure_M_context_Item 
--

CREATE INDEX FK_measure_M_context_Item ON Measure(Measure_Context_ID)
;
-- 
-- INDEX: FK_Measure_result_type 
--

CREATE INDEX FK_Measure_result_type ON Measure(Result_Type_ID)
;
-- 
-- INDEX: AK_Measure_Context_item 
--

CREATE UNIQUE INDEX AK_Measure_Context_item ON Measure_Context_Item(Measure_Context_ID, Group_No, Attribute_ID, Value_Display)
;
-- 
-- INDEX: FK_M_context_Item_M_Context 
--

CREATE INDEX FK_M_context_Item_M_Context ON Measure_Context_Item(Measure_Context_ID)
;
-- 
-- INDEX: FK_M_Context_item_assay 
--

CREATE INDEX FK_M_Context_item_assay ON Measure_Context_Item(Assay_ID)
;
-- 
-- INDEX: FK_M_context_item_attribute 
--

CREATE INDEX FK_M_context_item_attribute ON Measure_Context_Item(Attribute_ID)
;
-- 
-- INDEX: fk_M_context_item_value 
--

CREATE INDEX fk_M_context_item_value ON Measure_Context_Item(Value_ID)
;
-- 
-- INDEX: FK_ontology_item_Ontology 
--

CREATE INDEX FK_ontology_item_Ontology ON Ontology_Item(Ontology_ID)
;
-- 
-- INDEX: FK_ontology_item_element 
--

CREATE INDEX FK_ontology_item_element ON Ontology_Item(Element_ID)
;
-- 
-- INDEX: FK_Ontology_item_result_type 
--

CREATE INDEX FK_Ontology_item_result_type ON Ontology_Item(Result_Type_ID)
;
-- 
-- INDEX: FK_Project_assay_assay 
--

CREATE INDEX FK_Project_assay_assay ON Project_Assay(Assay_ID)
;
-- 
-- INDEX: FK_project_assay_project 
--

CREATE INDEX FK_project_assay_project ON Project_Assay(Project_ID)
;
-- 
-- INDEX: FK_Project_assay_stage 
--

CREATE INDEX FK_Project_assay_stage ON Project_Assay(Stage)
;
-- 
-- INDEX: FK_Protocol_assay 
--

CREATE INDEX FK_Protocol_assay ON Protocol(Assay_ID)
;
-- 
-- INDEX: FK_result_result_status 
--

CREATE INDEX FK_result_result_status ON Result(Result_Status_ID)
;
-- 
-- INDEX: FK_result_experiment 
--

CREATE INDEX FK_result_experiment ON Result(Experiment_ID)
;
-- 
-- INDEX: fk_result_result_context 
--

CREATE INDEX fk_result_result_context ON Result(Result_Context_ID)
;
-- 
-- INDEX: FK_Result_substance 
--

CREATE INDEX FK_Result_substance ON Result(Substance_ID)
;
-- 
-- INDEX: FK_result_unit 
--

CREATE INDEX FK_result_unit ON Result(Entry_Unit)
;
-- 
-- INDEX: FK_Result_result_type 
--

CREATE INDEX FK_Result_result_type ON Result(Result_Type_ID)
;
-- 
-- INDEX: AK_Measure_Context_item_1 
--

CREATE UNIQUE INDEX AK_Measure_Context_item_1 ON Result_Context_Item(Group_No, Attribute_ID, Value_Display)
;
-- 
-- INDEX: fk_R_Context_item_experiment 
--

CREATE INDEX fk_R_Context_item_experiment ON Result_Context_Item(Experiment_ID)
;
-- 
-- INDEX: FK_R_context_item_R_context 
--

CREATE INDEX FK_R_context_item_R_context ON Result_Context_Item(Result_Context_ID)
;
-- 
-- INDEX: FK_R_context_item_attribute 
--

CREATE INDEX FK_R_context_item_attribute ON Result_Context_Item(Attribute_ID)
;
-- 
-- INDEX: FK_R_Context_item_value 
--

CREATE INDEX FK_R_Context_item_value ON Result_Context_Item(Value_ID)
;
-- 
-- INDEX: FK_result_hierarchy_rslt_Prnt 
--

CREATE INDEX FK_result_hierarchy_rslt_Prnt ON Result_Hierarchy(Result_ID)
;
-- 
-- INDEX: FK_result_hierarchy_result 
--

CREATE INDEX FK_result_hierarchy_result ON Result_Hierarchy(Parent_Result_ID)
;
-- 
-- INDEX: FK_result_type_element_status 
--

CREATE INDEX FK_result_type_element_status ON Result_Type(Result_Type_Status_ID)
;
-- 
-- INDEX: FK_result_type_unit 
--

CREATE INDEX FK_result_type_unit ON Result_Type(Base_Unit)
;
-- 
-- INDEX: FK_result_type_rslt_typ_prnt 
--

CREATE INDEX FK_result_type_rslt_typ_prnt ON Result_Type(Parent_Result_type_ID)
;
-- 
-- INDEX: FK_Unit_conversion_from_Unit 
--

CREATE INDEX FK_Unit_conversion_from_Unit ON Unit_Conversion(From_Unit)
;
-- 
-- INDEX: FK_Unit_conversion_to_unit 
--

CREATE INDEX FK_Unit_conversion_to_unit ON Unit_Conversion(To_Unit)
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

ALTER TABLE External_Assay ADD CONSTRAINT FK_ext_assay_ext_system 
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

ALTER TABLE Project_Assay ADD CONSTRAINT FK_Project_assay_stage 
    FOREIGN KEY (Stage)
    REFERENCES Stage(Stage)
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


--
-- views
--

create view BARD.Assay as select * from MLBD.Assay;
create view BARD.assay_status as select * from MLBD.assay_status;
create view BARD.element as select * from MLBD.element;
create view BARD.element_status as select * from MLBD.element_status;
create view BARD.experiment as select * from MLBD.experiment;
create view BARD.experiment_status as select * from MLBD.experiment_status;
create view BARD.external_assay as select * from MLBD.external_assay;
create view BARD.external_system as select * from MLBD.external_system;
create view BARD.laboratory as select * from MLBD.laboratory;
create view BARD.measure as select * from MLBD.measure;
create view BARD.measure_context as select * from MLBD.measure_context;
create view BARD.measure_context_item as select * from MLBD.measure_context_item;
create view BARD.ontology as select * from MLBD.ontology;
create view BARD.ontology_item as select * from MLBD.ontology_item;
create view BARD.project as select * from MLBD.project;
create view BARD.project_assay as select * from MLBD.project_assay;
create view BARD.protocol as select * from MLBD.protocol;
create view BARD.result as select * from MLBD.result;
create view BARD.result_context as select * from MLBD.result_context;
create view BARD.result_context_item as select * from MLBD.result_context_item;
create view BARD.result_hierarchy as select * from MLBD.result_hierarchy;
create view BARD.result_status as select * from MLBD.result_status;
create view BARD.result_type as select * from MLBD.result_type;
create view BARD.stage as select * from MLBD.stage;
create view BARD.substance as select * from MLBD.substance;
create view BARD.unit as select * from MLBD.unit;
create view BARD.unit_conversion as select * from MLBD.unit_conversion;