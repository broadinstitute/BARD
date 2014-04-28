/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
