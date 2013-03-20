-- testing for trigger audits

BEGIN
    --Dbms_Output.PUT_LINE ('auditing_init.make_triggers');
    auditing_setup.setup_tables;
END;
/

SELECT * FroM audit_setting;
BEGIN
    --Dbms_Output.PUT_LINE ('auditing_init.make_triggers');
    auditing_setup.update_settings('', 'Refresh');
END;
/

DELETE fRom audit_setting
WHERE table_name LIKE '%_TREE'
    OR TABle_name LIKE 'PB%'
    OR TABle_name LIKE 'PC%'
    OR TABLE_NAME LIKE 'SECURITY%'
    OR TABLE_NAME LIKE 'TEMP%'
    OR TABLE_NAME LIKE 'MIGRATION%';

BEGIN
    auditing_init.make_triggers;
END;
/


DROP TABLE audit_setting;
DROP TABLE audit_row_log;
DROP TABLE audit_column_log;
SELECT Max(element_id) FROM element;


SELECT 'DROP TRIGGER ' || TRIGGER_NAME || ';' FROM USER_TRIGGERS
WHERE TRIGGER_NAME LIKE 'ADT%';


BEGIN
P_PBS_CONTEXT.SET_USERNAME('SIMON');
END;
/
UPDATE assay SET MODIFIED_BY = 'southern',
  last_updated = null
WHERE assay_ID = 1667;

 SELECT * FROM AUDIT_ROW_LOG;
 SELECT * FROM AUDIT_COLUMN_LOG;

SELECT * FROM ELEMENT WHERE ELEMENT_ID = 1667;
DELETE FROM element WHERE element_id = 1760;


SELECT * FROM USER_IND_COLUMNS
WHERE INDEX_NAME LIKE 'PK%';

SELECT column_name,
              data_type,
              (SELECT is_pk
                  FROM audit_setting a
                  WHERE a.table_owner = ac.owner
                    AND a.table_name = ac.table_name
                    AND a.column_name = ac.column_name) is_pk
          FROM all_tab_columns ac
          WHERE ac.owner = 'DATA_MIG'
            AND ac.table_name = 'ELEMENT'
          ORDER BY column_id;



UPDATE AUDIT_COLUMN_LOG
SET old_value = 'NULL'
WHERE old_value IS NULL;


DECLARE
    lv_sql  varchar2(32767);
BEGIN
    auditing.show_audit_trail('IDENTIFIER_MAPPING',
    '1234, ELEMENT, southern',
    lv_sql);
    Dbms_Output.put_line(lv_sql);
END;
/

DECLARE
    lv_sql  varchar2(32767);
BEGIN
    auditing.show_audit_trail('ELEMENT',
    '1667',
    lv_sql);
    Dbms_Output.put_line(lv_sql);
END;
/


SELECT user username,
    'Current' action,
    sysdate audit_timestamp,
    TO_CHAR(ELEMENT_ID) primary_key,
    ELEMENT_STATUS ELEMENT_STATUS,
    LABEL LABEL,
    TO_CHAR(UNIT_ID) UNIT_ID,
    ABBREVIATION ABBREVIATION,
    BARD_URI BARD_URI,
    DESCRIPTION DESCRIPTION,
    SYNONYMS SYNONYMS,
    EXTERNAL_URL EXTERNAL_URL,
    READY_FOR_EXTRACTION READY_FOR_EXTRACTION,
    TO_CHAR(VERSION) VERSION,
    TO_CHAR(DATE_CREATED, 'MM/DD/YYYY HH:MI:SS') DATE_CREATED,
    TO_CHAR(LAST_UPDATED, 'MM/DD/YYYY HH:MI:SS') LAST_UPDATED,
    MODIFIED_BY MODIFIED_BY
from ELEMENT
where ELEMENT_ID = '1667'

union all
SELECT  arl.username,
    arl.action,
    arl.audit_timestamp,
    arl.primary_key,
listagg(Decode(acl.column_name, 'ELEMENT_STATUS', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'LABEL', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'UNIT_ID', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'ABBREVIATION', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'BARD_URI', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'DESCRIPTION', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'SYNONYMS', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'EXTERNAL_URL', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'READY_FOR_EXTRACTION', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'VERSION', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'DATE_CREATED', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'LAST_UPDATED', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value),
listagg(Decode(acl.column_name, 'MODIFIED_BY', acl.old_value), '') WITHIN GROUP( ORDER BY acl.old_value)
FROM AUDIT_COLUMN_LOG acl,
    audit_row_log arl
WHERE arl.audit_id = acl.audit_id
AND arl.table_owner = 'SCHATWIN'
AND arl.table_name = 'ELEMENT'
AND arl.primary_key = '1667'
GROUP BY  arl.username, arl.action, arl.audit_timestamp, arl.primary_key
ORDER BY primary_key, audit_timestamp desc