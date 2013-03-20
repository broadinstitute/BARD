CREATE OR REPLACE PACKAGE auditing_init
AUTHID CURRENT_USER
AS

    PROCEDURE make_triggers(avi_table_name IN VARCHAR2 DEFAULT NULL);

END auditing_init;
/


CREATE OR REPLACE PACKAGE BODY auditing_init
AS
    CR  CHAR(2) := Chr(13) || Chr(10);
    prefix  VARCHAR2(10) := 'ADT_';
---------------------------------------------------------------------------------------
-- setup procedures
---------------------------------------------------------------------------------------
--   There are some rules to follow:
--   1. No Primary Key means no auditing - no triggers are created
--   2. Tables with a lot of columns (about 300?) make triggers with too much code,
--      Oracle has a limit of 32767 chars for trigger code - no triggers are created
--   3. Auditing triggers on tables named AUDIT..., TEMP..., or ...LOG are prohibited
--      you can get into circular triggering logic without these prohibitions
--   4. CLOBs and BLOBs are not audited - only columns that can convert to a string < 4000 long are allowed
--
--
--
--
--
    PROCEDURE delete_audit_trigger (avi_table_name IN VARCHAR2 DEFAULT null)
    AS
        CURSOR cur_trigger
        IS
        SELECT trigger_name
        FROM user_triggers
        WHERE TRIGGER_NAME LIKE prefix || '%'
          AND (table_name = avi_table_name OR avi_table_name IS NULL);

        lv_sql  VARCHAR2(1000);

    BEGIN
        FOR lr_trigger IN cur_trigger
        LOOP
            lv_sql := 'drop trigger ' || lr_trigger.trigger_name;

            EXECUTE IMMEDIATE lv_sql;

        END LOOP;

    END delete_audit_trigger;

    FUNCTION convert_data_type_string (avi_column_name IN varchar2,
                                       avi_data_type IN varchar2)
        RETURN VARCHAR2
    AS
        lv_string VARCHAR2(100) := NULL;

    BEGIN
        IF avi_data_type IN ('CHAR', 'VARCHAR2')
        then
            lv_string :=  avi_column_name;
        ELSIF avi_data_type IN ('NUMBER', 'INTEGER', 'FLOAT')
        THEN
            lv_string := 'TO_CHAR(' || avi_column_name || ')';
        ELSIF avi_data_type IN ('DATE', 'TIMESTAMP(6)')
        THEN
            lv_string := 'TO_CHAR(' || avi_column_name || ', ''MM/DD/YYYY HH:MI:SS'')';
        ELSE
            lv_string := null;
        END IF;

        RETURN lv_string;

    END convert_data_type_string;

    function write_trigger_code (avi_table_name IN VARCHAR2,
                                  avi_type  IN VARCHAR2)
        RETURN varchar2
    AS
        lv_sql varchar2(32767);
        lv_primary_key VARCHAR2(100) := NULL;
        lv_all_PK VARCHAR2(5000) := NULL;
        lv_col  VARCHAR2(1000) := NULL;
        --lv_all_cols VARCHAR2(10000) := NULL;

        CURSOR cur_row (cv_table_name IN VARCHAR2,
                        cv_crud IN VARCHAR2)
        IS
        SELECT a.table_owner,
            a.table_name,
            listagg(a.column_name, ',') within GROUP (ORDER BY a.column_name) col_list
        FROM cols,
            audit_setting a
        WHERE a.table_owner = USER
          AND a.table_name = cols.table_name
          AND a.table_name = cv_table_name
          AND a.column_name = cols.column_name
          AND Decode(cv_crud, 'IS_PK', a.is_pk, 'INSERT',a.audit_insert,'UPDATE',a.audit_update, 'DELETE',a.audit_delete) = 'Y'
        group BY a.table_owner, a.table_name;

        TYPE r_row IS RECORD (table_owner audit_setting.table_owner%TYPE,
                       table_name audit_setting.table_name%TYPE,
                       col_list VARCHAR2(4000));
        lr_row r_row;

        CURSOR cur_pk (cn_table_name IN VARCHAR2)
        IS
        SELECT a.table_owner,
            a.table_name,
            a.column_name,
            cols.data_type
        FROM cols,
            audit_setting a
        WHERE a.table_owner = USER
          AND a.table_name = cols.table_name
          AND a.table_name = cn_table_name
          AND a.column_name = cols.column_name
          AND a.is_pk = 'Y'
        ORDER BY a.table_name, cols.column_id;

        CURSOR cur_column (cn_table_name IN VARCHAR2,
                           cn_crud IN varchar2)
        IS
        SELECT a.table_owner,
            a.table_name,
            a.column_name,
            cols.data_type
        FROM cols,
            audit_setting a
        WHERE a.table_owner = USER
          AND a.table_name = cols.table_name
          AND a.table_name = cn_table_name
          AND a.column_name = cols.column_name
          AND Decode(cn_crud, 'IS_PK', a.is_pk, 'INSERT',a.audit_insert,'UPDATE',a.audit_update, 'DELETE',a.audit_delete) = 'Y'
        ORDER BY a.table_name, cols.column_name;

        lv_trigger_name VARCHAR2(100);

    BEGIN
        --------------------------------------------------------------
        -- setup the header
        OPEN cur_row(avi_table_name, avi_type);
        FETCH CUR_row INTO lr_row;
        lv_trigger_name := SubStr( prefix || SubStr(avi_type, 1, 3) || '_' || avi_table_name, 1, 30);
        lv_sql := 'CREATE OR REPLACE TRIGGER ' || lv_trigger_name || CR
                  || 'BEFORE ' || avi_type || cr;
        IF avi_type = 'UPDATE'
        THEN
            lv_sql := lv_sql || '    OF ' || lr_row.col_list || CR;
        END IF;
        lv_sql := lv_sql || '    ON ' || avi_table_name || cr
                  || '    FOR EACH ROW' || CR || CR
                  || 'DECLARE' || cr
                  || '    lt_columns  auditing.t_columns;' || cr
                  || '    lv_table_owner VARCHAR2(30) := ''' || USER || ''';' || cr
                  || '    lv_table_name VARCHAR2(30) := ''' || avi_table_name || ''';' || cr
                  || '    lv_username VARCHAR2(30);' || cr
                  || '    lv_primary_key  VARCHAR2(1000);' || cr
                  || '    lv_action VARCHAR2(30) := ''' || avi_type || ''';' || cr
                  || '    i BINARY_INTEGER := 0;' || cr || CR
                  || 'BEGIN' || cr
                  || '    lv_username := p_pbs_context.get_username;' || cr
                  || '    IF lv_username IS NULL' || cr
                  || '    THEN' || cr
                  || '        Raise_Application_Error(-20900, ''' || avi_table_name || ' ' || lower(avi_type) || ' refused.  Username not specified. '');' || cr
                  || '    END IF;' || CR || CR;
        CLOSE cur_row;
        --dbms_Output.put_line(lv_sql);
        --------------------------------------------------------------
        -- setup the primary key
        FOR lr_pk IN cur_pk(avi_table_name)
        LOOP
            lv_primary_key := convert_data_type_string (':old.' || lr_pk.column_name, lr_pk.data_type);

            IF lv_primary_key IS NOT NULL
            THEN
                IF lv_all_PK IS NULL
                THEN
                    lv_all_pk := '    lv_primary_key := ' || lv_primary_key || ';' || CR;
                ELSE
                    lv_all_pk := '    lv_primary_key := lv_primary_key || '', '' || ' || lv_primary_key || ';' || CR;
                END IF;
            END IF;
        END LOOP;

        -- if there's no primary key we can't audit the table
        IF lv_all_pk IS NULL
        THEN
            RETURN NULL;
        END IF;

        lv_sql := lv_sql || lv_all_pk || CR;
        --dbms_Output.put_line(lv_sql);

        --------------------------------------------------------------
        -- setup the columns
        FOR lr_col IN cur_column (avi_table_name, avi_type)
        LOOP
            Dbms_Output.PUT_LINE( substr(avi_type, 1, 3) || ' ' ||lr_col.TABLE_name || '.' || lr_col.column_name || ': ' || Length(lv_sql));
            IF Length(lv_sql) + 892 < 32767    -- 892 is the maximum length of the next piece and the end section
            THEN
                IF avi_type = 'UPDATE' THEN
                    lv_col := '    IF :new.' || lr_col.column_name || ' != :old.' || lr_col.column_name || CR
                          || '      OR (:new.' || lr_col.column_name || ' IS NULL AND :old.' || lr_col.column_name || ' IS NOT NULL)' || CR
                          || '      OR (:new.' || lr_col.column_name || ' IS NOT NULL AND :old.' || lr_col.column_name || ' IS NULL)' || CR
                          || '    THEN' || CR
                          || '       i := i + 1;' || CR
                          || '       lt_columns(i).column_name := ''' || lr_col.column_name || ''';' || CR
                          || '       lt_columns(i).old_value := ' || convert_data_type_string(':old.' || lr_col.column_name, lr_col.data_type) || ';' || CR
                          || '       lt_columns(i).new_value := ' || convert_data_type_string(':new.' || lr_col.column_name, lr_col.data_type) || ';' || CR
                          || '    END IF;' || CR;
                ELSIF AVI_TYPE = 'DELETE'
                THEN
                     lv_col := '       i := i + 1;' || CR
                          || '       lt_columns(i).column_name := ''' || lr_col.column_name || ''';' || CR
                          || '       lt_columns(i).old_value := ' || convert_data_type_string(':old.' || lr_col.column_name, lr_col.data_type) || ';' || CR ;
                ELSIF AVI_TYPE = 'INSERT'
                THEN
                     lv_col := '       i := i + 1;' || CR
                          || '       lt_columns(i).column_name := ''' || lr_col.column_name || ''';' || CR
                          || '       lt_columns(i).new_value := ' || convert_data_type_string(':new.' || lr_col.column_name, lr_col.data_type) || ';' || CR ;
                END IF;

                lv_sql := lv_sql || lv_col;
            ELSE
                lv_sql := NULL;
                EXIT;
            END IF;
        END LOOP;
        --dbms_Output.put_line(lv_sql);


        --------------------------------------------------------------
        -- setup the footer
        IF lv_sql IS NOT NULL
        then
            lv_sql := lv_sql || CR || '    if i > 0 then' || CR
                        || '    auditing.save_audit(lv_table_owner,' || CR
                        || '                        lv_table_name,' || CR
                        || '                        lv_username,' || CR
                        || '                        lv_action,' || CR
                        || '                        lv_primary_key,' || CR
                        || '                        lt_columns);' || CR
                        || '    end if;' || CR || CR
                        || 'END;';
            --dbms_Output.put_line(lv_sql);
        END IF;

        RETURN lv_sql;
    EXCEPTION
      WHEN OTHERS THEN
        IF cur_column%ISOPEN THEN
            CLOSE cur_column;
        END IF;
        IF cur_row%ISOPEN THEN
            CLOSE cur_row;
        END IF;
        IF cur_pk%ISOPEN THEN
            CLOSE cur_pk;
        END IF;
        RAISE;
    END write_trigger_code;

-- public facing one

    PROCEDURE make_triggers(avi_table_name IN VARCHAR2 DEFAULT NULL)
    AS

        lv_sql varchar2(32767) := null;

        CURSOR cur_table
        IS
        SELECT table_name,
            Sum(Decode(audit_insert, 'Y',1,0)) insert_count,
            Sum(Decode(audit_update, 'Y',1,0)) update_count,
            Sum(Decode(audit_delete, 'Y',1,0)) delete_count
        FROM audit_setting
        WHERE (audit_insert = 'Y' OR audit_update = 'Y' OR audit_delete = 'Y')
          AND is_pk = 'N'
          AND (table_name = avi_table_name OR avi_table_name IS NULL)
        GROUP BY table_name;

    BEGIN
        -- first delete the old triggers
        --dbms_Output.put_line('delete ' || avi_table_name);

        delete_audit_trigger (avi_table_name);

        -- loop thru creating new triggers
        FOR lr_table IN cur_table
        LOOP
            IF lr_table.insert_count > 0
            THEN
            --dbms_Output.put_line('insert trigger for ' || lr_table.table_name);
                lv_sql := write_trigger_code(lr_table.table_name, 'INSERT');
                IF lv_sql IS NOT NULL
                THEN
                    EXECUTE IMMEDIATE lv_sql;
                END IF;
            END IF;
            lv_sql := NULL;

            IF lr_table.update_count > 0
            THEN
            --dbms_Output.put_line('UPDATE trigger for ' || lr_table.table_name);
                lv_sql := write_trigger_code(lr_table.table_name, 'UPDATE');
                --dbms_Output.Put_line('UPD SQL: ' || lv_sql);
                IF lv_sql IS NOT NULL
                THEN
                   --dbms_Output.Put_line('Execute immediate UPD SQL:');
                   EXECUTE IMMEDIATE lv_sql;
                   --dbms_Output.Put_line('After Execute immediate UPD SQL:');

                END IF;
            END IF;
            lv_sql := NULL;
            IF lr_table.delete_count > 0
            THEN
            --dbms_Output.put_line('DELETE trigger for ' || lr_table.table_name);
                lv_sql := write_trigger_code(lr_table.table_name, 'DELETE');
                --dbms_Output.Put_line('del SQL: ' || lv_sql);
                IF lv_sql IS NOT NULL
                THEN
                    EXECUTE IMMEDIATE lv_sql;
                END IF;
            END IF;

        END LOOP;
    EXCEPTION
      WHEN OTHERS THEN
        IF cur_table%ISOPEN THEN
            CLOSE cur_table;
        END IF;
        RAISE;
    END make_triggers;


END auditing_init;
/
