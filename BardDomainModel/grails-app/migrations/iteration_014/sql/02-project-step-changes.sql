
-- Sequence Alter SQL

CREATE SEQUENCE EXPRMT_MEASURE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;
CREATE SEQUENCE PROJECT_EXPERIMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

CREATE SEQUENCE PRJCT_EXPRMT_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

CREATE SEQUENCE PRJCT_EXPRMT_CNTXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

-- Drop Referencing Constraint SQL

ALTER TABLE STEP_CONTEXT DROP CONSTRAINT FK_STEP_CNTXT_STEP
;

-- Drop Constraint, Rename and Create Table SQL

ALTER TABLE PROJECT_STEP DROP CONSTRAINT FK_PROJECT_STEP_EXPERIMENT
;
ALTER TABLE PROJECT_STEP DROP CONSTRAINT FK_PROJECT_STEP_FLLWS_EXPRMNT
;
ALTER TABLE PROJECT_STEP DROP CONSTRAINT FK_PROJECT_STEP_PROJECT
;
ALTER TABLE PROJECT_STEP DROP PRIMARY KEY DROP INDEX
;
DROP INDEX FK_PROJECT_STEP_PROJECT
;
DROP INDEX FK_PROJECT_STEP_EXPERIMENT
;
DROP INDEX FK_PROJECT_STEP_FLLWS_EXPRMNT
;
ALTER TABLE PROJECT_STEP RENAME TO PROJECT_ST_11292012183435000
;
CREATE TABLE PROJECT_STEP
(
    PROJECT_STEP_ID            NUMBER(19)    NOT NULL,
    VERSION                    NUMBER(38)    DEFAULT 0 NOT NULL,
    NEXT_PROJECT_EXPERIMENT_ID NUMBER(19)  NOT NULL,
    PREV_PROJECT_EXPERIMENT_ID NUMBER(19)  NOT NULL,
    DATE_CREATED               TIMESTAMP(6)  DEFAULT sysdate NOT NULL,
    EDGE_NAME                  VARCHAR2(128)     NULL,
    LAST_UPDATED               TIMESTAMP(6)      NULL,
    MODIFIED_BY                VARCHAR2(40)      NULL
)
;
COMMENT ON TABLE PROJECT_STEP IS
'The annotations (context items) for a step show the annotations of the incoming arrowhead on the edge between the experiments and the "follows" experiment.  Thus the annotations can refer to the experiment or the step (aka decision) that instigated the experiment.  Bear in mind that the "follows" experiment can be null, i.e. this the experiment does not have a predecessor.  Primary screens tend to have this characteristic.'
;
CREATE TABLE EXPRMT_MEASURE
(
    EXPRMT_MEASURE_ID         NUMBER(19)   NOT NULL,
    PARENT_EXPRMT_MEASURE_ID  NUMBER(19)       NULL,
    PARENT_CHILD_RELATIONSHIP VARCHAR2(20)     NULL,
    EXPERIMENT_ID             NUMBER(19)   NOT NULL,
    MEASURE_ID                NUMBER(19)   NOT NULL,
    VERSION                   NUMBER(38)   DEFAULT 0 NOT NULL,
    DATE_CREATED              TIMESTAMP(6) DEFAULT sysdate NOT NULL,
    LAST_UPDATED              TIMESTAMP(6)     NULL,
    MODIFIED_BY               VARCHAR2(40)     NULL
)
;
ALTER TABLE EXPRMT_MEASURE
    ADD CONSTRAINT PK_EXPRMT_MEASURE
    PRIMARY KEY (EXPRMT_MEASURE_ID)
    USING INDEX STORAGE(BUFFER_POOL DEFAULT)
    ENABLE
    VALIDATE
;
ALTER TABLE EXPRMT_MEASURE
    ADD CONSTRAINT CK_EXPRMT_MEASURE_RELATIONSHIP
