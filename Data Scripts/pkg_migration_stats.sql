PROMPT CREATE OR REPLACE PACKAGE migration_stats
CREATE OR REPLACE package migration_stats
as

    type r_cursor is ref cursor;

-- primary entry points ----------------------------------------------------------
    PROCEDURE Generate_Migration_stats
        (avi_refresh IN VARCHAR2 DEFAULT 'Increment');

-- internal house-keeping ------------------------------------------------------
    procedure log_error
        (an_errnum   in  number,
         av_errmsg  in varchar2,
         av_location    in varchar2,
         av_comment in varchar2 default null);

    procedure log_statement
        (av_table   IN  varchar2,
         an_identifier  in number,
         av_action      in varchar2,
         av_statement   IN varchar2);

------------------------------------------------------------------------------------

end Migration_stats;
/

PROMPT CREATE OR REPLACE PACKAGE BODY migration_stats
CREATE OR REPLACE package body migration_stats
as
    ------package constants and variables
    -- pv_src_schema    varchar2(31) :=  user;
    -- for testing use this.  in general this should be run from the sandbox schema
    pv_src_schema    varchar2(31) :=  'bard_qa';
    --pv_src_schema    varchar2(31) :=  lower(user);
    --type r_cursor is ref cursor;
    pb_logging  boolean := true;
-----------------------------------------------------------------------------------------

    procedure log_error
        (an_errnum   in  number,
         av_errmsg  in varchar2,
         av_location    in varchar2,
         av_comment in varchar2 default null)
    as
    begin
        insert into error_log
           ( ERROR_LOG_ID,
             ERROR_DATE,
             procedure_name,
             ERR_NUM,
             ERR_MSG,
             ERR_COMMENT
           ) values (
             ERROR_LOG_ID_SEQ.NEXTVAL,
             sysdate,
             av_location,
             an_errnum,
             av_errmsg,
             av_comment
           );


    exception
        when others
        then
            null;
    end log_error;

    procedure log_statement
        (av_table   IN  varchar2,
         an_identifier  in number,
         av_action      in varchar2,
         av_statement   IN varchar2)
    as
    begin
        if pb_logging
        then
            insert into statement_log
                ( TABLE_NAME,
                  IDENTIFIER,
                  ACTION_DATE,
                  ACTION,
                  DATA_CLAUSE
                ) values (
                  av_table,
                  an_identifier,
                  sysdate,
                  av_action,
                  substr(av_statement, 1, 1000)
                );
         end if;

    exception
        when others
        then
            null;
    end log_statement;

-------------------------------------------------------------------------------------
-- added 9/5/12 -- schatwin
-- requires the table MIGRATION_ACTION, _EVENT, _DAY, _PERSON
------------------------------------------------------------------------------------
    PROCEDURE Generate_Migration_stats
        (avi_refresh IN VARCHAR2 DEFAULT 'Increment')
    AS
        CURSOR cur_action
        IS
        SELECT action_ref, count_sql
        FROM migration_action
        WHERE count_sql IS NOT NULL;

        --TYPE r_cursor IS REF CURSOR;
        TYPE r_event IS RECORD (aid migration_event.aid%type,
                        assay_id migration_event.assay_id%type,
                        experiment_id migration_event.experiment_id%type,
                        project_id migration_event.project_id%type,
                        event_count migration_event.event_count%type,
                        modified_by migration_person.person_name%type,
                        day_ref migration_event.day_ref%type);

        cur_event   r_cursor;
        lr_event    r_event;
        ln_action_ref NUMBER;
        ln_person_ref  NUMBER;
        lv_count_sql  VARCHAR2(32000);
        le_bad_parameter  EXCEPTION;
        ld_last_end_date  DATE;
        lv_modified_by  VARCHAR2(100) := ' ';

    BEGIN
        --  OUTLINE PSUEDO CODE -----------------------------------
        -- select from migration event to find the largest data so far
        --   hint: use the ref ID as its monotonically increasing with Date
        -- if the refresh is set, truncate the table (using execute immediate)
        --   and set the start date to null
        IF Lower(avi_refresh) = 'refresh'
        THEN
            EXECUTE IMMEDIATE 'truncate table migration_event';
            ld_last_end_date := To_Date('01/01/2000','MM/DD/YYYY');

        ELSIF Lower(avi_refresh) = 'increment'
        THEN
            SELECT migration_date
            INTO ld_last_end_date
            FROM migration_day md,
                (SELECT Max(day_ref) AS max_ref
                FROM migration_event) me
            WHERE md.day_ref = me.max_ref;

        ELSE
            RAISE le_bad_parameter;
        END IF;

        -- populate the migration_aid dimension with any new items
        INSERT INTO migration_aid
            (aid)
        SELECT DISTINCT ext_assay_ref
        FROM external_reference er
        WHERE NOT EXISTS
            (SELECT 1
            FROM migration_aid ma
            WHERE ma.aid = er.ext_assay_ref);

        -- get a cursor for the migration actions.  Each of these contains a SQL statement
        --   designed to extract data form the real tables and make it ready for insertion
        --   into MIGRATION_EVENT
        OPEN cur_action;
        LOOP
            FETCH cur_action INTO ln_action_ref, lv_count_sql;
            EXIT WHEN cur_action%NOTFOUND;
