PROMPT CREATE TABLE temp_context_item
CREATE GLOBAL TEMPORARY TABLE temp_context_item (
  display_order     NUMBER(5,0)   NULL,
  ext_assay_ref     VARCHAR2(128) NOT NULL,
  assay_id          NUMBER(19,0)  NOT NULL,
  experiment_id     NUMBER(19,0)  NOT NULL,
  attribute_id      NUMBER(19,0)  NULL,
  aid               NUMBER(19,0)  NOT NULL,
  resulttype        VARCHAR2(384) NULL,
  stats_modifier    VARCHAR2(128) NULL,
  contextitem       VARCHAR2(384) NULL,
  concentration     FLOAT(20)     NULL,
  concentrationunit VARCHAR2(128) NULL
)
  ON COMMIT DELETE ROWS
/


---------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE Result_map_util
AS

    -- this type must match the cursor cur_rm_measure
    /*        e.experiment_id,
              e.assay_id,
              el.element_id result_type_id,
              el_sm.element_id stats_modifier_id,
              rm.aid,
              rm.resulttype,
              rm.stats_modifier,
              rm.relationship,
              rm.tid,
              rm.series_nos,
              rm.parent_tids*/
    TYPE r_resulttype IS RECORD (
          EXPERIMENT_ID   experiment.experiment_id%TYPE,
          ASSAY_ID        experiment.assay_id%TYPE,
          RESULT_TYPE_ID  element.element_id%type,
          STATS_MODIFIER_ID element.element_id%type,
          AID             southern.result_map.aid%type,
          RESULTTYPE      southern.result_map.resultType%type,
          STATS_MODIFIER  southern.result_map.stats_modifier%type,
          relationship    southern.result_map.relationship%TYPE,
          TID             southern.result_map.tid%type,
          SERIES_NOS      varchar2(4000),
          PARENT_TIDS     varchar2(4000)
          );

    TYPE t_result_Maps IS TABLE OF southern.result_map%rowtype;

    TYPE t_string IS varray (1) OF VARCHAR2(40);

    PROCEDURE transfer_result_map (avi_AID IN VARCHAR2 DEFAULT NULL);

    PROCEDURE save_measure_and_children (ani_recursion_level  IN binary_integer,
                                         ani_parent_measure_id IN NUMBER,
                                         ari_resulttype IN r_resulttype);

    function save_measure (ani_parent_measure_id IN NUMBER,
                          ari_measure IN r_resulttype)
        RETURN NUMBER;

    FUNCTION save_context_item_set (ari_resultType  IN  r_resultType)
        RETURN NUMBER;

    PROCEDURE save_assay_context_measure (ani_assay_context_id IN number,
                               ani_measure_id IN number);

    PROCEDURE save_exprmt_measure (ani_experiment_id IN number,
                                ani_measure_id IN number,
                                ani_parent_measure_id IN NUMBER,
                                avi_relationship IN varchar2);

    PROCEDURE delete_measure (ani_assay_id IN NUMBER,
                             ani_experiment_id IN number,
                             avi_owner IN varchar2);

    procedure log_error (an_errnum   in  number,
                  av_errmsg  in varchar2,
                  av_location    in varchar2,
                  av_comment in varchar2 default null);

END Result_map_util;
/

