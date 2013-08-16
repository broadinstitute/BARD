alter table ASSAY_CONTEXT add CONTEXT_TYPE VARCHAR2(25);
alter table EXPRMT_CONTEXT add CONTEXT_TYPE VARCHAR2(25);
alter table PRJCT_EXPRMT_CONTEXT add CONTEXT_TYPE VARCHAR2(25);
alter table PROJECT_CONTEXT add CONTEXT_TYPE VARCHAR2(25);
alter table STEP_CONTEXT add CONTEXT_TYPE VARCHAR2(25);

update ASSAY_CONTEXT set context_type = 'Biology' where context_group in ('biology> molecular interaction>', 'biology>', 'Biology');
update ASSAY_CONTEXT set context_type = 'Assay Protocol' where context_group in ('assay protocol> assay type>','assay protocol> assay format>', 'Assay Protocol');
update ASSAY_CONTEXT set context_type = 'Assay Design' where context_group in ('assay protocol> assay design>','assay protocol> assay design', 'Assay Design');
update ASSAY_CONTEXT set context_type = 'Assay Readout' where context_group in ('assay protocol> assay readout>', 'Assay Readout');
update ASSAY_CONTEXT set context_type = 'Assay Components' where context_group in ('assay protocol> assay component>', 'Assay Components');
update ASSAY_CONTEXT set context_type = 'Experimental Variables' where context_group in ('project management> project information>', 'project management> experiment>', 'Experimental Variables', 'result type> item count>');
update ASSAY_CONTEXT set context_type = 'Unclassified' where context_group in ('unclassified>') or context_group is null;
update EXPRMT_CONTEXT set context_type = 'Unclassified' where context_group in ('unclassified>') or context_group is null;
update PRJCT_EXPRMT_CONTEXT set context_type = 'Unclassified' where context_group in ('unclassified>') or context_group is null;
update PROJECT_CONTEXT set context_type = 'Unclassified' where context_group in ('unclassified>','Unclassified') or context_group is null;

alter table ASSAY_CONTEXT add constraint CK_ASSAY_CTX_TYPE CHECK ( CONTEXT_TYPE is not null and CONTEXT_TYPE in ('Biology','Assay Protocol','Assay Design','Assay Readout','Assay Components','Experimental Variables','Unclassified') );
alter table EXPRMT_CONTEXT add constraint CK_EXPRMT_CTX_TYPE CHECK ( CONTEXT_TYPE is not null and CONTEXT_TYPE in ('Biology','Assay Protocol','Assay Design','Assay Readout','Assay Components','Experimental Variables','Unclassified') );
alter table PRJCT_EXPRMT_CONTEXT add constraint CK_PRJCT_EXPRMT_CTX_TYPE CHECK (CONTEXT_TYPE is not null and CONTEXT_TYPE in ('Biology','Assay Protocol','Assay Design','Assay Readout','Assay Components','Experimental Variables','Unclassified') );
alter table PROJECT_CONTEXT add constraint CK_PROJECT_CTX_TYPE CHECK (CONTEXT_TYPE is not null and CONTEXT_TYPE in ('Biology','Assay Protocol','Assay Design','Assay Readout','Assay Components','Experimental Variables','Unclassified') );
alter table STEP_CONTEXT add constraint CK_STEP_CTX_TYPE CHECK (CONTEXT_TYPE is not null and CONTEXT_TYPE in ('Biology','Assay Protocol','Assay Design','Assay Readout','Assay Components','Experimental Variables','Unclassified') );