CHECK (Parent_Child_Relationship in('Derived from', 'has Child', 'has Sibling'))
;
CREATE TABLE PRJCT_EXPRMT_CONTEXT
(
    PRJCT_EXPRMT_CONTEXT_ID NUMBER(19)    NOT NULL,
    PROJECT_EXPERIMENT_ID   NUMBER(19)  NOT NULL,
    CONTEXT_NAME            VARCHAR2(128)     NULL,
    CONTEXT_GROUP           VARCHAR2(256)     NULL,
    DISPLAY_ORDER           NUMBER(5)     DEFAULT 0 NOT NULL,
    VERSION                 NUMBER(38)    DEFAULT 0 NOT NULL,
    DATE_CREATED            TIMESTAMP(6)  DEFAULT sysdate NOT NULL,
    LAST_UPDATED            TIMESTAMP(6)      NULL,
    MODIFIED_BY             VARCHAR2(40)      NULL
)
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT
    ADD CONSTRAINT PK_PRJCT_EXPRMT_CONTEXT
    PRIMARY KEY (PRJCT_EXPRMT_CONTEXT_ID)
    USING INDEX STORAGE(BUFFER_POOL DEFAULT)
    ENABLE
    VALIDATE
;
COMMENT ON COLUMN PRJCT_EXPRMT_CONTEXT.CONTEXT_NAME IS
'used as a title for the cards in the UI'
;
CREATE TABLE PRJCT_EXPRMT_CONTEXT_ITEM
(
    PRJCT_EXPRMT_CONTEXT_ITEM_ID NUMBER(19)    NOT NULL,
    PRJCT_EXPRMT_CONTEXT_ID      NUMBER(19)    NOT NULL,
    DISPLAY_ORDER                NUMBER(5)     NOT NULL,
    ATTRIBUTE_ID                 NUMBER(19)    NOT NULL,
    VALUE_ID                     NUMBER(19)        NULL,
    EXT_VALUE_ID                 VARCHAR2(60)      NULL,
    QUALIFIER                    CHAR(2)           NULL,
    VALUE_NUM                    NUMBER(30,15)     NULL,
    VALUE_MIN                    NUMBER(30,15)     NULL,
    VALUE_MAX                    NUMBER(30,15)     NULL,
    VALUE_DISPLAY                VARCHAR2(500)     NULL,
    VERSION                      NUMBER(38)    DEFAULT 0 NOT NULL,
    DATE_CREATED                 TIMESTAMP(6)  DEFAULT sysdate NOT NULL,
    LAST_UPDATED                 TIMESTAMP(6)      NULL,
    MODIFIED_BY                  VARCHAR2(40)      NULL
)
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT_ITEM
    ADD CONSTRAINT PK_PRJCT_EXPRMT_CONTEXT_ITEM
    PRIMARY KEY (PRJCT_EXPRMT_CONTEXT_ITEM_ID)
    USING INDEX STORAGE(BUFFER_POOL DEFAULT)
    ENABLE
    VALIDATE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT_ITEM
    ADD CONSTRAINT CK_PRJCT_EXPRMT_ITEM_QUALIFIER
CHECK (Qualifier IN ('= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '))
;
COMMENT ON COLUMN PRJCT_EXPRMT_CONTEXT_ITEM.VALUE_DISPLAY IS
'This is not a general text entry field, rather it is an easily displayable text version of the other value columns'
;
CREATE TABLE PROJECT_EXPERIMENT
(
    PROJECT_EXPERIMENT_ID NUMBER(19) NOT NULL,
    EXPERIMENT_ID         NUMBER(19)   NOT NULL,
    PROJECT_ID            NUMBER(19)   NOT NULL,
    STAGE_ID              NUMBER(19)       NULL,
    VERSION               NUMBER(38)   DEFAULT 0 NOT NULL,
    DATE_CREATED          TIMESTAMP(6) DEFAULT sysdate NOT NULL,
    LAST_UPDATED          TIMESTAMP(6)     NULL,
    MODIFIED_BY           VARCHAR2(40)     NULL
)
;
ALTER TABLE PROJECT_EXPERIMENT
    ADD CONSTRAINT PK_PROJECT_EXPERIMENT
    PRIMARY KEY (PROJECT_EXPERIMENT_ID)
    USING INDEX STORAGE(BUFFER_POOL DEFAULT)
    ENABLE
    VALIDATE
;

-- Insert Data SQL

