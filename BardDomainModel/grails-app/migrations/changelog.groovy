import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    File migrationsDir = getMigrationsDir()
    migrationsDir.eachFileMatch(DIRECTORIES, ~/iteration-\d+/) {dir ->
        dir.eachFileMatch(FILES, ~/\d+.*\.groovy/) {file ->
            include file: "${dir.name}/${file.name}"
        }
    }

    // do last
    include file: 'manage-stored-procedures.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'drop-retired-tables.groovy'
}

private File getMigrationsDir() {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    migrationsDir
}

/*
 Note: this is cheating a bit, liquibase.resource.ResourceAccessor doesn't have a property call baseDirectory.
 The baseDirectory is only available on the liquibase.resource.FileSystemResourceAccessor which is the concrete type.

 Also, this was configured as part of the BardDomainModelGrailsPlugin.groovy initialization, see the doWithSpring section
 based on the initialization that happens in the DatabaseMigrationGrailsPlugin.groovy
  */
private void includeSqlDir(String author, String relativeSqlDirName, String dbms) {
    File sqlDir = new File(getMigrationsDir(), relativeSqlDirName)
    sqlDir.eachFileMatch(FILES, ~/\d+.*.sql/) {file ->
        String fileName = file.name
        changeSet(author: author, id: fileName, dbms: dbms) {
            sqlFile(path: "${sqlDir}/${fileName}")
        }
    }
}