CREATE GLOBAL TEMPORARY TABLE temp_context_item (
  assay_id       NUMBER(19,0)  NOT NULL,
  attribute_id   NUMBER(19,0)  NULL,
  attribute_type VARCHAR2(20)  NULL,
  display_order  NUMBER(5,0)   NULL,
  value_num      FLOAT(20)     NULL,
  unit           VARCHAR2(30)  NULL,
  value_id       NUMBER(19,0)  NULL,
  ext_value_id   VARCHAR2(60)  NULL,
  value_min      NUMBER(19,0)  NULL,
  value_max      NUMBER(19,0)  NULL,
  aid            NUMBER(19,0)  NOT NULL,
  resulttype     VARCHAR2(384) NULL,
  statsmodifier  VARCHAR2(384) NULL,
  contextitem    VARCHAR2(384) NULL,
  value1         VARCHAR2(384) NULL
)
  ON COMMIT DELETE ROWS
/

CREATE OR REPLACE PACKAGE manage_names
AS
    type r_context IS RECORD (context_id assay_context.assay_context_id%type,
                         context_name assay_context.context_name%TYPE,
                         context_group assay_context.context_group%TYPE);

    TYPE t_contexts IS TABLE OF r_context
        index BY BINARY_INTEGER;

    procedure update_assay_short_name (ani_assay_id IN NUMBER  DEFAULT NULL);

    PROCEDURE update_context_name (an_assay_id IN NUMBER DEFAULT NULL);
    PROCEDURE update_context_group (an_assay_id IN NUMBER DEFAULT NULL);

    PROCEDURE update_project_context_name (an_project_id IN NUMBER DEFAULT NULL);
    PROCEDURE update_project_context_group (an_project_id IN NUMBER DEFAULT NULL);

    PROCEDURE update_exprmt_context_name (an_exprmt_id IN NUMBER DEFAULT NULL);
    PROCEDURE update_exprmt_context_group (an_exprmt_id IN NUMBER DEFAULT NULL);

    PROCEDURE update_step_context_name (an_project_step_id IN NUMBER DEFAULT NULL);
    PROCEDURE update_step_context_group (an_project_step_id IN NUMBER DEFAULT NULL);

-----------------------------------------------------------------------------------
-- these should all be private procedures  when in production
-----------------------------------------------------------------------------------
    PROCEDURE make_context_name
          (an_assay_id IN NUMBER,
           ato_contexts OUT t_contexts);

    PROCEDURE make_project_context_name
          (an_project_id IN NUMBER,
           ato_contexts OUT t_contexts);

    PROCEDURE make_exprmt_context_name
          (an_exprmt_id IN NUMBER,
           ato_contexts OUT t_contexts);

    PROCEDURE make_step_context_name
          (an_project_step_id IN NUMBER,
           ato_contexts OUT t_contexts);

END manage_names;
/

CREATE OR REPLACE PACKAGE BODY manage_names
AS
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

    PROCEDURE make_context_name
      (an_assay_id IN NUMBER,
       ato_contexts OUT t_contexts)


    AS
    -------------------------------------------------------------------------------------------------------------
    --   make context name
    --     parameter:  Assay_ID.  if no assay_id is given then all assays in the DB are updated
    --
    --     This uses a set of arbitrary rules that appear to make sense for most cases
    --     1. if there's only 1 item, name the context for the item
    --     2. for components, name for the role or the type in that order
    --     3. for biology (secondary to components) use the cell, protein or other type of macromolcule
    --     4. for experiment contexts, use the most common attribute name (usually the one with the longest list)
    --
    --    schatwin    intial version    12-15-2012
    --    schatwin    exclude Annotation ones   1-21-2013
    --    schatwin    rename Annotation nes with commonest attribute name
    --
    --
    -------------------------------------------------------------------------------------------------------------
        CURSOR cur_assay_context (cn_assay_id IN NUMBER)
        IS
        SELECT grp_attr.assay_id,
          grp_attr.assay_context_id,
          Sum(grp_attr.aci_count) aci_count,
          LISTAGG(grp_attr.ATTRIBUTE || '$# ' || grp_attr.ATTRIBUTE_TYPE, ';') WITHIN GROUP (ORDER BY grp_attr.aci_count desc) attributes
        FROM (SELECT ac.assay_id,
                ac.assay_context_id,
                e.label attribute,
                aci.attribute_type,
                Count(*) aci_count
                FROM assay_context ac,
                    assay_context_item aci,
                    element e
                WHERE aci.assay_context_id = ac.assay_context_id
                  AND e.element_id = aci.attribute_id
                  AND ac.assay_id = cn_assay_id
                GROUP BY ac.assay_id,
                      ac.assay_context_id,
                      e.label,
                      aci.attribute_type) grp_attr
        GROUP BY grp_attr.assay_id,
                grp_attr.assay_context_id;

        lv_context_name  element.label%TYPE;
        lv_context_group  assay_context.context_group%TYPE;
        i   BINARY_INTEGER := 0;

    BEGIN
        -- get a list of assay_contexts with a count of the items
          FOR lr_assay_context IN cur_assay_context (an_assay_id)
          LOOP
              IF lr_assay_context.aci_count = 1
              THEN
    --    if count of items = 1
    --    then set name  = attribute label
                  SELECT label
                  INTO lv_context_name
                  FROM element e,
                      assay_context_item aci
                  WHERE aci.assay_context_id = lr_assay_context.assay_context_id
                    -- we're guaranteed just one from the count(*) in the curosr
                    AND e.element_id = aci.attribute_id;

                  SELECT SubStr(full_path, InStr(full_path, '>') +2, InStr(full_path, '>', 1,3)-InStr(full_path, '>'))
                  INTO lv_context_group
                  FROM bard_tree
                  WHERE label = lv_context_name
                  AND ROWNUM = 1;

                  lv_context_group := Nvl(lv_context_group, 'unclassified>');

            ELSIF lr_assay_context.attributes LIKE '%assay component role%'
            THEN
    --    if group contains 'assay component role'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM assay_context_item
                WHERE assay_context_id = lr_assay_context.assay_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component role')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_assay_context.attributes LIKE '%assay component type%'
            THEN
    --    if group contains 'assay component type'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM assay_context_item
                WHERE assay_context_id = lr_assay_context.assay_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component type')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_assay_context.attributes LIKE '%detection%'
            THEN
    --    if group contains 'detection'
    --    then set name = value
                lv_context_name := 'detection method';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_assay_context.attributes LIKE '%List%'
                OR lr_assay_context.attributes LIKE '%Free%'
                OR lr_assay_context.attributes LIKE '%Range%'
            THEN
    --    if group contains items that are set in the Experiment
    --    then set name to the plural of the the most common attribute
    --    the query makes this the first attribute listed
                  lv_context_name := SubStr(lr_assay_context.attributes, 1, InStr(lr_assay_context.attributes, '$# ' ) - 1) || 's';

                  lv_context_group := 'project management> experiment>';


            ELSIF lr_assay_context.attributes LIKE '%readout%'
            THEN
    --    if group contains 'assay readout'
    --    then set name = value
                lv_context_name := 'assay readout';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_assay_context.attributes LIKE '%wavelength%'
            THEN
    --    if group contains 'fluorescence/luminescence'
    --    then set name = value
                lv_context_name := 'fluorescence/luminescence';

                lv_context_group := 'assay protocol> assay design>';

            ELSIF lr_assay_context.attributes LIKE '%number%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'result detail';

                lv_context_group := 'project management> experiment>';

            ELSIF lr_assay_context.attributes LIKE '%biolog%'
            THEN
    --    if group contains 'biology/ical'
    --    then set name = value
                lv_context_name := 'biology';

                lv_context_group := 'biology>';

            ELSIF lr_assay_context.attributes LIKE '%protein%'
                OR lr_assay_context.attributes LIKE '%gene%'
                OR lr_assay_context.attributes LIKE '%cell%'
            THEN
    --    if group contains 'protein'
    --    then set name = value
                lv_context_name := 'biological component';
                lv_context_group := 'biology>';

            ELSIF lr_assay_context.attributes LIKE '%assay format%'
                    OR
                  lr_assay_context.attributes LIKE '%assay type%'
            THEN
    --    if group contains 'assay format'
    --    then set name = 'assay protocol'
                lv_context_name := 'assay protocol';
                lv_context_group := 'assay protocol> assay format>';

            ELSE
                --lv_context_name := NULL;
                lv_context_name := 'Type a name here';
                lv_context_group := 'unclassified>';
            END IF;

            IF lv_context_name IS NOT NULL
            THEN
                i := i + 1;
                ato_contexts(i).context_id := lr_assay_context.assay_context_id;
                ato_contexts(i).context_name := lv_context_name;
                ato_contexts(i).context_group := lv_context_group;

