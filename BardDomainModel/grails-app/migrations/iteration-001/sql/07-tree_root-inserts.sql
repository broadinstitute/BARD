-- tree_root ------ specifies the starting place for the "materialized views" -----------------

insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (1, 'ASSAY_DESCRIPTOR', 5, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (2, 'BIOLOGY_DESCRIPTOR', 6, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (3, 'INSTANCE_DESCRIPTOR', 7, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (4, 'RESULT_TYPE', 594, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (5, 'UNIT', 123, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (6, 'STAGE', 223, 'has_a, is_a');
insert into tree_root (tree_root_id, tree_name, element_id, relationship_type) values (7, 'LABORATORY', 241, 'has_a, is_a');