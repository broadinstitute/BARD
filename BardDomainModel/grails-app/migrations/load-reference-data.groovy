databaseChangeLog = {
	changeSet(author: 'pmontgom', id: 'populate-reference-data.sql', dbms: 'oracle', context: 'load-reference', runOnChange: 'false') {
		grailsChange {
			sqlFile(path: "sql/populate-reference-data.sql");
			sqlFile(path: "sql/populate-reference-element.sql");
		}
	}
}