--            Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref));
            -- Open a cursor with the SQL using the start date as a parameter
            -- fetch into a standard rowtype for M_EVENT
            OPEN cur_event FOR lv_count_sql USING ld_last_end_date;
            LOOP
                FETCH cur_event INTO lr_event;
                EXIT WHEN cur_event%NOTFOUND;
--                Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                     || ' modified_by ' || lr_event.modified_by || ', '|| lv_modified_by);
                -- use a join with the dimensions to get the DAY_REF key value on the fly
                -- lookup the person_ref and insert if not found
                IF lr_event.modified_by != lv_modified_by
                then
                    BEGIN
                        SELECT person_ref INTO ln_person_ref
                        FROM migration_person
                        WHERE person_name = lr_event.modified_by;
                    EXCEPTION
                    WHEN No_data_found
                    THEN
--                    Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                          || ' new person ' || lr_event.modified_by);
                         SELECT Max(person_ref) + 1
                        INTO ln_person_ref
                        FROM migration_person;

                        INSERT INTO migration_person
                            (person_ref,
                            person_name)
                        VALUES
                            (ln_person_ref,
                            lr_event.modified_by);
                    END;
                    lv_modified_by := lr_event.modified_by;
                END IF;
                -- loop thru the cursor inserting a row at a time into the M_Event table
                -- this is generally efficient enough as its only a few rows when in increment mode
--               Dbms_Output.put_line(To_Char(SYSDATE, 'MI:SS') ||'action_ref = '|| To_Char(ln_action_ref)
--                     || ' insert day_ref = ' || To_Char(lr_event.day_ref) || ', '|| To_Char(ln_person_ref));
                 INSERT INTO migration_event
                        (aid,
                        assay_id,
                        experiment_id,
                        project_id,
                        event_count,
                        person_ref,
                        action_ref,
                        day_ref)
                VALUES (lr_event.aid,
                        lr_event.assay_id,
                        lr_event.experiment_id,
                        lr_event.project_id,
                        lr_event.event_count,
                        ln_person_ref,
                        ln_action_ref,
                        lr_event.day_ref);

            END LOOP;
            -- commit at the end of the loop
            COMMIT;

        END LOOP;

        -- and add the newly minted AID and equivalent items to the dimension table
        INSERT INTO migration_aid
            (aid)
        SELECT DISTINCT (aid)
            FROM migration_event me
        WHERE NOT EXISTS (SELECT 1
            FROM migration_aid ma
            WHERE ma.aid = me.aid);

        Commit;

    EXCEPTION
    WHEN le_bad_parameter THEN
        null;
--    WHEN OTHERS THEN
--        NULL;

    END Generate_Migration_stats;


end Migration_stats;
/

