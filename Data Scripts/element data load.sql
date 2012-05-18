Select 'insert into element (element_id, label, description, element_status_id) values ('
    || element_id || ', '''
    || label || ''', '''
    || description || ''', '
    || element_status_id  || ');'
from element;

Select 'insert into element_hierarchy (element_hierarchy_id, '
    || 'parent_element_id, child_element_id, relationship_type)'
    || ' values ('
    || element_hierarchy_id || ', '
    || parent_element_id || ', '
    || child_element_id || ', ''' 
    || relationship_type || ''');'
from element_hierarchy;

select 'insert into ontology (ontology_id, ontology_name, abbreviation, system_url) values ('
    || ontology_id || ', '''
    || ontology_name || ''', '''
    || abbreviation || ''', '''
    || system_url || ''');'
from ontology;

Select 'insert into ontology_item (ontology_item_id, ontology_id, element_id, item_reference) values ('
    || ontology_item_id ||', ' 
    || ontology_id ||', ' 
    || element_id ||', ''' 
    || trim(item_reference)  || ''');'
from ontology_item;

Select 'insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values ('
    || tree_root_id ||', '''
    || tree_name  ||''', '
    || element_id ||', '''
    || relationship_type ||''');'
from tree_root;