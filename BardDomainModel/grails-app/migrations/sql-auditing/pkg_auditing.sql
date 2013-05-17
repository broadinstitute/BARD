CREATE OR REPLACE PACKAGE auditing
AS
    TYPE r_column IS RECORD (column_name VARCHAR2(30),
                            old_value   VARCHAR2(4000),
                            new_value   VARCHAR2(4000));

    TYPE t_columns iS TABLE OF r_column
        INDEX BY binary_integer;

    TYPE r_trail IS REF CURSOR;

    PROCEDURE Save_audit (avi_table_owner IN VARCHAR2,
                          avi_table_name  IN VARCHAR2,
                          avi_username  IN VARCHAR2,
                          avi_action IN VARCHAR2,
                          avi_primary_key IN VARCHAR2,
                          ati_columns IN t_columns);

    PROCEDURE show_audit_trail (avi_table_name IN VARCHAR2,
                                avi_primary_key IN VARCHAR2,
                                avo_sql OUT VARCHAR2,
                                adi_from_date IN DATE DEFAULT null,
                                adi_to_date IN DATE DEFAULT NULL);

    PROCEDURE show_audit_trail (avi_table_name IN VARCHAR2,
                                avi_primary_key IN VARCHAR2,
                                aco_audit_trail OUT r_trail,
                                adi_from_date IN DATE DEFAULT null,
                                adi_to_date IN DATE DEFAULT NULL);

END auditing;
/

CREATE OR REPLACE PACKAGE BODY auditing
AS

    CR  CHAR(2) := Chr(13) || Chr(10);
---------------------------------------------------------------------------------------
-- operating procedures
---------------------------------------------------------------------------------------
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
        ELSIF avi_data_type IN ('CLOB')
        THEN
            lv_string := 'TO_CHAR(SUBSTR(' || avi_column_name || ',1,4000))';
        ELSIF avi_data_type IN ('DATE', 'TIMESTAMP(6)')
        THEN
            lv_string := 'TO_CHAR(' || avi_column_name || ', ''MM/DD/YYYY HH:MI:SS'')';
        ELSE
            lv_string := null;
        END IF;

        RETURN lv_string;

    END convert_data_type_string;

    function save_audit_row (avi_table_owner IN VARCHAR2,
                            avi_table_name IN varchar2,
                            avi_primary_key IN VARCHAR2,
                            avi_action in VARCHAR2,
                            avi_username IN VARCHAR2)
            RETURN NUMBER
    AS
        ln_audit_id NUMBER;

    BEGIN
        SELECT audit_id_seq.NEXTVAL
        INTO ln_audit_id
        FROM dual;

        INSERT INTO audit_row_log
            (audit_id,
             table_owner,
             table_name,
             primary_key,
             action,
             username,
             audit_timestamp)
        VALUES
            (ln_audit_id,
             avi_table_owner,
             avi_table_name,
             avi_primary_key,
             avi_action,
             avi_username,
             sysdate);

        RETURN ln_audit_id;
    END save_audit_row;

    PROCEDURE save_audit_column (ani_audit_id NUMBER,
                                avi_column_name IN VARCHAR2,
                                avi_old_value IN VARCHAR2,
                                avi_new_value IN VARCHAR2)
    AS


    BEGIN
        INSERT INTO audit_column_log
            (audit_id,
             column_name,
             old_value)
        VALUES
            (ani_audit_id,
             avi_column_name,
             Nvl(avi_old_value, 'NULL'));

    END save_audit_column;

