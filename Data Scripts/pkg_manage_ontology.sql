--PROMPT CREATE OR REPLACE PACKAGE manage_ontology
CREATE OR REPLACE package manage_ontology
as
    pv_tree_assay_descriptor varchar2(31) := 'ASSAY_DESCRIPTOR';
    pv_tree_biology_descriptor varchar2(31) := 'BIOLOGY_DESCRIPTOR';
    pv_tree_instance_descriptor varchar2(31) := 'INSTANCE_DESCRIPTOR';
    pv_tree_result_type varchar2(31) := 'RESULT_TYPE';
    pv_tree_unit varchar2(31) := 'UNIT';
    pv_tree_stage varchar2(31) := 'STAGE';
    pv_tree_laboratory varchar2(31) := 'LABORATORY';

--    procedure delete_old_tree(avi_tree_name in varchar2,
--                            ano_error out number,
--                            avo_errmsg out varchar2);

--    procedure walk_down_the_tree(ani_element_id in number,
--                                anio_node_id in out number,
--                                ani_parent_node_id in number,
--                                avi_relationship_type in varchar2,
--                                avi_tree_name in varchar2,
--                                ani_recursion_level number,
--                                ano_error out number,
--                                avo_errmsg out varchar2);

--    procedure Save_node (ari_element in element%rowtype,
--                                ani_node_id in number,
--                                ani_parent_node_id in number,
--                                avi_tree_name in varchar2,
--                                ano_error out number,
--                                avo_errmsg out varchar2);

    procedure make_trees (avi_tree_name in varchar2 default null);

    procedure add_element(avi_tree_name in varchar2,
                        ani_parent_element_id in number,
                        avi_element_label in varchar2,
                        avi_element_description in varchar2,
                        avi_element_abbreviation in varchar2,
                        avi_element_synonyms in varchar2);

    procedure swap_element_id (ani_element_id   in  number,
                               ani_new_element_id   in   number,
                               ab_delete_old    in boolean default false);

end manage_ontology;
/

--PROMPT CREATE OR REPLACE PACKAGE BODY manage_ontology
CREATE OR REPLACE package body manage_ontology
as
-- forward declaration, needed for the recursion to compile
    procedure walk_down_the_tree(ani_element_id in number,
                                anio_node_id in out number,
                                ani_parent_node_id in number,
                                avi_relationship_type in varchar2,
                                avi_tree_name in varchar2,
                                ani_recursion_level number,
                                ano_error out number,
                                avo_errmsg out varchar2);
    -- for preventing limitless trees (circular loops)
    pn_recursion_limit number := 20;  -- change this limit as needed

    -- for testing only
    pb_trace boolean := false;     -- true;
    pn_node_id_max number := null; --5; --1000;  -- for testing purposes only,
