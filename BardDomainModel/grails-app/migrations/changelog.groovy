import org.springframework.context.ApplicationContext

import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String bardDomainModelMigrationsDir = ctx.migrationResourceAccessor.baseDirectory
    File migrationsDir1 = new File(bardDomainModelMigrationsDir)
    File migrationsDir = migrationsDir1
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