-- public facing one
    PROCEDURE Save_audit (avi_table_owner IN VARCHAR2,
                          avi_table_name  IN VARCHAR2,
                          avi_username  IN VARCHAR2,
                          avi_action IN VARCHAR2,
                          avi_primary_key IN VARCHAR2,
                          ati_columns IN t_columns)
    as
        ln_audit_id NUMBER;

    BEGIN
        ln_audit_id := save_audit_row (avi_table_owner,
                                      avi_table_name,
                                      avi_primary_key,
                                      avi_action,
                                      avi_username);

        FOR i IN 1..ati_columns.last
        LOOP
            --Dbms_Output.put_line( ' ' || To_Char(i) || 'col:' ||ati_columns(i).column_name ||' old:' || ati_columns(i).old_value);
            save_audit_column (ln_audit_id,
                               ati_columns(i).column_name,
                               ati_columns(i).old_value,
                               ati_columns(i).new_value);
             --Dbms_Output.put_line(To_Char(ln_audit_id) );
        END LOOP;
    END save_audit;

    PROCEDURE show_audit_trail (avi_table_name IN VARCHAR2,
                                avi_primary_key IN VARCHAR2,
                                avo_sql OUT varchar2,
                                adi_from_date IN DATE DEFAULT null,
                                adi_to_date IN DATE DEFAULT NULL)
    AS
          lv_sql  VARCHAR2(32767);
          lv_column_text  VARCHAR2(1000);
          lv_table_name VARCHAR2(50);
          lv_owner VARCHAR2(50);
          lv_where VARCHAR2(1000);
          ln_period_pos BINARY_INTEGER;
          lv_key_value  varchar2(1000);
          lv_compound_key VARCHAR2(4000);
          ln_comma BINARY_INTEGER;
          lv_pk_columns VARCHAR2(1000);
          lv_all_pk_col_text VARCHAR2(5000);
          lv_all_col_text VARCHAR2(10000);

          CURSOR cur_col (cv_table_name IN VARCHAR2,
                          cv_table_owner IN VARCHAR2)
          IS
          SELECT column_name,
              data_type,
              (SELECT is_pk
                  FROM audit_setting a
                  WHERE a.table_owner = ac.owner
                    AND a.table_name = ac.table_name
                    AND a.column_name = ac.column_name) is_pk
          FROM all_tab_columns ac
          WHERE ac.owner = cv_table_owner
            AND ac.table_name = cv_table_name
          ORDER BY column_id;
    BEGIN
        -- split the incoming tablename into owner and table
        ln_period_pos := InStr(avi_table_name, '.');
        IF ln_period_pos = 0
        THEN
            lv_owner := USER;
            lv_table_name := Upper(avi_table_name);
        ELSE
            lv_owner := Upper(SubStr(avi_table_name, 1, ln_period_pos -1));
            lv_table_name := Upper(SubStr(avi_table_name, ln_period_pos + 1));
        END IF;

        -- assemble the 1st part of the union - the current entry
        lv_sql := 'SELECT user username,' || CR
              || '    ''Current'' action,'  || CR
              || '    sysdate audit_timestamp';
        lv_compound_key := avi_primary_key;
        FOR lr_col IN cur_col (lv_table_name, lv_owner)
        LOOP
            IF lr_col.is_pk = 'Y'
            THEN
                IF AVI_PRIMARY_KEY IS NOT NULL
                THEN
                    -- strip out the comma separated value of the primary key columns
                    ln_comma := InStr( lv_compound_key,',');
                    IF ln_comma = 0
                    THEN
                        lv_key_value := lv_compound_key;
                    ELSE
                        lv_key_value := SubStr(lv_compound_key, 1, ln_comma -1);
                        lv_compound_key := SubStr(lv_compound_key, ln_comma + 2);
                    END IF;
                    IF lv_where IS NULL
                    THEN
                        lv_where := 'where ' || lr_col.column_name || ' = ''' || lv_key_value || '''' || CR;
                    ELSE
                        lv_where := lv_where || '  and ' || lr_col.column_name || ' = ''' || lv_key_value || '''' || CR;
                    END IF;
                END IF;
                lv_pk_columns := lv_pk_columns || lr_col.column_name || ', ' ;
                lv_all_pk_col_text := lv_all_pk_col_text || convert_data_type_string(lr_col.column_name, lr_col.data_type) || ' || '', '' || ';
            ELSE
                lv_column_text := convert_data_type_string (lr_col.column_name, lr_col.data_type) || ' ' || lr_col.column_name;
                lv_all_col_text := lv_all_col_text || ',' || CR || '    ' || lv_column_text;
            END IF;
        END LOOP;
        lv_sql := lv_sql || ',' || CR || '    ' || substr(lv_all_pk_col_text, 1, Length(lv_all_pk_col_text)-12) || ' primary_key';
        lv_sql := lv_sql || lv_all_col_text;

        -- now the rest of the 1st clause
        lv_sql := lv_sql || CR || 'from ' || avi_table_name || CR
                         || lv_where || CR
                         || 'union all' || CR
                         || 'SELECT  arl.username,'|| CR
                         || '    arl.action,' || CR
                         || '    arl.audit_timestamp,' || CR
                         || '    arl.primary_key';

        FOR lr_col IN cur_col( lv_table_name, lv_owner)
        LOOP
             IF Nvl(lr_col.is_pk, 'N') = 'N'
             THEN
                IF lr_col.data_type like '_LOB'
                THEN
                    lv_column_text := '''<not audited>''';
                    lv_sql := lv_sql || ',' || CR || lv_column_text;

                ELSE
                    lv_column_text := 'listagg(Decode(acl.column_name, ''' || lr_col.column_name || ''', acl.old_value), '''') WITHIN GROUP( ORDER BY acl.old_value)';
                    lv_sql := lv_sql || ',' || CR || lv_column_text;
                END IF;
             END IF;
        END LOOP;

        -- and the final part
        lv_sql := lv_sql || CR || 'FROM AUDIT_COLUMN_LOG acl,' || CR
                  || '    audit_row_log arl' || CR
                  || 'WHERE arl.audit_id = acl.audit_id' || CR
                  || 'AND arl.table_owner = ''' || lv_owner || '''' || CR
                  || 'AND arl.table_name = ''' || lv_table_name || '''' || CR;
        IF AVI_PRIMARY_KEY IS NOT NULL
        THEN
            lv_sql := lv_sql || 'AND arl.primary_key = ''' || avi_primary_key || '''' || CR;
        END IF;
        lv_sql := lv_sql || 'GROUP BY  arl.username, arl.action, arl.audit_timestamp, arl.primary_key' || CR
                  || 'ORDER BY primary_key, audit_timestamp desc';

        --Dbms_Output.put_line(lv_sql);

        avo_sql := lv_sql;

    END show_audit_trail;

    ------------------------------------------------------------------------------
    -- overloaded version which returns a cursor that the applicaiton can walk thru
    PROCEDURE show_audit_trail (avi_table_name IN VARCHAR2,
                            avi_primary_key IN VARCHAR2,
                            aco_audit_trail OUT r_trail,
                            adi_from_date IN DATE DEFAULT null,
                            adi_to_date IN DATE DEFAULT NULL)
     AS
          lv_sql  VARCHAR2(32767);

     BEGIN
          show_audit_trail (avi_table_name,
                            avi_primary_key,
                            lv_sql,
                            adi_from_date,
                            adi_to_date);

          OPEN aco_audit_trail FOR lv_sql;

     END show_audit_trail;    -- version 2 with a cursor

END auditing;
/
