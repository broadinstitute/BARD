import groovy.sql.Sql

def sql = new Sql(ctx.dataSource)
sql.call( '''
create or replace package Manage_Ontology
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

end manage_ontology;         
''')