----------------------------------------------------------------------

    procedure trace(avi_msg in varchar2)
    as
        -- remember to turn on the serveroutput and set the buffer size to the max
        -- set serveroutput on size 1000000;

        lv_msg  varchar2(1000);

    BEGIN
        if pb_trace then
            lv_msg := to_char(sysdate, 'MM-DD HH:MI:SS');
            lv_msg := lv_msg || '. ' || substr(avi_msg, 0, 980);

            dbms_output.put_line(lv_msg);
        end if;

    END TRACE;

    procedure delete_old_tree(avi_tree_name in varchar2,
                            ano_error out number,
                            avo_errmsg out varchar2)
    as
    --
    -- schatwin 8_17_12  added "_tree" to all the materialized view tables
    --
    begin
        if avi_tree_name = pv_tree_assay_descriptor
        then
            delete from assay_descriptor_tree;


        elsif avi_tree_name = pv_tree_biology_descriptor
        then
            delete from biology_descriptor_tree;

        elsif avi_tree_name = pv_tree_instance_descriptor
        then
            delete from instance_descriptor_tree;

        elsif avi_tree_name = pv_tree_result_type
        then
            delete from result_type_tree;

        elsif avi_tree_name = pv_tree_unit
        then
            delete from Unit_tree;

        elsif avi_tree_name = pv_tree_stage
        then
            delete from Stage_tree;

        elsif avi_tree_name = pv_tree_laboratory
        then
            delete from laboratory_tree;
        end if;

        trace('Delete from ' || avi_tree_name || ' '|| to_char(sql%rowcount) || ' rows' );

        commit;

    end delete_old_tree;

   procedure Save_node (ari_element in element%rowtype,
                                ani_node_id in number,
                                ani_parent_node_id in number,
                                avi_tree_name in varchar2,
                                ano_error out number,
                                avo_errmsg out varchar2)
    as
    --
    -- schatwin 8_17_12  added "_tree" to all the materialized view tables
    --

    begin
        if avi_tree_name = pv_tree_assay_descriptor
        then
            insert into assay_descriptor_tree
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                external_URL,
                unit,
                element_status)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.external_URL,
                ari_element.unit,
                ari_element.element_status);

        elsif avi_tree_name = pv_tree_biology_descriptor
        then
            insert into biology_descriptor_tree
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                external_URL,
                unit,
                element_status)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.external_URL,
                ari_element.unit,
                ari_element.element_status);

        elsif avi_tree_name = pv_tree_instance_descriptor
        then
            insert into instance_descriptor_tree
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                external_URL,
                unit,
                element_status)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.external_URL,
                ari_element.unit,
                ari_element.element_status);

        elsif avi_tree_name = pv_tree_result_type
        then
            insert into result_type_tree
                (node_id,
                parent_node_id,
                result_type_id,
                result_type_name,
                description,
                abbreviation,
                synonyms,
                base_unit,
                result_type_status)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.unit,
                ari_element.element_status);

        elsif avi_tree_name = pv_tree_unit
        then
            insert into unit_tree
                (node_id,
                parent_node_id,
                unit_id,
                unit,
                description)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description);

        elsif avi_tree_name = pv_tree_stage
        then
            insert into stage_tree
                (node_id,
                parent_node_id,
                stage_id,
                stage,
                stage_status,
                description)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.element_status,
                ari_element.description);

        elsif avi_tree_name = pv_tree_laboratory
        then
            insert into laboratory_tree
                (node_id,
                parent_node_id,
                laboratory_id,
                laboratory,
                laboratory_status,
                description)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.element_status,
                ari_element.description);


        else
            null;
        end if;
        trace('Saved node ID='|| to_char(ani_node_id)
             || ' in tree=' || avi_tree_name
             || ' parent_ID=' || to_char(ani_parent_node_id)
             || ' Element_ID='  || to_char(ari_element.element_id)
             || ' label= "' || ari_element.label || '".'
             );

    end save_node;

    --- edited schatwin, 6/25/12 --------------------------------------------------
    -- changedwhere clause in cursor in walk_down_the_tree to just use 1st 3 letters of the relationship (property)
    ---
    procedure walk_down_the_tree(ani_element_id in number,
                                anio_node_id in out number,
                                ani_parent_node_id in number,
                                avi_relationship_type in varchar2,
                                avi_tree_name in varchar2,
                                ani_recursion_level number,
                                ano_error out number,
                                avo_errmsg out varchar2)
    as
    cursor cur_element is
            select e.*
            from element e,
                 element_hierarchy eh
            where e.element_id = eh.child_element_id
              and eh.parent_element_id = ani_element_id
              and avi_relationship_type like '%' || substr(eh.relationship_type, 1, 3) || '%';
