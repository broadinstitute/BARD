databaseChangeLog = {
    changeSet(author: "ddurkin", id: 'delete-data prior to loading data', dbms: 'oracle', context: 'load-data',runAlways: 'true') {
        String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
        File migrationsDir = new File(bardDomainModelMigrationsDir)
        File sqlDir = new File(migrationsDir, 'sql')
        sqlFile(path: "${sqlDir}/delete-data.sql", stripComments: true)
    }

    changeSet(author: "ddurkin", id: "execute load_data.load_assay stored procedure", dbms: 'oracle', context: 'load-data', runAlways: 'true') {
        grailsChange {
            change {
                sql.call('''
                        begin
                        load_data.load_assay;
                        end;
                    ''')
            }
        }
    }
}