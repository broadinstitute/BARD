-- used for before and after inspections.

Select * from assay_descriptor;
Select * from biology_descriptor;
Select * from instance_descriptor;
Select * from result_type;
Select * from unit;
select * from stage;
select * from laboratory;


Select count(*) from element;
Select count(*) from element_hierarchy;

select * from tree_root;


-- testing the cursor statement
select e.* 
from element e,
      element_hierarchy eh
where e.element_id = eh.child_element_id
  and eh.parent_element_id = 123
  and  'has_a, is_a' like '%' || eh.relationship_type || '%';

-- separate out the join to look for details
Select * from element_hierarchy
where parent_element_id = 5;

select * 
from element_hierarchy eh
where eh.parent_element_id = 5
  and  'has_a, is_a' like '%' || eh.relationship_type || '%';

