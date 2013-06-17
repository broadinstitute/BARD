package iteration_027

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)


    changeSet(author: "jasiedu", id: "iteration-027/01-create-acl-tables", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_027/01-create-acl-tables.sql", stripComments: true)
    }
	
	changeSet(author: "jasiedu", id: "iteration-027/02-create-acl-seq-tables", dbms: "oracle", context: "standard") {
		sqlFile(path: "${migrationsDir}/iteration_027/02-create-acl-seq-tables.sql", stripComments: true)
	}
	
	changeSet(author: "ycruz", id: "iteration-027/03-add-new-object-role-column-to-Person", dbms: "oracle", context: "standard") {
		sqlFile(path: "${migrationsDir}/iteration_027/03-add-new-object-role-column-to-Person.sql", stripComments: true)
	}

    changeSet(author: "jasiedu", id: "iteration-027/04-make-last-updated-field-non-nullable", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "${migrationsDir}/iteration_027/04-make-last-updated-field-non-nullable.sql", stripComments: true)
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.clear_username();
                               END;
                               """)
            }
        }
    }
}