ALTER SESSION ENABLE PARALLEL DML
;
------------------------------------------------------------------------------------------
-- hand crafted SQL to transfer from old project_step into project experiment
-- guaranteed to work by SJC
-----------------------------------------------------------------------------------------
INSERT INTO project_experiment
                   (PROJECT_EXPERIMENT_ID ,
                    EXPERIMENT_ID,
                    PROJECT_ID,
                    STAGE_ID,
                    VERSION,
                    DATE_CREATED,
                    LAST_UPDATED,
                    MODIFIED_BY)
            SELECT PROJECT_EXPERIMENT_ID_SEQ.NEXTVAL,
                   PS.EXPERIMENT_ID,
                   PS.PROJECT_ID,
                   (SELECT VALUE_ID
                      FROM STEP_CONTEXT_ITEM SCI,
                           STEP_CONTEXT SC,
                           PROJECT_ST_11292012183435000 PS2
                     WHERE SC.STEP_CONTEXT_ID = SCI.STEP_CONTEXT_ITEM_ID
                       AND PS2.PROJECT_STEP_ID = SC.PROJECT_STEP_ID
                       AND PS2.PROJECT_ID = PS.PROJECT_ID
                       AND PS2.EXPERIMENT_ID = PS.EXPERIMENT_ID
                       AND ATTRIBUTE_ID = 556  -- Only assay stage items transferred
                       AND ROWNUM = 1) STAGE_ID,
                   PS.VERSION,
                   PS.DATE_CREATED,
                   PS.LAST_UPDATED,
                   SubStr(PS.MODIFIED_BY, 1, 40)
            FROM (SELECT EXPERIMENT_ID,
                        PROJECT_ID,
                        Max(VERSION) VERSION,
                        Min(DATE_CREATED) DATE_CREATED,
                        Max(LAST_UPDATED) LAST_UPDATED,
                        LISTAGG(MODIFIED_BY, ',') WITHIN GROUP (ORDER BY PROJECT_STEP_ID) MODIFIED_BY
                  FROM PROJECT_ST_11292012183435000
                  GROUP BY EXPERIMENT_ID,
                        PROJECT_ID) PS
;
--------------------------------------------------------------------------------------------------
-- WE GOT ALL THE ANNOTATIONS, SO DELETE THEM FROM THEIR SOURCE TABLES
-- OTHERWISE THEY MESS WITH THE fk CONSTRAINTS FURTHER ON
-----------------------------------------------------------------------------------------------
DELETE FROM STEP_CONTEXT_ITEM
;
DELETE FROM STEP_CONTEXT
;
ALTER TABLE PROJECT_experiment LOGGING
;
------------------------------------------------------------
-- there is no data to load into the new project step!!
------------------------------------------------------------
ALTER TABLE PROJECT_STEP LOGGING
;

-- Add Constraint SQL

ALTER TABLE PROJECT_STEP ADD CONSTRAINT PK_PROJECT_STEP
PRIMARY KEY (PROJECT_STEP_ID)
;

-- Alter Index SQL

