PROMPT CREATE OR REPLACE PROCEDURE update_step_context_name
CREATE OR REPLACE PROCEDURE update_step_context_name
  (an_project_step_id IN NUMBER DEFAULT NULL)

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
       AND (ac.project_step_id = cn_step_id
          OR
           cn_step_id IS NULL)
     GROUP BY ac.project_step_id,
            ac.step_context_id;

    lv_context_name  element.label%TYPE;

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

         ELSIF lr_step_context.attributes LIKE '%wavelength%'
         THEN
--    if group contains 'fluorescence/luminescence'
--    then set name = value
             lv_context_name := 'fluorescence/luminescence';

         ELSIF lr_step_context.attributes LIKE '%number%'
         THEN
--    if group contains 'number'
--    then set name = value
             lv_context_name := 'result detail';

         ELSIF lr_step_context.attributes LIKE '%biolog%'
         THEN
--    if group contains 'biology/ical'
--    then set name = value
             lv_context_name := 'biology';

         ELSIF lr_step_context.attributes LIKE '%assay format%'
                OR
               lr_step_context.attributes LIKE '%assay type%'
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

          UPDATE step_context ac
          SET context_name = lv_context_name
          WHERE step_context_id = lr_step_context.step_context_id;

        END IF;

      END LOOP;

      --commit;

END;
/

