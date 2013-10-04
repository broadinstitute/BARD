CREATE TABLE RESULT_MAP
   ("AID" NUMBER(19,0),
	"TID" NUMBER(19,0),
	"TIDNAME" VARCHAR2(200 BYTE),
	"PARENTTID" VARCHAR2(3 BYTE),
	"RELATIONSHIP" VARCHAR2(20 BYTE),
	"QUALIFIERTID" NUMBER(3,0),
	"RESULTTYPE" VARCHAR2(128 BYTE),
	"STATS_MODIFIER" VARCHAR2(20 BYTE),
	"CONTEXTTID" NUMBER(19,0),
	"CONTEXTITEM" VARCHAR2(128 BYTE),
	"CONCENTRATION" FLOAT(20),
	"CONCENTRATIONUNIT" VARCHAR2(10 BYTE),
	"SERIESNO" NUMBER(3,0),
	"PANELNO" NUMBER(3,0),
	"ATTRIBUTE1" VARCHAR2(128 BYTE),
	"VALUE1" VARCHAR2(128 BYTE),
	"EXCLUDED_POINTS_SERIES_NO" NUMBER(3,0),
	"ATTRIBUTE2" VARCHAR2(128 BYTE),
	"VALUE2" VARCHAR2(128 BYTE),
	"DATE_CREATE" DATE DEFAULT SYSDATE,
	"LAST_UPDATED" DATE,
	"MODIFIED_BY" VARCHAR2(40 BYTE)
   )
/

CREATE INDEX IDX_RESULT_MAP_CONTEXTITEM ON RESULT_MAP ("CONTEXTITEM")
/

CREATE INDEX "IDX_RESULT_MAP_RESULTTYPE" ON "RESULT_MAP" ("RESULTTYPE")
/

CREATE UNIQUE INDEX "PK_RESULT_MAP" ON "RESULT_MAP" ("AID", "TID")
/

ALTER TABLE "RESULT_MAP" ADD CONSTRAINT "PK_RESULT_MAP" PRIMARY KEY ("AID", "TID")
/

ALTER TABLE "RESULT_MAP" MODIFY ("AID" NOT NULL ENABLE)
/

ALTER TABLE "RESULT_MAP" MODIFY ("TID" NOT NULL ENABLE)
/

CREATE OR REPLACE TRIGGER "UPD_RESULT_MAP_AUDIT_DATE"
BEFORE INSERT OR UPDATE
ON RESULT_MAP
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW  WHEN (new.modified_by is null) BEGIN
     :new.last_updated := SYSDATE;
     :new.modified_by := Lower(USER);
END;
/

ALTER TRIGGER "UPD_RESULT_MAP_AUDIT_DATE" ENABLE
/

CREATE OR REPLACE TRIGGER "FK_RESULT_MAP_ELEMENT"
BEFORE UPDATE OF ATTRIBUTE1,ATTRIBUTE2,CONCENTRATIONUNIT,CONTEXTITEM,RELATIONSHIP,RESULTTYPE,STATS_MODIFIER
ON result_map
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
 WHEN (new.resulttype is NOT NULL
      OR NEW.contextitem IS NOT NULL
      OR NEW.stats_modifier IS NOT NULL
      OR NEW.attribute1 IS NOT NULL
      OR NEW.attribute2 IS NOT NULL
      OR NEW.concentrationUnit IS NOT NULL
      OR NEW.relationship IS NOT NULL
      ) DECLARE
    lb_error  BOOLEAN := FALSE;
    lv_err_msg  VARCHAR2(2048);
    ln_found  NUMBER;
    lv_expected VARCHAR2(100);

