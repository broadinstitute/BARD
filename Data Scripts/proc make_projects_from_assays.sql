PROMPT CREATE OR REPLACE PROCEDURE make_projects
CREATE OR REPLACE PROCEDURE make_projects ( av_aid IN VARCHAR2 DEFAULT null)
AS
    CURSOR cur_project
    IS
    SELECT a.*,
        e.experiment_id,
        er.ext_assay_ref
    FROM assay a,
        experiment e,
        external_reference er
    WHERE a.assay_id = e.assay_id
      AND e.experiment_id = er.experiment_id
      AND a.assay_type = 'Panel - Group'
      AND (er.ext_assay_REF = 'aid=' || REPLACE(Lower(av_aid), 'aid=', '')
           OR av_aid IS NULL);

    CURSOR cur_existing_project (cv_ext_assay_ref VARCHAR2)
    IS
    SELECT project_id
    FROM external_reference
    WHERE project_id IS NOT NULL
      AND ext_assay_ref = cv_ext_assay_ref;

    CURSOR cur_context (cn_assay_id NUMBER)
    IS
    SELECT
          ac.assay_context_id,
          ac.assay_id,
          ac.display_order,
          ac.context_name
    FROM assay_context ac
    WHERE assay_id = cn_assay_id;

    CURSOR cur_context_item (cn_assay_id NUMBER)
    IS
    SELECT
          ac.assay_context_id,
          ac.assay_id,
          aci.display_order,
          aci.attribute_id,
          aci.value_id,
          aci.ext_value_id,
          aci.qualifier,
          aci.value_display,
          aci.value_num,
          aci.value_min,
          aci.value_max,
          aci.version,
          aci.date_created,
          SYSDATE last_updated,
          aci.modified_by
    FROM assay_context_item aci,
        assay_context ac
    WHERE ac.assay_context_id = aci.assay_context_id
      AND ac.assay_id = cn_assay_id
    ORDER BY ac.assay_context_id, display_order  ;

    ln_project_id   NUMBER;
    ln_group_id     NUMBER := 0;
    ln_project_context_item_id    NUMBER;
    ln_group_context_item_id      NUMBER;
    ln_project_context_id   NUMBER;

