package grails
databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: "jasiedu", id: "iteration-019/01-add-confidencelevel-to-experiment.sql", dbms: 'oracle', context: 'standard') {
        sqlFile(path:  "${migrationsDir}/iteration-019/01-add-confidencelevel-to-experiment.sql", stripComments: true)
    }
}
