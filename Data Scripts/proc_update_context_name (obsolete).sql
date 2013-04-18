PROMPT CREATE OR REPLACE PROCEDURE update_context_name
CREATE OR REPLACE PROCEDURE update_context_name
  (an_assay_id IN NUMBER DEFAULT NULL)

AS
-------------------------------------------------------------------------------------------------------------
--   update context name
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
              AND ac.assay_id = Nvl(cn_assay_id, ac.assay_id)
              --AND (ac.context_name NOT LIKE 'Annotation%' OR ac.context_name IS NULL)
            GROUP BY ac.assay_id,
                  ac.assay_context_id,
                  e.label,
                  aci.attribute_type) grp_attr
     GROUP BY grp_attr.assay_id,
            grp_attr.assay_context_id;

    lv_context_name  element.label%TYPE;
    lv_context_group  assay_context.context_group%TYPE;

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

          UPDATE assay_context ac
          SET context_name = lv_context_name,
              context_group = lv_context_group
          WHERE assay_context_id = lr_assay_context.assay_context_id;

        END IF;

      END LOOP;

      --commit;

END;
/

