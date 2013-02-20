import bard.db.util.SchemaReset;

databaseChangeLog = {
    changeSet(author: "pmontgom", id: 'create initial snapshot', dbms: 'oracle', context: 'save-snapshot', runAlways: 'true') {
        grailsChange {
            change {
                SchemaReset.createBaseline(connection, "SHADOW", ["DATABASECHANGELOGLOCK", "DATABASECHANGELOG"]);
            }
        }
    }
}