CREATE OR REPLACE PACKAGE BODY result_map_util
as

    pn_max_recursion_level CONSTANT BINARY_INTEGER := 5;

    pv_modified_by CONSTANT VARCHAR2(40) := 'resultmap';

    PROCEDURE transfer_result_map (avi_AID IN VARCHAR2 DEFAULT NULL)
    AS
        CURSOR cur_assay_experiment (cn_AID NUMBER)
        IS
        SELECT e.assay_id,
            e.experiment_id,
            er.project_id,
            er.ext_assay_ref,
            SubStr(EXT_ASSAY_REF, 5) aid
        FROM external_reference er,
            experiment e
        WHERE e.experiment_id = er.experiment_id
          AND EXISTS (SELECT 1
              FROM southern.result_map rm
              WHERE 'aid=' || rm.aid = er.ext_assay_ref)
          AND er.project_id IS null
          AND (EXT_ASSAY_REF = 'aid=' || To_Char(cn_aid)
              OR cn_aid IS NULL);

        CURSOR cur_rm_measure (cn_aid NUMBER)
        IS
        SELECT e.experiment_id,
              e.assay_id,
              el.element_id result_type_id,
              el_sm.element_id stats_modifier_id,
              rm.aid,
              rm.resulttype,
              rm.stats_modifier,
              rm.relationship,
              rm.tid,
              rm.series_nos,
              rm.parent_tids
        FROM (SELECT aid, resulttype, stats_modifier, relationship, tid,
            --listagg(relationship, ',') within GROUP (ORDER BY tid) relationship,
            listagg(seriesno, ',') within GROUP (ORDER BY tid) series_nos,
            listagg(parenttid, ',') within GROUP (ORDER BY tid) parent_tids
            FROM southern.result_map
            WHERE aid = cn_aid   --1705
              AND resulttype IS NOT NULL
              --AND modified_by != 'southalln'
              AND parenttid IS null
              --AND InStr(',' || parentTID || ',', ',6,') > 0
            GROuP BY aid, resulttype, stats_modifier, relationship, tid) rm,
            external_reference er,
            experiment e,
            element el,
            element el_sm
        WHERE er.ext_assay_ref = 'aid=' || rm.aid
          AND e.experiment_id = er.experiment_id
          AND el.label (+) = rm.resulttype
          AND el_sm.label (+) = rm.stats_modifier;

       lr_rm_resultType       r_resultType;
       lt_TIDs                t_string;

    BEGIN
        -- get the list of assays to handle

        FOR lr_assay_experiment IN cur_assay_experiment(REPLACE (avi_aid, 'aid=', ''))
        loop

            delete_measure (lr_assay_experiment.assay_id, lr_assay_experiment.experiment_id, pv_modified_by);

            FOR lr_rm_measure IN cur_rm_measure (lr_assay_experiment.aid)
            LOOP
                lr_rm_resultType := lr_rm_measure;
                -- this is a recursive call!
                save_measure_and_children ( 0, NULL, lr_rm_measure);  --lr_rm_resultType);

            END LOOP;

            -- clean up the display_orders
            UPDATE assay_context_item aci
            SET display_order =
                (SELECT Count(*)
                FROM assay_context_item aci2
                WHERE aci2.assay_context_id = aci.assay_context_id
                  AND aci2.assay_context_item_id < aci.assay_context_item_id)
            WHERE display_order !=
                  (SELECT Count(*)
                FROM assay_context_item aci2
                WHERE aci2.assay_context_id = aci.assay_context_id
                  AND aci2.assay_context_item_id < aci.assay_context_item_id)
              AND EXISTS ( SELECT 1
                      FROM assay_context ac
                      WHERE ac.assay_context_id = aci.assay_context_id
                        AND ac.assay_id = lr_assay_experiment.assay_id);

            UPDATE assay_context aci
            SET display_order =
                (SELECT Count(*)
                FROM assay_context aci2
                WHERE aci2.assay_id = aci.assay_id
                  AND aci2.assay_context_id < aci.assay_context_id)
            WHERE display_order !=
                  (SELECT Count(*)
                FROM assay_context aci2
                WHERE aci2.assay_id = aci.assay_id
                  AND aci2.assay_context_id < aci.assay_context_id)
              AND aci.assay_id = lr_assay_experiment.assay_id;


        END LOOP;


    END transfer_result_map;

    PROCEDURE delete_measure (ani_assay_id IN NUMBER,
                             ani_experiment_id IN number,
                             avi_owner IN varchar2)
    AS

    BEGIN
        -- exrmpt_measure
        -- assay_context_measure
        -- measure
        DELETE from exprmt_measure
        WHERE experiment_id = ani_experiment_id
        AND modified_by = avi_owner;

        DELETE FROM assay_context_measure acm
        WHERE EXISTS (SELECT 1
            FROM measure m
            WHERE m.measure_id = acm.measure_id
              AND m.assay_id = ani_assay_id
              AND m.modified_by = avi_owner);

        DELETE FROM  measure m
        WHERE assay_id = ani_assay_id
          AND modified_by = avi_owner
          AND NOT EXISTS (SELECT 1
              FROM assay_context_measure acm
              WHERE acm.measure_id = m.measure_id)
          AND NOT EXISTS (SELECT 1
              FROM exprmt_measure acm
              WHERE acm.measure_id = m.measure_id);

        -- should there be a check here for the ones that weren't deleted?

    END delete_measure;

    PROCEDURE save_measure_and_children (ani_recursion_level  IN binary_integer,
                                         ani_parent_measure_id IN NUMBER,
                                         ari_resulttype IN r_resultType)
    AS
        ln_measure_id       NUMBER;
        le_measure_failed   EXCEPTION;
        ln_assay_context_id NUMBER;
        ln_first_comma_pos  NUMBER;
        lv_parentTID        VARCHAR2(10);
        lr_rm_resultType    r_resultType;
        lt_TIDs             t_string;

    CURSOR cur_rm_measure (cn_aid NUMBER, cv_parentTID VARCHAR2)
        IS
        SELECT e.experiment_id,
              e.assay_id,
              el.element_id result_type_id,
              el_sm.element_id stats_modifier_id,
              rm.aid,
              rm.resulttype,
              rm.stats_modifier,
              rm.relationship,
              rm.tid,
              rm.series_nos,
              rm.parent_tids
        FROM (SELECT aid, resulttype, stats_modifier, relationship, tid,
            --listagg(relationship, ',') within GROUP (ORDER BY tid) relationship,
            listagg(seriesno, ',') within GROUP (ORDER BY tid) series_nos,
            listagg(parenttid, ',') within GROUP (ORDER BY tid) parent_tids
            FROM southern.result_map
            WHERE aid = cn_aid
              AND resulttype IS NOT NULL
              --AND parenttid IS null
              AND InStr(',' || parentTID || ',', ',' || cv_parentTID|| ',') > 0
            GROuP BY aid, resulttype, stats_modifier, relationship, tid) rm,
            external_reference er,
            experiment e,
            element el,
            element el_sm
        WHERE er.ext_assay_ref = 'aid=' || rm.aid
          AND e.experiment_id = er.experiment_id
          AND el.label (+) = rm.resulttype
          AND el_sm.label (+) = rm.stats_modifier;


    BEGIN
        -- now the hard work begins
        -- first check the recursion depth
            -- more than 4-6 is a sign of a circular relationship so exit quickly
        -- save the measure
            -- and update it to store the parentage
        -- find and save (if necessary) it's contextItems as set, a context
        -- save the assay_context_measure (ensuring no duplicates)
        -- save the exprmt_measure
            -- with its corresponding parentage
        -- then find the child resultTypes
        -- and call this procedure all over again!
        IF ani_recursion_level > pn_max_recursion_level
        THEN  return; END IF;

        ln_measure_id := save_measure ( ani_parent_measure_id, ari_resultType);

        IF ln_measure_id = -1
        THEN    --- we have an error!
            RAISE le_measure_failed;
        END IF;

        ln_assay_context_id := save_context_item_set(ari_resultType);
        -- if there is an assay_context, link it to the measure
        IF ln_assay_context_id IS NOT NULL
        THEN
            save_assay_context_measure (ln_assay_context_id, ln_measure_id);
        END IF;

        -- assign this measure (with its parentage!) to the experiment
        save_exprmt_measure(ari_resultType.experiment_id,
                            ln_measure_id,
                            ani_parent_measure_id,
                            ari_resultType.relationship);

        --extract the first parentTID from TIDs for this resultType
        FOR lr_rm_measure IN cur_rm_measure(ari_resultType.aid, ari_resultType.TID)
        LOOP
            lr_rm_resultType := lr_rm_measure;
            save_measure_and_children ( ani_recursion_level + 1, ln_measure_id, lr_rm_measure);   --lr_rm_resultType);

        END LOOP;

    EXCEPTION
        WHEN le_measure_failed
        THEN
            log_error (-90001, 'Attribute not in Element', 'Save_measure_and_children',
                    'recursion level=' || ani_recursion_level
                    || 'RT_Id='|| To_Char(ari_resulttype.result_type_id)
                    || ', attribute=' || ari_resultType.resultType
                    || ', Assay=' || To_Char(ari_resultType.assay_id)
                    || ', AID='|| To_Char(ari_resultType.aid));

        WHEN OTHERS
        THEN
            RAISE;

    END save_measure_and_children;

    function save_measure (ani_parent_measure_id IN NUMBER,
                          ari_measure IN r_resulttype)
        RETURN NUMBER
    AS
        ln_measure_id     NUMBER := null;
        Ln_entry_unit_id  NUMBER;

        CURSOR cur_measure
        IS
        SELECT measure_id
        FROM measure
        WHERE assay_id = ari_measure.assay_id
          AND result_type_id = ari_measure.result_type_id
          AND Nvl(stats_modifier_id, -100) = Nvl(ari_measure.stats_modifier_id, -100)
          AND Nvl(parent_measure_id, -200) = Nvl(ani_parent_measure_id, -200);

    BEGIN
        -- check the arguments
         IF ari_measure.assay_id IS NULL
        OR ari_measure.result_type_id IS NULL
        THEN
            ln_measure_id := -1;
            RETURN ln_measure_id;
        END IF;

       -- try to find an existing measure using the AK
        OPEN cur_measure;
        FETCH cur_measure INTO ln_measure_id;
        CLOSE cur_measure;

        -- if it exists update parent_measure_id and units
            -- and return the measure_id
        -- if not insert a new measure row
        IF ln_measure_id IS NULL
        THEN
            SELECT unit_id
            INTO ln_entry_unit_id
            FROM element
            WHERE element_id = ari_measure.result_type_id;

            SELECT measure_id_seq.NEXTVAL INTO ln_measure_id
            FROM dual;

            INSERT INTO measure
                (Measure_id,
                 assay_id,
                 result_type_id,
                 stats_modifier_id,
                 entry_unit_id,
                 parent_measure_id,
                 version,
                 date_created,
                 last_updated,
                 modified_by)
            VALUES
                (ln_Measure_id,
                 ari_measure.assay_id,
                 ari_measure.result_type_id,
                 ari_measure.stats_modifier_id,
                 ln_entry_unit_id,
                 ani_parent_measure_id,
                 0,
                 sysdate,
                 null,
                 pv_modified_by);

        END IF;

        RETURN ln_measure_id;

    END save_measure;

    FUNCTION save_context_item_set (ari_resultType  IN  r_resultType)
        RETURN NUMBER
    AS
        CURSOR cur_matching_context
        IS
        SELECT aci.assay_context_id
        FROM temp_context_item tci,
            assay_context_item aci,
            assay_context ac
        WHERE ac.assay_context_id = aci.assay_context_id
          AND ac.assay_id = tci.assay_id
          AND aci.attribute_id = tci.attribute_id
          AND Nvl(aci.value_num, -99999.999) = Nvl(tci.concentration, -99999.999)
        GROUP BY aci.assay_context_id
        HAVING Count(*) = (SELECT Count(*) FRoM temp_context_item)
          AND Count(*) = (SELECT Count(*) FroM assay_context_item aci2
                                          WHERE aci2.assay_context_id = aci.assay_context_id);

        CURSOR cur_temp_context_item
        IS
        SELECT display_order,
             ext_assay_ref,
             assay_id,
             experiment_id,
             attribute_id,
             aid,
             resulttype,
             stats_modifier,
             contextitem,
             concentration,
             concentrationunit,
             Decode ((SELECT Count(*)
                     FROM temp_context_item tci2
                     WHERE tci2.contextItem = tci.contextItem),
                     0,'Free',
                     1,'Free',
                     'List') attribute_type
        FROM temp_context_item tci
        ORDER BY display_order;

        ln_assay_context_id NUMBER := null;

    BEGIN

        -- clean out the temp table
        DELETE FROM temp_context_item;
        -- get the context_item set from the result_map table
        -- AND save IN a TEMPORARY table
        -- need special care to find a set of 'List' type items
            -- look for the attribute without the value
            -- insert items with values with a check to prevent inserting duplicates
        -- save the items in a temp table
        INSERT INTO temp_context_item
            (display_order,
             ext_assay_ref,
             assay_id,
             experiment_id,
             attribute_id,
             aid,
             resulttype,
             stats_modifier,
             contextitem,
             concentration,
             concentrationunit)
        SELECT ROWNUM - 1 display_order,
              ci.*
        FROM (SELECT er.ext_assay_ref,
                  e.assay_id,
                  e.experiment_id,
                  el_ci.element_id attribute_id,
                  rm.*
              FROM (SELECT rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.contextItem, rm_ci.concentration, rm_ci.concentrationunit
                  FROM southern.result_map rm_rt,
                      southern.result_map rm_ci
                  WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_rt.tid
                    AND rm_ci.aid = rm_rt.aid
                    AND rm_ci.contextItem IS NOT null
                    AND rm_rt.resultType = ari_resultType.resultType
                    AND rm_rt.resultType IS NOT null
                    AND Nvl(rm_rt.stats_modifier, '#####') = Nvl(ari_resultType.stats_modifier, '#####')
                    AND rm_rt.aid = ari_resultType.aid
                  GROUP BY rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.contextItem, rm_ci.concentration, rm_ci.concentrationunit) rm,
                  element el_ci,
                  external_reference er,
                  experiment e
              WHERE er.ext_assay_ref = 'aid=' || rm.aid
                AND e.experiment_id = er.experiment_id
                AND el_ci.label (+) = rm.contextItem
                ORDER BY rm.aid, rm.contextItem, rm.concentration) ci;

        IF SQL%ROWCOUNT = 0
        THEN return NULL;
            -- nothing got inserted, so we can make a quick exit
        END IF;

        -- query to find an existing matching set
        OPEN cur_matching_context;
        FETCH cur_matching_context INTO ln_assay_context_id;
        CLOSE cur_matching_context;

        IF ln_assay_context_id IS NULL
        THEN
            -- not found create an assay_context
            SELECT assay_context_id_seq.NEXTVAL
            INTO ln_assay_context_id
            FROM dual;

            INSERT INTO assay_context
                (assay_context_id,
                 assay_id,
                 context_name,
                 context_group,
                 display_order,
                 modified_by)
            VALUES
                (ln_assay_context_id,
                 ari_resultType.assay_id,
                 'Type a name',
                 'TBD',
                 0,
                 pv_modified_by);
            -- cycle thru the items saving each as you go
            FOR lr_context_item IN  cur_temp_context_item
            LOOP
                -- check the inputs
                IF lr_context_item.attribute_id IS NULL
                    OR
                    lr_context_item.assay_id is NULL
                THEN
                    -- log the error;
                    log_error (-90002, 'Attribute not in Element', 'save_context_item_set',
                            'resultType=' || lr_context_item.resultType || lr_context_item.stats_modifier
                            || ', Attr_Id='|| To_Char(lr_context_item.attribute_id)
                            || ', attribute=' || lr_context_item.contextItem
                            || ', Assay=' || To_Char(lr_context_item.assay_id)
                            || ', AID='|| To_Char(lr_context_item.aid));


                else
                    INSERT INTO assay_context_item
                        (assay_context_item_id,
                        assay_context_id,
                        display_order,
                        attribute_type,
                        attribute_id,
                        --value_id,
                        value_display,
                        value_num,
                        modified_by)
                    SELECT
                        assay_context_item_id_seq.nextval,
                        ln_assay_context_id,
                        lr_context_item.display_order,
                        lr_context_item.attribute_type,
                        lr_context_item.attribute_id,
                        --value_id,
                        lr_context_item.concentration || ' ' || lr_context_item.concentrationunit value_display,
                        lr_context_item.concentration value_num,
                        pv_modified_by
                    FROM dual;
                END IF;

            END LOOP;
        END IF;

        RETURN ln_assay_context_id;

    END save_context_item_set;

    PROCEDURE save_assay_context_measure (ani_assay_context_id IN number,
                               ani_measure_id IN number)
    AS

    BEGIN
        -- try inserting but don't overwrite an existing entry
        INSERT INTO assay_context_measure
            (assay_context_measure_id,
             assay_context_id,
             measure_id,
             modified_by)
        SELECT assay_context_measure_id_seq.nextval,
             ani_assay_context_id,
             ani_measure_id,
             pv_modified_by
        FROM dual
        WHERE NOT EXISTS (SELECT 1
              FROM assay_context_measure acm
              WHERE assay_context_id = ani_assay_context_id
                AND measure_id = ani_measure_id);

    END save_assay_context_measure;

    PROCEDURE save_exprmt_measure (ani_experiment_id IN number,
                                ani_measure_id IN number,
                                ani_parent_measure_id IN NUMBER,
                                avi_relationship IN varchar2)
    AS
         CURSOR cur_exprmt_measure
         IS
         SELECT exprmt_measure_id
         FROM exprmt_measure
         WHERE experiment_id = ani_experiment_id
           AND measure_id = ani_parent_measure_id;

        ln_parent_exprmt_measure_id NUMBER := null;

    BEGIN
        -- cleanup typos in the relationship
