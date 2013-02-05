PROMPT CREATE OR REPLACE PROCEDURE update_context_name
CREATE OR REPLACE PROCEDURE update_context_name
  (an_assay_id IN NUMBER DEFAULT NULL)

AS
     CURSOR cur_assay_context (cn_assay_id IN NUMBER)
     IS
     SELECT grp_attr.assay_id,
      grp_attr.assay_context_id,
      Sum(grp_attr.aci_count) aci_count,
      LISTAGG(grp_attr.ATTRIBUTE, ';') WITHIN GROUP (ORDER BY grp_attr.ATTRIBUTE) attributes
FROM (SELECT ac.assay_id,
            ac.assay_context_id,
            e.label attribute,
            Count(*) aci_count
      FROM assay_context ac,
          assay_context_item aci,
          element e
      WHERE aci.assay_context_id = ac.assay_context_id
        AND e.element_id = aci.attribute_id
        AND ac.assay_id = Nvl(cn_assay_id, ac.assay_id)
      GROUP BY ac.assay_id,
            ac.assay_context_id,
            e.label) grp_attr
GROUP BY grp_attr.assay_id,
      grp_attr.assay_context_id;

    lv_context_name  element.label%TYPE;

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

         ELSIF lr_assay_context.attributes LIKE '%detection%'
         THEN
--    if group contains 'detection'
--    then set name = value
             lv_context_name := 'detection method';

         ELSIF lr_assay_context.attributes LIKE '%readout%'
         THEN
--    if group contains 'assay readout'
--    then set name = value
             lv_context_name := 'assay readout';

         ELSIF lr_assay_context.attributes LIKE '%wavelength%'
         THEN
--    if group contains 'fluorescence/luminescence'
--    then set name = value
             lv_context_name := 'fluorescence/luminescence';

         ELSIF lr_assay_context.attributes LIKE '%number%'
         THEN
--    if group contains 'number'
--    then set name = value
             lv_context_name := 'result detail';

         ELSIF lr_assay_context.attributes LIKE '%biolog%'
         THEN
--    if group contains 'biology/ical'
--    then set name = value
             lv_context_name := 'biology';

         ELSIF lr_assay_context.attributes LIKE '%assay format%'
                OR
               lr_assay_context.attributes LIKE '%assay type%'
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

          UPDATE assay_context ac
          SET context_name = lv_context_name
          WHERE assay_context_id = lr_assay_context.assay_context_id;

        END IF;

      END LOOP;

      --commit;

END;
/

