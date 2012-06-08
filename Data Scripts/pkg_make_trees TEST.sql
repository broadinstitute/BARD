begin
    dbms_output.put_line(to_char(sysdate, 'MM-DD HH:MI:SS') ||' start');
    
   --manage_ontology.make_trees( manage_ontology.pv_tree_biology_descriptor );
    manage_ontology.make_trees;
    
    dbms_output.put_line(to_char(sysdate, 'MM-DD HH:MI:SS') ||' end');
end;
/

-- TO TEST THE RECURSION DEPTH MAKE A CIRCULAR RELATIONSHIP 
-- Make macromolecule a child of protein
-- (protein is a child of macromolecule type)
insert into element_hierarchy (
    element_hierarchy_id, 
    child_element_id, 
    parent_element_id, 
    relationship_type) 
values (
    element_hierarchy_id_seq.nextval, 
    526, 
    '38', 
    'is_a')
    ;
    
    
select * from tree_root;
    
    
    



