databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)

    changeSet(author: 'ddurkin', id: 'modify size of ONTOLOGY_ITEM.ITEM_REFERENCE to 20', dbms: 'oracle', context: 'standard') {
        sql('ALTER TABLE ONTOLOGY_ITEM MODIFY (ITEM_REFERENCE char(20))');
    }
}