BEGIN
     IF :new.resultType IS NOT NULL
        AND (:new.resulttype != :OLD.resultType OR :old.resulttype IS null)
     THEN
        SELECT e.expected_value_type, Count(*)
        INTO lv_expected, ln_found
        FROM result_type_tree rtt,
            element e
        WHERE e.element_id = rtt.result_type_id
          AND rtt.result_type_name = :new.resultType
        GROUP BY e.expected_value_type;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' resultType = "' || :new.resultType || '" not a Result_Type, ';
        ELSIF :new.resultType LIKE '%endpoint'
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' resultType = "' || :new.resultType || '", "...endpoint" is not a valid Result_Type, ';
        ELSIF lv_expected = 'none'
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' resultType = "' || :new.resultType || '", cannot use this as a contextItem and attribute, only as a value, ';
        END IF;
     END IF;

     IF :new.contextItem IS NOT null
        AND (:new.contextItem != :OLD.contextItem OR :old.contextItem IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM element
        WHERE label = :new.contextItem;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' contextItem = "' || :new.contextItem || '" is not in ELEMENT, ';
        ELSE
            SELECT Count(*)
            INTO ln_found
            FROM result_type_tree
            WHERE result_type_name = :new.contextItem;

            IF ln_found > 0
            THEN
                lb_error := TRUE;
                lv_err_msg := lv_err_msg || ' contextItem = "' || :new.contextItem || '" is a Result_Type, ';
            ELSE
                SELECT Count(*)
                INTO ln_found
                FROM unit_tree
                WHERE full_path || '> ' || unit LIKE '%> ' || :new.contextItem || '%';

                IF ln_found > 0
                THEN
                    lb_error := TRUE;
                    lv_err_msg := lv_err_msg || ' contextItem = "' || :new.contextItem || '" is a Unit, ';
                END IF;
            END IF;
        END IF;
     END IF;

     IF :new.stats_modifier IS NOT null
        AND (:new.stats_modifier != :OLD.stats_modifier OR :old.stats_modifier IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM stats_modifier_tree
        WHERE label = :new.stats_modifier;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' stats_modifier = "' || :new.stats_modifier || '" not an Endpoint Statistic, ';
        END IF;
     END IF;

     IF :new.attribute1 IS NOT null
        AND (:new.attribute1 != :OLD.attribute1 OR :old.attribute1 IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM element
        WHERE label = :new.attribute1;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' attribute1 = "' || :new.attribute1 || '" is not in ELEMENT, ';
        ELSE
            SELECT Count(*)
            INTO ln_found
            FROM result_type_tree
            WHERE result_type_name = :new.attribute1;

            IF ln_found > 0
            THEN
                lb_error := TRUE;
                lv_err_msg := lv_err_msg || ' attribute1 = "' || :new.attribute1 || '" is a Result_Type, ';
            ELSE
                SELECT Count(*)
                INTO ln_found
                FROM unit_tree
                WHERE full_path || '> ' || unit LIKE '%> ' || :new.attribute1 || '%';

                IF ln_found > 0
                THEN
                    lb_error := TRUE;
                    lv_err_msg := lv_err_msg || ' attribute1 = "' || :new.attribute1 || '" is a Unit, ';
                END IF;
            END IF;
        END IF;
     END IF;
     IF :new.attribute2 IS NOT null
        AND (:new.attribute2 != :OLD.attribute2 OR :old.attribute2 IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM element
        WHERE label = :new.attribute2;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' attribute2 = "' || :new.attribute2 || '" is not in ELEMENT, ';
        ELSE
            SELECT Count(*)
            INTO ln_found
            FROM result_type_tree
            WHERE result_type_name = :new.attribute2;

            IF ln_found > 0
            THEN
                lb_error := TRUE;
                lv_err_msg := lv_err_msg || ' attribute2 = "' || :new.attribute2 || '" is a Result_Type, ';
            ELSE
                SELECT Count(*)
                INTO ln_found
                FROM unit_tree
                WHERE full_path || '> ' || unit LIKE '%> ' || :new.attribute2 || '%';

                IF ln_found > 0
                THEN
                    lb_error := TRUE;
                    lv_err_msg := lv_err_msg || ' attribute2 = "' || :new.attribute2 || '" is a Unit, ';
                END IF;

            END IF;
        END IF;
     END IF;

     IF :new.concentrationUnit IS NOT null
        AND (:new.concentrationUnit != :OLD.concentrationUnit OR :old.concentrationUnit IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM unit_tree
        WHERE full_path || '> ' || unit LIKE '%concentration unit%> ' || :new.concentrationUnit || '%';

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' concentrationUnit = "' || :new.concentrationUnit || '" not a Concentration Unit, ';
        END IF;
     END IF;

     IF :new.relationship IS NOT NULL
        AND (:new.relationship != :OLD.relationship OR :old.relationship IS null)
     THEN
        IF :NEW.relationship NOT IN ('calculated from', 'supported by', 'Derives', 'Child')
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' relationship = "' || :new.relationship || '" not a Relationship, ';
        END IF;
     END IF;

     IF lb_error
     THEN
        lv_err_msg := ' where AID=' || To_Char(:new.aid) || ' and TID=' || :new.tid || '; ' || lv_err_msg;
        RAISE_application_error (-20001, lv_err_msg);
     END IF;

END;
/

ALTER TRIGGER "FK_RESULT_MAP_ELEMENT" ENABLE
/
