databaseChangeLog = {
	changeSet(author: 'pmontgom', id: 'populate-reference-data.sql', dbms: 'oracle', context: 'load-reference', runOnChange: 'false') {
        sqlFile(path: "sql/populate-reference-element.sql");
        sqlFile(path: "sql/populate-reference-data.sql");
	}
}

