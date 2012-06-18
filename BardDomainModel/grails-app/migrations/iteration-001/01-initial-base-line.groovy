import static groovy.io.FileType.FILES


databaseChangeLog = {
    File migrationsDir = new File('grails-app/migrations')
    String iteration001SqlRelativeDir = 'iteration-001/sql'
    File iteration001SqlDir = new File(migrationsDir, iteration001SqlRelativeDir)
    iteration001SqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->

        String fileName = file.name
        changeSet(author: "ddurkin", id: fileName, dbms: 'oracle') {
            sqlFile(path: "${iteration001SqlRelativeDir}/${fileName}")
        }
    }
}