BEGIN
      FOR lr_project IN cur_project
      LOOP
          ln_project_id := NULL;
          -- does the project exist?
          OPEN cur_existing_project( lr_project.ext_assay_ref);
          FETCH cur_existing_project INTO ln_project_id;
          CLOSE cur_existing_project;

          -- create a new project record
          IF ln_project_id IS NULL
          THEN
              SELECT project_id_seq.NEXTVAL INTO ln_project_id FROM dual;

              insert into project
                  (PROJECT_ID,
                  PROJECT_NAME,
                  GROUP_TYPE,
                  DESCRIPTION,
                  READY_FOR_EXTRACTION,
                  MODIFIED_BY
                ) values (
                  ln_project_id,  --4,
                  SubStr(lr_project.assay_name, 1, 256),  -- 'Summary of the probe development efforts to identify agonists of the human cholinergic receptor, muscarinic 5 (CHRM5)',
                  'Project',  -- project_type,
                  lr_project.assay_name, -- 'Summary of the probe development efforts to identify agonists of the human cholinergic receptor, muscarinic 5 (CHRM5)',
                  'Pending',  -- ready_for_extraction,
                  'southern');  -- modified_by
           ELSE
               UPDATE project
               SET PROJECT_NAME = lr_project.assay_name,
                  GROUP_TYPE = 'Project',
                  DESCRIPTION = lr_project.assay_name,
                  READY_FOR_EXTRACTION = lr_project.ready_for_extraction,
                  MODIFIED_BY = Nvl(lr_project.modified_by, 'southern')
               WHERE project_id = ln_project_id;

           END IF;

            -- remap the external reference
            update external_reference er
                set experiment_id = null,
                project_id = ln_project_id
            where experiment_id = lr_project.experiment_id
              AND NOT EXISTS (SELECT 1
                  FROM external_reference er2
                  WHERE er2.project_id = ln_project_id);

            -- insert the project_documents
            DELETE FROM project_document
            WHERE project_id = ln_project_id;

            INSERT INTO project_document
                (project_document_id,
                project_id,
                document_name,
                document_type,
                document_content,
                version,
                date_created,
                last_updated,
                modified_by)
            SELECT assay_document_id,
                ln_project_id,
                document_name,
                document_type,
                document_content,
                version,
                date_created,
                SYSDATE last_updated,
                modified_by
            FROM assay_document
            WHERE assay_id = lr_project.assay_id;

          -- transfer the assay_context_items to the project_context_items
          -- referencing the project (not the step)
          DELETE FROM project_context_item
          WHERE project_context_id IN (
              SELECT project_context_id
              FROM project_context
              WHERE project_id = lr_project.assay_id)
           AND modified_by = 'southern';

          delete FROM project_context pc
           WHERE project_id = ln_project_id
             AND modified_by = 'southern'
             AND NOT EXISTS (SELECT 1
                FROM project_context_item pci
                WHERE pci.project_context_id = pc.project_context_id);

          FOR lr_context IN cur_context (lr_project.assay_id)
          LOOP

              FOR lr_context_item IN cur_context_item (lr_project.assay_id)
              LOOP
                  SELECT project_context_item_id_seq.NEXTVAL INTO ln_project_context_item_id
                  FROM dual;

                  IF ln_group_id != lr_context_item.assay_context_id
                  THEN
                      ln_group_id := lr_context_item.assay_context_id;
                      ln_group_context_item_id := ln_project_context_item_id;

                  END IF;

                  INSERT INTO project_context_item
                      (project_context_item_id,
                      display_order,
                      project_context_id,
                      attribute_id,
                      value_id,
                      ext_value_id,
                      qualifier,
                      value_display,
                      value_num,
                      value_min,
                      value_max,
                      version,
                      date_created,
                      last_updated,
                      modified_by)
                  VALUES
                      (ln_project_context_item_id,
                      0,
                      ln_project_context_id,
                      lr_context_item.attribute_id,
                      lr_context_item.value_id,
                      lr_context_item.ext_value_id,
                      lr_context_item.qualifier,
                      lr_context_item.value_display,
                      lr_context_item.value_num,
                      lr_context_item.value_min,
                      lr_context_item.value_max,
                      lr_context_item.version,
                      lr_context_item.date_created,
                      SYSDATE,
                      lr_context_item.modified_by);
        --  AND NOT EXISTS (SELECT 1
        --      FROM project_context_item pci
        --      WHERE pci.project_id = ln_project_id,
        --        AND pci.discriminator = 'Project',
        --        AND pci.attribute_id = aci.attribute_id,
        --        AND Nvl(pci.value_id, -666) = Nvl(aci.value_id,-666)
        --        AND Nvl(pci.ext_value_id, 'Null') = Nvl(aci.ext_value_id, 'Null')
        --        AND Nvl(pci.qualifier, '= ') = Nvl(aci.qualifier, '= ')
        --        AND Nvl(pci.value_display, 'Null') = Nvl(aci.value_display, 'Null')
        --        AND nvl(pci.value_num, -666) = Nvl(aci.value_num, -666)
        --        AND Nvl(pci.value_min, -666) = Nvl(aci.value_min, -666)
        --        AND Nvl(pci.value_max, -666) = Nvl(aci.value_max, -666))

              END LOOP;
         END LOOP;

          -- get a list of subsidiary AIDs
              -- make project step records for each experiement
              -- for each experiment add the stage and project step items

    --  AND now the deletions
          -- check for relatives of experiment
          -- delete the experiment
          DELETE FROM experiment
          WHERE experiment_id = lr_project.experiment_id;

          -- delete the assay_documents
          DELETE FROM assay_document
          WHERE assay_id = lr_project.assay_id;

          -- delete the contexts
          DELETE FROM assay_context_item
          WHERE assay_context_id IN
              (SELECT assay_context_id
              FROM assay_context
              WHERE assay_id = lr_project.assay_id);

          DELETE FROM measure
          WHERE assay_id = lr_project.assay_id;

          DELETE FROM assay_context
          WHERE assay_id = lr_project.assay_id;

          delete FROM assay
          WHERE assay_id = lr_project.assay_id;

      END LOOP;

END;
/

