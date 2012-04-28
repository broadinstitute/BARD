create or replace package body bard_dev.Manage_Ontology
as
-- forward declaration 
    procedure walk_down_the_tree(ani_element_id in number,
                                anio_node_id in out number,
                                ani_parent_node_id in number,
                                avi_relationship_type in varchar2,
                                avi_tree_name in varchar2,
                                ano_error out number,
                                avo_errmsg out varchar2);
----------------------------------------------------------------------                                
    
    procedure delete_old_tree(avi_tree_name in varchar2,
                            ano_error out number, 
                            avo_errmsg out varchar2)
    as
    begin
        if avi_tree_name = pv_tree_assay_descriptor
        then
            delete from assay_descriptor;
            
        elsif avi_tree_name = pv_tree_biology_descriptor
        then
            delete from biology_descriptor;
            
        elsif avi_tree_name = pv_tree_instance_descriptor
        then
            delete from instance_descriptor;
            
        elsif avi_tree_name = pv_tree_result_type
        then
            delete from result_type;
            
        elsif avi_tree_name = pv_tree_unit
        then
            delete from Unit;
        end if;
        
        commit;
    
    end;
    
    procedure walk_down_the_tree(ani_element_id in number,
                                anio_node_id in out number,
                                ani_parent_node_id in number,
                                avi_relationship_type in varchar2,
                                avi_tree_name in varchar2,
                                ano_error out number,
                                avo_errmsg out varchar2)
    as
    cursor cur_element is
            select e.* 
            from element e,
                 element_hierarchy eh
            where e.element_id = eh.child_element_id
              and eh.parent_element_id = ani_element_id
              and eh.relationship_type = avi_relationship_type;
              
    ln_node_id number;
    ln_next_parent_node_id number;
    lr_element element%rowtype;
    ln_error number;
    lv_errmsg varchar2(1000);
    
    begin
        --  check the node_id counting!!  it's wicked
        ln_node_id := anio_node_id;
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
            walk_down_the_tree(lr_element.element_id,
                                ln_node_id,
                                ln_next_parent_node_id,
                                avi_relationship_type,
                                avi_tree_name,
                                ln_error,
                                lv_errmsg);
        end loop;
        anio_node_id := ln_node_id;
    end walk_down_the_tree;
                        
                  
    procedure Save_node (ari_element in element%rowtype,
                                ani_node_id in number,
                                ani_parent_node_id in number,
                                avi_tree_name in varchar2,
                                ano_error out number,
                                avo_errmsg out varchar2)
    as
    
    begin
        if avi_tree_name = pv_tree_assay_descriptor
        then
            insert into assay_descriptor
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                unit,
                element_status_id)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.unit,
                ari_element.element_status_id);
                
        elsif avi_tree_name = pv_tree_biology_descriptor
        then
            insert into biology_descriptor
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                unit,
                element_status_id)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.unit,
                ari_element.element_status_id);
        
        elsif avi_tree_name = pv_tree_instance_descriptor
        then
            insert into instance_descriptor
                (node_id,
                parent_node_id,
                element_id,
                label,
                description,
                abbreviation,
                synonyms,
                unit,
                element_status_id)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.unit,
                ari_element.element_status_id);
        
        elsif avi_tree_name = pv_tree_result_type
        then
            insert into result_type
                (node_id,
                parent_node_id,
                result_type_id,
                result_type_name,
                description,
                abbreviation,
                synonyms,
                base_unit,
                result_type_status_id)
                values
                (ani_node_id,
                ani_parent_node_id,
                ari_element.element_id,
                ari_element.label,
                ari_element.description,
                ari_element.abbreviation,
                ari_element.synonyms,
                ari_element.unit,
                ari_element.element_status_id);
        
        elsif avi_tree_name = pv_tree_unit
        then
            insert into unit
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
        

        else
            null;
        end if;
    end save_node;    
        
    procedure make_trees (avi_tree_name in varchar2 default null)
    as
    cursor cur_tree_root
        is select *
           from tree_root
           where tree_name = upper(avi_tree_name)
              or avi_tree_Name is null;
           
    lr_tree_root tree_root%rowtype;
    lr_element element%rowtype;
    ln_node_id number;
    ln_parent_node_id number := 0;
    ln_error number;
    lv_errmsg varchar2(1000);
    
    begin
        for lr_tree_root in cur_tree_root
        loop
            -- delete the current contents
            delete_old_tree(lr_tree_root.tree_name, ln_error, lv_errmsg);
            
            -- put in the root row
            lr_element.element_id := 0;
            lr_element.label := 'Root';
            lr_element.description := 'Singular root to ensure tree vieweers work';
            lr_element.element_status_id := 2;
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
            ln_node_id := 1;
            walk_down_the_tree(
                        lr_tree_root.element_id,
                        ln_node_id,
                        ln_parent_node_id,
                        lr_tree_root.relationship_type,
                        lr_tree_root.tree_name,
                        ln_error,
                        lv_errmsg);
            -- return with the Node_ID of the last elment inserted (= count +1)
                        
            commit;
        end loop;
        
    end make_trees;
    
end manage_ontology;
/
                        
        
                          
