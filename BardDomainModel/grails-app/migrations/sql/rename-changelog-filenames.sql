update DATABASECHANGELOG
set filename = 'iteration_006/006_changelog.groovy'
where filename = 'iteration-006/01-changelog.groovy';
update DATABASECHANGELOG
set filename = REGEXP_REPLACE(FILENAME, '-', '_')
where filename like 'iteration-%';