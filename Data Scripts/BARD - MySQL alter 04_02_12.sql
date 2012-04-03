--
-- AUDIT COLUMNS TO ALL TABLES
--

-- 04-02-12
-- SJ Chatwin

alter table MLBD.Assay 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Assay_Status 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Element 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Element_Status 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Experiment DROP RUN_DATE
;


alter table MLBD.Experiment 
add RUN_DATE_FROM DATE null,
add run_date_to DATE NULL,
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Experiment_Status 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.External_Assay 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.External_System 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Laboratory 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Measure 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Measure_Context 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Measure_Context_Item 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Ontology 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Ontology_Item 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Project 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Project_Assay 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Protocol 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Qualifier 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result_Context 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result_Context_Item 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result_Hierarchy 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result_Status 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Result_Type 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Stage 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Substance 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Unit 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

alter table MLBD.Unit_Conadd version 
add version INTEGER NOT NULL DEFAULT 0 COMMENT 'add version is used by Hibernate to resolve the lost Update problem when used in optimistic locking', 
add Date_Created TIMESTAMP DEFAULT current_timestamp NOT NULL, 
add Last_Modified DATETIME NULL, 
add modified_by VARCHAR(40) NULL
;