--              and avi_relationship_type like '%' || eh.relationship_type || '%';

    ln_node_id number;
    ln_next_parent_node_id number;
    lr_element element%rowtype;
    ln_error number;
    lv_errmsg varchar2(1000);
    lb_trace boolean;

    begin
        --  checkout the node_id counting!!  it's wicked
        ln_node_id := anio_node_id;
        if ani_recursion_level <= pn_recursion_limit
        then
            for lr_element in cur_element
            loop
                Save_node(lr_element,
                            ln_node_id,
                            ani_parent_node_id,
                            avi_tree_name,
                            ln_error,
                            lv_errmsg);

                ln_next_parent_node_id := ln_node_id;
                ln_node_id := ln_node_id + 1;
                trace('next node, R-level=' || ani_recursion_level
                      || ' node_id=' || to_char(ln_node_id)
                      || ' parent_node_id=' || to_char(ln_next_parent_node_id)
                      || ' tree = ' || avi_tree_name
                      );

                walk_down_the_tree(lr_element.element_id,
                                    ln_node_id,
                                    ln_next_parent_node_id,
                                    avi_relationship_type,
                                    avi_tree_name,
                                    ani_recursion_level +1,
                                    ln_error,
                                    lv_errmsg);
                trace( 'returning node_id = '|| to_char(ln_node_id)
                      || ' level = ' || to_char(ani_recursion_level)
                      );

                if ln_node_id > pn_node_id_max then
                    -- for testing only, null returns false in an IF
                    -- thus NULL is unlimited
                    exit;  -- reached a limit, so jump out of the loop
                end if;

            end loop;
        else
            lb_trace := pb_trace;
            pb_trace:= true;
            trace(' recursion limit exceeded '
                  || ' Element_id = ' || to_char(lr_element.element_id)
                  || ' next node, R-level=' || ani_recursion_level
                  || ' node_id=' || to_char(ln_node_id)
                  || ' parent_node_id=' || to_char(ln_next_parent_node_id)
                  || ' tree = ' || avi_tree_name
                  );
            pb_trace := lb_trace;

        end if;

        anio_node_id := ln_node_id;

    end walk_down_the_tree;


     procedure make_trees (avi_tree_name in varchar2 default null)
     --
     -- schatwin
     -- 8/16/12 -- changed the values for the root node in each tree to get them fom the element table
    as
    cursor cur_tree_root
        -- adds the Element as a join to get the label and descriptions (8/16/12)
        is select tr.*, e.description, e.label
           from tree_root tr,
            element e
           where e.element_id = tr.element_id
             and (tree_name = upper(avi_tree_name)
              or avi_tree_Name is null);

    lr_tree_root tree_root%rowtype;
    lr_element element%rowtype;
    ln_node_id number;
    ln_parent_node_id number;
    ln_error number;
    lv_errmsg varchar2(1000);
    ln_recursion_level number := 1; -- start of the recursion checking

    begin
        for lr_tree_root in cur_tree_root
        loop
            ln_node_id := 1;         -- first "real" node from Element
            ln_parent_node_id := 0;  -- every tree starts with a 0 node

            -- delete the current contents
            delete_old_tree(lr_tree_root.tree_name, ln_error, lv_errmsg);

            -- put in the root row
            lr_element.element_id := lr_tree_root.element_id;
            lr_element.label := lr_tree_root.label; --  was lr_tree_root.tree_name; (8/16/12)
            lr_element.description := nvl(lr_tree_root.description, 'Placeholder til we get a definition'); --  was 'Singular root to ensure tree viewers work'; (8/16/12)
            lr_element.element_status := 'Published';
            lr_element.version := 0;
            lr_element.date_created := sysdate;
            -- all other values are nulls

            Save_node ( lr_element,
                        ln_parent_Node_id,
                        null,
                        lr_tree_root.tree_name,
                        ln_error,
                        lv_errmsg);

            -- now loop thru the children, get the next node_id
            walk_down_the_tree(
                        lr_tree_root.element_id,
                        ln_node_id,
                        ln_parent_node_id,
                        lr_tree_root.relationship_type,
                        lr_tree_root.tree_name,
                        ln_recursion_level,
                        ln_error,
                        lv_errmsg);
            -- return with the Node_ID of the last elment inserted (= count +1)

            commit;
        end loop;

    end make_trees;


    procedure add_element(avi_tree_name in varchar2,
                        ani_parent_element_id in number,
                        avi_element_label in varchar2,
                        avi_element_description in varchar2,
                        avi_element_abbreviation in varchar2,
                        avi_element_synonyms in varchar2)
    as
    begin
        -- just stubbed for now

        -- pseudo code:

        -- check that the label doesn't already exist in Element (they're
        --  supposed to be unique)  and return the element_id, tree_name
        --  and node_id if it does.  Do this case-insensitive!

        -- save the new node in the relevant tree table
        -- remember to get the max(node_id) first

        -- add the element to the Element table using the sequence
        -- for the element_id

        -- enter the hierarchy into Element_hierarchy using the element_id
        --  and the parent_element_id with the relationship 'is_a'
        --  this assumes the new item is a leaf node - we cannot handle middle nodes!

        -- and put a reference into the ontology_element table to allow BAO
        --  to find and reference it

        -- and don't forget to commit;

        null;

    end add_element;

    procedure swap_element_id (ani_element_id   in  number,
                               ani_new_element_id   in   number,
                               ab_delete_old    in boolean default false)
    as
    -- tables in this order
    -- result_context_item, attribute, value
    -- result, result_type_id
    -- measure_context_item, attribute, value
    -- measure, result_type_id
    -- project_experiment, stage_id
    -- experiment, laboratory_id
    -- element_hierarchy, parent_element_id, child_element_id
    -- unit_conversion, from_unit, to_unit

    lv_old_label    element.label%type;
    lv_new_label    element.label%type;

    begin
        select label into lv_old_label
        from element
        where element_id = ani_element_id;

        select label into lv_new_label
        from element
        where element_id = ani_new_element_id;

        update run_context_item
           set attribute_id = ani_new_element_id
         where attribute_id = ani_element_id;

        update run_context_item
           set value_id = ani_new_element_id,
               value_display = replace(value_display, lv_old_label, lv_new_label)
         where value_id = ani_element_id;

        update project_context_item
           set attribute_id = ani_new_element_id
         where attribute_id = ani_element_id;

        update project_context_item
           set value_id = ani_new_element_id,
               value_display = replace(value_display, lv_old_label, lv_new_label)
         where value_id = ani_element_id;

        update result
           set result_type_id = ani_new_element_id
         where result_type_id = ani_element_id;

        update assay_context_item
           set attribute_id = ani_new_element_id
         where attribute_id = ani_element_id;

        update assay_context_item
           set value_id = ani_new_element_id,
               value_display = replace(value_display, lv_old_label, lv_new_label)
         where value_id = ani_element_id;

        update measure
           set result_type_id = ani_new_element_id
         where result_type_id = ani_element_id;

