PROMPT CREATE OR REPLACE PROCEDURE update_project_context_name
CREATE OR REPLACE PROCEDURE update_project_context_name
  (an_project_id IN NUMBER DEFAULT NULL)

AS
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
       AND (ac.project_id = cn_project_id
          OR
           cn_project_id IS NULL)
     GROUP BY ac.project_id,
            ac.project_context_id;

    lv_context_name  element.label%TYPE;

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

         ELSIF lr_project_context.attributes LIKE '%detection%'
         THEN
--    if group contains 'detection'
--    then set name = value
             lv_context_name := 'detection method';

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

         ELSIF lr_project_context.attributes LIKE '%number%'
         THEN
--    if group contains 'number'
--    then set name = value
             lv_context_name := 'result detail';

         ELSIF lr_project_context.attributes LIKE '%biolog%'
         THEN
--    if group contains 'biology/ical'
--    then set name = value
             lv_context_name := 'biology';

         ELSIF lr_project_context.attributes LIKE '%assay format%'
                OR
               lr_project_context.attributes LIKE '%assay type%'
         THEN
--    if group contains 'assay format'
--    then set name = 'assay protocol'
             lv_context_name := 'assay protocol';

         ELSE
             --lv_context_name := NULL;
             lv_context_name := '<Needs a name>';
         END IF;

        IF lv_context_name IS NOT NULL
        THEN

          UPDATE project_context ac
          SET context_name = lv_context_name
          WHERE project_context_id = lr_project_context.project_context_id;

        END IF;

      END LOOP;

      --commit;

END;
/

