alter table experiment add PANEL_EXPRMT_ID number(19,0);

create table panel_experiment (PANEL_EXPRMT_ID number(19,0) not null,
version number(19,0) not null,
panel_id number(19,0) not null,
date_created timestamp not null,
last_updated timestamp,
MODIFIED_BY VARCHAR2(40),
primary key (PANEL_EXPRMT_ID));

alter table experiment add constraint FK_EXP_PANEL foreign key (PANEL_EXPRMT_ID) references panel_experiment;

alter table project_experiment modify experiment_id number(19,0) null;
alter table project_experiment add type varchar2(10);
alter table project_experiment add panel_exprmt_id NUMBER(19,0);
update project_experiment set type = 'single' where type is null;

create sequence PANEL_EXPRMT_ID_SEQ;
