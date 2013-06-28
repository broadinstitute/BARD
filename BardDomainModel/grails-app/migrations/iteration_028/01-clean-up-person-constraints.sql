-- clean up funny data from data migration
update person set full_name = username, username = null where full_name is null and username is not null;

