package iteration_027

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)


    changeSet(author: "jasiedu", id: "iteration-027/01-create-acl-tables", dbms: "oracle", context: "standard") {
        sqlFile(path: "${migrationsDir}/iteration_027/01-create-acl-tables.sql", stripComments: true)
    }
    changeSet(author: 'jasiedu', id: 'populate role table with team names', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                String query = """SELECT TEAM_NAME FROM TEAM"""
                sql.eachRow(query){ row ->
                    String teamName = row.TEAM_NAME
                    teamName = teamName.replaceAll("\\s+","_").toUpperCase()
                    String insertStatement = "INSERT INTO ROLE (ROLE_ID, AUTHORITY,DISPLAY_NAME,VERSION,MODIFIED_BY,DATE_CREATED) VALUES (ROLE_ID_SEQ.NEXTVAL, ${teamName},${row.TEAM_NAME},0,'jasiedu',SYSDATE)"
                    sql.execute(insertStatement)
                }
            }
        }
    }
}

