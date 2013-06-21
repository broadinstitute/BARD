-- clean up funny data from data migration
update person set full_name = username, username = null where full_name is null and username is not null;
-- alter table person add constraint ck_person_new_object_role check (username is null or new_object_role is not null);

