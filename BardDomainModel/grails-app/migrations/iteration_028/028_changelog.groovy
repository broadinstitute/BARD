package iteration_028

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "pmontgom", id: "iteration-028/01-clean-up-person-constraints", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('pmontgom');
                               END;
                               """)
            }
        }
        sqlFile(path: "${migrationsDir}/iteration_028/01-clean-up-person-constraints.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-028/02-add-substance-count", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_028/02-add-substance-count.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-028/03-populate-person-groups", dbms: "oracle", context: "production-data-update") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('pmontgom');
                               END;
                               """)
            }
        }
        sqlFile(path: "${migrationsDir}/iteration_028/03-populate-person-groups.sql", stripComments: true)
    }
}

