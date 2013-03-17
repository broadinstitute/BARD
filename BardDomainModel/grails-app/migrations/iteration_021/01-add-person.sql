ALTER TABLE FAVORITE DROP CONSTRAINT FK_FAVORITE_PERSON
;
ALTER TABLE PERSON_ROLE DROP CONSTRAINT FK_PERSON_ROLE_PERSON
;
ALTER TABLE RSLT_CONTEXT_ITEM DROP CONSTRAINT FK_R_CONTEXT_ITEM_RESULT
;
ALTER TABLE TEAM_MEMBER DROP CONSTRAINT FK_TEAM_MEMBER_TEAM
;
DROP INDEX AK_PERSON
;
ALTER TABLE PERSON DROP CONSTRAINT pk_person
;
ALTER TABLE PERSON DROP CONSTRAINT ck_person_username
;
ALTER TABLE PERSON RENAME TO PERSON_03072013221721000
;
CREATE TABLE PERSON
(
    PERSON_ID       NUMBER(19)    NOT NULL,
    USERNAME        VARCHAR2(255) NOT NULL,
    EMAIL_ADDRESS   VARCHAR2(255)     NULL,
    FULL_NAME       VARCHAR2(255)     NULL,
    ACCOUNT_EXPIRED NUMBER(1)     DEFAULT 0 NOT NULL,
    ACCOUNT_LOCKED  NUMBER(1)     DEFAULT 0 NOT NULL,
    ACCOUNT_ENABLED NUMBER(1)     DEFAULT 1 NOT NULL,
    VERSION         NUMBER(5)     DEFAULT 0 NOT NULL,
    DATE_CREATED    DATE          DEFAULT sysdate     NULL,
    LAST_UPDATED    DATE              NULL,
    MODIFIED_BY     VARCHAR2(40)      NULL
)
;
ALTER TABLE TEAM DROP PRIMARY KEY DROP INDEX
;
DROP INDEX AK_TEAM
;
ALTER TABLE TEAM RENAME TO TEAM_03072013221816000
;
CREATE TABLE TEAM
(
    TEAM_ID      NUMBER(19)    NOT NULL,
    TEAM_NAME    VARCHAR2(100) NOT NULL,
    LOCATION     VARCHAR2(255)     NULL,
    VERSION      NUMBER(5)     DEFAULT 0 NOT NULL,
    DATE_CREATED DATE          DEFAULT sysdate     NULL,
    LAST_UPDATED DATE              NULL,
    MODIFIED_BY  VARCHAR2(40)      NULL
)
;
ALTER SESSION ENABLE PARALLEL DML
;
INSERT INTO PERSON(
          PERSON_ID,
          USERNAME,
          EMAIL_ADDRESS,
          FULL_NAME,
          ACCOUNT_EXPIRED,
          ACCOUNT_LOCKED,
          ACCOUNT_ENABLED,
          VERSION,
          DATE_CREATED,
          LAST_UPDATED,
          MODIFIED_BY
          )
    SELECT
          PERSON_ID,
          NVL(USERNAME,' '),
          NULL,
          NULL,
          ACCOUNT_EXPIRED,
          ACCOUNT_LOCKED,
          ACCOUNT_ENABLED,
          VERSION,
          DATE_CREATED,
          LAST_UPDATED,
          MODIFIED_BY
      FROM PERSON_03072013221721000
;
COMMIT
;
ALTER TABLE PERSON LOGGING
;
ALTER SESSION ENABLE PARALLEL DML
;
INSERT INTO TEAM(
            TEAM_ID,
            TEAM_NAME,
            LOCATION,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
            )
      SELECT
            TEAM_ID,
            SUBSTR(TEAM_NAME, 1, 100),
            LOCATION,
            VERSION,
            DATE_CREATED,
            LAST_UPDATED,
            MODIFIED_BY
        FROM TEAM_03072013221816000
;
COMMIT
;
ALTER TABLE TEAM LOGGING
;
CREATE UNIQUE INDEX AK_PERSON
    ON PERSON(USERNAME)
;
CREATE UNIQUE INDEX AK_TEAM
    ON TEAM(TEAM_NAME)
;
ALTER TABLE PERSON ADD CONSTRAINT PK_PERSON
PRIMARY KEY (PERSON_ID)
;
ALTER TABLE PERSON ADD CONSTRAINT CK_PERSON_EMAIL
CHECK (email_address like '%@%.%')
;
ALTER TABLE TEAM ADD CONSTRAINT PK_TEAM
PRIMARY KEY (TEAM_ID)
;
ALTER TABLE FAVORITE ADD CONSTRAINT FK_FAVORITE_PERSON
FOREIGN KEY (PERSON_ID)
REFERENCES PERSON (PERSON_ID)
ENABLE VALIDATE
;
ALTER TABLE PERSON_ROLE ADD CONSTRAINT FK_PERSON_ROLE_PERSON
FOREIGN KEY (PERSON_ID)
REFERENCES PERSON (PERSON_ID)
ENABLE VALIDATE
;
ALTER TABLE TEAM_MEMBER ADD CONSTRAINT FK_TEAM_MEMBER_TEAM
FOREIGN KEY (TEAM_ID)
REFERENCES TEAM (TEAM_ID)
ENABLE VALIDATE
;