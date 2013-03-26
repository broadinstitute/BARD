-- find all the component items that a name would depend on
SELECT
--      DISTINCT ay.assay_id
       ac.assay_id,
       ay.assay_short_name,
       ay.assay_name,
       ac.assay_context_id,
       ac.display_order group_display_order,
       ac.context_name,
       aci.assay_context_item_id,
       aci.display_order item_display_order,
       el.label attribute,
       aci.value_display,
       aci.attribute_id,
       aci.value_id,
       aci.ext_value_id,
       a.items,
       a.GROUPS,
       a.avg
FROM ASSAY_CONTEXT AC,
    assay_context_item aci,
    element el,
    assay ay,
    (SELECT ac.assay_id, Count(*) items,
          Count(DISTINCT ac.assay_context_id) GROUPS,
          Round(Count(*)/Count(DISTINCT ac.assay_context_id),2)  avg
    FROM assay_context ac,
        assay_context_item aci
    WHERE ac.assay_context_id = aci.assay_context_id
      AND NOT EXISTS (SELECT 1
          FROM ASSAY_CONTEXT AC2
          WHERE AC2.ASSAY_ID = AC.ASSAY_ID
            AND AC.modified_by IN ('BAO_1000'))
    GROUP BY ac.assay_id
    HAVING Count(*) > 5
      AND Count(*) !=  Count(DISTINCT ac.assay_context_id)) a
WHERE aci.assay_context_id = ac.assay_context_id
AND el.element_id = aci.attribute_id
AND ay.assay_id = ac.assay_id
AND a.assay_id (+) = ac.assay_id
--AND ac.assay_id = 326
--AND ay.assay_short_name LIKE '%...'
--AND ac.context_name  like '%biolog%'
--AND el.label IN ('biological process', 'GO process term', 'molecular interaction')
ORDER BY ac.assay_id, ac.display_order, aci.display_order;

-----------------------------------------------------------------------------------------------------------------
--  cleanup the target names
----------------------------------------------------------------------------------------------------------------





----------------------------------------------------------------------------------------------------------------------
--  create the stored procedure
----------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE procedure update_assay_short_name
                    (ani_assay_id IN NUMBER  DEFAULT NULL)
                  --RETURN varchar2
AS
    -- schatwin    11/21/2012   initial version
    -- schatwin    12/17/12     excluded 'Context for...' names
    --
    --
    -----------------------------------------------------------------------------------------
    CURSOR cur_assay (cn_assay_id NUMBER)
    IS
    SELECT assay_id, assay_type
    FROM assay a
    WHERE EXISTS (SELECT 1
        FROM assay_context ac
        WHERE ac.assay_id = a.assay_id)
      AND ( assay_id = cn_assay_id
            OR
            cn_assay_id IS NULL);


    CURSOR cur_assay_context_item (cn_assay_id NUMBER )
    IS
    SELECT ac.assay_id,
       ac.assay_context_id,
       aci.assay_context_item_id,
       ac.display_order group_display_order,
       aci.display_order item_display_order,
       ac.context_name,
       el.label attribute,
       aci.value_display,
       aci.attribute_id,
       aci.value_id,
       aci.ext_value_id
    FROM assay_context_item aci,
        assay_context ac,
        element el
    WHERE aci.assay_context_id = ac.assay_context_id
      AND el.element_id = aci.attribute_id
      AND ac.assay_id = cn_assay_id
      ORDER BY ac.assay_id, ac.display_order, aci.display_order;

    lv_assay_format   VARCHAR2(500);
    lv_assay_type     VARCHAR2(500);
    lv_biology        VARCHAR2(500);
    lv_target         VARCHAR2(500);
    lv_target_gene    VARCHAR2(500);
    lv_biology_gene   VARCHAR2(500);
    lv_detection      VARCHAR2(500);

    lv_short_name     VARCHAR2(4000);

    ln_trim_length    NUMBER;
    ln_assay_format   NUMBER;
    ln_assay_type     NUMBER;
    ln_biology        NUMBER;
    ln_target         NUMBER;
    ln_detection     NUMBER;
    ---------------------------------------------------------------------------------------------------

    FUNCTION add_term (avi_string IN VARCHAR2,
                       avi_term IN VARCHAR2)
            RETURN VARCHAR2
    AS
        lv_return VARCHAR2(1000);
        lv_many_suffix VARCHAR2 (10) := 'et al';
    BEGIN
        IF avi_string IS NULL
        THEN
              lv_return := avi_term;
        ELSIF SubStr(avi_string, - Length(lv_many_suffix)) = lv_many_suffix
        THEN
              lv_return := avi_string;
        ELSIF InStr(avi_string, avi_term) > 0
        THEN
              lv_return := avi_string;
        ELSE
              lv_return := avi_string || ' ' || lv_many_suffix;
        END IF;

        RETURN lv_return;
    END add_term;

