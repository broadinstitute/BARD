------------------------------------------------------------------------------------
-- Create the package
------------------------------------------------------------------------------------

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

