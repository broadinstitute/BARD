update role set authority = 'ROLE_'||authority where authority not like 'ROLE_%';

update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_UNM') where username = 'awaller';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_VANDERBILT') where username = 'sharangdhar.s.phatak';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_VANDERBILT') where username = 'eric.dawson';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NIH') where username = 'michael.north2';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NIH') where username = 'glenn.mcfadden';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NIH') where username = 'ajay.pillai3';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NCGC') where username = 'tanegac';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_BURNHAM') where username = 'tchung';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_BURNHAM') where username = 'ian.pass';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_BURNHAM') where username = 'mhedrick';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_BURNHAM') where username = 'ssalaniwal';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NCGC') where username = 'southalln';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_UNM') where username = 'jjyang';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_UNM') where username = 'toprea';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_NCGC') where username = 'henrike.nelson';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_HOPKINS') where username = 'alison_neal';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_SCRIPPS') where username = 'hodderp';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_UNM') where username = 'mbcarter';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_UNM') where username = 'cbologa';
update person set new_object_role = (select role_id from role where authority = 'ROLE_TEAM_BROAD') where username = 'jbittker';
update person set new_object_role = (select role_id from role where authority = 'ROLE_BARD_ADMINISTRATOR') where username = 'pmontgom';
update person set new_object_role = (select role_id from role where authority = 'ROLE_BARD_ADMINISTRATOR') where username = 'dlahr';

insert into person_role (person_role_id, role_id, person_id, version, date_created, last_updated, modified_by)
select person_role_id_seq.nextval, p.new_object_role, p.person_id, 0, sysdate, sysdate, 'pmontgom'
from person p where new_object_role is not null and not exists (
select 1 from person_role pr where pr.person_id = p.person_id and pr.role_id = p.new_object_role);