CREATE INDEX FK_PRJCT_STEP_NXT_PRJCT_EXPRMT
    ON PROJECT_STEP(NEXT_PROJECT_EXPERIMENT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_STEP_PRV_PRJCT_EXPRMT
    ON PROJECT_STEP(PREV_PROJECT_EXPERIMENT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_EXPRMT_MEASURE_PARENT
    ON EXPRMT_MEASURE(PARENT_CHILD_RELATIONSHIP)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_EXPRMT_MEASURE_EXPRMT
    ON EXPRMT_MEASURE(EXPERIMENT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_EXPRMT_MEASURE_MEASURE
    ON EXPRMT_MEASURE(MEASURE_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJ_EXPRMT_CNTXT_PRJ_EXPRMT
    ON PRJCT_EXPRMT_CONTEXT(PROJECT_EXPERIMENT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_CNTXT_ITM_ATTR
    ON PRJCT_EXPRMT_CONTEXT_ITEM(ATTRIBUTE_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_CNTXT_ITM_VAL
    ON PRJCT_EXPRMT_CONTEXT_ITEM(VALUE_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_CNTXT_ITM_CNTX
    ON PRJCT_EXPRMT_CONTEXT_ITEM(PRJCT_EXPRMT_CONTEXT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_EXPERIMENT
    ON PROJECT_EXPERIMENT(EXPERIMENT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_PROJECT
    ON PROJECT_EXPERIMENT(PROJECT_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;
CREATE INDEX FK_PRJCT_EXPRMT_STAGE
    ON PROJECT_EXPERIMENT(STAGE_ID)
STORAGE(BUFFER_POOL DEFAULT)
NOPARALLEL
NOCOMPRESS
;

-- Add Referencing Foreign Keys SQL
DELETE FROM STEP_CONTEXT_ITEM
;
DELETE FROM STEP_CONTEXT
;

ALTER TABLE STEP_CONTEXT ADD CONSTRAINT FK_STEP_CNTXT_STEP
FOREIGN KEY (PROJECT_STEP_ID)
REFERENCES PROJECT_STEP (PROJECT_STEP_ID)
ENABLE VALIDATE
;
ALTER TABLE EXPRMT_MEASURE
    ADD CONSTRAINT FK_EXPRMT_MEASURE_PARENT
FOREIGN KEY (PARENT_EXPRMT_MEASURE_ID)
REFERENCES EXPRMT_MEASURE (EXPRMT_MEASURE_ID)
DEFERRABLE INITIALLY DEFERRED ENABLE VALIDATE
;
ALTER TABLE EXPRMT_MEASURE
    ADD CONSTRAINT FK_EXPRMT_MEASURE_EXPRMT
FOREIGN KEY (EXPERIMENT_ID)
REFERENCES EXPERIMENT (EXPERIMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE EXPRMT_MEASURE
    ADD CONSTRAINT FK_EXPRMT_MEASURE_MEASURE
FOREIGN KEY (MEASURE_ID)
REFERENCES MEASURE (MEASURE_ID)
ENABLE VALIDATE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT
    ADD CONSTRAINT FK_PRJ_EXPT_CNTXT_PRJ_EXPRMT
FOREIGN KEY (PROJECT_EXPERIMENT_ID)
REFERENCES PROJECT_EXPERIMENT (PROJECT_EXPERIMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT_ITEM
    ADD CONSTRAINT FK_PRJ_EXPT_CNTXT_ITM_VAL
FOREIGN KEY (VALUE_ID)
REFERENCES ELEMENT (ELEMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT_ITEM
    ADD CONSTRAINT FK_PRJ_EXPT_CNTXT_ITM_PRJ_EXPT
FOREIGN KEY (PRJCT_EXPRMT_CONTEXT_ID)
REFERENCES PRJCT_EXPRMT_CONTEXT (PRJCT_EXPRMT_CONTEXT_ID)
ENABLE VALIDATE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT_ITEM
    ADD CONSTRAINT FK_PRJ_EXPT_CNTXT_ITM_ATTR
FOREIGN KEY (ATTRIBUTE_ID)
REFERENCES ELEMENT (ELEMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PROJECT_EXPERIMENT
    ADD CONSTRAINT FK_PRJCT_EXPRMT_STAGE
FOREIGN KEY (STAGE_ID)
REFERENCES ELEMENT (ELEMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PROJECT_EXPERIMENT
    ADD CONSTRAINT FK_PRJCT_EXPRMT_EXPERIMENT
FOREIGN KEY (EXPERIMENT_ID)
REFERENCES EXPERIMENT (EXPERIMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PROJECT_EXPERIMENT
    ADD CONSTRAINT FK_PRJCT_EXPRMT_PROJECT
FOREIGN KEY (PROJECT_ID)
REFERENCES PROJECT (PROJECT_ID)
ENABLE VALIDATE
;
ALTER TABLE PROJECT_STEP ADD CONSTRAINT FK_PRJCT_STEP_NXT_PRJCT_EXPRMT
FOREIGN KEY (NEXT_PROJECT_EXPERIMENT_ID)
REFERENCES PROJECT_EXPERIMENT (PROJECT_EXPERIMENT_ID)
ENABLE VALIDATE
;
ALTER TABLE PROJECT_STEP ADD CONSTRAINT FK_PRJCT_STEP_PRV_PRJCT_EXPRMT
FOREIGN KEY (PREV_PROJECT_EXPERIMENT_ID)
REFERENCES PROJECT_EXPERIMENT (PROJECT_EXPERIMENT_ID)
ENABLE VALIDATE
;