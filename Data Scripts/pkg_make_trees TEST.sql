begin
    dbms_output.put_line(to_char(sysdate, 'MM-DD HH:MI:SS') ||' start');
    
   --manage_ontology.make_trees( manage_ontology.pv_tree_biology_descriptor );
    manage_ontology.make_trees;
    
    dbms_output.put_line(to_char(sysdate, 'MM-DD HH:MI:SS') ||' end');
end;
/