--        update project_step
--           set stage_id = ani_new_element_id
--         where stage_id = ani_element_id;

--        update experiment
--           set laboratory_id = ani_new_element_id
--         where laboratory_id = ani_element_id;

        update unit_conversion uc
          set uc.from_unit = lv_new_label
        where uc.from_unit = lv_old_label
          and not exists (select 1 from unit_conversion uc2
            where uc2.from_unit = lv_new_label
            and uc2.to_unit = uc.to_unit);

        update unit_conversion uc
          set uc.to_unit = lv_new_label
        where uc.to_unit = lv_old_label
          and not exists (select 1 from unit_conversion uc2
            where uc2.to_unit = lv_new_label
            and uc2.from_unit = uc.from_unit);

        update element_hierarchy eh
          set eh.parent_element_id = ani_new_element_id
        where eh.parent_element_id = ani_element_id
          and not exists (select 1 from element_hierarchy eh2
            where eh2.parent_element_id = ani_new_element_id
            and eh2.child_element_id = eh.child_element_id);

        update element_hierarchy eh
          set eh.child_element_id = ani_new_element_id
        where eh.child_element_id = ani_element_id
          and not exists (select 1 from element_hierarchy eh2
            where eh2.child_element_id = ani_new_element_id
            and eh2.parent_element_id = eh.parent_element_id);

        if ab_delete_old
        then
            delete from element
            where element_id = ani_element_id;

            delete from identifier_mapping
            where target_id = ani_element_id
              and table_name = 'ELEMENT';
        end if;

        commit;

    end swap_element_id;


end manage_ontology;
/

