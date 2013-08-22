package iteration_007

databaseChangeLog = {

    changeSet(author: 'ddurkin', id: 'modify size of ONTOLOGY_ITEM.ITEM_REFERENCE to 20', dbms: 'oracle', context: 'standard') {
        sql('ALTER TABLE ONTOLOGY_ITEM MODIFY (ITEM_REFERENCE char(20))');
    }
}