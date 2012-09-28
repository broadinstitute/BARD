databaseChangeLog = {
    changeSet(author: "ddurkin", id: 'delete-data prior to loading data', dbms: 'oracle', context: 'load-data', runAlways: 'true') {
        String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
        File migrationsDir = new File(bardDomainModelMigrationsDir)
        File sqlDir = new File(migrationsDir, 'sql')
        sqlFile(path: "${sqlDir}/delete-data.sql", stripComments: true)
    }

    changeSet(author: "ddurkin", id: 'apply-grants prior to loading data', dbms: 'oracle', context: 'load-data', runAlways: 'true') {
        grailsChange {
            change {
                def usernames = ['SCHATWIN', 'BARD_DEV', 'SBRUDZ', 'DATA_MIG', 'YCRUZ', 'SOUTHERN', 'BARD_QA', 'BARD_CI', 'DSTONICH', 'BALEXAND', 'DDURKIN', 'JASIEDU']
                String currentUsername = application.config.dataSource.username
                usernames = usernames - currentUsername.toUpperCase()
                for (username in usernames) {
                    String grant = "GRANT EXECUTE ON LOAD_DATA TO ${username}"
                    //println(grant)
                    sql.execute(grant)
                }
            }
        }
    }

    changeSet(author: "ddurkin", id: "execute load_data.load_assay stored procedure", dbms: 'oracle', context: 'load-data', runAlways: 'true') {
        grailsChange {
            change {
                sql.call('''
                        begin
                        load_data.load_reference;
                        load_data.load_assay(7);
                        load_data.load_assay(8);
                        load_data.load_assay(726);

                        load_data.load_assay(1432);
                        load_data.load_assay(1565);
                        load_data.load_assay(1847);
                        load_data.load_assay(186);
                        load_data.load_assay(1246);
                        --load_data.load_assay(99);
                        --load_data.load_assay(1217);
                        --load_data.load_assay(819);
                        --load_data.load_assay(326);
                        end;
                    ''')
            }
        }
    }
}