--              UPDATE assay_context ac
--              SET context_name = lv_context_name,
--                  context_group = lv_context_group
--              WHERE assay_context_id = lr_assay_context.assay_context_id;

            END IF;

          END LOOP;

          --commit;

    END make_context_name;

    PROCEDURE update_context_name
      (an_assay_id IN NUMBER DEFAULT NULL)

    AS
    -------------------------------------------------------------------------------------------------------------
    --   update context name
    --     parameter:  Assay_ID.  if no assay_id is given then all assays in the DB are updated
    --
    --     This uses loops thru the assays getting names and
    --    applying them to the contexts
    --
    --    schatwin    intial version    12-15-2012
    --    schatwin    exclude Annotation ones   1-21-2013
    --    schatwin    rename Annotation nes with commonest attribute name
    --    schatwin    converted from a procedure to a package   4-17-2013
    --
    -------------------------------------------------------------------------------------------------------------
        CURSOR cur_assay (cn_assay_id IN NUMBER)
        IS
        SELECT DISTINCT assay_id
        FROM assay_context ac
        WHERE ac.assay_id = Nvl(cn_assay_id, ac.assay_id);

        lv_context_name  element.label%TYPE;
        lv_context_group  assay_context.context_group%TYPE;
        lt_contexts       t_contexts;

    BEGIN
        -- get a list of assay_contexts with a count of the items
          FOR lr_assay IN cur_assay (an_assay_id)
          LOOP
             lt_contexts.DELETE;

             make_context_name( lr_assay.assay_id,
                                lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
              FOR i IN lt_contexts.first .. lt_contexts.last
              LOOP
                  UPDATE assay_context ac
                  SET context_name = lt_contexts(i).context_name,
                      context_group = lt_contexts(i).context_group
                  WHERE assay_context_id = lt_contexts(i).context_id;

              END LOOP;
            END IF;

          END LOOP;

          --commit;

    END update_context_name;

    PROCEDURE update_context_group
          (an_assay_id IN NUMBER DEFAULT NULL)
    AS

    -------------------------------------------------------------------------------------------------------------
    --   update context group
    --     parameter:  Assay_ID.  if no assay_id is given then all assays in the DB are updated
    --
    --     This uses loops thru the assays getting names and
    --    applying them to the contexts
    --
    --    schatwin    intial version    12-15-2012
    --    schatwin    exclude Annotation ones   1-21-2013
    --    schatwin    rename Annotation nes with commonest attribute name
    --    schatwin    converted from a procedure to a package   4-17-2013
    --
    -------------------------------------------------------------------------------------------------------------
        CURSOR cur_assay (cn_assay_id IN NUMBER)
        IS
        SELECT DISTINCT assay_id
        FROM assay_context ac
        WHERE ac.assay_id = Nvl(cn_assay_id, ac.assay_id);

        lv_context_name  element.label%TYPE;
        lv_context_group  assay_context.context_group%TYPE;
        lt_contexts       t_contexts;

    BEGIN
        -- get a list of assay_contexts with a count of the items
          FOR lr_assay IN cur_assay (an_assay_id)
          LOOP
             lt_contexts.DELETE;

             make_context_name( lr_assay.assay_id,
                                lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
              FOR i IN lt_contexts.first .. lt_contexts.last
              LOOP
                  UPDATE assay_context ac
                  SET context_group = lt_contexts(i).context_group
                  WHERE assay_context_id = lt_contexts(i).context_id;

              END LOOP;
            END IF;

          END LOOP;
    END update_context_group;

    PROCEDURE make_project_context_name
          (an_project_id IN NUMBER,
           ato_contexts OUT t_contexts)
    as
        CURSOR cur_project_context (cn_project_id IN NUMBER)
        IS
        SELECT ac.project_id,
                ac.project_context_id,
                Count(*) aci_count,
                LISTAGG(e.label, ';') WITHIN GROUP (ORDER BY aci.display_order) attributes
        FROM project_context ac,
              project_context_item aci,
              element e
        WHERE aci.project_context_id = ac.project_context_id
          AND e.element_id = aci.attribute_id
          AND ac.project_id = cn_project_id
        GROUP BY ac.project_id,
                ac.project_context_id;

        lv_context_name  element.label%TYPE;
        lv_context_group  project_context.context_group%TYPE;
        i BINARY_INTEGER  := 0;

    BEGIN
    --    get a list of project_contexts with a count of the items
          FOR lr_project_context IN cur_project_context (an_project_id)
          LOOP
              IF lr_project_context.aci_count = 1
              THEN
    --    if count of items = 1
    --    then set name  = attribute label
                  SELECT label
                  INTO lv_context_name
                  FROM element e,
                      project_context_item aci
                  WHERE aci.project_context_id = lr_project_context.project_context_id
                    -- we're guaranteed just one from the count(*) in the curosr
                    AND e.element_id = aci.attribute_id;

                  SELECT SubStr(full_path, InStr(full_path, '>') +2, InStr(full_path, '>', 1,3)-InStr(full_path, '>'))
                  INTO lv_context_group
                  FROM bard_tree
                  WHERE label = lv_context_name
                  AND ROWNUM = 1;

                  lv_context_group := Nvl(lv_context_group, 'unclassified>');

            ELSIF lr_project_context.attributes LIKE '%assay component role%'
            THEN
    --    if group contains 'assay component role'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM project_context_item
                WHERE project_context_id = lr_project_context.project_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component role')
                  AND ROWNUM = 1;

                  lv_context_group := 'project information> component>';

            ELSIF lr_project_context.attributes LIKE '%assay component type%'
            THEN
    --    if group contains 'assay component type'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM project_context_item
                WHERE project_context_id = lr_project_context.project_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component type')
                  AND ROWNUM = 1;

                  lv_context_group := 'project information> component>';

            ELSIF lr_project_context.attributes LIKE '%detection%'
            THEN
    --    if group contains 'detection'
    --    then set name = value
                lv_context_name := 'detection method';
                lv_context_group := 'project information> readout>';

            ELSIF lr_project_context.attributes LIKE '%readout%'
            THEN
    --    if group contains 'assay readout'
    --    then set name = value
                lv_context_name := 'assay readout';

            ELSIF lr_project_context.attributes LIKE '%wavelength%'
            THEN
    --    if group contains 'fluorescence/luminescence'
    --    then set name = value
                lv_context_name := 'fluorescence/luminescence';
                lv_context_group := 'project information> readout>';

            ELSIF lr_project_context.attributes LIKE '%laboratory%'
                OR lr_project_context.attributes LIKE '%grant%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'project management';
                lv_context_group := 'project information> project management>';

            ELSIF lr_project_context.attributes LIKE '%protein%'
                OR lr_project_context.attributes LIKE '%gene%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'biological component';
                lv_context_group := 'biology>';

            ELSIF lr_project_context.attributes LIKE '%number%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'result detail';
                lv_context_group := 'project management> experiment>';

            ELSIF lr_project_context.attributes LIKE '%biolog%'
            THEN
    --    if group contains 'biology/ical'
    --    then set name = value
                lv_context_name := 'biology';
                lv_context_group := 'biology>';

            ELSIF lr_project_context.attributes LIKE '%assay format%'
                    OR
                  lr_project_context.attributes LIKE '%assay type%'
            THEN
    --    if group contains 'assay format'
    --    then set name = 'assay protocol'
                lv_context_name := 'assay protocol';
                lv_context_group := 'project information> protocol>';

            ELSE
                --lv_context_name := NULL;
                lv_context_name := 'Type a name here';
                lv_context_group := 'unclassified>';
            END IF;

            IF lv_context_name IS NOT NULL
            THEN
                i := i + 1;
                ato_contexts(i).context_name := lv_context_name;
                ato_contexts(i).context_group := lv_context_group;
                ato_contexts(i).context_id := lr_project_context.project_context_id;

            END IF;

          END LOOP;
    END make_project_context_name;

    PROCEDURE update_project_context_name
      (an_project_id IN NUMBER DEFAULT NULL)

    AS
        CURSOR cur_project (cn_project_id IN NUMBER)
        IS
        SELECT DISTINCT ac.project_id
        FROM project_context ac
        WHERE ac.project_id = Nvl(cn_project_id, ac.project_id);

        lt_contexts   t_contexts;

    BEGIN
    --    get a list of project_contexts with a count of the items
          FOR lr_project IN cur_project (an_project_id)
          LOOP
            lt_contexts.DELETE;
            make_project_context_name(lr_project.project_id,
                                      lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
                FOR i IN lt_contexts.first .. lt_contexts.last
                LOOP
                    UPDATE project_context ac
                    SET context_name = lt_contexts(i).context_name,
                        context_group = lt_contexts(i).context_group
                    WHERE project_context_id = lt_contexts(i).context_id;

                END LOOP;


            END IF;

          END LOOP;

          --commit;

    END update_project_context_name;


    PROCEDURE update_project_context_group (an_project_id IN NUMBER DEFAULT NULL)

    AS
        CURSOR cur_project (cn_project_id IN NUMBER)
        IS
        SELECT DISTINCT ac.project_id
        FROM project_context ac
        WHERE ac.project_id = Nvl(cn_project_id, ac.project_id);

        lt_contexts   t_contexts;

    BEGIN
    --    get a list of project_contexts with a count of the items
          FOR lr_project IN cur_project (an_project_id)
          LOOP
            lt_contexts.DELETE;
            make_project_context_name(lr_project.project_id,
                                      lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
                FOR i IN lt_contexts.first .. lt_contexts.last
                LOOP
                    UPDATE project_context ac
                    SET context_group = lt_contexts(i).context_group
                    WHERE project_context_id = lt_contexts(i).context_id;

                END LOOP;


            END IF;

          END LOOP;

          --commit;
     END update_project_context_group;

    PROCEDURE make_exprmt_context_name
          (an_exprmt_id IN NUMBER,
           ato_contexts OUT t_contexts)
     AS
        CURSOR cur_exprmt_context (cn_exprmt_id IN NUMBER)
        IS
        SELECT ac.experiment_id,
                ac.exprmt_context_id,
                Count(*) aci_count,
                LISTAGG(e.label, ';') WITHIN GROUP (ORDER BY aci.display_order) attributes
        FROM exprmt_context ac,
              exprmt_context_item aci,
              element e
        WHERE aci.exprmt_context_id = ac.exprmt_context_id
          AND e.element_id = aci.attribute_id
          AND ac.experiment_id = cn_exprmt_id
         GROUP BY ac.experiment_id,
                ac.exprmt_context_id;

        lv_context_name  exprmt_context.context_name%TYPE;
        lv_context_group  exprmt_context.context_group%TYPE;
        i   BINARY_INTEGER  := 0;

    BEGIN
    --    get a list of exprmt_contexts with a count of the items
          FOR lr_exprmt_context IN cur_exprmt_context (an_exprmt_id)
          LOOP
              IF lr_exprmt_context.aci_count = 1
              THEN
    --    if count of items = 1
    --    then set name  = attribute label
                  SELECT label
                  INTO lv_context_name
                  FROM element e,
                      exprmt_context_item aci
                  WHERE aci.exprmt_context_id = lr_exprmt_context.exprmt_context_id
                    -- we're guaranteed just one from the count(*) in the curosr
                    AND e.element_id = aci.attribute_id;

                  SELECT SubStr(full_path, InStr(full_path, '>') +2, InStr(full_path, '>', 1,3)-InStr(full_path, '>'))
                  INTO lv_context_group
                  FROM bard_tree
                  WHERE label = lv_context_name
                  AND ROWNUM = 1;

                  lv_context_group := Nvl(lv_context_group, 'unclassified>');

            ELSIF lr_exprmt_context.attributes LIKE '%assay component role%'
            THEN
    --    if group contains 'assay component role'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM exprmt_context_item
                WHERE exprmt_context_id = lr_exprmt_context.exprmt_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component role')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_exprmt_context.attributes LIKE '%assay component type%'
            THEN
    --    if group contains 'assay component type'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM exprmt_context_item
                WHERE exprmt_context_id = lr_exprmt_context.exprmt_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component type')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_exprmt_context.attributes LIKE '%detection%'
            THEN
    --    if group contains 'detection'
    --    then set name = value
                lv_context_name := 'detection method';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_exprmt_context.attributes LIKE '%readout%'
            THEN
    --    if group contains 'assay readout'
    --    then set name = value
                lv_context_name := 'assay readout';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_exprmt_context.attributes LIKE '%wavelength%'
            THEN
    --    if group contains 'fluorescence/luminescence'
    --    then set name = value
                lv_context_name := 'fluorescence/luminescence';

                lv_context_group := 'assay protocol> assay design>';

            ELSIF lr_exprmt_context.attributes LIKE '%number%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'result detail';

                lv_context_group := 'project management> experiment>';

            ELSIF lr_exprmt_context.attributes LIKE '%biolog%'
            THEN
    --    if group contains 'biology/ical'
    --    then set name = value
                lv_context_name := 'biology';

                lv_context_group := 'biology>';

            ELSIF lr_exprmt_context.attributes LIKE '%assay format%'
                    OR
                  lr_exprmt_context.attributes LIKE '%assay type%'
            THEN
    --    if group contains 'assay format'
    --    then set name = 'assay protocol'
                lv_context_name := 'assay protocol';

                lv_context_group := 'biology>';

            ELSE
                --lv_context_name := NULL;
                lv_context_name := 'Type a name here';
                lv_context_group := 'unclassified>';
            END IF;

            IF lv_context_name IS NOT NULL
            THEN
              i := i + 1;
              ato_contexts(i).context_name := lv_context_name;
              ato_contexts(i).context_group := lv_context_group;
              ato_contexts(i).context_id := lr_exprmt_context.exprmt_context_id;

            END IF;

          END LOOP;

     END make_exprmt_context_name;


    PROCEDURE update_exprmt_context_name
      (an_exprmt_id IN NUMBER DEFAULT NULL)

    AS
        CURSOR cur_experiment (cn_exprmt_id IN NUMBER)
        IS
        SELECT DISTINCT ac.experiment_id
        FROM exprmt_context ac
        WHERE ac.experiment_id = cn_exprmt_id;

        lt_contexts  t_contexts;

    BEGIN
    --    get a list of exprmt_contexts with a count of the items
          FOR lr_experiment IN cur_experiment (an_exprmt_id)
          LOOP
             lt_contexts.DELETE;
             make_exprmt_context_name(lr_experiment.experiment_id,
                                      lt_contexts);

             IF lt_contexts.last IS NOT NULL
             THEN
                FOR i IN lt_contexts.first .. lt_contexts.last
                LOOP
                    UPDATE exprmt_context ac
                    SET context_name = lt_contexts(i).context_name,
                        context_group = lt_contexts(i).context_group
                    WHERE exprmt_context_id = lt_contexts(i).context_id;

                END LOOP;

            END IF;

          END LOOP;

          --commit;

    END update_exprmt_context_name;

    PROCEDURE update_exprmt_context_group (an_exprmt_id IN NUMBER DEFAULT NULL)
    AS

        CURSOR cur_experiment (cn_exprmt_id IN NUMBER)
        IS
        SELECT DISTINCT ac.experiment_id
        FROM exprmt_context ac
        WHERE ac.experiment_id = cn_exprmt_id;

        lt_contexts  t_contexts;

    BEGIN
    --    get a list of exprmt_contexts with a count of the items
          FOR lr_experiment IN cur_experiment (an_exprmt_id)
          LOOP
             lt_contexts.DELETE;
             make_exprmt_context_name(lr_experiment.experiment_id,
                                      lt_contexts);

             IF lt_contexts.last IS NOT NULL
             THEN
                FOR i IN lt_contexts.first .. lt_contexts.last
                LOOP
                    UPDATE exprmt_context ac
                    SET context_group = lt_contexts(i).context_group
                    WHERE exprmt_context_id = lt_contexts(i).context_id;

                END LOOP;

            END IF;

          END LOOP;

    END update_exprmt_context_group;

    PROCEDURE make_step_context_name
          (an_project_step_id IN NUMBER,
           ato_contexts OUT t_contexts)
    AS
        CURSOR cur_step_context (cn_step_id IN NUMBER)
        IS
        SELECT ac.project_step_id,
                ac.step_context_id,
                Count(*) aci_count,
                LISTAGG(e.label, ';') WITHIN GROUP (ORDER BY aci.display_order) attributes
        FROM step_context ac,
              step_context_item aci,
              element e
        WHERE aci.step_context_id = ac.step_context_id
          AND e.element_id = aci.attribute_id
          AND ac.project_step_id = cn_step_id
        GROUP BY ac.project_step_id,
                ac.step_context_id;

        lv_context_name  step_context.context_name%TYPE;
        lv_context_group  step_context.context_group%TYPE;
        i   BINARY_INTEGER  := 0;
    BEGIN
    --    get a list of step_contexts with a count of the items
          FOR lr_step_context IN cur_step_context (an_project_step_id)
          LOOP
              IF lr_step_context.aci_count = 1
              THEN
    --    if count of items = 1
    --    then set name  = attribute label
                  SELECT label
                  INTO lv_context_name
                  FROM element e,
                      step_context_item aci
                  WHERE aci.step_context_id = lr_step_context.step_context_id
                    -- we're guaranteed just one from the count(*) in the curosr
                    AND e.element_id = aci.attribute_id;

                  SELECT SubStr(full_path, InStr(full_path, '>') +2, InStr(full_path, '>', 1,3)-InStr(full_path, '>'))
                  INTO lv_context_group
                  FROM bard_tree
                  WHERE label = lv_context_name
                  AND ROWNUM = 1;

                  lv_context_group := Nvl(lv_context_group, 'unclassified>');

            ELSIF lr_step_context.attributes LIKE '%assay component role%'
            THEN
    --    if group contains 'assay component role'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM step_context_item
                WHERE step_context_id = lr_step_context.step_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component role')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_step_context.attributes LIKE '%assay component type%'
            THEN
    --    if group contains 'assay component type'
    --    then set name = value
                SELECT value_display
                INTO lv_context_name
                FROM step_context_item
                WHERE step_context_id = lr_step_context.step_context_id
                  AND attribute_id =
                        (SELECT element_id
                        FROM element
                        WHERE label = 'assay component type')
                  AND ROWNUM = 1;

                  lv_context_group := 'assay protocol> assay component>';

            ELSIF lr_step_context.attributes LIKE '%detection%'
            THEN
    --    if group contains 'detection'
    --    then set name = value
                lv_context_name := 'detection method';

            ELSIF lr_step_context.attributes LIKE '%readout%'
            THEN
    --    if group contains 'assay readout'
    --    then set name = value
                lv_context_name := 'assay readout';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_step_context.attributes LIKE '%wavelength%'
            THEN
    --    if group contains 'fluorescence/luminescence'
    --    then set name = value
                lv_context_name := 'fluorescence/luminescence';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_step_context.attributes LIKE '%number%'
            THEN
    --    if group contains 'number'
    --    then set name = value
                lv_context_name := 'result detail';

                lv_context_group := 'assay protocol> assay readout>';

            ELSIF lr_step_context.attributes LIKE '%biolog%'
            THEN
    --    if group contains 'biology/ical'
    --    then set name = value
                lv_context_name := 'biology';

                lv_context_group := 'biology>';

            ELSIF lr_step_context.attributes LIKE '%assay format%'
                    OR
                  lr_step_context.attributes LIKE '%assay type%'
            THEN
    --    if group contains 'assay format'
    --    then set name = 'assay protocol'
                lv_context_name := 'assay protocol';

               lv_context_group := 'assay protocol> assay format>';

            ELSE
                lv_context_name := 'Type a name here';
                lv_context_group := 'unclassified>';
            END IF;

            IF lv_context_name IS NOT NULL
            THEN
              i := i+ 1;
              ato_contexts(i).context_name := lv_context_name;
              ato_contexts(i).context_group := lv_context_group;
              ato_contexts(i).context_id := lr_step_context.step_context_id;

            END IF;

          END LOOP;
    END make_step_context_name;



    PROCEDURE update_step_context_group (an_project_step_id IN NUMBER DEFAULT NULL)

    AS
        CURSOR cur_step (cn_step_id IN NUMBER)
        IS
        SELECT DISTINCT ac.project_step_id
        FROM step_context ac
        WHERE ac.project_step_id = cn_step_id;

        lt_contexts  t_contexts;

    BEGIN
    --    get a list of step_contexts with a count of the items
          FOR lr_step IN cur_step (an_project_step_id)
          LOOP
            lt_contexts.DELETE;
            make_step_context_name(lr_step.project_step_id,
                                  lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
              FOR i IN lt_contexts.first .. lt_contexts.last
              LOOP
                  UPDATE step_context ac
                  SET context_group = lt_contexts(i).context_group
                  WHERE step_context_id = lt_contexts(i).context_id;
              END LOOP;
            END IF;

          END LOOP;
    END update_step_context_group;


    PROCEDURE update_step_context_name
      (an_project_step_id IN NUMBER DEFAULT NULL)

    AS
        CURSOR cur_step (cn_step_id IN NUMBER)
        IS
        SELECT DISTINCT ac.project_step_id
        FROM step_context ac
        WHERE ac.project_step_id = cn_step_id;

        lt_contexts  t_contexts;

    BEGIN
    --    get a list of step_contexts with a count of the items
          FOR lr_step IN cur_step (an_project_step_id)
          LOOP
            lt_contexts.DELETE;
            make_step_context_name(lr_step.project_step_id,
                                  lt_contexts);

            IF lt_contexts.last IS NOT NULL
            THEN
              FOR i IN lt_contexts.first .. lt_contexts.last
              LOOP
                  UPDATE step_context ac
                  SET context_name = lt_contexts(i).context_name,
                      context_group = lt_contexts(i).context_group
                  WHERE step_context_id = lt_contexts(i).context_id;
              END LOOP;
            END IF;

          END LOOP;

          --commit;

    END update_step_context_name;

    procedure update_assay_short_name
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

              ELSIF lr_item.attribute in ('gene Entrez GI', 'OMIM term', 'Mesh term')
                    AND lr_item.context_name != 'target'
              THEN
                  lv_biology_gene := add_term( lv_biology_gene, lr_item.value_display);

              ELSIF lr_item.attribute in ('biological process', 'GO biological process term', 'molecular interaction')
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
                    lr_item.attribute LIKE ('%detection%')
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

    END update_assay_short_name;

END manage_names;
/



CREATE OR REPLACE PACKAGE "RESULT_MAP_UTIL" as

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
          AID             result_map.aid%type,
          RESULTTYPE      result_map.resultType%type,
          STATS_MODIFIER  result_map.stats_modifier%type,
--          relationship    result_map.relationship%TYPE,
          relationship    VARCHAR2(20),
          TID             result_map.tid%type,
          --entry_unit      result_map.concentrationunit%TYPE,
          entry_unit      VARCHAR2(10),
          SERIES_NOS      varchar2(4000),
          PARENT_TIDS     varchar2(4000)
          );

    --TYPE t_result_Maps IS TABLE OF result_map%rowtype;

    TYPE t_string IS varray (1) OF VARCHAR2(40);

    PROCEDURE transfer_result_map (avi_AID IN VARCHAR2 DEFAULT NULL);

    PROCEDURE save_measure_and_children (ani_recursion_level  IN binary_integer,
                                         ani_parent_measure_id IN NUMBER,
                                         ari_resulttype IN r_resulttype);

END Result_map_util;

/


CREATE OR REPLACE PACKAGE BODY "RESULT_MAP_UTIL" 
as

    pn_max_recursion_level CONSTANT BINARY_INTEGER := 5;

    pv_modified_by CONSTANT VARCHAR2(40) := 'resultmap';

    pb_Reuse_IDs  CONSTANT BOOLEAN := TRUE;

    function get_new_id (avi_sequence_name IN VARCHAR2)
        RETURN NUMBER
    AS
        ln_next_id  NUMBER;
        lv_table_name VARCHAR2(30);
        lv_column_name VARCHAR2(30);
        lv_sql          VARCHAR2(1000);
        lv_sequence_name  VARCHAR2(30);

    BEGIN
        lv_sequence_name := Upper (avi_sequence_name);

        IF lv_sequence_name NOT LIKE '%_ID_SEQ'
        THEN RETURN null;
        END IF;

        lv_table_name := REPLACE (lv_sequence_name, '_ID_SEQ', '');
        lv_column_name := REPLACE (lv_sequence_name, '_SEQ', '');

        IF pb_reuse_IDs
        THEN
            lv_sql := 'select ' || lv_column_name || ' + 1
                        from ' || lv_table_name || ' t1
                        where not exists (select 1
                                  from ' || lv_table_name || ' t2
                                  where t2.' || lv_column_name || ' = t1.' || lv_column_name || ' + 1)
                          and rownum = 1
                          and ' || lv_column_name || ' < (select max('|| lv_column_name || ')
                                      from ' || lv_table_name || ')';

        ELSE
            lv_sql := 'SELECT ' || lv_sequence_name || '.NEXTVAL FROM dual';

        END IF;
        -- Dbms_Output.put_line (lv_sql);
        begin
            EXECUTE IMMEDIATE lv_sql INTO ln_next_id;
        EXCEPTION
            WHEN No_Data_Found       -- means the table is completely empty
            then
                lv_sql := 'SELECT ' || lv_sequence_name || '.NEXTVAL FROM dual';
                EXECUTE IMMEDIATE lv_sql INTO ln_next_id;
            WHEN OTHERS
            THEN
                RAISE;
         END;

        -- dbms_output.put_line (lv_column_name || '= '|| To_Char(ln_next_id));
        RETURN ln_next_id;

    END get_new_id;

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

        DELETE FROM assay_context_item aci
        WHERE EXISTS (SELECT 1
            FROM assay_context ac
            WHERE ac.assay_context_id = aci.assay_context_id
            AND ac.assay_id = ani_assay_id)
        AND aci.modified_by = avi_owner;

        DELETE FROM assay_context ac
        WHERE  ac.assay_id = ani_assay_id
        AND NOT EXISTS (SELECT 1
            FROM assay_context_item aci
            WHERE aci.assay_context_id = ac.assay_context_id)
        AND NOT EXISTS (SELECT 1
            FROM assay_context_measure acm
            WHERE acm.assay_context_id = ac.assay_context_id);


    END delete_measure;

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
            log_error (-90005, 'No result type specified', 'Save_measure',
                'assay_ID= '|| To_Char(ari_measure.assay_id)
                || ', result_type_id= ' || To_Char(ari_measure.result_type_id)
                || ', AID= ' || To_Char(ari_measure.aid)
                || ', resultType= ' || ari_measure.resultType);
            RETURN ln_measure_id;
        END IF;
        IF (ani_parent_measure_id IS NULL AND ari_measure.relationship IS null)
            OR
            (ani_parent_measure_id IS NOT NULL AND ari_measure.relationship IS NOT null)
        THEN
          -- try to find an existing measure using the AK
            OPEN cur_measure;
            FETCH cur_measure INTO ln_measure_id;
            CLOSE cur_measure;

            -- if it exists update parent_measure_id and units
                -- and return the measure_id
            -- if not insert a new measure row
            IF ln_measure_id IS NULL
            THEN
                -- find the appropriate entry unit
                IF ari_measure.entry_unit IS NOT NULL
                THEN
                    BEGIN
                        -- use unit_tree 'cos this handles abreviations like Result_map does
                        SELECT unit_id
                        INTO ln_entry_unit_id
                        FROM unit_tree
                        WHERE unit = ari_measure.entry_unit;
                    EXCEPTION
                    WHEN OTHERS THEN
                        ln_entry_unit_id := NULL;
                    END;
                END IF;
                -- if we can't use or find the specified one, try the default for the result type
                IF ln_entry_unit_id IS NULL
                THEN
                    SELECT unit_id
                    INTO ln_entry_unit_id
                    FROM element
                    WHERE element_id = ari_measure.result_type_id;
                END IF;

                ln_measure_id := get_new_id ('measure_id_seq');

                INSERT INTO measure
                    (Measure_id,
                    assay_id,
                    result_type_id,
                    stats_modifier_id,
                    entry_unit_id,
                    parent_measure_id,
                    parent_child_relationship,     -- add this column 4/25/13 SJC
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
                    ari_measure.relationship,     -- add this column 4/25/13 SJC
                    0,
                    sysdate,
                    null,
                    pv_modified_by);

            END IF;

        ELSE
            log_error (-90004, 'Null parentage in Measure', 'Save_measure',
                'AID='|| To_Char(ari_measure.Aid)
                || ', resultType=' || ari_measure.resultType
                || ', parent_measure_id=' || To_Char(ani_parent_measure_id)
                || ', parent_child_relationship=' || ari_measure.relationship);
            ln_measure_id := -1;
        END IF;
        RETURN ln_measure_id;


    END save_measure;

    PROCEDURE separate_value_unit (avi_value_string IN  varchar2,
                                    avio_value_num IN OUT number,
                                    avio_unit IN OUT varchar2)
    AS
        ln_pos_space  BINARY_INTEGER;
        lv_first_word VARCHAR2(500);
        lv_last_word  VARCHAR2(500);
        ln_number     NUMBER;

    BEGIN
        -- get the number from the first part (if it's not in the first part it's probably not a real number)
        --Dbms_Output.put_line ('separate value_unit. value1 = ' || avi_value_string);
        ln_pos_space := InStr(avi_value_string, ' ');
        IF ln_pos_space > 0
        THEN
            ln_pos_space := ln_pos_space - 1;
        ELSE
            ln_pos_space := Length(avi_value_string);
        END IF;
        lv_first_word := SubStr(avi_value_string, 1, InStr(avi_value_string, ' ') -1 );
        -- test for a number
        begin
            ln_number := To_Number(lv_first_word);
        EXCEPTION
            WHEN OTHERS THEN
                ln_number := NULL;
        END;

        IF ln_number IS NOT NULL
        THEN
            --get the units from the end
            ln_pos_space := InStr(avi_value_string, ' ', -1);
            lv_last_word := SubStr(avi_value_string, ln_pos_space);
            avio_value_num := Nvl(avio_value_num, ln_number);
            avio_unit := Nvl(avio_unit, lv_last_word);
        END IF;

    END separate_value_unit;

    PROCEDURE save_exprmt_context_item_set (ari_resultType  IN  r_resultType)

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
          AND Nvl(aci.value_id, -100) = Nvl(tci.value_id, -100)
          --AND Nvl(aci.value_num, -99999.999) = Nvl(tci.value_num, -99999.999)
          and Decode(aci.value_id, NULL, Nvl(aci.value_display, '######'),'$$$$$$')
                  = Decode(tci.value_id, NULL, Nvl(tci.value1, '######'),'$$$$$$')
        GROUP BY aci.assay_context_id
        HAVING Count(*) = (SELECT Count(*) FRoM temp_context_item)
          AND Count(*) = (SELECT Count(*) FroM assay_context_item aci2
                                          WHERE aci2.assay_context_id = aci.assay_context_id);

        CURSOR cur_temp_context_item
        IS
        SELECT assay_id,
              attribute_id,
              display_order,
              value_num,
              unit,
              value_id,
              ext_value_id,
              value_min,
              value_max,
              aid,
              resultType,
              statsmodifier,
              contextItem,
              value1,
             Decode ((SELECT Count(*)
                     FROM temp_context_item tci2
                     WHERE tci2.contextItem = tci.contextItem),
                     0,'Free',
                     1,Decode(value1, NULL, 'Free', 'Fixed'),
                     'List') attribute_type
        FROM temp_context_item tci
        ORDER BY display_order;

        ln_assay_context_id NUMBER := null;
        ln_assay_context_item_id NUMBER ;

    BEGIN

        -- clean out the temp table
        DELETE FROM temp_context_item;
        -- get the context_item set from the result_map table
        -- AND save IN a TEMPORARY table
        -- need special care to find a set of 'List' type items
            -- look for the attribute without the value
            -- insert items with values with a check to prevent inserting duplicates
            -- note use of assay_ID argument to prevent doubling of item rows
        -- save the items in a temp table
--          Dbms_Output.put_line ('iinsert into temp_context_item '
--        || ', exprt_id=' || ari_resulttype.experiment_id
--        || ', assay_id=' || ari_resulttype.assay_id
--        || ', rt_id=' || ari_resulttype.result_type_id
--        || ', sm_id=' || ari_resulttype.stats_modifier_id
--        || ', aid=' || ari_resulttype.aid
--        || ', resulttype=' || ari_resulttype.resulttype
--        || ', modifier=' || ari_resulttype.stats_modifier
--        || ', relationship=' || ari_resulttype.relationship
--        || ', tid=' || ari_resulttype.tid
--        || ', expseries_no=' || ari_resulttype.series_nos
--        || ', parentTIds=' || ari_resulttype.parent_tids);

        INSERT INTO temp_context_item
            (display_order,
              assay_id,
              attribute_id,
              value_id,
              aid,
              resultType,
              statsmodifier,
              contextItem,
              value1,
              value_num,
              unit)
        SELECT ROWNUM - 1 display_order,
              ci.*
        FROM (SELECT e.assay_id,
                el_ci.element_id attribute_id,
                el_val.element_id value_id,
                rm.aid,
                rm.resultType,
                rm.stats_modifier,
                rm.contextItem,
                Nvl(rm.value1, Decode(rm.value_num, NULL, NULL, rm.value_num || ' ' || rm.unit)) value1,
                rm.value_num,
                rm.unit
            FROM (SELECT rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.contextItem, NULL value1, rm_ci.concentration value_num, rm_ci.concentrationunit unit
                  FROM result_map rm_ci
                  WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_ci.tid
                    AND rm_ci.contextItem != 'do not import'
                    AND rm_ci.resultType IS null
                    AND rm_ci.aid = ari_resulttype.aid
                  GROUP BY rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.contextItem, rm_ci.concentration, rm_ci.concentrationunit
                  UNION all
                  SELECT rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.attribute1, rm_ci.value1, null value_num, NULL unit
                  FROM result_map rm_ci
                  WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_ci.tid
                    AND rm_ci.attribute1 IS NOT null
                    AND rm_ci.resultType IS null
                    AND rm_ci.aid = ari_resulttype.aid
                  GROUP BY rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.attribute1, rm_ci.value1
                  UNION all
                  SELECT rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.attribute2, rm_ci.value2, null value_num, NULL unit
                  FROM result_map rm_ci
                  WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_ci.tid
                    AND rm_ci.attribute2 IS NOT null
                    AND rm_ci.resultType IS null
                    AND rm_ci.aid = ari_resulttype.aid
                  GROUP BY rm_ci.aid, rm_ci.resultType, rm_ci.stats_modifier, rm_ci.attribute2, rm_ci.value2
                    ) rm,
                element el_ci,
                element el_val,
                external_reference er,
                experiment e
            WHERE er.ext_assay_ref = 'aid=' || rm.aid
              AND e.experiment_id = er.experiment_id
              AND e.assay_id = ari_resulttype.assay_id
              AND el_ci.label (+) = rm.contextItem
              AND el_val.label (+) = rm.value1
              ORDER BY rm.aid, rm.contextItem, rm.value_num, rm.value1) ci;

--         Dbms_Output.put_line ('inserted '
--              || 'SQL%rowcount=' || SQL%rowcount);

        IF SQL%ROWCOUNT = 0
        THEN
           return;
            -- nothing got inserted, so we can make a quick exit
        END IF;

        -- query to find an existing matching set
        OPEN cur_matching_context;
--        Dbms_Output.put_line (' opened the matching cursor');
        FETCH cur_matching_context INTO ln_assay_context_id;
--        Dbms_Output.put_line (' fetched the matching cursor');
        CLOSE cur_matching_context;

        IF ln_assay_context_id IS NULL
        THEN
            -- not found create an assay_context
           ln_assay_context_id := get_new_id ('assay_context_id_seq');
--          Dbms_Output.put_line (' got assay_context_id=' || ln_assay_context_id);

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
                 'annotations for ' || ari_resultType.resultType,
                 'project management> experiment>',
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
                    log_error (-90002, 'Attribute not in Element', 'save_exprmt_context_item_set',
                            'resultType=' || lr_context_item.resultType || lr_context_item.statsmodifier
                            || ', Attr_Id='|| To_Char(lr_context_item.attribute_id)
                            || ', contextItem/attribute1=' || lr_context_item.contextItem
                            || ', Assay=' || To_Char(lr_context_item.assay_id)
                            || ', AID='|| To_Char(lr_context_item.aid));
                    CONTINUE;
                END IF;
                IF lr_context_item.value_id IS NULL
                    and lr_context_item.value_num is NULL
                    and lr_context_item.attribute_type != 'Free'
                THEN
                    -- log the error;
                    log_error (-90003, 'WARNING: no value_id available', 'save_exprmt_context_item_set',
                            'resultType=' || lr_context_item.resultType || lr_context_item.statsmodifier
                            || ', Attr_Id='|| To_Char(lr_context_item.attribute_id)
                            || ', attribute=' || lr_context_item.contextItem
                            || ', value_display=' || lr_context_item.value1
                            || ', Assay=' || To_Char(lr_context_item.assay_id)
                            || ', AID='|| To_Char(lr_context_item.aid));
                END IF;
                -- convert the value1 into a number/unit pair if possible
--                Dbms_Output.put_line (' splitting value1=' || lr_context_item.value1);
                IF lr_context_item.value1 IS NOT NULL
                then
                    separate_value_unit (lr_context_item.value1, lr_context_item.value_num, lr_context_item.unit);
                END IF;

                ln_assay_context_item_id := get_new_id ('assay_context_item_id_seq');
                INSERT INTO assay_context_item
                    (assay_context_item_id,
                    assay_context_id,
                    display_order,
                    attribute_type,
                    attribute_id,
                    value_id,
                    value_display,
                    value_num,
                    modified_by)
                VALUES(
                    ln_assay_context_item_id,
                    ln_assay_context_id,
                    lr_context_item.display_order,
                    lr_context_item.attribute_type,
                    lr_context_item.attribute_id,
                    lr_context_item.value_id,
                    Nvl(lr_context_item.value1, Decode(lr_context_item.value_num, NULL, NULL, To_Char(lr_context_item.value_num) || ' ' || lr_context_item.unit)),
                    lr_context_item.value_num,
                    pv_modified_by);

            END LOOP;
        END IF;

        --RETURN ln_assay_context_id;

    END save_exprmt_context_item_set;

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
              FROM result_map rm
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
              rm.entry_unit,
              rm.series_nos,
              rm.parent_tids
        FROM (SELECT aid,
                resulttype,
                stats_modifier,
                Decode (Lower(relationship),
                      'derives', 'calculated from',
                      'child', 'supported by',
                      relationship) relationship,  -- change this to do the translation  4/25/2013
                tid,
                Decode(contextitem, NULL, concentrationunit) entry_unit,
                listagg(seriesno, ',') within GROUP (ORDER BY tid) series_nos,
                listagg(parenttid, ',') within GROUP (ORDER BY tid) parent_tids
            FROM result_map
            WHERE aid = cn_aid   --1705
              AND resulttype IS NOT NULL
              --AND modified_by != 'southalln'
              AND parenttid IS null
              --AND InStr(',' || parentTID || ',', ',6,') > 0
            GROuP BY aid, resulttype, stats_modifier, relationship, tid, Decode(contextitem, NULL, concentrationunit)) rm,
            external_reference er,
            experiment e,
            result_type_element el,
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
            ---------------------------------------------------------------------------------------
            -- TODO -  this may be doing duplicate deletion work.  maybe a separate loop for this?
            --------------------------------------------------------------------------------------
            delete_measure (lr_assay_experiment.assay_id, lr_assay_experiment.experiment_id, pv_modified_by);

            FOR lr_rm_measure IN cur_rm_measure (lr_assay_experiment.aid)
            LOOP
                 -- this is a recursive call!
--                Dbms_Output.put_line (' send to first recursion '
--                      || 'exprt_id=' || lr_rm_measure.experiment_id
--                      || ', assay_id=' || lr_rm_measure.assay_id
--                      || ', rt_id=' || lr_rm_measure.result_type_id
--                      || ', sm_id=' || lr_rm_measure.stats_modifier_id
--                      || ', aid=' || lr_rm_measure.aid
--                      || ', resulttype=' || lr_rm_measure.resulttype
--                      || ', modifier=' || lr_rm_measure.stats_modifier
--                      || ', relationship=' || lr_rm_measure.relationship
--                      || ', tid=' || lr_rm_measure.tid
--                      || ', expseries_no=' || lr_rm_measure.series_nos
--                      || ', parentTIds=' || lr_rm_measure.parent_tids);
                save_measure_and_children ( 0, To_Number(NULL), lr_rm_measure);  --lr_rm_resultType);

                -- to get the all the experiment, AID, assay information outside the loop
                lr_rm_resulttype := lr_rm_measure;

            END LOOP;
            -- now save the contextitems that are not associated with specific result types
            save_exprmt_context_item_set(lr_rm_resulttype);

            -- clean up the display_orders for this assay
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

            manage_names.update_context_name(lr_rm_resulttype.assay_id);

        END LOOP;


    END transfer_result_map;


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
          AND Nvl(aci.value_id, -100) = Nvl(tci.value_id, -100)
          --AND Nvl(aci.value_num, -99999.999) = Nvl(tci.value_num, -99999.999)
          and Decode(aci.value_id, NULL, Nvl(aci.value_display, '######'),'$$$$$$')
                  = Decode(tci.value_id, NULL, Nvl(tci.value1, '######'),'$$$$$$')
        GROUP BY aci.assay_context_id
        HAVING Count(*) = (SELECT Count(*) FRoM temp_context_item)
          AND Count(*) = (SELECT Count(*) FroM assay_context_item aci2
                                          WHERE aci2.assay_context_id = aci.assay_context_id);

        CURSOR cur_temp_context_item
        IS
        SELECT assay_id,
              attribute_id,
              display_order,
              value_num,
              unit,
              value_id,
              ext_value_id,
              value_min,
              value_max,
              aid,
              resultType,
              statsmodifier,
              contextItem,
              value1,
             Decode ((SELECT Count(*)
                     FROM temp_context_item tci2
                     WHERE tci2.contextItem = tci.contextItem),
                     0,'Free',
                     1,Decode(value1, NULL, 'Free', 'Fixed'),
                     'List') attribute_type
        FROM temp_context_item tci
        ORDER BY display_order;

        ln_assay_context_id NUMBER := null;
        ln_assay_context_item_id NUMBER ;

    BEGIN

        -- clean out the temp table
        DELETE FROM temp_context_item;
        -- get the context_item set from the result_map table
        -- AND save IN a TEMPORARY table
        -- need special care to find a set of 'List' type items
            -- look for the attribute without the value
            -- insert items with values with a check to prevent inserting duplicates
            -- note use of assay_ID argument to prevent doubling of item rows
        -- save the items in a temp table
--          Dbms_Output.put_line ('iinsert into temp_context_item '
--        || ', exprt_id=' || ari_resulttype.experiment_id
--        || ', assay_id=' || ari_resulttype.assay_id
--        || ', rt_id=' || ari_resulttype.result_type_id
--        || ', sm_id=' || ari_resulttype.stats_modifier_id
--        || ', aid=' || ari_resulttype.aid
--        || ', resulttype=' || ari_resulttype.resulttype
--        || ', modifier=' || ari_resulttype.stats_modifier
--        || ', relationship=' || ari_resulttype.relationship
--        || ', tid=' || ari_resulttype.tid
--        || ', expseries_no=' || ari_resulttype.series_nos
--        || ', parentTIds=' || ari_resulttype.parent_tids);

        INSERT INTO temp_context_item
            (display_order,
              assay_id,
              attribute_id,
              value_id,
              aid,
              resultType,
              statsmodifier,
              contextItem,
              value1,
              value_num,
              unit)
        SELECT ROWNUM - 1 display_order,
               ci.*
        FROM (SELECT e.assay_id,
                  el_ci.element_id attribute_id,
                  el_val.element_id value_id,
                  rm.aid,
                  rm.resultType,
                  rm.stats_modifier,
                  rm.contextItem,
                  Nvl(rm.value1, Decode(rm.value_num, NULL, NULL, rm.value_num || ' ' || rm.unit)) value1,
                  rm.value_num,
                  rm.unit
              FROM (SELECT rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.contextItem, NULL value1, rm_ci.concentration value_num, rm_ci.concentrationunit unit
                    FROM result_map rm_rt,
                        result_map rm_ci
                    WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_rt.tid
                      AND rm_ci.aid = rm_rt.aid
                      AND rm_ci.contextItem != 'do not import'
                      AND rm_rt.resultType = ari_resultType.resultType
                      --AND rm_rt.resultType IS NOT null
                      AND Nvl(rm_rt.stats_modifier, '#####') = Nvl(ari_resultType.stats_modifier, '#####')
                      AND rm_rt.aid = ari_resultType.aid
                    GROUP BY rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.contextItem, rm_ci.concentration, rm_ci.concentrationunit
                    UNION all
                    SELECT rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.attribute1, rm_ci.value1, null value_num, NULL unit
                    FROM result_map rm_rt,
                        result_map rm_ci
                    WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_rt.tid
                      AND rm_ci.aid = rm_rt.aid
                      AND rm_ci.attribute1 IS NOT null
                      AND rm_rt.resultType = ari_resultType.resultType
                      --AND rm_rt.resultType IS NOT null
                      AND Nvl(rm_rt.stats_modifier, '#####') = Nvl(ari_resultType.stats_modifier, '#####')
                      AND rm_rt.aid = ari_resultType.aid
                    GROUP BY rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.attribute1, rm_ci.value1
                    UNION all
                    SELECT rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.attribute2, rm_ci.value2, null value_num, NULL unit
                    FROM result_map rm_rt,
                        result_map rm_ci
                    WHERE Nvl(rm_ci.contextTID, rm_ci.tid) = rm_rt.tid
                      AND rm_ci.aid = rm_rt.aid
                      AND rm_ci.attribute2 IS NOT null
                      AND rm_rt.resultType = ari_resultType.resultType
                      --AND rm_rt.resultType IS NOT null
                      AND Nvl(rm_rt.stats_modifier, '#####') = Nvl(ari_resultType.stats_modifier, '#####')  --
                      AND rm_rt.aid = ari_resultType.aid
                    GROUP BY rm_rt.aid, rm_rt.resultType, rm_rt.stats_modifier, rm_ci.attribute2, rm_ci.value2) rm,
                  element el_ci,
                  element el_val,
                  external_reference er,
                  experiment e
              WHERE er.ext_assay_ref = 'aid=' || rm.aid
                AND e.experiment_id = er.experiment_id
                AND e.assay_id = ari_resultType.assay_id
                AND el_ci.label (+) = rm.contextItem
                AND el_val.label (+) = rm.value1
                ORDER BY rm.aid, rm.contextItem, rm.value_num, rm.value1) ci;
--         Dbms_Output.put_line ('inserted '
--              || 'SQL%rowcount=' || SQL%rowcount);

        IF SQL%ROWCOUNT = 0
        THEN return NULL;
            -- nothing got inserted, so we can make a quick exit
        END IF;

        -- query to find an existing matching set
        OPEN cur_matching_context;
--        Dbms_Output.put_line (' opened the matching cursor');
        FETCH cur_matching_context INTO ln_assay_context_id;
--        Dbms_Output.put_line (' fetched the matching cursor');
        CLOSE cur_matching_context;

        IF ln_assay_context_id IS NULL
        THEN
            -- not found create an assay_context
           ln_assay_context_id := get_new_id ('assay_context_id_seq');
--          Dbms_Output.put_line (' got assay_context_id=' || ln_assay_context_id);

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
                 'Annotations for ' || ari_resultType.resultType,
                 'Project management> experiment>',
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
                            'resultType=' || lr_context_item.resultType || lr_context_item.statsmodifier
                            || ', Attr_Id='|| To_Char(lr_context_item.attribute_id)
                            || ', contextItem/attribute1=' || lr_context_item.contextItem
                            || ', Assay=' || To_Char(lr_context_item.assay_id)
                            || ', AID='|| To_Char(lr_context_item.aid));
                    CONTINUE;
                END IF;
                IF lr_context_item.value_id IS NULL
                    and lr_context_item.value_num is NULL
                    and lr_context_item.attribute_type != 'Free'
                THEN
                    -- log the error;
                    log_error (-90003, 'WARNING: no value_id available', 'save_context_item_set',
                            'resultType=' || lr_context_item.resultType || lr_context_item.statsmodifier
                            || ', Attr_Id='|| To_Char(lr_context_item.attribute_id)
                            || ', attribute=' || lr_context_item.contextItem
                            || ', value_display=' || lr_context_item.value1
                            || ', Assay=' || To_Char(lr_context_item.assay_id)
                            || ', AID='|| To_Char(lr_context_item.aid));
                END IF;
                -- convert the value1 into a number/unit pair if possible
--                Dbms_Output.put_line (' splitting value1=' || lr_context_item.value1);
                IF lr_context_item.value1 IS NOT NULL
                then
                    separate_value_unit (lr_context_item.value1, lr_context_item.value_num, lr_context_item.unit);
                END IF;

                ln_assay_context_item_id := get_new_id ('assay_context_item_id_seq');
                INSERT INTO assay_context_item
                    (assay_context_item_id,
                    assay_context_id,
                    display_order,
                    attribute_type,
                    attribute_id,
                    value_id,
                    value_display,
                    value_num,
                    modified_by)
                VALUES(
                    ln_assay_context_item_id,
                    ln_assay_context_id,
                    lr_context_item.display_order,
                    lr_context_item.attribute_type,
                    lr_context_item.attribute_id,
                    lr_context_item.value_id,
                    Nvl(lr_context_item.value1, Decode(lr_context_item.value_num, NULL, NULL, To_Char(lr_context_item.value_num) || ' ' || lr_context_item.unit)),
                    lr_context_item.value_num,
                    pv_modified_by);

            END LOOP;
        END IF;

        RETURN ln_assay_context_id;

    END save_context_item_set;

    PROCEDURE save_assay_context_measure (ani_assay_context_id IN number,
                               ani_measure_id IN number)
    AS
        ln_assay_context_measure_id NUMBER := null;

        CURSOR cur_assay_context_measure
        IS
        SELECT assay_context_measure_id
        FROM assay_context_measure acm
        WHERE assay_context_id = ani_assay_context_id
          AND measure_id = ani_measure_id;


    BEGIN
        -- try inserting but don't overwrite an existing entry
        open cur_assay_context_measure;
        fetch cur_assay_context_measure INto ln_assay_context_measure_id;
        CLOSE cur_assay_context_measure;

        IF ln_assay_context_measure_id IS NULL
        then
            ln_assay_context_measure_id := get_new_id ('assay_context_measure_id_seq');

            INSERT INTO assay_context_measure
                (assay_context_measure_id,
                assay_context_id,
                measure_id,
                modified_by)
            VALUES (ln_assay_context_measure_id,
                ani_assay_context_id,
                ani_measure_id,
                pv_modified_by);

          END IF;

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
        ln_exprmt_measure_id NUMBER;
        ln_exists           NUMBER := 0;
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
        SELECT Count(*)  INTO ln_exists
        FROM exprmt_measure
        WHERE experiment_id = ani_experiment_id
          AND measure_id = ani_measure_id
          AND Nvl(parent_exprmt_measure_id, -600) = Nvl(ln_parent_exprmt_measure_id, -600);

        IF ln_exists = 0 OR ln_exists IS null
        THEN
            IF (avi_relationship IS NULL AND ln_parent_exprmt_measure_id IS NULL)
                OR
               (avi_relationship IS NOT NULL AND ln_parent_exprmt_measure_id IS NOT NULL)
            THEN
                ln_exprmt_measure_id := get_new_id ('exprmt_measure_id_seq');
                INSERT INTO exprmt_measure
                    (exprmt_measure_id,
                    parent_exprmt_measure_id,
                    parent_child_relationship,
                    experiment_id,
                    measure_id,
                    modified_by)
                VALUES( ln_exprmt_measure_id,
                    ln_parent_exprmt_measure_id,
                  -- Decode (Lower(Trim(avi_relationship)),
                  --        'derives', 'Derived from',
                  --        'child', 'has Child',
                  --        'sibling', 'has Sibling',
                  --        'has Child'),
                    avi_relationship,   -- no translation needed here, it's done at the select from result_map  4-25-13 - SJC
                    ani_experiment_id,
                    ani_measure_id,
                    pv_modified_by);
            ELSE
                log_error (-90004, 'Null parentage in Exprmt_measure', 'Save_exprmt_measure',
                    'measure_ID='|| To_Char(ani_measure_id)
                    || ', parent_measure_id=' || To_Char(ln_parent_exprmt_measure_id)
                    || ', parent_child_relationship=' || avi_relationship);
            END IF;
        END IF;
    END save_exprmt_measure;

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
              rm.entry_unit,
              rm.series_nos,
              rm.parent_tids
        FROM (SELECT aid,
                resulttype,
                stats_modifier,
                Decode (Lower(relationship),
                      'derives', 'calculated from',
                      'child', 'supported by',
                      relationship) relationship,  -- change this to do the translation  4/25/2013
                tid,
                Decode(contextitem, NULL, concentrationunit) entry_unit,
                listagg(seriesno, ',') within GROUP (ORDER BY tid) series_nos,
                listagg(parenttid, ',') within GROUP (ORDER BY tid) parent_tids
            FROM result_map
            WHERE aid = cn_aid
              AND resulttype IS NOT NULL
              --AND parenttid IS null
              AND relationship IS NOT null
              AND InStr(',' || parentTID || ',', ',' || cv_parentTID|| ',') > 0
            GROuP BY aid, resulttype, stats_modifier, relationship, tid, Decode(contextitem, NULL, concentrationunit)) rm,
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
--        Dbms_Output.put_line ('inside next recursion '
--              || 'recurse=' || ani_recursion_level
--              || ', prnt_meas=' || ani_parent_measure_id
--              || ', exprt_id=' || ari_resulttype.experiment_id
--              || ', assay_id=' || ari_resulttype.assay_id
--              || ', rt_id=' || ari_resulttype.result_type_id
--              || ', sm_id=' || ari_resulttype.stats_modifier_id
--              || ', aid=' || ari_resulttype.aid
--              || ', resulttype=' || ari_resulttype.resulttype
--              || ', modifier=' || ari_resulttype.stats_modifier
--              || ', relationship=' || ari_resulttype.relationship
--              || ', tid=' || ari_resulttype.tid
--              || ', expseries_no=' || ari_resulttype.series_nos
--              || ', parentTIds=' || ari_resulttype.parent_tids);

        ln_measure_id := save_measure ( ani_parent_measure_id, ari_resultType);
--        Dbms_Output.put_line (' found measure=' || ln_measure_id);

        IF ln_measure_id = -1
        THEN    --- we have an error!
            RAISE le_measure_failed;
        END IF;

        ln_assay_context_id := save_context_item_set(ari_resultType);
--        Dbms_Output.put_line (' saved the context=' || ln_assay_context_id);
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
--            Dbms_Output.put_line ('  going to next recursion '
--              || 'recurse=' || To_Char(ani_recursion_level + 1)
--              || ', prnt_meas=' || ln_measure_id
--              || ', exprt_id=' || lr_rm_measure.experiment_id
--              || ', assay_id=' || lr_rm_measure.assay_id
--              || ', rt_id=' || lr_rm_measure.result_type_id
--              || ', sm_id=' || lr_rm_measure.stats_modifier_id
--              || ', aid=' || lr_rm_measure.aid
--              || ', resulttype=' || lr_rm_measure.resulttype
--              || ', modifier=' || lr_rm_measure.stats_modifier
--              || ', relationship=' || lr_rm_measure.relationship
--              || ', tid=' || lr_rm_measure.tid
--              || ', expseries_no=' || lr_rm_measure.series_nos
--              || ', parentTIds=' || lr_rm_measure.parent_tids
--              );

            save_measure_and_children ( ani_recursion_level + 1, ln_measure_id, lr_rm_measure);   --lr_rm_resultType);

        END LOOP;

    EXCEPTION
        WHEN le_measure_failed
        THEN
            log_error (-90001, 'Result Type not in Element', 'Save_measure_and_children',
                    'recursion level=' || ani_recursion_level
                    || 'RT_Id='|| To_Char(ari_resulttype.result_type_id)
                    || ', attribute=' || ari_resultType.resultType
                    || ', Assay=' || To_Char(ari_resultType.assay_id)
                    || ', AID='|| To_Char(ari_resultType.aid));

        WHEN OTHERS
        THEN
            RAISE;

    END save_measure_and_children;

END result_map_util;

/
