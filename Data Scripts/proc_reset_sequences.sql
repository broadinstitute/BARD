CREATE OR REPLACE PROCEDURE RESET_SEQUENCES
AUTHID CURRENT_USER
AS
    cursor cur_sequence
    is
    select sequence_name
    from user_sequences
    WHERE sequence_name LIKE '%_ID_SEQ';

    lv_max_sql  varchar2(1000);
    lv_drop_sql varchar2(1000);
    lv_create_sql   varchar2(1000);
    lv_grant_sql    varchar2(1000);
    lv_table_name   varchar2(50);
    lv_primary_key  varchar2(50);
    ln_max_id   number;

begin
    for rec_sequence in cur_sequence
    loop
--        IF rec_sequence.sequence_name = 'PRJCT_EXPRMT_CNTXT_ITEM_ID_SEQ'
--        THEN
--            lv_table_name := 'PRJCT_EXPRMT_CONTEXT_ITEM';
--            lv_primary_key :=  'PRJCT_EXPRMT_CONTEXT_ITEM_ID';
--        ELSE
            lv_table_name := replace(rec_sequence.sequence_name, '_ID_SEQ', null);
            lv_primary_key := replace(rec_sequence.sequence_name, '_SEQ', null);
--        END IF;

        lv_max_sql := 'select nvl(max(' || lv_primary_key || '), 0) from ' || lv_table_name;
        begin
            --dbms_output.put_line(lv_max_sql);
            EXECUTE IMMEDIATE lv_max_sql INTO ln_max_ID;

            lv_drop_sql := 'drop sequence ' || rec_sequence.sequence_name;
            --dbms_output.put_line(lv_drop_sql);
            EXECUTE IMMEDIATE lv_drop_sql;

            lv_create_sql := 'create sequence ' || rec_sequence.sequence_name
                    || ' start with ' || to_char(ln_max_id + 1)
                    || ' increment by 1 nominvalue';
            IF rec_sequence.sequence_name in ('RESULT_ID_SEQ', 'RESULT_HIERARCHY_ID_SEQ', 'RSLT_CONTEXT_ITEM_ID_SEQ')
            THEN
                lv_create_sql := lv_create_sql || ' maxvalue 999999999999999999 cache 10000';
            ELSE
                lv_create_sql := lv_create_sql || ' maxvalue 2147483648 cache 20';
            END IF;
                lv_create_sql := lv_create_sql || ' nocycle noorder';
            --dbms_output.put_line(lv_create_sql);

            lv_grant_sql := 'grant select on ' || rec_sequence.sequence_name
                    || ' to schatwin';
            --dbms_output.put_line(lv_grant_sql);
            EXECUTE IMMEDIATE lv_create_sql;
            EXECUTE IMMEDIATE lv_grant_sql;

        exception
            when others
            then
                null;   --dbms_output.put_line (to_char(sqlcode) || ', ' || sqlerrm);

        end;

    end loop;

    if cur_sequence%isopen
    then
        close cur_sequence;
    end if;

end;
/

SELECT * FROM user_sequences;