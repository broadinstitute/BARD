package iteration_021
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/11/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "xiaorong", id: "iteration-021/01-add-person.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration_021/01-add-person.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-021/02-change-entity-status.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration_021/02-change-entity-status.sql", stripComments: true)
    }
}