--        lv_relationship := Decode (Lower(Trim(avi_relationship)),
--                                  'derives', 'Derived from',
--                                  'child', 'has Child',
--                                  'sibling', 'has Sibling',
--                                  NULL);
        -- discover the ID of the parent exprmt_measure
        -- This works because we are walking down the parentage tree
        OPEN cur_exprmt_measure;
        FETCH cur_exprmt_measure INTO ln_parent_exprmt_measure_id;
        CLOSE cur_exprmt_measure;
        --- doesn't matter if we don't find a parent, cos the measure might not have one!

        -- now insert the exprmt_measure
        INSERT INTO exprmt_measure
            (exprmt_measure_id,
            parent_exprmt_measure_id,
            parent_child_relationship,
            experiment_id,
            measure_id,
            modified_by)
        SELECT exprmt_measure_id_seq.nextval,
            ln_parent_exprmt_measure_id,
            Decode (Lower(Trim(avi_relationship)),
                                  'derives', 'Derived from',
                                  'child', 'has Child',
                                  'sibling', 'has Sibling',
                                  NULL),
            ani_experiment_id,
            ani_measure_id,
            pv_modified_by
        FROM dual
        WHERE NOT EXISTS (SELECT 1
              FROM exprmt_measure
              WHERE experiment_id = ani_experiment_id
                AND measure_id = ani_measure_id
                AND Nvl(parent_exprmt_measure_id, -600) = Nvl(ln_parent_exprmt_measure_id, -600));

    END save_exprmt_measure;

    procedure log_error (an_errnum    in  number,
                        av_errmsg     in varchar2,
                        av_location   in varchar2,
                        av_comment    in varchar2 default null)
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

END result_map_util;