BEGIN
    -- we need assay format, type and biology, target and detection method
    -- this procedure relies on the context names to identify items.
    -- Must use UPDATE_CONTEXT_NAME(an_assay_id) first to standardize the names.
    -- then rollback that transform after we've compiled the short_name to revert
    --     to the user's context_names
    -- use the structure:
    --     <assay format> <assay type> on <biology> with <target> using <detection>
    -- notice that these names may not be unique!!  need advice...

    -- for each assay cycle thru the context items, picking out the ones we need.
    -- if we get multiples, just add ' et al' to keep it short
    -- if we reach the end without finding a value to put in, use some defaults

    -- when we've got all the components of the name, concatenate them into a
    --     readable sentence and abbreviate to fit the 250 char limit

    -- depending on Assay_type we can do different things

    FOR lr_assay IN cur_assay (ani_assay_id)
    LOOP
        update_context_name (lr_assay.assay_id);

        lv_assay_format   := '';
        lv_assay_type     := '';
        lv_biology        := '';
        lv_target         := '';
        lv_target_gene    := '';
        lv_biology_gene   := '';
        lv_detection      := '';
        lv_short_name     := '';

        FOR lr_item IN cur_assay_context_item( lr_assay.assay_id)
        LOOP
          IF lr_item.attribute = 'assay format'
          THEN
              lv_assay_format := add_term( lv_assay_format, lr_item.value_display);

          ELSIF lr_item.attribute = 'assay type'
          THEN
              lv_assay_type := add_term( lv_assay_type, lr_item.value_display);

          ELSIF lr_item.attribute in ('gene (Entrez)', 'OMIM term')
                AND lr_item.context_name != 'target'
          THEN
              lv_biology_gene := add_term( lv_biology_gene, lr_item.value_display);

          ELSIF lr_item.attribute in ('biological process', 'GO process term', 'molecular interaction')
          THEN
              lv_biology := add_term( lv_biology, lr_item.value_display);

          ELSIF lr_item.context_name = 'target'
          THEN
              IF lr_item.attribute LIKE '%gene%'
              THEN
                  lv_target_gene := add_term( lv_target_gene, lr_item.value_display);
              ELSIF lr_item.attribute in ('assay reagent name', 'assay component', 'assay component name')
                 OR
                 lr_item.attribute LIKE ('%cell%')
                 OR
                 lr_item.attribute LIKE ('%protein%')
              THEN
                  lv_target := add_term( lv_target, lr_item.value_display);
              END IF;

          ELSIF lr_item.attribute in ('assay method', 'readout type')
                 OR
                 lr_item.attribute LIKE ('%detection method%')
          THEN
              lv_detection := add_term( lv_detection, lr_item.value_display);

          ELSE
              NULL;
          END IF;


        END LOOP;
        ROLLBACK;   -- to remove the temporary changes to the context_names

        ------------------------------------------------------------------------------
        -- sort outthe preferences for bioogy and target, put in default values
        ----------------------------------------------------------------------------
        lv_assay_format := Nvl( lv_assay_format, 'no format');
        lv_assay_type := Nvl( lv_assay_type, 'no type');
        lv_biology := Nvl( lv_biology, Nvl(lv_biology_gene, 'undefined'));
        IF Length(lv_target_gene) > 0
        THEN
            lv_target := lv_target_gene;
        END IF;
        lv_target := Nvl( lv_target, 'phenotypic');
        lv_detection := Nvl( lv_detection, 'unknown detection');

        -- Now assemble the short_name
        -- first determine lengths and how to distribute the abrbevation (truncating)
        ln_trim_length := Length (lv_assay_format) + Length(lv_assay_type) + Length(lv_biology) + Length(lv_target) + Length(lv_detection) - 220;
        ln_assay_format := Length( lv_assay_format);
        ln_assay_type := Length( lv_assay_type);
        ln_biology := Length( lv_biology);
        ln_target := Length( lv_target);
        ln_detection := Length( lv_detection);
        IF ln_trim_length > 0
        THEN
            IF ln_assay_format > 44
            THEN
                lv_assay_format := SubStr(lv_assay_format, 1, ln_assay_format - Trunc(ln_trim_length/5));
            END IF;
            IF ln_assay_type > 44
            THEN
                lv_assay_type := SubStr(lv_assay_type, 1, ln_assay_type - Trunc(ln_trim_length/5));
            END IF;
            IF ln_biology > 44
            THEN
                lv_biology := SubStr(lv_biology, 1, ln_biology - Trunc(ln_trim_length/5));
            END IF;
            IF ln_detection > 44
            THEN
                lv_detection := SubStr(lv_detection, 1, ln_detection - Trunc(ln_trim_length/5));
            END IF;
            IF ln_assay_type > 44
            THEN
                lv_target := SubStr(lv_target, 1, ln_target - Trunc(ln_trim_length/5));
            END IF;
        END IF;

        ----------------------------------------------------------------------------------------
        -- assemble the name
        ----------------------------------------------------------------------------------------
        lv_short_name := lv_target;
        lv_short_name := lv_short_name || '; ' ||lv_assay_format || '; ' || lv_assay_type;
        IF lv_biology != 'undefined'
        THEN
            lv_short_name := lv_short_name || '; on ' || lv_biology;
        END IF;
        IF lv_detection != 'unknown detection'
        THEN
            lv_short_name := lv_short_name || '; using ' || lv_detection;
        END IF;
        --
        ---------------------------------------------------------------------------
        --
        IF Length(lv_short_name) > 250
        THEN
            lv_short_name := SubStr(lv_short_name, 1, 247) || '...';
        END IF;
        -- update the assay record
        UPDATE assay
        SET assay_short_name = lv_short_name
        WHERE assay_id = lr_assay.assay_id;

        commit;    -- MUST do this each assay or the next assay will rollback this good work!

    END LOOP;


--EXCEPTION
--  WHEN OTHERS THEN
--      NULL;

END;
/

----------------------------------------------------------------------------------------------------------------------
--  and run it (takes between 3/4 and 3 minutes)
----------------------------------------------------------------------------------------------------------------------


BEGIN
    update_assay_short_name('');
END;
/

----------------------------------------------------------------------------------------------------------------------
--  find some sample names that seem reasonable
----------------------------------------------------------------------------------------------------------------------

SELECT ext_assay_ref, a.assay_id, assay_name, assay_short_name
FROM assay a,
    experiment e,
    external_reference er
WHERE a.assay_id = e.assay_id
AND e.experiment_id = er.experiment_id
AND a.assay_short_name NOT LIKE  '%no format, no type%'
ORDER BY assay_id;

--AND look FOR potential duplicates
SELECT assay_short_name,
Count(*),
listagg(assay_id, ', ') WITHIN GROUP (ORDER BY assay_id) ASSAY_idS
FROM assay
WHERE assay_short_name NOT IN ('TBD', 'phenotypic, no format, no type')
GROUP BY assay_short_name
HAVING Count(*) >1
ORDER BY 2 DESC, Lower(assay_short_name);
