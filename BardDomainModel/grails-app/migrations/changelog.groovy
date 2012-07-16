import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir = new File(bardDomainModelMigrationsDir)
    migrationsDir.eachFileMatch(DIRECTORIES, ~/iteration-\d+/) {dir ->
        dir.eachFileMatch(FILES, ~/\d+.*/) {file ->
            include file: "${dir.name}/${file.name}"
        }
    }



    // do last
    include file: 'manage-stored-procedures.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'drop-retired-tables.groovy'
}
