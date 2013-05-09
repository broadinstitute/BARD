PROMPT CREATE OR REPLACE TRIGGER fk_result_map_element
CREATE OR REPLACE TRIGGER fk_result_map_element
 BEFORE update
        --OR insert
     OF resulttype, contextitem, stats_modifier, attribute1, attribute2, concentrationUnit, relationship
     ON result_map
     FOR EACH ROW

WHEN (new.resulttype is NOT NULL
      OR NEW.contextitem IS NOT NULL
      OR NEW.stats_modifier IS NOT NULL
      OR NEW.attribute1 IS NOT NULL
      OR NEW.attribute2 IS NOT NULL
      OR NEW.concentrationUnit IS NOT NULL
      OR NEW.relationship IS NOT NULL
      )
DECLARE
    lb_error  BOOLEAN := FALSE;
    lv_err_msg  VARCHAR2(2048);
    ln_found  NUMBER;

BEGIN
     IF :new.resultType IS NOT NULL
        AND (:new.resulttype != :OLD.resultType OR :old.resulttype IS null)
     THEN
        SELECT Count(*)
        INTO ln_found
        FROM result_type_tree
        WHERE result_type_name = :new.resultType;

        IF ln_found = 0
        THEN
            lb_error := TRUE;
            lv_err_msg := lv_err_msg || ' resultType = "' || :new.resultType || '" not a Result_Type, ';
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

